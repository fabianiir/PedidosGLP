package jac.infosyst.proyectogas;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import jac.infosyst.proyectogas.modelo.ObjetoRes;
import jac.infosyst.proyectogas.modelo.Usuario;
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

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.List;

import jac.infosyst.proyectogas.utils.SQLiteDBHelper;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity{

    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private ProgressDialog dialog;

    private SQLiteDBHelper sqLiteDBHelper = null;

    private String strIP = "";

    private Sessions objSessions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            sqLiteDBHelper = new SQLiteDBHelper(getApplicationContext());
            final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();
            db.execSQL("DELETE FROM '" + SQLiteDBHelper.Usuario_Table + "'");
            db.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = '" + SQLiteDBHelper.Usuario_Table + "'");
            db.execSQL("DELETE FROM '" + SQLiteDBHelper.Pedidos_Table + "'");
            db.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = '" + SQLiteDBHelper.Pedidos_Table + "'");
            db.execSQL("DELETE FROM '" + SQLiteDBHelper.Productos_Table + "'");
            db.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = '" + SQLiteDBHelper.Productos_Table + "'");
            finish();
        }

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

        edtUsername = findViewById(R.id.input_email);
        edtPassword = findViewById(R.id.input_password);
        btnLogin = findViewById(R.id.btn_login);
        objSessions = new Sessions();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              String username = edtUsername.getText().toString();
              String password = edtPassword.getText().toString();

              //Obtiene Token del dispositivo
                Log.w("tokenFire",FirebaseInstanceId.getInstance().getToken());
                ((Sessions) getApplication()).setStrFireTOken(FirebaseInstanceId.getInstance().getToken());

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

        sqLiteDBHelper = new SQLiteDBHelper(getApplicationContext());
        final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();
        String sql = "SELECT * FROM configuracion";

        final Cursor record = db.rawQuery(sql, null);

        if (record.moveToFirst()) {
            strIP = record.getString(record.getColumnIndex("ip"));
            objSessions.setSesstrIpServidor(strIP);
        }

        String BASEURL = strIP + "glpservices/webresources/glpservices/";

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

                        sqLiteDBHelper = new SQLiteDBHelper(getApplicationContext());
                        final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();
                        /*primera vez */

                        ContentValues values = new ContentValues();
                        values.put("oid", arrayListUsuario.get(0).getId());
                        values.put("nombre", arrayListUsuario.get(0).getnombre());
                        values.put("placas", "");
                        values.put("camion", "");
                        values.put("foto", "");
                        values.put("token", resObj.gettoken());
                        values.put("admin", Boolean.parseBoolean(resObj.getAdmin()));
                        db.insert(SQLiteDBHelper.Usuario_Table, null, values);

                        objSessions.setsessIDuser(arrayListUsuario.get(0).getId());
                        if (resObj.getAdmin().equals("true")) {
                            ((Sessions) getApplication()).setsesUsuarioRol("Admin");
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);

                        }else if (resObj.getAdmin().equals("false")) {
                            ((Sessions) getApplication()).setsesUsuarioRol("Operador");
                            Intent intent = new Intent(LoginActivity.this, Escaner.class);
                            startActivity(intent);
                        }
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
                Toast.makeText(LoginActivity.this, "Conexion la alcanza un servidor!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
