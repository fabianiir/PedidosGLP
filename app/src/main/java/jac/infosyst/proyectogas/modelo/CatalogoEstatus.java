package jac.infosyst.proyectogas.modelo;

public class CatalogoEstatus {

    private String Oid;
    private String nombre;

    public CatalogoEstatus(String Oid,  String nombre) {
        this.Oid = Oid;
        this.nombre = nombre;
    }

    public String getIdProducto() {
        return Oid;
    }

    public String getdescripcion() {
        return nombre;
    }
}
