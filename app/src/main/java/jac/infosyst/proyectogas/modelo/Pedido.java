package jac.infosyst.proyectogas.modelo;

import java.util.ArrayList;
import java.util.List;

public class Pedido {

/*nombres de variables exactos a los valores retorno php*/
    private String Oid;
    private String nombre;
    private String placas;
    private String fecha_hora_programada;
    private String cliente;
    private String direccion;
    private String cp;
    private String telefono;
    private String comentarios_cliente;
    private String suma_iva;
    private String total;
    private String tipo_pedido;
    private String estatus;
    private String empresa;
    private String forma_pago;

    private String ubicacion_lat;
    private String ubicacion_long;

    private Producto[] productos;

    public Pedido(String Oid, String nombre, String placas, String fecha_hora_programada, String cliente, String direccion,
            String cp, String telefono, String comentarios_cliente, String suma_iva, String total,String tipo_pedido,  String estatus
           , Producto[] productos, String ubicacion_lat,String ubicacion_long, String empresa, String forma_pago
                ) {
        this.Oid = Oid;
        this.nombre = nombre;
        this.placas = placas;
        this.fecha_hora_programada = fecha_hora_programada;
        this.cliente = cliente;
        this.direccion = direccion;
        this.cp = cp;
        this.telefono = telefono;
        this.comentarios_cliente = comentarios_cliente;
        this.suma_iva = suma_iva;
        this.total = total;
        this.tipo_pedido = tipo_pedido;
        this.estatus = estatus;
        this.productos = productos;
        this.ubicacion_lat = ubicacion_lat;
        this.ubicacion_long = ubicacion_long;
        this.empresa = empresa;
        this.forma_pago = forma_pago;
    }


    public String getOid() {
        return Oid;
    }

    public void setOid(String oid) {
        Oid = oid;
    }

    public String getnombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getplacas() {
        return placas;
    }

    public void setPlacas(String placas) {
        this.placas = placas;
    }

    public String getcliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getdireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getcp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public String gettelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getcomentarios_cliente() {
        return comentarios_cliente;
    }

    public void setComentarios_cliente(String comentarios_cliente) {
        this.comentarios_cliente = comentarios_cliente;
    }

    public String getsuma_iva() {
        return suma_iva;
    }

    public void setSuma_iva(String suma_iva) {
        this.suma_iva = suma_iva;
    }

    public String gettotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getfechaprogramada() {
        return fecha_hora_programada;
    }

    public void setFecha_hora_programada(String fecha_hora_programada) {
        this.fecha_hora_programada = fecha_hora_programada;
    }

    public  String gettipo_pedido() { return  tipo_pedido; }

    public void setTipo_pedido(String tipo_pedido) {
        this.tipo_pedido = tipo_pedido;
    }

    public  String getestatus () { return  estatus; }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getForma_pago() {
        return forma_pago;
    }

    public void setForma_pago(String forma_pago) {
        this.forma_pago = forma_pago;
    }

    public Producto[] getHobbies() {
        return productos;
    }

    public void setHobbies(Producto[] productos) {
        this.productos = productos;
    }


    public  String getubicacion_lat() { return  ubicacion_lat; }

    public void setUbicacion_lat(String ubicacion_lat) {
        this.ubicacion_lat = ubicacion_lat;
    }

    public  String getubicacion_long() { return  ubicacion_long; }

    public void setUbicacion_long(String ubicacion_long) {
        this.ubicacion_long = ubicacion_long;
    }
}



