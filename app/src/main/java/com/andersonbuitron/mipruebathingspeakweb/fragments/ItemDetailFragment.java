package com.andersonbuitron.mipruebathingspeakweb.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.andersonbuitron.mipruebathingspeakweb.R;
import com.andersonbuitron.mipruebathingspeakweb.modelos.Dispositivo;


/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link //ItemListActivity}
 * in two-pane mode (on tablets) or a {@link //ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    Button btn_get_consumo_diario;
    Button btn_get_consumo_mensual;

    public static final String ARG_ITEM_DISPOSITIVO = "dispositivo_selected";

    /**
     * The dummy content this fragment is presenting.
     */
    private Dispositivo itemDispositivo;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_DISPOSITIVO)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            itemDispositivo = (Dispositivo) getArguments().getSerializable(ARG_ITEM_DISPOSITIVO);

        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item_detail, container, false);
        //Log.i("oncreate detalle: ","--"+itemDispositivo.toString());
        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.toolbar_layout);

        appBarLayout.setTitle(itemDispositivo.getNombre());
        Toast.makeText(activity, "Asignando el titulo", Toast.LENGTH_SHORT).show();
        // Show the dummy content as text in a TextView.
        if (itemDispositivo != null) {
            //((TextView) rootView.findViewById(R.id.item_detail)).setText(itemDispositivo.getApi_key_write());
            // ((TextView) rootView.findViewById(R.id.id_detail)).setText(itemDispositivo.getId());

        }

        return rootView;
    }


}
