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
import android.support.v4.app.Fragment;

import  jac.infosyst.proyectogas.modelo.ConfiguracionModelo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.squareup.okhttp.OkHttpClient;

import java.util.ArrayList;
import java.util.List;


public class Configuracion extends AppCompatActivity {

    EditText edtIP;
    EditText edtTelefono;
    Button btnConfig;

    String BASEURL;
    static int checkConfiguracionSqLite = 0;
    private static SQLiteDBHelper sqLiteDBHelper = null;

    boolean fromSplash = false;

    private static int  statusConf;

    String strIP = "";

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

        sqLiteDBHelper = new SQLiteDBHelper(getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
        final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registrando Configuracion...");
        progressDialog.show();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

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
                    sqLiteDBHelper = new SQLiteDBHelper(getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
                    final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();
                    /*primera vez */
                    ContentValues values2 = new ContentValues();
                    values2.put("oid", resObj.getConfiguracion_id());
                    values2.put("ip", dominio);
                    values2.put("telefono", telefono);
                    values2.put("imei", ObtenerIMEI());

                    db.insert(SQLiteDBHelper.Config_Table, null, values2);

                    ContentValues cv = new ContentValues();
                    cv.put("oid", resObj.getConfiguracion_id());
                    cv.put("ip", dominio);
                    cv.put("telefono", telefono);
                    cv.put("imei", ObtenerIMEI());

                //db.update(SQLiteDBHelper.Config_Table, cv, "oid = " + resObj.getConfiguracion_id() , null);
                //poner if de la primera vez
                ((Sessions)getApplication()).setSesstrIpServidor(dominio);

                    Intent intent = new Intent(Configuracion.this, LoginActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "nnn:" +  t.getMessage(), Toast.LENGTH_LONG).show();
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

            sqLiteDBHelper = new SQLiteDBHelper(getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
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
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
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
