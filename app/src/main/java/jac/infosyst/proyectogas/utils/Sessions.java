package jac.infosyst.proyectogas.utils;

import android.app.Application;

public class Sessions  extends Application {

    public String nameTitle;
    public String getSesnameTitle() {
        return nameTitle;
    }

    public void setSesnameTitle(String nameTitle) {
        this.nameTitle = nameTitle;
    }


    private int idPedido;

    public int getSesIdPedido() {
        return idPedido;
    }

    public void setSesIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    private String sesCliente;

    public String getSesCliente() {
        return sesCliente;
    }

    public void setSesCliente(String sesCliente) {
        this.sesCliente = sesCliente;
    }


    private String sesDireccion;

    public String getsesDireccion() {
        return sesDireccion;
    }

    public void setsesDireccion(String sesDireccion) {
        this.sesDireccion = sesDireccion;
    }

    private String sesDescripcion;

    public String getsesDescripcion() {
        return sesDescripcion;
    }

    public void setsesDescripcion(String sesDescripcion) {
        this.sesDescripcion = sesDescripcion;
    }

    private String sesEstatus;

    public String getsesEstatus() {
        return sesEstatus;
    }

    public void setsesEstatus(String sesEstatus) {
        this.sesEstatus = sesEstatus;
    }

    private String sesDetalleProducto;

    public String getsesDetalleProducto() {
        return sesDetalleProducto;
    }

    public void setsesDetalleProducto(String sesDetalleProducto) { this.sesDetalleProducto = sesDetalleProducto; }

    private String sesFirmaURL;

    public String getsesFirmaURL() {
        return sesFirmaURL;
    }

    public void setsesFirmaURL(String sesFirmaURL) {this.sesFirmaURL = sesFirmaURL; }

    private String sesTotal;

    public String getsesTotal() {
        return sesTotal;
    }

    public void setsesTotal(String sesTotal) {
        this.sesTotal = sesTotal;
    }



    private String sesDetalleProductoSurtir;

    public String getSesDetalleProductoSurtir() {
        return sesDetalleProductoSurtir;
    }

    public void setSesDetalleProductoSurtir(String sesDetalleProductoSurtir) { this.sesDetalleProductoSurtir = sesDetalleProductoSurtir; }


    private int idProducto;

    public int getSesidProducto() {
        return idProducto;
    }

    public void setSesidProducto(int idProducto) {
        this.idProducto = idProducto;
    }



    private String sesUsuarioRol;

    public String getsesUsuarioRol() {
        return sesUsuarioRol;
    }

    public void setsesUsuarioRol(String sesUsuarioRol) {
        this.sesUsuarioRol = sesUsuarioRol;
    }



}
