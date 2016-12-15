package com.andersonbuitron.mipruebathingspeakweb.activities;

import android.app.Activity;
import android.os.Bundle;

import com.andersonbuitron.mipruebathingspeakweb.R;
import com.andersonbuitron.mipruebathingspeakweb.extras.GraficaBarrras;
import com.github.mikephil.charting.charts.BarChart;

public class GraficaFullScreenActivity extends Activity {

    public static final String EXTRA_GRAFICO = "grafico_barras";
    GraficaBarrras barraGraf;
    BarChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafica_full_screen);

        barraGraf = (GraficaBarrras) getIntent().getSerializableExtra(EXTRA_GRAFICO);
        mChart = (BarChart) findViewById(R.id.barchart);
        barraGraf.crearGrafica(mChart,GraficaBarrras.ESTILO_DOS_COLOR_SIN_LINEA_DELIMITADORA,true,getApplicationContext());

    }

}
