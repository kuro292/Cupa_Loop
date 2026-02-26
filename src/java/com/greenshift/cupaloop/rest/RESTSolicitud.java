package com.greenshift.cupaloop.rest;

import com.google.gson.Gson;
import com.greenshift.cupaloop.controller.ControllerSolicitud;
import com.greenshift.cupaloop.model.Solicitud;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("admin/solicitud")
public class RESTSolicitud {
    
    @GET 
    @Path("getPendientes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPendientes() {
        String out = null;
        List<Solicitud> solicitudes = null;
        ControllerSolicitud cs = new ControllerSolicitud();
        Gson gson = new Gson();
        
        try {
            solicitudes = cs.getPendientes();
            out = gson.toJson(solicitudes);
        } 
        catch (Exception e) {
            e.printStackTrace();
            out = "{\"exception\" : \"" + e.toString() + "\"}";
        }
        return Response.ok(out).build();
    }
    
    @GET 
    @Path("getByEstado")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByEstado(@QueryParam("estado") @DefaultValue("PENDIENTE") String estado) {
        String out = null;
        List<Solicitud> solicitudes = null;
        ControllerSolicitud cs = new ControllerSolicitud();
        Gson gson = new Gson();
        
        try {
            solicitudes = cs.getAll(estado);
            out = gson.toJson(solicitudes);
        } 
        catch (Exception e) {
            e.printStackTrace();
            out = "{\"exception\" : \"" + e.toString() + "\"}";
        }
        return Response.ok(out).build();
    }
    
    @GET 
    @Path("getByUsuario")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByUsuario(@QueryParam("idUsuario") @DefaultValue("0") int idUsuario) {
        String out = null;
        List<Solicitud> solicitudes = null;
        ControllerSolicitud cs = new ControllerSolicitud();
        Gson gson = new Gson();
        
        try {
            if(idUsuario < 1) {
                out = "{\"error\":\"ID de usuario no valido.\"}";
            } else {
                solicitudes = cs.getByUsuario(idUsuario);
                out = gson.toJson(solicitudes);
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
            out = "{\"exception\" : \"" + e.toString() + "\"}";
        }
        return Response.ok(out).build();
    }
    
    @POST 
    @Path("create")
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@FormParam("id_producto") @DefaultValue("0") int idProducto,
                          @FormParam("id_usuario") @DefaultValue("0") int idUsuario,
                          @FormParam("motivo") @DefaultValue("") String motivo) {
        String out = null;
        ControllerSolicitud cs = new ControllerSolicitud();
        Gson gson = new Gson();
        Solicitud sol = null;
        
        try {
            if(idProducto < 1 || idUsuario < 1) {
                out = "{\"error\":\"Parametros invalidos.\"}";
            } else if(motivo.isEmpty()) {
                out = "{\"error\":\"El motivo es requerido.\"}";
            } else {
                sol = new Solicitud();
                sol.setId_producto(idProducto);
                sol.setId_usuario(idUsuario);
                sol.setMotivo(motivo);
                cs.insert(sol);
                out = new Gson().toJson(sol);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            out = "{\"exception\" : \"" + e.toString() + "\"}";
        }
        return Response.ok(out).build();
    }
    
    @POST 
    @Path("update")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@FormParam("id") @DefaultValue("0") int id,
                          @FormParam("estado") @DefaultValue("PENDIENTE") String estado,
                          @FormParam("comentario") @DefaultValue("") String comentario) {
        String out = null;
        ControllerSolicitud cs = new ControllerSolicitud();
        
        try {
            if(id < 1) {
                out = "{\"error\":\"ID de solicitud no valido.\"}";
            } else {
                Solicitud sol = new Solicitud();
                sol.setId(id);
                sol.setEstado_solicitud(estado);
                sol.setComentario(comentario);
                cs.update(sol);
                out = "{\"result\" : \"OK\"}";
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            out = "{\"exception\" : \"" + e.toString() + "\"}";
        }
        return Response.ok(out).build();
    }
}
