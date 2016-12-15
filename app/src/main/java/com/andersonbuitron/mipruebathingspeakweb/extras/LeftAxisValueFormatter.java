package com.andersonbuitron.mipruebathingspeakweb.extras;

import android.content.Context;

import com.andersonbuitron.mipruebathingspeakweb.gestores.GestorConsumo;
import com.andersonbuitron.mipruebathingspeakweb.gestores.GestorDispositivos;
import com.andersonbuitron.mipruebathingspeakweb.gestores.GestorLlaveValor;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;

import java.text.DecimalFormat;

public class LeftAxisValueFormatter implements YAxisValueFormatter
{

    private DecimalFormat mFormat;
    private int valorConsumoKWh;
    public LeftAxisValueFormatter(Context context) {
        mFormat = new DecimalFormat("###,###,###,##0.0");
        GestorDispositivos gestor =  GestorDispositivos.getInstance(context);
        // valor en pesos de consumo por Kws

        String llave = GestorConsumo.LLAVE_CONSUMO;
        this.valorConsumoKWh =  GestorLlaveValor.getInstance(context).obtenerIntValor(llave);
    }

    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        return  "$"+mFormat.format((value/1000)*valorConsumoKWh);
    }
}
