/*
    Artifact:   RESTUsuario.java

    Version:    1.0
    Date:       2026-02-25 19:00:00
    Author:     Claudia Estefania Contreras Portugal
    Email:      88014@alumnos.utloen.edu.mx
    Comments:   Esta clase contiene los servicios web (API REST) para que la 
                página web pueda enviar datos de inicio de sesión y registro, 
                conectando la interfaz de usuario con la lógica del servidor
*/
package com.greenshift.cupaloop.rest;

import com.google.gson.Gson;
import com.greenshift.cupaloop.controller.ControllerUsuario;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.greenshift.cupaloop.model.Usuario;
 
@Path("usuario")
public class RESTUsuario {
    
    @Path("login")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    // CAMBIO AQUÍ: Cambiamos "nombre" por "identificador" para que coincida con JS y la BD
    public Response login(@FormParam("identificador") @DefaultValue("") String identificador,
                          @FormParam("contrasenia") @DefaultValue("") String contrasenia)
    {
        String out = null;
        ControllerUsuario cu = new ControllerUsuario();
        Usuario u = null;

        try
        {
            // Pasamos la variable corregida al controlador
            u = cu.validate(identificador, contrasenia);
            
            if(u == null){
                out = """
                      {"error" : "Nombre de usuario o contraseña incorrectos."}
                      """;
            }else{
                out = new Gson().toJson(u);
            }
        }
        catch(Exception e){
            e.printStackTrace();
            out = """
                  {"exception" : "%s"}
                  """;
            out = String.format(out, e.toString());
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }
    
    @Path("register")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(@FormParam("identificador") @DefaultValue("") String identificador,
                            @FormParam("correo") @DefaultValue("") String correo,
                            @FormParam("nombre") @DefaultValue("") String nombre,
                            @FormParam("apellido") @DefaultValue("") String apellido,
                            @FormParam("contrasenia") @DefaultValue("") String contrasenia)
    {
        String out = null;
        ControllerUsuario cu = new ControllerUsuario();
        Usuario u = null;

        try
        {
            if(identificador.isEmpty() || correo.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || contrasenia.isEmpty()) {
                out = "{\"error\":\"Todos los campos son requeridos.\"}";
            } else {
                u = new Usuario();
                u.setIdentificador(identificador);
                u.setCorreo(correo);
                u.setNombre(nombre);
                u.setApellido(apellido);
                u.setPassword_hash(contrasenia);
                
                int idUsuario = cu.insert(u);
                u.setId(idUsuario);
                u.setRol("ESTUDIANTE");
                out = new Gson().toJson(u);
            }
        }
        catch(Exception e){
            e.printStackTrace();
            out = "{\"error\" : \"" + e.toString() + "\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }
}