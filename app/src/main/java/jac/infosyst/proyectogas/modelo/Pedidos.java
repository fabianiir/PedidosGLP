package jac.infosyst.proyectogas.modelo;

import java.util.ArrayList;

public class Pedidos {

    int realizado = 0;
    private ArrayList<Pedido> pedidos;

    public Pedidos() {

    }

    public ArrayList<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(ArrayList<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

}
