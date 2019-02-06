package jac.infosyst.proyectogas.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
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
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import jac.infosyst.proyectogas.ImpresoraBluetooth.Impresora;
import jac.infosyst.proyectogas.LoginActivity;
import jac.infosyst.proyectogas.R;
import jac.infosyst.proyectogas.adaptadores.CatalagoProductosAdapter;
import jac.infosyst.proyectogas.adaptadores.PedidoAdapter;
import jac.infosyst.proyectogas.adaptadores.ProductoAdapter;
import jac.infosyst.proyectogas.modelo.CatalagoProducto;
import jac.infosyst.proyectogas.modelo.ObjetoRes;
import jac.infosyst.proyectogas.modelo.ObjetoRes2;
import jac.infosyst.proyectogas.modelo.Pedido;

import jac.infosyst.proyectogas.modelo.Producto;
import jac.infosyst.proyectogas.utils.ApiUtils;
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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;


public class SurtirPedidoFragment  extends Fragment implements LocationListener {
    private TextView textViewCliente, textViewDireccion, textViewDescripcion, textViewEstatus, textViewDetalle
            , textViewFirma, textViewTotal;

    Button btnFirmar, btnGuardar, btnReimpresionTicket, btnLimpiar;
    private PopupWindow POPUP_WINDOW_CONFIRMACION = null;
    private PopupWindow POPUP_WINDOW_CATALAGOPRODUCTOS = null;

    View layout, layoutCatalagoProductos;
    LayoutInflater layoutInflater, layoutInflaterCatalagoProductos;
    String strIdPedido;

    private RecyclerView recyclerViewProductos, recyclerViewCatalagoProductos;
    private RecyclerView.Adapter adapter, adapterCatalago;

    private ProductoAdapter productoAdapter;

    private ArrayList<String> productoList = new ArrayList<>();

    SignaturePad signaturePad;
    /*foto incidencia*/
    private static final int PICTURE_RESULT = 122 ;
    private ContentValues values;
    private Uri imageUri;
    private Bitmap thumbnail;

    String imageurl;
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
    ServicioUsuario userService;


    private static final String TAG = "SurtirPedidoFragment";

    private List<Pedido> pedidos;
 //   private List<Producto> productos;
 //

   // List<Producto> listAdapter;
    List<String> listAdapter;

    Producto myCustomProducto;
    CatalagoProducto myCatalagoProducto;

    String strHora = "";
    String strFecha = "";


    static SimpleDateFormat simpleDateFormatFecha = new SimpleDateFormat("dd-MM-yyyy");
    static SimpleDateFormat simpleDateFormatHora = new SimpleDateFormat("HH:mm:ss");
    LocationManager locationManager;
    String strLatitude = "";
    String strLongitude = "";
    Location location;
    FloatingActionButton fabAgregarProducto;

    String strGettoken = "";

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
// path to /data/data/yourapp/app_data/imageDir
        ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
        directory = cw.getDir("firmas", Context.MODE_PRIVATE);
        directoryIncidencia = cw.getDir("incidencias", Context.MODE_PRIVATE);

