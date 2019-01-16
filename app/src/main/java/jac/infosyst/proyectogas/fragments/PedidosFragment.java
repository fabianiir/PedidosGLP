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


import jac.infosyst.proyectogas.R;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jac.infosyst.proyectogas.modelo.Pedido;
import jac.infosyst.proyectogas.utils.ServicioUsuario;
import jac.infosyst.proyectogas.utils.ApiUtils;
import jac.infosyst.proyectogas.adaptadores.PedidoAdapter;
import jac.infosyst.proyectogas.modelo.Pedidos;

import jac.infosyst.proyectogas.utils.Sessions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;




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





        actualizarPedidos(tipoPedidos);

        return rootView;
    }

    public void actualizarPedidos(int tipoPedidos){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiUtils.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServicioUsuario service = retrofit.create(ServicioUsuario.class);

        Call<Pedidos> call = service.getPedidos(tipoPedidos);

        call.enqueue(new Callback<Pedidos>() {
            @Override
            public void onResponse(Call<Pedidos> call, Response<Pedidos> response) {
                adapter = new PedidoAdapter(response.body().getPedidos(), getActivity(), getFragmentManager() );
                recyclerViewPedidos.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<Pedidos> call, Throwable t) {

            }

        });


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

