package com.andersonbuitron.mipruebathingspeakweb.callbacks;

import com.andersonbuitron.mipruebathingspeakweb.modelos.FeedField;

import java.util.ArrayList;
import java.util.List;

/**
 * Interface para realizar tareas dirigidas a ejecutar se en una peticion http
 *
 */

public interface TareaList {
    void ejecutar(ArrayList<FeedField> listaFeedField);
    List getList();
}
