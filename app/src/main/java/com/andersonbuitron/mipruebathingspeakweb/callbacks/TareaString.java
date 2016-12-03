package com.andersonbuitron.mipruebathingspeakweb.callbacks;

/**
 * Interface para realizar tareas dirigidas a ejecutar se en una peticion http
 *
 */

public abstract class TareaString {

    String string;

    public void ejecutar(String resultado){

    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}
