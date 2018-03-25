package com.example.sheryl.deerdetection;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final Button startButton = (Button) findViewById(R.id.startButton);
        final Button aboutButton = (Button) findViewById(R.id.aboutButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(home.this, start.class);
                startActivity(intent);
            }
        });

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
//                startButton.setVisibility(View.GONE);
//                aboutButton.setVisibility(View.GONE);
                Intent intent = new Intent(home.this, about.class);
                startActivity(intent);
            }
        });

    }


}
