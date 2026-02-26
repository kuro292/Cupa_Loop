/*
    Artifact:   ControllerUsuario.java

    Version:    1.0
    Date:       2026-02-25 19:00:00
    Author:     Claudia Estefania Contreras Portugal
    Email:      88014@alumnos.utloen.edu.mx
    Comments:   Esta clase controla el acceso al sistema verificando que el 
                usuario y la contraseña sean correctos en la base de datos. 
                También se encarga de dar de alta a nuevos alumnos, revisando 
                primero que no estén registrados previamente
*/

package com.greenshift.cupaloop.controller;

import com.greenshift.cupaloop.bd.ConexionMySQL;
import com.greenshift.cupaloop.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ControllerUsuario
{
    public Usuario validate(String identificador, String contrasenia) throws Exception {
        // Buscar usuario y validar contrasenia en texto plano
        String sql = "SELECT * FROM usuarios WHERE (identificador=? OR correo=?) AND contrasenia=?";
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = null;
        Usuario u = null;

        // Pasamos el identificador dos veces (para evaluar identificador o correo)
        pstmt.setString(1, identificador);
        pstmt.setString(2, identificador);
        pstmt.setString(3, contrasenia);  // Contrasenia en texto plano

        rs = pstmt.executeQuery();

        if (rs.next()) {
            u = new Usuario();
            u.setId(rs.getInt("id"));
            u.setIdentificador(rs.getString("identificador"));
            u.setCorreo(rs.getString("correo"));
            u.setRol(rs.getString("rol"));
            u.setNombre(rs.getString("nombre"));
            u.setApellido(rs.getString("apellido"));
        }

        rs.close();
        pstmt.close();
        conn.close();

        return u;
    }

    public int insert(Usuario u) throws Exception {
        // Verificar si el usuario ya existe
        String sqlCheck = "SELECT id FROM usuarios WHERE identificador=? OR correo=?";
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheck);
        
        pstmtCheck.setString(1, u.getIdentificador());
        pstmtCheck.setString(2, u.getCorreo());
        ResultSet rsCheck = pstmtCheck.executeQuery();
        
        if (rsCheck.next()) {
            rsCheck.close();
            pstmtCheck.close();
            conn.close();
            throw new Exception("El usuario ya existe");
        }
        
        rsCheck.close();
        pstmtCheck.close();
        
        // Insertar nuevo usuario
        String sql = "INSERT INTO usuarios (identificador, correo, contrasenia, nombre, apellido, rol) VALUES (?, ?, ?, ?, ?, 'ESTUDIANTE')";
        PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        
        pstmt.setString(1, u.getIdentificador());
        pstmt.setString(2, u.getCorreo());
        pstmt.setString(3, u.getPassword_hash());
        pstmt.setString(4, u.getNombre());
        pstmt.setString(5, u.getApellido());
        
        pstmt.executeUpdate();
        
        ResultSet rs = pstmt.getGeneratedKeys();
        if (rs.next()) {
            u.setId(rs.getInt(1));
        }
        
        rs.close();
        pstmt.close();
        conn.close();
        
        return u.getId();
    }
}
