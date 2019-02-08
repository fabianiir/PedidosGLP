package jac.infosyst.proyectogas.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jac.infosyst.proyectogas.Configuracion;
import jac.infosyst.proyectogas.LectorQR.Escaner;
import jac.infosyst.proyectogas.LoginActivity;
import jac.infosyst.proyectogas.R;
import jac.infosyst.proyectogas.adaptadores.ProductoAdapter;
import jac.infosyst.proyectogas.modelo.ModeloSpinner;
import jac.infosyst.proyectogas.modelo.ObjetoRes;
import jac.infosyst.proyectogas.modelo.Productos;
import jac.infosyst.proyectogas.modelo.Usuario;
import jac.infosyst.proyectogas.utils.ApiUtils;
import jac.infosyst.proyectogas.utils.ServicioUsuario;
import jac.infosyst.proyectogas.utils.Sessions;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jac.infosyst.proyectogas.modelo.Spinners;


@SuppressLint("ValidFragment")

public class CancelarPedidoFragment  extends Fragment {

    TextView tvCanNombreOperador, tvCanDireccion, tvCanDescripcion;
    EditText textViewObservaciones;
    Button btnAceptarCancelarPedido;
    String idPedido;
    private Context mCtx;

    ModeloSpinner spinnerMod;
    Spinner spinner;
    ServicioUsuario userService;

    public CancelarPedidoFragment(Context mCtx) {
        this.mCtx = mCtx;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userService = ApiUtils.getUserService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cancelar_pedidos, container, false);

        tvCanNombreOperador = (TextView) rootView.findViewById(R.id.tvCanNombreOperador);
        tvCanDireccion = (TextView) rootView.findViewById(R.id.tvCanDireccion);
        tvCanDescripcion = (TextView) rootView.findViewById(R.id.tvCanDescripcion);

        tvCanNombreOperador.setText("Nombre: " + ((Sessions)getActivity().getApplicationContext()).getSesCliente());
        tvCanDireccion.setText("Direccion: " + ((Sessions)getActivity().getApplicationContext()).getsesDireccion());
        tvCanDescripcion.setText("Descripcion: " + ((Sessions)getActivity().getApplicationContext()).getsesDescripcion());

        idPedido = ((Sessions)getActivity().getApplicationContext()).getSesIdPedido();

        textViewObservaciones = (EditText) rootView.findViewById(R.id.textViewObservaciones);

        spinner = (Spinner) rootView.findViewById(R.id.spinnerMotivoCancelacion);
        llenarMotivosCancelacion();

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id)
            {
                Toast.makeText(adapterView.getContext(),
                        (String) adapterView.getItemAtPosition(pos), Toast.LENGTH_SHORT).show();
            }
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });

        btnAceptarCancelarPedido = (Button) rootView.findViewById(R.id.btnAceptarCancelarPedido);
        btnAceptarCancelarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelarPedido(idPedido);
            }
        });

        return rootView;
    }

    public void cancelarPedido(String idPedido){

        Toast.makeText(getActivity(), "Cancelando Pedido: " + idPedido , Toast.LENGTH_SHORT).show();
    }


    public void llenarMotivosCancelacion(){

        final String[] letra = {"Cliente Ausente","Datos erroneos","Servicio realziado por otro proveedor"};
       // spinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, letra));

        Call call = userService.obtenerMotivosCancelacion("c6861e99-0069-4ced-b8dd-549a124f87d5");
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    ObjetoRes resObj = (ObjetoRes) response.body();
                    // Toast.makeText(LoginActivity.this, "Respuesta: " + resObj.geterror(), Toast.LENGTH_SHORT).show();
                    if(resObj.geterror().equals("false")){
                        ArrayList<jac.infosyst.proyectogas.modelo.Spinner> latLngData = new ArrayList<jac.infosyst.proyectogas.modelo.Spinner>();
                        latLngData.addAll(Arrays.asList(resObj.getmotivoscancelacion()));
                        ArrayList<String> list = new ArrayList<String>();

                        for (int i = 0; i < latLngData.size(); i++) {
                            String lat = latLngData.get(i).getnombre();
                            list.add(lat);
                        }
                        StringBuilder builder = new StringBuilder();
                        for (String value : list) {
                            builder.append(value);
                        }
                        String text = builder.toString();

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
                        spinner.setAdapter(adapter);

                        Toast.makeText(getActivity(), "Respuesta: " + resObj.getmotivoscancelacion(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), resObj.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getActivity(), "Error! Intenta Nuevamente", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
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
