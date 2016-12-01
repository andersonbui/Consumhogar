package com.andersonbuitron.mipruebathingspeakweb.activities.inicioM;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import com.andersonbuitron.mipruebathingspeakweb.R;


public class SplashScreenActivity extends AppCompatActivity {

    private static final int segundos = 3;
    private static int milisegundos = segundos*1000;
    private static int delay = 2;
    private ProgressBar pb_progreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        inicializarVariables();
        empezarAnimacion();
    }

    private void inicializarVariables()
    {
        pb_progreso = (ProgressBar)findViewById(R.id.pb_progreso);
        pb_progreso.setMax(maximo_progreso());
    }

    private void empezarAnimacion()
    {
        new CountDownTimer(milisegundos, 1000)
        {
            @Override
            public void onTick(long millisUntilFinished) {
                pb_progreso.setProgress(establecer_progreso(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                Intent intent_pagInicio  = new Intent(SplashScreenActivity.this, InicioSesionActivity.class);
                startActivity(intent_pagInicio);
                finish();
            }
        }.start();
    }

    private int establecer_progreso(long miliseconds)
    {
        return (int)((milisegundos - miliseconds)/1000);
    }

    private int maximo_progreso()
    {
        return segundos - delay;
    }
}
