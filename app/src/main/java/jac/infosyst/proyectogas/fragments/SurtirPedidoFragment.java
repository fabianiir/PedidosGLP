package jac.infosyst.proyectogas.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import jac.infosyst.proyectogas.R;
import jac.infosyst.proyectogas.adaptadores.PedidoAdapter;
import jac.infosyst.proyectogas.adaptadores.ProductoAdapter;
import jac.infosyst.proyectogas.modelo.Pedido;
import jac.infosyst.proyectogas.modelo.Pedidos;
import jac.infosyst.proyectogas.modelo.Producto;
import jac.infosyst.proyectogas.modelo.Productos;

import jac.infosyst.proyectogas.utils.ApiUtils;
import jac.infosyst.proyectogas.utils.ServicioUsuario;
import jac.infosyst.proyectogas.utils.Sessions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SurtirPedidoFragment  extends Fragment {
    private TextView textViewCliente, textViewDireccion, textViewDescripcion, textViewEstatus, textViewDetalle
            , textViewFirma, textViewTotal;

    Button btnFirmar, btnGuardar, btnReimpresionTicket;
    private PopupWindow POPUP_WINDOW_CONFIRMACION = null;
    View layout;
    LayoutInflater layoutInflater;
    int strIdPedido;

    private RecyclerView recyclerViewProductos;
    private RecyclerView.Adapter adapter;

    private ProductoAdapter productoAdapter;

    private ArrayList<String> productoList = new ArrayList<>();


    public SurtirPedidoFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_surtir_pedido, container, false);

        strIdPedido = ((Sessions)getActivity().getApplication()).getSesIdPedido();
        String strCliente = ((Sessions)getActivity().getApplication()).getSesCliente();
        String strDireccion = ((Sessions)getActivity().getApplication()).getsesDireccion();
        String strDescripcion = ((Sessions)getActivity().getApplication()).getsesDescripcion();
        final String strDetalle = ((Sessions)getActivity().getApplication()).getsesDetalleProducto();
        String strEstatus = ((Sessions)getActivity().getApplication()).getsesEstatus();
        String strFirma = ((Sessions)getActivity().getApplication()).getsesFirmaURL();
        String strTotal = ((Sessions)getActivity().getApplication()).getsesTotal();
        textViewCliente = (TextView) rootView.findViewById(R.id.tvCliente);
        textViewCliente.setText("Nombre: " + strCliente);
        textViewDireccion = (TextView) rootView.findViewById(R.id.tvDireccion);
        textViewDireccion.setText("Direccion: " + strDireccion);
        textViewDescripcion = (TextView) rootView.findViewById(R.id.tvDescripcion);
        textViewDescripcion.setText("Descripcion: " + strDescripcion);
        textViewEstatus = (TextView) rootView.findViewById(R.id.tvEstatus);
        textViewEstatus.setText("Estatus: " + strEstatus);
        textViewDetalle = (TextView) rootView.findViewById(R.id.tvDetalle);
        textViewDetalle.setText("Detalle Producto: ");
        textViewFirma = (TextView) rootView.findViewById(R.id.tvFirma);
        textViewFirma.setText("Firma: " + strFirma);
        textViewTotal = (TextView) rootView.findViewById(R.id.tvTotal);
        textViewTotal.setText("Total: " + strTotal);
        btnGuardar = (Button)rootView.findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarConfirmacion("Confirmacion "+ strDetalle.toString()+ " Confirmar?");


            }
        });
        btnReimpresionTicket = (Button)rootView.findViewById(R.id.btnReimpresionTicket);
        btnReimpresionTicket.setVisibility(View.GONE);
        btnReimpresionTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getActivity(), "Reimpimir ticket", Toast.LENGTH_SHORT).show();

            }
        });

        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = layoutInflater.inflate(R.layout.layout_popup, null);





        recyclerViewProductos = (RecyclerView) rootView.findViewById(R.id.recyclerViewProductos);
        recyclerViewProductos.setHasFixedSize(true);
        recyclerViewProductos.setLayoutManager(new LinearLayoutManager(getActivity()));
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiUtils.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServicioUsuario service = retrofit.create(ServicioUsuario.class);


        int strIdPedido = ((Sessions)getActivity().getApplication()).getSesIdPedido();

        Call<Productos> call = service.getProductos(strIdPedido);

        Toast.makeText(getActivity(), "Pedido por producto" + strIdPedido, Toast.LENGTH_SHORT).show();


        call.enqueue(new Callback<Productos>() {
            @Override
            public void onResponse(Call<Productos> call, Response<Productos> response) {
              //  adapter = new PedidoAdapter(response.body().getPedidos(), getActivity(), getFragmentManager() );
                //Toast.makeText(getActivity(), "CALL" + response.body().getPedidos(), Toast.LENGTH_SHORT).show();


                adapter = new ProductoAdapter(response.body().getProductos(), getActivity(),  getFragmentManager());
                recyclerViewProductos.setAdapter(adapter);

            }

            @Override
            public void onFailure(Call<Productos> call, Throwable t) {

            }

        });

        return rootView;

    }

    public void mostrarConfirmacion(String mensaje){
        Toast.makeText(getActivity(), "Pedido guardado!", Toast.LENGTH_SHORT).show();
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        layout.setVisibility(View.VISIBLE);
        POPUP_WINDOW_CONFIRMACION = new PopupWindow(getActivity());
        POPUP_WINDOW_CONFIRMACION.setContentView(layout);
        POPUP_WINDOW_CONFIRMACION.setWidth(width);
        POPUP_WINDOW_CONFIRMACION.setHeight(height);
        POPUP_WINDOW_CONFIRMACION.setFocusable(true);


        POPUP_WINDOW_CONFIRMACION.setBackgroundDrawable(null);

        POPUP_WINDOW_CONFIRMACION.showAtLocation(layout, Gravity.CENTER, 1, 1);

        TextView txtMessage = (TextView) layout.findViewById(R.id.layout_popup_txtMessage);
        txtMessage.setText(mensaje);

        Button btnSurtirPedidoNo = (Button) layout.findViewById(R.id.btnSurtirPedidoNo);
        btnSurtirPedidoNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                POPUP_WINDOW_CONFIRMACION.dismiss();
            }
        });


        Button btnSurtirPedidoSi = (Button) layout.findViewById(R.id.btnSurtirPedidoSi);
        btnSurtirPedidoSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedidoConfirmado(strIdPedido);
                Toast.makeText(getActivity(), "SI guardado!", Toast.LENGTH_SHORT).show();
                POPUP_WINDOW_CONFIRMACION.dismiss();
            }
        });
    }



    public void pedidoConfirmado(int idPedido){
        btnReimpresionTicket.setVisibility(View.VISIBLE);



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
