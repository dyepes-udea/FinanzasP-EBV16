package com.finanzas.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "fuentes_ingreso")
public class FuenteIngreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la fuente es obligatorio")
    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @Column(nullable = false)
    private String tipo = "INGRESO";

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private Usuario usuario;

    @Transient
    private Long usuarioId;

    public FuenteIngreso() {}

    public FuenteIngreso(String nombre) {
        this.nombre = nombre;
        this.tipo = "INGRESO";
    }

    public FuenteIngreso(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipo = "INGRESO";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public boolean isGlobal() { return usuario == null; }
}
