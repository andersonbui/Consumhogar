package com.andersonbuitron.mipruebathingspeakweb.datos;

/**
 * Interface para realizar tareas dirigidas a ejecutar se en una peticion http
 *
 */

public interface Tarea {
    void ejecutar(String resultado);
    String getUrl();
}
