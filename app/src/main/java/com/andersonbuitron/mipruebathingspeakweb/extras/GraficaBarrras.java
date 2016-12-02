package com.andersonbuitron.mipruebathingspeakweb.extras;

import android.graphics.Color;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

import static com.github.mikephil.charting.components.YAxis.AxisDependency.RIGHT;

/**
 * Created by debian on 1/12/16.
 */

public class GraficaBarrras {

    public static void crearGrafica(BarChart mChart, float limiteConsumoDiario, List<ValorLabel> listaFeedField, String subtitle) {


        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();
        int i = 0;
        for (ValorLabel elem: listaFeedField) {
            //obtener todos los labels
            labels.add(elem.getLabel());
            //obtener todos los float con el formato [limite | valor-limite]

            float valorOriginal = elem.getValor();
            float val1 = valorOriginal;
            float val2 = 0;
            if (valorOriginal > limiteConsumoDiario) {
                val1 = limiteConsumoDiario;
                val2 = valorOriginal - limiteConsumoDiario;
            }
            entries.add(new BarEntry(new float[]{val1, val2}, i,"care"));
            i++;
        }


        BarDataSet bardataset = new BarDataSet(entries, "Consumo");
        bardataset.setColors(getColors());
        bardataset.setStackLabels(new String[]{"Normal", "Exceso"});

        BarData data = new BarData(labels, bardataset);
        data.setValueFormatter(new MyValueFormatter(limiteConsumoDiario));
        //data.setValueTextColor(Color.WHITE);

        //obtener valor del consumo por kwh
        //GestorDispositivos gestorD =  GestorDispositivos.getInstance(getApplicationContext());
        //int consumo = gestorD.leerConsumo(this);
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setValueFormatter(new LeftAxisValueFormatter(200));

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setValueFormatter(new RightAxisValueFormatter());
        //leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)
        mChart.setY(0);
        //mChart.getAxisRight().setEnabled(false);
        //mChart.getXAxis().setPosition(XAxis.XAxisPosition.TOP);

        mChart.setData(data); // set the data and list of lables into chart
        mChart.setVisibleXRange(0, labels.size());
        mChart.setNoDataTextDescription("Por favor espere un momento a que se carguen los datos.");

        mChart.setDescription(subtitle);  // set the description
        //bardataset.setColors( ColorTemplate.);
        bardataset.setHighLightColor(Color.rgb(0, 255, 10));
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

    private static int[] getColors() {

        // have as many colors as stack-values per entry
        int[] colors = {
                Color.GREEN, Color.RED
        };

        return colors;
    }


    public static class ValorLabel{
        private float valor;
        private String label;

        public ValorLabel(float valor, String label) {
            this.valor = valor;
            this.label = label;
        }

        public float getValor() {
            return valor;
        }

        public void setValor(float valor) {
            this.valor = valor;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }

}
