package com.andersonbuitron.mipruebathingspeakweb.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.andersonbuitron.mipruebathingspeakweb.R;
import com.andersonbuitron.mipruebathingspeakweb.fragments.ItemDetailFragment;
import com.andersonbuitron.mipruebathingspeakweb.gestores.GestorDispositivos;
import com.andersonbuitron.mipruebathingspeakweb.modelos.Dispositivo;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link //ItemListActivity}.
 */
public class ItemDetailActivity extends AppCompatActivity {

    Dispositivo mDispositivo;
    CompoundButton compoundButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_details);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View compoundButton) {
                Snackbar.make(compoundButton, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        mDispositivo = (Dispositivo) getIntent().getSerializableExtra(ItemDetailFragment.ARG_ITEM_DISPOSITIVO);

        //comportamiento del switch power
        compoundButton = ((CompoundButton)findViewById(R.id.switch_power));

        compoundButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)  {
                String valor = "";
                if(isChecked){

                    Toast.makeText(ItemDetailActivity.this, "Dispositivo prendido", Toast.LENGTH_SHORT).show();
                    valor = "1";
                }else {
                    valor = "0";
                    Toast.makeText(ItemDetailActivity.this, "Dispositivo apagado", Toast.LENGTH_SHORT).show();
                }

                GestorDispositivos.
                        getInstance(getApplicationContext()).
                        enviarDatoThingSpeak(mDispositivo.getApi_key_write(),2,valor, compoundButton);
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putSerializable(ItemDetailFragment.ARG_ITEM_DISPOSITIVO,
                    getIntent().getSerializableExtra(ItemDetailFragment.ARG_ITEM_DISPOSITIVO));

            ItemDetailFragment fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.btn_borrar_dispositivo) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.dialog_borrar_dispositivo)
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(getApplicationContext(), "dispositivo borrado ", Toast.LENGTH_SHORT).show();
                            navigateUpTo(new Intent(getApplicationContext(), MainActivity.class));
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
             builder.create().show();
            return true;
        }else
        if (id == android.R.id.home) {

            navigateUpTo(new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_button_borrar_dispositivo, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
