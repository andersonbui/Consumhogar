package com.andersonbuitron.mipruebathingspeakweb.gestores;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.andersonbuitron.mipruebathingspeakweb.R;
import com.andersonbuitron.mipruebathingspeakweb.callbacks.TareaFloat;
import com.andersonbuitron.mipruebathingspeakweb.callbacks.TareaList;
import com.andersonbuitron.mipruebathingspeakweb.extras.GraficaBarrras;
import com.andersonbuitron.mipruebathingspeakweb.modelos.Dispositivo;
import com.andersonbuitron.mipruebathingspeakweb.modelos.FeedField;
import com.github.mikephil.charting.charts.BarChart;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.andersonbuitron.mipruebathingspeakweb.extras.UrlEndPoints.configurarFinFecha;
import static com.andersonbuitron.mipruebathingspeakweb.extras.UrlEndPoints.configurarInicioFecha;
import static com.andersonbuitron.mipruebathingspeakweb.gestores.GestorFeedField.obtenerListaValorLabel;
import static com.andersonbuitron.mipruebathingspeakweb.gestores.GestorFeedField.promedio;
import static com.andersonbuitron.mipruebathingspeakweb.gestores.GestorFeedField.suma;

/**
 * Created by debian on 4/12/16.
 */

public class GestorConsumo {

    /**
     * Inicio del intervalo de tiempo del cual llega la fecha (Calendar)
     *
     * @param field de tipo Calendar, ejemplo Calendar.DAY_OF_MONTH
     * @return
     */
    private static final String FORMATO_CONSUMO = "%.4f kWh - $%.4f";
    Context context;
    private static GestorConsumo gestorConsumo = new GestorConsumo();
    public static final int NUM_MESES_OPROMEDIO = 3;
    public static final String LLAVE_CONSUMO = "consumo_en_pesos";
    public static final String LLAVE_CONSUMO_DETERMINADO_X_USUARIO = "consumo_determinado_x_usuario";
    public static final String LLAVE_ES_CONSUMO_DETERMINADO_X_USUARIO = "es_consumo_determinado_x_usuario";
    public static final String LLAVE_ULTIMA_FECHA_NOTIFICACION_DIARIA = "fecha_ultima_notificacion_diaria";
    public static final String FORMATO_FECHA_NOTIFICACION_DIARIA = "yyyy-MM-dd";

    public static GestorConsumo getInstance(Context context) {
        gestorConsumo.context = context;
        return gestorConsumo;
    }

    private Date getInicioIntervaloTiempo2(final Calendar fecha, int field) {
        //Calendar fecha = (Calendar) cal.clone();
        switch (field) {
            case Calendar.DAY_OF_MONTH:

                return null;

            case Calendar.MONTH:
                //-----
                int anio = fecha.get(Calendar.YEAR);
                int mes = fecha.get(Calendar.MONTH);
                int dia = 1; //primer dia ddel mes
                //int dia = 0;
                // int hora = fecha.getMaximum(Calendar.HOUR_OF_DAY);
                //int min = fecha.getMaximum(Calendar.MINUTE);
                //int sec = fecha.getMaximum(Calendar.SECOND);
                // Primer dia del mes
                fecha.set(anio, mes, 1, 0, 0, 0);
                Date finicial = fecha.getTime();
                return finicial;

            default:
                return null;
        }
    }

    /**
     * Inicio del intervalo de tiempo del cual llega la fecha (Calendar)
     *
     * @param field de tipo Calendar, ejemplo Calendar.DAY_OF_MONTH
     * @return
     */
    private Date getFinIntervaloTiempo2(final Calendar cal, int field) {
        Calendar fecha = (Calendar) cal.clone();
        switch (field) {
            case Calendar.DAY_OF_MONTH:

                return null;

            case Calendar.MONTH:

                int anio = fecha.get(Calendar.YEAR);
                int mes = fecha.get(Calendar.MONTH);
                int dia = fecha.getActualMaximum(Calendar.DAY_OF_MONTH); //ultimo dia del mes

                // Fijar fecha final del mes a graficar
                fecha.set(anio, mes, dia, 23, 59, 0);
                fecha.clear(Calendar.MILLISECOND);
                Date ffinal = fecha.getTime();
                return ffinal;
            default:
                return null;
        }
    }
    // grafico configuracion grafico de barras

