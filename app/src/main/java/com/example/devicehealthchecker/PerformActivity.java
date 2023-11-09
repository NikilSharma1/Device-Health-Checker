package com.example.devicehealthchecker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PerformActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perform);
        ColorDrawable colorDrawable
                = new ColorDrawable(getColor(R.color.red));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        Button button=findViewById(R.id.gobutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PerformActivity.this,CheckerActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}