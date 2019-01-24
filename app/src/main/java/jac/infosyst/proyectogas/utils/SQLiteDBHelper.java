package jac.infosyst.proyectogas.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;


public class SQLiteDBHelper extends SQLiteOpenHelper{


    private Context ctx;
    private String crearTablaUsuarios = "";
    private String crearTablaconfsqlite = "";
    private boolean isUpgrade = false;
    public static final String USUARIOS_TABLE_NAME = "usuarios";
    public static final String CONFSQLITE_TABLE_NAME = "confsqlite";
    public static final String LOG_TAG_SQLITE_DB = "LOG_TAG_SQLITE_DB";

    public SQLiteDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        ctx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        this.buildCreateTableSql();
        sqLiteDatabase.execSQL(crearTablaUsuarios);
        sqLiteDatabase.execSQL(crearTablaconfsqlite);

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
        confsqliteSqlBuf.append(" status integer )");


        crearTablaconfsqlite = confsqliteSqlBuf.toString();


    }








}



