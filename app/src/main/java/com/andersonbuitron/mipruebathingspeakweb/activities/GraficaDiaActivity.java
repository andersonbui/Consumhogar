package com.andersonbuitron.mipruebathingspeakweb.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.WebView;

import com.andersonbuitron.mipruebathingspeakweb.R;
import com.andersonbuitron.mipruebathingspeakweb.callbacks.TareaList;
import com.andersonbuitron.mipruebathingspeakweb.extras.LeftAxisValueFormatter;
import com.andersonbuitron.mipruebathingspeakweb.extras.MyValueFormatter;
import com.andersonbuitron.mipruebathingspeakweb.extras.RightAxisValueFormatter;
import com.andersonbuitron.mipruebathingspeakweb.gestores.GestorDispositivos;
import com.andersonbuitron.mipruebathingspeakweb.modelos.Dispositivo;
import com.andersonbuitron.mipruebathingspeakweb.modelos.FeedField;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.github.mikephil.charting.components.YAxis.AxisDependency.RIGHT;

public class GraficaDiaActivity extends AppCompatActivity {

    ArrayList<FeedField> listaFeedField;
    public static final String EXTRA_DISPOSITIVO = "dispositivo_a_graficar";
    Dispositivo mDispositivo;
    BarChart mChart;
    float limiteConsumoDiario = 35;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafica_dia);
        listaFeedField = new ArrayList<>();
        mDispositivo = (Dispositivo) getIntent().getSerializableExtra(EXTRA_DISPOSITIVO);
        Calendar fechaActual = Calendar.getInstance();

        solicitarDatos(fechaActual.get(Calendar.YEAR), fechaActual.get(Calendar.MONTH), fechaActual.get(Calendar.DAY_OF_MONTH));
    }

    public void solicitarDatos(int anio, int mes, int dia) {
        //-----
        Calendar calendar = Calendar.getInstance();
        calendar.set(anio, mes, 0);
        Date finicial = calendar.getTime();

        calendar.set(anio, mes, dia + 1);
        Date ffinal = calendar.getTime();

        String escala = "720"; //diario

        listaFeedField = new ArrayList();
        TareaList nuevaTarea = new TareaList() {
            @Override
            public void ejecutar(ArrayList<FeedField> listaFF) {
                listaFeedField = listaFF;
                //Toast.makeText(getApplicationContext(), "listaFeedField: "+listaFeedField.toString(), Toast.LENGTH_LONG).show();
                limiteConsumoDiario = (float) calcularLimiteCondumoDiario(listaFF);
                inicializar_variables();
            }

            @Override
            public List getList() {
                return listaFeedField;
            }
        };

        GestorDispositivos gestionDis = GestorDispositivos.getInstance(getApplicationContext());
        String url = gestionDis.solicitarValoresDeField(mDispositivo.getApi_key_write(), GestorDispositivos.VALUE_FIELD_NUMBER, mDispositivo.getId(), finicial, ffinal, escala, nuevaTarea);

    }

    public double calcularLimiteCondumoDiario(ArrayList<FeedField> listaFF) {

        //return  promedio( listaFF);
        return media(listaFF);
    }
    public ArrayList<FeedField>  ordenarListaFF(ArrayList<FeedField> listaFF) {
        ArrayList<FeedField> lista = new ArrayList<>();
        int tamaniolistaFF = listaFF.size();
        if (tamaniolistaFF != 0) {
            lista.add(listaFF.get(0));
        }
        int tamanio_lista = 1;
        for (int k = 1; k < tamaniolistaFF; k++) {
            FeedField valoraAInsertar = listaFF.get(k);
            for (int i = 0; i < tamanio_lista; i++) {

                if (lista.get(i).getValor() > valoraAInsertar.getValor()) {
                    lista.add(i, valoraAInsertar);
                    tamanio_lista++;
                    break;
                } else if (i == tamanio_lista - 1){
                    lista.add( valoraAInsertar);
                    tamanio_lista++;
                    break;
                }
            }

        }

        return lista;
    }
    public double media(ArrayList<FeedField> listaFF) {
        ArrayList<Double> lista = new ArrayList<>();
        int tamaniolistaFF = listaFF.size();
        if (tamaniolistaFF != 0) {
            lista.add(listaFF.get(0).getValor());
        }
        int tamanio_lista = 1;
        for (int k = 1; k < tamaniolistaFF; k++) {
            double valoraAInsertar = listaFF.get(k).getValor();
            for (int i = 0; i < tamanio_lista; i++) {
                if (lista.get(i) > valoraAInsertar) {
                    lista.add(i, valoraAInsertar);
                    tamanio_lista++;
                    break;
                } else if (i == tamanio_lista - 1){
                    lista.add( valoraAInsertar);
                    tamanio_lista++;
                    break;
                }
            }

        }
        if (!lista.isEmpty()) {
            return lista.get((lista.size() - 1) / 2);
        }
        return 0;
    }

    public double promedio(ArrayList<FeedField> listaFF) {
        double suma = 0;
        for (FeedField elem : listaFF) {
            suma += elem.getValor();
        }
        return suma / listaFF.size();
    }

    private void inicializar_variables() {

        mChart = (BarChart) findViewById(R.id.barchart);

        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < listaFeedField.size(); i++) {

            float valorOriginal = Float.parseFloat(listaFeedField.get(i).getValor().toString());
            float val1 = valorOriginal;
            float val2 = 0;
            if (valorOriginal > limiteConsumoDiario) {
                val1 = limiteConsumoDiario;
                val2 = valorOriginal - limiteConsumoDiario;
            }
            //entries.add(new BarEntry(new float[]{val1, val2}, i, noms[i]));
            entries.add(new BarEntry(new float[]{val1, val2}, i));
        }


        BarDataSet bardataset = new BarDataSet(entries, "Consumo");
        bardataset.setColors(getColors());
        bardataset.setStackLabels(new String[]{"Normal", "Exceso"});

        DateFormat df = new SimpleDateFormat("dd");
        ArrayList<String> labels = new ArrayList<String>();
        for (int i = 0; i < listaFeedField.size(); i++) {
            labels.add(df.format(listaFeedField.get(i).getFecha()));
        }
        BarData data = new BarData(labels, bardataset);
        data.setValueFormatter(new MyValueFormatter(limiteConsumoDiario));
        //data.setValueTextColor(Color.WHITE);

        //obtener valor del consumo por kwh
        GestorDispositivos gestorD =  GestorDispositivos.getInstance(getApplicationContext());
        //int consumo = gestorD.leerConsumo(this);
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setValueFormatter(new LeftAxisValueFormatter(300));

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setValueFormatter(new RightAxisValueFormatter());
        //leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)
        mChart.setY(0);

        //mChart.getAxisRight().setEnabled(false);
        //mChart.getXAxis().setPosition(XAxis.XAxisPosition.TOP);

        mChart.setData(data); // set the data and list of lables into chart
        mChart.setVisibleXRange(0, labels.size());
        mChart.setNoDataTextDescription("Por favor espere un momento a que se carguen los datos.");

        df = new SimpleDateFormat("MMMM");
        String descripcion = "mes: ";
        if (listaFeedField.size() > 0) {
            descripcion += df.format(listaFeedField.get(0).getFecha());
        }
        mChart.setDescription(descripcion);  // set the description
        //bardataset.setColors( ColorTemplate.);
        bardataset.setHighLightColor(Color.rgb(255, 0, 0));
        bardataset.setHighlightEnabled(true);
        bardataset.setAxisDependency(RIGHT);

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.ABOVE_CHART_CENTER);
        //VerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        //l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        //l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        //l.setDrawInside(false);
        l.setFormSize(8f);
        l.setFormToTextSpace(4f);
        l.setXEntrySpace(6f);

        mChart.animateY(500);
    }
