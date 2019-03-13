package jac.infosyst.proyectogas.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import jac.infosyst.proyectogas.LoginActivity;
import jac.infosyst.proyectogas.MainActivity;
import jac.infosyst.proyectogas.R;
import jac.infosyst.proyectogas.adaptadores.CatalagoProductosAdapter;
import jac.infosyst.proyectogas.adaptadores.ProductoAdapter;
import jac.infosyst.proyectogas.modelo.CatalagoProducto;
import jac.infosyst.proyectogas.modelo.Chofer;
import jac.infosyst.proyectogas.modelo.Estatus;
import jac.infosyst.proyectogas.modelo.Imagen;
import jac.infosyst.proyectogas.modelo.ObjetoRes;
import jac.infosyst.proyectogas.modelo.ObjetoRes2;
import jac.infosyst.proyectogas.modelo.Pedido;

import jac.infosyst.proyectogas.modelo.Producto;
import jac.infosyst.proyectogas.modelo.Productos;
import jac.infosyst.proyectogas.utils.RetrofitClient;
import jac.infosyst.proyectogas.utils.SQLiteDBHelper;
import jac.infosyst.proyectogas.utils.ServicioUsuario;
import jac.infosyst.proyectogas.utils.Sessions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.support.design.widget.FloatingActionButton;

import java.util.Calendar;
import java.util.Locale;

public class SurtirPedidoFragment  extends Fragment implements LocationListener {
    private TextView textViewCliente, textViewDireccion, textViewDescripcion, textViewEstatus, textViewDetalle, textViewFirma, textViewTotal;
    Button btnGuardar, btnReimpresionTicket, btnLimpiar;
    private PopupWindow POPUP_WINDOW_CONFIRMACION = null;
    private PopupWindow POPUP_WINDOW_CATALAGOPRODUCTOS = null;

    View layout, layoutCatalagoProductos;
    LayoutInflater layoutInflater, layoutInflaterCatalagoProductos;
    String strIdPedido;
    FloatingActionButton favplus;

    private RecyclerView recyclerViewProductos, recyclerViewCatalagoProductos, recyclerViewPedidosHide;
    private RecyclerView.Adapter adapter, adapterCatalago;

    private ProductoAdapter productoAdapter;

    private ArrayList<String> productoList = new ArrayList<>();

    SignaturePad signaturePad;
    /*foto incidencia*/
    private static final int PICTURE_RESULT = 122;
    private ContentValues values;
    private Uri imageUri;
    private Bitmap thumbnail;

    String imageurl;
    File directory, directoryIncidencia;
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
    ServicioUsuario userService;


    private static final String TAG = "SurtirPedidoFragment";

    private List<Pedido> pedidos;
    //   private List<Producto> productos;
    //

    // List<Producto> listAdapter;
    List<String> listAdapter;

    Producto myCustomProducto;
    CatalagoProducto myCatalagoProducto;


    //Variables para impresora
    String imprCliente = "";
    String imprDireccion="";
    String imprTotal="";
    String imprChofer="";
    String imprUnidad="";
    String strTotal;


    String strHora = "";
    String strFecha = "";
    String pedidoID = "";

    static SimpleDateFormat simpleDateFormatFecha = new SimpleDateFormat("dd-MM-yyyy");
    static SimpleDateFormat simpleDateFormatHora = new SimpleDateFormat("HH:mm:ss");
    LocationManager locationManager;
    String strLatitude = "";
    String strLongitude = "";
    Location location;
    FloatingActionButton fabAgregarProducto;

    String strGettoken = "";
    String strLocalIdPedido = "";
    String strcamion= Chofer.getCamion();
    String strimei=Chofer.getImei();

    String archivo = "";
    Bitmap decodedByte;

    public SurtirPedidoFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void threat(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getProductos(true);
                handler.postDelayed(this, 5000);
            }
        }, 1000);  //the time is in miliseconds
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_surtir_pedido, container, false);

        MainActivity.setFragmentController(2);

