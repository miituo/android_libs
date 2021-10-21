package com.miituo.miituolibrary.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.miituo.miituolibrary.R;
import com.miituo.miituolibrary.activities.data.IinfoClient;
import com.miituo.miituolibrary.activities.utils.TextWatcherCustom;

public class ConfirmActivity extends AppCompatActivity {

    TextInputLayout odometer_layout;
    TextInputEditText odometro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        odometer_layout = findViewById(R.id.odometer_layout);
        odometro = findViewById(R.id.editTextConfirma);
        odometro.addTextChangedListener(new TextWatcherCustom(odometer_layout));

        SharedPreferences preferences = getSharedPreferences(getString(R.string.shared_name_prefs), Context.MODE_PRIVATE);
        String mCurrentPhotoPath = preferences.getString("nombrefotoodometro", "null");
        String filePath = mCurrentPhotoPath;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap bmp = BitmapFactory.decodeFile(filePath,options);

        ImageView vistaodo = findViewById(R.id.imageView17);
        vistaodo.setImageBitmap(bmp);
    }

    public void validar(View v){
        //get odometer value
        //validar no sea ""
        //lanzar nuevo actiivty con valor para confimar del otro lado...

        String odo = odometro.getText().toString();

        if(odo.equals("")){
            odometer_layout.setHelperText("Captura los kms. que marca el odómetro.");
        }else{
            //launch activity and save odometro
            Intent i = new Intent(this,LastOdometerActivity.class);
            i.putExtra("valor",odo);
            startActivity(i);
        }
    }

}