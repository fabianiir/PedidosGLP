package jac.infosyst.proyectogas.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDBHelper extends SQLiteOpenHelper{

    private static final String TAG = "DatabaseHelper";

    private static final int DATABASE_VERSION = 1;
    protected static final String DATABASE_NAME = "proyectoGas";
    public static final String LOG_TAG_SQLITE_DB = "LOG_TAG_SQLITE_DB";

    //region Configuracion
    public static final String Config_Table = "configuracion";
    private static final String Config_Colum1 = "oid";
    private static final String Config_Colum2 = "ip";
    private static final String Config_Colum3 = "telefono";
    private static final String Config_Colum4 = "imei";
    //endregion

    //region Cat_Estatus
    public static final String CatEstatus_Table = "cat_estatus";
    private static final String CatEstatus_Colum1 = "oid";
    private static final String CatEstatus_Colum2 = "nombre";
    //endregion

    //region Cat_MotCanc
    public static final String CatMotCanc_Table = "cat_motivo_cancelacion";
    private static final String CatMotCanc_Colum1 = "oid";
    private static final String CatMotCanc_Colum2 = "nombre";
    //endregion

    //region Cat_Tpago
    public static final String CatTpago_Table = "cat_tipo_pago";
    private static final String CatTpago_Colum1 = "oid";
    private static final String CatTpago_Colum2 = "nombre";
    //endregion

    //region Cat_Productos
    public static final String CatProductos_Table = "cat_productos";
    private static final String CatProductos_Colum1 = "oid";
    private static final String CatProductos_Colum2 = "descripcion";
    private static final String CatProductos_Colum3 = "unidad";
    private static final String CatProductos_Colum4 = "precio_unitario";
    //endregion

    //region Usuario
    public static final String Usuario_Table = "usuario";
    private static final String Usuario_Colum1 = "oid";
    private static final String Usuario_Colum2 = "nombre";
    private static final String Usuario_Colum3 = "placas";
    private static final String Usuario_Colum4 = "foto";
    private static final String Usuario_Colum5 = "token";
    //endregion

    //region Productos
    public static final String Productos_Table = "productos";
    private static final String Productos_Colum1 = "oid";
    private static final String Productos_Colum2 = "cantidad";
    private static final String Productos_Colum3 = "surtido";
    private static final String Productos_Colum4 = "precio";
    private static final String Productos_Colum5 = "descripcion";
    private static final String Productos_Colum6 = "pedido";
    //endregion

    //region Productos_Mod
    public static final String Productos_Mod_Table = "productos_modificados";
    private static final String Productos_Mod_Colum1 = "oid";
    private static final String Productos_Mod_Colum2 = "cantidad";
    private static final String Productos_Mod_Colum3 = "surtido";
    private static final String Productos_Mod_Colum4 = "precio";
    private static final String Productos_Mod_Colum5 = "pedido_id";
    private static final String Productos_Mod_Colum6 = "producto_id";
    private static final String Productos_Mod_Colum7 = "tipo_modificacion";
    //endregion

    //region Pedidos
    public static final String Pedidos_Table = "pedidos";
    private static final String Pedidos_Colum1 = "oid";
    private static final String Pedidos_Colum2 = "fecha_hora_programada";
    private static final String Pedidos_Colum3 = "cliente";
    private static final String Pedidos_Colum4 = "direccion";
    private static final String Pedidos_Colum5 = "cp";
    private static final String Pedidos_Colum6 = "telefono";
    private static final String Pedidos_Colum7 = "lat";
    private static final String Pedidos_Colum8 = "lon";
    private static final String Pedidos_Colum9 = "comentario_cliente";
    private static final String Pedidos_Colum10 = "suma_iva";
    private static final String Pedidos_Colum11 = "total";
    private static final String Pedidos_Colum12 = "empresa";
    private static final String Pedidos_Colum13 = "tipo_pedido";
    private static final String Pedidos_Colum14 = "forma_pago";
    private static final String Pedidos_Colum15 = "estatus";
    //endregion

    //region Pedidos_Mod
    public static final String Pedidos_Mod_Table = "pedidos_modificados";
    private static final String Pedidos_Mod_Colum1 = "oid";
    private static final String Pedidos_Mod_Colum2 = "hora";
    private static final String Pedidos_Mod_Colum3 = "fecha";
    private static final String Pedidos_Mod_Colum4 = "comentario_chofer";
    private static final String Pedidos_Mod_Colum5 = "suma_iva";
    private static final String Pedidos_Mod_Colum6 = "total";
    private static final String Pedidos_Mod_Colum7 = "pago_id";
    private static final String Pedidos_Mod_Colum8 = "motivo_cancelacion_id";
    private static final String Pedidos_Mod_Colum9 = "estatus_id";
    private static final String Pedidos_Mod_Colum10 = "firma";
    private static final String Pedidos_Mod_Colum11 = "foto_fuga";
    private static final String Pedidos_Mod_Colum12 = "clave";
    //endregion

    //region Synchro
        String Synchro_Table = "synchro";
        String Synchro_Colum1 = "id";
        String Synchro_Colum2 = "fecha";
        String Synchro_Colum3 = "chofer";
    //endregion

    public SQLiteDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createConfig_Table = "CREATE TABLE " + Config_Table + " (" +
                Config_Colum1 + "TEXT PRIMARY KEY, " +
                Config_Colum2 + "TEXT, " +
                Config_Colum3 + "TEXT, " +
                Config_Colum4 + "TEXT" +
                ")";
        db.execSQL(createConfig_Table);

        String createCat_Estatus_Table = "CREATE TABLE " + CatEstatus_Table + " (" +
                CatEstatus_Colum1 + "TEXT PRIMARY KEY, " +
                CatEstatus_Colum2 + "TEXT" +
                ")";
        db.execSQL(createCat_Estatus_Table);

        String createCat_Motv_Table = "CREATE TABLE " + CatMotCanc_Table + " (" +
                CatMotCanc_Colum1 + "TEXT PRIMARY KEY, " +
                CatMotCanc_Colum2 + "TEXT" +
                ")";
        db.execSQL(createCat_Motv_Table);

        String createCat_Productos_Table = "CREATE TABLE " + CatProductos_Table + " (" +
                Config_Colum1 + "TEXT PRIMARY KEY, " +
                Config_Colum2 + "TEXT, " +
                Config_Colum3 + "TEXT, " +
                Config_Colum4 + "DOUBLE" +
                ")";
        db.execSQL(createCat_Productos_Table);

        String createCat_Tpago_Table = "CREATE TABLE " + CatTpago_Table + " (" +
                CatTpago_Colum1 + "TEXT PRIMARY KEY, " +
                CatTpago_Colum2 + "TEXT" +
                ")";
        db.execSQL(createCat_Tpago_Table);

        String createUsuario_Table = "CREATE TABLE " + Usuario_Table + " (" +
                Usuario_Colum1 + "TEXT PRIMARY KEY, " +
                Usuario_Colum2 + "TEXT, " +
                Usuario_Colum3 + "TEXT, " +
                Usuario_Colum4 + "TEXT, " +
                Usuario_Colum5 + "TEXT" +
                ")";
        db.execSQL(createUsuario_Table);

        String createProductos_Table = "CREATE TABLE " + Productos_Table + " (" +
                Productos_Colum1 + "TEXT PRIMARY KEY, " +
                Productos_Colum2 + "INTEGER, " +
                Productos_Colum3 + "BOOL, " +
                Productos_Colum4 + "DOUBLE, " +
                Productos_Colum5 + "TEXT, " +
                Productos_Colum6 + "TEXT" +
                ")";
        db.execSQL(createProductos_Table);

        String createProductos_Mod_Table = "CREATE TABLE " + Productos_Mod_Table + " (" +
                Productos_Mod_Colum1 + "TEXT PRIMARY KEY, " +
                Productos_Mod_Colum2 + "INTEGER, " +
                Productos_Mod_Colum3 + "BOOL, " +
                Productos_Mod_Colum4 + "DOUBLE, " +
                Productos_Mod_Colum5 + "TEXT, " +
                Productos_Mod_Colum6 + "TEXT, " +
                Productos_Mod_Colum7 + "TEXT" +
                ")";
        db.execSQL(createProductos_Mod_Table);

        String createPedidos_Table = "CREATE TABLE " + Pedidos_Table + " (" +
                Pedidos_Colum1 + "TEXT PRIMARY KEY, " +
                Pedidos_Colum2 + "DATETIME, " +
                Pedidos_Colum3 + "TEXT, " +
                Pedidos_Colum4 + "TEXT, " +
                Pedidos_Colum5 + "TEXT, " +
                Pedidos_Colum6 + "TEXT, " +
                Pedidos_Colum7 + "TEXT, " +
                Pedidos_Colum8 + "TEXT, " +
                Pedidos_Colum9 + "TEXT, " +
                Pedidos_Colum10 + "DOUBLE, " +
                Pedidos_Colum11 + "DOUBLE, " +
                Pedidos_Colum12 + "TEXT, " +
                Pedidos_Colum13 + "TEXT, " +
                Pedidos_Colum14 + "TEXT, " +
                Pedidos_Colum15 + "TEXT" +
                ")";
        db.execSQL(createPedidos_Table);

        String createPedidos_Mod_Table = "CREATE TABLE " + Pedidos_Mod_Table + " (" +
                Pedidos_Mod_Colum1 + "TEXT PRIMARY KEY, " +
                Pedidos_Mod_Colum2 + "TEXT, " +
                Pedidos_Mod_Colum3 + "TEXT, " +
                Pedidos_Mod_Colum4 + "TEXT, " +
                Pedidos_Mod_Colum5 + "DOUBLE, " +
                Pedidos_Mod_Colum6 + "DOUBLE, " +
                Pedidos_Mod_Colum7 + "TEXT, " +
                Pedidos_Mod_Colum8 + "TEXT, " +
                Pedidos_Mod_Colum9 + "TEXT, " +
                Pedidos_Mod_Colum10 + "TEXT, " +
                Pedidos_Mod_Colum11 + "TEXT, " +
                Pedidos_Mod_Colum12 + "TEXT" +
                ")";
        db.execSQL(createPedidos_Mod_Table);

        String synchro_Table = "CREATE TABLE " + Synchro_Table + " (" +
                Synchro_Colum1 + "TEXT PRIMARY KEY, " +
                Synchro_Colum2 + "DATE, " +
                Synchro_Colum3 + "TEXT " +
                ")";
        db.execSQL(synchro_Table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + Config_Table);
        db.execSQL("DROP TABLE " + CatEstatus_Table);
        db.execSQL("DROP TABLE " + CatMotCanc_Table);
        db.execSQL("DROP TABLE " + CatProductos_Table);
        db.execSQL("DROP TABLE " + CatTpago_Table);
        db.execSQL("DROP TABLE " + Usuario_Table);
        db.execSQL("DROP TABLE " + Productos_Table);
        db.execSQL("DROP TABLE " + Productos_Mod_Table);
        db.execSQL("DROP TABLE " + Pedidos_Table);
        db.execSQL("DROP TABLE " + Pedidos_Mod_Table);
        db.execSQL("DROP TABLE " + Synchro_Table);
        onCreate(db);
    }
}