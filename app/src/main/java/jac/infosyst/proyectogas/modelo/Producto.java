package jac.infosyst.proyectogas.modelo;


public class Producto {

    private int idProducto;
    private int idPedido;

    private String detalle;
    private double cantidad;
    private double precio;



    public Producto(int idProducto, int idPedido,  String detalle, double cantidad, double precio) {
        this.idProducto = idProducto;
        this.idPedido = idPedido;
        this.detalle = detalle;
        this.cantidad = cantidad;
        this.precio = precio;

    }


    public int getIdProducto() {
        return idProducto;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public String getDetalle() {
        return detalle;
    }

    public double getCantidad(){
        return cantidad;
    }

    public double getPrecio() {
        return precio;
    }

}



