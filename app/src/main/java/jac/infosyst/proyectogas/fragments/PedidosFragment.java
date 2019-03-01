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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import jac.infosyst.proyectogas.FragmentDrawer;
import jac.infosyst.proyectogas.LectorQR.Escaner;
import jac.infosyst.proyectogas.LoginActivity;
import jac.infosyst.proyectogas.MainActivity;
import jac.infosyst.proyectogas.R;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jac.infosyst.proyectogas.modelo.Camion;
import jac.infosyst.proyectogas.modelo.CatalogoEstatus;
import jac.infosyst.proyectogas.modelo.Estatus;
import jac.infosyst.proyectogas.modelo.ObjetoRes;
import jac.infosyst.proyectogas.modelo.Chofer;
import jac.infosyst.proyectogas.modelo.ObjetoRes3;
import jac.infosyst.proyectogas.modelo.Pedido;
import jac.infosyst.proyectogas.modelo.Usuario;
import jac.infosyst.proyectogas.modelo.UsuarioInfo;
import jac.infosyst.proyectogas.utils.SQLiteDBHelper;
import jac.infosyst.proyectogas.utils.ServicioUsuario;
import jac.infosyst.proyectogas.utils.ApiUtils;
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

    private SQLiteDBHelper sqLiteDBHelper = null;
    private String DB_NAME = "proyectogas17.db";
    private int DB_VERSION = 1;
    private String TABLE_NAME = "usuarios";
    ServicioUsuario userService;

    private String BASEURL = "";
    Sessions objSessions;
    String strIP = "";
    String strchofer = "";
    String strtoken = "";
    String strcamion = Chofer.getCamion();
    String strimei = Chofer.getImei();
    String strcamionid;

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

        Sessions strSess = new Sessions();
        sqLiteDBHelper = new SQLiteDBHelper(getActivity(), DB_NAME, null, DB_VERSION);
        final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();

        String sql = "SELECT * FROM config WHERE id = 1 ORDER BY id DESC limit 1";

        final int recordCount = db.rawQuery(sql, null).getCount();
        final Cursor record = db.rawQuery(sql, null);

        if (record.moveToFirst()) {
            strIP = record.getString(record.getColumnIndex("ip"));
        }

        sqLiteDBHelper = new SQLiteDBHelper(getActivity(), DB_NAME, null, DB_VERSION);

        final SQLiteDatabase db3 = sqLiteDBHelper.getWritableDatabase();


        String sql3 = "SELECT * FROM usuario ORDER BY id DESC limit 1";


        final int recordCount3 = db.rawQuery(sql3, null).getCount();
        SQLiteDatabase dbConn3 = sqLiteDBHelper.getWritableDatabase();
        Cursor cursor3 = dbConn3.rawQuery(sql3, null);

        if (cursor3.moveToFirst()) {
            strchofer = cursor3.getString(cursor3.getColumnIndex("Oid"));
            strtoken = cursor3.getString(cursor3.getColumnIndex("token"));
        }
        objSessions = new Sessions();
        userService = ApiUtils.getUserService();

        swipeRefreshLayout= (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                actualizarPedidosPendientes();

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
                ReimpresionPedidoFragment rpf = new ReimpresionPedidoFragment(getActivity().getBaseContext());
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, rpf);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        String strNameTittle = String.valueOf(((AppCompatActivity)getActivity()).getSupportActionBar().getTitle());

        if(strNameTittle.equals("Pedidos")){
            btnAtenderPedido.setVisibility(View.VISIBLE);

            btnImprimirPedido.setVisibility(View.GONE);
            tipoPedidos = 0;

            Toast.makeText(getActivity(), "Actualizando pedidos..." , Toast.LENGTH_SHORT).show();
        }
        if(strNameTittle.equals("Pedidos Realizados")){
            btnAtenderPedido.setVisibility(View.GONE);

            btnImprimirPedido.setVisibility(View.VISIBLE);
            tipoPedidos = 1;

            Toast.makeText(getActivity(), "Actualizando pedidos realizados..." , Toast.LENGTH_SHORT).show();
        }

        if (tipoPedidos == 0){
            actualizarPedidosPendientes();
        }else if(tipoPedidos == 1){
            actualizarPedidosSurtidos();
        }
        return rootView;
    }

    public void actualizarPedidosPendientes(){
        BASEURL = strIP+ "glpservices/webresources/glpservices/";
        final String[] strReturnToken = new String[1];
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final ServicioUsuario service = retrofit.create(ServicioUsuario.class);
        Call call;

        call = service.getCatalogoEstatus(strtoken);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    ObjetoRes3 obj_estatus = (ObjetoRes3) response.body();
                    if (obj_estatus.geterror().equals("false")) {
                        List<CatalogoEstatus> arrayListEstatus = Arrays.asList(obj_estatus.getCatalogoEstatus());
                        Estatus estatus = new Estatus();
                        for (int i = 0; i < arrayListEstatus.size(); i ++){
                            if(arrayListEstatus.get(i).getdescripcion().equals("Pendiente")){
                                estatus.setPendienteId(arrayListEstatus.get(i).getIdProducto());
                            }else if(arrayListEstatus.get(i).getdescripcion().equals("Surtido")){
                                estatus.setSurtidoId(arrayListEstatus.get(i).getIdProducto());
                            }else if(arrayListEstatus.get(i).getdescripcion().equals("Cancelado")){
                                estatus.setCanceladoId(arrayListEstatus.get(i).getIdProducto());
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });

        if (strtoken == null) {
            call = service.camion(Integer.parseInt(strcamion));
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    if (response.isSuccessful()) {
                        ObjetoRes obj_camion = (ObjetoRes) response.body();
                        if (obj_camion.geterror().equals("false")) {
                            List<Camion> arrayListCamion = Arrays.asList(obj_camion.getcamion());
                            UsuarioInfo uss = new UsuarioInfo();
                            uss.setPlacas(arrayListCamion.get(0).getplacas());
                            strcamionid = arrayListCamion.get(0).getId();
                            call = service.bitacora(true, strimei, strchofer, arrayListCamion.get(0).getId(), null);
                            call.enqueue(new Callback() {
                                @Override
                                public void onResponse(Call call, Response response) {
                                    if (response.isSuccessful()) {
                                        ObjetoRes obj_bitacora = (ObjetoRes) response.body();
                                        if (obj_bitacora.geterror().equals("false")) {

                                            ((Sessions)getActivity().getApplicationContext()).setStrImei(strimei);
                                            ((Sessions)getActivity().getApplicationContext()).setStrChoferId(strchofer);
                                            ((Sessions)getActivity().getApplicationContext()).setStrCamionId(strcamionid);
                                            ((Sessions)getActivity().getApplicationContext()).setsessToken(obj_bitacora.gettoken());

                                            sqLiteDBHelper = new SQLiteDBHelper(getActivity(), DB_NAME, null, DB_VERSION);
                                            final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();

                                            ContentValues values1 = new ContentValues();

                                            values1.put("Oid", strchofer);
                                            values1.put("token", obj_bitacora.gettoken());

                                            Intent intent = new Intent(getActivity(), MainActivity.class);
                                            startActivity(intent);

                                            db.insert("usuario", null, values1);

                                            call = userService.getPedidos(strchofer, "Pendiente", obj_bitacora.gettoken());
                                            call.enqueue(new Callback() {
                                                @Override
                                                public void onResponse(Call call, Response response) {
                                                    if (response.isSuccessful()) {
                                                        ObjetoRes resObj = (ObjetoRes) response.body();

                                                        if (resObj.geterror().equals("false")) {

                                                            if (resObj.getpedido() != null) {
                                                                adapter = new PedidoAdapter(Arrays.asList(resObj.getpedido()), getActivity(), getFragmentManager());
                                                                recyclerViewPedidos.setAdapter(adapter);
                                                            } else {
                                                                Toast.makeText(getActivity(), "No existen Pedidos!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        } else {
                                                            Toast.makeText(getActivity(), "no datos!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } else {
                                                        Toast.makeText(getActivity(), "error! ", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                                @Override
                                                public void onFailure(Call call, Throwable t) {
                                                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }else{
                                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                            builder.setMessage("IMEI no registrado, se cerrará la aplicación")
                                                    .setCancelable(false)
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            intent.putExtra("EXIT", true);
                                                            startActivity(intent);
                                                        }
                                                    });
                                            AlertDialog alert = builder.create();
                                            alert.show();
                                        }
                                    }else{
                                        Toast.makeText(getActivity(), "response.success.bitacora!", Toast.LENGTH_SHORT).show();

                                    }
                                }

                                @Override
                                public void onFailure(Call call, Throwable t) {
                                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            Toast.makeText(getActivity(), "camion.error.true!", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                    }
                }
                @Override
                public void onFailure(Call call, Throwable t) {
                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            call = userService.getPedidos(strchofer, "Pendiente", strtoken);
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    if (response.isSuccessful()) {
                        ObjetoRes resObj = (ObjetoRes) response.body();
                        if(resObj != null) {
                            if (resObj.geterror().equals("false")) {

                                if (resObj.getpedido() != null) {
                                    adapter = new PedidoAdapter(Arrays.asList(resObj.getpedido()), getActivity(), getFragmentManager());
                                    recyclerViewPedidos.setAdapter(adapter);
                                    MainActivity.setConexionEstablecida(true);

                                } else {
                                    Toast.makeText(getActivity(), "No existen Pedidos!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), " no hay datos!" + strchofer + "/" + strtoken, Toast.LENGTH_SHORT).show();
                                MainActivity.setConexionEstablecida(false);
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), "error! ", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call call, Throwable t) {
                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    MainActivity.setConexionEstablecida(false);
                }
            });
        }
    }

    public void actualizarPedidosSurtidos() {


        BASEURL = strIP + "glpservices/webresources/glpservices/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Call call;

        call = userService.getPedidos(strchofer, "Surtido", strtoken);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    ObjetoRes resObj = (ObjetoRes) response.body();

                    if (resObj.geterror().equals("false")) {

                        if (resObj.getpedido() != null) {
                            adapter = new PedidoAdapter(Arrays.asList(resObj.getpedido()), getActivity(), getFragmentManager());
                            recyclerViewPedidos.setAdapter(adapter);

                        } else {
                            Toast.makeText(getActivity(), "No existen Pedidos!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "no datos!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "error! ", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void obtenerDatosUsuario(){
        sqLiteDBHelper = new SQLiteDBHelper(getActivity(), DB_NAME, null, DB_VERSION);

        if(sqLiteDBHelper!=null) {
            // Create the database tables again, this time because database version increased so the onUpgrade() method is invoked.
            SQLiteDatabase sqLiteDatabase = sqLiteDBHelper.getWritableDatabase();
            Cursor cursor = sqLiteDatabase.query(SQLiteDBHelper.USUARIOS_TABLE_NAME, null, null, null, null, null, null);

            boolean hasRecord = cursor.moveToFirst();
            if(hasRecord)
            {
                do{
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    String nombre = cursor.getString(cursor.getColumnIndex("nombre"));

                    StringBuffer bookInfoBuf = new StringBuffer();
                    bookInfoBuf.append("book id : ");
                    bookInfoBuf.append(id);
                    bookInfoBuf.append(" , Nombre : ");
                    bookInfoBuf.append(nombre);

                    // Log.d(SQLiteDBHelper.LOG_TAG_SQLITE_DB, bookInfoBuf.toString());

                }while(cursor.moveToNext());
            }
            String name = "Login Correcto";
            Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM usuarios WHERE TRIM(nombre) = '"+name.trim()+"'", null);
            boolean hasRecord2 = c.moveToFirst();
            if(hasRecord2)
            {
                do{
                    int id = c.getInt(c.getColumnIndex("id"));
                    String nombre = c.getString(c.getColumnIndex("nombre"));

                    StringBuffer bookInfoBuf = new StringBuffer();
                    bookInfoBuf.append("book id IOS: ");
                    bookInfoBuf.append(id);
                    bookInfoBuf.append(" , Nombre S.O.MOVIL2 : ");
                    bookInfoBuf.append(nombre);

                    Log.d(SQLiteDBHelper.LOG_TAG_SQLITE_DB, bookInfoBuf.toString());

                }while(c.moveToNext());
            }
            Toast.makeText(getActivity(), "Look at android monitor console to see the query result.", Toast.LENGTH_LONG).show();
        }else
        {
            Toast.makeText(getActivity(), "Please create database first.", Toast.LENGTH_LONG).show();
        }
    }

    public void seguimiento(){
        getLocation();
    }

    public void getLocation(){

        try {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null){
                callSeguimiento();
            }else{
                Toast.makeText(getActivity(), "Error de  GPS!", Toast.LENGTH_SHORT).show();

            }
        }
        catch(SecurityException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error de  GPS!", Toast.LENGTH_SHORT).show();
        }
    }

    public void callSeguimiento(){
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