package jac.infosyst.proyectogas.modelo;

public class Spinner {

        private String Oid;
        private String nombre;

        public Spinner(String Oid , String nombre) {
            this.Oid = Oid;
            this.nombre = nombre;
        }

        public String getoid() {
            return Oid;
        }

        public String getnombre() {
            return nombre;
        }
}
