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

    private  String configuracion_id;

    public void setConfiguracion_id(String configuracion_id) {
        this.configuracion_id = configuracion_id;
    }

    public String getConfiguracion_id() {
        return configuracion_id;
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


    private String estatus;

    public String getestatus() {
        return estatus;
    }

    public void setestatus(String estatus) {
        this.estatus = estatus;
    }

    public Usuario[] user;

    public Usuario[] getuser() {
        return user;
    }

    private Pedido[] pedido;

    public Pedido[] getpedido() {
        return pedido;
    }

    private Producto[] productos;

    public Producto[] getproducto() {
        return productos;
    }


    private Camion[] camion;

    public Camion[] getcamion() {
        return camion;
    }

    private Imagen[] imagen;

    public Imagen[] getImagen() {
        return imagen;
    }

    private Spinner[] catalogo;

    public Spinner[] getmotivoscancelacion() {
        return catalogo;
    }


    private int total;

    public int getsumaTotal() {
        return total;
    }

    public void setsumaTotal(int sumaTotal) {
        this.total = total;
    }

}

