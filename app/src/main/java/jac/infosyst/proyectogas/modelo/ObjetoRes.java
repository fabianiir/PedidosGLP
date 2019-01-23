package jac.infosyst.proyectogas.modelo;

import java.util.ArrayList;

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
    private Spinner[] catalogo;

    public Spinner[] getmotivoscancelacion() {
        return catalogo;
    }




}

