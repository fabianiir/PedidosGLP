package jac.infosyst.proyectogas.modelo;

public class Pedido {

/*nombres de variables exactos a los valores retorno php*/
    private int id;
    private String cliente;
    private String descripcion;
    private String direccion;
    private String estatus;
    private String detalle;
    private String firmaUrl;
    private double total;


    public Pedido(int id, String cliente, String descripcion, String direccion, String estatus
                        , String detalle, String firmaUrl, double total) {
        this.id = id;
        this.cliente = cliente;
        this.descripcion = descripcion;
        this.direccion = direccion;
        this.estatus = estatus;
        this.detalle = detalle;
        this.firmaUrl = firmaUrl;
        this.total = total;

    }


    public int getId() {
        return id;
    }

    public String getcliente() {
        return cliente;
    }

    public String getdescripcion() {
        return descripcion;
    }

    public String getdireccion(){
        return direccion;
    }

    public String getestatus() {
        return estatus;
    }

    public String getdetalleproducto() {
        return detalle;
    }

    public String getfirmaurl () {
        return firmaUrl ;
    }

    public double gettotal() {
        return total;
    }



}



