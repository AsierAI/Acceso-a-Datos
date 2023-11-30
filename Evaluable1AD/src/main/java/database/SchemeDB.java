package database;

public interface SchemeDB {
    String HOST= "127.0.0.1:3306";
    String DB_NAME= "almacen";
    String TAB_PRODUCTOS= "productos";
    String COL_ID= "id";
    String COL_NAME= "nombre";
    String COL_PRICE= "precio";
    String COL_DESCRIPTION= "descripcion";
        String COL_STOCK= "cantidad";
}
