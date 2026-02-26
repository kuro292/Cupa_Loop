package com.greenshift.cupaloop.model;

/**
 *
 * @author roble
 */
public class Usuario {
    private int id;
    private String identificador;
    private String correo;
    private String password_hash;
    private String rol;
    private String nombre;
    private String apellido;

    public Usuario() {
    }

    public Usuario(int id, String identificador, String correo, String password_hash, String rol, String nombre, String apellido) {
        this.id = id;
        this.identificador = identificador;
        this.correo = correo;
        this.password_hash = password_hash;
        this.rol = rol;
        this.nombre = nombre;
        this.apellido = apellido;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword_hash() {
        return password_hash;
    }

    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
}