        userService = ApiUtils.getUserService();
        dialog = new ProgressDialog(getActivity());


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
                mostrarConfirmacion("Confirmar?");



            }
        });
        btnReimpresionTicket = (Button)rootView.findViewById(R.id.btnReimpresionTicket);
        btnReimpresionTicket.setVisibility(View.GONE);
        btnReimpresionTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                try
                {
                    Toast.makeText(getActivity(), "Reimpimir ticket", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getActivity(), Impresora.class);

                    startActivity(intent);


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




        sqLiteDBHelper = new SQLiteDBHelper(getActivity(), DB_NAME, null, DB_VERSION);

        String sql3 = "SELECT * FROM usuario ORDER BY id DESC limit 1";

        SQLiteDatabase dbConn3 = sqLiteDBHelper.getWritableDatabase();

        Cursor cursor3 = dbConn3.rawQuery(sql3, null);

        if (cursor3.moveToFirst()) {
            strchofer = cursor3.getString(cursor3.getColumnIndex("Oid"));
            strtoken = cursor3.getString(cursor3.getColumnIndex("token"));
        }

        recyclerViewProductos = (RecyclerView) rootView.findViewById(R.id.recyclerViewProductos);
        recyclerViewProductos.setHasFixedSize(true);
        recyclerViewProductos.setLayoutManager(new LinearLayoutManager(getActivity()));


        String sql = "SELECT * FROM config WHERE id = 1 ORDER BY id DESC limit 1";

        final int recordCount = dbConn3.rawQuery(sql, null).getCount();
        //  Toast.makeText(getActivity(), "count:" + recordCount, Toast.LENGTH_SHORT).show();


        final Cursor record = dbConn3.rawQuery(sql, null);

        if (record.moveToFirst()) {
            strIP = record.getString(record.getColumnIndex("ip"));

        }




        Toast.makeText(getActivity(), "dato:" + ((Sessions)getActivity().getApplicationContext()).getsessToken() , Toast.LENGTH_SHORT).show();


        BASEURL = "http://"+ strIP+ ":8060/glpservices/webresources/glpservices/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServicioUsuario service = retrofit.create(ServicioUsuario.class);

       Call call = service.getProductos(String.valueOf(((Sessions)getActivity().getApplicationContext()).getSesIdPedido()), strtoken);
       call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    ObjetoRes resObj = (ObjetoRes) response.body();

                    if(resObj.geterror().equals("false")) {
                        adapter = new ProductoAdapter(Arrays.asList(resObj.getproducto()), getActivity(),  getFragmentManager());
                        recyclerViewProductos.setAdapter(adapter);

                    } else {
                        Toast.makeText(getActivity(), "no datos!" , Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "error! " , Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



        /*
        if (strtoken == null){
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    if(response.isSuccessful()){
                        ObjetoRes obj_bitacora = (ObjetoRes) response.body();
                        if(obj_bitacora.geterror().equals("false")) {
                            if (strtoken == null){
                                call = userService.getproductos(strchofer, "Pendiente", obj_bitacora.gettoken());
                               // Toast.makeText(getActivity(), "token null:" +  obj_bitacora.gettoken(), Toast.LENGTH_SHORT).show();
                                strGettoken = obj_bitacora.gettoken();
                            }else {
                                //Toast.makeText(getActivity(), "token not null!" , Toast.LENGTH_SHORT).show();

                                call = userService.getproductos(strchofer, "Pendiente", strtoken);

                            }
                            //  Call call = userService.getPedidos("255abae2-a6ed-43de-8aa3-b637f3490b8a", "Cancelado", "8342d5e8-1fa7-4e86-890d-763eb5a7a193");
                            call.enqueue(new Callback() {
                                @Override
                                public void onResponse(Call call, Response response) {
                                    if(response.isSuccessful()){
                                        ObjetoRes resObj = (ObjetoRes) response.body();

                                        if(resObj.geterror().equals("false")) {



                                            adapter = new PedidoAdapter(Arrays.asList(resObj.getpedido()), getActivity(),  getFragmentManager());
                                            recyclerViewProductos.setAdapter(adapter);

                                            */

                                            /*
                                            sqLiteDBHelper = new SQLiteDBHelper(getActivity(), DB_NAME, null, DB_VERSION);

                                            String sql = "SELECT distinct * FROM productos WHERE activo='uno' AND OidPedido = '"+String.valueOf(((Sessions)getActivity().getApplicationContext()).getSesIdPedido())+"' ORDER BY id DESC";

                                            SQLiteDatabase dbConn = sqLiteDBHelper.getWritableDatabase();
                                            final int recordCount = dbConn.rawQuery(sql, null).getCount();

                                            Cursor cursor = dbConn.rawQuery(sql, null);
                                            ArrayList<Producto> listItems = new ArrayList<Producto>();

                                            boolean hasRecord = cursor.moveToFirst();

                                            if(hasRecord)
                                            {
                                                do{
                                                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                                                    String Oid = cursor.getString(cursor.getColumnIndex("OidProducto"));
                                                    int cantidad = cursor.getInt(cursor.getColumnIndex("cantidad"));
                                                    String surtido =  String.valueOf((cursor.getColumnIndex("surtido")));
                                                    boolean boolSurtido = Boolean.parseBoolean(surtido);
                                                    double precio = Double.parseDouble(cursor.getString(cursor.getColumnIndex("precio")));
                                                    String descripcion = cursor.getString(cursor.getColumnIndex("descripcion"));
                                                    Log.d("descripcion:", descripcion);
                                                    myCustomProducto=new Producto(Oid,cantidad,boolSurtido, precio,descripcion);
                                                    listItems.add(myCustomProducto);

                                                }while(cursor.moveToNext());
                                            }
                                            adapter = new ProductoAdapter(listItems, getActivity(),  getFragmentManager());

                                            recyclerViewProductos.setAdapter(adapter);
*/


/*

                                        } else {
                                            Toast.makeText(getActivity(), "no datos!" , Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(getActivity(), "error! " , Toast.LENGTH_SHORT).show();
                                    }
                                }
                                @Override
                                public void onFailure(Call call, Throwable t) {
                                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
                @Override
                public void onFailure(Call call, Throwable t) {

                }
            });
        }else {
            */


      //  }



        final String strDescripcion2 = ((Sessions) getActivity().getApplication()).getsesDescripcion();
        final String strIdPedido2 = ((Sessions) getActivity().getApplication()).getSesIdPedido();

        /*
        imageViewIncidencia = (ImageView) rootView.findViewById(R.id.imageViewIncidencia);
        imageViewIncidencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), strDescripcion2, Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //Check permissions for Android 6.0+
                    if (!checkExternalStoragePermission()) {
                        return;
                    }
                }
                values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "incidencia" + strIdPedido2);
                values.put(MediaStore.Images.Media.DESCRIPTION, "tomada en: " + System.currentTimeMillis());
                imageUri = getActivity().getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                //imageUri = Uri.fromFile(directoryIncidencia);

                Toast.makeText(getActivity(), "Foto guardada en: " + imageUri, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, PICTURE_RESULT);

            }
        });
        */


  //      btnFirmar = (Button) rootView.findViewById(R.id.btnFirmar);
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



        try {
            File f=new File(directory , "firma.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            imgFirma=(ImageView)rootView.findViewById(R.id.imgFirma);
            imgFirma.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }


        fabAgregarProducto = (FloatingActionButton) rootView.findViewById(R.id.fabAgregarProducto);
        fabAgregarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarCatalagoProductos("Productos");
                Toast.makeText(getActivity(), "fabAgregarProducto!", Toast.LENGTH_SHORT).show();

            }
        });



        // Inflate the layout for this fragment
        return rootView;

    }


    public void mostrarConfirmacion(String mensaje){
      //  Toast.makeText(getActivity(), "Pedido guardado!", Toast.LENGTH_SHORT).show();
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

                pedidoActualizarSurtido("d9da86d7-0fee-43c9-b969-94779d106231");
                Toast.makeText(getActivity(), "Pedido Surtido Exitosamente!", Toast.LENGTH_SHORT).show();
                POPUP_WINDOW_CONFIRMACION.dismiss();
            }
        });
    }

    public void mostrarCatalagoProductos(String mensaje){
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

/*
        ArrayList<CatalagoProducto> listItemsCatalagoProducto = new ArrayList<CatalagoProducto>();
        myCatalagoProducto=new CatalagoProducto("f6674b0d-6265-443d-abfd-5c1b292942b7","Tanque de 40 K",650.0,"Tanque");
        listItemsCatalagoProducto.add(myCatalagoProducto);
        adapterCatalago = new CatalagoProductosAdapter(listItemsCatalagoProducto, getActivity(),  getFragmentManager());
*/


        /*section - getCatalagoProductos*/
        BASEURL = "http://"+ strIP+ ":8060/glpservices/webresources/glpservices/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServicioUsuario service = retrofit.create(ServicioUsuario.class);

        Call call;

        if (strtoken == null){
            call = service.getCatalagoProductos(((Sessions)getActivity().getApplicationContext()).getsessToken());
        }else{
            call = service.getCatalagoProductos(strtoken);
        }



        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    ObjetoRes2 resObj2 = (ObjetoRes2) response.body();

                    if(resObj2.geterror().equals("false")) {

                       /*
                        ArrayList<CatalagoProducto> listItemsCatalagoProducto = new ArrayList<CatalagoProducto>();

                        myCatalagoProducto=new CatalagoProducto("f6674b0d-6265-443d-abfd-5c1b292942b7","GEORGE de 40 K",650.0,"Tanque");


                        listItemsCatalagoProducto.add(myCatalagoProducto);
                        listItemsCatalagoProducto.add((CatalagoProducto) Arrays.asList(resObj2.getcatalogoProductos()));
                        adapterCatalago = new CatalagoProductosAdapter(listItemsCatalagoProducto, getActivity(),  getFragmentManager());
*/
                        adapterCatalago = new CatalagoProductosAdapter(Arrays.asList(resObj2.getcatalogoProductos()), getActivity(),  getFragmentManager());
                        recyclerViewCatalagoProductos.setAdapter(adapterCatalago);

                    } else {
                        Toast.makeText(getActivity(), "no catalago productos!" , Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "error catalago productos! " , Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getActivity(), "catalago productos" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });






        Button btnAgregarProductoNo = (Button) layoutCatalagoProductos.findViewById(R.id.btnAgregarProductoNo);
        btnAgregarProductoNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                POPUP_WINDOW_CATALAGOPRODUCTOS.dismiss();
            }
        });
    }





    public void pedidoActualizarSurtido(String idPedido){
        btnReimpresionTicket.setVisibility(View.VISIBLE);
        fabAgregarProducto.setEnabled(false);
        signaturePad.setEnabled(false);
        btnLimpiar.setEnabled(false);
        btnGuardar.setEnabled(false);

        dialog.setMax(10);
        dialog.setMessage("Actualizando Pedido....");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();

        sqLiteDBHelper = new SQLiteDBHelper(getActivity(), DB_NAME, null, DB_VERSION);
        final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();


        String sql = "SELECT * FROM config WHERE id = 1 ORDER BY id DESC limit 1";

        final Cursor record = db.rawQuery(sql, null);

        if (record.moveToFirst()) {

            strIP = record.getString(record.getColumnIndex("ip"));

        }

        getHora();
        getFecha();
      //  getUbicacion();

        BASEURL = "http://"+ strIP+ ":8060/glpservices/webresources/glpservices/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServicioUsuario service = retrofit.create(ServicioUsuario.class);

        Call call = service.up_pedido(String.valueOf(((Sessions)getActivity().getApplicationContext()).getSesIdPedido()), strHora, strFecha,
                "comentario_cliente", "comentario_chofer", strLatitude, strLongitude,
                19, 21, "b01020c8-4ab1-49b1-9ae1-87b2ec84465d", "null",
                "1bcd4387-9f14-43cb-84c8-e2fb46ac67f2", "Up_1",
                strtoken);

        call.enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) {


                if (response.isSuccessful()) {
                    ObjetoRes resObj = (ObjetoRes) response.body();
                    if (resObj.geterror().equals("false")) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(getActivity(), "Hora:"+strHora + "Fecha:"+ strFecha + "Latitude:"+strLatitude + "Longitude"+strLongitude
                                + resObj.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    if (resObj.geterror().equals("true")) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(getActivity(), resObj.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }

            }
            @Override
            public void onFailure(Call call, Throwable t) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
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
                if (requestCode == PICTURE_RESULT)
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

    public void getFecha(){

        Calendar calendar = Calendar.getInstance();

        strFecha = String.valueOf(simpleDateFormatFecha.format(calendar.getTime()));
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
        Toast.makeText(getActivity(), "Latitude: " + strLongitude + " Longitude:"  + strLongitude , Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onLocationChanged(Location location) {
        // locationText.setText("Current Location: " + location.getLatitude() + ", " + location.getLongitude());
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
