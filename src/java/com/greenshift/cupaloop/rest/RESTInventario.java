package com.greenshift.cupaloop.rest;

import com.google.gson.Gson;
import com.greenshift.cupaloop.controller.ControllerInventario;
import com.greenshift.cupaloop.model.Producto;
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

@Path("admin/producto")
public class RESTInventario {
    @GET 
    @Path("getALL")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@QueryParam("filtro") @DefaultValue("") String filtro){
        String out = null;
        List<Producto> productos = null;
        ControllerInventario cp = new ControllerInventario();
        Gson gson = new Gson();
        
        try 
        {
            productos = cp.getAll(filtro);
            out = gson.toJson(productos);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            out = """
                  {
                  "exception" : "%s"
                  }
                  """;
            out = String.format(out, e.toString().replaceAll("\"", ""));
        }
        return Response.ok(out).build();
    }
    
    @GET 
    @Path("getAllAdmin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAdmin(@QueryParam("filtro") @DefaultValue("") String filtro){
        String out = null;
        List<Producto> productos = null;
        ControllerInventario cp = new ControllerInventario();
        Gson gson = new Gson();
        
        try 
        {
            productos = cp.getAllAdmin(filtro);
            out = gson.toJson(productos);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            out = """
                  {
                  "exception" : "%s"
                  }
                  """;
            out = String.format(out, e.toString().replaceAll("\"", ""));
        }
        return Response.ok(out).build();
    }
    
    @POST 
    @Path("save")
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(@FormParam("producto") @DefaultValue("") String datosProducto){
        String out = null;
        ControllerInventario cp = new ControllerInventario();
        Gson gson = new Gson();
        Producto p = null;
        try
        {
            p = gson.fromJson(datosProducto, Producto.class);
            if(p == null)
            {
                out = "{\"error\":\"No se proporcionaron datos del producto.\"}";
            }
            else
            {
                if(p.getId() == 0)
                {
                    cp.insert(p);
                    out = new Gson().toJson(p);
                }
                else
                {
                    cp.update(p);
                    out = new Gson().toJson(p);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            out = """
                  {
                  "exception" : "%s"
                  }
                  """;
            out = String.format(out, e.toString().replaceAll("\"", ""));
        }
        return Response.ok(out).build();
    }
    
    @POST 
    @Path("delete")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@FormParam("id") @DefaultValue("0") int id){
        String out = null;
        ControllerInventario cp = new ControllerInventario();
        Gson gson = new Gson();
        try{
            if(id < 1){
                out = "{\"error\":\"ID de producto no valido.\"}";
            }else{
                cp.delete(id);
                out = """
                      {"result" : "OK"}
                      """;
            }
        }
        catch(Exception e){
            e.printStackTrace();
            out = """
                  {
                  "exception" : "%s"
                  }
                  """;
            out = String.format(out, e.toString().replaceAll("\"", ""));
        }
        return Response.ok(out).build();
    }
    
    @POST 
    @Path("updateEstado")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateEstado(@FormParam("id") @DefaultValue("0") int id,
                                  @FormParam("estado_inventario") @DefaultValue("") String estado_inventario){
        String out = null;
        ControllerInventario cp = new ControllerInventario();
        Gson gson = new Gson();
        try{
            if(id < 1 || estado_inventario.isEmpty()){
                out = "{\"error\":\"Parametros invalidos. ID y/o estado requeridos.\"}";
            }else{
                cp.updateEstado(id, estado_inventario);
                out = """
                      {"result" : "OK", "mensaje" : "Producto actualizado correctamente"}
                      """;
            }
        }
        catch(Exception e){
            e.printStackTrace();
            out = """
                  {
                  "exception" : "%s"
                  }
                  """;
            out = String.format(out, e.toString().replaceAll("\"", ""));
        }
        return Response.ok(out).build();
    }
    
    @POST 
    @Path("updateCompleto")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCompleto(@FormParam("id") @DefaultValue("0") int id,
                                    @FormParam("descripcion") @DefaultValue("") String descripcion,
                                    @FormParam("condicion_fisica") @DefaultValue("") String condicion_fisica,
                                    @FormParam("estado_inventario") @DefaultValue("") String estado_inventario){
        String out = null;
        ControllerInventario cp = new ControllerInventario();
        Gson gson = new Gson();
        try{
            if(id < 1 || descripcion.isEmpty() || condicion_fisica.isEmpty() || estado_inventario.isEmpty()){
                out = "{\"error\":\"Parametros invalidos. Todos los campos son requeridos.\"}";
            }else{
                cp.updateCompleto(id, descripcion, condicion_fisica, estado_inventario);
                out = """
                      {"result" : "OK", "mensaje" : "Producto actualizado correctamente"}
                      """;
            }
        }
        catch(Exception e){
            e.printStackTrace();
            out = """
                  {
                  "exception" : "%s"
                  }
                  """;
            out = String.format(out, e.toString().replaceAll("\"", ""));
        }
        return Response.ok(out).build();
    }
    
}
