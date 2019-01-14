package jac.infosyst.proyectogas.modelo;

public class Configuracion {

    private int id;
    private String ip;
    private String celular;

    public Configuracion(String ip, String celular) {
        this.ip = ip;
        this.celular = celular;

    }


    public String getIP() {
        return ip;
    }

    public String getCelular() {
        return celular;
    }



    
}