/*
    private void inicializar_variables2() {

//        mToolbar = (Toolbar) findViewById( R.id.toolbar_actionbar );
  //      setSupportActionBar( mToolbar );
    //    getSupportActionBar().setDisplayHomeAsUpEnabled( true );


        BarChart barChart = (BarChart) findViewById(R.id.barchart);

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(8, 0));
        entries.add(new BarEntry(2, 1));
        entries.add(new BarEntry(5, 2));
        entries.add(new BarEntry(200, 3));
        entries.add(new BarEntry(15, 4));
        entries.add(new BarEntry(19, 5));

        BarDataSet bardataset = new BarDataSet(entries, "Cells");

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("2016");
        labels.add("2015");
        labels.add("2014");
        labels.add("2013");
        labels.add("2012");
        labels.add("2011");

        BarData data = new BarData(labels, bardataset);
        barChart.setData(data); // set the data and list of lables into chart
        barChart.setVisibleXRange(0, labels.size() - 1);

        barChart.setDescription("Set Bar Chart Description");  // set the description

        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);

        barChart.animateY(5000);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(GraficaDiaActivity.this, MainActivity.class);
            //intent.putExtra( "disp_seleccionado", objDispositivo );
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private int[] getColors() {

        // have as many colors as stack-values per entry
        int[] colors = {
                Color.GREEN, Color.RED
        };

        return colors;
    }
}
