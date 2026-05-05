package com.finanzas.entity;

public enum TipoCategoria {
    GASTO("Gasto"),
    INGRESO("Ingreso");

    private final String descripcion;

    TipoCategoria(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
