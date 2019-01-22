package jac.infosyst.proyectogas.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    public CancelarPedidoFragment(Context mCtx) {
        this.mCtx = mCtx;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        /*
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiUtils.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServicioUsuario service = retrofit.create(ServicioUsuario.class);

        Call<MotivoCancelacion> call = service.getMotivosCancelacion();

        call.enqueue(new Callback<MotivoCancelacion>() {
            @Override
            public void onResponse(Call<MotivoCancelacion> call, Response<MotivoCancelacion> response) {
*/




               // adapter = new PedidoAdapter(response.body().getPedidos(), getActivity(), getFragmentManager() );



              //  ArrayAdapter<MotivoCancelacion>
                //        adapter = new ArrayAdapter<MotivoCancelacion>(response.body().getMotivosCancelacion(), android.R.layout.simple_spinner_item, motivos);


                // adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

                //spinner.setAdapter(adapter);

                //recyclerViewPedidos.setAdapter(adapter);


          /*  }

            @Override
            public void onFailure(Call<MotivoCancelacion> call, Throwable t) {

            }

        });
        */


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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiUtils.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServicioUsuario service = retrofit.create(ServicioUsuario.class);

        Call<Spinners> call = service.obtenerMotivosCancelacion();

       // Toast.makeText(getActivity(), "Pedido por producto" + strIdPedido, Toast.LENGTH_SHORT).show();

        final List<String> formatedTimes = new ArrayList<>();


        call.enqueue(new Callback<Spinners>() {

            @Override
            public void onResponse(Call<Spinners> call, Response<Spinners> response) {
                if(response.isSuccessful()) {
                    Spinners resSpinners = (Spinners) response.body();
                    Toast.makeText(getActivity(), "resSpinners! " + resSpinners.getmotivoscancelacion(), Toast.LENGTH_SHORT).show();
                    ArrayList<jac.infosyst.proyectogas.modelo.Spinner> latLngData = new ArrayList<jac.infosyst.proyectogas.modelo.Spinner>();
                    latLngData.addAll(Arrays.asList(resSpinners.getmotivoscancelacion()));
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

                    // Toast.makeText(LoginActivity.this, "Bienvenido!" + resObj.getuser(), Toast.LENGTH_SHORT).show();
                    // Toast.makeText(LoginActivity.this, "Bienvenido!" + latLngData, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(), "Motivo! " + text, Toast.LENGTH_SHORT).show();



                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
                    spinner.setAdapter(adapter);
                }

                else {
                    Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
                }


               // adapter = new ProductoAdapter(response.body().getspinners(), getActivity(),  getFragmentManager());


            }

            @Override
            public void onFailure(Call<Spinners> call, Throwable t) {

            }
        });



        /*
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiUtils.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServicioUsuario service = retrofit.create(ServicioUsuario.class);


        ServicioUsuario api = retrofit.create(ServicioUsuario.class);
        api.obtenerMotivosCancelacion(new Callback<JsonArray>() {
            @Override
            public void success(JsonArray jsonElements, Response response) {
                try{

                    for (int i = 0; i < jsonElements.size(); i++) {

                        JsonObject jsonObject= jsonElements.get(i).getAsJsonObject();
                        int year =  jsonObject.get("releaseYear").getAsInt();
                        integerList.add(String.valueOf(year));
                    }

                }catch (JsonIOException  e){

                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(MainActivity.this, ""+error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(
                this,android.R.layout.simple_spinner_item,integerList);
        getSpin.setAdapter(stringArrayAdapter);

*/




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
