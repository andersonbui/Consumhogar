package com.andersonbuitron.mipruebathingspeakweb.servicios;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.andersonbuitron.mipruebathingspeakweb.R;
import com.andersonbuitron.mipruebathingspeakweb.activities.DetalleDispositivoActivity;
import com.andersonbuitron.mipruebathingspeakweb.callbacks.TareaList;
import com.andersonbuitron.mipruebathingspeakweb.extras.GraficaBarrras;
import com.andersonbuitron.mipruebathingspeakweb.gestores.GestorConsumo;
import com.andersonbuitron.mipruebathingspeakweb.gestores.GestorDispositivos;
import com.andersonbuitron.mipruebathingspeakweb.modelos.Dispositivo;
import com.andersonbuitron.mipruebathingspeakweb.modelos.FeedField;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.andersonbuitron.mipruebathingspeakweb.gestores.GestorFeedField.obtenerListaValorLabel;


/**
 * Created by debian on 7/12/16.
 */

public class ServicioNotificacion extends Service {

    public static final String SERVICE_CLASSNAME = "com.andersonbuitron.mipruebathingspeakweb.servicios.ServicioNotificacion";
    public static final String TAG = "ServicioNotificacionConsumo";
    private static int ID_NOTIFICATION_COUNTER = 1001;
    public static final String ID_NOTIFICATION_STRING = "ID_NOTIFICACION_CONSUMO";
    public static final String ID_DISPOSITIVO_NOTIFICADO = "id_dispositivo_notificado";
    MiTarea miTarea;
    NotificationManager notificationManager;
    GestorDispositivos gestorDispositivos;
    boolean tareaCancelada;
    Dispositivo unDispositivoNotifi;
    public ServicioNotificacion() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "sericio de notificacion de consumo creado", Toast.LENGTH_SHORT).show();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        gestorDispositivos = GestorDispositivos.getInstance(getApplicationContext());
        tareaCancelada = false;
        miTarea = new MiTarea();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Servicio de notificacion de consumo iniciado [" + startId + "]", Toast.LENGTH_SHORT).show();
        miTarea.execute();


        return START_STICKY; //se crea un nuevo servicio despues de haber sido destruido por el sistema
        // cuando se inicia y termina explicitamente
        //return  START_NOT_STICKY; //para procesar acciones especificas o comandos
        //solo se reiniciara si hay llamadas pendientes a starService, para actualizaciones de red o polling

        //return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "servicio de notificacion de consumo detenido", Toast.LENGTH_SHORT).show();

        miTarea.onCancelled("");
        tareaCancelada = true;
    }

    public class MiTarea extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {



            while (!tareaCancelada) {

                Calendar fechaActual = Calendar.getInstance();
                Calendar fechaUtimaNoti = GestorConsumo.getInstance(getApplicationContext()).obtenerFechaUltimaNotificacionDiaria();
                Log.i("fechaActual", "" + fechaActual.getTime());

                if (fechaUtimaNoti == null) {
                    verificarConsumo();
                } else {
                    Log.i("fechaUtimaNoti", "" + fechaUtimaNoti.getTime());
                    fechaActual = GestorConsumo.configurarFinFecha(fechaActual, Calendar.DAY_OF_MONTH);
                    fechaUtimaNoti = GestorConsumo.configurarFinFecha(fechaUtimaNoti, Calendar.DAY_OF_MONTH);

                    if (!fechaActual.equals(fechaUtimaNoti)) {
                        verificarConsumo();
                        GestorConsumo.getInstance(getApplicationContext()).guardarFechaUltimaNotificacionDiaria(null);
                    }
                }
            }
            return "";
        }

        private void verificarConsumo( ) {

            List<Dispositivo> listaDispositivos = gestorDispositivos.recuperarDispositivosBaseDatos();


            for ( Dispositivo elem : listaDispositivos) {
                unDispositivoNotifi = elem;

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Calendar fechaInicio = Calendar.getInstance();
                fechaInicio = GestorConsumo.configurarInicioFecha(fechaInicio, Calendar.MONTH);
                Calendar fechaFin = Calendar.getInstance();
                String escala = "720";

                TareaList unaTarea = new TareaList() {

                    Dispositivo unDispositivo = unDispositivoNotifi;

                    @Override
                    public void ejecutar(ArrayList<FeedField> listaFeedField) {
                        int tamanio = listaFeedField.size();
                        Log.i("ServicioNotificacion", "tamanio: " + tamanio);
                        ArrayList<GraficaBarrras.ValorLabel> listaVL = obtenerListaValorLabel(listaFeedField, Calendar.MONTH);
                        if (!listaVL.isEmpty()) {
                            GraficaBarrras.ValorLabel valor = listaVL.remove(listaVL.size() - 1);
                            boolean isdeterUsuario = GestorConsumo.getInstance(getApplicationContext()).esConsumoDiarioDeterminadoPorUsuario();
                            double promedio = 0;
                            if (isdeterUsuario) {
                                //obtener consumo determinado por el usuario en configuraciones de la app
                                promedio = GestorConsumo.getInstance(getApplicationContext()).obtenerLimiteConsumoDiarioDeterminadoPorUsuario();
                            } else {
                                promedio = GestorConsumo.calcularLimiteCondumo(listaVL);
                            }
                            Log.i("Servicio de consumo", "promedio[" + promedio + "]-ultimoValor[" + valor.getValor() + "]" + (float) (valor.getValor() - promedio));
                            if (promedio < valor.getValor()) {
                                publishProgress();
                                crearNotificacion("Dispositivo nombrado: " + unDispositivo.getNombre(),  (float) (valor.getValor() - promedio),unDispositivo);
                            }

                        }
                    }

                    @Override
                    public List getList() {
                        return new ArrayList<FeedField>();
                    }
                };
                gestorDispositivos.solicitarValoresDeField(elem.getApi_key_write(), GestorDispositivos.VALUE_FIELD_NUMBER, elem.getId(), fechaInicio, fechaFin, escala, unaTarea);
            }


        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            //Toast.makeText(getApplicationContext(), "contando hasta " + values[0], Toast.LENGTH_SHORT).show();
            //guardar fecha de ultima notificacion
            GestorConsumo.getInstance(getApplicationContext()).guardarFechaUltimaNotificacionDiaria(Calendar.getInstance());
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }


        private void crearNotificacion(String descripcion, float valor, Dispositivo dispositivo) {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());
            builder.setSmallIcon(R.mipmap.ic_logo);
            builder.setContentTitle("Exceso de consumo");
            builder.setTicker("Exceso de consumo diario " + valor + " Wh");
            //builder.setContentText("En estos momentos se le informa que ha excedido su consumo diario de energia");
            builder.setContentText(descripcion);

            builder.setWhen(System.currentTimeMillis());

            Uri sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(sonido);

            Intent intent = new Intent(getApplicationContext(), DetalleDispositivoActivity.class);
            //intent.setClass(getApplicationContext(),ListDispositivosActivity.class);
            intent.putExtra(ID_NOTIFICATION_STRING, ID_NOTIFICATION_COUNTER);
            intent.putExtra(DetalleDispositivoActivity.ARG_ITEM_DISPOSITIVO, dispositivo);

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

            builder.setContentIntent(pendingIntent);
            //notificationManager.
            notificationManager.notify(ID_NOTIFICATION_COUNTER, builder.build());
            //ID_NOTIFICATION_COUNTER++;
        }
    }

}
