package jac.infosyst.proyectogas.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import jac.infosyst.proyectogas.Configuracion;
import jac.infosyst.proyectogas.LoginActivity;
import jac.infosyst.proyectogas.R;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import jac.infosyst.proyectogas.modelo.ObjetoRes;
import jac.infosyst.proyectogas.modelo.Pedido;
import jac.infosyst.proyectogas.modelo.Spinners;
import jac.infosyst.proyectogas.utils.SQLiteDBHelper;
import jac.infosyst.proyectogas.utils.ServicioUsuario;
import jac.infosyst.proyectogas.utils.ApiUtils;
import jac.infosyst.proyectogas.adaptadores.PedidoAdapter;
import jac.infosyst.proyectogas.modelo.Pedidos;

import jac.infosyst.proyectogas.utils.Sessions;
//import jac.infosyst.proyectogas.vista.Escaner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.Cursor;
import android.util.Log;


public class PedidosFragment extends Fragment{
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
    private String DB_NAME = "proyectogas.db";
    private int DB_VERSION = 1;
    private String TABLE_NAME = "usuarios";
    ServicioUsuario userService;

    private String BASEURL = "";
    Sessions objSessions;

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
        Sessions strSess = new Sessions();
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
                DetallePedidoFragment dpf = new DetallePedidoFragment(getActivity().getBaseContext());
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction =        fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, dpf);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
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


        BASEURL = "http://"+ objSessions.getSesstrIpServidor()+ ":8060/glpservices/webresources/glpservices/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServicioUsuario service = retrofit.create(ServicioUsuario.class);

        Call call = userService.getPedidos("Yo", "Pendiente", "c6861e99-0069-4ced-b8dd-549a124f87d5");
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    ObjetoRes resObj = (ObjetoRes) response.body();

                        if(resObj.geterror().equals("false")) {


                            Toast.makeText(getActivity(), "mensaje! " + resObj.getpedido(), Toast.LENGTH_SHORT).show();
                                adapter = new PedidoAdapter(Arrays.asList(resObj.getpedido()), getActivity(),  getFragmentManager());
                                recyclerViewPedidos.setAdapter(adapter);

                    } else {
                            Toast.makeText(getActivity(), "no datos! " , Toast.LENGTH_SHORT).show();

                        }
                }

                else {
                    Toast.makeText(getActivity(), "error! " , Toast.LENGTH_SHORT).show();

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


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}

