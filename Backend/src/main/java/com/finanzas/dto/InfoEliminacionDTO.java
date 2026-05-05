package com.finanzas.dto;

public class InfoEliminacionDTO {

    private Long categoriaId;
    private String categoriaNombre;
    private Long gastosVinculados;
    private Long ingresosVinculados;
    private boolean puedeEliminar;
    private String mensaje;

    public InfoEliminacionDTO() {}

    public InfoEliminacionDTO(Long categoriaId, String categoriaNombre, 
                             Long gastosVinculados, Long ingresosVinculados) {
        this.categoriaId = categoriaId;
        this.categoriaNombre = categoriaNombre;
        this.gastosVinculados = gastosVinculados;
        this.ingresosVinculados = ingresosVinculados;
        this.puedeEliminar = gastosVinculados == 0 && ingresosVinculados == 0;
        
        if (puedeEliminar) {
            this.mensaje = "La categoría puede ser eliminada sin problemas";
        } else {
            this.mensaje = "La categoría tiene " + (gastosVinculados + ingresosVinculados) + 
                          " transacciones vinculadas. Considere reasignarlas antes de eliminar.";
        }
    }

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }

    public String getCategoriaNombre() { return categoriaNombre; }
    public void setCategoriaNombre(String categoriaNombre) { this.categoriaNombre = categoriaNombre; }

    public Long getGastosVinculados() { return gastosVinculados; }
    public void setGastosVinculados(Long gastosVinculados) { this.gastosVinculados = gastosVinculados; }

    public Long getIngresosVinculados() { return ingresosVinculados; }
    public void setIngresosVinculados(Long ingresosVinculados) { this.ingresosVinculados = ingresosVinculados; }

    public boolean isPuedeEliminar() { return puedeEliminar; }
    public void setPuedeEliminar(boolean puedeEliminar) { this.puedeEliminar = puedeEliminar; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
