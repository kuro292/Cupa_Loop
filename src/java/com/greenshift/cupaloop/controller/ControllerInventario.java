package com.greenshift.cupaloop.controller;

import com.greenshift.cupaloop.bd.ConexionMySQL;
import com.greenshift.cupaloop.model.Producto;
import com.greenshift.cupaloop.model.Categoria;
import java.util.List;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

public class ControllerInventario
{
  
    public int insert(Producto p) throws Exception
    {
        // Se define la consulta SQL:
        String sql = "INSERT INTO productos (id_categoria, nombre, descripcion, condicion_fisica, estado_inventario, fecha_ingreso, fecha_salida, id_usuario_asignado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        // Se crea un objeto de conexion con MySQL:
        ConexionMySQL connMySQL = new ConexionMySQL();
        
        // Se abre la conexion con MySQL:
        Connection conn = connMySQL.open();
        
        // Se genera un objeto para definir la consulta SQL y se indica que
        // se devolveran los ID's que se generen despues de ejecutarla:
        PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        
        // En este objeto se almacenaran los resultados de la consulta que,
        // en este caso, sera el ID que se genera al realizar la insercion
        // del registro:
        ResultSet rs = null;
        
        // Se llenan los valores de la sentencia SQL. 
        // Es importante recordar que el estandar JDBC es el unico caso especial
        // en el que cuyos indices comienzan en 1, en lugar de 0:
        pstmt.setInt(1, p.getId_categoria());
        pstmt.setString(2, p.getNombre());
        pstmt.setString(3, p.getDescripcion());
        pstmt.setString(4, p.getCondicion_fisica());
        pstmt.setString(5, p.getEstado_inventario());
        pstmt.setString(6, p.getFecha_ingreso());
        pstmt.setString(7, p.getFecha_salida());
        pstmt.setString(8, p.getId_usuario_asignado());
        
        // Se ejecuta la sentencia:
        pstmt.executeUpdate();
        
        // Se recupera el ID del Producto que se inserto:
        rs = pstmt.getGeneratedKeys();
        if (rs.next())
            p.setId(rs.getInt(1)); //Se asigna el ID al objeto de tipo Producto
        
        // Se cierran los objetos de BD:
        rs.close();
        pstmt.close();
        conn.close();
        
        // Se devuelve el ID que se genero:
        return p.getId();
    }
    
    /**
     * Actualiza un registro en la tabla [producto].
     * 
     * @param p Es un objeto de tipo Producto con todos los datos que van a 
     *          actualizarse.
     * @throws Exception    Se lanza una excepcion cuando ocurre un fallo en la
     *                      comunicacion con la Base de Datos o se altero de
     *                      forma erronea una sentencia SQL.
     */
    public void update(Producto p) throws Exception
    {
        // Se define la consulta SQL:
        String sql = "UPDATE productos SET id_categoria = ?, nombre = ?, descripcion = ?, condicion_fisica = ?, estado_inventario = ?, fecha_ingreso = ?, fecha_salida = ?, id_usuario_asignado = ? WHERE id = ?";
        
        // Se crea un objeto de conexion con MySQL:
        ConexionMySQL connMySQL = new ConexionMySQL();
        
        // Se abre la conexion con MySQL:
        Connection conn = connMySQL.open();
        
        // Se genera un objeto para definir la consulta SQL:
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        // Se llenan los valores de la sentencia SQL:
        pstmt.setInt(1, p.getId_categoria());
        pstmt.setString(2, p.getNombre());
        pstmt.setString(3, p.getDescripcion());
        pstmt.setString(4, p.getCondicion_fisica());
        pstmt.setString(5, p.getEstado_inventario());
        pstmt.setString(6, p.getFecha_ingreso());
        pstmt.setString(7, p.getFecha_salida());
        pstmt.setString(8, p.getId_usuario_asignado());
        
        // Se ejecuta la sentencia:
        pstmt.executeUpdate();
        
        // Se cierran los objetos de BD:
        pstmt.close();
        conn.close();
    }
    
    /**
     * Elimina de forma logica el registro de la tabla [producto] 
     * correspondiente al valor del identificador (ID) pasado como parametro.
     * 
     * @param id    El valor del ID del producto que desea eliminarse.
     * @throws Exception    Se lanza una excepcion cuando ocurre un fallo en la
     *                      comunicacion con la Base de Datos o se altero de
     *                      forma erronea una sentencia SQL.
     */
    public void delete(int id) throws Exception
    {
        // Se define la consulta SQL:
        String sql = "UPDATE productos SET estado_inventario='NO_DISPONIBLE' WHERE id=?";
        
        // Se crea un objeto de conexion con MySQL:
        ConexionMySQL connMySQL = new ConexionMySQL();
        
        // Se abre la conexion con MySQL:
        Connection conn = connMySQL.open();
        
        // Se genera un objeto para definir la consulta SQL:
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        // Se llenan los valores de la sentencia SQL:
        pstmt.setInt(1, id);
        
        // Se ejecuta la sentencia:
        pstmt.executeUpdate();
        
        // Se cierran los objetos de BD:
        pstmt.close();
        conn.close();
    }
    
