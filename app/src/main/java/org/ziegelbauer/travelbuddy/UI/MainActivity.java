package org.ziegelbauer.travelbuddy.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.ziegelbauer.travelbuddy.R;

public class MainActivity extends AppCompatActivity {
    public static int alertCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VacationList.class);
                intent.putExtra("test", "Information Sent");
                startActivity(intent);
            }
        });
    }
}