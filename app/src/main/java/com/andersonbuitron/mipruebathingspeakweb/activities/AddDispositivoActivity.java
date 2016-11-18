package com.andersonbuitron.mipruebathingspeakweb.activities;

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
import com.andersonbuitron.mipruebathingspeakweb.database.BDCanal;
import com.andersonbuitron.mipruebathingspeakweb.modelos.Canal;

public class AddDispositivoActivity extends AppCompatActivity {

    ActionMode mActionMode = null;
    TextView tv_id;
    EditText et_nombre, et_consumo;
    Spinner spinner_icono;
    Canal canal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_socket);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Agregar Socket");

        //mActionMode = startActionMode(mActionModeCallback);
        tv_id = (TextView) findViewById(R.id.tv_id);
        et_nombre = (EditText) findViewById(R.id.et_nombre);
        et_consumo = (EditText) findViewById(R.id.et_consumo);
        spinner_icono = (Spinner) findViewById(R.id.spinner_icono);

        canal = (Canal) getIntent().getSerializableExtra("canal_clickeado");

        tv_id.setText(canal.getId());
        et_nombre.setText(canal.getNombre());
        Toast.makeText(this, "api_key: "+canal.getApi_key_write(), Toast.LENGTH_LONG).show();
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
                Toast.makeText(this, "Presionaste aceptar.", Toast.LENGTH_SHORT).show();

                BDCanal bdCanal = new BDCanal(this);
                canal.setNombre(et_nombre.getText().toString());
                canal.setIcono("");
                bdCanal.insertCanal(canal);
                onBackPressed();
                break;

            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
