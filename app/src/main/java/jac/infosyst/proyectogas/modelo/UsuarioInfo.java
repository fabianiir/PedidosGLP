package jac.infosyst.proyectogas.modelo;

public class UsuarioInfo {

    private static String oid;
    private static String nombre;
    private static String placas;

    public UsuarioInfo() {
        this.oid = oid;
        this.nombre = nombre;
        this.placas = placas;
    }

    public static String getOid() {
        return oid;
    }

    public void setOid(String Oid) {
        UsuarioInfo.oid = Oid;
    }

    public static String getNombre() {
            return nombre;
        }

    public void setNombre(String nombre) {
        UsuarioInfo.nombre = nombre;
    }

    public static String getPlacas() { return placas; }

    public void setPlacas(String placas) {
        UsuarioInfo.placas = placas;
    }
}
