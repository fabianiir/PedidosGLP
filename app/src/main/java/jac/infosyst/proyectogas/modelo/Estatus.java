package jac.infosyst.proyectogas.modelo;

public class Estatus {

    private static String PendienteId;
    private static String SurtidoId;
    private static String CanceladoId;

    public Estatus(){
        this.PendienteId = PendienteId;
        this.SurtidoId = SurtidoId;
        this.CanceladoId = CanceladoId;
    }

    public static String getPendienteId() {
        return PendienteId;
    }

    public void setPendienteId(String pendienteId) {
        PendienteId = pendienteId;
    }

    public static String getSurtidoId() {
        return SurtidoId;
    }

    public void setSurtidoId(String surtidoId) {
        SurtidoId = surtidoId;
    }

    public static String getCanceladoId() {
        return CanceladoId;
    }

    public void setCanceladoId(String canceladoId) {
        CanceladoId = canceladoId;
    }
}
