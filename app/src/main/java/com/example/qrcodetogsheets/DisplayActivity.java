package com.example.qrcodetogsheets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class DisplayActivity extends AppCompatActivity {
    TextView displaytxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        Intent intent = getIntent();
        String data = intent.getStringExtra("data");


        displaytxt = findViewById(R.id.dispTxt);

        displaytxt.setText(data);

    }
}
