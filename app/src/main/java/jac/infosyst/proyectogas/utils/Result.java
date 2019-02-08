package jac.infosyst.proyectogas.utils;

import com.google.gson.annotations.SerializedName;
//import jac.infosyst.proyectogas.modelos.User;


public class Result {
    @SerializedName("error")
    private Boolean error;

    @SerializedName("message")
    private String message;

    public Boolean getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}