    /**
     * @param fechaCal
     * @param tipo     de tipo Calendar.DAY_OF_MONTH, Calendar.MONTH o Calendar.HOUR_OF_DAY
     */
    public void rellenarGraficaBarras(final List<FeedField> listaFeedFielddd, final BarChart mChart,
                                      final Dispositivo dispositivo, final Calendar fechaCal,
                                      final int tipo, final GraficaBarrras gbarras, final Holder holder) {

        // listaFeedField = new ArrayList();
        final String[] tituloGrafico = {""};

        switch (tipo) {
            case Calendar.MONTH:
                tituloGrafico[0] = "MES: " + obtenerNombreMes(fechaCal.getTime());
                break;
            case Calendar.DAY_OF_MONTH:
                tituloGrafico[0] = "DIA: " + obtenerNombreDia(fechaCal.getTime());
                break;

        }

        final GestorDispositivos gestionDis = GestorDispositivos.getInstance(context);

        Calendar inicio = (Calendar) fechaCal.clone();
        inicio = configurarInicioFecha(inicio, tipo);
        //Log.i("fecha inicio", inicio.getTime().toString());

        Calendar fin = (Calendar) fechaCal.clone();
        fin = configurarFinFecha(fin, tipo);
        //Log.i("fecha fin", fin.getTime().toString());

        TareaList nuevaTarea = new TareaList() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void ejecutar(ArrayList<FeedField> listaFF) {
                if (listaFF == null) {
                    listaFF = new ArrayList<>();
                }
                //listaFeedField.addAll(listaFF);
                //Toast.makeText(getApplicationContext(), "listaFeedField: "+listaFeedField.toString(), Toast.LENGTH_LONG).show();

                Log.i("unaTareaList", String.valueOf(listaFF));

                float limiteConsumo = 0;
                List<GraficaBarrras.ValorLabel> listavalores = obtenerListaValorLabel(listaFF, tipo);
                limiteConsumo = (float) calcularLimiteCondumo(listavalores);
                Log.i("unaTareaList", String.valueOf(listaFF));

                gbarras.setLimiteConsumo(limiteConsumo);
                gbarras.setListaValorLabel(listavalores);
                gbarras.setSubtitle(tituloGrafico[0]);
                gbarras.crearGrafica(mChart, GraficaBarrras.ESTILO_UN_COLOR_CON_LINEA_DELIMITADORA, false, context);

                // calculo de consumo total
                final float sumaTotal = suma(listavalores);

                //float promedio = limiteConsumo;
                boolean isdeterUsuario = GestorConsumo.getInstance(context).esConsumoDiarioDeterminadoPorUsuario();
                double promedioDeter = 0;
                if (isdeterUsuario && tipo == Calendar.DAY_OF_MONTH) {
                    //obtener consumo determinado por el usuario en configuraciones de la app
                    promedioDeter = GestorConsumo.getInstance(context).obtenerLimiteConsumoDiarioDeterminadoPorUsuario();
                    actualizarDetallesDispositivosHolder(holder, (float) promedioDeter,sumaTotal);
                } else {
                    // holder.pbar_consumoTotal.setsetMax((int)limiteConsumo);
                    TareaFloat unaTareaF = new TareaFloat() {

                        @Override
                        public void ejecutar(float promedio) {
                            actualizarDetallesDispositivosHolder(holder, promedio,sumaTotal);
                        }
                    };
                    obtenerConsumoPromedio(tipo, dispositivo, unaTareaF, fechaCal);
                }
            }

            public void actualizarDetallesDispositivosHolder(Holder holder, float promedio, float total) {

                holder.consumo_total.setText(obtenerConsumoFormateado(total));
                // ingreso de promedio
                holder.consumo_promedio.setText(obtenerConsumoFormateado(promedio));
                // ingreso de diferencia
                holder.Consumo_diferencia.setText(obtenerConsumoFormateado(promedio - total));
                // actualizacion de la barra de progreso indicando el consumo con respecto el promedio
                // actualizacion de la barra de progreso indicando el consumo con respecto el promedio
                rellenarBarraProgreso(promedio, total);
                //Toast.makeText(context, "promedio: " + promedio, Toast.LENGTH_SHORT).show();
            }

            //rev
            private void rellenarBarraProgreso(float promedio, float sumaTotal) {
                if (promedio > sumaTotal) {
                    holder.pbar_consumoTotal.setMax((int) promedio);
                    holder.pbar_consumoTotal.setProgress((int) sumaTotal);

                    cambiarColorBarra(R.drawable.fondo_progreso_menor);
                    //holder.pbar_consumoTotal.set
                } else {
                    holder.pbar_consumoTotal.setMax((int) sumaTotal);
                    holder.pbar_consumoTotal.setProgress((int) promedio);

                    cambiarColorBarra(R.drawable.fondo_progreso_mayor);
                }
            }

            /**
             * Método que cambia los colores de la barra de progreso.
             * Ha sido necesario hacer la 'ñapa' de obtener el objeto
             * {@link Rect} antes de cambiar los colores y después volver
             * a asignarlo
             * @param id
             */
            private void cambiarColorBarra(int id) {
                Rect bounds = holder.pbar_consumoTotal.getProgressDrawable().getBounds();
                holder.pbar_consumoTotal.setProgressDrawable(context.getResources().getDrawable(id));
                holder.pbar_consumoTotal.getProgressDrawable().setBounds(bounds);
            }

        };

