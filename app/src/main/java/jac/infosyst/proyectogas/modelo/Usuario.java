package jac.infosyst.proyectogas.modelo;


import java.util.ArrayList;

public class Usuario {

  //  private ArrayList<UsuarioInfo> UsuarioList = new ArrayList<UsuarioInfo>();

    private String Oid;
    private String nombre;
    private String UserName;
    private String rol;

    public String getId() {
        return Oid;
    }

    public String getnombre() {
        return nombre;
    }
    public String getUserName() {
        return UserName;
    }

    public String getrol() {
        return rol;
    }


    private UsuarioInfo[] user;

    public UsuarioInfo[] getUsuarioInfo() {
        return user;
    }





}
