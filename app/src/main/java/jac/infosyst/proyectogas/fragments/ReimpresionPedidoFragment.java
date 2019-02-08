package jac.infosyst.proyectogas.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import jac.infosyst.proyectogas.ImpresoraBluetooth.Impresora;
import jac.infosyst.proyectogas.R;
import jac.infosyst.proyectogas.utils.Sessions;


@SuppressLint("ValidFragment")
public class ReimpresionPedidoFragment  extends Fragment {

    private Context mCtx;
    TextView tvCanNombreOperador, tvCanDireccion, tvCanDescripcion, tvCanEstatus, tvCanTotal;
    Button btnReimprimirPedido;

    public ReimpresionPedidoFragment(Context mCtx) {
        this.mCtx = mCtx;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reimpresion_pedido, container, false);

        tvCanNombreOperador = (TextView) rootView.findViewById(R.id.tvCanNombreOperador);
        tvCanDireccion = (TextView) rootView.findViewById(R.id.tvCanDireccion);
        tvCanDescripcion = (TextView) rootView.findViewById(R.id.tvCanDescripcion);
        tvCanEstatus = (TextView) rootView.findViewById(R.id.tvCanEstatus);
        tvCanTotal = (TextView) rootView.findViewById(R.id.tvCanTotal);

        tvCanNombreOperador.setText("Nombre: " + ((Sessions)getActivity().getApplicationContext()).getSesCliente());
        tvCanDireccion.setText("Direccion: " + ((Sessions)getActivity().getApplicationContext()).getsesDireccion());
        tvCanDescripcion.setText("Descripcion: " + ((Sessions)getActivity().getApplicationContext()).getsesDescripcion());
        tvCanEstatus.setText("Estatus: " + ((Sessions)getActivity().getApplicationContext()).getsesEstatus());
        tvCanTotal.setText("Total: " + ((Sessions)getActivity().getApplicationContext()).getsesTotal());

        btnReimprimirPedido = (Button) rootView.findViewById(R.id.btnReimprimirPedido);
        btnReimprimirPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reimprimirPedido();
            }
        });

        return  rootView;
    }

    public void reimprimirPedido(){
        Intent intent = new Intent(getActivity(), Impresora.class);
        startActivity(intent);
        Toast.makeText(getActivity(), "Reimprimiendo Pedido" , Toast.LENGTH_SHORT).show();


            }
        });



        return  rootView;


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