    /**
     * Devuelve todos los registros de la tabla producto.
     * 
     * @param filtro    Un valor que sera buscado por coincidencia parcial
     *                  en todos los campos de la vista que contiene los
     *                  registros de productos.
     * @return          Devuelve una lista <code>List&lt;Producto&gt;</code>
     *                  que contiene todos los registros encontrados en la BD.
     * @throws Exception 
     */
    public List<Producto> getAll(String filtro) throws Exception
    {
        // Se define la consulta SQL que devuelve todos los productos
        // ADMIN puede ver todos sin importar estado, ALUMNO solo ve DISPONIBLES
        String sql = "SELECT * FROM productos WHERE (estado_inventario='DISPONIBLE' OR estado_inventario='RESERVADO')";
        
        // Si se proporciona un filtro, se agrega a la consulta
        if(filtro != null && !filtro.isEmpty()) {
            sql += " AND (nombre LIKE '%" + filtro + "%' OR descripcion LIKE '%" + filtro + "%')";
        }
        
        sql += " ORDER BY nombre ASC";
        
        // Se crea un objeto de conexion con MySQL:
        ConexionMySQL connMySQL = new ConexionMySQL();
        
        // Se abre la conexion con MySQL:
        Connection conn = connMySQL.open();
        
        // Se genera un objeto para definir la consulta SQL:
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        // Se ejecuta la consulta SQL y se almacena el resultado:
        ResultSet rs = pstmt.executeQuery();
        
        // En este objeto de tipo lista se agregara cada registro recuperado
        // de la BD:
        List<Producto> productos = new ArrayList<>();
        
        // Se itera sobre cada renglon (Row) del ResultSet:
        while(rs.next())
            productos.add(fill(rs)); //Por cada registro, se genera un nuevo objeto
        
        // Se cierran los objetos de BD:
        rs.close();
        pstmt.close();
        conn.close();
        
        // Se devuelve la lista con los productos recuperados de la BD.
        return productos;
    }
    
    public List<Producto> getAllAdmin(String filtro) throws Exception
    {
        // Para ADMIN: VER TODOS sin importar estado
        String sql = "SELECT * FROM productos WHERE 1=1";
        
        if(filtro != null && !filtro.isEmpty()) {
            sql += " AND (nombre LIKE '%" + filtro + "%' OR descripcion LIKE '%" + filtro + "%')";
        }
        
        sql += " ORDER BY nombre ASC";
        
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        List<Producto> productos = new ArrayList<>();
        
        while(rs.next())
            productos.add(fill(rs));
        
        rs.close();
        pstmt.close();
        conn.close();
        
        return productos;
    }
    
    /**
     * Actualiza solo el estado del inventario de un producto.
     * 
     * @param id                ID del producto a actualizar
     * @param estado_inventario Nuevo estado (DISPONIBLE, RESERVADO, ENTREGADO, RECICLADO)
     * @throws Exception        Se lanza una excepcion cuando ocurre un fallo
     */
    public void updateEstado(int id, String estado_inventario) throws Exception
    {
        // Se define la consulta SQL:
        String sql = "UPDATE productos SET estado_inventario = ? WHERE id = ?";
        
        // Se crea un objeto de conexion con MySQL:
        ConexionMySQL connMySQL = new ConexionMySQL();
        
        // Se abre la conexion con MySQL:
        Connection conn = connMySQL.open();
        
        // Se genera un objeto para definir la consulta SQL:
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        // Se llenan los valores de la sentencia SQL:
        pstmt.setString(1, estado_inventario);
        pstmt.setInt(2, id);
        
        // Se ejecuta la sentencia:
        pstmt.executeUpdate();
        
        // Se cierran los objetos de BD:
        pstmt.close();
        conn.close();
    }
    
    /**
     * Actualiza múltiples campos de un producto (descripción, condición y estado)
     * 
     * @param id                    ID del producto
     * @param descripcion           Nueva descripción
     * @param condicion_fisica      Nueva condición (EXCELENTE, BUENO, REGULAR, MALO)
     * @param estado_inventario     Nuevo estado (DISPONIBLE, RESERVADO, ENTREGADO, RECICLADO)
     * @throws Exception            Se lanza una excepcion cuando ocurre un fallo
     */
    public void updateCompleto(int id, String descripcion, String condicion_fisica, String estado_inventario) throws Exception
    {
        // Se define la consulta SQL:
        String sql = "UPDATE productos SET descripcion = ?, condicion_fisica = ?, estado_inventario = ? WHERE id = ?";
        
        // Se crea un objeto de conexion con MySQL:
        ConexionMySQL connMySQL = new ConexionMySQL();
        
        // Se abre la conexion con MySQL:
        Connection conn = connMySQL.open();
        
        // Se genera un objeto para definir la consulta SQL:
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        // Se llenan los valores de la sentencia SQL:
        pstmt.setString(1, descripcion);
        pstmt.setString(2, condicion_fisica);
        pstmt.setString(3, estado_inventario);
        pstmt.setInt(4, id);
        
        // Se ejecuta la sentencia:
        pstmt.executeUpdate();
        
        // Se cierran los objetos de BD:
        pstmt.close();
        conn.close();
    }
    
    /**
     * Este metodo genera un objeto de tipo <code>Producto<code> extrayendo 
     * los datos de la posicion en la que se encuentra el <i>cursor</i> del
     * <code>ResultSet<code> pasado como parametro.
     * @param rs
     * @return
     * @throws Exception 
     */
    private Producto fill(ResultSet rs) throws Exception
    {
        Producto p = new Producto();
        
        p.setId(rs.getInt("id"));
        p.setId_categoria(rs.getInt("id_categoria"));
        p.setNombre(rs.getString("nombre"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setCondicion_fisica(rs.getString("condicion_fisica"));
        p.setEstado_inventario(rs.getString("estado_inventario"));
        p.setFecha_ingreso(rs.getString("fecha_ingreso"));
        p.setFecha_salida(rs.getString("fecha_salida"));
        p.setId_usuario_asignado(rs.getString("id_usuario_asignado"));
        
        return p;
    }
}
