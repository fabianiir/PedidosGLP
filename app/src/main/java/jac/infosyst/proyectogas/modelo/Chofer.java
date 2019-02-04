package jac.infosyst.proyectogas.modelo;

public class Chofer {

    private  static String camion;
    private static String imei;

    public Chofer() {
        this.camion = camion;
        this.imei = imei;
    }

    public static String getCamion() {
        return camion;
    }

    public static String getImei() {
        return imei;
    }

    public void setCamion(String  camion) {

        this.camion = camion;
    }

    public  void setImei(String imei) {
        this.imei = imei;
    }
}