        gestionDis.solicitarValoresDeField(GestorDispositivos.VALUE_FIELD_NUMBER, dispositivo.getId(), inicio, fin, tipo, nuevaTarea);
    }

    private String obtenerConsumoFormateado(float consumo_watts) {
        String cadena;

        String llave = GestorConsumo.LLAVE_CONSUMO;
        float consumo = GestorLlaveValor.getInstance(context).obtenerIntValor(llave);
        float consumoKw = consumo_watts / 1000;

        cadena = String.format(FORMATO_CONSUMO, consumoKw, consumoKw * consumo);
        return cadena;
    }

    public String obtenerNombreMes(Date fecha) {
        DateFormat df = new SimpleDateFormat("MMMM");
        if (fecha != null) {
            return df.format(fecha);
        }
        return "-";
    }

    public String obtenerNombreDia(Date fecha) {
        DateFormat df = new SimpleDateFormat("dd-MMMM");
        if (fecha != null) {
            return df.format(fecha);
        }
        return "-";
    }

    public static double calcularLimiteCondumo(List<GraficaBarrras.ValorLabel> listaFF) {
        if (listaFF == null) {
            return 0;
        }
        return promedio(listaFF);
    }

    public static class Holder {
        public TextView consumo_total;
        public TextView consumo_promedio;
        public TextView Consumo_diferencia;
        public ProgressBar pbar_consumoTotal;
    }

    public void guardarEsConsumoDiarioDeterminadoPorUsuario(boolean es) {
        GestorLlaveValor.getInstance(context).guardarValor(es, LLAVE_ES_CONSUMO_DETERMINADO_X_USUARIO);
    }

    public boolean esConsumoDiarioDeterminadoPorUsuario() {
        boolean valor = GestorLlaveValor.getInstance(context).obtenerBooleanValor(LLAVE_ES_CONSUMO_DETERMINADO_X_USUARIO);
        return valor;
    }

    public void guardarLimiteConsumoDiarioDeterminadoPorUsuario(float limiteConsumo) {
        GestorLlaveValor.getInstance(context).guardarValor(limiteConsumo, LLAVE_CONSUMO_DETERMINADO_X_USUARIO);
    }

    public float obtenerLimiteConsumoDiarioDeterminadoPorUsuario() {
        float valor = GestorLlaveValor.getInstance(context).obtenerFloatValor(LLAVE_CONSUMO_DETERMINADO_X_USUARIO);
        return valor;
    }

    public void guardarFechaUltimaNotificacionDiaria(Calendar fecha) {
        String strfecha = "";
        if (fecha != null) {
            DateFormat df = new SimpleDateFormat(FORMATO_FECHA_NOTIFICACION_DIARIA);
            strfecha = df.format(fecha.getTime());
        }
        GestorLlaveValor.getInstance(context).guardarValor(strfecha, LLAVE_ULTIMA_FECHA_NOTIFICACION_DIARIA);
    }

    public Calendar obtenerFechaUltimaNotificacionDiaria() {
        Calendar fecha = Calendar.getInstance();
        String strfecha = GestorLlaveValor.getInstance(context).obtenerStringValor(LLAVE_ULTIMA_FECHA_NOTIFICACION_DIARIA);
        if (strfecha != null) {
            DateFormat df = new SimpleDateFormat(FORMATO_FECHA_NOTIFICACION_DIARIA);
            try {
                Date fechadate = df.parse(strfecha);
                fecha.setTime(fechadate);
                return fecha;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * obtiene el promedio segun tipo de escala
     *
     * @param tipo        Calendar.MOUNTH, Calendar.DAY_OF_MONTH o Calendar.HOUR_OF_DAY
     * @param dispositivo
     */
    public void obtenerConsumoPromedio(final int tipo, final Dispositivo dispositivo, final TareaFloat tareaF, Calendar fechaFin) {
        final List<FeedField> listaFeedFields = new ArrayList();
        TareaList unaTarea = null;
        Calendar fechaInicio = (Calendar) fechaFin.clone();
        //fechaInicio.setTime(fechaFin.getTime());
        fechaFin.add(Calendar.MONTH, -1);
        fechaFin = configurarFinFecha(fechaFin, tipo);
        fechaInicio.add(Calendar.MONTH, -NUM_MESES_OPROMEDIO);
        fechaInicio = configurarInicioFecha(fechaInicio, tipo);

        unaTarea = new TareaList() {
            @Override
            public void ejecutar(ArrayList<FeedField> listaFeedField) {
                tareaF.ejecutar(promediar(listaFeedField, tipo));
            }
        };

        GestorDispositivos.getInstance(context).solicitarValoresDeField(
                GestorDispositivos.VALUE_FIELD_NUMBER, dispositivo.getId(),
                fechaInicio, fechaFin, tipo, unaTarea);
    }

    /**
     * @param listaValores
     * @param tipo
     * @return
     */
    public static float promediar(List<FeedField> listaValores, int tipo) {
        float suma = 0;
        float sumaAnterior = 0;
        int diferentes = 0;
        int anterior = -1;
        Calendar calendar = Calendar.getInstance();
        List<Float> listaValAux = new ArrayList<>();
        for (FeedField elem : listaValores) {
            calendar.setTime(elem.getFecha());
            int datoActual = calendar.get(tipo);
            if (anterior == -1) {
                anterior = datoActual;
                diferentes = 1;
            } else {
                if (anterior != datoActual) {
                    anterior = datoActual;
                    diferentes++;
                    listaValAux.add(suma - sumaAnterior);
                    sumaAnterior = suma;
                }
            }
            float valor = elem.getValor().floatValue();
            suma += valor;
        }
        listaValAux.add(suma - sumaAnterior);
        for (int i = 0; i < listaValAux.size(); ) {

            if (listaValAux.get(i) == 0) {
                listaValAux.remove(i);
            } else {
                i++;
            }
        }
        Log.i("Listas-- ", "lista.size: " + listaValAux.size() + "difer:" + diferentes);
        if (diferentes != 0) {
            return suma / diferentes;
        }
        return 0;
    }

}
