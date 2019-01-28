package jac.infosyst.proyectogas;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import jac.infosyst.proyectogas.utils.ApiUtils;
import jac.infosyst.proyectogas.utils.Result;
import jac.infosyst.proyectogas.utils.SQLiteDBHelper;
import jac.infosyst.proyectogas.utils.ServicioUsuario;
import jac.infosyst.proyectogas.LectorQR.Escaner;
import jac.infosyst.proyectogas.utils.Sessions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import  jac.infosyst.proyectogas.modelo.ConfiguracionModelo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.squareup.okhttp.OkHttpClient;

import java.util.ArrayList;
import java.util.List;


public class Configuracion extends AppCompatActivity{

    EditText edtIP;
    EditText edtTelefono;
    Button btnConfig;

    static int checkConfiguracionSqLite = 0;
    private static SQLiteDBHelper sqLiteDBHelper = null;
    private static String DB_NAME = "proyectogas9.db";
    private static int DB_VERSION = 1;

    private static int  statusConf ;

    String strIP = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_PHONE_STATE );
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE }, 225);
        } else {
            Log.i("Mensaje", "Se tiene permiso!");
        }

        edtIP = (EditText) findViewById(R.id.input_IP);
        edtTelefono = (EditText) findViewById(R.id.input_telefono);
        btnConfig = (Button) findViewById(R.id.btn_configuracion);


        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipDominio = edtIP.getText().toString();
                String telefono = edtTelefono.getText().toString();
                //validate form
                if(validateConfig(ipDominio, telefono)){
                    insertarConfiguracion();

                }
            }
        });
    }

    private void insertarConfiguracion(){
        sqLiteDBHelper = new SQLiteDBHelper(getApplicationContext(), DB_NAME, null, DB_VERSION);

        final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registrando Configuracion...");
        progressDialog.show();

        strIP = edtIP.getText().toString().trim();
        String strCelular = edtTelefono.getText().toString().trim();

        OkHttpClient client = new OkHttpClient();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiUtils.BASE_URL)
              //  .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();


        //Defining retrofit api service
        ServicioUsuario service = retrofit.create(ServicioUsuario.class);

        ConfiguracionModelo conf = new ConfiguracionModelo(strIP, strCelular);


        Call<Result> call = service.registroConfiguracion(
                conf.getIP(),
                conf.getCelular()

        );

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
               // insertSqLite("x", strIP);
                ContentValues values = new ContentValues();

                values.put("status", "1");
                values.put("ip", strIP);

                boolean createSuccessful = db.insert("config", null, values) > 0;
                db.close();
              //  Toast.makeText(getApplicationContext(), "geo: " + createSuccessful, Toast.LENGTH_LONG).show();


                ((Sessions)getApplication()).setSesstrIpServidor(strIP);

                Intent intent = new Intent(Configuracion.this, LoginActivity.class);
                startActivity(intent);

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }


    private boolean validateConfig(String ip, String telefono){
        if(ip == null || ip.trim().length() == 0){
            Toast.makeText(this, "Ingresar IP o Dominio", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(telefono == null || telefono.trim().length() == 0){
            Toast.makeText(this, "Ingresar Telefono", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static class SplashActivity extends Activity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_splash);
//Permisos


            int PermisoAlmacenamiento = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE);




            if (PermisoAlmacenamiento != PackageManager.PERMISSION_GRANTED) {
                Log.i("Mensaje", "No se tiene permiso.");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 225);
            } else {
                Log.i("Mensaje", "Se tiene permiso!");
            }

















            sqLiteDBHelper = new SQLiteDBHelper(getApplicationContext(), DB_NAME, null, DB_VERSION);

            final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();



            int studentId = 1 ;

           // String sql = "SELECT * FROM config ORDER BY id DESC limit 1";

            String sql = "SELECT * FROM config where id = " + studentId;

            final int recordCount = db.rawQuery(sql, null).getCount();
            Toast.makeText(getApplicationContext(), "contador: " + recordCount, Toast.LENGTH_LONG).show();

            SQLiteDatabase dbConn = sqLiteDBHelper.getWritableDatabase();

            Cursor cursor = dbConn.rawQuery(sql, null);
            String email="";
            String checkEmpty = "";
            if (cursor.moveToFirst()) {

                int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
                String firstname = cursor.getString(cursor.getColumnIndex("status"));
                email = cursor.getString(cursor.getColumnIndex("ip"));
              //  Toast.makeText(getApplicationContext(), "datos: " + id, Toast.LENGTH_LONG).show();

            }

            cursor.close();

            //db.close();
//




            /*
            sqLiteDBHelper = new SQLiteDBHelper(getApplicationContext(), DB_NAME, null, DB_VERSION);

            if (!hasDBVersionError()) {

                sqLiteDBHelper.getWritableDatabase();

                if (sqLiteDBHelper != null) {
                    SQLiteDatabase sqLiteDatabase = sqLiteDBHelper.getWritableDatabase();
                    Cursor c = sqLiteDatabase.query(SQLiteDBHelper.CONFSQLITE_TABLE_NAME, null, null, null,
                            null, null, null);

                    int idx = c.getColumnIndex("status");
                    int ip = c.getColumnIndex("ipServidor");

                    checkConfiguracionSqLite = idx;
                    int idStatus = idx;

                    Toast.makeText(getApplicationContext(), "IP: " + ip, Toast.LENGTH_LONG).show();

                    statusConf = ip;


                } else {
                    Toast.makeText(getApplicationContext(), "Please create database first.", Toast.LENGTH_LONG).show();

                }
            }

*/

            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run() {
                    //email.equals("null");
                    if(recordCount == 0) {

                        /*cuando se le agrega un campo nuevo (sqlite sin valor)a una tabla ya existente, por default se le asigna un -1 */
                        Intent intent = new Intent(SplashActivity.this, Configuracion.class);

                        startActivity(intent);


                    }


                    else{

                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);

                    }
                }


            }, 4000);

        }
    }

    /*
    public void insertSqLite(String message, String ip) {
        sqLiteDBHelper = new SQLiteDBHelper(getApplicationContext(), DB_NAME, null, DB_VERSION);

        if (!hasDBVersionError()) {
            sqLiteDBHelper.getWritableDatabase();
            Toast.makeText(getApplicationContext(), "SQLite bd " + DB_NAME + " creado satisfactoriamente.", Toast.LENGTH_LONG).show();
            insertUsuario(message, ip);
           // selectConf(statusConf);
        }
    }



    private static boolean hasDBVersionError()
    {
        boolean ret = false;
        try
        {
            SQLiteDatabase sqliteDatabase = sqLiteDBHelper.getReadableDatabase();
        }catch(SQLiteException ex)
        {
            ret = true;

            String errorMessage = ex.getMessage();

            Log.d(SQLiteDBHelper.LOG_TAG_SQLITE_DB, errorMessage, ex);

            if(errorMessage.startsWith("No se pudo acutalizar la base de datos sqlite"))
            {
             //   Toast.makeText(SplashActivity.this, errorMessage + " , porfavor, elimine la base de datos sqlite desintalando la app primero.", Toast.LENGTH_LONG).show();
            }else
            {
               // Toast.makeText(getApplicationContext(), "Error al crear la bd, mensaje: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        }finally {
            return ret;
        }
    }

    public void insertUsuario(String mensaje, String ip){

        if(sqLiteDBHelper!=null) {
            SQLiteDatabase sqLiteDatabase = sqLiteDBHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.clear();
            contentValues.put("status", 1);
            contentValues.put("ipServidor", ip);
            sqLiteDatabase.insert(SQLiteDBHelper.CONFSQLITE_TABLE_NAME, null, contentValues);

            Toast.makeText(getApplicationContext(), "CONFSQLITE_TABLE_NAME table successfully." + contentValues.getAsString("status"), Toast.LENGTH_LONG).show();
        }else
        {
            Toast.makeText(getApplicationContext(), "Please create database first.", Toast.LENGTH_LONG).show();
        }

    }

    public int selectConf(int idStatus){


        return idStatus;

    }
    */

}
