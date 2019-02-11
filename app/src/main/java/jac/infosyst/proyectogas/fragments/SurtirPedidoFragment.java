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
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import jac.infosyst.proyectogas.modelo.Usuario;
import jac.infosyst.proyectogas.modelo.UsuarioInfo;
import jac.infosyst.proyectogas.modelo.UsuarioInfo;
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


import android.util.Base64;

public class SurtirPedidoFragment  extends Fragment implements LocationListener {
    private TextView textViewCliente, textViewDireccion, textViewDescripcion, textViewEstatus, textViewDetalle, textViewFirma, textViewTotal;

    Button btnFirmar, btnGuardar, btnReimpresionTicket, btnLimpiar;
    private PopupWindow POPUP_WINDOW_CONFIRMACION = null;
    private PopupWindow POPUP_WINDOW_CATALAGOPRODUCTOS = null;

    View layout, layoutCatalagoProductos;
    LayoutInflater layoutInflater, layoutInflaterCatalagoProductos;
    String strIdPedido;

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

        sqLiteDBHelper = new SQLiteDBHelper(getActivity(), DB_NAME, null, DB_VERSION);

        final SQLiteDatabase db3 = sqLiteDBHelper.getWritableDatabase();

        String sql3 = "SELECT * FROM usuario ORDER BY id DESC limit 1";

        SQLiteDatabase dbConn3 = sqLiteDBHelper.getWritableDatabase();

        Cursor cursor3 = dbConn3.rawQuery(sql3, null);

        if (cursor3.moveToFirst()) {
            strchofer = cursor3.getString(cursor3.getColumnIndex("Oid"));
            strtoken = cursor3.getString(cursor3.getColumnIndex("token"));

        }

      //  strtoken
                ((Sessions)getActivity().getApplicationContext()).setsessToken(strtoken);



        String sql = "SELECT * FROM config WHERE id = 1 ORDER BY id DESC limit 1";

        final int recordCount = dbConn3.rawQuery(sql, null).getCount();
        //  Toast.makeText(getActivity(), "count:" + recordCount, Toast.LENGTH_SHORT).show();

        final Cursor record = dbConn3.rawQuery(sql, null);

        if (record.moveToFirst()) {
            strIP = record.getString(record.getColumnIndex("ip"));

        }

    //    checkPedidoPendiente();

        Toast.makeText(getActivity(), "ticket:" + ((Sessions)getActivity().getApplication()).getSesIdPedido(), Toast.LENGTH_SHORT).show();



        strIdPedido = ((Sessions)getActivity().getApplication()).getSesIdPedido();
        String strCliente = ((Sessions)getActivity().getApplication()).getSesCliente();
        String strDireccion = ((Sessions)getActivity().getApplication()).getsesDireccion();
        String strDescripcion = ((Sessions)getActivity().getApplication()).getsesDescripcion();
        final String strDetalle = ((Sessions)getActivity().getApplication()).getsesDetalleProducto();
        String strEstatus = ((Sessions)getActivity().getApplication()).getsesEstatus();
        String strFirma = ((Sessions)getActivity().getApplication()).getsesFirmaURL();
        String strTotal = ((Sessions)getActivity().getApplication()).getsesTotal();

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


        //Asignacion de variables para impresora
        imprCliente=strCliente;
        imprDireccion=strDireccion;
        imprTotal= strTotal;
        imprChofer = UsuarioInfo.getNombre();
        imprUnidad= UsuarioInfo.getPlacas();



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
                    MainActivity.printData(imprCliente,imprDireccion, imprTotal, imprChofer, imprUnidad,strFecha,true);

                   // Intent intent = new Intent(getActivity(), Impresora.class);

                    // startActivity(intent);


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

        Toast.makeText(getActivity(), "dato:" + ((Sessions)getActivity().getApplicationContext()).getsessToken() , Toast.LENGTH_SHORT).show();


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
                Toast.makeText(getActivity(), "fabAgregarProducto!", Toast.LENGTH_SHORT).show();

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
                //Toast.makeText(getActivity(), strDescripcion2, Toast.LENGTH_SHORT).show();
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

