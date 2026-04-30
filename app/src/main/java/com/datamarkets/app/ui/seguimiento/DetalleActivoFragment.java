package com.datamarkets.app.ui.seguimiento;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.datamarkets.app.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DetalleActivoFragment extends Fragment {

    // Vistas del layout
    private TextView tvTituloBarra;
    private TextView tvSimbolo;
    private TextView tvNombre;
    private TextView tvPrecio;
    private TextView tvVariacion;
    private TextView tvMaximo;
    private TextView tvMinimo;
    private ImageButton btnVolver;

    private LineChart grafico;

    // Datos del activo recibidos como argumentos
    private String idExterno;
    private String simbolo;
    private String nombre;
    private float precioActual;
    private float variacion24h;
    private float cambio24h;
    private float maximo24h;
    private float minimo24h;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(
                R.layout.fragment_detalle_activo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Recoger los argumentos que llegan desde Seguimiento
        leerArgumentos();

        // 2. Obtener referencias a las vistas del layout
        obtenerReferencias(view);

        // 3. Pintar los datos en las vistas
        mostrarDatos();

        // 4. Configurar el botón de volver
        btnVolver.setOnClickListener(v ->
                Navigation.findNavController(v).popBackStack());
    }

    private void leerArgumentos() {
        Bundle args = getArguments();
        if (args != null) {
            idExterno    = args.getString("idExterno", "");
            simbolo      = args.getString("simbolo", "");
            nombre       = args.getString("nombre", "");
            precioActual = args.getFloat("precioActual", 0f);
            variacion24h = args.getFloat("variacion24h", 0f);
            cambio24h    = args.getFloat("cambio24h", 0f);
            maximo24h    = args.getFloat("maximo24h", 0f);
            minimo24h    = args.getFloat("minimo24h", 0f);
        }
    }

    private void obtenerReferencias(View view) {
        tvTituloBarra = view.findViewById(R.id.tvTituloBarra);
        tvSimbolo     = view.findViewById(R.id.tvSimbolo);
        tvNombre      = view.findViewById(R.id.tvNombre);
        tvPrecio      = view.findViewById(R.id.tvPrecio);
        tvVariacion   = view.findViewById(R.id.tvVariacion);
        tvMaximo      = view.findViewById(R.id.tvMaximo);
        tvMinimo      = view.findViewById(R.id.tvMinimo);
        btnVolver     = view.findViewById(R.id.btnVolver);
        grafico       = view.findViewById(R.id.grafico);
    }

    private void mostrarDatos() {
        // Título y datos básicos
        tvTituloBarra.setText(nombre);
        tvSimbolo.setText(simbolo.toUpperCase());
        tvNombre.setText(nombre);

        // Precio formateado con separador de miles y 2 decimales
        tvPrecio.setText(String.format("%,.2f €", precioActual));

        // Máximo y mínimo formateados
        tvMaximo.setText(String.format("%,.2f €", maximo24h));
        tvMinimo.setText(String.format("%,.2f €", minimo24h));

        // Variación con flecha y color según suba o baje
        if (variacion24h >= 0) {
            tvVariacion.setText(String.format(
                    "▲ +%.2f%% (+%,.2f €)", variacion24h, cambio24h));
            tvVariacion.setTextColor(
                    getResources().getColor(R.color.variacion_positiva, null));
        } else {
            tvVariacion.setText(String.format(
                    "▼ %.2f%% (%,.2f €)", variacion24h, cambio24h));
            tvVariacion.setTextColor(
                    getResources().getColor(R.color.variacion_negativa, null));
        }
        configurarGrafico();
    }
    private void configurarGrafico() {
        // Generar 7 puntos de datos simulando variación de precio
        List<Entry> puntos = generarDatosSimulados(7);

        // Crear el conjunto de datos con estilo
        LineDataSet dataSet = new LineDataSet(puntos, "Evolución 7 días");
        dataSet.setColor(getResources().getColor(R.color.azul_primario, null));
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleColor(
                getResources().getColor(R.color.azul_primario, null));
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextSize(0f);          // ocultar valores sobre los puntos
        dataSet.setDrawFilled(true);           // relleno bajo la línea
        dataSet.setFillColor(
                getResources().getColor(R.color.azul_primario, null));
        dataSet.setFillAlpha(40);              // transparencia del relleno
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);  // curva suave

        // Aplicar al gráfico
        LineData data = new LineData(dataSet);
        grafico.setData(data);

        // Configuración general del gráfico
        Description desc = new Description();
        desc.setText("");                      // ocultar descripción
        grafico.setDescription(desc);
        grafico.getLegend().setEnabled(false); // ocultar leyenda
        grafico.setTouchEnabled(true);
        grafico.setDragEnabled(true);
        grafico.setScaleEnabled(true);
        grafico.setPinchZoom(true);

        // Eje X (días)
        XAxis ejeX = grafico.getXAxis();
        ejeX.setPosition(XAxis.XAxisPosition.BOTTOM);
        ejeX.setDrawGridLines(false);
        ejeX.setGranularity(1f);
        ejeX.setValueFormatter(new ValueFormatter() {
            private final String[] dias = {
                    "-6d", "-5d", "-4d", "-3d", "-2d", "-1d", "Hoy"
            };
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < dias.length) {
                    return dias[index];
                }
                return "";
            }
        });

        // Eje Y derecho (oculto)
        grafico.getAxisRight().setEnabled(false);

        // Eje Y izquierdo
        YAxis ejeY = grafico.getAxisLeft();
        ejeY.setDrawGridLines(true);
        ejeY.setGridColor(getResources().getColor(R.color.gris_borde, null));

        // Animación al cargar
        grafico.animateX(800);

        // Refrescar
        grafico.invalidate();
    }

    private List<Entry> generarDatosSimulados(int dias) {
        List<Entry> puntos = new ArrayList<>();
        Random random = new Random();

        // El precio actual es el último punto (Hoy)
        // Los días anteriores varían entre -5% y +5% respecto al actual
        float precioBase = precioActual;

        for (int i = 0; i < dias - 1; i++) {
            float variacion = (random.nextFloat() - 0.5f) * 0.10f; // -5% a +5%
            float precioDia = precioBase * (1 + variacion);
            puntos.add(new Entry(i, precioDia));
        }

        // El último punto es el precio actual real
        puntos.add(new Entry(dias - 1, precioActual));

        return puntos;
    }
}