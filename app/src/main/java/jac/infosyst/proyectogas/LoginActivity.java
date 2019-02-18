package jac.infosyst.proyectogas;

/**
 * Created by jorgeaguilar on 12/27/18.
 */

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import jac.infosyst.proyectogas.modelo.Chofer;
import jac.infosyst.proyectogas.modelo.ObjetoRes;
import jac.infosyst.proyectogas.modelo.Usuario;
import jac.infosyst.proyectogas.modelo.UsuarioInfo;
import jac.infosyst.proyectogas.utils.ServicioUsuario;


import jac.infosyst.proyectogas.utils.Sessions;
import jac.infosyst.proyectogas.LectorQR.Escaner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.app.ProgressDialog;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.content.ContentValues;

import java.util.List;

import jac.infosyst.proyectogas.utils.SQLiteDBHelper;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity{

    private static final int DATABASE_VERSION = 1;
    protected static final String DATABASE_NAME = "proyectoGas";
    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private ProgressDialog dialog;

    private SQLiteDBHelper sqLiteDBHelper = null;

    private int DB_VERSION = 1;
    private String DB_NAME = "proyectoGas.db";

    private String BASEURL = "";
    private String strEmai = "";
    private String strIP = "";

    private Sessions objSessions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ObtenerIMEI();
        Log.v("TAG","chyno");
        dialog = new ProgressDialog(LoginActivity.this);

        int PermisoLocalizacion = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (PermisoLocalizacion != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION }, 225);
        } else {
            Log.i("Mensaje", "Se tiene permiso!");
        }

        edtUsername = (EditText) findViewById(R.id.input_email);
        edtPassword = (EditText) findViewById(R.id.input_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        objSessions = new Sessions();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();
                //validate form
                if(validateLogin(username, password)){
                    login(username, password);
                }
            }
        });
    }

    private boolean validateLogin(String username, String password){
        if(username == null || username.trim().length() == 0 || password == null || password.trim().length() == 0){
            Toast.makeText(this, "Ingresar Usuario", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void login(final String pusername, String ppassword) {

        sqLiteDBHelper = new SQLiteDBHelper(getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
        final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();
        String sql = "SELECT * FROM config WHERE id = 1 ORDER BY id DESC limit 1";

        final int recordCount = db.rawQuery(sql, null).getCount();
        final Cursor record = db.rawQuery(sql, null);

        if (record.moveToFirst()) {
            strIP = record.getString(record.getColumnIndex("ip"));
            objSessions.setSesstrIpServidor(strIP);
        }

        SQLiteDatabase dbConn = sqLiteDBHelper.getWritableDatabase();
        Cursor cursor = dbConn.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
            String firstname = cursor.getString(cursor.getColumnIndex("status"));
        }

        cursor.close();

        BASEURL = strIP + "glpservices/webresources/glpservices/";

        Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASEURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        ServicioUsuario service = retrofit.create(ServicioUsuario.class);

        Call call = service.login(pusername, ppassword);

        dialog.setMax(100);
        dialog.setMessage("Iniciando Sesion....");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();

        call.enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    ObjetoRes resObj = (ObjetoRes) response.body();
                    if (resObj.geterror().equals("false")) {
                        Toast.makeText(LoginActivity.this, "Bienvenido! ", Toast.LENGTH_SHORT).show();
                        List<Usuario> arrayListUsuario = Arrays.asList(resObj.getuser());
                        objSessions.setsessIDuser(arrayListUsuario.get(0).getId());
                        UsuarioInfo uss = new UsuarioInfo();
                        uss.setNombre(arrayListUsuario.get(0).getnombre());
                        uss.setOid(arrayListUsuario.get(0).getId());
                        if (resObj.getAdmin().equals("true")) {
                            ((Sessions) getApplication()).setsesUsuarioRol("Admin");
                            Intent intent = new Intent(LoginActivity.this, Configuracion.class);
                            intent.putExtra("username", pusername);
                            startActivity(intent);
                            String sqlEmai = "SELECT * FROM dispositivo WHERE id = 1 ORDER BY id DESC limit 1";
                            final Cursor recordEmai = db.rawQuery(sqlEmai, null);
                            if (recordEmai.moveToFirst()) {
                                strEmai = recordEmai.getString(recordEmai.getColumnIndex("emai"));
                                Toast.makeText(LoginActivity.this, "Sqlite strEmai:" + strEmai, Toast.LENGTH_SHORT).show();
                            }
                        }else if (resObj.getAdmin().equals("false")) {
                            ((Sessions) getApplication()).setsesUsuarioRol("Operador");
                            Intent intent = new Intent(LoginActivity.this, Escaner.class);
                            intent.putExtra("username", pusername);
                            startActivity(intent);
                        }
                        ContentValues cv = new ContentValues();
                        cv.put("ip",strIP);
                        db.update("config", cv, "id=" + 1, null);
                        ContentValues values2 = new ContentValues();
                        values2.put("Oid", arrayListUsuario.get(0).getId());
                        values2.put("token", ((ObjetoRes) response.body()).gettoken());
                        db.insert("usuario", null, values2);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, resObj.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Login Error! Intenta Nuevamente", Toast.LENGTH_SHORT).show();
                }
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                btnLogin.setEnabled(true);
            }
            @Override
            public void onFailure(Call call, Throwable t) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Toast.makeText(LoginActivity.this, "Conexion No alcanza un servidor!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, Configuracion.class);
                startActivity(intent);
            }
        });
    }

    public void ObtenerIMEI()
    {
        int permissionCheck = ContextCompat.checkSelfPermission( this, Manifest.permission.READ_PHONE_STATE );
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE }, 225);
        } else {
            Log.i("Mensaje", "Se tiene permiso!");
        }
        Chofer myIMEI = new Chofer();
        TelephonyManager mTelephony = (TelephonyManager) getApplication().getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephony.getDeviceId() != null){
            myIMEI.setImei(mTelephony.getDeviceId());
        }
    }

    public void insertaImeiSqLite(String emai){
        sqLiteDBHelper = new SQLiteDBHelper(LoginActivity.this, DATABASE_NAME, null, DATABASE_VERSION);
        final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();
        ContentValues values2 = new ContentValues();
        values2.put("emai", emai);
        db.insert("dispositivo", null, values2);
    }
}
