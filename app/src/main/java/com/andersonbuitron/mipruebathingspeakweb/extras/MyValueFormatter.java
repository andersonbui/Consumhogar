package com.andersonbuitron.mipruebathingspeakweb.extras;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

public class MyValueFormatter implements ValueFormatter
{

    private DecimalFormat mFormat;
    double promedio;
    
    public MyValueFormatter(double promedio) {
        mFormat = new DecimalFormat("###,###,###,##0.0");
        this.promedio = promedio;
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        //Log.i("comparacion:["+(String.valueOf(value).equals("NaN"))+"]","valor["+value+"]dataSetIndex["+dataSetIndex+"]entry["+entry+"]");
        if(String.valueOf(value).equals("NaN")|| value==0){
            return "";
        }

        if(entry.getVal()>promedio  ){
            if(promedio == value){
                return "";
            }
            //BarEntry
            return mFormat.format(entry.getVal()) ;
        }

        return mFormat.format(value);
    }
}
