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


import java.io.IOException;
import java.util.Calendar;

import jac.infosyst.proyectogas.MainActivity;
import jac.infosyst.proyectogas.R;
import jac.infosyst.proyectogas.modelo.UsuarioInfo;
import jac.infosyst.proyectogas.utils.Sessions;

import static jac.infosyst.proyectogas.fragments.SurtirPedidoFragment.simpleDateFormatFecha;


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


        final String nombCliente= ((Sessions)getActivity().getApplicationContext()).getSesCliente();
        final String direcCliente = ((Sessions)getActivity().getApplicationContext()).getsesDireccion();
        final String totalCliente = ((Sessions)getActivity().getApplicationContext()).getsesTotal();
        Calendar calendar = Calendar.getInstance();
        final String fecha = String.valueOf(simpleDateFormatFecha.format(calendar.getTime()));



        tvCanNombreOperador.setText("Nombre: " + nombCliente);
        tvCanDireccion.setText("Direccion: " + direcCliente);

        tvCanDescripcion.setText("Descripcion: " + ((Sessions)getActivity().getApplicationContext()).getsesDescripcion());
        tvCanEstatus.setText("Estatus: " + ((Sessions)getActivity().getApplicationContext()).getsesEstatus());
        tvCanTotal.setText("Total: " + totalCliente);

        btnReimprimirPedido = (Button) rootView.findViewById(R.id.btnReimprimirPedido);
        btnReimprimirPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MainActivity.printData(nombCliente,direcCliente,totalCliente, UsuarioInfo.getNombre(),UsuarioInfo.getPlacas(),fecha);
                } catch (IOException e) {
                    e.printStackTrace();
                }

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
