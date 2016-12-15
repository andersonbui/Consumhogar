package com.andersonbuitron.mipruebathingspeakweb.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.andersonbuitron.mipruebathingspeakweb.R;
import com.andersonbuitron.mipruebathingspeakweb.gestores.GestorConsumo;
import com.andersonbuitron.mipruebathingspeakweb.gestores.GestorDispositivos;
import com.andersonbuitron.mipruebathingspeakweb.gestores.GestorLlaveValor;

import static com.andersonbuitron.mipruebathingspeakweb.servicios.ServicioNotificacion.SERVICE_CLASSNAME;

public class ConfiguracionesActivity extends AppCompatActivity {

    EditText et_valor_consumo;
    CheckBox cb_servicio_notificacion;
    CheckBox cb_es_limite_determinado;
    EditText et_limite_consumo;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuraciones);

        //int consumo = gestorD.leerConsumo(this);
        //et_valor_consumo.setText(consumo);

        // servicio de notificacion
        cb_servicio_notificacion = (CheckBox) findViewById(R.id.cb_servicio_notificacion);
        cb_servicio_notificacion.setChecked(serviceIsRunning());
        cb_es_limite_determinado = (CheckBox) findViewById(R.id.cb_es_limite_determinado);
        et_limite_consumo = (EditText) findViewById(R.id.et_limite_consumo);
        context =  this;

        //colocar limite de consumo guardado anteriormente, si existe
        float limiteCon = GestorConsumo.getInstance(getApplicationContext()).obtenerLimiteConsumoDiarioDeterminadoPorUsuario();
        if(!Float.isNaN(limiteCon)){
            et_limite_consumo.setText(""+limiteCon);
        }
        // activar/desactivar cb_es_limite_determinado del estado anterior elegido por el usuario
        boolean estado_cb = GestorConsumo.getInstance(getApplicationContext()).esConsumoDiarioDeterminadoPorUsuario();
        cb_es_limite_determinado.setChecked(estado_cb);

        obserEsLimiteDeterminado();

        cb_es_limite_determinado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obserEsLimiteDeterminado();
            }
        });
        inicializarVariables();
    }

    public void obserEsLimiteDeterminado(){
        if(cb_es_limite_determinado.isChecked()){
            et_limite_consumo.setEnabled(true);
        }else{
            et_limite_consumo.setEnabled(false);
        }
    }

    private void inicializarVariables() {
        et_valor_consumo = (EditText) findViewById(R.id.et_valor_consumo);
        et_valor_consumo.setText(getValorConsumo());
    }

    private String getValorConsumo() {
        String llave = GestorConsumo.LLAVE_CONSUMO;
        int consumo = GestorLlaveValor.getInstance(this).obtenerIntValor(llave);
        String valor_consumo = String.valueOf(consumo);
        return valor_consumo;
    }

    public void onClick_aceptar(View view) {
        String valor_consumo = et_valor_consumo.getText().toString();
        try {
            int consumo = Integer.parseInt(valor_consumo);
            if (consumo >= 0) {
                String llave = GestorConsumo.LLAVE_CONSUMO;
                if (GestorLlaveValor.getInstance(this).guardarValor(consumo, llave))
                    this.onBackPressed();
                if(cb_es_limite_determinado.isChecked()){
                    GestorConsumo.getInstance(this).guardarEsConsumoDiarioDeterminadoPorUsuario(true);
                    float limitConsumo = Float.parseFloat(et_limite_consumo.getText().toString());
                    if(!Float.isNaN(limitConsumo)){
                        GestorConsumo.getInstance(getApplicationContext()).guardarLimiteConsumoDiarioDeterminadoPorUsuario(limitConsumo);
                    }else{
                        Toast.makeText(this, "limite de consumo debe ser entero positivo.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    GestorConsumo.getInstance(getApplicationContext()).guardarLimiteConsumoDiarioDeterminadoPorUsuario(Float.NaN);
                    GestorConsumo.getInstance(this).guardarEsConsumoDiarioDeterminadoPorUsuario(false);
                }
            } else
                Toast.makeText(this, "Debe ingresar un valor en pesos mayor o igual a cero para el Kw.", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Toast.makeText(this, "Debe ingresar un valor en pesos para el Kw.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickServicoNotificacion(View view) {
        CheckBox cb = (CheckBox) view;
        activarServicio(cb.isChecked());
    }

    public void onClickResetNotificaciones(View view){
        //eeiminar fecha de ultima notificacion, si existiese
        GestorConsumo.getInstance(getApplicationContext()).guardarFechaUltimaNotificacionDiaria(null);
    }

    private void activarServicio(boolean activar) {
        //if ((!serviceIsRunning() && activar) || (serviceIsRunning() && !activar) ) {
            GestorDispositivos gdispo = GestorDispositivos.getInstance(this);
            gdispo.activarServicioNotificaciones(activar);
            //GestorLlaveValor.getInstance(this).guardarValor(activar, ServicioNotificacion.TAG);
        //}
    }

    private boolean serviceIsRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (SERVICE_CLASSNAME.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
