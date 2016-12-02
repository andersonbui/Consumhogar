package com.andersonbuitron.mipruebathingspeakweb.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.andersonbuitron.mipruebathingspeakweb.R;
import com.andersonbuitron.mipruebathingspeakweb.adaptadores.DispositivoNoRegAdapter;
import com.andersonbuitron.mipruebathingspeakweb.gestores.GestorDispositivos;
import com.andersonbuitron.mipruebathingspeakweb.modelos.Dispositivo;

import java.util.ArrayList;
import java.util.List;

public class ListDispositivoDisponiblesActivity extends AppCompatActivity {

    //ista de elementos
    ListView vCanalesList;
    ArrayAdapter mCanalesAdapter;
    List<Dispositivo> list_canales;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_dispositivo_disponibles);
        //soporte de boton atras

        setTitle("Dispositivos disponibles");
        list_canales = new ArrayList<>();

        context = this;

        //instancia del listView
        vCanalesList = (ListView) findViewById(R.id.dispositivos_disponibles_list);

        //inicializa el adaptador con la fuente de datos
        mCanalesAdapter = new DispositivoNoRegAdapter(context,list_canales);
        GestorDispositivos.getInstance(context).obtenerDispositivosEn(mCanalesAdapter);

        //relacionando la lista con el adaptador
        vCanalesList.setAdapter(mCanalesAdapter);
        //setear una escucha a las clicks de los item

        vCanalesList.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Dispositivo dispositivoClickeado = (Dispositivo) mCanalesAdapter.getItem(position);
                Toast.makeText(context, "Agregando socket:\n "+ dispositivoClickeado.getNombre(), Toast.LENGTH_SHORT).show();
                //view.setSelected(true);
                Intent intent = new Intent(context,AddDispositivoActivity.class);
                intent.putExtra("canal_clickeado", dispositivoClickeado);
                startActivity(intent);
            }
        });
    }

}
