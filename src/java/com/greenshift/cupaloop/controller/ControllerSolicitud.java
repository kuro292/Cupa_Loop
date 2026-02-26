package com.greenshift.cupaloop.controller;

import com.greenshift.cupaloop.bd.ConexionMySQL;
import com.greenshift.cupaloop.model.Solicitud;
import java.util.List;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

public class ControllerSolicitud
{
    public int insert(Solicitud s) throws Exception
    {
        String sql = "INSERT INTO solicitudes (id_producto, id_usuario, motivo, estado_solicitud) VALUES (?, ?, ?, 'PENDIENTE')";
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        ResultSet rs = null;

        pstmt.setInt(1, s.getId_producto());
        pstmt.setInt(2, s.getId_usuario());
        pstmt.setString(3, s.getMotivo());

        pstmt.executeUpdate();

        rs = pstmt.getGeneratedKeys();
        if (rs.next())
            s.setId(rs.getInt(1));

        rs.close();
        pstmt.close();
        conn.close();

        return s.getId();
    }

    public void update(Solicitud s) throws Exception
    {
        String sql = "UPDATE solicitudes SET estado_solicitud = ?, fecha_resolucion = NOW(), comentario = ? WHERE id = ?";
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        PreparedStatement pstmt = conn.prepareStatement(sql);

        pstmt.setString(1, s.getEstado_solicitud());
        pstmt.setString(2, s.getComentario());
        pstmt.setInt(3, s.getId());

        pstmt.executeUpdate();

        pstmt.close();
        conn.close();
    }

    public List<Solicitud> getAll(String estado) throws Exception
    {
        String sql = "SELECT s.*, u.nombre, u.identificador, p.nombre as producto_nombre FROM solicitudes s " +
                     "JOIN usuarios u ON s.id_usuario = u.id " +
                     "JOIN productos p ON s.id_producto = p.id " +
                     "WHERE s.estado_solicitud = ? " +
                     "ORDER BY s.fecha_solicitud DESC";
        
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, estado);
        ResultSet rs = pstmt.executeQuery();
        List<Solicitud> solicitudes = new ArrayList<>();

        while(rs.next())
            solicitudes.add(fill(rs));

        rs.close();
        pstmt.close();
        conn.close();

        return solicitudes;
    }

    public List<Solicitud> getPendientes() throws Exception
    {
        return getAll("PENDIENTE");
    }

    public List<Solicitud> getByUsuario(int idUsuario) throws Exception
    {
        String sql = "SELECT s.*, u.nombre, u.identificador, p.nombre as producto_nombre FROM solicitudes s " +
                     "JOIN usuarios u ON s.id_usuario = u.id " +
                     "JOIN productos p ON s.id_producto = p.id " +
                     "WHERE s.id_usuario = ? " +
                     "ORDER BY s.fecha_solicitud DESC";
        
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, idUsuario);
        ResultSet rs = pstmt.executeQuery();
        List<Solicitud> solicitudes = new ArrayList<>();

        while(rs.next())
            solicitudes.add(fill(rs));

        rs.close();
        pstmt.close();
        conn.close();

        return solicitudes;
    }

    private Solicitud fill(ResultSet rs) throws Exception
    {
        Solicitud s = new Solicitud();
        s.setId(rs.getInt("id"));
        s.setId_producto(rs.getInt("id_producto"));
        s.setId_usuario(rs.getInt("id_usuario"));
        s.setEstado_solicitud(rs.getString("estado_solicitud"));
        s.setFecha(rs.getString("fecha_solicitud"));
        s.setMotivo(rs.getString("motivo"));
        s.setComentario(rs.getString("comentario"));
        // Datos adicionales del usuario y producto
        s.setNombre(rs.getString("nombre"));
        s.setIdentificador(rs.getString("identificador"));
        s.setProducto_nombre(rs.getString("producto_nombre"));
        return s;
    }
}
