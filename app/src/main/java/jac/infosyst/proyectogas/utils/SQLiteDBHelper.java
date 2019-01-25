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
    protected static final String DATABASE_NAME = "glppedidos";

    /*
    public SQLiteDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        ctx = context;
    }
    */
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

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String sql = "DROP TABLE IF EXISTS config";
        db.execSQL(sql);

        onCreate(db);
    }



/*

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        this.buildCreateTableSql();
        sqLiteDatabase.execSQL(crearTablaUsuarios);
        sqLiteDatabase.execSQL(crearTablaconfsqlite);
        sqLiteDatabase.execSQL(crearTablaconfsqlite2);


        //Toast.makeText(ctx, "Table " + USUARIOS_TABLE_NAME + " is created successfully. ", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Toast.makeText(ctx, "SQLITE ACUTALIZADA", Toast.LENGTH_SHORT).show();

    }


    private void buildCreateTableSql()
    {
        // Build create book table sql.
        StringBuffer bookSqlBuf = new StringBuffer();

        // Create table sql.
        bookSqlBuf.append("create table ");
        bookSqlBuf.append(USUARIOS_TABLE_NAME);
        bookSqlBuf.append("( id integer primary key autoincrement,");
        bookSqlBuf.append(" nombre text,");
        bookSqlBuf.append(" correo text,");
        bookSqlBuf.append(" contrasena text,");
        bookSqlBuf.append(" sexo text )");

        crearTablaUsuarios = bookSqlBuf.toString();

        StringBuffer confsqliteSqlBuf = new StringBuffer();


        confsqliteSqlBuf.append("create table ");
        confsqliteSqlBuf.append(CONFSQLITE_TABLE_NAME);
        confsqliteSqlBuf.append("( id integer primary key autoincrement,");
        confsqliteSqlBuf.append(" status integer, ");
        confsqliteSqlBuf.append(" ipServidor text )");

        crearTablaconfsqlite = confsqliteSqlBuf.toString();
*/

        /*
        StringBuffer confsqlite2 = new StringBuffer();


        confsqlite2.append("create table ");
        confsqlite2.append(CONFSQLITE2_TABLE_NAME);
        confsqlite2.append("( id integer primary key autoincrement,");
        confsqlite2.append(" status integer, ");
        confsqlite2.append(" ipServidor text )");

        crearTablaconfsqlite2 = confsqlite2.toString();



    }
*/


}



