package Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionDB {
    private static final String URL = "jdbc:sqlite:techlab.db";

    public static Connection obtenerConexion() {
        Connection conexion = null;
        try {
            conexion = DriverManager.getConnection(URL);
            crearTablasSiNoExisten(conexion);
        } catch (SQLException e) {
            System.out.println("❌ Error al conectar a la base de datos: " + e.getMessage());
        }
        return conexion;
    }

    private static void crearTablasSiNoExisten(Connection conexion) {
        String sqlProductos = "CREATE TABLE IF NOT EXISTS productos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "precio REAL NOT NULL, " +
                "stock INTEGER NOT NULL, " +
                "tipo_producto TEXT" +
                ");";

        String sqlPedidos = "CREATE TABLE IF NOT EXISTS pedidos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "cliente TEXT NOT NULL, " +
                "total REAL NOT NULL, " +
                "fecha TEXT DEFAULT CURRENT_TIMESTAMP" +
                ");";

        // Tabla intermedia para guardar los productos de cada pedido y sus cantidades
        String sqlDetalles = "CREATE TABLE IF NOT EXISTS detalles_pedido (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "pedido_id INTEGER, " +
                "producto_id INTEGER, " +
                "cantidad INTEGER NOT NULL, " +
                "subtotal REAL NOT NULL, " +
                "FOREIGN KEY(pedido_id) REFERENCES pedidos(id), " +
                "FOREIGN KEY(producto_id) REFERENCES productos(id)" +
                ");";

        try (Statement stmt = conexion.createStatement()) {
            stmt.execute(sqlProductos);
            stmt.execute(sqlPedidos);
            stmt.execute(sqlDetalles);
        } catch (SQLException e) {
            System.out.println("❌ Error al crear las tablas: " + e.getMessage());
        }
    }
}