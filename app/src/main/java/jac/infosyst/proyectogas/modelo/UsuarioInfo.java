package jac.infosyst.proyectogas.modelo;

import android.graphics.Bitmap;

public class UsuarioInfo {

    private static String oid;
    private static String nombre;
    private static String placas;
    private static Bitmap foto;
    private static Bitmap fotoFirma;

    public UsuarioInfo() {
        this.oid = oid;
        this.nombre = nombre;
        this.placas = placas;
        this.foto = foto;
        this.fotoFirma = fotoFirma;
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

    public static String getPlacas() {
        return placas;
    }

    public void setPlacas(String placas) {
        UsuarioInfo.placas = placas;
    }

    public static Bitmap getFoto() {
        return foto;
    }

    public void  setFoto(Bitmap bitmap) { UsuarioInfo.foto = bitmap; }

    public static Bitmap getFotoFirma() {
        return fotoFirma;
    }

    public void  setFotoFirma(Bitmap bitmap) { UsuarioInfo.fotoFirma = bitmap; }



}
