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
import jac.infosyst.proyectogas.utils.ApiUtils;
import jac.infosyst.proyectogas.utils.Result;
import jac.infosyst.proyectogas.utils.ServicioUsuario;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.app.ProgressDialog;

import com.google.android.gms.common.api.Api;

public class LoginActivity extends AppCompatActivity{

    EditText edtUsername;
    EditText edtPassword;
    Button btnLogin;
    ServicioUsuario userService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUsername = (EditText) findViewById(R.id.input_email);
        edtPassword = (EditText) findViewById(R.id.input_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        userService = ApiUtils.getUserService();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              String username = edtUsername.getText().toString();
              String password = edtPassword.getText().toString();
                //validate form
                if(validateLogin(username, password)){
                   // Intent intent = new Intent(LoginActivity.this, Configuracion.class);
                   // startActivity(intent);
                    login(username, password);
                }
            }
        });

    }

    private boolean validateLogin(String username, String password){
        if(username == null || username.trim().length() == 0){
            Toast.makeText(this, "Ingresar Usuario", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password == null || password.trim().length() == 0){
            Toast.makeText(this, "Ingresar Contraseña", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void login(final String pusername, String ppassword) {

        Call call = userService.login(pusername,ppassword);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    ObjetoRes resObj = (ObjetoRes) response.body();

                   // Toast.makeText(LoginActivity.this, "Respuesta: " + resObj.geterror(), Toast.LENGTH_SHORT).show();


                    if(resObj.geterror().equals("false")){
                        Toast.makeText(LoginActivity.this, "Bienvenido!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, Configuracion.class);
                        intent.putExtra("username", pusername);
                        startActivity(intent);

                    } else {
                        Toast.makeText(LoginActivity.this, resObj.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                else {
                    Toast.makeText(LoginActivity.this, "Error! Intenta Nuevamente", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }

}
