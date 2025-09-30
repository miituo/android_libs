package com.miituo.samplelibraryapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.miituo.miituolibrary.activities.PrincipalActivity;
import com.miituo.miituolibrary.activities.VehiclePictures;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_main);

        final View root = findViewById(R.id.root);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Aplica padding para que nada quede oculto
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
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
