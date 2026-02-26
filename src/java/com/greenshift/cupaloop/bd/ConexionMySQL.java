package com.greenshift.cupaloop.bd;
import java.sql.Connection;
import java.sql.DriverManager;

public class ConexionMySQL
{
    Connection conn;
    
    public Connection open() throws Exception
    {
        // Definimos la ruta de conexión a MySQL para CUPA LOOP:
        String url = "jdbc:mysql://127.0.0.1:3306/cupa_loop";
        String usuario = "root";
        String password = "root";

        // Registramos el Driver de MySQL:
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Abrimos la conexión con MySQL:
        conn = DriverManager.getConnection(url, usuario, password);

        // Devolvemos la conexión:
        return conn;
    }
    
    public void close() throws Exception
    {
        //Revisamos si hay una conexion activa:
        if (conn != null)
            conn.close();
    }
}