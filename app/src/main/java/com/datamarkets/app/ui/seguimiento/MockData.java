package com.datamarkets.app.ui.seguimiento;

import com.datamarkets.app.model.Activo;
import java.util.ArrayList;
import java.util.List;

public class MockData {

    public static List<Activo> getActivosFavoritos() {

        List<Activo> lista = new ArrayList<>();

        // Bitcoin
        Activo btc = new Activo();
        btc.setId("bitcoin");
        btc.setSimbolo("BTC");
        btc.setNombre("Bitcoin");
        btc.setPrecioActual(60886.00);
        btc.setVariacion24h(-0.84);
        btc.setCambio24h(-518.56);
        btc.setMaximo24h(61542.00);
        btc.setMinimo24h(59928.00);
        lista.add(btc);

        // Ethereum
        Activo eth = new Activo();
        eth.setId("ethereum");
        eth.setSimbolo("ETH");
        eth.setNombre("Ethereum");
        eth.setPrecioActual(1849.10);
        eth.setVariacion24h(-2.86);
        eth.setCambio24h(-54.43);
        eth.setMaximo24h(1903.53);
        eth.setMinimo24h(1826.67);
        lista.add(eth);

        // Apple
        Activo aapl = new Activo();
        aapl.setId("AAPL");
        aapl.setSimbolo("AAPL");
        aapl.setNombre("Apple Inc.");
        aapl.setPrecioActual(248.96);
        aapl.setVariacion24h(-0.39);
        aapl.setCambio24h(-0.98);
        aapl.setMaximo24h(251.83);
        aapl.setMinimo24h(247.30);
        lista.add(aapl);

        // Inditex
        Activo itx = new Activo();
        itx.setId("ITX");
        itx.setSimbolo("ITX");
        itx.setNombre("Inditex");
        itx.setPrecioActual(47.50);
        itx.setVariacion24h(1.24);
        itx.setCambio24h(0.58);
        itx.setMaximo24h(48.10);
        itx.setMinimo24h(46.90);
        lista.add(itx);

        // Oro
        Activo oro = new Activo();
        oro.setId("XAU");
        oro.setSimbolo("XAU");
        oro.setNombre("Oro");
        oro.setPrecioActual(3327.40);
        oro.setVariacion24h(0.62);
        oro.setCambio24h(20.45);
        oro.setMaximo24h(3341.20);
        oro.setMinimo24h(3305.80);
        lista.add(oro);

        return lista;
    }
}