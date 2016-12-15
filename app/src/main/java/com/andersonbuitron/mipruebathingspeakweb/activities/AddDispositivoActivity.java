package com.andersonbuitron.mipruebathingspeakweb.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.andersonbuitron.mipruebathingspeakweb.R;
import com.andersonbuitron.mipruebathingspeakweb.adaptadores.IconoSpinnerAdapter;
import com.andersonbuitron.mipruebathingspeakweb.database.BDDispositivo;
import com.andersonbuitron.mipruebathingspeakweb.gestores.GestorDispositivos;
import com.andersonbuitron.mipruebathingspeakweb.modelos.Dispositivo;

public class AddDispositivoActivity extends AppCompatActivity {

    ActionMode mActionMode = null;
    TextView tv_id;
    EditText et_nombre;
    Spinner spinner_icono;
    Dispositivo dispositivo;

    private ImageView iv_icono;

    //rev
    private String nombre_icono;

    private String[] nombres_dispositivos = {"Television", "Ventilador", "Equipo de Sonido", "Bombillo"};

    private int[] iconos_pequeños =
            { R.drawable.ic_stv_24, R.drawable.ic_sventilador_24, R.drawable.ic_sequipo_24, R.drawable.ic_sbombillo_24 };

    private int[] iconos_grandes =
            { R.drawable.ic_stv, R.drawable.ic_sventilador, R.drawable.ic_sequipo, R.drawable.ic_sbombillo };

    private String[] nombres_iconos =
            { "ic_stv", "ic_sventilador", "ic_sequipo", "ic_sbombillo"};
    //revend
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dispositivo);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Agregar barraGraf");

        inicializarVariables();
        //rev
        GestorDispositivos objGD = new GestorDispositivos();
        if(objGD.AGREGAR)
            getSupportActionBar().setTitle("Agregar Dispositivo");
        else
            getSupportActionBar().setTitle("Editar Dispositivo");

    }

    private void inicializarVariables()
    {
        //mActionMode = startActionMode(mActionModeCallback);
        tv_id = (TextView) findViewById(R.id.tv_id);
        et_nombre = (EditText) findViewById(R.id.et_nombre);
        iv_icono = (ImageView) findViewById(R.id.iv_icono);

        spinner_icono = (Spinner) findViewById(R.id.spinner_icono);

        IconoSpinnerAdapter objISA = new IconoSpinnerAdapter(this, nombres_dispositivos, iconos_pequeños);
        spinner_icono.setAdapter(objISA);

        spinner_icono.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                //Toast.makeText(getApplicationContext(), nombres_dispositivos[position], Toast.LENGTH_SHORT).show();
                iv_icono.setImageResource(iconos_grandes[position]);
                //rev
                nombre_icono = nombres_iconos[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dispositivo = (Dispositivo) getIntent().getSerializableExtra("canal_clickeado");

        tv_id.setText(dispositivo.getId());
        et_nombre.setText(dispositivo.getNombre());

        //rev

        GestorDispositivos objGD = new GestorDispositivos();
        if(!objGD.AGREGAR)
        {
            Resources resources = this.getResources();
            int resourceId = resources.getIdentifier(dispositivo.getIcono(), "drawable", this.getPackageName());
            iv_icono.setImageResource(resourceId);
        }


        //Toast.makeText(this, "api_key: "+barraGraf.getApi_key_write(), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_context, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button

            case R.id.btn_Aceptar:
                //rev
                GestorDispositivos objGD = new GestorDispositivos();

                BDDispositivo bdDispositivo = BDDispositivo.getInstance(this);
                dispositivo.setNombre(et_nombre.getText().toString());

                dispositivo.setIcono(nombre_icono);

                if(objGD.AGREGAR)
                {
                    Toast.makeText(this, "Dispositivo agregado: "+ dispositivo.toString(), Toast.LENGTH_SHORT).show();
                    //insertar canal en la base de datos
                    bdDispositivo.insertDispositivo(dispositivo);
                }
                else
                {
                    bdDispositivo.actualizarDispositivo(dispositivo);
                    Toast.makeText(this, "Dispositivo editado: "+ dispositivo.toString(), Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(this,ListDispositivosActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                //onBackPressed();

                break;

            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
