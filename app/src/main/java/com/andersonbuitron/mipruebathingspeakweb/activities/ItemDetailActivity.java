package com.andersonbuitron.mipruebathingspeakweb.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.andersonbuitron.mipruebathingspeakweb.R;
import com.andersonbuitron.mipruebathingspeakweb.adaptadores.DispositivoAdapter;
import com.andersonbuitron.mipruebathingspeakweb.callbacks.TareaString;
import com.andersonbuitron.mipruebathingspeakweb.fragments.ConsumoMesFragment;
import com.andersonbuitron.mipruebathingspeakweb.fragments.DispositivosFragment;
import com.andersonbuitron.mipruebathingspeakweb.gestores.GestorDispositivos;
import com.andersonbuitron.mipruebathingspeakweb.modelos.Dispositivo;

import java.util.Calendar;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link //ItemListActivity}.
 */

//public class ItemDetailActivity extends AppCompatActivity implements
//        ConsumoMesFragment.OnFragmentInteractionListener,ConsumoHoraFragment.OnFragmentInteractionListener{
public class ItemDetailActivity extends AppCompatActivity {

    Dispositivo unDispositivo;
    CompoundButton compoundButton;
    public static final String ARG_ITEM_DISPOSITIVO = "dispositivo_selected";

    // region manipulacion de FragmentStatePager

    private static final int num_paginas = 6;
    SlidePagerAdapter mAdapter;
    ViewPager mPager;
    static Context context;
    Calendar fechaDetalle;
    int mesFechaAux;// Servira para guardar el mes mostrado en grafico, util para trasladarse al siguiente o el anterior

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        //obtener dispositivo
        unDispositivo = (Dispositivo) getIntent().getSerializableExtra(ARG_ITEM_DISPOSITIVO);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);

        toolbar.setTitle(unDispositivo.getNombre());
        setSupportActionBar(toolbar);

        //comportamiento del switch power
        compoundButton = ((CompoundButton) findViewById(R.id.switch_power));

        //ultimo datos de field 2 para
        actualizarCompoundButton(compoundButton, unDispositivo.getId(), GestorDispositivos.SWITCH_FIELD, this);

        //comportamiento de conpoundButton
        compoundButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DispositivoAdapter.notificarCambiodeSwitchApagado(isChecked, getApplicationContext(), buttonView, unDispositivo.getApi_key_write());
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
/*
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
           Bundle arguments = new Bundle();
            arguments.putSerializable(ARG_ITEM_DISPOSITIVO,
                    getIntent().getSerializableExtra(ARG_ITEM_DISPOSITIVO));

            ItemDetailFragment fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();

            // Definir fragment inicial de consumo

            getSupportFragmentManager().beginTransaction().
                    replace(R.id.contenedor_tipo_consumo,  new ConsumoMesFragment()).
                    commit();

        }*/

        // Establecer la fecha final del mes actual y defnir el numero de paginas
        fechaDetalle = Calendar.getInstance();

        //num_paginas = ges
        mesFechaAux =  fechaDetalle.get(Calendar.MONTH);

        mAdapter = new SlidePagerAdapter(getSupportFragmentManager(), unDispositivo);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mPager.setCurrentItem(num_paginas - 1);

        // Watch for button clicks.
        Button button = (Button) findViewById(R.id.goto_first);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            }
        });
        button = (Button) findViewById(R.id.goto_last);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mPager.setCurrentItem(num_paginas - 1);
            }
        });
    }

    public  class SlidePagerAdapter extends FragmentStatePagerAdapter {

        //Dispositivo unDispositivo;

        public SlidePagerAdapter(FragmentManager fm, Dispositivo dispositivo) {
            super(fm);
            //this.unDispositivo = dispositivo;
        }

        @Override
        public Fragment getItem(int position) {
            Log.i("Llamando a getItem", "Llamando a getItem()");
            int posicionDerIzq = num_paginas-1-position;
            mesFechaAux += posicionDerIzq;

            Calendar fecha = (Calendar) fechaDetalle.clone();
            fecha.add(Calendar.MONTH,num_paginas-position);
            Log.i("Calendar","posicion["+(this.getCount())+"]"+" fecha ["+ fecha.getTime().toString()+"]");
            Toast.makeText(ItemDetailActivity.this, "item: "+mPager.getCurrentItem(), Toast.LENGTH_SHORT).show();
            return ConsumoMesFragment.newInstance(unDispositivo, fechaDetalle,position);

        }
        @Override
        public int getCount() {
            //Log.i("Llamando a cound", "Llamando a getCound()");
            return num_paginas;
        }

    }

    // endregion


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.btn_borrar_dispositivo) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.dialog_borrar_dispositivo)
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            GestorDispositivos gestorD = GestorDispositivos.getInstance(getApplicationContext());
                            gestorD.eliminarDispositivo(unDispositivo.getId(), DispositivosFragment.disposRegistradosAdapter);
                            Toast.makeText(getApplicationContext(), "dispositivo borrado ", Toast.LENGTH_SHORT).show();
                            navigateUpTo(new Intent(getApplicationContext(), MisDispositivosActivity.class));
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
        } else if (id == android.R.id.home) {

            navigateUpTo(new Intent(this, MisDispositivosActivity.class));
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

/*
    public void mostrarGraficoDiario(View view) {

        Intent intent = new Intent(getApplicationContext(),GraficaFullScreenActivity.class);
        intent.putExtra(GraficaFullScreenActivity.EXTRA_DISPOSITIVO,unDispositivo);
        startActivity(intent);

    }

    public void getConsumoMensual(View view) {
        GestorDispositivos gestionDis = GestorDispositivos.getInstance(getApplicationContext());
        Calendar calendar = Calendar.getInstance();
        Date ffinal  = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        Date finicial = calendar.getTime();
        String ESCALA_TIEMPO = "daily"; //minutos

        String url=gestionDis.solicitarValoresDeField(unDispositivo.getApi_key_write(),2,unDispositivo.getId(),finicial,ffinal,ESCALA_TIEMPO);

        Intent intent = new Intent(this,GraficaFullScreenActivity.class);
        intent.putExtra("url",url);
        startActivity(intent);

    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

*/

    /*
        public void onClickItemSelected(View view) {
            // Handle navigation compoundButton item clicks here.
            int id = view.getId();
            Fragment fragment = null;
            Boolean fragmentoSeleccionado = false;
            switch (id) {
                case R.id.btn_get_consumo_diario:

                    fragment = new ConsumoMesFragment();
                    fragmentoSeleccionado = true;

                    break;
                case R.id.btn_get_consumo_hora:
                    fragment = new ConsumoHoraFragment();
                    fragmentoSeleccionado = true;
                    break;
                default:

            }

            if (fragmentoSeleccionado) {

                getSupportFragmentManager().beginTransaction().
                        replace(R.id.contenedor_tipo_consumo, fragment).
                        commit();
            }
        }
    */
    public static void actualizarCompoundButton(final CompoundButton compoundButtonSwitch, String idChannel, int field, final Context context) {
        GestorDispositivos gestionDis = GestorDispositivos.getInstance(context);
        gestionDis.solicitarUltimoValorDeField(field, idChannel,
                new TareaString() {

                    @Override
                    public void ejecutar(String resultado) {
                        compoundButtonSwitch.setChecked(resultado.equals("1"));
                        //Toast.makeText(context, "ultimo valor: " + resultado, Toast.LENGTH_SHORT).show();
                    }

                }
        );
    }


}
