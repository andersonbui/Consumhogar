package com.andersonbuitron.mipruebathingspeakweb.gestores;

import com.andersonbuitron.mipruebathingspeakweb.extras.GraficaBarrras;
import com.andersonbuitron.mipruebathingspeakweb.modelos.FeedField;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by debian on 1/12/16.
 */

public class GestorFeedField {


    public static ArrayList<FeedField> ordenarListaFF(ArrayList<FeedField> listaFF) {

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

    public static  ArrayList<GraficaBarrras.ValorLabel> obtenerListaValorLabel(List<FeedField> listaFF,int tipo) {
        String formato = "";
        switch(tipo){
            case Calendar.MONTH:
                formato = "dd";
                break;
            case Calendar.DAY_OF_MONTH:
                formato = "HH";
                break;
        }

        ArrayList<GraficaBarrras.ValorLabel> lista = new ArrayList<>();
        for (FeedField item: listaFF) {
            float valor = item.getValor().floatValue();
            DateFormat df = new SimpleDateFormat(formato);
            String label = df.format(item.getFecha());
            GraficaBarrras.ValorLabel unV = new GraficaBarrras.ValorLabel(valor,label);
            lista.add(unV);
        }
        return lista;
    }

    public static  double media(List<GraficaBarrras.ValorLabel> listaFF) {
        ArrayList<Float> lista = new ArrayList<>();
        int tamaniolistaFF = listaFF.size();
        if (tamaniolistaFF != 0) {
            lista.add(listaFF.get(0).getValor());
        }
        int tamanio_lista = 1;
        for (int k = 1; k < tamaniolistaFF; k++) {
            float valoraAInsertar = listaFF.get(k).getValor();
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

    public static float suma(List<GraficaBarrras.ValorLabel> listaFF){
        float suma = 0;
        for (GraficaBarrras.ValorLabel elem : listaFF) {
            suma +=  elem.getValor();
        }
        return suma;
    }

    public static  double promedio(List<GraficaBarrras.ValorLabel> listaFF) {
        double suma = 0;
        for (GraficaBarrras.ValorLabel elem : listaFF) {
            suma += elem.getValor();
        }
        return suma / listaFF.size();
    }

}
