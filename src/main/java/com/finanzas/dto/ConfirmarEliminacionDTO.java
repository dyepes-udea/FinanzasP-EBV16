package com.finanzas.dto;

public class ConfirmarEliminacionDTO {

    private boolean confirmar;

    private String razon;

    private Long reasignarCategoriaId;

    private boolean eliminarTransacciones;

    public ConfirmarEliminacionDTO() {}

    public ConfirmarEliminacionDTO(boolean confirmar) {
        this.confirmar = confirmar;
    }

    public ConfirmarEliminacionDTO(boolean confirmar, String razon) {
        this.confirmar = confirmar;
        this.razon = razon;
    }

    public ConfirmarEliminacionDTO(boolean confirmar, Long reasignarCategoriaId, boolean eliminarTransacciones) {
        this.confirmar = confirmar;
        this.reasignarCategoriaId = reasignarCategoriaId;
        this.eliminarTransacciones = eliminarTransacciones;
    }

    public boolean isConfirmar() { return confirmar; }
    public void setConfirmar(boolean confirmar) { this.confirmar = confirmar; }

    public String getRazon() { return razon; }
    public void setRazon(String razon) { this.razon = razon; }

    public Long getReasignarCategoriaId() { return reasignarCategoriaId; }
    public void setReasignarCategoriaId(Long reasignarCategoriaId) { this.reasignarCategoriaId = reasignarCategoriaId; }

    public boolean isEliminarTransacciones() { return eliminarTransacciones; }
    public void setEliminarTransacciones(boolean eliminarTransacciones) { this.eliminarTransacciones = eliminarTransacciones; }
}
