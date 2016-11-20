package com.andersonbuitron.mipruebathingspeakweb.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andersonbuitron.mipruebathingspeakweb.R;
import com.andersonbuitron.mipruebathingspeakweb.activities.ItemDetailActivity;
import com.andersonbuitron.mipruebathingspeakweb.gestores.GestorDispositivos;
import com.andersonbuitron.mipruebathingspeakweb.modelos.Dispositivo;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VerdeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VerdeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VerdeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private boolean mTwoPane;

    private OnFragmentInteractionListener mListener;

    public VerdeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VerdeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VerdeFragment newInstance(String param1, String param2) {
        VerdeFragment fragment = new VerdeFragment();
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_verde, container, false);

        View recyclerView = root.findViewById(R.id.item_list);
        Log.i("onCreateView", "ittemlist: " + recyclerView);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (root.findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
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

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        //Toast.makeText(getActivity(), "recycler: "+recyclerView.toString(), Toast.LENGTH_SHORT).show();
        Log.i("recyclerView", String.valueOf(recyclerView));
        List<Dispositivo> dispositivos_list = GestorDispositivos.getInstance(getContext()).recuperarDispositivosBaseDatos();
        RecyclerView.Adapter adapter = new SimpleItemRecyclerViewAdapter(dispositivos_list);
        recyclerView.setAdapter(adapter);

    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Dispositivo> mValues;

        public SimpleItemRecyclerViewAdapter(List<Dispositivo> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.template_list_item_dispositivos, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mNombreView.setText(mValues.get(position).getNombre());
            holder.mConsumoView.setText(mValues.get(position).getApi_key_write());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        /*Bundle arguments = new Bundle();
                        arguments.putString(ItemDetailFragment.ARG_ITEM_DISPOSITIVO, holder.mItem.getId());
                        ItemDetailFragment fragment = new ItemDetailFragment();
                        fragment.setArguments(arguments);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();*/
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ItemDetailActivity.class);
                        intent.putExtra(ItemDetailFragment.ARG_ITEM_DISPOSITIVO, holder.mItem.getId());

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mNombreView;
            public final TextView mConsumoView;
            public Dispositivo mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mNombreView = (TextView) view.findViewById(R.id.tv_reg_nombre);
                mConsumoView = (TextView) view.findViewById(R.id.tv_reg_consumo);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mConsumoView.getText() + "'";
            }
        }
    }
}
