package jac.infosyst.proyectogas.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jac.infosyst.proyectogas.Configuracion;
import jac.infosyst.proyectogas.LectorQR.Escaner;
import jac.infosyst.proyectogas.LoginActivity;
import jac.infosyst.proyectogas.MainActivity;
import jac.infosyst.proyectogas.R;
import jac.infosyst.proyectogas.adaptadores.ProductoAdapter;
import jac.infosyst.proyectogas.modelo.CatalagoProducto;
import jac.infosyst.proyectogas.modelo.Chofer;
import jac.infosyst.proyectogas.modelo.Estatus;
import jac.infosyst.proyectogas.modelo.ModeloSpinner;
import jac.infosyst.proyectogas.modelo.ObjetoRes;
import jac.infosyst.proyectogas.modelo.Pedido;
import jac.infosyst.proyectogas.modelo.Producto;
import jac.infosyst.proyectogas.modelo.Productos;
import jac.infosyst.proyectogas.modelo.Usuario;
import jac.infosyst.proyectogas.utils.SQLiteDBHelper;
import jac.infosyst.proyectogas.utils.ServicioUsuario;
import jac.infosyst.proyectogas.utils.Sessions;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;

import com.github.gcacace.signaturepad.views.SignaturePad;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.File;
import java.text.SimpleDateFormat;
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

    File directory,directoryIncidencia;
    ImageView imgFirma;
    ImageView firmaImage, imageViewIncidencia;

    private ProgressDialog dialog;

    private String BASEURL = "";
    String strIP = "";
    private SQLiteDBHelper sqLiteDBHelper = null;
    private String DB_NAME = "proyectogas17.db";
    private int DB_VERSION = 1;
    String strchofer = "";
    String strtoken = "";

    ArrayList<String> list = new ArrayList<String>();
    ArrayList<String> idlist = new ArrayList<String>();

    public CancelarPedidoFragment(Context mCtx) {
        this.mCtx = mCtx;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        synchronized (userService = userService) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cancelar_pedidos, container, false);

        MainActivity.setFragmentController(1);

        ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
        directory = cw.getDir("firmas", Context.MODE_PRIVATE);
        directoryIncidencia = cw.getDir("incidencias", Context.MODE_PRIVATE);

        userService = userService;
        dialog = new ProgressDialog(getActivity());

        sqLiteDBHelper = new SQLiteDBHelper(getActivity());

        final SQLiteDatabase db3 = sqLiteDBHelper.getWritableDatabase();

        String sql3 = "SELECT * FROM usuario ORDER BY id DESC limit 1";

        SQLiteDatabase dbConn3 = sqLiteDBHelper.getWritableDatabase();

        Cursor cursor3 = dbConn3.rawQuery(sql3, null);

        if (cursor3.moveToFirst()) {
            strchofer = cursor3.getString(cursor3.getColumnIndex("Oid"));
            strtoken = cursor3.getString(cursor3.getColumnIndex("token"));

        }

        tvCanNombreOperador = (TextView) rootView.findViewById(R.id.tvCanNombreOperador);
        tvCanDireccion = (TextView) rootView.findViewById(R.id.tvCanDireccion);
        tvCanDescripcion = (TextView) rootView.findViewById(R.id.tvCanDescripcion);

        tvCanNombreOperador.setText(Html.fromHtml("<b>Nombre: </b>"+ ((Sessions)getActivity().getApplicationContext()).getSesCliente()));
        tvCanDireccion.setText(Html.fromHtml("<b>Direccion: </b>" + ((Sessions)getActivity().getApplicationContext()).getsesDireccion()));
        tvCanDescripcion.setText(Html.fromHtml("<b>Descripcion: </b>" + ((Sessions)getActivity().getApplicationContext()).getsesDescripcion()));

        idPedido = ((Sessions)getActivity().getApplicationContext()).getSesIdPedido();

        textViewObservaciones = (EditText) rootView.findViewById(R.id.textViewObservaciones);

        spinner = (Spinner) rootView.findViewById(R.id.spinnerMotivoCancelacion);
        llenarMotivosCancelacion();

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id)
            {
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
        long Mot_Canc = spinner.getSelectedItemId();
        Toast.makeText(getActivity(), "Cancelando Pedido: " + idPedido , Toast.LENGTH_SHORT).show();
        String oid = "";
        for (int i = 0; i < list.size(); i++){
            if(Mot_Canc == i){
                oid = idlist.get(i);
            }
        }
        
        Call call = userService.up_pedido(idPedido, "Fecha", "Hora", "comentario_cliente", "comentario_chofer",
                "Comentario Null", "Comentario Null", 0, 0, "Pago Null", oid,
                Estatus.getCanceladoId(), "Up_2", strtoken);

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Toast.makeText(getActivity(), "Pedido Cancelado", Toast.LENGTH_SHORT).show();
                PedidosFragment spf = new PedidosFragment();

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, spf);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }

    public void llenarMotivosCancelacion(){

        final String[] letra = {"Cliente Ausente","Datos erroneos","Servicio realziado por otro proveedor"};
       // spinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, letra));

        Call call = userService.obtenerMotivosCancelacion(strtoken);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    ObjetoRes resObj = (ObjetoRes) response.body();
                    if(resObj.geterror().equals("false")){
                        ArrayList<jac.infosyst.proyectogas.modelo.Spinner> latLngData = new ArrayList<jac.infosyst.proyectogas.modelo.Spinner>();
                        latLngData.addAll(Arrays.asList(resObj.getmotivoscancelacion()));

                        for (int i = 0; i < latLngData.size(); i++) {
                            String lat = latLngData.get(i).getnombre();
                            list.add(lat);
                            String lon = latLngData.get(i).getoid();
                            idlist.add(lon);
                        }
                        StringBuilder builder = new StringBuilder();
                        for (String value : list) {
                            builder.append(value);
                        }
                        String text = builder.toString();

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, list);
                        spinner.setAdapter(adapter);
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
