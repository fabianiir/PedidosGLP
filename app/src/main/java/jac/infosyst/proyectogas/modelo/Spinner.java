package jac.infosyst.proyectogas.modelo;

import java.util.List;


public class Spinner {

        private int idProducto;
        private String detalle;
        private List<String> mAvailabilityTimes;

        public Spinner(int idProducto , String detalle) {
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
