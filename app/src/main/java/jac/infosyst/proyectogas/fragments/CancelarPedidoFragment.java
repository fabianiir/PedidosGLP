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
import android.util.DisplayMetrics;
import android.view.Gravity;
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
import android.widget.PopupWindow;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jac.infosyst.proyectogas.modelo.Spinners;


@SuppressLint("ValidFragment")

public class CancelarPedidoFragment  extends Fragment {

    TextView tvCanNombreOperador, tvCanDireccion, tvCanDescripcion;
    EditText textViewObservaciones;
    Button btnAceptarCancelarPedido;
    String idPedido;
    String oidC = "";
    private Context mCtx;

    ModeloSpinner spinnerMod;
    Spinner spinner;

    File directory,directoryIncidencia;

    private SQLiteDBHelper sqLiteDBHelper = null;
    private PopupWindow POPUP_WINDOW_CONFIRMACION = null;
    View layout;


    String strObservaciones = "";
    String strchofer = "";
    String strtoken = "";
    String strIp = "";

    ArrayList<String> list = new ArrayList<String>();
    ArrayList<String> idlist = new ArrayList<String>();

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

        MainActivity.setFragmentController(1);

        ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
        directory = cw.getDir("firmas", Context.MODE_PRIVATE);
        directoryIncidencia = cw.getDir("incidencias", Context.MODE_PRIVATE);

        sqLiteDBHelper = new SQLiteDBHelper(getActivity());

        final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();

        String sql = "SELECT * FROM usuario";

        SQLiteDatabase dbConn3 = sqLiteDBHelper.getWritableDatabase();

        Cursor cursor3 = dbConn3.rawQuery(sql, null);

        if (cursor3.moveToFirst()) {
            strchofer = cursor3.getString(cursor3.getColumnIndex("oid"));
            strtoken = cursor3.getString(cursor3.getColumnIndex("token"));
        }

        sql = "SELECT * FROM configuracion";

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            strIp = cursor.getString(cursor.getColumnIndex("ip"));
        }

        tvCanNombreOperador = (TextView) rootView.findViewById(R.id.tvCanNombreOperador);
        tvCanDireccion = (TextView) rootView.findViewById(R.id.tvCanDireccion);
        tvCanDescripcion = (TextView) rootView.findViewById(R.id.tvCanDescripcion);

        tvCanNombreOperador.setText(Html.fromHtml("<b>Nombre: </b>"+ ((Sessions)getActivity().getApplicationContext()).getSesCliente()));
        tvCanDireccion.setText(Html.fromHtml("<b>Direccion: </b>" + ((Sessions)getActivity().getApplicationContext()).getsesDireccion()));
        String descripcion = ((Sessions)getActivity().getApplicationContext()).getsesDescripcion();
        if(descripcion == null){
            tvCanDescripcion.setVisibility(View.GONE);
        }else if(descripcion.isEmpty()){
            tvCanDescripcion.setVisibility(View.GONE);
        }else{
            tvCanDescripcion.setVisibility(View.VISIBLE);
        }
        tvCanDescripcion.setText(Html.fromHtml("<b>Descripcion: </b>" + ((Sessions)getActivity().getApplicationContext()).getsesDescripcion()));

        idPedido = ((Sessions)getActivity().getApplicationContext()).getSesIdPedido();

        textViewObservaciones = (EditText) rootView.findViewById(R.id.textViewObservaciones);
        strObservaciones = textViewObservaciones.getText().toString();

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
                LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                layout = layoutInflater.inflate(R.layout.layout_popup, null);

                DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
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
                txtMessage.setText("¿Desea cancelar éste pedido?");

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
                        POPUP_WINDOW_CONFIRMACION.dismiss();
                        cancelarPedido(idPedido);
                    }
                });
            }
        });

        return rootView;
    }

    public void cancelarPedido(final String idPedido){
        long Mot_Canc = spinner.getSelectedItemId();
        String oid = "";
        for (int i = 0; i < list.size(); i++){
            if(Mot_Canc == i){
                oid = idlist.get(i);
                oidC = idlist.get(i);
            }
        }

        String BASEURL = strIp + "glpservices/webresources/glpservices/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final ServicioUsuario userService = retrofit.create(ServicioUsuario.class);
        
        Call call = userService.up_pedido(idPedido, "Fecha", "Hora", "comentario_cliente", "comentario_chofer",
                "Comentario Null", "Comentario Null", 0, 0, "Pago Null", oid,
                Estatus.getCanceladoId(), "Up_2", strtoken);

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                sqLiteDBHelper = new SQLiteDBHelper(getContext());
                final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();

                db.delete(SQLiteDBHelper.Pedidos_Table, "oid = ?", new String[] {idPedido});

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
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                String hora = timeFormat.format(calendar.getTime());

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                java.util.Date date = new Date();
                String fecha = dateFormat.format(date);

                sqLiteDBHelper = new SQLiteDBHelper(getContext());
                final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put("oid", idPedido);
                values.put("hora", hora);
                values.put("fecha", fecha);
                values.put("comentario_chofer", strObservaciones);
                values.put("suma_iva", ((Sessions)getActivity().getApplicationContext()).getsessumaiva());
                values.put("pago_id",  "");
                values.put("motivo_cancelacion_id", oidC);
                values.put("estatus_id", Estatus.getCanceladoId());
                values.put("firma", "");
                values.put("foto_fuga", "");
                values.put("clave", "Up_2");
                db.insert(SQLiteDBHelper.Pedidos_Mod_Table, null, values);

                db.delete(SQLiteDBHelper.Pedidos_Table, "oid = ?", new String[] {idPedido});

                Toast.makeText(getActivity(), "Pedido Cancelado", Toast.LENGTH_SHORT).show();
                PedidosFragment spf = new PedidosFragment();

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, spf);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    public void llenarMotivosCancelacion(){

        final String[] letra = {"Cliente Ausente","Datos erroneos","Servicio realziado por otro proveedor"};
        final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();
        String sqlPedido = "SELECT * FROM cat_motivo_cancelacion";

        Cursor cursor = db.rawQuery(sqlPedido, null);

        jac.infosyst.proyectogas.modelo.Spinner[] spinners = new jac.infosyst.proyectogas.modelo.Spinner[cursor.getCount()];
        int j = 0;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            jac.infosyst.proyectogas.modelo.Spinner spinner =
                    new jac.infosyst.proyectogas.modelo.Spinner(cursor.getString(cursor.getColumnIndex("oid")),
                            cursor.getString(cursor.getColumnIndex("nombre")));
            if (spinner != null) {
                spinners[j] = spinner;
            }
            j++;
        }

        cursor.close();

                        ArrayList<jac.infosyst.proyectogas.modelo.Spinner> latLngData = new ArrayList<jac.infosyst.proyectogas.modelo.Spinner>();
                        latLngData.addAll(Arrays.asList(spinners));

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
