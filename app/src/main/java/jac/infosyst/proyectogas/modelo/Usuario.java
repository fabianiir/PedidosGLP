package jac.infosyst.proyectogas.modelo;


import java.util.ArrayList;

public class Usuario {

  //  private ArrayList<UsuarioInfo> UsuarioList = new ArrayList<UsuarioInfo>();

    private int id;
    private String nombre;
    private String rol;

    public int getId() {
        return id;
    }

    public String getnombre() {
        return nombre;
    }

    public String getrol() {
        return rol;
    }


    private UsuarioInfo[] user;

    public UsuarioInfo[] getUsuarioInfo() {
        return user;
    }





}
