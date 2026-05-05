package com.finanzas.dto;

public class InfoEliminacionFuenteDTO {

    private Long fuenteId;
    private String fuenteNombre;
    private Long ingresosVinculados;
    private boolean puedeEliminar;
    private String mensaje;

    public InfoEliminacionFuenteDTO() {}

    public InfoEliminacionFuenteDTO(Long fuenteId, String fuenteNombre, Long ingresosVinculados) {
        this.fuenteId = fuenteId;
        this.fuenteNombre = fuenteNombre;
        this.ingresosVinculados = ingresosVinculados;
        this.puedeEliminar = ingresosVinculados == 0;
        
        if (puedeEliminar) {
            this.mensaje = "La fuente de ingreso puede ser eliminada sin problemas";
        } else {
            this.mensaje = "La fuente tiene " + ingresosVinculados + 
                          " ingresos vinculados. Considere reasignarlos antes de eliminar.";
        }
    }

    public Long getFuenteId() { return fuenteId; }
    public void setFuenteId(Long fuenteId) { this.fuenteId = fuenteId; }

    public String getFuenteNombre() { return fuenteNombre; }
    public void setFuenteNombre(String fuenteNombre) { this.fuenteNombre = fuenteNombre; }

    public Long getIngresosVinculados() { return ingresosVinculados; }
    public void setIngresosVinculados(Long ingresosVinculados) { this.ingresosVinculados = ingresosVinculados; }

    public boolean isPuedeEliminar() { return puedeEliminar; }
    public void setPuedeEliminar(boolean puedeEliminar) { this.puedeEliminar = puedeEliminar; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
