package com.greenshift.cupaloop.model;

/**
 *
 * @author roble
 */
public class Categoria {
    private int id;
    private String nombre;
    private String descripcion;
    private String activa;

    public Categoria() {
    }

    public Categoria(int id, String nombre, String descripcion, String activa) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activa = activa;
    }

    public String getActiva() {
        return activa;
    }

    public void setActiva(String activa) {
        this.activa = activa;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
