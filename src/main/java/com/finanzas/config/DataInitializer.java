package com.finanzas.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Inicializador reservado para datos generales del sistema.
 * Las categorias y fuentes iniciales se crean por usuario durante el registro.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) {
        // Sin datos globales gestionables por usuario.
    }
}
