package com.andersonbuitron.mipruebathingspeakweb.modelos;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by debian on 21/11/16.
 */

public class FeedField implements Serializable{
    Date fecha;
    Double valor;

    public FeedField(Date fecha, Double valor) {
        this.fecha = fecha;
        this.valor = valor;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "FeedField{" +
                "fecha=" + fecha +
                ", valor=" + valor +
                '}';
    }
}
