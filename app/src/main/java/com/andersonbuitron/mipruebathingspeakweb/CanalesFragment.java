package com.andersonbuitron.mipruebathingspeakweb;

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

import com.andersonbuitron.mipruebathingspeakweb.adaptadores.CanalesAdapter;
import com.andersonbuitron.mipruebathingspeakweb.adaptadores.GestorCanales;
import com.andersonbuitron.mipruebathingspeakweb.modelos.Canal;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CanalesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CanalesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CanalesFragment extends Fragment {

    //ista de elementos
    ListView vCanalesList;
    ArrayAdapter mCanalesAdapter;
    List<Canal> list_canales;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public CanalesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CanalesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CanalesFragment newInstance(String param1, String param2) {
        CanalesFragment fragment = new CanalesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        getActivity().setTitle("Sockets disponibles");
        list_canales = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("Informe","entro en onCreateView");
        View root = inflater.inflate(R.layout.fragment_canales, container, false);

        //instancia del listView
        vCanalesList = (ListView) root.findViewById(R.id.canales_list);

        //inicializa el adaptador con la fuente de datos
        mCanalesAdapter = new CanalesAdapter(getActivity(),list_canales);
        GestorCanales.getInstance().recuperarCanales(mCanalesAdapter);

        //relacionando la lista con el adaptador
        vCanalesList.setAdapter(mCanalesAdapter);
        //setear una escucha a las clicks de los item

        vCanalesList.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Canal canalClickeado = (Canal) mCanalesAdapter.getItem(position);
                Toast.makeText(getActivity(), "Agregando socket:\n "+canalClickeado.getNombre(), Toast.LENGTH_SHORT).show();
                //view.setSelected(true);
                Intent intent = new Intent(getActivity(),AddSocketActivity.class);
                intent.putExtra("idCanal",canalClickeado.getId());
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
