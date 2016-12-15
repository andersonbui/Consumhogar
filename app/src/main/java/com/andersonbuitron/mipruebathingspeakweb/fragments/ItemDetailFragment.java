package com.andersonbuitron.mipruebathingspeakweb.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.andersonbuitron.mipruebathingspeakweb.R;
import com.andersonbuitron.mipruebathingspeakweb.extras.ModoDetalle;
import com.andersonbuitron.mipruebathingspeakweb.modelos.Dispositivo;

import java.util.Calendar;

import static com.andersonbuitron.mipruebathingspeakweb.activities.DetalleDispositivoActivity.ARG_ITEM_DISPOSITIVO;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link //ItemListActivity}
 * in two-pane mode (on tablets) or a {@link //DetalleDispositivoActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    Button btn_get_consumo_diario;
    Button btn_get_consumo_mensual;

    // region manipulacion de FragmentStatePager
    SlidePagerAdapter mAdapter;
    ViewPager mPager;
    Calendar fechaDetalle;
    private static final int num_paginas = 24;
    int posicionAnteriorAux;// Servira para guardar el mes mostrado en grafico, util para trasladarse al siguiente o el anterior
    Context context;

    /**
     * The dummy content this fragment is presenting.
     */
    private Dispositivo unDispositivo;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {

    }

    public static ItemDetailFragment newInstance(Dispositivo param1) {
        ItemDetailFragment fragment = new ItemDetailFragment();
        Bundle args = new Bundle();
        Log.i( "Dispositivo llegado","dispositivo llegado: "+param1.getId());
        args.putSerializable(ARG_ITEM_DISPOSITIVO, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();

        if (getArguments().containsKey(ARG_ITEM_DISPOSITIVO)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            unDispositivo = (Dispositivo) getArguments().getSerializable(ARG_ITEM_DISPOSITIVO);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item_detail, container, false);
        //Log.i("oncreate detalle: ","--"+itemDispositivo.toString());
        Activity activity = this.getActivity();
        //CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.toolbar_layout);
        String nombre = unDispositivo.getNombre();
        //appBarLayout.setTitle(nombre);
        activity.setTitle(nombre);
        Toast.makeText(activity, "Asignando el titulo", Toast.LENGTH_SHORT).show();

        // Show the dummy content as text in a TextView.
        if (unDispositivo != null) {
            //((TextView) rootView.findViewById(R.id.item_detail)).setText(itemDispositivo.getApi_key_write());
            // ((TextView) rootView.findViewById(R.id.id_detail)).setText(itemDispositivo.getId());

            // Establecer la fecha final del mes actual y defnir el numero de paginas
            fechaDetalle = Calendar.getInstance();

            //num_paginas = ges
            posicionAnteriorAux =  0;

            mAdapter = new SlidePagerAdapter(getFragmentManager(), unDispositivo);

            mPager = (ViewPager) rootView.findViewById(R.id.pager);
            mPager.setAdapter(mAdapter);

            mPager.setCurrentItem(num_paginas - 1);

            // Watch for button avanza derecha.
            Button button = (Button) rootView.findViewById(R.id.btn_flecha_izquierda);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mPager.setCurrentItem(mPager.getCurrentItem() - 1);
                }
            });

            // Watch for button avanza izquierda.
            button = (Button) rootView.findViewById(R.id.btn_flecha_derecha);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                }
            });

            // comportamiento boton para ver el ultimo pager
            button = (Button) rootView.findViewById(R.id.goto_last);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mPager.setCurrentItem(num_paginas - 1);
                }
            });
        }

        return rootView;
    }


    public  class SlidePagerAdapter extends FragmentStatePagerAdapter {

        //Dispositivo unDispositivo;

        public SlidePagerAdapter(FragmentManager fm, Dispositivo dispositivo) {
            super(fm);
            //this.unDispositivo = barraGraf;
        }

        @Override
        public Fragment getItem(int position) {
            //Log.i("Llamando a getItem", "Llamando a getItem()");
            int posicionDerIzq = num_paginas-1-position;
            int sentido = posicionAnteriorAux - posicionDerIzq  ;
            posicionAnteriorAux = posicionDerIzq;
            fechaDetalle.add(ModoDetalle.modo,sentido);

            //Log.i("Calendar","posicion["+(posicionDerIzq)+"]"+" fecha ["+ fechaDetalle.getTime().toString()+"]");
            //Toast.makeText(DetalleDispositivoActivity.this, "item: "+mPager.getCurrentItem(), Toast.LENGTH_SHORT).show();
            return ConsumoMesFragment.newInstance(unDispositivo, (Calendar) fechaDetalle.clone(),posicionDerIzq);

        }

        @Override
        public int getCount() {
            //Log.i("Llamando a cound", "Llamando a getCound()");
            return num_paginas;
        }

    }

    // endregion
}
