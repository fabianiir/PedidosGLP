package jac.infosyst.proyectogas.utils;

/**
 * Created by jorgeaguilar on 12/27/18.
 */

import java.util.List;

import jac.infosyst.proyectogas.modelo.ObjetoRes;

import jac.infosyst.proyectogas.modelo.ObjetoRes2;
import jac.infosyst.proyectogas.modelo.Pedidos;
//import jac.infosyst.proyectogas.utils.Result;
//import jac.infosyst.proyectogas.modelo.User;

import jac.infosyst.proyectogas.modelo.Productos;
import jac.infosyst.proyectogas.modelo.Spinners;
import jac.infosyst.proyectogas.modelo.Usuario;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.GET;

import retrofit2.Call;
import retrofit2.http.Path;


public interface ServicioUsuario {


  //  @FormUrlEncoded
  //  @POST("login.php/{username}/{password}")
 //   Call<ObjetoRes>login(@Field("username") String username, @Field("password") String password);
  // Call<Usuario>login(@Field("username") String username, @Field("password") String password);




  @FormUrlEncoded
  //@Headers("Content-Type:application/x-www-form-urlencoded")
  @POST("login/")
  Call<ObjetoRes>login(@Field("username") String username, @Field("password") String password);


  @FormUrlEncoded
  @POST("pedido/")
  Call<ObjetoRes> getPedidos(
            @Field("chofer") String chofer,
            @Field("estatus") String estatus,
          @Field("token") String token
  );





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


//    @FormUrlEncoded
 //   @POST("obtenerPedidos.php/{tipoPedidos}")
 //   Call<Pedidos> getPedidos(@Field("tipoPedidos") int tipoPedidos);


/*php*//*
    @FormUrlEncoded
    @POST("registroConfiguracion.php")
    Call<Result> registroConfiguracion(
            @Field("ip") String ip,
            @Field("celular") String celular);

*/
  @FormUrlEncoded
  @POST("config")
  Call<Result> registroConfiguracion(
          @Field("public_ip") String ip,
          @Field("phone") String celular);




    @FormUrlEncoded
    @POST("actualizarPedido.php/{idPedido}")
    Call<Result> actualizarPedido(
            @Path("idPedido") int idPedido);



  //  @GET("obtenerProductos.php")
    // Call<Productos> getProductos();


    @FormUrlEncoded
    @POST("obtenerProductos.php/{idPedido}")
    Call<Productos> getProductos(@Field("idPedido") String idPedido);


    @FormUrlEncoded
    @POST("actualizarProducto.php/{idProducto}")
    Call<Result> actualizarProducto(
            @Field("idProducto") int idProducto);

  @FormUrlEncoded
  @POST("obtenerProductos.php/{idPedido}")
  Call<Spinners> getProductos2(@Field("idPedido") int idPedido);

/*php*/
   // @GET("obtenerMotivosCancelacion.php")
   //  Call<Spinners> obtenerMotivosCancelacion();

  @FormUrlEncoded
  @POST("cat_motcanc")
  Call<ObjetoRes> obtenerMotivosCancelacion(@Field("token") String token);

  @FormUrlEncoded
  @POST("bitacora/")
  Call<ObjetoRes> bitacora(
          @Field("evento") boolean evento,
          @Field("emai") String emai,
                  @Field("chofer_id") String chofer_id,
                  @Field("camion_id") String camion_id,
                  @Field("token") String token
  );

  @FormUrlEncoded
  @POST("bitacora")
  Call<ObjetoRes> bitacoraOperador(
          @Field("evento") boolean evento,
          @Field("emai") String emai,
          @Field("chofer_id") String chofer_id,
          @Field("token") String token
  );


  @FormUrlEncoded
  @POST("up_pedido")
  Call<ObjetoRes> up_pedido(
          @Field("pedido_id") String pedido_id,
          @Field("hora") String hora,
          @Field("fecha") String fecha,
          @Field("comentario_cliente") String comentario_cliente,
          @Field("comentario_chofer") String comentario_chofer,
          @Field("latitud") String latitud,
          @Field("longitud") String longitud,
          @Field("suma_iva") int suma_iva,
          @Field("total") int total,
          @Field("pago_id") String pago_id,
          @Field("motivo_cancelacion_id") String motivo_cancelacion_id,
          @Field("estatus_id") String estatus_id,
          @Field("clave") String clave,
          @Field("token") String token


          );


  @FormUrlEncoded
  @POST("cat_productos")
  Call<ObjetoRes2> getCatalagoProductos(@Field("token") String token);


    @FormUrlEncoded
    @POST("productos")
    Call<ObjetoRes> getProductos(
            @Field("pedido_id") String pedido_id,
            @Field("token") String token
    );

  @FormUrlEncoded
  @POST("detalle")
  Call<ObjetoRes> sumarProducto(
          @Field("cantidad") int cantidad,
          @Field("precio") int precio,
          @Field("pedido_id") String pedido_id,
          @Field("producto_id") String producto_id,
          @Field("token") String token
  );




}
