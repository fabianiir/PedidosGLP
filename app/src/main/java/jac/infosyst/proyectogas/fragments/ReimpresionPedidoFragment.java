package jac.infosyst.proyectogas.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import jac.infosyst.proyectogas.utils.SQLiteDBHelper;
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

        MainActivity.setFragmentController(1);

        tvCanNombreOperador = (TextView) rootView.findViewById(R.id.tvCanNombreOperador);
        tvCanDireccion = (TextView) rootView.findViewById(R.id.tvCanDireccion);
        tvCanDescripcion = (TextView) rootView.findViewById(R.id.tvCanDescripcion);
        tvCanEstatus = (TextView) rootView.findViewById(R.id.tvCanEstatus);
        tvCanTotal = (TextView) rootView.findViewById(R.id.tvCanTotal);


        final String nombCliente= ((Sessions)getActivity().getApplicationContext()).getSesCliente();
        final String direcCliente = ((Sessions)getActivity().getApplicationContext()).getsesDireccion();
        final  String descripCliente=  ((Sessions)getActivity().getApplicationContext()).getsesDescripcion();
        final  String estatusCliente = ((Sessions)getActivity().getApplicationContext()).getsesEstatus();
        final String totalCliente = ((Sessions)getActivity().getApplicationContext()).getsesTotal();
        Calendar calendar = Calendar.getInstance();
        final String fecha = String.valueOf(simpleDateFormatFecha.format(calendar.getTime()));

if(nombCliente==null)
{
    tvCanNombreOperador.setVisibility(View.GONE);
}
if(direcCliente==null)
{
    tvCanDireccion.setVisibility(View.GONE);
}
if (descripCliente==null)
{
    tvCanDescripcion.setVisibility(View.GONE);
}
if (estatusCliente==null)
{
    tvCanEstatus.setVisibility(View.GONE);
}


        tvCanNombreOperador.setText("Nombre: " + nombCliente);
        tvCanDireccion.setText("Direccion: " + direcCliente);
        tvCanDescripcion.setText("Descripción" + descripCliente);
        tvCanDescripcion.setText("Descripcion: " + estatusCliente);
        tvCanEstatus.setText("Estatus: " + estatusCliente);
        tvCanTotal.setText("Total: " + totalCliente);

        btnReimprimirPedido = (Button) rootView.findViewById(R.id.btnReimprimirPedido);
        btnReimprimirPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    String placas = "", nombre = "";

                    SQLiteDBHelper sqLiteDBHelper = new SQLiteDBHelper(getContext());
                    SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();
                    String sql = "SELECT * FROM usuario";
                    Cursor record = db.rawQuery(sql, null);

                    if (record.moveToFirst()) {
                        nombre = record.getString(record.getColumnIndex("nombre"));
                        placas = record.getString(record.getColumnIndex("placas"));
                    }
                    MainActivity.printData(nombCliente,direcCliente,totalCliente,nombre,placas,fecha,true);
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
