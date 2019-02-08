package jac.infosyst.proyectogas.modelo;

import java.util.ArrayList;
import java.util.List;

public class ModeloSpinner {

    private int idProducto;
    private String detalle;

    private List<String> mAvailabilityTimes;

    public ModeloSpinner(int idProducto,  String detalle) {
        this.idProducto = idProducto;
        this.detalle = detalle;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public String getdetalle() {
        return detalle;
    }

    public List<String> getAvailabilityTimes() {
        return mAvailabilityTimes;
    }
}



