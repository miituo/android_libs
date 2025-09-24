package com.miituo.androidlibs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.miituo.miituolibrary.activities.PrincipalActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = new Intent(MainActivity.this, PrincipalActivity.class);
        i.putExtra("telefono", "5522514347");
        i.putExtra("dev", 0);

        startActivity(i);
    }
}