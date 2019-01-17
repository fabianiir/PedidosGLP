package jac.infosyst.proyectogas.utils;

/**
 * Created by jorgeaguilar on 12/27/18.
 */

import jac.infosyst.proyectogas.modelo.ObjetoRes;

import jac.infosyst.proyectogas.modelo.Pedidos;
//import jac.infosyst.proyectogas.utils.Result;
//import jac.infosyst.proyectogas.modelo.User;

import jac.infosyst.proyectogas.modelo.Productos;
import jac.infosyst.proyectogas.modelo.Spinners;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;


import retrofit2.Call;
import retrofit2.http.Path;


public interface ServicioUsuario {

/*
    @FormUrlEncoded
    @POST("login.php/{username}/{password}")
    Call<ObjetoRes>login(@Field("username") String username, @Field("password") String password);
*/


  @FormUrlEncoded
  //@Headers("Content-Type:application/x-www-form-urlencoded")
  @POST("log/")
  Call<ObjetoRes>login(@Field("user") String username, @Field("passw") String password);




/*
    @FormUrlEncoded
    @POST("login/{username}/{password}")
    Call<Result> userLogin(
            @Field("username") String username,
            @Field("password") String password
    );
*/


  //  @GET("obtenerPedidos.php")
  //  Call<Pedidos> getPedidos();
    @FormUrlEncoded
    @POST("obtenerPedidos.php/{tipoPedidos}")
    Call<Pedidos> getPedidos(@Field("tipoPedidos") int tipoPedidos);



    @FormUrlEncoded
    @POST("registroConfiguracion.php")
    Call<Result> registroConfiguracion(
            @Field("ip") String ip,
            @Field("celular") String celular);


    @FormUrlEncoded
    @POST("actualizarPedido.php/{idPedido}")
    Call<Result> actualizarPedido(
            @Path("idPedido") int idPedido);



  //  @GET("obtenerProductos.php")
    // Call<Productos> getProductos();


    @FormUrlEncoded
    @POST("obtenerProductos.php/{idPedido}")
    Call<Productos> getProductos(@Field("idPedido") int idPedido);


    @FormUrlEncoded
    @POST("actualizarProducto.php/{idProducto}")
    Call<Result> actualizarProducto(
            @Field("idProducto") int idProducto);

  @FormUrlEncoded
  @POST("obtenerProductos.php/{idPedido}")
  Call<Spinners> getProductos2(@Field("idPedido") int idPedido);





}
