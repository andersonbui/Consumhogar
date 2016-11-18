package com.andersonbuitron.mipruebathingspeakweb.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.andersonbuitron.mipruebathingspeakweb.R;
import com.andersonbuitron.mipruebathingspeakweb.fragments.CanalesFragment;

public class MainActivity extends AppCompatActivity implements CanalesFragment.OnFragmentInteractionListener{

    TextView tvTexto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Agregar fragmento
        FragmentManager fragmentManager = getSupportFragmentManager();
        CanalesFragment canalesFragment = (CanalesFragment) fragmentManager.findFragmentById(R.id.fragment_canales_list);
        if(canalesFragment == null){
            canalesFragment = CanalesFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contenedor,canalesFragment)
                    .commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
