package Main;

import Servicios.TechLabService;
import java.util.Scanner;

public class Main {
    // Colores ANSI para la interfaz
    public static final String RESET = "\u001B[0m";
    public static final String CYAN = "\u001B[36m";
    public static final String AMARILLO = "\u001B[33m";
    public static final String VERDE = "\u001B[32m";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TechLabService service = new TechLabService();
        int opcion = 0;

        do {
            System.out.println(CYAN + "\n╔══════════════════════════════════════════════════════════════╗" + RESET);
            System.out.println(CYAN + "║               SISTEMA DE GESTIÓN - TECHLAB                   ║" + RESET);
            System.out.println(CYAN + "╠══════════════════════════════════════════════════════════════╣" + RESET);
            System.out.println(CYAN + "║" + RESET + "  1) Agregar producto                                         " + CYAN + "║" + RESET);
            System.out.println(CYAN + "║" + RESET + "  2) Listar productos                                         " + CYAN + "║" + RESET);
            System.out.println(CYAN + "║" + RESET + "  3) Buscar/Actualizar producto                               " + CYAN + "║" + RESET);
            System.out.println(CYAN + "║" + RESET + "  4) Eliminar producto                                        " + CYAN + "║" + RESET);
            System.out.println(CYAN + "║" + RESET + "  5) Crear un pedido                                          " + CYAN + "║" + RESET);
            System.out.println(CYAN + "║" + RESET + "  6) Listar pedidos                                           " + CYAN + "║" + RESET);
            System.out.println(CYAN + "║" + RESET + "  7) Eliminar pedido                                          " + CYAN + "║" + RESET);
            System.out.println(CYAN + "║" + RESET + "  8) Salir                                                    " + CYAN + "║" + RESET);
            System.out.println(CYAN + "╚══════════════════════════════════════════════════════════════╝" + RESET);
            System.out.print(AMARILLO + "👉 Elija una opción: " + RESET);

            try {
                opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1:
                        service.agregarProducto(scanner);
                        break;
                    case 2:
                        service.listarProductos();
                        break;
                    case 3:
                        service.buscarOActualizarProducto(scanner);
                        break;
                    case 4:
                        service.eliminarProducto(scanner);
                        break;
                    case 5:
                        service.crearPedido(scanner);
                        break;
                    case 6:
                        service.listarPedidos();
                        break;
                    case 7:
                        service.eliminarPedido(scanner);
                        break;
                    case 8:
                        System.out.println(VERDE + "\n¡Gracias por utilizar TechLab! Saliendo del sistema..." + RESET);
                        break;
                    default:
                        System.out.println(AMARILLO + "⚠️ Opción no válida. Por favor, elija entre 1 y 8." + RESET);
                }
            } catch (NumberFormatException e) {
                System.out.println(AMARILLO + "⚠️ Error: Debe ingresar un número válido correspondiente al menú." + RESET);
            }

        } while (opcion != 8);

        scanner.close();
    }
}