package com.andersonbuitron.mipruebathingspeakweb.callbacks;

/**
 * Interface para realizar tareas dirigidas a ejecutar se en una peticion http
 *
 */

public interface TareaUrl {
    void ejecutar(String resultado);
    String getUrl();
}
