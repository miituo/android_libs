package com.miituo.samplelibraryapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.widget.SwitchCompat;

import com.miituo.miituolibrary.activities.PrincipalActivity;
import com.miituo.miituolibrary.activities.VehiclePictures;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonLaunch = findViewById(R.id.buttonLaunch);
        final EditText editTextPhone = findViewById(R.id.editTextPhone);
        final SwitchCompat devSelect = findViewById(R.id.switchDev);

        buttonLaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editTextPhone.getText().toString().equals("")) {
                    final int dev = devSelect.isChecked() ? 1 : 0;

                    Intent i = new Intent(MainActivity.this, PrincipalActivity.class);
                    i.putExtra("telefono", editTextPhone.getText().toString());
                    i.putExtra("dev", dev);
                    startActivity(i);
                }
            }
        });
    }
}
