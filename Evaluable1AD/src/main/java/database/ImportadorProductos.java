package database;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class ImportadorProductos {
    public static void importarProductos(String jsonUrl, Connection connection) {
        BufferedReader reader = null;
        HttpURLConnection miconexion = null;
        try {
            URL url = new URL(jsonUrl);
            miconexion = (HttpURLConnection) url.openConnection();
            reader = new BufferedReader(new InputStreamReader(miconexion.getInputStream()));
            StringBuffer stringBuffer = new StringBuffer();
            String linea = null;
            while ((linea = reader.readLine()) != null) {
                stringBuffer.append(linea);
            }
            JSONObject response = new JSONObject(stringBuffer.toString());

            JSONArray jsonArray = (JSONArray) response.get("products");
            System.out.println(jsonArray);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject unProduct = jsonArray.getJSONObject(i);
                String nombre = unProduct.getString("title");
                String descripcion = unProduct.getString("description");
                int cantidad = unProduct.getInt("stock");
                int precio = unProduct.getInt("price");
                agregarABaseDatos(connection, nombre, descripcion, cantidad, precio);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (miconexion != null) {
                miconexion.disconnect();
            }
        }
    }

    private static void agregarABaseDatos(Connection connection, String nombre, String descripcion, int cantidad, int precio){
        String query = "INSERT INTO productos (nombre, descripcion, cantidad, precio) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,nombre);
            preparedStatement.setString(2,descripcion);
            preparedStatement.setInt(3,cantidad);
            preparedStatement.setInt(4,precio);
            preparedStatement.executeUpdate();
            preparedStatement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
