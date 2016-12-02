package com.andersonbuitron.mipruebathingspeakweb.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.WebView;

import com.andersonbuitron.mipruebathingspeakweb.R;
import com.andersonbuitron.mipruebathingspeakweb.callbacks.TareaList;
import com.andersonbuitron.mipruebathingspeakweb.extras.GraficaBarrras;
import com.andersonbuitron.mipruebathingspeakweb.gestores.GestorDispositivos;
import com.andersonbuitron.mipruebathingspeakweb.modelos.Dispositivo;
import com.andersonbuitron.mipruebathingspeakweb.modelos.FeedField;
import com.github.mikephil.charting.charts.BarChart;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.andersonbuitron.mipruebathingspeakweb.gestores.GestorFeedField.media;
import static com.andersonbuitron.mipruebathingspeakweb.gestores.GestorFeedField.obtenerListaValorLabel;

public class GraficaDiaActivity extends AppCompatActivity {

    public static final String EXTRA_DISPOSITIVO = "dispositivo_a_graficar";
    private final String ESCALA_TIEMPO_MIN = "720"; //en minutos
    Dispositivo mDispositivo;
    BarChart mChart;
    float limiteConsumoDiario = 35;
    WebView webView;
    private ArrayList<FeedField> listaFeedField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafica_dia);

        mDispositivo = (Dispositivo) getIntent().getSerializableExtra(EXTRA_DISPOSITIVO);
        Calendar fechaActual = Calendar.getInstance();
        solicitarDatos(fechaActual);
    }

    public void solicitarDatos(Calendar fechaCal) {
        //-----
        int anio = fechaCal.get(Calendar.YEAR);
        int mes = fechaCal.get(Calendar.MONTH);
        int dia = fechaCal.get(Calendar.DAY_OF_MONTH);

        Calendar calendar = Calendar.getInstance();
        calendar.set(anio, mes - 1, 0);
        Date finicial = calendar.getTime();

        calendar.set(anio, mes, dia + 1);
        Date ffinal = calendar.getTime();


        listaFeedField = new ArrayList();
        TareaList nuevaTarea = new TareaList() {
            @Override
            public void ejecutar(ArrayList<FeedField> listaFF) {
                listaFeedField = listaFF;
                if (listaFeedField == null) {
                    listaFeedField = new ArrayList<>();
                }
                //Toast.makeText(getApplicationContext(), "listaFeedField: "+listaFeedField.toString(), Toast.LENGTH_LONG).show();
                limiteConsumoDiario = (float) calcularLimiteCondumoDiario(listaFeedField);
                mChart = (BarChart) findViewById(R.id.barchart);
                List<GraficaBarrras.ValorLabel> listavalores = obtenerListaValorLabel(listaFeedField);

                String mes = "mes: " + obtenerNombreMes(listaFeedField);

                GraficaBarrras.crearGrafica(mChart, limiteConsumoDiario, listavalores, mes);
            }

            @Override
            public List getList() {
                return listaFeedField;
            }
        };

        GestorDispositivos gestionDis = GestorDispositivos.getInstance(getApplicationContext());
        gestionDis.solicitarValoresDeField(mDispositivo.getApi_key_write(), GestorDispositivos.VALUE_FIELD_NUMBER, mDispositivo.getId(), finicial, ffinal, ESCALA_TIEMPO_MIN, nuevaTarea);

    }

    public String obtenerNombreMes(ArrayList<FeedField> listaFF) {
        DateFormat df = new SimpleDateFormat("MMMM");
        ;

        if (listaFF.size() > 0) {
            return df.format(listaFF.get(0).getFecha());
        }
        return "-";
    }

    public double calcularLimiteCondumoDiario(ArrayList<FeedField> listaFF) {

        if (listaFF == null) {
            return 0;
        }
        return media(listaFF);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(GraficaDiaActivity.this, MisDispositivosActivity.class);
            //intent.putExtra( "disp_seleccionado", objDispositivo );
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


}
