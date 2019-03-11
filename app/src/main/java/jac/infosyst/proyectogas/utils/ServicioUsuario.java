package jac.infosyst.proyectogas.utils;

/**
 * Created by jorgeaguilar on 12/27/18.
 */

import java.util.List;

import jac.infosyst.proyectogas.modelo.ObjetoRes;

import jac.infosyst.proyectogas.modelo.ObjetoRes2;
import jac.infosyst.proyectogas.modelo.ObjetoRes3;
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

  @FormUrlEncoded
  //@Headers("Content-Type:application/x-www-form-urlencoded")
  @POST("login/")
  Call<ObjetoRes>login(@Field("username") String username, @Field("password") String password);

  @FormUrlEncoded
  @POST("camionqr/")
  Call<ObjetoRes>camion(@Field("id") int identificador);


  @FormUrlEncoded
  @POST("pedido/")
  Call<ObjetoRes> getPedidos(
            @Field("chofer") String chofer,
            @Field("estatus") String estatus,
          @Field("token") String token
  );

  @FormUrlEncoded
  @POST("fotos/")
  Call<ObjetoRes> Foto(
          @Field("id") String id,
          @Field("tipo") String tipo,
          @Field("token") String token
  );

  @FormUrlEncoded
  @POST("fotos/")
  Call<ObjetoRes> Foto(
          @Field("id") String id,
          @Field("tipo") int tipo,
          @Field("token") String token
  );

    @FormUrlEncoded
    @POST("in_foto/")
    Call<ObjetoRes> in_foto(
            @Field("id") String id,
            @Field("archivo") String archivo,
            @Field("tipo") int tipo,
            @Field("token") String token
    );

  @FormUrlEncoded
  @POST("config")
  Call<ObjetoRes> registroConfiguracion(
          @Field("public_ip") String ip,
          @Field("phone") String celular);

  @FormUrlEncoded
  @POST("cat_motcanc")
  Call<ObjetoRes> obtenerMotivosCancelacion(@Field("token") String token);

  @FormUrlEncoded
  @POST("bitacora/")
  Call<ObjetoRes> bitacora(
          @Field("evento") boolean evento,
          @Field("imei") String emai,
          @Field("chofer_id") String chofer_id,
          @Field("camion_id") String camion_id,
          @Field("token") String token,
          @Field("Fire_token") String fireToken
  );

  @FormUrlEncoded
  @POST("bitacora")
  Call<ObjetoRes> bitacoraOperador(
          @Field("evento") boolean evento,
          @Field("imei") String emai,
          @Field("chofer_id") String chofer_id,
          @Field("token") String token,
          @Field("Fire_token") String fireToken
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
  @POST("cat_estatus")
  Call<ObjetoRes3> getCatalogoEstatus(@Field("token") String token);

  @FormUrlEncoded
  @POST("productos")
  Call<ObjetoRes> getProductos(
          @Field("pedido_id") String pedido_id,
          @Field("token") String token
  );

  @FormUrlEncoded
  @POST("detalle")
  Call<ObjetoRes> sumarProducto(
          @Field("oid") String oid,
          @Field("cantidad") int cantidad,
          @Field("precio") int precio,
          @Field("pedido_id") String pedido_id,
          @Field("producto_id") String producto_id,
          @Field("token") String token
  );

  @FormUrlEncoded
  @POST("up_detalle")
  Call<ObjetoRes> up_detalle(
          @Field("id") String id,
          @Field("cantidad") int cantidad,
          @Field("surtido") boolean surtido,
          @Field("precio") int precio,
          @Field("token") String token
  );
}
