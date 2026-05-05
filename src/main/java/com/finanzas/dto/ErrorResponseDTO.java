package com.finanzas.dto;

public class ErrorResponseDTO {

    private int codigo;
    private String mensaje;
    private String detalles;

    public ErrorResponseDTO() {}

    public ErrorResponseDTO(int codigo, String mensaje) {
        this.codigo = codigo;
        this.mensaje = mensaje;
    }

    public ErrorResponseDTO(int codigo, String mensaje, String detalles) {
        this.codigo = codigo;
        this.mensaje = mensaje;
        this.detalles = detalles;
    }

    public int getCodigo() { return codigo; }
    public void setCodigo(int codigo) { this.codigo = codigo; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public String getDetalles() { return detalles; }
    public void setDetalles(String detalles) { this.detalles = detalles; }
}
