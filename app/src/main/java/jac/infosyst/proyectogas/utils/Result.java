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


/*
    @SerializedName("user")
   // private User user;

    public Result(Boolean error, String message, User user) {
        this.error = error;
        this.message = message;
        this.user = user;
    }

    public Boolean getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }
*/

}

