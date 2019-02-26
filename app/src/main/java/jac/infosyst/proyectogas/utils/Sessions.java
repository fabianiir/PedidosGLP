package jac.infosyst.proyectogas.utils;

import android.app.Application;

import java.lang.reflect.Array;
import java.util.ArrayList;

import jac.infosyst.proyectogas.modelo.Producto;
import jac.infosyst.proyectogas.modelo.Productos;

public class Sessions  extends Application {

    public String nameTitle;
    public String getSesnameTitle() {
        return nameTitle;
    }
    public void setSesnameTitle(String nameTitle) {
        this.nameTitle = nameTitle;
    }


    private String idPedido;
    public String getSesIdPedido() {
        return idPedido;
    }
    public void setSesIdPedido(String idPedido) {
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


    private Producto[] sesDetalleProductoSurtir;
    public Producto[] getSesDetalleProductoSurtir() {
        return sesDetalleProductoSurtir;
    }
    public void setSesDetalleProductoSurtir(Producto[] sesDetalleProductoSurtir) { this.sesDetalleProductoSurtir = sesDetalleProductoSurtir; setImpProductos(sesDetalleProductoSurtir);}


    public static  Producto[] impProductos;
    public static Producto[] getImpProductos(){ return impProductos; }
    public static void setImpProductos(Producto[] impProductos) {
        Sessions.impProductos = impProductos;
    }


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

/*nuevas*/

    private String sesNombre;
    public String getsessesNombrell() {
        return sesNombre;
    }
    public void setSesNombre(String sesUsuarioRol) {
        this.sesNombre = sesNombre;
    }


    private String Placas;
    public String getsesPlacas() {
        return Placas;
    }
    public void setsesPlacas(String sesUsuarioRol) {
        this.Placas = Placas;
    }


    private String sesfechaprogramada;
    public String getsesfechaprogramada() {
        return sesfechaprogramada;
    }
    public void setsesfechaprogramada(String sesfechaprogramada) {
        this.sesfechaprogramada = sesfechaprogramada;
    }


    private String setsescp;
    public String getsetsescp() {
        return setsescp;
    }
    public void setsetsescp(String setsescp) {
        this.setsescp = setsescp;
    }


    private String sestelefono;
    public String getsestelefono() {
        return sestelefono;
    }
    public void setsestelefono(String sestelefono) {
        this.sestelefono = sestelefono;
    }


    private String sescomentarioscliente;
    public String getsescomentarioscliente() {
        return sescomentarioscliente;
    }
    public void setsescomentarioscliente(String sescomentarioscliente) {
        this.sescomentarioscliente = sescomentarioscliente;
    }


    private String sessumaiva;
    public String getsessumaiva() {
        return sessumaiva;
    }
    public void setsessumaiva(String sessumaiva) {
        this.sessumaiva = sessumaiva;
    }


    private String strIpServidor;
    public String getSesstrIpServidor() {
        return strIpServidor;
    }
    public void setSesstrIpServidor(String strIpServidor) { this.strIpServidor = strIpServidor; }


    private String sesscamion_id;
    public String getsesscamion_id() {
        return sesscamion_id;
    }
    public void setsesscamion_id(String sesscamion_id) { this.sesscamion_id = sesscamion_id; }


    private String sessToken;
    public String getsessToken() {
        return sessToken;
    }
    public void setsessToken(String sessToken) { this.sessToken = sessToken; }


    private String sessIDuser;
    public String getsessIDuser() {
        return sessIDuser;
    }
    public void setsessIDuser(String sessIDuser) { this.sessIDuser = sessIDuser; }


    private String sessIDcamion;
    public String getsessIDcamion() {
        return sessIDcamion;
    }
    public void setsessIDcamion(String sessIDcamion) { this.sessIDcamion = sessIDcamion; }


    private String OidProducto;
    public String getSesOidProducto() {
        return OidProducto;
    }
    public void setSesOidProducto(String OidProducto) {
        this.OidProducto = OidProducto;
    }


    private String tipo_pedido;
    public String getSestipo_pedido() {
        return tipo_pedido;
    }
    public void setSestipo_pedido(String tipo_pedido) {
        this.tipo_pedido = tipo_pedido;
    }


    private ArrayList<Double> arrayPriceTotal;
    public ArrayList<Double> getSesarrayPriceTotal() {
        return arrayPriceTotal;
    }
    public void setSesarrayPriceTotal(ArrayList<Double> arrayPriceTotal) {
        this.arrayPriceTotal = arrayPriceTotal;
    }


    private String ubicacion_lat;
    public String getSesubicacion_latitude() {
        return ubicacion_lat;
    }
    public void setSesubicacion_latitude(String ubicacion_lat) {
        this.ubicacion_lat = ubicacion_lat;
    }


    private String ubicacion_long;
    public String getSesubicacion_longitude() {
        return ubicacion_long;
    }
    public void setSesubicacion_longitude(String ubicacion_long) {
        this.ubicacion_long = ubicacion_long;
    }


    private String strRestarProducto;
    public String getSesstrRestarProducto() {
        return strRestarProducto;
    }
    public void setSessstrRestarProducto(String strRestarProducto) {
        this.strRestarProducto = strRestarProducto;
    }


    private String strChoferId;
    public String getStrChoferId() {
        return strChoferId;
    }
    public void setStrChoferId(String strChoferId) {
        this.strChoferId = strChoferId;
    }


    private String strImei;
    public String getStrImei() {
        return strImei;
    }
    public void setStrImei(String strImei) {
        this.strImei = strImei;
    }


    private String strCamionId;
    public String getStrCamionId() {
        return strCamionId;
    }
    public void setStrCamionId(String strCamionId) {
        this.strCamionId = strCamionId;
    }


    private String strDominio;
    public String getStrDominio() {
        return strDominio;
    }
    public void setStrDominio(String strDominio) {
        this.strDominio = strDominio;
    }
}
