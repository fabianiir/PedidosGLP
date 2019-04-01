package jac.infosyst.proyectogas;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
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
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import jac.infosyst.proyectogas.modelo.ObjetoRes;
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
import android.support.v4.app.Fragment;

import  jac.infosyst.proyectogas.modelo.ConfiguracionModelo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.squareup.okhttp.OkHttpClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class Configuracion extends AppCompatActivity {

    EditText edtIP;
    EditText edtTelefono;
    Button btnConfig;

    String Base_Url;
    private static SQLiteDBHelper sqLiteDBHelper = null;

    boolean fromSplash = false;

    @Override
    public void onBackPressed() {
        if(fromSplash){
            finish();
        }
        else{
            MainActivity.setFragmentController(0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        if (getIntent().getBooleanExtra("SPLASH", false)) {
            fromSplash = true;
        }

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
                    insertarConfiguracion(ipDominio, telefono);
                }
            }
        });
    }


    private void insertarConfiguracion(final String dominio, final String telefono){

        sqLiteDBHelper = new SQLiteDBHelper(getApplicationContext());
        final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registrando Configuracion...");
        progressDialog.show();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        try{
            Base_Url = dominio + "glpservices/webresources/glpservices/";
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Base_Url)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            //Defining retrofit api service
            ServicioUsuario service = retrofit.create(ServicioUsuario.class);

            Call call = service.registroConfiguracion(dominio, telefono);

            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    if(response.isSuccessful()) {
                        progressDialog.dismiss();
                        ObjetoRes resObj = (ObjetoRes) response.body();
                        sqLiteDBHelper = new SQLiteDBHelper(getApplicationContext());
                        final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();
                        /*primera vez */
                        ContentValues values = new ContentValues();
                        values.put("oid", resObj.getConfiguracion_id());
                        values.put("ip", dominio);
                        values.put("telefono", telefono);
                        values.put("imei", ObtenerIMEI());

                        db.insert(SQLiteDBHelper.Config_Table, null, values);
                        //poner if de la primera vez
                        ((Sessions)getApplication()).setStrDominio(dominio);

                        Intent intent = new Intent(Configuracion.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "No hay conexión a internet o la IP no es valida", Toast.LENGTH_LONG).show();
                }
            });
        }catch (Exception ex){
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "URL no valido", Toast.LENGTH_LONG).show();
        }
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

        if(telefono.trim().length() <8 ){
            Toast.makeText(this, "El numero de telefono debe tener por lo menos 8 digitos", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static  class SplashActivity extends Activity {

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

            sqLiteDBHelper = new SQLiteDBHelper(getApplicationContext());
            final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();
            String sql = "SELECT * FROM " + SQLiteDBHelper.Config_Table;
            final int recordCount = db.rawQuery(sql, null).getCount();

            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run() {
                    //email.equals("null");
                    if(recordCount == 0) {
                        /*cuando se le agrega un campo nuevo (sqlite sin valor)a una tabla ya existente, por default se le asigna un -1 */
                        Intent intent = new Intent(SplashActivity.this, Configuracion.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        sqLiteDBHelper = new SQLiteDBHelper(getApplicationContext());
                        final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();
                        String sql = "SELECT * FROM " + SQLiteDBHelper.Synchro_Table + " ORDER BY id DESC LIMIT 1";
                        Cursor cursor = db.rawQuery(sql, null);
                        if(cursor.getCount() <= 0){
                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            java.util.Date date = new Date();
                            String only_date = dateFormat.format(date);
                            try {
                                date = dateFormat.parse(only_date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Date Cdate = new Date();
                            String SyncDate = "";
                            if (cursor.moveToFirst()) {
                                SyncDate = cursor.getString(cursor.getColumnIndex("fecha"));
                            }
                            try {
                                Cdate = dateFormat.parse(SyncDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if(Cdate.before(date)){
                                db.execSQL("DELETE FROM '" + SQLiteDBHelper.CatEstatus_Table + "'");
                                db.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = '" + SQLiteDBHelper.CatEstatus_Table + "'");
                                db.execSQL("DELETE FROM '" + SQLiteDBHelper.CatMotCanc_Table + "'");
                                db.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = '" + SQLiteDBHelper.CatMotCanc_Table + "'");
                                db.execSQL("DELETE FROM '" + SQLiteDBHelper.CatProductos_Table + "'");
                                db.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = '" + SQLiteDBHelper.CatProductos_Table + "'");
                                db.execSQL("DELETE FROM '" + SQLiteDBHelper.CatTpago_Table + "'");
                                db.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = '" + SQLiteDBHelper.CatTpago_Table + "'");
                                db.execSQL("DELETE FROM '" + SQLiteDBHelper.Usuario_Table + "'");
                                db.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = '" + SQLiteDBHelper.Usuario_Table + "'");
                                db.execSQL("DELETE FROM '" + SQLiteDBHelper.Pedidos_Table + "'");
                                db.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = '" + SQLiteDBHelper.Pedidos_Table + "'");
                                db.execSQL("DELETE FROM '" + SQLiteDBHelper.Productos_Table + "'");
                                db.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = '" + SQLiteDBHelper.Productos_Table + "'");
                                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                sql = "SELECT * FROM " + SQLiteDBHelper.Usuario_Table;
                                cursor = db.rawQuery(sql, null);
                                sql = "SELECT placas FROM " + SQLiteDBHelper.Usuario_Table;
                                Cursor cursor1 = db.rawQuery(sql, null);
                                if(cursor.getCount() > 0){
                                    cursor1.moveToFirst();
                                    String placa = cursor1.getString(cursor1.getColumnIndex("placas"));
                                    if(!placa.isEmpty()) {
                                        boolean admin = false;
                                        if (cursor.moveToFirst()) {
                                            admin = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("admin")));
                                        }
                                        if (admin) {
                                            ((Sessions) getApplication()).setsesUsuarioRol("Admin");
                                        } else {
                                            ((Sessions) getApplication()).setsesUsuarioRol("Operador");
                                        }
                                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                        intent.putExtra("User", true);
                                        startActivity(intent);
                                        finish();
                                    }else{
                                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }else{
                                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }
                    }
                }
            }, 4000);
        }
    }

    public String ObtenerIMEI()
    {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_PHONE_STATE );
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE }, 225);
        } else {
            Log.i("Mensaje", "Se tiene permiso!");
        }

        String myIMEI = "";

        TelephonyManager mTelephony = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephony.getDeviceId() != null){
            myIMEI = mTelephony.getDeviceId();
        }
        return myIMEI;
    }
}
