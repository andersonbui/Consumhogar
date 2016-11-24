package com.andersonbuitron.mipruebathingspeakweb.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.andersonbuitron.mipruebathingspeakweb.R;
import com.andersonbuitron.mipruebathingspeakweb.database.BDDispositivo;
import com.andersonbuitron.mipruebathingspeakweb.modelos.Dispositivo;

public class AddDispositivoActivity extends AppCompatActivity {

    ActionMode mActionMode = null;
    TextView tv_id;
    EditText et_nombre;
    Spinner spinner_icono;
    Dispositivo dispositivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dispositivo);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Agregar dispositivo");

        //mActionMode = startActionMode(mActionModeCallback);
        tv_id = (TextView) findViewById(R.id.tv_id);
        et_nombre = (EditText) findViewById(R.id.et_nombre);
        spinner_icono = (Spinner) findViewById(R.id.spinner_icono);

        dispositivo = (Dispositivo) getIntent().getSerializableExtra("canal_clickeado");

        tv_id.setText(dispositivo.getId());
        et_nombre.setText(dispositivo.getNombre());
        //Toast.makeText(this, "api_key: "+dispositivo.getApi_key_write(), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_context, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button

            case R.id.btn_Aceptar:
                BDDispositivo bdDispositivo = BDDispositivo.getInstance(this);
                dispositivo.setNombre(et_nombre.getText().toString());
                dispositivo.setIcono("");
                Toast.makeText(this, "dispositivo guardado: "+ dispositivo.toString(), Toast.LENGTH_SHORT).show();
                //insertar canal en la base de datos
                bdDispositivo.insertCanal(dispositivo);
                Intent intent = new Intent(this,MainActivity.class);
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
