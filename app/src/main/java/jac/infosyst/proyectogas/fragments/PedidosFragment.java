package jac.infosyst.proyectogas.fragments;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import jac.infosyst.proyectogas.R;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jac.infosyst.proyectogas.modelo.ObjetoRes;
import jac.infosyst.proyectogas.modelo.Chofer;
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
    private RecyclerView recyclerViewPedidos;
    private RecyclerView.Adapter adapter;

    private PedidoAdapter pedidoAdapter;

    private ArrayList<String> pedidoList = new ArrayList<>();

    int tiempoActualizarPedidos = 30000;
    int tipoPedidos, idPedido;
    String strtext;
    Button btnAtenderPedido, btnCancelarPedido, btnImprimirPedido;
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
    String strcamion= Chofer.getCamion();
    String strimei=Chofer.getImei();

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




       /*
        final Handler handler = new Handler();


        final Runnable r = new Runnable() {
            public void run() {
                seguimiento();
                handler.postDelayed(this, tiempoSeguimiento);
            }
        };

        handler.postDelayed(r, tiempoSeguimiento);
        */



        Sessions strSess = new Sessions();
        sqLiteDBHelper = new SQLiteDBHelper(getActivity(), DB_NAME, null, DB_VERSION);
        final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();


        String sql = "SELECT * FROM config WHERE id = 1 ORDER BY id DESC limit 1";

        final int recordCount = db.rawQuery(sql, null).getCount();
        //  Toast.makeText(getActivity(), "count:" + recordCount, Toast.LENGTH_SHORT).show();


        final Cursor record = db.rawQuery(sql, null);

        if (record.moveToFirst()) {
            strIP = record.getString(record.getColumnIndex("ip"));

        }



        sqLiteDBHelper = new SQLiteDBHelper(getActivity(), DB_NAME, null, DB_VERSION);

        final SQLiteDatabase db3 = sqLiteDBHelper.getWritableDatabase();


        String sql3 = "SELECT * FROM usuario ORDER BY id DESC limit 1";


        final int recordCount3 = db.rawQuery(sql3, null).getCount();
        //  Toast.makeText(getActivity(), "CONTADOR PEDIDOS: " + recordCount3, Toast.LENGTH_LONG).show();

        SQLiteDatabase dbConn3 = sqLiteDBHelper.getWritableDatabase();

        Cursor cursor3 = dbConn3.rawQuery(sql3, null);

        if (cursor3.moveToFirst()) {
            strchofer = cursor3.getString(cursor3.getColumnIndex("Oid"));
            strtoken = cursor3.getString(cursor3.getColumnIndex("token"));
            //  Toast.makeText(getActivity(), "usuario: " + strchofer + strtoken , Toast.LENGTH_LONG).show();


        }




      /*
        if (record2.moveToFirst()) {

            strchofer = record2.getString(record.getColumnIndex("Oid"));
            strtoken = record2.getString(record.getColumnIndex("token"));
        }
        */




        objSessions = new Sessions();
        userService = ApiUtils.getUserService();


        recyclerViewPedidos = (RecyclerView) rootView.findViewById(R.id.recyclerViewPedidos);
        recyclerViewPedidos.setHasFixedSize(true);
        recyclerViewPedidos.setLayoutManager(new LinearLayoutManager(getActivity()));


        /*
        final Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                actualizarPedidos();
                handler.postDelayed(this, tiempoActualizarPedidos);
            }
        };

        handler.postDelayed(r, tiempoActualizarPedidos);
        */

        //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Home");
        // getActivity().setTitle("your title");
