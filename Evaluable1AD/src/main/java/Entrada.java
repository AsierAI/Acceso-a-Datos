import database.GestionDB;
import database.ImportadorProductos;

import java.sql.Connection;
import java.util.Scanner;

public class Entrada {
    public static void main(String[] args) {

        Connection connection = GestionDB.getConnection();
        Scanner scanner = new Scanner(System.in);

        //GestionDB.createDatabase();   // solo la primera vez para crear la DB, (cambiar conection a localhost). Alternativa a crear la BD manualmente

        while (true) {
            mostrarMenu();
            int opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    ImportadorProductos.importarProductos("https://dummyjson.com/products", connection);
                    break;
                case 2:
                    System.out.println("Ingrese nombre del empleado:");
                    String nombreEmpleado = scanner.nextLine();
                    System.out.println("Ingrese apellidos del empleado:");
                    String apellidosEmpleado = scanner.nextLine();
                    System.out.println("Ingrese correo del empleado:");
                    String correoEmpleado = scanner.nextLine();
                    GestionDB.agregarEmpleado(connection, nombreEmpleado, apellidosEmpleado, correoEmpleado);
                    break;
                case 3:
                    System.out.println("Ingrese ID del producto para el pedido:");
                    int idProdPedido = scanner.nextInt();
                    System.out.println("Ingrese cantidad del producto para el pedido:");
                    int cantidadPedido = scanner.nextInt();
                    GestionDB.agregarPedido(connection, idProdPedido, cantidadPedido);
                    break;
                case 4:
                    GestionDB.mostrarTodosLosProductos(connection);
                    break;
                case 5:
                    GestionDB.mostrarTodosLosEmpleados(connection);
                    break;
                case 6:
                    GestionDB.mostrarTodosLosPedidos(connection);
                    break;
                case 7:
                    System.out.println("Ingrese el precio máximo:");
                    double precioLimite = scanner.nextDouble();
                    GestionDB.mostrarProductosConPrecioInferior(connection, precioLimite);
                    break;
                case 8:
                    System.out.println("Se guardarán en favoritos los productos con precio superior al indicado, Ingresa precio:");
                    int valorLimite = scanner.nextInt();
                    GestionDB.insertarProductosFavoritos(connection, valorLimite);
                    break;
                case 9:
                    System.out.println("Saliendo del programa.");
                    System.exit(0);
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        }

    }

    private static void mostrarMenu() {
        System.out.println("\nMenú:");
        System.out.println("1. Importar productos de dummyjson.com");
        System.out.println("2. Agregar Empleado");
        System.out.println("3. Agregar Pedido");
        System.out.println("4. Mostrar Todos los Productos");
        System.out.println("5. Mostrar Todos los Empleados");
        System.out.println("6. Mostrar Todos los Pedidos");
        System.out.println("7. Mostrar Productos con Precio Inferior a");
        System.out.println("8. Insertar Productos por Encima de un Precio en Favoritos");
        System.out.println("9. Salir");
        System.out.println("Seleccione una opción:");
    }

}

