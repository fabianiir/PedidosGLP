package jac.infosyst.proyectogas.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;


public class SQLiteDBHelper extends SQLiteOpenHelper{

    private Context ctx;
    private String crearTablaUsuarios = "";
    private String crearTablaconfsqlite = "";
    private String crearTablaconfsqlite2 = "";
    private boolean isUpgrade = false;
    public static final String USUARIOS_TABLE_NAME = "usuarios";
    public static final String CONFSQLITE_TABLE_NAME = "confsqlite";
    public static final String CONFSQLITE2_TABLE_NAME = "confsqlite3";
    public static final String LOG_TAG_SQLITE_DB = "LOG_TAG_SQLITE_DB";
    private static final int DATABASE_VERSION = 1;
    //protected static final String DATABASE_NAME = "glppedidos2";

    protected static final String DATABASE_NAME = "proyectogas17";

    public SQLiteDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        ctx = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE config " +
                "( id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "status TEXT, " +
                "ip TEXT ) ";

        db.execSQL(sql);

        String sqlUsuario = "CREATE TABLE usuario " +
                "( id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Oid TEXT, " +
                "token TEXT ) ";

        db.execSQL(sqlUsuario);

        String sqlDispositivo = "CREATE TABLE dispositivo " +
                "( id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " emai TEXT) ";

        db.execSQL(sqlDispositivo);

        String sqlproductos = "CREATE TABLE productos " +
                "( id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " OidPedido TEXT," +
                " OidProducto TEXT, " +
                " cantidad INTEGER, " +
                " surtido BOOLEAN, " +
                " precio DOUBLE, " +
                " descripcion TEXT, " +
                " activo TEXT) ";
        db.execSQL(sqlproductos);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String sql = "DROP TABLE IF EXISTS config";
        db.execSQL(sql);

        onCreate(db);
    }
}



