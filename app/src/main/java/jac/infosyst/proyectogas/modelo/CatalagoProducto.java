package jac.infosyst.proyectogas.modelo;


import java.util.ArrayList;
import java.util.List;

public class CatalagoProducto {

    private String Oid;
    private String descripcion;
    private double precio_unitario;

    private String unidad;




    // private List<Integer> productos = new ArrayList<Integer>();

    public CatalagoProducto(String Oid,  String descripcion,  double precio_unitario,  String unidad ) {
        this.Oid = Oid;
        this.descripcion = descripcion;

        this.precio_unitario = precio_unitario;
        this.unidad = unidad;


    }


    public String getIdProducto() {
        return Oid;
    }

    public String getdescripcion() {
        return descripcion;
    }

    public String getunidad(){
        return unidad;
    }

    public double getprecio_unitario() {
        return precio_unitario;
    }



}




