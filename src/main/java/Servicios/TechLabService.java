package Servicios;

import Conexion.ConexionDB;
import Excepciones.StockInsuficienteException;
import Pedidos.LineaPedido;
import Pedidos.Pedido;
import Productos.Producto;
import Productos.ProductoEstandar;
import java.sql.*;
import java.util.Scanner;

public class TechLabService {

    // Constantes de colores ANSI para la consola
    public static final String RESET = "\u001B[0m";
    public static final String ROJO = "\u001B[31m";
    public static final String VERDE = "\u001B[32m";
    public static final String AMARILLO = "\u001B[33m";
    public static final String AZUL = "\u001B[34m";
    public static final String CYAN = "\u001B[36m";

    public void agregarProducto(Scanner scanner) {
        System.out.println(CYAN + "\n--- AGREGAR NUEVO PRODUCTO ---" + RESET);
        System.out.print("Ingrese nombre del producto: ");
        String nombre = scanner.nextLine();

        double precio = leerDouble(scanner, "Ingrese precio: ");
        int stock = leerEntero(scanner, "Ingrese stock inicial: ");

        String sql = "INSERT INTO productos (nombre, precio, stock, tipo_producto) VALUES (?, ?, ?, ?)";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement pstmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, nombre);
            pstmt.setDouble(2, precio);
            pstmt.setInt(3, stock);
            pstmt.setString(4, "Estándar");

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int idGenerado = rs.getInt(1);
                System.out.println(VERDE + "✔ ¡Producto agregado exitosamente a la BD con ID " + idGenerado + "!" + RESET);
            }

        } catch (SQLException e) {
            System.out.println(ROJO + "❌ Error al insertar el producto en la base de datos: " + e.getMessage() + RESET);
        }
    }

    public void listarProductos() {
        String sql = "SELECT * FROM productos";

        try (Connection conexion = ConexionDB.obtenerConexion();
             Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println(CYAN + "\n=================== LISTA DE PRODUCTOS ===================" + RESET);
            boolean hayProductos = false;

            while (rs.next()) {
                hayProductos = true;
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                double precio = rs.getDouble("precio");
                int stock = rs.getInt("stock");
                String tipo = rs.getString("tipo_producto");

                // AQUÍ USAMOS LAS CLASES DEL PAQUETE Productos
                Producto p = new ProductoEstandar(id, nombre, precio, stock, tipo);

                System.out.println(AZUL + "ID: " + p.getId() + RESET + " | [" + tipo + "] " + p.getNombre() + " | Precio: " + VERDE + "$" + p.getPrecio() + RESET + " | Stock: " + p.getStock());
            }

            if (!hayProductos) {
                System.out.println(AMARILLO + "No hay productos registrados en la base de datos." + RESET);
            }
            System.out.println(CYAN + "==========================================================" + RESET);

        } catch (SQLException e) {
            System.out.println(ROJO + "❌ Error al listar los productos: " + e.getMessage() + RESET);
        }
    }

    public void buscarOActualizarProducto(Scanner scanner) {
        int id = leerEntero(scanner, "Ingrese el ID del producto a buscar: ");
        String sqlSelect = "SELECT * FROM productos WHERE id = ?";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement pstmt = conexion.prepareStatement(sqlSelect)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String nombre = rs.getString("nombre");
                double precio = rs.getDouble("precio");
                int stock = rs.getInt("stock");
                String tipo = rs.getString("tipo_producto");

                System.out.println(VERDE + "✔ Encontrado -> " + RESET + "ID: " + id + " | [" + tipo + "] " + nombre + " | Precio: $" + precio + " | Stock: " + stock);

                System.out.print("¿Desea actualizar este producto? (s/n): ");
                String opcion = scanner.nextLine();

                if (opcion.equalsIgnoreCase("s")) {
                    System.out.print("Nuevo precio (dejar en blanco para no cambiar): ");
                    String nuevoPrecioStr = scanner.nextLine();
                    double nuevoPrecio = nuevoPrecioStr.isEmpty() ? precio : Double.parseDouble(nuevoPrecioStr);

                    System.out.print("Nuevo stock (dejar en blanco para no cambiar): ");
                    String nuevoStockStr = scanner.nextLine();
                    int nuevoStock = nuevoStockStr.isEmpty() ? stock : Integer.parseInt(nuevoStockStr);

                    String sqlUpdate = "UPDATE productos SET precio = ?, stock = ? WHERE id = ?";
                    try (PreparedStatement pstmtUpdate = conexion.prepareStatement(sqlUpdate)) {
                        pstmtUpdate.setDouble(1, nuevoPrecio);
                        pstmtUpdate.setInt(2, nuevoStock);
                        pstmtUpdate.setInt(3, id);
                        pstmtUpdate.executeUpdate();
                        System.out.println(VERDE + "✔ ¡Producto actualizado correctamente en la base de datos!" + RESET);
                    }
                }
            } else {
                System.out.println(AMARILLO + "❌ No se encontró ningún producto con el ID " + id + RESET);
            }

        } catch (SQLException e) {
            System.out.println(ROJO + "❌ Error al buscar/actualizar el producto: " + e.getMessage() + RESET);
        }
    }

    public void eliminarProducto(Scanner scanner) {
        int id = leerEntero(scanner, "Ingrese el ID del producto que desea eliminar: ");
        String sql = "DELETE FROM productos WHERE id = ?";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement pstmt = conexion.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println(VERDE + "✔ ¡Producto eliminado exitosamente de la base de datos!" + RESET);
            } else {
                System.out.println(AMARILLO + "❌ No se encontró ningún producto con el ID " + id + RESET);
            }

        } catch (SQLException e) {
            System.out.println(ROJO + "❌ Error al eliminar el producto: " + e.getMessage() + RESET);
        }
    }

    public void crearPedido(Scanner scanner) {
        System.out.println(CYAN + "\n--- CREAR NUEVO PEDIDO ---" + RESET);
        System.out.print("Ingrese el nombre del cliente: ");
        String cliente = scanner.nextLine();

        listarProductos();

        int idProducto = leerEntero(scanner, "Ingrese el ID del producto que desea comprar: ");
        int cantidadDeseada = leerEntero(scanner, "Ingrese la cantidad deseada: ");

        String sqlVerificarStock = "SELECT id, nombre, precio, stock, tipo_producto FROM productos WHERE id = ?";
        String sqlInsertarPedido = "INSERT INTO pedidos (cliente, total) VALUES (?, ?)";
        String sqlInsertarDetalle = "INSERT INTO detalles_pedido (pedido_id, producto_id, cantidad, subtotal) VALUES (?, ?, ?, ?)";
        String sqlActualizarStock = "UPDATE productos SET stock = stock - ? WHERE id = ?";

        try (Connection conexion = ConexionDB.obtenerConexion()) {
            conexion.setAutoCommit(false);

            try {
                PreparedStatement pstmtStock = conexion.prepareStatement(sqlVerificarStock);
                pstmtStock.setInt(1, idProducto);
                ResultSet rs = pstmtStock.executeQuery();

                if (!rs.next()) {
                    System.out.println(ROJO + "❌ El producto con ID " + idProducto + " no existe." + RESET);
                    conexion.rollback();
                    return;
                }

                // Mapeamos a un objeto Producto
                Producto productoObj = new ProductoEstandar(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getDouble("precio"),
                    rs.getInt("stock"),
                    rs.getString("tipo_producto")
                );

                // AQUÍ USAMOS LA EXCEPCIÓN PERSONALIZADA StockInsuficienteException
                if (productoObj.getStock() < cantidadDeseada) {
                    throw new StockInsuficienteException("Stock insuficiente. Disponible: " + productoObj.getStock());
                }

                System.out.println(CYAN + "📦 Producto seleccionado: " + productoObj.getNombre() + RESET);

                // AQUÍ USAMOS LineaPedido para calcular el subtotal
                LineaPedido linea = new LineaPedido(productoObj, cantidadDeseada);
                double totalPedido = linea.getSubtotal();

                PreparedStatement pstmtPedido = conexion.prepareStatement(sqlInsertarPedido, Statement.RETURN_GENERATED_KEYS);
                pstmtPedido.setString(1, cliente);
                pstmtPedido.setDouble(2, totalPedido);
                pstmtPedido.executeUpdate();

                ResultSet rsPedidoId = pstmtPedido.getGeneratedKeys();
                int pedidoId = 0;
                if (rsPedidoId.next()) {
                    pedidoId = rsPedidoId.getInt(1);
                }

                // AQUÍ USAMOS EL OBJETO Pedido
                java.util.List<LineaPedido> lineas = new java.util.ArrayList<>();
                lineas.add(linea);
                Pedido pedidoObj = new Pedido(pedidoId, cliente, lineas, totalPedido);

                PreparedStatement pstmtDetalle = conexion.prepareStatement(sqlInsertarDetalle);
                pstmtDetalle.setInt(1, pedidoObj.getId());
                pstmtDetalle.setInt(2, productoObj.getId());
                pstmtDetalle.setInt(3, cantidadDeseada);
                pstmtDetalle.setDouble(4, totalPedido);
                pstmtDetalle.executeUpdate();

                PreparedStatement pstmtUpdateStock = conexion.prepareStatement(sqlActualizarStock);
                pstmtUpdateStock.setInt(1, cantidadDeseada);
                pstmtUpdateStock.setInt(2, productoObj.getId());
                pstmtUpdateStock.executeUpdate();

                conexion.commit();
                System.out.println(VERDE + "✔ ¡Pedido #" + pedidoObj.getId() + " creado con éxito para " + pedidoObj.getCliente() + "! Total: $" + pedidoObj.getTotal() + RESET);

            } catch (StockInsuficienteException e) {
                conexion.rollback();
                System.out.println(AMARILLO + "⚠️ " + e.getMessage() + RESET);
            } catch (SQLException e) {
                conexion.rollback();
                System.out.println(ROJO + "❌ Error en la transacción del pedido: " + e.getMessage() + RESET);
            }

        } catch (SQLException e) {
            System.out.println(ROJO + "❌ Error de conexión: " + e.getMessage() + RESET);
        }
    }

    public void listarPedidos() {
        String sql = "SELECT p.id, p.cliente, p.total, p.fecha, pr.nombre, d.cantidad " +
                     "FROM pedidos p " +
                     "JOIN detalles_pedido d ON p.id = d.pedido_id " +
                     "JOIN productos pr ON d.producto_id = pr.id";

        try (Connection conexion = ConexionDB.obtenerConexion();
             Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println(CYAN + "\n=================== LISTA DE PEDIDOS ===================" + RESET);
            boolean hayPedidos = false;

            while (rs.next()) {
                hayPedidos = true;
                int id = rs.getInt("id");
                String cliente = rs.getString("cliente");
                double total = rs.getDouble("total");
                String fecha = rs.getString("fecha");
                String producto = rs.getString("nombre");
                int cantidad = rs.getInt("cantidad");

                System.out.printf(AZUL + "Pedido #%d" + RESET + " | Cliente: %s | Producto: %s (Cant: %d) | Total: " + VERDE + "$%.2f" + RESET + " | Fecha: %s\n",
                        id, cliente, producto, cantidad, total, fecha);
            }

            if (!hayPedidos) {
                System.out.println(AMARILLO + "No hay pedidos registrados todavía." + RESET);
            }
            System.out.println(CYAN + "========================================================" + RESET);

        } catch (SQLException e) {
            System.out.println(ROJO + "❌ Error al listar los pedidos: " + e.getMessage() + RESET);
        }
    }

    public void eliminarPedido(Scanner scanner) {
        System.out.println(CYAN + "\n--- ELIMINAR PEDIDO ---" + RESET);
        listarPedidos();

        int idPedido = leerEntero(scanner, "Ingrese el ID del pedido que desea eliminar: ");

        String sqlDeleteDetalles = "DELETE FROM detalles_pedido WHERE pedido_id = ?";
        String sqlDeletePedido = "DELETE FROM pedidos WHERE id = ?";

        try (Connection conexion = ConexionDB.obtenerConexion()) {
            conexion.setAutoCommit(false);

            try {
                PreparedStatement pstmtDetalles = conexion.prepareStatement(sqlDeleteDetalles);
                pstmtDetalles.setInt(1, idPedido);
                pstmtDetalles.executeUpdate();

                PreparedStatement pstmtPedido = conexion.prepareStatement(sqlDeletePedido);
                pstmtPedido.setInt(1, idPedido);
                int filasAfectadas = pstmtPedido.executeUpdate();

                if (filasAfectadas > 0) {
                    conexion.commit();
                    System.out.println(VERDE + "✔ ¡Pedido eliminado exitosamente!" + RESET);
                } else {
                    conexion.rollback();
                    System.out.println(AMARILLO + "❌ No se encontró ningún pedido con el ID " + idPedido + RESET);
                }

            } catch (SQLException e) {
                conexion.rollback();
                System.out.println(ROJO + "❌ Error al eliminar el pedido: " + e.getMessage() + RESET);
            }

        } catch (SQLException e) {
            System.out.println(ROJO + "❌ Error de conexión: " + e.getMessage() + RESET);
        }
    }

    private int leerEntero(Scanner scanner, String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println(AMARILLO + "⚠️ Entrada inválida. Ingrese un número entero." + RESET);
            }
        }
    }

    private double leerDouble(Scanner scanner, String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println(AMARILLO + "⚠️ Entrada inválida. Ingrese un número decimal válido." + RESET);
            }
        }
    }
}