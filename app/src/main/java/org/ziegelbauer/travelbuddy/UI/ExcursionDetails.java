package org.ziegelbauer.travelbuddy.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.ziegelbauer.travelbuddy.R;
import org.ziegelbauer.travelbuddy.database.Repository;
import org.ziegelbauer.travelbuddy.entities.Excursion;
import org.ziegelbauer.travelbuddy.entities.Vacation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExcursionDetails extends AppCompatActivity {
    private Excursion currentExcursion;
    private EditText excursionNameControl;
    private TextView excursionDateControl;
    private Repository repository;
    private DatePickerDialog.OnDateSetListener startDateListener;
    private final Calendar calendar = Calendar.getInstance();
    List<Vacation> vacations;
    private final String dateFormat = "MM/dd/yy";
    private final SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_details);

        currentExcursion = new Excursion(
                getIntent().getIntExtra("id", -1),
                getIntent().getStringExtra("name"),
                getIntent().getStringExtra("excursionDate"),
                getIntent().getIntExtra("vacationID", -1)
        );

        repository = new Repository(getApplication());

        excursionNameControl = findViewById(R.id.excursionName);
        excursionNameControl.setText(currentExcursion.getExcursionName());
        excursionNameControl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentExcursion.setExcursionName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        startDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                excursionDateControl.setText(sdf.format(calendar.getTime()));
                currentExcursion.setExcursionDate(sdf.format(calendar.getTime()));
            }
        };

        excursionDateControl = findViewById(R.id.excursionDate);
        excursionDateControl.setText(currentExcursion.getExcursionDate());
        excursionDateControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String info = currentExcursion.getExcursionDate();

                if(info.isEmpty()) {
                    info = sdf.format(Date.from(Instant.now()));
                }

                try{
                    Date initialDate = sdf.parse(info);
                    if(initialDate != null) {
                        calendar.setTime(initialDate);
                    } else {
                        calendar.setTime(Date.from(Instant.now()));
                    }
                } catch (ParseException e) {
                    calendar.setTime(Date.from(Instant.now()));
                }

                new DatePickerDialog(
                        ExcursionDetails.this,
                        startDateListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        vacations = new ArrayList<>(repository.getAllVacations());
        ArrayAdapter<Vacation> vacationIDAdapter = new ArrayAdapter<>(
                this,
                R.layout.vacation_spinner_item,
                R.id.vacationName,
                vacations
        );

        Spinner vacationSpinner = findViewById(R.id.vacationSpinner);
        vacationSpinner.setAdapter(vacationIDAdapter);
        vacationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentExcursion.setVacationID(vacations.get(position).getVacationID());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                currentExcursion.setVacationID(1);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_excursion_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }

        if(item.getItemId() == R.id.saveExcursion) {
            if(currentExcursion.getVacationID() == -1) {
                Toast.makeText(
                        ExcursionDetails.this,
                        "The excursion must be associated with a vacation",
                        Toast.LENGTH_SHORT
                ).show();
                return true;
            }

            if(currentExcursion.getExcursionName() == null || currentExcursion.getExcursionName().isEmpty()) {
                Toast.makeText(
                        ExcursionDetails.this,
                        "An excursion name is required",
                        Toast.LENGTH_SHORT
                ).show();
                return true;
            }

            Vacation parentVacation = vacations.stream()
                    .filter(v -> v.getVacationID() == currentExcursion.getVacationID())
                    .findFirst()
                    .orElseThrow(RuntimeException::new);

            try {
                Date parentStartDate = sdf.parse(parentVacation.getStartDate());
                Date parentEndDate = sdf.parse(parentVacation.getEndDate());
                Date excursionDate = sdf.parse(currentExcursion.getExcursionDate());
                assert excursionDate != null;
                if(excursionDate.before(parentStartDate) || excursionDate.after(parentEndDate)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("The excursion must be within the dates of the parent vacation (");
                    sb.append(parentVacation.getStartDate());
                    sb.append("-");
                    sb.append(parentVacation.getEndDate());
                    sb.append(")");
                    Toast.makeText(
                            ExcursionDetails.this,
                            sb.toString(),
                            Toast.LENGTH_SHORT
                    ).show();
                    return true;
                }
            } catch(ParseException ignored) {} // The string values being parsed are validated and the parse cannot fail

            if (currentExcursion.getExcursionID() == -1) {
                List<Excursion> excursions = repository.getAllExcursions();
                int excursionCount = excursions.size();

                if (excursionCount == 0) {
                    currentExcursion.setExcursionID(1);
                } else {
                    currentExcursion.setExcursionID(excursions.get(excursionCount - 1).getExcursionID() + 1);
                }

                repository.addExcursion(currentExcursion);
            } else {
                repository.updateExcursion(currentExcursion);
            }

            Toast.makeText(
                    ExcursionDetails.this,
                    currentExcursion.getExcursionName() + " saved",
                    Toast.LENGTH_SHORT
            ).show();

            this.finish();

            return true;
        }

        if(item.getItemId() == R.id.deleteExcursion) {
            if(currentExcursion.getExcursionID() == -1) {
                Toast.makeText(
                        ExcursionDetails.this,
                        "Cannot delete an unsaved excursion",
                        Toast.LENGTH_SHORT
                ).show();
                return true;
            }

            repository.deleteExcursion(currentExcursion);

            Toast.makeText(
                    ExcursionDetails.this,
                    currentExcursion.getExcursionName() + " was deleted",
                    Toast.LENGTH_SHORT
            ).show();

            this.finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}