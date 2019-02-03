package jac.infosyst.proyectogas.modelo;

import android.app.ProgressDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jorgeaguilar on 12/27/18.
 */

public class ObjetoRes {


    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String error;

    public String geterror() {
        return error;
    }

    public void seterror(String error) {
        this.error = error;
    }


    private String Admin;

    public String getAdmin() {
        return Admin;
    }

    public void setAdmin(String Admin) {
        this.Admin = Admin;
    }

    private String token;

    public String gettoken() {
        return token;
    }

    public void settoken(String token) {
        this.token = token;
    }


    public Usuario[] user;

    public Usuario[] getuser() {
        return user;
    }

    private Pedido[] pedido;

    public Pedido[] getpedido() {
        return pedido;
    }




    /*
    private List<Producto> productos;
    public List<Producto> getResults() {
        return productos;
    }
    */




 //   private Producto[] productos;

  //  public Producto[] getproductos() {return productos; }

  /*  private Producto[] productos;
    public Producto[] get2productos(){
        return productos;
    }
*/


    private Spinner[] catalogo;

    public Spinner[] getmotivoscancelacion() {
        return catalogo;
    }





}

