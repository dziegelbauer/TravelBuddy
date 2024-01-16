package org.ziegelbauer.travelbuddy.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.ziegelbauer.travelbuddy.R;
import org.ziegelbauer.travelbuddy.database.Repository;
import org.ziegelbauer.travelbuddy.entities.Excursion;
import org.ziegelbauer.travelbuddy.entities.Vacation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VacationDetails extends AppCompatActivity {
    private Repository repository;
    private EditText vacationNameControl;
    private EditText vacationLodgingControl;
    private TextView vacationStartDateControl;
    private TextView vacationEndDateControl;
    private Vacation currentVacation;
    private final String dateFormat = "MM/dd/yy";
    private final SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
    private final Calendar calendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener startDateListener;
    private DatePickerDialog.OnDateSetListener endDateListener;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_details);

        currentVacation = new Vacation(
                getIntent().getIntExtra("id", -1),
                getIntent().getStringExtra("name"),
                getIntent().getStringExtra("lodging"),
                getIntent().getStringExtra("startDate"),
                getIntent().getStringExtra("endDate")
        );

        FloatingActionButton btnDetails = findViewById(R.id.btnExcursionDetails);
        btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VacationDetails.this, ExcursionDetails.class);
                intent.putExtra("vacationID", currentVacation.getVacationID());
                intent.putExtra("excursionDate", Date.from(Instant.now()));
                startActivity(intent);
            }
        });

        vacationNameControl = findViewById(R.id.vacationName);
        vacationNameControl.setText(currentVacation.getVacationName());
        vacationNameControl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentVacation.setVacationName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        vacationLodgingControl = findViewById(R.id.vacationLodging);
        vacationLodgingControl.setText(currentVacation.getVacationLodging());
        vacationLodgingControl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentVacation.setVacationLodging(s.toString());
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

                vacationStartDateControl.setText(sdf.format(calendar.getTime()));
                currentVacation.setStartDate(sdf.format(calendar.getTime()));
            }
        };

        vacationStartDateControl = findViewById(R.id.vacationStartDate);
        vacationStartDateControl.setText(currentVacation.getStartDate());
        vacationStartDateControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String info = currentVacation.getStartDate();

                if(info == null || info.isEmpty()) {
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
                        VacationDetails.this,
                        startDateListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        endDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                vacationEndDateControl.setText(sdf.format(calendar.getTime()));
                currentVacation.setEndDate(sdf.format(calendar.getTime()));
            }
        };

        vacationEndDateControl = findViewById(R.id.vacationEndDate);
        vacationEndDateControl.setText(currentVacation.getEndDate());
        vacationEndDateControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String info = currentVacation.getEndDate();

                if(info == null || info.isEmpty()) {
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
                        VacationDetails.this,
                        endDateListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        repository = new Repository(getApplication());

        populateRecycler();
    }

    private void populateRecycler() {
        RecyclerView recyclerView = findViewById(R.id.excursionRecyclerView);
        List<Excursion> allExcursions = repository.getAssociatedExcursions(currentVacation.getVacationID());
        final ExcursionAdaptor excursionAdaptor = new ExcursionAdaptor(this);
        recyclerView.setAdapter(excursionAdaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        excursionAdaptor.setExcursions(allExcursions);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacation_details, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateRecycler();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }

        if(item.getItemId() == R.id.saveVacation) {
            try {
                Date startDate = sdf.parse(currentVacation.getStartDate());
                Date endDate = sdf.parse(currentVacation.getEndDate());

                assert endDate != null;
                if(endDate.before(startDate) || endDate.equals(startDate)) {
                    Toast.makeText(
                            VacationDetails.this,
                            "The vacation's start date must be before the vacation's return date",
                            Toast.LENGTH_SHORT
                    ).show();
                    return true;
                }
            } catch (ParseException ignored) {} // The string values being parsed are validated and the parse cannot fail

            if(currentVacation.getVacationName() == null || currentVacation.getVacationName().isEmpty()) {
                Toast.makeText(
                        VacationDetails.this,
                        "The vacation's name is required",
                        Toast.LENGTH_SHORT
                ).show();
                return true;
            }

            if(currentVacation.getVacationLodging() == null || currentVacation.getVacationLodging().isEmpty()) {
                Toast.makeText(
                        VacationDetails.this,
                        "The vacation's lodging is required",
                        Toast.LENGTH_SHORT
                ).show();
                return true;
            }

            if(currentVacation.getVacationID() == -1) {
                List<Vacation> vacations = repository.getAllVacations();
                int vacationCount = vacations.size();
                if(vacationCount == 0) {
                    currentVacation.setVacationID(1);
                } else {
                    int newID = vacations
                            .get(vacationCount - 1)
                            .getVacationID() + 1;
                    currentVacation.setVacationID(newID);
                }
                repository.addVacation(currentVacation);
            } else {
                repository.updateVacation(currentVacation);
            }

            Toast.makeText(
                    VacationDetails.this,
                    currentVacation.getVacationName() + " saved",
                    Toast.LENGTH_SHORT
            ).show();

            this.finish();

            return true;
        }

        if(item.getItemId() == R.id.deleteVacation) {
            if(currentVacation.getVacationID() != -1) {
                List<Excursion> associatedExcursions = repository.getAssociatedExcursions(currentVacation.getVacationID());

                if(associatedExcursions.size() > 0) {
                    Toast.makeText(
                            VacationDetails.this,
                            "Cannot delete a vacation with associated excursions",
                            Toast.LENGTH_SHORT
                    ).show();
                } else {
                    repository.deleteVacation(currentVacation);

                    Toast.makeText(
                            VacationDetails.this,
                            currentVacation.getVacationName() + " deleted",
                            Toast.LENGTH_SHORT
                    ).show();

                    this.finish();
                }
            } else {
                Toast.makeText(
                        VacationDetails.this,
                        "Cannot delete an unsaved vacation",
                        Toast.LENGTH_SHORT
                ).show();
            }
            return true;
        }

        if(item.getItemId()== R.id.shareVacation) {
            StringBuilder sb = new StringBuilder();

            sb.append("I'm taking a vacation!\n");
            sb.append("Where: ");
            sb.append(currentVacation.getVacationName());
            sb.append("\n");
            sb.append("When: ");
            sb.append(currentVacation.getStartDate());
            sb.append("-");
            sb.append(currentVacation.getEndDate());
            sb.append("\n");
            sb.append("Activities:\n");

            for(Excursion ex : repository.getAssociatedExcursions(currentVacation.getVacationID())) {
                sb.append(ex.getExcursionName());
                sb.append(" on ");
                sb.append(ex.getExcursionDate());
                sb.append("\n");
            }

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
            sendIntent.putExtra(Intent.EXTRA_TITLE, "Upcoming Vacation");
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
            return true;
        }

        if(item.getItemId() == R.id.notifyVacation) {
            try {
                Date vacationStartDate = sdf.parse(currentVacation.getStartDate());
                Date vacationEndDate = sdf.parse(currentVacation.getEndDate());

                String startMessage = "Your vacation to " +
                        currentVacation.getVacationName() +
                        " is starting!";

                assert vacationStartDate != null;
                setAlert(vacationStartDate, startMessage);

                String endMessage = "Your vacation to " +
                        currentVacation.getVacationName() +
                        " is ending.";

                assert vacationEndDate != null;
                setAlert(vacationEndDate, endMessage);

            } catch (ParseException e) {
                Toast.makeText(
                        VacationDetails.this,
                        "Your vacation start or end date is invalid",
                        Toast.LENGTH_SHORT
                ).show();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setAlert(@NonNull Date alertDate, String message) {
        long trigger = alertDate.getTime();

        Intent intent = new Intent(VacationDetails.this, NotifyReceiver.class);
        intent.putExtra("message", message);
        PendingIntent sender = PendingIntent.getBroadcast(
                VacationDetails.this,
                ++MainActivity.alertCount,
                intent,
                PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, trigger, sender);
    }
}