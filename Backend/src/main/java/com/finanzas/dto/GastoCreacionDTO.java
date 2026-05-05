package com.finanzas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public class GastoCreacionDTO {
    
    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;
    
    @NotNull(message = "El ID de categoría es obligatorio")
    private Long categoriaId;
    
    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser positivo")
    private Double monto;
    
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    public GastoCreacionDTO() {}

    public GastoCreacionDTO(String descripcion, Long categoriaId, Double monto, LocalDate fecha) {
        this.descripcion = descripcion;
        this.categoriaId = categoriaId;
        this.monto = monto;
        this.fecha = fecha;
    }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }

    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
}
