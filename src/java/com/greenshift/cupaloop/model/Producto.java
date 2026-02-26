package com.greenshift.cupaloop.model;

/**
 *
 * @author roble
 */
public class Producto {
    private int id;
    private int id_categoria;
    private String nombre;
    private String descripcion;
    private String condicion_fisica;
    private String estado_inventario;
    private String fecha_ingreso;
    private String fecha_salida;
    private String id_usuario_asignado;

    public Producto() {
    }

    public Producto(int id, int id_categoria, String nombre, String descripcion, String condicion_fisica, String estado_inventario, String fecha_ingreso, String fecha_salida, String id_usuario_asignado) {
        this.id = id;
        this.id_categoria = id_categoria;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.condicion_fisica = condicion_fisica;
        this.estado_inventario = estado_inventario;
        this.fecha_ingreso = fecha_ingreso;
        this.fecha_salida = fecha_salida;
        this.id_usuario_asignado = id_usuario_asignado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_categoria() {
        return id_categoria;
    }

    public void setId_categoria(int id_categoria) {
        this.id_categoria = id_categoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCondicion_fisica() {
        return condicion_fisica;
    }

    public void setCondicion_fisica(String condicion_fisica) {
        this.condicion_fisica = condicion_fisica;
    }

    public String getEstado_inventario() {
        return estado_inventario;
    }

    public void setEstado_inventario(String estado_inventario) {
        this.estado_inventario = estado_inventario;
    }

    public String getFecha_ingreso() {
        return fecha_ingreso;
    }

    public void setFecha_ingreso(String fecha_ingreso) {
        this.fecha_ingreso = fecha_ingreso;
    }

    public String getFecha_salida() {
        return fecha_salida;
    }

    public void setFecha_salida(String fecha_salida) {
        this.fecha_salida = fecha_salida;
    }

    public String getId_usuario_asignado() {
        return id_usuario_asignado;
    }

    public void setId_usuario_asignado(String id_usuario_asignado) {
        this.id_usuario_asignado = id_usuario_asignado;
    }
    
    
}
