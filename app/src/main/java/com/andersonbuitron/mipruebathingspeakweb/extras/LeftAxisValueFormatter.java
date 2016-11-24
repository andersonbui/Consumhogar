package com.andersonbuitron.mipruebathingspeakweb.extras;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;

import java.text.DecimalFormat;

public class LeftAxisValueFormatter implements YAxisValueFormatter
{

    private DecimalFormat mFormat;
    private int valorConsumoKWh;
    public LeftAxisValueFormatter(int valorConsumoKWh) {
        mFormat = new DecimalFormat("###,###,###,##0.0");
        this.valorConsumoKWh = valorConsumoKWh;
    }

    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        return  "$"+mFormat.format(value*valorConsumoKWh);
    }
}
