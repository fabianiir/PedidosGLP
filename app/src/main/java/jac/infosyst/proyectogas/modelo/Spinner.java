package jac.infosyst.proyectogas.modelo;

import java.util.List;


public class Spinner {

        private String Oid;
        private String nombre;
        private String OptimisticLockField;
        private String GCRecord;

        //private List<String> mAvailabilityTimes;

        public Spinner(String Oid , String nombre, String OptimisticLockField, String GCRecord) {
            this.Oid = Oid;
            this.nombre = nombre;
            this.OptimisticLockField = OptimisticLockField;
            this.GCRecord = GCRecord;

        }



        public String getoid() {
            return Oid;
        }

        public String getnombre() {
            return nombre;
        }
    public String getGCRecord() {
        return GCRecord;
    }
    public String getOptimisticLockField() {
        return OptimisticLockField;
    }



}
