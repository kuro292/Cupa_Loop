package com.greenshift.cupaloop.model;

/**
 *
 * @author roble
 */
public class Solicitud {
    private int id;
    private int id_producto;
    private int id_usuario;
    private String estado_solicitud;
    private String fecha;
    private String motivo;          // Campo nuevo: motivo del alumno
    private String comentario;      // Comentario del administrador
    // Campos adicionales para mostrar información relacionada
    private String nombre;           // Nombre del usuario/alumno
    private String identificador;    // Identificador del usuario (matricula)
    private String producto_nombre;  // Nombre del producto
    
    public Solicitud() {
    }

    public Solicitud(int id, int id_producto, int id_usuario, String estado_solicitud, String fecha, String motivo, String comentario) {
        this.id = id;
        this.id_producto = id_producto;
        this.id_usuario = id_usuario;
        this.estado_solicitud = estado_solicitud;
        this.fecha = fecha;
        this.motivo = motivo;
        this.comentario = comentario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_producto() {
        return id_producto;
    }

    public void setId_producto(int id_producto) {
        this.id_producto = id_producto;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getEstado_solicitud() {
        return estado_solicitud;
    }

    public void setEstado_solicitud(String estado_solicitud) {
        this.estado_solicitud = estado_solicitud;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
    
    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getIdentificador() {
        return identificador;
    }
    
    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }
    
    public String getProducto_nombre() {
        return producto_nombre;
    }
    
    public void setProducto_nombre(String producto_nombre) {
        this.producto_nombre = producto_nombre;
    }
}
