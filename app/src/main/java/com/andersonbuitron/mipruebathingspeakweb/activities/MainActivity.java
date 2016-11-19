package com.andersonbuitron.mipruebathingspeakweb.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.andersonbuitron.mipruebathingspeakweb.R;
import com.andersonbuitron.mipruebathingspeakweb.adaptadores.DispositivoAdapter;
import com.andersonbuitron.mipruebathingspeakweb.fragments.AzulFragment;
import com.andersonbuitron.mipruebathingspeakweb.fragments.DispositivoNoRegFragment;
import com.andersonbuitron.mipruebathingspeakweb.fragments.VerdeFragment;
import com.andersonbuitron.mipruebathingspeakweb.gestores.GestorDispositivos;
import com.andersonbuitron.mipruebathingspeakweb.modelos.Dispositivo;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements
        NavigationView.OnNavigationItemSelectedListener,
        VerdeFragment.OnFragmentInteractionListener,
        AzulFragment.OnFragmentInteractionListener,
        DispositivoNoRegFragment.OnFragmentInteractionListener {


    ListView lvDisposRegistradosList;
    ArrayAdapter disposRegistradosAdapter;
    ArrayList<Dispositivo> list_dispos_registrados;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        //--------------------------------------------
        //instancia del listView
        lvDisposRegistradosList = (ListView) findViewById(R.id.dispos_registrados_list);

        list_dispos_registrados = new ArrayList<>();
        //inicializa el adaptador con la fuente de datos
        disposRegistradosAdapter = new DispositivoAdapter(this, list_dispos_registrados);

        GestorDispositivos.getInstance(this).recuperarCanalesBaseDatos(disposRegistradosAdapter);

        //relacionando la lista con el adaptador
        lvDisposRegistradosList.setAdapter(disposRegistradosAdapter);
        //setear una escucha a las clicks de los item

        lvDisposRegistradosList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Dispositivo dispositivoClickeado = (Dispositivo) disposRegistradosAdapter.getItem(position);
                Toast.makeText(getApplicationContext(), "Agregando socket:\n " + dispositivoClickeado.getNombre(), Toast.LENGTH_SHORT).show();
                //view.setSelected(true);
                Intent intent = new Intent(getApplicationContext(), AddDispositivoActivity.class);
                intent.putExtra("canal_clickeado", dispositivoClickeado);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        Boolean fragmentoSeleccionado = false;
        switch (id) {
            case R.id.nav_camera:
                if (getSupportFragmentManager().getFragments() != null
                        && getSupportFragmentManager().getFragments().size() > 0
                        && getSupportFragmentManager().getFragments().get(0) != null
                        ) {
                    Toast.makeText(this, "fragmesnt: " + getSupportFragmentManager().getFragments().size(), Toast.LENGTH_SHORT).show();
                    Log.i("tostring", "" + getSupportFragmentManager().getFragments().toString());
                    Log.i("size", "" + getSupportFragmentManager().getFragments().size());
                    Log.i("toarray", "" + getSupportFragmentManager().getFragments().get(0));
                    getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().getFragments().get(0)).commit();
                }
                break;
            case R.id.nav_gallery:
                fragment = new VerdeFragment();
                fragmentoSeleccionado = true;
                break;
            case R.id.nav_slideshow:
                fragment = new DispositivoNoRegFragment();
                fragmentoSeleccionado = true;
                break;
            case R.id.nav_manage:
                fragment = new AzulFragment();
                fragmentoSeleccionado = true;
                break;
            case R.id.nav_share:
                break;
            case R.id.nav_send:
                break;
            default:

        }

        if (fragmentoSeleccionado) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager().popBackStackImmediate();
            fragmentTransaction.replace(R.id.contenedor, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
