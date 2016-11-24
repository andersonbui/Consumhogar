package com.andersonbuitron.mipruebathingspeakweb.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.andersonbuitron.mipruebathingspeakweb.R;
import com.andersonbuitron.mipruebathingspeakweb.gestores.GestorDispositivos;

public class ValorConsumoActivity extends AppCompatActivity {

    EditText et_valor_consumo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valor_consumo);
        et_valor_consumo = (EditText)findViewById(R.id.et_valor_consumo);
        GestorDispositivos gestorD =  GestorDispositivos.getInstance(getApplicationContext());
        //int consumo = gestorD.leerConsumo(this);
        //et_valor_consumo.setText(consumo);
    }

    public void onClick_aceptar(View view){
        GestorDispositivos gestorD =  GestorDispositivos.getInstance(getApplicationContext());
        gestorD.guardarConsumo(Integer.parseInt(et_valor_consumo.getText().toString()),this);
    }
}