                //imageUri = Uri.fromFile(directoryIncidencia);

                Toast.makeText(getActivity(), "Foto guardada en: " + imageUri, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, PICTURE_RESULT);

            }
        });
        BASEURL = strIP + "glpservices/webresources/glpservices/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServicioUsuario service = retrofit.create(ServicioUsuario.class);

        pedidoID = ((Sessions) getActivity().getApplicationContext()).getSesIdPedido();

        Call call = service.getProductos( pedidoID,
                strtoken);

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    ObjetoRes resObj = (ObjetoRes) response.body();
                    if(resObj.getestatus().equals("Pendiente")){
                        btnReimpresionTicket.setVisibility(View.GONE);
                    }else {
                        fabAgregarProducto.setEnabled(false);
                        btnReimpresionTicket.setVisibility(View.VISIBLE);
                        signaturePad.setEnabled(false);
                        btnGuardar.setEnabled(false);
                        btnLimpiar.setEnabled(false);
                        getImageFirma();
                    }
                    textViewTotal.setText("" + ((Sessions)getActivity().getApplicationContext()).getSesarrayPriceTotal());

                    if(resObj.getproducto() != null){
                        adapter = new ProductoAdapter(Arrays.asList(resObj.getproducto()), getActivity(), getFragmentManager());
                        recyclerViewProductos.setAdapter(adapter);
                    }else {
                        Toast.makeText(getActivity(), "No Productos!", Toast.LENGTH_SHORT).show();
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

        final String strDescripcion2 = ((Sessions) getActivity().getApplication()).getsesDescripcion();
        final String strIdPedido2 = ((Sessions) getActivity().getApplication()).getSesIdPedido();

        imgFirma=(ImageView)rootView.findViewById(R.id.imgFirma);

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
                putImageFirma();
                pedidoActualizarSurtido("d9da86d7-0fee-43c9-b969-94779d106231");
                Toast.makeText(getActivity(), "Pedido Surtido Exitosamente!", Toast.LENGTH_SHORT).show();
                POPUP_WINDOW_CONFIRMACION.dismiss();
                try {
                    MainActivity.printData(imprCliente, imprDireccion,imprTotal, imprChofer, imprUnidad,strFecha,false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

        /*section - getCatalagoProductos*/
        BASEURL = strIP + "glpservices/webresources/glpservices/";
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

        BASEURL = strIP + "glpservices/webresources/glpservices/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServicioUsuario service = retrofit.create(ServicioUsuario.class);

        Call call = service.up_pedido(pedidoID, strHora, strFecha,
                "comentario_cliente", "comentario_chofer", strLatitude, strLongitude,
                19, 21, "b01020c8-4ab1-49b1-9ae1-87b2ec84465d", "null",
                Estatus.getSurtidoId(), "Up_1", strtoken);

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

    public void getFecha(){
        Calendar calendar = Calendar.getInstance();
        strFecha = String.valueOf(simpleDateFormatFecha.format(calendar.getTime()));
        Toast.makeText(getActivity(), "strFecha:" + strFecha, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), "archivo:" + archivo, Toast.LENGTH_SHORT).show();

                        signaturePad.setVisibility(View.GONE);

                        decodedByte = decodeBase64(archivo);
                        UsuarioInfo uss = new UsuarioInfo();
                        uss.setFotoFirma(decodedByte);
                        imgFirma.setImageBitmap(UsuarioInfo.getFotoFirma());
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

    public void putImageFirma(){
        //encode image to base64 string
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap = signaturePad.getSignatureBitmap();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

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
                        Toast.makeText(getActivity(), resObj.getMessage(), Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getActivity(), "No fue posible guardar la firma!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call call, Throwable t) {
            }
        });
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
