package jac.infosyst.proyectogas.modelo;

import java.util.List;


public class Spinner {

        private int oid;
        private String nombre;
        //private List<String> mAvailabilityTimes;

        public Spinner(int oid , String nombre) {
            this.oid = oid;
            this.nombre = nombre;

        }



        public int getoid() {
            return oid;
        }

        public String getnombre() {
            return nombre;
        }

   /* public List<String> getAvailabilityTimes() {
        return mAvailabilityTimes;
    }*/




}
