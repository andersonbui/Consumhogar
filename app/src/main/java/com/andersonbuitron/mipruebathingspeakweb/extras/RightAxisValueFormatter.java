package com.andersonbuitron.mipruebathingspeakweb.extras;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;

import java.text.DecimalFormat;

public class RightAxisValueFormatter implements YAxisValueFormatter
{

    private DecimalFormat mFormat;

    public RightAxisValueFormatter() {
        mFormat = new DecimalFormat("###,###,###,##0.00");
    }

    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        return  mFormat.format(value/1000)+" KWh";
    }
}
