package jac.infosyst.proyectogas.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jac.infosyst.proyectogas.R;
import jac.infosyst.proyectogas.utils.Sessions;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;

import org.w3c.dom.Text;

@SuppressLint("ValidFragment")

public class CancelarPedidoFragment  extends Fragment {


    TextView tvCanNombreOperador, tvCanDireccion, tvCanDescripcion;
    EditText textViewObservaciones;
    Button btnAceptarCancelarPedido;
    int idPedido;
    private Context mCtx;

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

        Spinner spinner = (Spinner) rootView.findViewById(R.id.spinnerMotivoCancelacion);
        String[] letra = {"Cliente Ausente","Datos erroneos","Servicio realziado por otro proveedor"};
        spinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, letra));

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

    public void cancelarPedido(int idPedido){

        Toast.makeText(getActivity(), "Cancelando Pedido: " + idPedido , Toast.LENGTH_SHORT).show();


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
