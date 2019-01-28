package jac.infosyst.proyectogas;

/**
 * Created by jorgeaguilar on 12/27/18.
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import jac.infosyst.proyectogas.modelo.ObjetoRes;
import jac.infosyst.proyectogas.modelo.Login;
import jac.infosyst.proyectogas.modelo.Pedidos;
import jac.infosyst.proyectogas.modelo.Usuario;
import jac.infosyst.proyectogas.modelo.UsuarioInfo;
import jac.infosyst.proyectogas.utils.ApiUtils;
import jac.infosyst.proyectogas.utils.Result;
import jac.infosyst.proyectogas.utils.ServicioUsuario;


import jac.infosyst.proyectogas.utils.Sessions;
import jac.infosyst.proyectogas.LectorQR.Escaner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.app.ProgressDialog;

import com.google.android.gms.common.api.Api;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.content.ContentValues;

import java.util.List;

import jac.infosyst.proyectogas.utils.SQLiteDBHelper;
import java.util.ArrayList;
import java.util.Arrays;



public class LoginActivity extends AppCompatActivity{

    EditText edtUsername;
    EditText edtPassword;
    Button btnLogin, btnConfiguracion;
    ServicioUsuario userService;
    private SQLiteDBHelper sqLiteDBHelper = null;
    private String DB_NAME = "proyectogas2.db";
    private int DB_VERSION = 3;
    private String BASEURL = "";
    String ipServidor="";
    Sessions objSessions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Log.v("TAG","chyno");



        edtUsername = (EditText) findViewById(R.id.input_email);
        edtPassword = (EditText) findViewById(R.id.input_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        userService = ApiUtils.getUserService();
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
        btnConfiguracion = (Button) findViewById(R.id.btn_configuracionLogin);


        btnConfiguracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, Configuracion.class);
                startActivity(intent);
            }
        });


    }

    private boolean validateLogin(String username, String password){
        if(username == null || username.trim().length() == 0){
            Toast.makeText(this, "Ingresar Usuario", Toast.LENGTH_SHORT).show();
            return false;
        }
        /*
        if(password == null || password.trim().length() == 0){
            Toast.makeText(this, "Ingresar Contraseña", Toast.LENGTH_SHORT).show();
            return false;
        }
        */

        return true;
    }


    private void login(final String pusername, String ppassword) {

        sqLiteDBHelper = new SQLiteDBHelper(getApplicationContext(), DB_NAME, null, DB_VERSION);
        final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();


        String sql = "SELECT * FROM config WHERE id = 1 ORDER BY id DESC limit 1";

        final int recordCount = db.rawQuery(sql, null).getCount();

        final Cursor record = db.rawQuery(sql, null);
        String strIP = "";
        if (record.moveToFirst()) {

            strIP = record.getString(record.getColumnIndex("ip"));

            objSessions.setSesstrIpServidor(strIP);


        }



        /*admin va al main , operador al de escaner */



       Toast.makeText(getApplicationContext(), "RESULTADO L: " + strIP, Toast.LENGTH_LONG).show();

        SQLiteDatabase dbConn = sqLiteDBHelper.getWritableDatabase();

        Cursor cursor = dbConn.rawQuery(sql, null);

        String checkEmpty = "";
        if (cursor.moveToFirst()) {

            int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
            String firstname = cursor.getString(cursor.getColumnIndex("status"));
            ipServidor = cursor.getString(cursor.getColumnIndex("ip"));
            objSessions.setSesstrIpServidor(ipServidor);


        }

        cursor.close();
        objSessions.setSesstrIpServidor("189.208.163.83");

        BASEURL = "http://"+ objSessions.getSesstrIpServidor()+ ":8060/glpservices/webresources/glpservices/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServicioUsuario service = retrofit.create(ServicioUsuario.class);

        Call call = service.login(pusername,ppassword);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    ObjetoRes resObj = (ObjetoRes) response.body();
                    if(resObj.geterror().equals("false")){
                        Toast.makeText(LoginActivity.this, "Bienvenido! " , Toast.LENGTH_SHORT).show();

                       // insertBitacora();
                        List<Usuario> arrayListUsuario = Arrays.asList(resObj.getuser());

                      //  insertSqLiteUsuario(arrayListUsuario.get(0).getUserName(), resObj.gettoken());

                        objSessions.setsessIDuser(arrayListUsuario.get(0).getId());
                        objSessions.setsessIDcamion("idcamion1");


                        // objSessions.setsessIDuser(objSessions.getsessIDuser());


                        Toast.makeText(LoginActivity.this, "USUARIO:" + objSessions.getsessIDuser()
                                + "CAMION:" +  objSessions.getsessIDcamion() , Toast.LENGTH_SHORT).show();

                     insertBitacora(true, "emai", objSessions.getsessIDuser(), objSessions.getsessIDcamion() , resObj.gettoken() );

                        if (resObj.getAdmin().equals("true")){

                            ((Sessions)getApplication()).setsesUsuarioRol("Admin");
                            Intent intent = new Intent(LoginActivity.this, Escaner.class);
                            intent.putExtra("username", pusername);
                            startActivity(intent);

                        }

                        if (resObj.getAdmin().equals("false")){
                            ((Sessions)getApplication()).setsesUsuarioRol("Operador");
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("username", pusername);
                            startActivity(intent);

                        }



                    } else {
                        Toast.makeText(LoginActivity.this, resObj.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                else {
                    Toast.makeText(LoginActivity.this, "Login Error! Intenta Nuevamente", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Conexion No alcanza un servidor!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, Configuracion.class);
                startActivity(intent);

            }
        });

    }


    public void insertSqLiteUsuario(String message, String token) {
        sqLiteDBHelper = new SQLiteDBHelper(getApplicationContext(), DB_NAME, null, DB_VERSION);

        if (!hasDBVersionError()) {
            sqLiteDBHelper.getWritableDatabase();
           // Toast.makeText(getApplicationContext(), "SQLite bd " + DB_NAME + " creado satisfactoriamente.", Toast.LENGTH_LONG).show();
            insertUsuario(message, token);
        }
    }



    private boolean hasDBVersionError()
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
                Toast.makeText(getApplicationContext(), errorMessage + " , porfavor, elimine la base de datos sqlite desintalando la app primero.", Toast.LENGTH_LONG).show();
            }else
            {
                Toast.makeText(getApplicationContext(), "Error al crear la bd, mensaje: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        }finally {
            return ret;
        }
    }

    public void insertUsuario(String mensaje, String token){

        if(sqLiteDBHelper!=null) {
            SQLiteDatabase sqLiteDatabase = sqLiteDBHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.clear();
            contentValues.put("nombre", mensaje);
            contentValues.put("correo", token);
            contentValues.put("contrasena", "Jerry");
            contentValues.put("sexo", "FEM");
            sqLiteDatabase.insert(SQLiteDBHelper.USUARIOS_TABLE_NAME, null, contentValues);
            Toast.makeText(getApplicationContext(), "Add Sqlite: " + mensaje + "token:" + token , Toast.LENGTH_LONG).show();


        }else
        {
            Toast.makeText(getApplicationContext(), "Crear la bd Primero.", Toast.LENGTH_LONG).show();
        }
    }

    public void insertBitacora(boolean evento, String emai, String chofer_id, String camion_id , String token){
          BASEURL = "http://"+ objSessions.getSesstrIpServidor()+ ":8060/glpservices/webresources/glpservices/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServicioUsuario service = retrofit.create(ServicioUsuario.class);

        if(token == null)
        {
            token = "null";

        }
        Call call = service.bitacora(evento, emai, chofer_id , camion_id , token);
        Toast.makeText(LoginActivity.this, "e:" + evento + "emai:" + emai + "chofer_id:" + chofer_id + "camion_id:"+ camion_id
                + "token:" +  token , Toast.LENGTH_SHORT).show();

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){

                    ObjetoRes resObj = (ObjetoRes) response.body();

                    if(resObj.geterror().equals("false")){
                        Toast.makeText(LoginActivity.this, "inicio:insertBitacora: " + resObj.gettoken(), Toast.LENGTH_SHORT).show();


                        objSessions.setsessToken(resObj.gettoken());


                    } else {
                        Toast.makeText(LoginActivity.this, "inicio:" + resObj.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                else {
                    Toast.makeText(LoginActivity.this, "Bitacora Error! Intenta Nuevamente", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }





}
