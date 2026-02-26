package com.greenshift.cupaloop.rest;

import com.google.gson.Gson;
import com.greenshift.cupaloop.controller.ControllerCategoria;
import com.greenshift.cupaloop.model.Categoria;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("admin/categoria")
public class RESTCategoria {
    
    @GET 
    @Path("getALL")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        String out = null;
        List<Categoria> categorias = null;
        ControllerCategoria cc = new ControllerCategoria();
        Gson gson = new Gson();
        
        try {
            categorias = cc.getAll();
            out = gson.toJson(categorias);
        } 
        catch (Exception e) {
            e.printStackTrace();
            out = "{\"exception\" : \"" + e.toString() + "\"}";
        }
        return Response.ok(out).build();
    }
    
    @POST 
    @Path("save")
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(@FormParam("categoria") @DefaultValue("") String datosCategoria) {
        String out = null;
        ControllerCategoria cc = new ControllerCategoria();
        Gson gson = new Gson();
        Categoria cat = null;
        
        try {
            cat = gson.fromJson(datosCategoria, Categoria.class);
            if(cat == null) {
                out = "{\"error\":\"No se proporcionaron datos de la categoria.\"}";
            } else {
                if(cat.getId() == 0) {
                    cc.insert(cat);
                    out = new Gson().toJson(cat);
                } else {
                    cc.update(cat);
                    out = new Gson().toJson(cat);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            out = "{\"exception\" : \"" + e.toString() + "\"}";
        }
        return Response.ok(out).build();
    }
    
    @POST 
    @Path("delete")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@FormParam("id") @DefaultValue("0") int id) {
        String out = null;
        ControllerCategoria cc = new ControllerCategoria();
        
        try {
            if(id < 1) {
                out = "{\"error\":\"ID de categoria no valido.\"}";
            } else {
                cc.delete(id);
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
