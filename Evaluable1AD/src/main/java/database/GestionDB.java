package database;

import java.sql.*;

public class GestionDB {
    static Connection connection;
    private static void createConnection(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            //String url = "jdbc:mysql://127.0.0.1:3306/almacen;
            //String url = String.format("jdbc:mysql://%s",SchemeDB.HOST) ;
            String url = String.format("jdbc:mysql://%s/%s",SchemeDB.HOST,SchemeDB.DB_NAME) ;
            connection = DriverManager.getConnection(url,"root","");
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static Connection getConnection(){
        if (connection == null){
            createConnection();
        }
        return connection;
    }

    public static void agregarEmpleado(Connection connection, String nombre, String apellidos, String correo) {
        String query = "INSERT INTO empleados (nombre, apellidos, correo) VALUES (?,?,?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, nombre);
            preparedStatement.setString(2, apellidos);
            preparedStatement.setString(3, correo);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void agregarPedido(Connection connection, int idProducto, int cantidad) {

        String descripcion;
        int precio;
        Statement statement = null;
        ResultSet resultSet= null;
        try {
            statement = connection.createStatement();

            String query1 = "SELECT id, descripcion, precio FROM productos WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query1);
            preparedStatement.setInt(1, idProducto);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            descripcion=resultSet.getString("descripcion");
            precio = resultSet.getInt("precio");

            String query2 = "INSERT INTO pedidos (id_producto, descripcion, cantidad, precio_total) VALUES (?,?,?,?)";
            preparedStatement = connection.prepareStatement(query2);
            preparedStatement.setInt(1, idProducto);
            preparedStatement.setString(2, descripcion);
            preparedStatement.setInt(3, cantidad);
            preparedStatement.setInt(4, (cantidad*precio));
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void mostrarTodosLosProductos(Connection connection) {
        String query = "SELECT * FROM productos";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            System.out.println("Todos los productos:");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nombre = resultSet.getString("nombre");
                String descripcion = resultSet.getString("descripcion");
                int cantidad = resultSet.getInt("cantidad");
                int precio = resultSet.getInt("precio");

                System.out.println("ID: " + id + ", Nombre: " + nombre + ", Descripción: " +
                        descripcion + ", Cantidad: " + cantidad + ", Precio: " + precio);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void mostrarTodosLosEmpleados(Connection connection) {
        String query = "SELECT * FROM empleados";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            System.out.println("Todos los empleados:");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nombre = resultSet.getString("nombre");
                String apellidos = resultSet.getString("apellidos");
                String correo = resultSet.getString("correo");

                System.out.println("ID: " + id + ", Nombre: " + nombre + ", Apellidos: " +
                        apellidos + ", Correo: " + correo );
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void mostrarTodosLosPedidos(Connection connection) {
        String query = "SELECT * FROM pedidos";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            System.out.println("Todos los pedidos:");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int idProducto = resultSet.getInt("id_producto");
                String descripcion = resultSet.getString("descripcion");
                int cantidad = resultSet.getInt("cantidad");
                int precioTotal = resultSet.getInt("precio_total");

                System.out.println("ID: " + id + ", id_producto: " + idProducto + ", Descripción producto: " +
                        descripcion + ", Cantidad: " + cantidad + ", PrecioTotal: " + precioTotal);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void mostrarProductosConPrecioInferior(Connection connection, double precioLimite) {
        String query = "SELECT * FROM productos WHERE precio < ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setDouble(1, precioLimite);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                System.out.println("Productos con precio inferior a " + precioLimite + "€:");
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String nombre = resultSet.getString("nombre");
                    String descripcion = resultSet.getString("descripcion");
                    int cantidad = resultSet.getInt("cantidad");
                    double precio = resultSet.getDouble("precio");

                    System.out.println("ID: " + id + ", Nombre: " + nombre + ", Descripción: " +
                            descripcion + ", Cantidad: " + cantidad + ", Precio: " + precio + "€");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void insertarProductosFavoritos(Connection connection, double precioLimite) {
        String selectQuery = "SELECT * FROM productos WHERE precio > ?";
        String insertQuery = "INSERT INTO productos_fav (id_producto) VALUES (?)";

        try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
             PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {

            selectStatement.setDouble(1, precioLimite);

            try (ResultSet resultSet = selectStatement.executeQuery()) {
                System.out.println("Productos con precio superior a " + precioLimite + "€:");
                while (resultSet.next()) {
                    int idProducto = resultSet.getInt("id");
                    System.out.println("ID Producto: " + idProducto);

                    // Insertar en la tabla productos_fav
                    insertStatement.setInt(1, idProducto);
                    insertStatement.executeUpdate();
                }
                System.out.println("Productos con precio superior a " + precioLimite + "€ han sido insertados en la tabla de favoritos");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createDatabase() {
        try (Statement statement = connection.createStatement()) {
            // Crear la base de datos
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS almacen");
            statement.executeUpdate("USE almacen");

            // Crear la tabla Productos con ID autoincremental
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Productos (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "nombre VARCHAR(255)," +
                    "descripcion VARCHAR(255)," +
                    "cantidad INT," +
                    "precio DOUBLE)");

            // Crear la tabla Empleados con ID autoincremental
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Empleados (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "nombre VARCHAR(255)," +
                    "apellidos VARCHAR(255)," +
                    "correo VARCHAR(255))");

            // Crear la tabla Pedidos con ID autoincremental
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Pedidos (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "id_producto INT," +
                    "descripcion VARCHAR(255)," +
                    "precio_total DOUBLE," +
                    "FOREIGN KEY (id_producto) REFERENCES Productos(id))");

            // Crear la tabla Productos_Fav con ID autoincremental
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Productos_Fav (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "id_producto INT," +
                    "FOREIGN KEY (id_producto) REFERENCES Productos(id))");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
