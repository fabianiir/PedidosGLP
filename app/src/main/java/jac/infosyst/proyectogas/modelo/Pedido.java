package jac.infosyst.proyectogas.modelo;

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



    public Pedido(String Oid, String nombre, String placas, String fecha_hora_programada, String cliente, String direccion,
            String cp, String telefono, String comentarios_cliente, String suma_iva, String total) {
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

    }


    public String getOid() {
        return Oid;
    }

    public String getnombre() {
        return nombre;
    }
    public String getplacas() {
        return placas;
    }

    public String getcliente() {
        return cliente;
    }

    public String getdireccion() {
        return direccion;
    }

    public String getcp() {
        return cp;
    }

    public String gettelefono() {
        return telefono;
    }

    public String getcomentarios_cliente() {
        return comentarios_cliente;
    }

    public String getsuma_iva() {
        return suma_iva;
    }

    public String gettotal() {
        return total;
    }

    public String getfechaprogramada() {
        return fecha_hora_programada;
    }





    /*
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
*/




}



