package com.finanzas.dto;

import com.finanzas.entity.TipoCategoria;
import java.time.LocalDateTime;

public class CategoriaResponseDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private TipoCategoria tipo;
    private LocalDateTime fechaCreacion;

    public CategoriaResponseDTO() {}

    public CategoriaResponseDTO(Long id, String nombre, String descripcion, TipoCategoria tipo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipo = tipo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public TipoCategoria getTipo() { return tipo; }
    public void setTipo(TipoCategoria tipo) { this.tipo = tipo; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
