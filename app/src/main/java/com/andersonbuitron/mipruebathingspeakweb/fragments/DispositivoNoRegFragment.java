package com.andersonbuitron.mipruebathingspeakweb.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.andersonbuitron.mipruebathingspeakweb.activities.AddDispositivoActivity;
import com.andersonbuitron.mipruebathingspeakweb.R;
import com.andersonbuitron.mipruebathingspeakweb.adaptadores.DispositivoNoRegAdapter;
import com.andersonbuitron.mipruebathingspeakweb.gestores.GestorDispositivos;
import com.andersonbuitron.mipruebathingspeakweb.modelos.Dispositivo;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DispositivoNoRegFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DispositivoNoRegFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DispositivoNoRegFragment extends Fragment {

    //ista de elementos
    ListView vCanalesList;
    ArrayAdapter mCanalesAdapter;
    List<Dispositivo> list_canales;

    private OnFragmentInteractionListener mListener;

    public DispositivoNoRegFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DispositivoNoRegFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DispositivoNoRegFragment newInstance() {
        DispositivoNoRegFragment fragment = new DispositivoNoRegFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Dispositivos disponibles");
        list_canales = new ArrayList<>();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("Informe","entro en onCreateView");
        View root = inflater.inflate(R.layout.fragment_dispositivos_no_reg, container, false);

        //instancia del listView
        vCanalesList = (ListView) root.findViewById(R.id.canales_list);

        //inicializa el adaptador con la fuente de datos
        mCanalesAdapter = new DispositivoNoRegAdapter(getActivity(),list_canales);
        GestorDispositivos.getInstance(getContext()).obtenerDispositivosEn(mCanalesAdapter);

        //relacionando la lista con el adaptador
        vCanalesList.setAdapter(mCanalesAdapter);
        //setear una escucha a las clicks de los item

        vCanalesList.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Dispositivo dispositivoClickeado = (Dispositivo) mCanalesAdapter.getItem(position);
                Toast.makeText(getActivity(), "Agregando socket:\n "+ dispositivoClickeado.getNombre(), Toast.LENGTH_SHORT).show();
                //view.setSelected(true);
                Intent intent = new Intent(getActivity(),AddDispositivoActivity.class);
                intent.putExtra("canal_clickeado", dispositivoClickeado);
                startActivity(intent);
            }
        });

        return root;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }



}