// path to /data/data/yourapp/app_data/imageDir
        ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
        directory = cw.getDir("firmas", Context.MODE_PRIVATE);
        directoryIncidencia = cw.getDir("incidencias", Context.MODE_PRIVATE);

        dialog = new ProgressDialog(getActivity());

        sqLiteDBHelper = new SQLiteDBHelper(getActivity());

        final SQLiteDatabase db3 = sqLiteDBHelper.getWritableDatabase();

        String sql3 = "SELECT * FROM usuario";

        SQLiteDatabase dbConn3 = sqLiteDBHelper.getWritableDatabase();

        Cursor cursor3 = dbConn3.rawQuery(sql3, null);

        if (cursor3.moveToFirst()) {
            strchofer = cursor3.getString(cursor3.getColumnIndex("oid"));
            strtoken = cursor3.getString(cursor3.getColumnIndex("token"));
        }

        //  strtoken
        ((Sessions)getActivity().getApplicationContext()).setsessToken(strtoken);
        String sql = "SELECT * FROM configuracion";

        final int recordCount = dbConn3.rawQuery(sql, null).getCount();

        final Cursor record = dbConn3.rawQuery(sql, null);

        if (record.moveToFirst()) {
            strIP = record.getString(record.getColumnIndex("ip"));
        }

        //    checkPedidoPendiente();

        strIdPedido = ((Sessions)getActivity().getApplication()).getSesIdPedido();
        String strCliente = ((Sessions)getActivity().getApplication()).getSesCliente();
        String strDireccion = ((Sessions)getActivity().getApplication()).getsesDireccion();
        String strDescripcion = ((Sessions)getActivity().getApplication()).getsesDescripcion();
        final String strDetalle = ((Sessions)getActivity().getApplication()).getsesDetalleProducto();
        String strEstatus = ((Sessions)getActivity().getApplication()).getsesEstatus();
        String strFirma = ((Sessions)getActivity().getApplication()).getsesFirmaURL();
        strTotal = ((Sessions)getActivity().getApplication()).getsesTotal();

        if(strCliente == null){
            strCliente = "N/A";
        }
        if(strDireccion == null){
            strDireccion = "N/A";
        }
        if(strDescripcion == null){
            strDescripcion = "N/A";
        }
        if(strEstatus == null){
            strEstatus = "N/A";
        }
        if(strDetalle == null){
            strDescripcion = "N/A";
        }
        if(strFirma == null){
            strFirma = "N/A";
        }
        if(strFirma == null){
            strFirma = "N/A";
        }
        if(strTotal == null){
            strTotal = "N/A";
        }

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
        btnGuardar = (Button)rootView.findViewById(R.id.btnGuardar);
        favplus = (FloatingActionButton) rootView.findViewById(R.id.fabAgregarProducto);

        //Asignacion de variables para impresora
        imprCliente = strCliente;
        imprDireccion = strDireccion;
        imprTotal = strTotal;
        imprChofer = "";
        imprUnidad = "";

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((Sessions) getActivity().getApplicationContext()).getSestipo_pedido().equals("Fuga")) {
                    if (thumbnail != null) {
                        if (!signaturePad.isEmpty()) {
                            mostrarConfirmacion("¿Desea Confirmar?");
                        } else {
                            Toast.makeText(getActivity(), "No existe una firma", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Se debe de tomar la foto de la fuga", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (strTotal != "0") {
                        sqLiteDBHelper = new SQLiteDBHelper(getContext());
                        SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();
                        String sqlValidación = "SELECT * FROM productos WHERE pedido = '" + pedidoID + "'";
                        Cursor cursorPr = db.rawQuery(sqlValidación, null);
                        if (cursorPr.getCount() > 0) {
                            if (!signaturePad.isEmpty()) {
                                mostrarConfirmacion("¿Desea Confirmar?");
                            } else {
                                Toast.makeText(getActivity(), "No existe una firma", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setMessage("Este pedido no contiene productos y no puede ser guardado")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();

                        }
                    }else{
                        Toast.makeText(getActivity(), "No puede surtir el pedido sy el total es \"0\" ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        btnReimpresionTicket = (Button)rootView.findViewById(R.id.btnReimpresionTicket);
        btnReimpresionTicket.setVisibility(View.GONE);
        btnReimpresionTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    String placas = "", nombre = "";

                    sqLiteDBHelper = new SQLiteDBHelper(getContext());
                    SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();
                    String sql = "SELECT * FROM usuario";
                    Cursor record = db.rawQuery(sql, null);

                    if (record.moveToFirst()) {
                        nombre = record.getString(record.getColumnIndex("nombre"));
                        placas = record.getString(record.getColumnIndex("placas"));
                    }

                    Toast.makeText(getActivity(), "Reimpimir ticket", Toast.LENGTH_SHORT).show();
                    MainActivity.printData(imprCliente,imprDireccion, String.valueOf(Double.parseDouble(strTotal) * 1.16), nombre, placas, strFecha,true);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("¿Se imprimio el ticket?");
                    builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            PedidosFragment spf = new PedidosFragment();

                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.container_body, spf);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                            btnReimpresionTicket.setVisibility(View.GONE);
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            btnReimpresionTicket.setVisibility(View.VISIBLE);
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();


                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        });

        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = layoutInflater.inflate(R.layout.layout_popup, null);

        LayoutInflater layoutInflaterCatalagoProductos = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutCatalagoProductos = layoutInflaterCatalagoProductos.inflate(R.layout.layout_popup_catalago_productos, null);

        recyclerViewProductos = (RecyclerView) rootView.findViewById(R.id.recyclerViewProductos);
        recyclerViewProductos.setHasFixedSize(true);
        recyclerViewProductos.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getProductos(true);
            }
        });

        btnLimpiar = (Button) rootView.findViewById(R.id.btnLimpiarFirmar);

        btnLimpiar.setEnabled(true);
        signaturePad = (SignaturePad) rootView.findViewById(R.id.signaturePad);
        signaturePad.setEnabled(true);
        signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {

            @Override
            public void onStartSigning() {
                //Event triggered when the pad is touched

            }

            @Override
            public void onSigned() {
                //Event triggered when the pad is signed
                // btnFirmar.setEnabled(true);
                btnLimpiar.setEnabled(true);
            }

            @Override
            public void onClear() {
                //Event triggered when the pad is cleared
                // btnFirmar.setEnabled(false);
                btnLimpiar.setEnabled(false);

            }
        });

        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signaturePad.clear();
            }
        });

        fabAgregarProducto = (FloatingActionButton) rootView.findViewById(R.id.fabAgregarProducto);
        fabAgregarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarCatalagoProductos("Productos");
            }
        });

        FloatingActionButton fabRestarProducto = (FloatingActionButton) rootView.findViewById(R.id.fabRestarProducto);
        fabRestarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String producto = ((Sessions) getActivity().getApplicationContext()).getSesOidProducto();
                if (producto != null) {
                    LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    layout = layoutInflater.inflate(R.layout.layout_popup, null);

                    DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
                    int width = displayMetrics.widthPixels;
                    int height = displayMetrics.heightPixels;

                    layout.setVisibility(View.VISIBLE);
                    POPUP_WINDOW_CONFIRMACION = new PopupWindow(getContext());
                    POPUP_WINDOW_CONFIRMACION.setContentView(layout);
                    POPUP_WINDOW_CONFIRMACION.setWidth(width);
                    POPUP_WINDOW_CONFIRMACION.setHeight(height);
                    POPUP_WINDOW_CONFIRMACION.setFocusable(true);

                    POPUP_WINDOW_CONFIRMACION.setBackgroundDrawable(null);

                    POPUP_WINDOW_CONFIRMACION.showAtLocation(layout, Gravity.CENTER, 1, 1);

                    TextView txtMessage = (TextView) layout.findViewById(R.id.layout_popup_txtMessage);
                    txtMessage.setText("¿Desea eliminar este producto?");

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
                            restarProducto(((Sessions) getActivity().getApplicationContext()).getSesOidProducto());
                            POPUP_WINDOW_CONFIRMACION.dismiss();
                        }
                    });

                }else {
                    Toast.makeText(getActivity(), "Debe de seleccionar un producto", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageViewIncidencia = (ImageView) rootView.findViewById(R.id.imageViewIncidencia);
        imageViewIncidencia.setVisibility(View.GONE);


        if(((Sessions)getActivity().getApplicationContext()).getSestipo_pedido().equals("Fuga")){
            // recyclerViewProductos.setVisibility(View.GONE);
            // fabAgregarProducto.setVisibility(View.GONE);

            imageViewIncidencia.setVisibility(View.VISIBLE);

            // signaturePad.setEnabled(false);
            // btnLimpiar.setEnabled(false);
            // btnGuardar.setEnabled(false);
        }


        imageViewIncidencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //Check permissions for Android 6.0+
                    if (!checkExternalStoragePermission()) {
                        return;
                    }
                }
                values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "incidencia" );
                values.put(MediaStore.Images.Media.DESCRIPTION, "tomada en: " + System.currentTimeMillis());
                imageUri = getActivity().getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, PICTURE_RESULT);

            }
        });

        getProductos(true);

        final String strDescripcion2 = ((Sessions) getActivity().getApplication()).getsesDescripcion();
        final String strIdPedido2 = ((Sessions) getActivity().getApplication()).getSesIdPedido();

        imgFirma=(ImageView)rootView.findViewById(R.id.imgFirma);
        // Inflate the layout for this fragment
        return rootView;
    }

    public void mostrarConfirmacion(String mensaje){
        getProductos(true);
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
                putImageFirma();
                dialog.setMax(10);
                dialog.setMessage("Actualizando Pedido....");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.show();
                pedidoActualizarSurtido();
                Toast.makeText(getActivity(), "Pedido Surtido Exitosamente!", Toast.LENGTH_SHORT).show();
                POPUP_WINDOW_CONFIRMACION.dismiss();
                try {
                    MainActivity.printData(imprCliente, imprDireccion, String.valueOf(Double.parseDouble(strTotal) * 1.16), imprChofer, imprUnidad, strFecha, false);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("¿Se imprimio el ticket?");
                    builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            PedidosFragment spf = new PedidosFragment();

                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.container_body, spf);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            btnReimpresionTicket.setVisibility(View.VISIBLE);
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void restarProducto(final String idProducto){
        int setcantidad = 0;
        boolean surtido = false;
        int setprecio = 0;
        String token = "";
        sqLiteDBHelper = new SQLiteDBHelper(getActivity());
        final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();

        String sql = "SELECT * FROM productos WHERE oid = '" + idProducto + "'";

        Cursor cursor = db.rawQuery(sql, null);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            setcantidad = Integer.parseInt(cursor.getString(cursor.getColumnIndex("cantidad")));
            setprecio = Integer.parseInt(cursor.getString(cursor.getColumnIndex("precio")));
        }
        final int cantidad = setcantidad;
        final int precio = setprecio;
        token = ((Sessions) getActivity().getApplicationContext()).getsessToken();

        sql = "SELECT * FROM configuracion";

        final Cursor record = db.rawQuery(sql, null);

        if (record.moveToFirst()) {
            strIP = record.getString(record.getColumnIndex("ip"));
        }

        BASEURL = strIP + "glpservices/webresources/glpservices/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServicioUsuario service = retrofit.create(ServicioUsuario.class);

        Call call = service.up_detalle(idProducto, cantidad, surtido,  precio, token);

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    ObjetoRes resObj = (ObjetoRes) response.body();
                    if(resObj.geterror().equals("false")) {
                        ContentValues values = new ContentValues();
                        values.put("surtido", 1);
                        db.update(SQLiteDBHelper.Productos_Mod_Table, values,"oid = ?", new String[]{idProducto});
                        db.delete(SQLiteDBHelper.Productos_Table, "oid = ?", new String[]{idProducto});
                        ((Sessions) getActivity().getApplicationContext()).setSesOidProducto(null);
                        getProductos(true);
                    } else {
                        Toast.makeText(getActivity(), resObj.getMessage()  , Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ContentValues values = new ContentValues();
                    values.put("surtido", 1);
                    db.update(SQLiteDBHelper.Productos_Mod_Table, values,"oid = ?", new String[]{idProducto});
                    db.delete(SQLiteDBHelper.Productos_Table, "oid = ?", new String[]{idProducto});
                    ((Sessions) getActivity().getApplicationContext()).setSesOidProducto(null);
                    getProductos(true);
                }
            }
            @Override
            public void onFailure(Call call, Throwable t) {
                sqLiteDBHelper = new SQLiteDBHelper(getActivity());
                SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("oid", idProducto);
                values.put("cantidad", cantidad);
                values.put("surtido", 0);
                values.put("precio", precio);
                db.insert(SQLiteDBHelper.Productos_Mod_Table, null, values);

                db.delete(SQLiteDBHelper.Productos_Table, "oid = ?", new String[]{idProducto});
                ((Sessions) getActivity().getApplicationContext()).setSesOidProducto(null);
                getProductos(true);
            }
        });
    }

    public void mostrarCatalagoProductos(String mensaje){
        getProductos(true);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        layoutCatalagoProductos.setVisibility(View.VISIBLE);
        POPUP_WINDOW_CATALAGOPRODUCTOS = new PopupWindow(getActivity());
        POPUP_WINDOW_CATALAGOPRODUCTOS.setContentView(layoutCatalagoProductos);
        POPUP_WINDOW_CATALAGOPRODUCTOS.setWidth(width);
        POPUP_WINDOW_CATALAGOPRODUCTOS.setHeight(height);
        POPUP_WINDOW_CATALAGOPRODUCTOS.setFocusable(true);

        POPUP_WINDOW_CATALAGOPRODUCTOS.setBackgroundDrawable(null);

        POPUP_WINDOW_CATALAGOPRODUCTOS.showAtLocation(layoutCatalagoProductos, Gravity.CENTER, 1, 1);

        TextView txtMessage = (TextView) layoutCatalagoProductos.findViewById(R.id.layout_popup_txtMessage);
        txtMessage.setText(mensaje);

        recyclerViewCatalagoProductos = (RecyclerView) layoutCatalagoProductos.findViewById(R.id.recyclerViewCatalagoProductos);
        recyclerViewCatalagoProductos.setHasFixedSize(true);
        recyclerViewCatalagoProductos.setLayoutManager(new LinearLayoutManager(getActivity()));

        /*section - getCatalagoProductos*/
        BASEURL = strIP + "glpservices/webresources/glpservices/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();

                String sql = "SELECT * FROM cat_productos";
                Cursor cursorPr = db.rawQuery(sql, null);

                CatalagoProducto[] catalagoProductos = new CatalagoProducto[cursorPr.getCount()];
                int j = 0;
                for (cursorPr.moveToFirst(); !cursorPr.isAfterLast(); cursorPr.moveToNext()) {
                    CatalagoProducto catalagoProducto = new CatalagoProducto(cursorPr.getString(cursorPr.getColumnIndex("oid")),
                            cursorPr.getString(cursorPr.getColumnIndex("descripcion")),
                            Double.parseDouble(cursorPr.getString(cursorPr.getColumnIndex("precio_unitario"))),
                            cursorPr.getString(cursorPr.getColumnIndex("unidad")));
                    if (catalagoProducto != null) {
                        catalagoProductos[j] = catalagoProducto;
                    }
                    j++;
                }

                adapterCatalago = new CatalagoProductosAdapter(Arrays.asList(catalagoProductos), getActivity(), getFragmentManager());
                recyclerViewCatalagoProductos.setAdapter(adapterCatalago);

        Button btnAgregarProductoNo = (Button) layoutCatalagoProductos.findViewById(R.id.btnAgregarProductoNo);
        btnAgregarProductoNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                POPUP_WINDOW_CATALAGOPRODUCTOS.dismiss();
                getProductos(true);
            }
        });
    }

    public void pedidoActualizarSurtido(){
        fabAgregarProducto.setEnabled(false);
        signaturePad.setEnabled(false);
        btnLimpiar.setEnabled(false);
        btnGuardar.setEnabled(false);
        favplus.setEnabled(false);
        imageViewIncidencia.setEnabled(false);
        getProductos(false);

        sqLiteDBHelper = new SQLiteDBHelper(getActivity());
        final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();

        String sql = "SELECT * FROM configuracion";

        final Cursor record = db.rawQuery(sql, null);

        if (record.moveToFirst()) {

            strIP = record.getString(record.getColumnIndex("ip"));
        }

        getHora();
        getFecha();
        //  getUbicacion();
        BASEURL = strIP + "glpservices/webresources/glpservices/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServicioUsuario service = retrofit.create(ServicioUsuario.class);

        Call call = service.up_pedido(pedidoID, strHora, strFecha,
                "comentario_cliente", "comentario_chofer", strLatitude, strLongitude,
                0, 0, "", "null",
                Estatus.getSurtidoId(), "Up_1", strtoken);

        call.enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) {

                if (response.isSuccessful()) {
                    ObjetoRes resObj = (ObjetoRes) response.body();
                    if (resObj.geterror().equals("false")) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                            db.delete(SQLiteDBHelper.Pedidos_Table, "oid = ?", new String[] {pedidoID});
                        }
                    }
                    if (resObj.geterror().equals("true")) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call call, Throwable t) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                    sqLiteDBHelper = new SQLiteDBHelper(getContext());
                    SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();

                    String sql = "SELECT * FROM pedidos_modificados WHERE oid = '" + pedidoID + "'";

                    Cursor record = db.rawQuery(sql, null);
                    int count = record.getCount();
                    if (count > 0) {
                        record.moveToFirst();
                        ContentValues values = new ContentValues();
                        values.put("oid", pedidoID);
                        values.put("hora", strHora);
                        values.put("fecha", strFecha);
                        values.put("comentario_chofer", record.getString(record.getColumnIndex("comentario_chofer")));
                        values.put("suma_iva", ((Sessions) getActivity().getApplicationContext()).getsessumaiva());
                        values.put("pago_id", record.getString(record.getColumnIndex("pago_id")));
                        values.put("motivo_cancelacion_id", record.getString(record.getColumnIndex("motivo_cancelacion_id")));
                        values.put("estatus_id", Estatus.getSurtidoId());
                        values.put("firma", record.getString(record.getColumnIndex("firma")));
                        values.put("foto_fuga", record.getString(record.getColumnIndex("foto_fuga")));
                        values.put("clave", "Up_1");
                        db.update(SQLiteDBHelper.Pedidos_Mod_Table, values, "oid = ?", new String[]{pedidoID});

                        db.delete(SQLiteDBHelper.Pedidos_Table, "oid = ?", new String[]{pedidoID});
                    }else {

                        ContentValues values = new ContentValues();
                        values.put("oid", pedidoID);
                        values.put("hora", strHora);
                        values.put("fecha", strFecha);
                        values.put("comentario_chofer", "");
                        values.put("suma_iva", ((Sessions) getActivity().getApplicationContext()).getsessumaiva());
                        values.put("pago_id", "");
                        values.put("motivo_cancelacion_id", "");
                        values.put("estatus_id", Estatus.getSurtidoId());
                        values.put("firma", "");
                        values.put("foto_fuga", "");
                        values.put("clave", "Up_1");
                        db.insert(SQLiteDBHelper.Pedidos_Mod_Table, null, values);

                        db.delete(SQLiteDBHelper.Pedidos_Table, "oid = ?", new String[]{pedidoID});
                    }
                }
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

    private String saveToInternalStorage(Bitmap bitmapImage){

        // Create imageDir
        File mypath=new File(directory,"firma.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    public void limpiarFirmaImagen(){
        try {
            File f=new File(directory , "firmaLimpia.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));

            imgFirma.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    /*foto incidencia*/
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICTURE_RESULT:
                if (requestCode == PICTURE_RESULT) {
                    if (resultCode == Activity.RESULT_OK) {
                        try {
                            thumbnail = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                            imageViewIncidencia.setImageBitmap(thumbnail);
                            imageurl = getRealPathFromURI(imageUri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private boolean checkExternalStoragePermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(
                getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission not granted.");
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 225);
        } else {
            Log.i(TAG, "You already have permission!");
            return true;
        }
        return false;
    }

    public void getHora(){
        Calendar calendar = Calendar.getInstance();
        strHora = String.valueOf(simpleDateFormatHora.format(calendar.getTime()));
    }

    public void getFecha() {
        Calendar calendar = Calendar.getInstance();
        strFecha = String.valueOf(simpleDateFormatFecha.format(calendar.getTime()));
    }

    public void getImageFirma(){
        BASEURL = strIP + "glpservices/webresources/glpservices/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ServicioUsuario service = retrofit.create(ServicioUsuario.class);

        Call call = service.Foto(pedidoID, 3, strtoken);

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    ObjetoRes resObj = (ObjetoRes) response.body();

                    if (resObj.geterror().equals("false")) {
                        List<Imagen> arrayListImagen = Arrays.asList(resObj.getImagen());
                        archivo = arrayListImagen.get(0).getArchivo();

                        signaturePad.setVisibility(View.GONE);

                        decodedByte = decodeBase64(archivo);

                        imgFirma.setImageBitmap(decodedByte);
                    }else {
                        Toast.makeText(getActivity(), "No fue posible obtener la firma!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call call, Throwable t) {
            }
        });
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input.getBytes(), Base64.DEFAULT);
        BitmapFactory.Options options;

        try {
            options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length, options);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    public void putImageFirma() {
        //encode image to base64 string
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (!signaturePad.isEmpty()) {
            Bitmap bitmap = signaturePad.getSignatureBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            byte[] imageBytes = baos.toByteArray();
            final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            BASEURL = strIP + "glpservices/webresources/glpservices/";
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASEURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            final ServicioUsuario service = retrofit.create(ServicioUsuario.class);

            Call call = service.in_foto(pedidoID, imageString, 3, strtoken);

            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    if (response.isSuccessful()) {
                        ObjetoRes resObj = (ObjetoRes) response.body();
                        if (resObj.geterror().equals("false")) {
                        } else {
                            Toast.makeText(getActivity(), "No fue posible guardar la firma!", Toast.LENGTH_SHORT).show();
                        }
                    }
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

                    sqLiteDBHelper = new SQLiteDBHelper(getContext());

                    String sql = "SELECT * FROM pedidos_modificados WHERE oid = '" + pedidoID + "'";

                    Cursor record = db.rawQuery(sql, null);

                    if (record.getCount() > 0) {
                        ContentValues values = new ContentValues();
                        values.put("oid", pedidoID);
                        values.put("hora", hora);
                        values.put("fecha", fecha);
                        values.put("comentario_chofer", record.getString(record.getColumnIndex("comentario_chofer")));
                        values.put("suma_iva", ((Sessions) getActivity().getApplicationContext()).getsessumaiva());
                        values.put("pago_id", record.getString(record.getColumnIndex("pago_id")));
                        values.put("motivo_cancelacion_id", record.getString(record.getColumnIndex("motivo_cancelacion_id")));
                        values.put("estatus_id", record.getString(record.getColumnIndex("estatus_id")));
                        values.put("firma", imageString);
                        values.put("foto_fuga", record.getString(record.getColumnIndex("foto_fuga")));
                        values.put("clave", record.getString(record.getColumnIndex("clave")));
                        db.update(SQLiteDBHelper.Pedidos_Mod_Table, values, "oid = ?", new String[]{pedidoID});

                        db.delete(SQLiteDBHelper.Pedidos_Table, "oid = ?", new String[]{pedidoID});
                    } else {

                        ContentValues values = new ContentValues();
                        values.put("oid", pedidoID);
                        values.put("hora", hora);
                        values.put("fecha", fecha);
                        values.put("comentario_chofer", "");
                        values.put("suma_iva", ((Sessions) getActivity().getApplicationContext()).getsessumaiva());
                        values.put("pago_id", "");
                        values.put("motivo_cancelacion_id", "");
                        values.put("estatus_id", "");
                        values.put("firma", imageString);
                        values.put("foto_fuga", "");
                        values.put("clave", "");
                        db.insert(SQLiteDBHelper.Pedidos_Mod_Table, null, values);

                        db.delete(SQLiteDBHelper.Pedidos_Table, "oid = ?", new String[]{pedidoID});

                    }
                }
            });
        } else {
            Toast.makeText(getActivity(), "No existe una firma", Toast.LENGTH_SHORT).show();
        }
        if (((Sessions) getActivity().getApplicationContext()).getSestipo_pedido().equals("Fuga")) {
            Bitmap bitmap = thumbnail;
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                byte[] imageBytes = baos.toByteArray();
                final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                BASEURL = strIP + "glpservices/webresources/glpservices/";
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASEURL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                final ServicioUsuario service = retrofit.create(ServicioUsuario.class);

                Call call = service.in_foto(pedidoID, imageString, 4, strtoken);

                call.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        if (response.isSuccessful()) {
                            ObjetoRes resObj = (ObjetoRes) response.body();
                            if (resObj.geterror().equals("false")) {
                            } else {
                                Toast.makeText(getActivity(), "No fue posible guardar la foto de la incidencia", Toast.LENGTH_SHORT).show();
                            }
                        }
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

                        sqLiteDBHelper = new SQLiteDBHelper(getContext());

                        String sql = "SELECT * FROM pedidos_modificados WHERE oid = '" + pedidoID + "'";

                        Cursor record = db.rawQuery(sql, null);

                        if (record.getCount() > 0) {
                            ContentValues values = new ContentValues();
                            values.put("oid", pedidoID);
                            values.put("hora", hora);
                            values.put("fecha", fecha);
                            values.put("comentario_chofer", record.getString(record.getColumnIndex("comentario_chofer")));
                            values.put("suma_iva", ((Sessions) getActivity().getApplicationContext()).getsessumaiva());
                            values.put("pago_id", record.getString(record.getColumnIndex("pago_id")));
                            values.put("motivo_cancelacion_id", record.getString(record.getColumnIndex("motivo_cancelacion_id")));
                            values.put("estatus_id", record.getString(record.getColumnIndex("estatus_id")));
                            values.put("firma", record.getString(record.getColumnIndex("firma")));
                            values.put("foto_fuga", imageString);
                            values.put("clave", record.getString(record.getColumnIndex("clave")));
                            db.update(SQLiteDBHelper.Pedidos_Mod_Table, values, "oid = ?", new String[]{pedidoID});

                            db.delete(SQLiteDBHelper.Pedidos_Table, "oid = ?", new String[]{pedidoID});
                        } else {

                            ContentValues values = new ContentValues();
                            values.put("oid", pedidoID);
                            values.put("hora", hora);
                            values.put("fecha", fecha);
                            values.put("comentario_chofer", "");
                            values.put("suma_iva", ((Sessions) getActivity().getApplicationContext()).getsessumaiva());
                            values.put("pago_id", "");
                            values.put("motivo_cancelacion_id", "");
                            values.put("estatus_id", "");
                            values.put("firma", imageString);
                            values.put("foto_fuga", "");
                            values.put("clave", "");
                            db.insert(SQLiteDBHelper.Pedidos_Mod_Table, null, values);

                            db.delete(SQLiteDBHelper.Pedidos_Table, "oid = ?", new String[]{pedidoID});

                        }
                    }
                });
            }else{
                Toast.makeText(getActivity(), "Se debe de tomar la foto de la incidencia", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getProductos(final boolean pendiente) {
        SQLiteDBHelper sqLiteDBHelper = null;

        sqLiteDBHelper = new SQLiteDBHelper(getActivity());

        SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();

        String sql = "SELECT * FROM configuracion";

        final Cursor record = db.rawQuery(sql, null);

        if (record.moveToFirst()) {

            strIP = record.getString(record.getColumnIndex("ip"));
        }
        BASEURL = strIP + "glpservices/webresources/glpservices/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServicioUsuario service = retrofit.create(ServicioUsuario.class);

        pedidoID = ((Sessions) getActivity().getApplicationContext()).getSesIdPedido();

        /*Call call = service.getProductos(pedidoID, strtoken);

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    ObjetoRes resObj = (ObjetoRes) response.body();
                    if(pendiente){
                        if (resObj.getestatus().equals("Pendiente")) {
                            btnReimpresionTicket.setVisibility(View.GONE);
                            ((Sessions) getActivity().getApplicationContext()).setSessstrRestarProducto("visible");

                        } else {
                            ((Sessions) getActivity().getApplicationContext()).setSessstrRestarProducto("gone");
                            fabAgregarProducto.setEnabled(false);
                            signaturePad.setEnabled(false);
                            btnGuardar.setEnabled(false);
                            btnLimpiar.setEnabled(false);
                            getImageFirma();
                        }
                    }else{
                        ((Sessions) getActivity().getApplicationContext()).setSessstrRestarProducto("gone");
                        fabAgregarProducto.setEnabled(false);

                        signaturePad.setEnabled(false);
                        btnGuardar.setEnabled(false);
                        btnLimpiar.setEnabled(false);
                        getImageFirma();
                    }
                    strTotal = String.valueOf(resObj.getsumaTotal());
                    textViewTotal.setText("Total $:" + resObj.getsumaTotal());
                    if (resObj.getproducto() != null) {
                        adapter = new ProductoAdapter(Arrays.asList(resObj.getproducto()), getActivity(), getFragmentManager(), pendiente);
                        Sessions sessions = new Sessions();
                        sessions.setSesDetalleProductoSurtir(resObj.getproducto());
                        recyclerViewProductos.setAdapter(adapter);
                    } else {
                        Toast.makeText(getActivity(), "No Productos!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "error! ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });*/

        if (pendiente) {
            btnReimpresionTicket.setVisibility(View.GONE);
            ((Sessions) getActivity().getApplicationContext()).setSessstrRestarProducto("visible");
        } else {
            ((Sessions) getActivity().getApplicationContext()).setSessstrRestarProducto("gone");
            fabAgregarProducto.setEnabled(false);
            imageViewIncidencia.setEnabled(false);
            signaturePad.setEnabled(false);
            btnGuardar.setEnabled(false);
            btnLimpiar.setEnabled(false);
            getImageFirma();
        }

        db = sqLiteDBHelper.getWritableDatabase();

        double total = 0;

        sql = "SELECT * FROM productos WHERE pedido = '" + pedidoID + "'";
        Cursor cursorPr = db.rawQuery(sql, null);
        if (cursorPr.getCount() > 0) {
            Producto[] productos = new Producto[cursorPr.getCount()];
            int j = 0;

            for (cursorPr.moveToFirst(); !cursorPr.isAfterLast(); cursorPr.moveToNext()) {
                Producto producto = new Producto(cursorPr.getString(cursorPr.getColumnIndex("oid")),
                        Integer.parseInt(cursorPr.getString(cursorPr.getColumnIndex("cantidad"))),
                        Boolean.parseBoolean(cursorPr.getString(cursorPr.getColumnIndex("surtido"))),
                        Double.parseDouble(cursorPr.getString(cursorPr.getColumnIndex("precio"))),
                        cursorPr.getString(cursorPr.getColumnIndex("descripcion")),
                        cursorPr.getString(cursorPr.getColumnIndex("pedido")));
                total = total + Double.parseDouble(cursorPr.getString(cursorPr.getColumnIndex("precio")));
                if (producto != null) {
                    productos[j] = producto;
                }
                j++;

                strTotal = String.valueOf(total);
                textViewTotal.setText("Total $:" + total);

                adapter = new ProductoAdapter(Arrays.asList(productos), getActivity(), getFragmentManager(), pendiente);
                recyclerViewProductos.setAdapter(adapter);
            }
        }else {
            strTotal = String.valueOf(0);
            textViewTotal.setText("Total $:" + 0);

            adapter = new ProductoAdapter(null, getActivity(), getFragmentManager(), pendiente);
            recyclerViewProductos.setAdapter(adapter);
        }
    }

    public void getUbicacion(){
        getLocation();
    }

    public void getLocation(){

        try {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null){
                callSeguimiento();
            }else{
                Toast.makeText(getActivity(), "Error de  GPS!", Toast.LENGTH_SHORT).show();
            }
        }
        catch(SecurityException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error de  GPS!", Toast.LENGTH_SHORT).show();
        }
    }

    public void callSeguimiento(){
    }

    @Override
    public void onLocationChanged(Location location) {
        strLatitude = String.valueOf(location.getLatitude());
        strLongitude = String.valueOf(location.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getActivity(), "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }
}
