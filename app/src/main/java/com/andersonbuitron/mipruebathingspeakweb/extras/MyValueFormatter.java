package com.andersonbuitron.mipruebathingspeakweb.extras;

import android.content.Context;

import com.andersonbuitron.mipruebathingspeakweb.gestores.GestorConsumo;
import com.andersonbuitron.mipruebathingspeakweb.gestores.GestorLlaveValor;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

public class MyValueFormatter implements ValueFormatter {

    private DecimalFormat mFormat;
    double promedio;
    Context contexto;
    int consumoPesoskWh;

    public MyValueFormatter(double promedio, Context contexto) {
        this.contexto = contexto;
        mFormat = new DecimalFormat("###,###,###,##0.0");
        this.promedio = promedio;
        String llave = GestorConsumo.LLAVE_CONSUMO;
        consumoPesoskWh = GestorLlaveValor.getInstance(contexto).obtenerIntValor(llave);
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        float valorFinal = 0;
        //Log.i("comparacion:["+(String.valueOf(value).equals("NaN"))+"]","valor["+value+"]dataSetIndex["+dataSetIndex+"]entry["+entry+"]");
        if (String.valueOf(value).equals("NaN") || value == 0) {
            return "";
        } else if (entry.getVal() > promedio) {
            if (promedio == value) {
                return "";
            }
            //BarEntry
            valorFinal = entry.getVal();

        } else {
            valorFinal = value;
        }

        return "$"+mFormat.format((valorFinal/1000)*consumoPesoskWh);
    }
}
