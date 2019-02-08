package jac.infosyst.proyectogas.modelo;

public class ObjetoRes3 {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String error;

    public String geterror() {
        return error;
    }

    public void seterror(String error) {
        this.error = error;
    }

    private CatalogoEstatus[] catalogo;

    public CatalogoEstatus[] getCatalogoEstatus() {
        return catalogo;
    }
}
