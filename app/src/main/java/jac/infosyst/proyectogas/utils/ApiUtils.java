package jac.infosyst.proyectogas.utils;

/**
 * Created by jorgeaguilar on 12/27/18.
 */

import jac.infosyst.proyectogas.utils.Sessions;


public class ApiUtils {

    public static final String BASE_URL = "http://189.208.163.83:8060/glpservices/webresources/glpservices/";

    public static ServicioUsuario getUserService(){
        return RetrofitClient.getClient(BASE_URL).create(ServicioUsuario.class);
    }



}
