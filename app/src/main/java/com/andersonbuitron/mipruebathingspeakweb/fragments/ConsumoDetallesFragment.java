package com.andersonbuitron.mipruebathingspeakweb.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.andersonbuitron.mipruebathingspeakweb.R;
import com.andersonbuitron.mipruebathingspeakweb.activities.GraficaFullScreenActivity;
import com.andersonbuitron.mipruebathingspeakweb.activities.ListDispositivosActivity;
import com.andersonbuitron.mipruebathingspeakweb.extras.GraficaBarrras;
import com.andersonbuitron.mipruebathingspeakweb.extras.ModoDetalle;
import com.andersonbuitron.mipruebathingspeakweb.gestores.GestorConsumo;
import com.andersonbuitron.mipruebathingspeakweb.modelos.Dispositivo;
import com.andersonbuitron.mipruebathingspeakweb.modelos.FeedField;
import com.github.mikephil.charting.charts.BarChart;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ConsumoDetallesFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_DISPOSITIVO = "dispositivo_seleccionado";
    private static final String ARG_FECHA = "fecha";
    private static final String ARG_POSICION = "posicion";


    //no estaticas
    private Dispositivo dispositivo;
    private Calendar fecha;
    private int posicion;
    BarChart mChart;
    private final ArrayList<FeedField> listaFeedField = new ArrayList<>();
    //boton fullscreen para la grafica
    //Button btn_fullScreenGrafico;
    GraficaBarrras gbarras;

    public ConsumoDetallesFragment() {
        // Required empty public constructor
    }

    public static ConsumoDetallesFragment newInstance(Dispositivo param1, Calendar fecha, int position) {
        ConsumoDetallesFragment fragment = new ConsumoDetallesFragment();
        Bundle args = new Bundle();
        Log.i( "Dispositivo llegado","dispositivo llegado: "+param1.getId());
        args.putSerializable(ARG_DISPOSITIVO, param1);
        args.putSerializable(ARG_FECHA, fecha);
        args.putInt(ARG_POSICION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dispositivo = (Dispositivo) getArguments().getSerializable(ARG_DISPOSITIVO);
            fecha = (Calendar) getArguments().getSerializable(ARG_FECHA);
            posicion = getArguments().getInt(ARG_POSICION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_consumo_mes, container, false);

        DateFormat df = new SimpleDateFormat(getFormatoTitulo());
        ((TextView)root.findViewById(R.id.tv_consumo)).setText(""+df.format(fecha.getTime()));

        mChart = (BarChart) root.findViewById(R.id.barchart);
        gbarras = new GraficaBarrras();

        GestorConsumo.Holder holder = new GestorConsumo.Holder();
        holder.pbar_consumoTotal = (ProgressBar) root.findViewById(R.id.pbar_consumoTotal);
        holder.Consumo_diferencia = (TextView) root.findViewById(R.id.tv_consumo_diferencia);
        holder.consumo_promedio = (TextView) root.findViewById(R.id.tv_consumo_promedio);
        holder.consumo_total = (TextView) root.findViewById(R.id.tv_consumo_total);

        GestorConsumo.getInstance(getContext()).rellenarGraficaBarras(listaFeedField, mChart, dispositivo, fecha, ModoDetalle.modo,gbarras,holder);

        Button btn_fullScreenGrafico = (Button)root.findViewById(R.id.btn_fullScreenGrafico);
        btn_fullScreenGrafico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GraficaFullScreenActivity.class);
                intent.putExtra(GraficaFullScreenActivity.EXTRA_GRAFICO,gbarras);
                startActivity(intent);
            }
        });
        return root;
    }

    public String getFormatoTitulo(){
        switch (ModoDetalle.modo){
            case Calendar.MONTH:
                return "MMMM' de 'yyyy";
            case Calendar.DAY_OF_MONTH:
                return "dd' de 'MMMM' de 'yyyy";
        }
        return "yyyy";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent intent = new Intent(getActivity(), ListDispositivosActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    // end grafico
}
