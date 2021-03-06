package jac.infosyst.proyectogas.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import jac.infosyst.proyectogas.LoginActivity;
import jac.infosyst.proyectogas.MainActivity;
import jac.infosyst.proyectogas.R;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import jac.infosyst.proyectogas.modelo.Camion;
import jac.infosyst.proyectogas.modelo.CatalogoEstatus;
import jac.infosyst.proyectogas.modelo.Estatus;
import jac.infosyst.proyectogas.modelo.ObjetoRes;
import jac.infosyst.proyectogas.modelo.Chofer;
import jac.infosyst.proyectogas.modelo.ObjetoRes3;
import jac.infosyst.proyectogas.modelo.Pedido;
import jac.infosyst.proyectogas.modelo.Pedidos;
import jac.infosyst.proyectogas.modelo.Producto;
import jac.infosyst.proyectogas.utils.SQLiteDBHelper;
import jac.infosyst.proyectogas.utils.ServicioUsuario;
import jac.infosyst.proyectogas.adaptadores.PedidoAdapter;

import jac.infosyst.proyectogas.utils.Sessions;
//import jac.infosyst.proyectogas.vista.Escaner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.util.Log;

import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class PedidosFragment extends Fragment implements LocationListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewPedidos;
    private RecyclerView.Adapter adapter;

    private PedidoAdapter pedidoAdapter;

    private ArrayList<String> pedidoList = new ArrayList<>();

    int tiempoActualizarPedidos = 30000;
    int tipoPedidos, idPedido;
    String strtext;
    Button btnAtenderPedido,  btnImprimirPedido;
    FragmentManager f_manager;

    private SQLiteDBHelper sqLiteDBHelper = new SQLiteDBHelper(getActivity());;
    private String DB_NAME = "proyectogas17.db";
    private int DB_VERSION = 1;
    private String TABLE_NAME = "usuarios";
    ServicioUsuario userService;
    private Context ctx = getContext();

    private String BASEURL = "";
    Sessions objSessions;
    String strIP = "";
    String strchofer = "";
    String strtoken = "";
    String Imei = "";

    LocationManager locationManager;
    String strLatitude = "";
    String strLongitude = "";
    int tiempoSeguimiento = 10000;
    Location location;

    private List<String> hobbies = new ArrayList<String>();

    public PedidosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pedidos, container, false);
        ((Sessions)getActivity().getApplicationContext()).setSesIdPedido("null");
        MainActivity.setFragmentController(0);

        ctx = getContext();

        Sessions strSess = new Sessions();
        sqLiteDBHelper = new SQLiteDBHelper(getActivity());
        final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();

        String sql = "SELECT * FROM configuracion";

        final int recordCount = db.rawQuery(sql, null).getCount();
        final Cursor record = db.rawQuery(sql, null);

        if (record.moveToFirst()) {
            strIP = record.getString(record.getColumnIndex("ip"));
        }

        sqLiteDBHelper = new SQLiteDBHelper(getActivity());

        final SQLiteDatabase db3 = sqLiteDBHelper.getWritableDatabase();


        String sql3 = "SELECT * FROM usuario";


        final int recordCount3 = db.rawQuery(sql3, null).getCount();
        SQLiteDatabase dbConn3 = sqLiteDBHelper.getWritableDatabase();
        Cursor cursor3 = dbConn3.rawQuery(sql3, null);

        if (cursor3.moveToFirst()) {
            strchofer = cursor3.getString(cursor3.getColumnIndex("oid"));
            strtoken = cursor3.getString(cursor3.getColumnIndex("token"));
        }
        objSessions = new Sessions();
        //userService = ApiUtils.getUserService();

        swipeRefreshLayout= (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                obtener_pedidos();
                guardar_pedidos_productos();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        recyclerViewPedidos = (RecyclerView) rootView.findViewById(R.id.recyclerViewPedidos);
        recyclerViewPedidos.setHasFixedSize(true);
        recyclerViewPedidos.setLayoutManager(new LinearLayoutManager(getActivity()));

        btnAtenderPedido = (Button) rootView.findViewById(R.id.btnAtenderPedido);
        btnAtenderPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.getDispositivoEncontrado())
                {
                    Toast.makeText(getActivity(),"Impresora conectada", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getActivity(),"Impresora no conectada", Toast.LENGTH_SHORT).show();
                }
                if(((Sessions)getActivity().getApplicationContext()).getSesIdPedido().equals("null")) {

                    Toast.makeText(getActivity(),  "Debe seleccionar un Pedido!" , Toast.LENGTH_SHORT).show();
                }
                else{
                    DetallePedidoFragment dpf = new DetallePedidoFragment(getActivity().getBaseContext());
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, dpf);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });

        btnImprimirPedido = (Button) rootView.findViewById(R.id.btnImprimirPedido);
        btnImprimirPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((Sessions)getActivity().getApplicationContext()).getSesIdPedido().equals("null")) {

                    Toast.makeText(getActivity(),  "Debe seleccionar un Pedido!" , Toast.LENGTH_SHORT).show();
                }
                else {

                }
            }
        });

        String strNameTittle = String.valueOf(((AppCompatActivity)getActivity()).getSupportActionBar().getTitle());

        if(strNameTittle.equals("Pedidos")){
            tipoPedidos = 0;
            ((Sessions)getActivity().getApplicationContext()).setBoolPedidosRealizados(false);
        }
        if(strNameTittle.equals("Pedidos Realizados")){
            tipoPedidos = 1;
            ((Sessions)getActivity().getApplicationContext()).setBoolPedidosRealizados(true);
        }

        if (tipoPedidos == 0){
            try {
                actualizarPedidosPendientes();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(tipoPedidos == 1){
            actualizarPedidosSurtidos();
        }
        return rootView;
    }

    @Override
    public void onResume() {
        guardar_pedidos_productos();
        obtener_pedidos();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (tipoPedidos == 0){
                    try {
                        actualizarPedidosPendientes();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else if(tipoPedidos == 1){
                    actualizarPedidosSurtidos();
                }
                handler.postDelayed(this, 60000);
            }
        }, 60000);
        super.onResume();
    }

    public void obtener_pedidos(){
        final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();
        String sql = "SELECT * FROM configuracion";
        Cursor record = db.rawQuery(sql, null);

        if (record.moveToFirst()) {
            strIP = record.getString(record.getColumnIndex("ip"));
            Imei = record.getString(record.getColumnIndex("imei"));
        }

        String oid = "", token = "";
        sql = "SELECT * FROM usuario";
        record = db.rawQuery(sql, null);

        if (record.moveToFirst()) {
            oid = record.getString(record.getColumnIndex("oid"));
            token = record.getString(record.getColumnIndex("token"));
        }

        BASEURL = strIP + "glpservices/webresources/glpservices/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServicioUsuario service = retrofit.create(ServicioUsuario.class);

        Call call = service.getPedidos(oid, "Pendiente", token);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    ObjetoRes resObj = (ObjetoRes) response.body();
                    if (resObj.geterror().equals("false")) {
                        if (resObj.getpedido() != null) {
                            List<Pedido> list = Arrays.asList(resObj.getpedido());
                            db.execSQL("DELETE FROM '" + SQLiteDBHelper.Pedidos_Table + "'");
                            db.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = '" + SQLiteDBHelper.Pedidos_Table + "'");
                            db.execSQL("DELETE FROM '" + SQLiteDBHelper.Productos_Table + "'");
                            db.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = '" + SQLiteDBHelper.Productos_Table + "'");

                            for (Pedido pedido : list) {
                                ContentValues values = new ContentValues();
                                values.put("oid", pedido.getOid());
                                String datetime = pedido.getfechaprogramada();
                                datetime.replace('T', ' ');
                                values.put("fecha_hora_programada", datetime);
                                values.put("cliente", pedido.getcliente());
                                values.put("direccion", pedido.getdireccion());
                                values.put("cp", pedido.getcp());
                                values.put("telefono", pedido.gettelefono());
                                values.put("lat", pedido.getubicacion_lat());
                                values.put("lon", pedido.getubicacion_long());
                                values.put("comentario_cliente", pedido.getcomentarios_cliente());
                                if (pedido.getsuma_iva() == null) {
                                    values.put("suma_iva", 0);
                                } else {
                                    values.put("suma_iva", Double.parseDouble(pedido.getsuma_iva()));
                                }
                                if (pedido.gettotal() == null) {
                                    values.put("total", 0);
                                } else {
                                    values.put("total", Double.parseDouble(pedido.gettotal()));
                                }
                                values.put("empresa", pedido.getEmpresa());
                                values.put("tipo_pedido", pedido.gettipo_pedido());
                                values.put("forma_pago", pedido.getForma_pago());
                                values.put("estatus", pedido.getestatus());
                                values.put("surtido", "0");
                                db.insert(SQLiteDBHelper.Pedidos_Table, null, values);

                                if (pedido.getHobbies() != null) {

                                    List<Producto> listproductos = Arrays.asList(pedido.getHobbies());

                                    for (Producto producto : listproductos) {
                                        ContentValues valuesProd = new ContentValues();
                                        valuesProd.put("oid", producto.getOidProducto());
                                        valuesProd.put("cantidad", producto.getCantidad());
                                        valuesProd.put("surtido", producto.getsurtido());
                                        valuesProd.put("precio", producto.getPrecio());
                                        valuesProd.put("descripcion", producto.getdescripcion());
                                        valuesProd.put("pedido", pedido.getOid());
                                        db.insert(SQLiteDBHelper.Productos_Table, null, valuesProd);
                                    }
                                }
                                final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();
                                String sql = "SELECT * FROM pedidos_modificados";
                                Cursor record = db.rawQuery(sql, null);
                                if(record.getCount()>0) {
                                    for (record.moveToFirst(); !record.isAfterLast(); record.moveToNext()) {
                                        try {
                                            db.delete(SQLiteDBHelper.Pedidos_Table,"oid = ?", new String[] {record.getString(record.getColumnIndex("oid"))});
                                        }catch(Exception ex){

                                        }
                                    }
                                }
                            }
                                if (tipoPedidos == 0){
                                    try {
                                        actualizarPedidosPendientes();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }else if(tipoPedidos == 1) {
                                    actualizarPedidosSurtidos();
                                }
                        } else {
                            Toast.makeText(ctx, "No existen Pedidos!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ctx, "No hay pedidos nuevos", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ctx, "error! ", Toast.LENGTH_SHORT).show();
                }
                obtener_pedidos_surtidos();
            }
            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(ctx, "No hay conexión a Internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void obtener_pedidos_surtidos(){
        final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();
        String sql = "SELECT * FROM configuracion";
        Cursor record = db.rawQuery(sql, null);

        if (record.moveToFirst()) {
            strIP = record.getString(record.getColumnIndex("ip"));
            Imei = record.getString(record.getColumnIndex("imei"));
        }

        String oid = "", token = "";
        sql = "SELECT * FROM usuario";
        record = db.rawQuery(sql, null);

        if (record.moveToFirst()) {
            oid = record.getString(record.getColumnIndex("oid"));
            token = record.getString(record.getColumnIndex("token"));
        }

        BASEURL = strIP + "glpservices/webresources/glpservices/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServicioUsuario service = retrofit.create(ServicioUsuario.class);

        Call call = service.getPedidos(oid, "Surtido", token);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    ObjetoRes resObj = (ObjetoRes) response.body();
                    if (resObj.geterror().equals("false")) {
                        if (resObj.getpedido() != null) {
                            List<Pedido> list = Arrays.asList(resObj.getpedido());

                            for (Pedido pedido : list) {
                                ContentValues values = new ContentValues();
                                values.put("oid", pedido.getOid());
                                String datetime = pedido.getfechaprogramada();
                                datetime.replace('T', ' ');
                                values.put("fecha_hora_programada", datetime);
                                values.put("cliente", pedido.getcliente());
                                values.put("direccion", pedido.getdireccion());
                                values.put("cp", pedido.getcp());
                                values.put("telefono", pedido.gettelefono());
                                values.put("lat", pedido.getubicacion_lat());
                                values.put("lon", pedido.getubicacion_long());
                                values.put("comentario_cliente", pedido.getcomentarios_cliente());
                                if (pedido.getsuma_iva() == null) {
                                    values.put("suma_iva", 0);
                                } else {
                                    values.put("suma_iva", Double.parseDouble(pedido.getsuma_iva()));
                                }
                                if (pedido.gettotal() == null) {
                                    values.put("total", 0);
                                } else {
                                    values.put("total", Double.parseDouble(pedido.gettotal()));
                                }
                                values.put("empresa", pedido.getEmpresa());
                                values.put("tipo_pedido", pedido.gettipo_pedido());
                                values.put("forma_pago", pedido.getForma_pago());
                                values.put("estatus", pedido.getestatus());
                                values.put("surtido", "1");
                                db.insert(SQLiteDBHelper.Pedidos_Table, null, values);

                                if (pedido.getHobbies() != null) {

                                    List<Producto> listproductos = Arrays.asList(pedido.getHobbies());

                                    for (Producto producto : listproductos) {
                                        ContentValues valuesProd = new ContentValues();
                                        valuesProd.put("oid", producto.getOidProducto());
                                        valuesProd.put("cantidad", producto.getCantidad());
                                        valuesProd.put("surtido", producto.getsurtido());
                                        valuesProd.put("precio", producto.getPrecio());
                                        valuesProd.put("descripcion", producto.getdescripcion());
                                        valuesProd.put("pedido", pedido.getOid());
                                        db.insert(SQLiteDBHelper.Productos_Table, null, valuesProd);
                                    }
                                }
                                final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();
                                String sql = "SELECT * FROM pedidos_modificados";
                                Cursor record = db.rawQuery(sql, null);
                                if(record.getCount()>0) {
                                    for (record.moveToFirst(); !record.isAfterLast(); record.moveToNext()) {
                                        try {
                                            db.delete(SQLiteDBHelper.Pedidos_Table,"oid = ?", new String[] {record.getString(record.getColumnIndex("oid"))});
                                        }catch(Exception ex){

                                        }
                                    }
                                }
                            }
                            try {
                                if (tipoPedidos == 0){
                                    try {
                                        actualizarPedidosPendientes();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }else if(tipoPedidos == 1) {
                                    actualizarPedidosSurtidos();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(ctx, "No existen Pedidos!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ctx, "No hay pedidos nuevos", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ctx, "error! ", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(ctx, "No hay conexión a Internet", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void actualizarPedidosPendientes() throws JSONException {
        BASEURL = strIP + "glpservices/webresources/glpservices/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final ServicioUsuario service = retrofit.create(ServicioUsuario.class);
        Call call;

        boolean admin = false;

        SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();

        String sql3 = "SELECT * FROM usuario";

        Cursor cursor3 = db.rawQuery(sql3, null);

        if (cursor3.moveToFirst()) {
            strchofer = cursor3.getString(cursor3.getColumnIndex("oid"));
            strtoken = cursor3.getString(cursor3.getColumnIndex("token"));
        }

        String sqlPedido = "SELECT * FROM pedidos WHERE surtido = '0'";

        Cursor cursor = db.rawQuery(sqlPedido, null);

        Pedido[] pedidos = new Pedido[cursor.getCount()];
        int i = 0;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String sqlProducto = "SELECT * FROM productos WHERE pedido = '" + cursor.getString(cursor.getColumnIndex("oid")) + "'";
            Cursor cursorPr = db.rawQuery(sqlProducto, null);
            if(cursorPr.getCount() > 0) {
                Producto[] productos = new Producto[cursorPr.getCount()];
                int j = 0;
                for (cursorPr.moveToFirst(); !cursorPr.isAfterLast(); cursorPr.moveToNext()) {
                    Producto producto = new Producto(cursorPr.getString(cursorPr.getColumnIndex("oid")),
                            Integer.parseInt(cursorPr.getString(cursorPr.getColumnIndex("cantidad"))),
                            Boolean.parseBoolean(cursorPr.getString(cursorPr.getColumnIndex("surtido"))),
                            Double.parseDouble(cursorPr.getString(cursorPr.getColumnIndex("precio"))),
                            cursorPr.getString(cursorPr.getColumnIndex("descripcion")),
                            cursorPr.getString(cursorPr.getColumnIndex("pedido")));
                    if (producto != null) {
                        productos[j] = producto;
                    }
                    j++;
                }
                Pedido pedido = new Pedido(cursor.getString(cursor.getColumnIndex("oid")),
                        "",
                        "",
                        cursor.getString(cursor.getColumnIndex("fecha_hora_programada")),
                        cursor.getString(cursor.getColumnIndex("cliente")),
                        cursor.getString(cursor.getColumnIndex("direccion")),
                        cursor.getString(cursor.getColumnIndex("cp")),
                        cursor.getString(cursor.getColumnIndex("telefono")),
                        cursor.getString(cursor.getColumnIndex("comentario_cliente")),
                        cursor.getString(cursor.getColumnIndex("suma_iva")),
                        cursor.getString(cursor.getColumnIndex("total")),
                        cursor.getString(cursor.getColumnIndex("tipo_pedido")),
                        cursor.getString(cursor.getColumnIndex("estatus")), productos,
                        cursor.getString(cursor.getColumnIndex("lat")),
                        cursor.getString(cursor.getColumnIndex("lon")),
                        cursor.getString(cursor.getColumnIndex("empresa")),
                        cursor.getString(cursor.getColumnIndex("forma_pago")));
                if (pedido != null) {
                    pedidos[i] = pedido;
                }
                i++;
            }else{
                Pedido pedido = new Pedido(cursor.getString(cursor.getColumnIndex("oid")),
                        "",
                        "",
                        cursor.getString(cursor.getColumnIndex("fecha_hora_programada")),
                        cursor.getString(cursor.getColumnIndex("cliente")),
                        cursor.getString(cursor.getColumnIndex("direccion")),
                        cursor.getString(cursor.getColumnIndex("cp")),
                        cursor.getString(cursor.getColumnIndex("telefono")),
                        cursor.getString(cursor.getColumnIndex("comentario_cliente")),
                        cursor.getString(cursor.getColumnIndex("suma_iva")),
                        cursor.getString(cursor.getColumnIndex("total")),
                        cursor.getString(cursor.getColumnIndex("tipo_pedido")),
                        cursor.getString(cursor.getColumnIndex("estatus")), null,
                        cursor.getString(cursor.getColumnIndex("lat")),
                        cursor.getString(cursor.getColumnIndex("lon")),
                        cursor.getString(cursor.getColumnIndex("empresa")),
                        cursor.getString(cursor.getColumnIndex("forma_pago")));
                if (pedido != null) {
                    pedidos[i] = pedido;
                }
                i++;
            }
        }

        cursor.close();

        adapter = new PedidoAdapter(Arrays.asList(pedidos),getActivity(), getFragmentManager());
        recyclerViewPedidos.setAdapter(adapter);
    }

    public void guardar_pedidos_productos(){
        final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();
        String sql = "SELECT * FROM pedidos_modificados";
        Cursor record = db.rawQuery(sql, null);
        if(record.getCount()>0) {
            for (record.moveToFirst(); !record.isAfterLast(); record.moveToNext()) {

                BASEURL = strIP + "glpservices/webresources/glpservices/";
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASEURL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                String token = "";
                sql = "SELECT * FROM usuario";
                Cursor recordUss = db.rawQuery(sql, null);

                if (recordUss.moveToFirst()) {
                    token = recordUss.getString(recordUss.getColumnIndex("token"));
                }

                ServicioUsuario userService = retrofit.create(ServicioUsuario.class);

                Call  call = userService.in_foto(record.getString(record.getColumnIndex("oid")),
                        record.getString(record.getColumnIndex("firma")),
                        3, strtoken);

                call.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        if (response.isSuccessful()) {
                            ObjetoRes resObj = (ObjetoRes) response.body();
                            if (resObj.geterror().equals("false")) {
                            } else {
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call call, Throwable t) {

                    }
                });

                if  (!record.getString(record.getColumnIndex("foto_fuga")).isEmpty()){
                    call = userService.in_foto(record.getString(record.getColumnIndex("oid")),
                            record.getString(record.getColumnIndex("foto_fuga")),
                            4, strtoken);

                    call.enqueue(new Callback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            if (response.isSuccessful()) {
                                ObjetoRes resObj = (ObjetoRes) response.body();
                                if (resObj.geterror().equals("false")) {
                                } else {
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call call, Throwable t) {

                        }
                    });
                }

                call = userService.up_pedido(record.getString(record.getColumnIndex("oid")),
                        record.getString(record.getColumnIndex("hora")),
                        record.getString(record.getColumnIndex("fecha")),
                        "",
                        record.getString(record.getColumnIndex("comentario_chofer")),
                        "",
                        "",
                        0,
                        0,
                        record.getString(record.getColumnIndex("pago_id")),
                        record.getString(record.getColumnIndex("motivo_cancelacion_id")),
                        record.getString(record.getColumnIndex("estatus_id")),
                        "Up_8",
                        token);
                call.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        if (response.isSuccessful()) {
                            ObjetoRes resObj = (ObjetoRes) response.body();
                            if (resObj.geterror().equals("false")) {
                                db.delete(SQLiteDBHelper.Pedidos_Mod_Table, "oid = ?", new String[]{resObj.getMessage()});
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {

                    }
                });
            }
        }

        sql = "SELECT * FROM productos_modificados";
        record = db.rawQuery(sql, null);
        int j = 0;
        if(record.getCount()>0){
            for (record.moveToFirst(); !record.isAfterLast(); record.moveToNext()) {

                sql = "SELECT * FROM configuracion";

                Cursor recordConf = db.rawQuery(sql, null);

                if (recordConf.moveToFirst()) {
                    strIP = recordConf.getString(recordConf.getColumnIndex("ip"));
                }

                BASEURL = strIP + "glpservices/webresources/glpservices/";
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASEURL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                String token = "";
                sql = "SELECT * FROM usuario";
                Cursor recordUss = db.rawQuery(sql, null);

                if (recordUss.moveToFirst()) {
                    token = recordUss.getString(recordUss.getColumnIndex("token"));
                }

                ServicioUsuario service = retrofit.create(ServicioUsuario.class);
                String prueba = record.getString(record.getColumnIndex("surtido"));
                if(prueba.equals("1")){
                    Call call = service.sumarProducto(record.getString(record.getColumnIndex("oid")),
                            Integer.parseInt(record.getString(record.getColumnIndex("cantidad"))),
                            Double.parseDouble(record.getString(record.getColumnIndex("precio"))),
                            record.getString(record.getColumnIndex("pedido_id")),
                            record.getString(record.getColumnIndex("producto_id")),
                            token);
                    call.enqueue(new Callback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            if (response.isSuccessful()) {
                                ObjetoRes resObj = (ObjetoRes) response.body();
                                if (resObj.geterror().equals("false")) {
                                    String token = "";
                                    String sql = "SELECT * FROM usuario";
                                    Cursor recordUss = db.rawQuery(sql, null);

                                    if (recordUss.moveToFirst()) {
                                        token = recordUss.getString(recordUss.getColumnIndex("token"));
                                    }

                                    BASEURL = strIP + "glpservices/webresources/glpservices/";
                                    Retrofit retrofit = new Retrofit.Builder()
                                            .baseUrl(BASEURL)
                                            .addConverterFactory(GsonConverterFactory.create())
                                            .build();

                                    sql = "SELECT * FROM " + SQLiteDBHelper.Productos_Mod_Table + " WHERE oid = '" + resObj.geteraseoid() + "'";
                                    Cursor record = db.rawQuery(sql, null);
                                    if (record.getCount() > 1) {
                                        ServicioUsuario service = retrofit.create(ServicioUsuario.class);
                                        for (record.moveToFirst(); !record.isAfterLast(); record.moveToNext()) {
                                            String prueba = record.getString(record.getColumnIndex("surtido"));
                                            if (prueba.equals("1")) {
                                                call = service.up_detalle(record.getString(record.getColumnIndex("oid")),
                                                        Integer.parseInt(record.getString(record.getColumnIndex("cantidad"))), true,
                                                        Integer.parseInt(record.getString(record.getColumnIndex("precio"))),
                                                        token);
                                            } else {
                                                call = service.up_detalle(record.getString(record.getColumnIndex("oid")),
                                                        Integer.parseInt(record.getString(record.getColumnIndex("cantidad"))), false,
                                                        Integer.parseInt(record.getString(record.getColumnIndex("precio"))),
                                                        token);
                                            }

                                            call.enqueue(new Callback() {
                                                @Override
                                                public void onResponse(Call call, Response response) {
                                                    if (response.isSuccessful()) {
                                                        ObjetoRes resObj = (ObjetoRes) response.body();
                                                        if (resObj.geterror().equals("false")) {
                                                            db.delete(SQLiteDBHelper.Productos_Mod_Table, "oid = ?", new String[]{resObj.geteraseoid()});
                                                            db.delete(SQLiteDBHelper.Productos_Mod_Table, "oid = ?", new String[]{resObj.getMessage()});
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call call, Throwable t) {
                                                }
                                            });
                                        }
                                    } else {
                                        db.delete(SQLiteDBHelper.Productos_Mod_Table, "oid = ?", new String[]{resObj.geteraseoid()});
                                        db.delete(SQLiteDBHelper.Productos_Mod_Table, "oid = ?", new String[]{resObj.getMessage()});
                                    }
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call call, Throwable t) {

                        }
                    });
                }else {
                    Call call = service.up_detalle(record.getString(record.getColumnIndex("oid")),
                                             Integer.parseInt(record.getString(record.getColumnIndex("cantidad"))), Boolean.parseBoolean(record.getString(record.getColumnIndex("surtido"))),
                                             Double.parseDouble(record.getString(record.getColumnIndex("precio"))),
                                     token);
                    call.enqueue(new Callback() {
                                         @Override
                                         public void onResponse(Call call, Response response) {
                                             if (response.isSuccessful()) {
                                                 ObjetoRes resObj = (ObjetoRes) response.body();
                                                 if (resObj.geterror().equals("false")) {
                                                     db.delete(SQLiteDBHelper.Productos_Mod_Table, "oid = ?", new String[]{resObj.getMessage()});
                                                 }
                                             }
                                         }

                                         @Override
                        public void onFailure(Call call, Throwable t) {

                        }
                    });
                }
                j++;
            }
        }

        sql = "SELECT * FROM pedidos_modificados";
        record = db.rawQuery(sql, null);
        if(record.getCount()>0){
            for (record.moveToFirst(); !record.isAfterLast(); record.moveToNext()) {

            }
        }
    }

    public void actualizarPedidosSurtidos() {
        BASEURL = strIP + "glpservices/webresources/glpservices/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final ServicioUsuario service = retrofit.create(ServicioUsuario.class);
        Call call;

        boolean admin = false, flag = false;
        try {
            do {
                sqLiteDBHelper = new SQLiteDBHelper(getContext());
                if (sqLiteDBHelper.getWritableDatabase() == null) {
                    flag = false;
                } else {
                    SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();

                    String sql3 = "SELECT * FROM usuario";

                    Cursor cursor3 = db.rawQuery(sql3, null);

                    if (cursor3.moveToFirst()) {
                        strchofer = cursor3.getString(cursor3.getColumnIndex("oid"));
                        strtoken = cursor3.getString(cursor3.getColumnIndex("token"));
                    }

                    String sqlPedido = "SELECT * FROM pedidos WHERE surtido = '1'";

                    Cursor cursor = db.rawQuery(sqlPedido, null);

                    Pedido[] pedidos = new Pedido[cursor.getCount()];
                    int i = 0;
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        String sqlProducto = "SELECT * FROM productos WHERE pedido = '" + cursor.getString(cursor.getColumnIndex("oid")) + "'";
                        Cursor cursorPr = db.rawQuery(sqlProducto, null);
                        if (cursorPr.getCount() > 0) {
                            Producto[] productos = new Producto[cursorPr.getCount()];
                            int j = 0;
                            for (cursorPr.moveToFirst(); !cursorPr.isAfterLast(); cursorPr.moveToNext()) {
                                Producto producto = new Producto(cursorPr.getString(cursorPr.getColumnIndex("oid")),
                                        Integer.parseInt(cursorPr.getString(cursorPr.getColumnIndex("cantidad"))),
                                        Boolean.parseBoolean(cursorPr.getString(cursorPr.getColumnIndex("surtido"))),
                                        Double.parseDouble(cursorPr.getString(cursorPr.getColumnIndex("precio"))),
                                        cursorPr.getString(cursorPr.getColumnIndex("descripcion")),
                                        cursorPr.getString(cursorPr.getColumnIndex("pedido")));
                                if (producto != null) {
                                    productos[j] = producto;
                                }
                                j++;
                            }
                            Pedido pedido = new Pedido(cursor.getString(cursor.getColumnIndex("oid")),
                                    "",
                                    "",
                                    cursor.getString(cursor.getColumnIndex("fecha_hora_programada")),
                                    cursor.getString(cursor.getColumnIndex("cliente")),
                                    cursor.getString(cursor.getColumnIndex("direccion")),
                                    cursor.getString(cursor.getColumnIndex("cp")),
                                    cursor.getString(cursor.getColumnIndex("telefono")),
                                    cursor.getString(cursor.getColumnIndex("comentario_cliente")),
                                    cursor.getString(cursor.getColumnIndex("suma_iva")),
                                    cursor.getString(cursor.getColumnIndex("total")),
                                    cursor.getString(cursor.getColumnIndex("tipo_pedido")),
                                    cursor.getString(cursor.getColumnIndex("estatus")), productos,
                                    cursor.getString(cursor.getColumnIndex("lat")),
                                    cursor.getString(cursor.getColumnIndex("lon")),
                                    cursor.getString(cursor.getColumnIndex("empresa")),
                                    cursor.getString(cursor.getColumnIndex("forma_pago")));
                            if (pedido != null) {
                                pedidos[i] = pedido;
                            }
                            i++;
                        } else {
                            Pedido pedido = new Pedido(cursor.getString(cursor.getColumnIndex("oid")),
                                    "",
                                    "",
                                    cursor.getString(cursor.getColumnIndex("fecha_hora_programada")),
                                    cursor.getString(cursor.getColumnIndex("cliente")),
                                    cursor.getString(cursor.getColumnIndex("direccion")),
                                    cursor.getString(cursor.getColumnIndex("cp")),
                                    cursor.getString(cursor.getColumnIndex("telefono")),
                                    cursor.getString(cursor.getColumnIndex("comentario_cliente")),
                                    cursor.getString(cursor.getColumnIndex("suma_iva")),
                                    cursor.getString(cursor.getColumnIndex("total")),
                                    cursor.getString(cursor.getColumnIndex("tipo_pedido")),
                                    cursor.getString(cursor.getColumnIndex("estatus")), null,
                                    cursor.getString(cursor.getColumnIndex("lat")),
                                    cursor.getString(cursor.getColumnIndex("lon")),
                                    cursor.getString(cursor.getColumnIndex("empresa")),
                                    cursor.getString(cursor.getColumnIndex("forma_pago")));
                            if (pedido != null) {
                                pedidos[i] = pedido;
                            }
                            i++;
                        }
                    }

                    cursor.close();

                    adapter = new PedidoAdapter(Arrays.asList(pedidos), getActivity(), getFragmentManager());
                    recyclerViewPedidos.setAdapter(adapter);
                    flag = true;
                }
            } while (flag == false);
        }catch (Exception e){
        }
    }

        @Override
    public void onLocationChanged(Location location) {
        // locationText.setText("Current Location: " + location.getLatitude() + ", " + location.getLongitude());
        strLatitude = String.valueOf(location.getLatitude());
        strLongitude = String.valueOf(location.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getActivity(), "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}