/*
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null)
        {
            strtext = this.getArguments().getString("tipoPedidos");
        }
        */

        // String strtext = this.getArguments().getString("tipoPedidos");

        //Toast.makeText(getActivity(),  strNameTittle , Toast.LENGTH_SHORT).show();


        //idPedido =  ((Sessions)getActivity().getApplicationContext()).getSesIdPedido();


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
        btnCancelarPedido = (Button) rootView.findViewById(R.id.btnCancelarPedido);
        btnCancelarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelarPedidoFragment cpf = new CancelarPedidoFragment(getActivity().getBaseContext());
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction =        fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, cpf);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        btnImprimirPedido = (Button) rootView.findViewById(R.id.btnImprimirPedido);
        btnImprimirPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReimpresionPedidoFragment rpf = new ReimpresionPedidoFragment(getActivity().getBaseContext());
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction =        fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, rpf);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        String strNameTittle = String.valueOf(((AppCompatActivity)getActivity()).getSupportActionBar().getTitle());

        if(strNameTittle.equals("Pedidos")){
            btnAtenderPedido.setVisibility(View.VISIBLE);
            btnCancelarPedido.setVisibility(View.VISIBLE);
            btnImprimirPedido.setVisibility(View.GONE);
            tipoPedidos = 0;

            Toast.makeText(getActivity(), "Actualizando pedidos..." , Toast.LENGTH_SHORT).show();

        }
        if(strNameTittle.equals("Pedidos Realizados")){
            btnAtenderPedido.setVisibility(View.GONE);
            btnCancelarPedido.setVisibility(View.GONE);
            btnImprimirPedido.setVisibility(View.VISIBLE);
            tipoPedidos = 1;

            Toast.makeText(getActivity(), "Actualizando pedidos realizados..." , Toast.LENGTH_SHORT).show();

        }

        actualizarPedidos();
        //  actualizarPedidos2();
        // obtenerDatosUsuario();


        return rootView;
    }

    public void actualizarPedidos(){
/*
        sqLiteDBHelper = new SQLiteDBHelper(getActivity(), DB_NAME, null, DB_VERSION);
        final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();

        String sql = "SELECT * FROM config ORDER BY id DESC limit 1";

        final int recordCount = db.rawQuery(sql, null).getCount();
        //  Toast.makeText(getApplicationContext(), "contador: " + recordCount, Toast.LENGTH_LONG).show();

        SQLiteDatabase dbConn = sqLiteDBHelper.getWritableDatabase();

        Cursor cursor = dbConn.rawQuery(sql, null);
        String email="";
        String checkEmpty = "";
        if (cursor.moveToFirst()) {

            int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
            String firstname = cursor.getString(cursor.getColumnIndex("status"));
            email = cursor.getString(cursor.getColumnIndex("ip"));
            // Toast.makeText(getApplicationContext(), "datos: " + email, Toast.LENGTH_LONG).show();

        }
        cursor.close();
*/


        BASEURL = "http://"+ strIP+ ":8060/glpservices/webresources/glpservices/";
        final String[] strReturnToken = new String[1];
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServicioUsuario service = retrofit.create(ServicioUsuario.class);

        Call call = service.bitacora(true, strimei, strchofer,  strcamion, null);

        if (strtoken == null) {
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    if (response.isSuccessful()) {
                        ObjetoRes obj_bitacora = (ObjetoRes) response.body();
                        if (obj_bitacora.geterror().equals("false")) {

                            sqLiteDBHelper = new SQLiteDBHelper(getActivity(), DB_NAME, null, DB_VERSION);
                            final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();

                            ContentValues values1 = new ContentValues();

                            values1.put("Oid", strchofer);
                            values1.put("token", obj_bitacora.gettoken());

                            db.insert("usuario", null, values1);

                            call = userService.getPedidos(strchofer, "Pendiente", obj_bitacora.gettoken());

                            //  Call call = userService.getPedidos("255abae2-a6ed-43de-8aa3-b637f3490b8a", "Cancelado", "8342d5e8-1fa7-4e86-890d-763eb5a7a193");
                            call.enqueue(new Callback() {
                                @Override
                                public void onResponse(Call call, Response response) {
                                    if (response.isSuccessful()) {
                                        ObjetoRes resObj = (ObjetoRes) response.body();

                                        if (resObj.geterror().equals("false")) {

                                            if (resObj.getpedido() != null) {
                                                Toast.makeText(getActivity(), " != null", Toast.LENGTH_SHORT).show();
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
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {

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

                        if (resObj.geterror().equals("false")) {

                            if (resObj.getpedido() != null) {
                                Toast.makeText(getActivity(), " != null", Toast.LENGTH_SHORT).show();
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


/*
    public void actualizarPedidos2(){

        Call call = userService.getPedidos("Yo", "Pendiente", "c6861e99-0069-4ced-b8dd-549a124f87d5");
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    ObjetoRes resObj = (ObjetoRes) response.body();

                    // Toast.makeText(LoginActivity.this, "Respuesta: " + resObj.geterror(), Toast.LENGTH_SHORT).show();

                    if(resObj.geterror().equals("false")){

                        Toast.makeText(getActivity(), "Respuesta: " +  resObj.getMessage(), Toast.LENGTH_SHORT).show();


                    } else {
                        Toast.makeText(getActivity(), "Respuesta: " +  resObj.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                else {
                    Toast.makeText(getActivity(), "Error: ", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getActivity(), "onFailure: " , Toast.LENGTH_SHORT).show();

            }
        });

    }

    */

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


            //   isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            // strLatitude = String.valueOf(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude());
            // strLongitude = String.valueOf(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude());



        }
        catch(SecurityException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error de  GPS!", Toast.LENGTH_SHORT).show();

        }


    }




    public void callSeguimiento(){
        Toast.makeText(getActivity(), "Latitude: " + strLongitude + " Longitude:"  + strLongitude , Toast.LENGTH_SHORT).show();

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