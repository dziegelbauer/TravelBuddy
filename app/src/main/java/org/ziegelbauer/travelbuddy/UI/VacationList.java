package org.ziegelbauer.travelbuddy.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.ziegelbauer.travelbuddy.R;
import org.ziegelbauer.travelbuddy.database.Repository;
import org.ziegelbauer.travelbuddy.entities.Excursion;
import org.ziegelbauer.travelbuddy.entities.Vacation;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VacationList extends AppCompatActivity {
    private Repository repository;
    private final String dateFormat = "MM/dd/yy";
    private final SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);

    @Override
    protected void onResume() {
        super.onResume();

        populateRecycler();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_list);

        FloatingActionButton btnDetails = findViewById(R.id.btnDetails);
        btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VacationList.this, VacationDetails.class);
                intent.putExtra("startDate", sdf.format(Date.from(Instant.now())));
                intent.putExtra("endDate", sdf.format(Date.from(Instant.now())));
                startActivity(intent);
            }
        });

        repository = new Repository(getApplication());
        populateRecycler();
    }

    private void populateRecycler() {
        List<Vacation> allVacations = repository.getAllVacations();
        RecyclerView recyclerView = findViewById(R.id.vacationRecyclerView);
        final VacationAdaptor vacationAdaptor = new VacationAdaptor(this);
        recyclerView.setAdapter(vacationAdaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        vacationAdaptor.setVacations(allVacations);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacation_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }

        if(item.getItemId() == R.id.sample) {
            String startDate = "12/1/24";
            String endDate = "12/7/24";
            Vacation vacation = new Vacation(
                    0,
                    "Bermuda",
                    "Four Seasons Hotel",
                    startDate,
                    endDate);
            repository.addVacation(vacation);

            String excursionDate = "12/3/24";
            Excursion excursion = new Excursion(
                    0,
                    "Snorkeling",
                    excursionDate,
                    1);
            repository.addExcursion(excursion);

            populateRecycler();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}