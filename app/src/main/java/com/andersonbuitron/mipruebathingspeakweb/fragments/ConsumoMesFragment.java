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
import android.widget.TextView;

import com.andersonbuitron.mipruebathingspeakweb.R;
import com.andersonbuitron.mipruebathingspeakweb.activities.GraficaFullScreenActivity;
import com.andersonbuitron.mipruebathingspeakweb.activities.MisDispositivosActivity;
import com.andersonbuitron.mipruebathingspeakweb.callbacks.TareaList;
import com.andersonbuitron.mipruebathingspeakweb.extras.GraficaBarrras;
import com.andersonbuitron.mipruebathingspeakweb.gestores.GestorDispositivos;
import com.andersonbuitron.mipruebathingspeakweb.modelos.Dispositivo;
import com.andersonbuitron.mipruebathingspeakweb.modelos.FeedField;
import com.github.mikephil.charting.charts.BarChart;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.andersonbuitron.mipruebathingspeakweb.gestores.GestorFeedField.media;
import static com.andersonbuitron.mipruebathingspeakweb.gestores.GestorFeedField.obtenerListaValorLabel;


public class ConsumoMesFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_DISPOSITIVO = "dispositivo_seleccionado";
    private static final String ARG_FECHA = "fecha";
    private static final String ARG_POSICION = "posicion";


    //no estaticas
    private Dispositivo dispositivo;
    private Calendar fechaActual;
    private int posicion;
    private static final String ESCALA_TIEMPO_MIN = "720"; /*en minutos*/
    BarChart mChart;
    float limiteConsumoDiario = 0;
    private ArrayList<FeedField> listaFeedField;
    //boton fullscreen para la grafica
    Button btn_fullScreenGrafico;
    GraficaBarrras gbarras;

    public ConsumoMesFragment() {
        // Required empty public constructor
    }

    public static ConsumoMesFragment newInstance(Dispositivo param1, Calendar fecha,int position) {
        ConsumoMesFragment fragment = new ConsumoMesFragment();
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
            fechaActual = (Calendar) getArguments().getSerializable(ARG_FECHA);
            posicion = getArguments().getInt(ARG_POSICION);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_consumo_mes, container, false);

        ((TextView)root.findViewById(R.id.tv_consumo)).setText(""+posicion);
        solicitarDatos(fechaActual, root);
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
    // grafico configuracion grafico de barras

    public void solicitarDatos(Calendar fechaCal, final View v) {
        //-----
        int anio = fechaCal.get(Calendar.YEAR);
        int mes = fechaCal.get(Calendar.MONTH);
        int dia = fechaCal.get(Calendar.DAY_OF_MONTH);

        Calendar calendar = Calendar.getInstance();
        calendar.set(anio, mes - 1, 0);
        Date finicial = calendar.getTime();

        calendar.set(anio, mes, dia + 1);
        Date ffinal = calendar.getTime();

        listaFeedField = new ArrayList();
        TareaList nuevaTarea = new TareaList() {
            @Override
            public void ejecutar(ArrayList<FeedField> listaFF) {
                listaFeedField = listaFF;
                if (listaFeedField == null) {
                    listaFeedField = new ArrayList<>();
                }
                //Toast.makeText(getApplicationContext(), "listaFeedField: "+listaFeedField.toString(), Toast.LENGTH_LONG).show();
                limiteConsumoDiario = (float) calcularLimiteCondumoDiario(listaFeedField);
                mChart = (BarChart) v.findViewById(R.id.barchart);
                List<GraficaBarrras.ValorLabel> listavalores = obtenerListaValorLabel(listaFeedField);

                String mes = "mes: " + obtenerNombreMes(listaFeedField);

                gbarras = new GraficaBarrras( limiteConsumoDiario, listavalores, mes,1);
                gbarras.crearGrafica(mChart,GraficaBarrras.ESTILO_UN_COLOR_CON_LINEA_DELIMITADORA,false);
            }

            @Override
            public List getList() {
                return listaFeedField;
            }
        };

        GestorDispositivos gestionDis = GestorDispositivos.getInstance(getActivity());
        gestionDis.solicitarValoresDeField(dispositivo.getApi_key_write(), GestorDispositivos.VALUE_FIELD_NUMBER, dispositivo.getId(), finicial, ffinal, ESCALA_TIEMPO_MIN, nuevaTarea);

    }

    public String obtenerNombreMes(ArrayList<FeedField> listaFF) {
        DateFormat df = new SimpleDateFormat("MMMM");

        if (listaFF.size() > 0) {
            return df.format(listaFF.get(0).getFecha());
        }
        return "-";
    }

    public double calcularLimiteCondumoDiario(ArrayList<FeedField> listaFF) {

        if (listaFF == null) {
            return 0;
        }
        return media(listaFF);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(getActivity(), MisDispositivosActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    // end grafico
}
