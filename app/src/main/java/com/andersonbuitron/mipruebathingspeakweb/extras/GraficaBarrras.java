package com.andersonbuitron.mipruebathingspeakweb.extras;

import android.graphics.Color;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.github.mikephil.charting.components.YAxis.AxisDependency.RIGHT;

/**
 * Created by debian on 1/12/16.
 */

public class GraficaBarrras implements Serializable{

    public static final int ESTILO_UN_COLOR_CON_LINEA_DELIMITADORA = 1;
    public static final int ESTILO_DOS_COLOR_SIN_LINEA_DELIMITADORA = 2;
    public static final int ESTILO_DOS_COLORES_CON_LINEA_DELIMITADORA = 3;

    float limiteConsumoDiario;
    List<ValorLabel> listaFeedField;
    String subtitle;
    int estilo;

    public GraficaBarrras( float limiteConsumoDiario, List<ValorLabel> listaFeedField, String subtitle, int estilo) {

        this.limiteConsumoDiario = limiteConsumoDiario;
        this.listaFeedField = listaFeedField;
        this.subtitle = subtitle;
        this.estilo = estilo;
    }

    /**
     *
     * @param mChart
     * @param estilo entero que permite seleccionar estilo de grafico. Posibles valores:
     *               {@link ESTILO_UN_COLOR_CON_LINEA_DELIMITADORA}, {@link ESTILO_DOS_COLOR_SIN_LINEA_DELIMITADORA},
     *               {@link ESTILO_DOS_COLORES_CON_LINEA_DELIMITADORA}
     */
    public void crearGrafica(BarChart mChart, int estilo, boolean interaccion) {

        // Eleccion de estilo
        boolean separarColores = false;
        switch(estilo){
            case 3: //separacion por colores con linea delimitadora
                separarColores = true;
            case 1: //utilizacion de linea delimitadora
                aplicarDelimitador(mChart,limiteConsumoDiario);
                break;
            case 2: //separacion de barra en colores por linea imaginaria
                separarColores = true;
                break;

        }

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();
        int i = 0;
        for (ValorLabel elem : listaFeedField) {
            //obtener todos los labels
            labels.add(elem.getLabel());
            //obtener todos los float con el formato [delimitador | valor-delimitador]

            float valorOriginal = elem.getValor();
            float val1 = valorOriginal;
            float val2 = 0;
            if (valorOriginal > limiteConsumoDiario && separarColores) {
                val1 = limiteConsumoDiario;
                val2 = valorOriginal - limiteConsumoDiario;
            }
            entries.add(new BarEntry(new float[]{val1, val2}, i, "care"));
            i++;
        }


        BarDataSet bardataset = new BarDataSet(entries, "Consumo");
        bardataset.setColors(getColors());
        if(separarColores){
            bardataset.setStackLabels(new String[]{"Normal", "Exceso"});
            //mChart.setBackgroundColor(Color.WHITE);
        }else{
            //quitar valores de las barras
            bardataset.setDrawValues(false);
            bardataset.setStackLabels(new String[]{"", ""});

        }

        BarData data = new BarData(labels, bardataset);
        // asignar formato de impresion de valores
        data.setValueFormatter(new MyValueFormatter(limiteConsumoDiario));

        // Establecer formato de eje izquierdo
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setValueFormatter(new LeftAxisValueFormatter(200));

        // Establecer formato de eje derecho
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setValueFormatter(new RightAxisValueFormatter());
        //leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)
        mChart.setY(0);

        // zoom de los ejes x y de forma dependiente
        //mChart.setPinchZoom(true);

        //decidir si hay interaccion del usuario con el grafico
        if (!interaccion) {
            //zoom
            mChart.setPinchZoom(false);
            //mChart.setKeepScreenOn(true);
            mChart.setClickable(false);
            //mChart.setDoubleTapToZoomEnabled(false);
            mChart.setScaleEnabled(false);
            //mChart.setOnTouchListener(null);
        }

        mChart.setData(data); // set the data and list of lables into chart
        mChart.setVisibleXRange(0, labels.size());
        mChart.setNoDataTextDescription("Por favor espere un momento a que se carguen los datos.");

        mChart.setDescription(subtitle);  // set the description
        //bardataset.setColors( ColorTemplate.);
        bardataset.setHighLightColor(Color.rgb(0, 0, 255));
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

    private static void aplicarDelimitador(BarChart mChart,float delimitador) {

        //configuracion de linea  delimitadora
        LimitLine limitY = new LimitLine(delimitador, "consumo promedio");
        // posicion de label
        limitY.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        //tamanio del texto del label
        limitY.setTextSize(10f);
        // anchor de la linea
        limitY.setLineWidth(4f);
        // forma de la linea
        limitY.enableDashedLine(10f, 10f, 0f);
        limitY.setTextColor(Color.rgb(255, 0, 0));
        //asignar el delimitador al eje
        mChart.getAxisRight().addLimitLine(limitY);

    }

    private static int[] getColors() {

        // have as many colors as stack-values per entry
        int[] colors = {
                Color.GREEN, Color.RED
        };

        return colors;
    }


    public static class ValorLabel implements Serializable {
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
