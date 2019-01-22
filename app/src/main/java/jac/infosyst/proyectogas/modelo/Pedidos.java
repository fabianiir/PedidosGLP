package jac.infosyst.proyectogas.modelo;

import java.util.ArrayList;

public class Pedidos {

    int realizado = 0;
    private ArrayList<Pedido> pedidos;

    public Pedidos() {

    }

   /* public ArrayList<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(ArrayList<Pedido> pedidos) {
        this.pedidos = pedidos;
    }
*/

    private int Oid;
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

    public int getOid() {
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


    private Pedido[] pedido;

    public Pedido[] getpedido() {
        return pedido;
    }


}
