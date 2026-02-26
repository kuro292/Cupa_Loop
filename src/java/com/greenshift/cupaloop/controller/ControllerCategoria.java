/**
 * ============================================================================
 * Archivo:     ControllerCategoria.java
 * Proyecto:    CUPA LOOP - Sistema de Gestión de Residuos Electrónicos (RAEE)
 * Paquete:     com.greenshift.cupaloop.controller
 * ============================================================================
 *
 * Versión:     1.0
 * Fecha:       2025-02-16
 * Autor:       Robledo Negrete Juan Antonio
 * Email:       robledonegrete@gmail.com
 * Comentarios: Controlador CRUD para la entidad Categoria. Gestiona el acceso
 *              a la tabla [categorias] de la base de datos MySQL. Incluye
 *              operaciones de inserción, actualización, eliminación lógica
 *              y consulta de registros activos.
 */
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
    /**
     * Inserta una nueva categoría en la tabla [categorias].
     *
     * @param c     Objeto {@link Categoria} con los datos a registrar.
     *              Los campos nombre, descripcion y activa deben estar
     *              inicializados antes de llamar a este método.
     * @return      El ID autogenerado por la base de datos para el
     *              nuevo registro.
     * @throws Exception    Se lanza cuando ocurre un fallo en la comunicación
     *                      con la base de datos o en la ejecución de la sentencia SQL.
     */
    public int insert(Categoria c) throws Exception
    {
        String sql = "INSERT INTO categorias (nombre, descripcion, activa) VALUES (?, ?, ?)";
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();

        // RETURN_GENERATED_KEYS permite recuperar el ID autogenerado tras el INSERT
        PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        ResultSet rs = null;

        // Índices JDBC comienzan en 1 (no en 0)
        pstmt.setString(1, c.getNombre());
        pstmt.setString(2, c.getDescripcion());
        pstmt.setString(3, c.getActiva());

        pstmt.executeUpdate();

        // Se recupera el ID generado y se asigna al objeto para devolverlo al llamador
        rs = pstmt.getGeneratedKeys();
        if (rs.next())
            c.setId(rs.getInt(1));

        rs.close();
        pstmt.close();
        conn.close();

        return c.getId();
    }

    /**
     * Actualiza un registro existente en la tabla [categorias].
     *
     * @param c     Objeto {@link Categoria} con los datos actualizados.
     *              El campo id debe estar establecido para identificar
     *              el registro que se va a modificar.
     * @throws Exception    Se lanza cuando ocurre un fallo en la comunicación
     *                      con la base de datos o en la ejecución de la sentencia SQL.
     */
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

    /**
     * Realiza una eliminación lógica de una categoría.
     * El registro NO se borra físicamente de la base de datos;
     * se establece el campo activa = '0' para ocultarlo del sistema.
     *
     * @param id    Identificador único de la categoría a desactivar.
     * @throws Exception    Se lanza cuando ocurre un fallo en la comunicación
     *                      con la base de datos o en la ejecución de la sentencia SQL.
     */
    public void delete(int id) throws Exception
    {
        // Eliminación lógica: se marca como inactiva en lugar de borrar el registro
        String sql = "UPDATE categorias SET activa = '0' WHERE id = ?";
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        PreparedStatement pstmt = conn.prepareStatement(sql);

        pstmt.setInt(1, id);

        pstmt.executeUpdate();

        pstmt.close();
        conn.close();
    }

    /**
     * Recupera todas las categorías activas (activa = '1') ordenadas
     * alfabéticamente por nombre.
     *
     * @return  {@code List<Categoria>} con todas las categorías activas.
     *          Devuelve una lista vacía si no hay registros.
     * @throws Exception    Se lanza cuando ocurre un fallo en la comunicación
     *                      con la base de datos o en la ejecución de la sentencia SQL.
     */
    public List<Categoria> getAll() throws Exception
    {
        // Solo se devuelven categorías activas (activa = '1')
        String sql = "SELECT * FROM categorias WHERE activa = '1' ORDER BY nombre ASC";
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        List<Categoria> categorias = new ArrayList<>();

        // Por cada fila del resultado se genera un objeto Categoria mediante fill()
        while(rs.next())
            categorias.add(fill(rs));

        rs.close();
        pstmt.close();
        conn.close();

        return categorias;
    }

    /**
     * Método auxiliar privado que construye un objeto {@link Categoria}
     * a partir de la fila actual del {@link ResultSet}.
     * Se utiliza internamente por getAll() para mapear cada registro.
     *
     * @param rs    ResultSet posicionado en la fila que se desea mapear.
     * @return      Objeto {@link Categoria} con los datos del registro actual.
     * @throws Exception    Se lanza si ocurre un error al leer los datos del ResultSet.
     **/
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