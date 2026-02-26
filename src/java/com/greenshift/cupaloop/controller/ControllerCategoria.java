package com.greenshift.cupaloop.controller;

import com.greenshift.cupaloop.bd.ConexionMySQL;
import com.greenshift.cupaloop.model.Categoria;
import java.util.List;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

public class ControllerCategoria
{
    public int insert(Categoria c) throws Exception
    {
        String sql = "INSERT INTO categorias (nombre, descripcion, activa) VALUES (?, ?, ?)";
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        ResultSet rs = null;

        pstmt.setString(1, c.getNombre());
        pstmt.setString(2, c.getDescripcion());
        pstmt.setString(3, c.getActiva());

        pstmt.executeUpdate();

        rs = pstmt.getGeneratedKeys();
        if (rs.next())
            c.setId(rs.getInt(1));

        rs.close();
        pstmt.close();
        conn.close();

        return c.getId();
    }

    public void update(Categoria c) throws Exception
    {
        String sql = "UPDATE categorias SET nombre = ?, descripcion = ?, activa = ? WHERE id = ?";
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        PreparedStatement pstmt = conn.prepareStatement(sql);

        pstmt.setString(1, c.getNombre());
        pstmt.setString(2, c.getDescripcion());
        pstmt.setString(3, c.getActiva());
        pstmt.setInt(4, c.getId());

        pstmt.executeUpdate();

        pstmt.close();
        conn.close();
    }

    public void delete(int id) throws Exception
    {
        String sql = "UPDATE categorias SET activa = '0' WHERE id = ?";
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        PreparedStatement pstmt = conn.prepareStatement(sql);

        pstmt.setInt(1, id);

        pstmt.executeUpdate();

        pstmt.close();
        conn.close();
    }

    public List<Categoria> getAll() throws Exception
    {
        String sql = "SELECT * FROM categorias WHERE activa = '1' ORDER BY nombre ASC";
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        List<Categoria> categorias = new ArrayList<>();

        while(rs.next())
            categorias.add(fill(rs));

        rs.close();
        pstmt.close();
        conn.close();

        return categorias;
    }

    private Categoria fill(ResultSet rs) throws Exception
    {
        Categoria c = new Categoria();
        c.setId(rs.getInt("id"));
        c.setNombre(rs.getString("nombre"));
        c.setDescripcion(rs.getString("descripcion"));
        c.setActiva(rs.getString("activa"));
        return c;
    }
}
