package jac.infosyst.proyectogas.utils;

/**
 * Created by jorgeaguilar on 12/27/18.
 */



public class ApiUtils {

  //  public static final String BASE_URL = "http://localhost:8080/infosystGLPpedidos/";

    public static final String BASE_URL = "http://189.208.163.83:8060/testing_services_51/webresources/generic/";

 //   public static final String BASE_URL = "http://192.168.1.79/proyectogas/app/v1/";

    public static ServicioUsuario getUserService(){
        return RetrofitClient.getClient(BASE_URL).create(ServicioUsuario.class);
    }



}
