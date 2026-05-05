package com.finanzas.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.finanzas.entity.Categoria;
import com.finanzas.entity.FuenteIngreso;
import com.finanzas.entity.TipoCategoria;
import com.finanzas.repository.CategoriaRepository;
import com.finanzas.repository.FuenteIngresoRepository;

/**
 * Inicializador de datos predefinidos del sistema.
 * Crea categorías de gasto/ingreso y fuentes de ingreso en el startup.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private FuenteIngresoRepository fuenteRepository;

    @Override
    public void run(String... args) throws Exception {
        // Crear categorías predefinidas de GASTO si no existen
        crearCategoriasiNoExiste("Comida", "Gastos en alimentación y restaurantes", TipoCategoria.GASTO);
        crearCategoriasiNoExiste("Transporte", "Gastos de transporte y combustible", TipoCategoria.GASTO);
        crearCategoriasiNoExiste("Servicios", "Servicios básicos (agua, luz, internet)", TipoCategoria.GASTO);
        crearCategoriasiNoExiste("Salud", "Gastos médicos y de salud", TipoCategoria.GASTO);
        crearCategoriasiNoExiste("Entretenimiento", "Gastos de ocio y entretenimiento", TipoCategoria.GASTO);

        // Crear categorías predefinidas de INGRESO si no existen
        crearCategoriasiNoExiste("Salario", "Ingreso por salario laboral", TipoCategoria.INGRESO);
        crearCategoriasiNoExiste("Bonus", "Ingreso por bonificaciones", TipoCategoria.INGRESO);

        // Crear fuentes de ingreso predefinidas si no existen
        crearFuentesiNoExiste("Salario", "Ingreso por empleo regular");
        crearFuentesiNoExiste("Horas Extra", "Ingreso por trabajo adicional");
        crearFuentesiNoExiste("Comisiones", "Ingreso por comisiones de ventas");
        crearFuentesiNoExiste("Bonificaciones", "Ingreso por bonos y gratificaciones");
    }

    private void crearCategoriasiNoExiste(String nombre, String descripcion, TipoCategoria tipo) {
        if (categoriaRepository.findByNombre(nombre).isEmpty()) {
            Categoria cat = new Categoria(nombre, descripcion, tipo);
            categoriaRepository.save(cat);
        }
    }

    private void crearFuentesiNoExiste(String nombre, String descripcion) {
        if (fuenteRepository.findByNombre(nombre).isEmpty()) {
            FuenteIngreso fuente = new FuenteIngreso(nombre, descripcion);
            fuenteRepository.save(fuente);
        }
    }
}
