package jac.infosyst.proyectogas.modelo;


import java.util.ArrayList;
import java.util.List;

public class Producto {

    private String Oid;


    private int cantidad;
private boolean surtido;
    private double precio;

    private String descripcion;

   // private List<Integer> productos = new ArrayList<Integer>();

    public Producto(String Oid, int cantidad  , boolean surtido,  double precio,  String descripcion) {
        this.Oid = Oid;
        this.cantidad = cantidad;
        this.surtido = surtido;
        this.precio = precio;
        this.descripcion = descripcion;


    }


    public String getIdProducto() {
        return Oid;
    }

    public String getdescripcion() {
        return descripcion;
    }

    public boolean getsurtido() {
        return surtido;
    }


    public int getCantidad(){
        return cantidad;
    }

    public double getPrecio() {
        return precio;
    }



}



