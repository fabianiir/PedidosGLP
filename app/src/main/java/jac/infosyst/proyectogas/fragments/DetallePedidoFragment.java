package jac.infosyst.proyectogas.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import jac.infosyst.proyectogas.MainActivity;
import jac.infosyst.proyectogas.R;
import jac.infosyst.proyectogas.modelo.Producto;
import jac.infosyst.proyectogas.utils.Sessions;


import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.github.gcacace.signaturepad.views.SignaturePad;

import android.content.ContentValues;

@SuppressLint("ValidFragment")
public class DetallePedidoFragment  extends Fragment  implements LocationListener {

    private TextView textViewCliente, textViewDireccion, textViewTelefono, textViewDescripcion, textViewEstatus, textViewDetalle
            , textViewFirma, textViewTotal, textViewObservaciones;

    /*firma*/
    RelativeLayout mContent;
    Button btnFirmar, btnLimpiarFirma, btnSurtirPedido, btnComoLlegar, btnLlamar,btnCancelarPedido;
    ListView listView;

    Button mClear, mGetSign, mCancel;
    File file;
    View view;

    Bitmap bitmap;

    private PopupWindow POPUP_WINDOW_CONFIRMACION = null;
    View layout;

    // Creating Separate Directory for saving Generated Images
    String DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/firmas/";
    String pic_name = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    String StoredPath = DIRECTORY + pic_name + ".png";
    ImageView firmaImage, imageViewIncidencia;

    public static String tempDir;
    public int count = 1;
    public String current = null;
    private Bitmap mBitmap;
    View mView;
    File mypath;

    private String uniqueId;
    private Context mCtx;
    int RESULT_OK = 1;
    SignaturePad signaturePad;

    private static final String TAG = "DetallePedidoFragment";

    /*foto incidencia*/
    private static final int PICTURE_RESULT = 122 ;
    private ContentValues values;
    private Uri imageUri;
    private Bitmap thumbnail;


    File directory,directoryIncidencia;
    ImageView imgFirma;
    String imageurl;

    LocationManager locationManager;
    String strLatitude = "";
    String strLongitude = "";

    Location location;
    private TableLayout mTableLayout;

    public DetallePedidoFragment(Context mCtx) {
        // Required empty public constructor
        this.mCtx = mCtx;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detalle_pedido, container, false);
        getLocation();


        // setup the table




        MainActivity.setFragmentController(1);
// path to /data/data/yourapp/app_data/imageDir
        ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
        directory = cw.getDir("firmas", Context.MODE_PRIVATE);
        directoryIncidencia = cw.getDir("incidencias", Context.MODE_PRIVATE);

        String strIdPedido = ((Sessions) getActivity().getApplication()).getSesIdPedido();
        String strCliente = ((Sessions) getActivity().getApplication()).getSesCliente();
        String strDireccion = ((Sessions) getActivity().getApplication()).getsesDireccion();
        String strDescripcion = ((Sessions) getActivity().getApplication()).getsescomentarioscliente();
        String strEstatus = ((Sessions) getActivity().getApplication()).getsesEstatus();
        String strDetalle = ((Sessions) getActivity().getApplication()).getsesDetalleProducto();
        String strFirma = ((Sessions) getActivity().getApplication()).getsesFirmaURL();
        String strTotal = ((Sessions) getActivity().getApplication()).getsesTotal();
        final String strTelefono = ((Sessions) getActivity().getApplication()).getsestelefono();
        Producto[] producto = ((Sessions) getActivity().getApplication()).getSesDetalleProductoSurtir();

        NumberFormat format = NumberFormat.getCurrencyInstance();


        //region Tabla Productos
        mTableLayout = (TableLayout) rootView.findViewById(R.id.tableInvoices);

        mTableLayout.setStretchAllColumns(true);

        cargarProductos(producto);
        //endregion


        ArrayList arrayListProductos = new ArrayList();
        if(producto != null && producto.length != 0){
            if(((Sessions)getActivity().getApplicationContext()).getSestipo_pedido().equals("Fuga")){
                arrayListProductos.add("Fuga");
                arrayListProductos.add(Html.fromHtml(
                        "<table style=\"width:100%\">" +
                                "  <tr>" +
                                "    <th>\tProducto\t</th>" +
                                "    <th>\tCantidad\t</th>" +
                                "    <th>\tPrecio\t</th>" +
                                "</table>"));
            }else {
                arrayListProductos.add(Html.fromHtml(
                        "<table style=\"width:200em\">" +
                                "  <tr>" +
                                "    <th>\tProducto\t</th>" +
                                "    <th>\tCantidad\t</th>" +
                                "    <th>\tPrecio\t</th>" +
                                "</table>"));
            }
            for (int i=0; i < producto.length; i++)
            arrayListProductos.add(Html.fromHtml(
                    "<table style=\"width:100%\">" +
                            "  <tr>" +
                            "    <td>\t" + producto[i].getdescripcion() +"\t</td>" +
                            "    <td>\t" + producto[i].getCantidad() + "\t</td>" +
                            "    <td>\t" + format.format(producto[i].getPrecio()) + "\t</td>" +
                            "</table>"));
        }else {
            if (((Sessions) getActivity().getApplicationContext()).getSestipo_pedido().equals("Fuga")) {
                arrayListProductos.add("Fuga");
            } else {
                arrayListProductos.add("N/A");
            }
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, arrayListProductos);




        textViewObservaciones = (TextView) rootView.findViewById(R.id.textViewObservaciones);
        imageViewIncidencia = (ImageView) rootView.findViewById(R.id.imageViewIncidencia);

        textViewCliente = (TextView) rootView.findViewById(R.id.tvCliente);
        textViewDireccion = (TextView) rootView.findViewById(R.id.tvDireccion);
        textViewTelefono = (TextView) rootView.findViewById(R.id.tvTelefono);
        textViewDescripcion = (TextView) rootView.findViewById(R.id.tvDescripcion);
        textViewEstatus = (TextView) rootView.findViewById(R.id.tvEstatus);
        textViewDetalle = (TextView) rootView.findViewById(R.id.tvDetalle);
        textViewFirma = (TextView) rootView.findViewById(R.id.tvFirma);
        textViewTotal = (TextView) rootView.findViewById(R.id.tvTotal);

        if(strCliente == null){
            textViewCliente.setVisibility(View.GONE);
        }
        if(strDireccion == null){
            textViewDireccion.setVisibility(View.GONE);
        }
        if(strDescripcion == null){
            textViewDescripcion.setVisibility(View.GONE);
        }
        if(strEstatus == null){
            textViewEstatus.setVisibility(View.GONE);
        }
        if(strDetalle == null){
            textViewDetalle.setVisibility(View.GONE);
        }
        if(strFirma == null){
            textViewFirma.setVisibility(View.GONE);
        }
        if(strTelefono == null){
            textViewTelefono.setVisibility(View.GONE);
        }

        textViewCliente.setText(Html.fromHtml    ("<b>Nombre:&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp;</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + strCliente));
        textViewDireccion.setText(Html.fromHtml  ("<b>Direccion:&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + strDireccion));
        textViewTelefono.setText(Html.fromHtml   ("<b>Teléfono:&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + strTelefono));
        textViewDescripcion.setText(Html.fromHtml("<b>Descripción:&nbsp&nbsp&nbsp&nbsp&nbsp</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + strDescripcion));
        textViewEstatus.setText(Html.fromHtml    ("<b>Estatus:&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + strEstatus));
        textViewDetalle.setText(Html.fromHtml    ("<b>Detalle Producto:</b>"));
        textViewFirma.setText("Firma: " + strFirma);
        textViewTotal.setText(Html.fromHtml    ("<b>Total: </b>" + format.format(Integer.parseInt(strTotal))));

        btnSurtirPedido = (Button) rootView.findViewById(R.id.btnSurtirPedido);
        btnSurtirPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iraSurtirPedido();
            }
        });

        btnLlamar = (Button) rootView.findViewById(R.id.btnLlamar);
        btnLlamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLlamada(v, strTelefono);
            }
        });

        btnCancelarPedido = (Button) rootView.findViewById(R.id.btnCancelarPedido);
        btnCancelarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelarPedidoFragment cpf = new CancelarPedidoFragment(getActivity().getBaseContext());
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction =        fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, cpf);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        btnComoLlegar = (Button) rootView.findViewById(R.id.btnComoLlegar);
        btnComoLlegar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(((Sessions) mCtx.getApplicationContext()).getSesubicacion_latitude() != null ||
                        ((Sessions) mCtx.getApplicationContext()).getSesubicacion_longitude() != null ) {
                    String query = "google.navigation:q=" + ((Sessions) mCtx.getApplicationContext()).getSesubicacion_latitude()
                            + "," + ((Sessions) mCtx.getApplicationContext()).getSesubicacion_longitude() + "";

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(query));

                    startActivity(intent);
                }
                else{

                    Toast.makeText(getActivity(), "Ubicacion No disponible!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(strTotal.equals("0")){
            if (((Sessions) getActivity().getApplicationContext()).getSestipo_pedido().equals("Fuga")) {
            } else {
                LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                layout = layoutInflater.inflate(R.layout.layout_popup, null);

                DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
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
                txtMessage.setText("Este pedido tiene productos. ¿Desea surtirlo?");

                Button btnSurtirPedidoNo = (Button) layout.findViewById(R.id.btnSurtirPedidoNo);
                btnSurtirPedidoNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PedidosFragment rpf = new PedidosFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container_body, rpf);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        POPUP_WINDOW_CONFIRMACION.dismiss();
                    }
                });

                Button btnSurtirPedidoSi = (Button) layout.findViewById(R.id.btnSurtirPedidoSi);
                btnSurtirPedidoSi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        POPUP_WINDOW_CONFIRMACION.dismiss();
                    }
                });
            }
        }else{
        }
        return rootView;
    }

    public void iraSurtirPedido() {

        SurtirPedidoFragment spf = new SurtirPedidoFragment();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, spf);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

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

    public void getLocation(){

        try {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null){
            }else{
                Toast.makeText(getActivity(), "Error de  GPS!", Toast.LENGTH_SHORT).show();
            }
        }
        catch(SecurityException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error de  GPS!", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        // locationText.setText("Current Location: " + location.getLatitude() + ", " + location.getLongitude());
        strLatitude = String.valueOf(location.getLatitude());
        strLongitude = String.valueOf(location.getLongitude());
    }

    public void onClickLlamada(View v, String numTelefono) {
        Intent i = new Intent(Intent.ACTION_CALL);
        i.setData(Uri.parse("tel:" + numTelefono));
        startActivity(i);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getActivity(), "Porfavor Habilite su GPS e Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    public void cargarProductos(Producto[] productos) {

        int leftRowMargin=0;
        int topRowMargin=0;
        int rightRowMargin=0;
        int bottomRowMargin = 0;
        int textSize = 0, smallTextSize =0, mediumTextSize = 0;

        textSize = (int) getResources().getDimension(R.dimen.font_size_verysmall);
        smallTextSize = (int) getResources().getDimension(R.dimen.font_size_small);
        mediumTextSize = (int) getResources().getDimension(R.dimen.font_size_medium);




        int rows = productos.length;
        //getSupportActionBar().setTitle("Invoices (" + String.valueOf(rows) + ")");
        TextView textSpacer = null;

       mTableLayout.removeAllViews();

        // -1 es la el heading
        for(int i = -1; i < rows; i ++) {
            Producto row = null;
            if (i > -1)
                row = productos[i];
            else {
                textSpacer = new TextView(getContext());
                textSpacer.setText("");

            }
            //Columnas



            //region columna 1
            final TextView tv2 = new TextView(getContext());
            if (i == -1) {
                tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, mediumTextSize);
            } else {
                tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.MATCH_PARENT));
                tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, mediumTextSize);
            }

            tv2.setGravity(Gravity.CENTER);

            tv2.setPadding(5, 10, 0, 10);
            if (i == -1) {
                tv2.setText("Producto");
                tv2.setBackgroundColor(Color.parseColor("#477ea0"));
                tv2.setTextColor(Color.parseColor("#DBDCDC"));
            }else {
                tv2.setBackgroundColor(Color.parseColor("#ffffff"));

                tv2.setText(row.getdescripcion());
            }

//endregion

            //region columna 2
            final TextView tv3 = new TextView(getContext());
            tv3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            tv3.setGravity(Gravity.CENTER);

            tv3.setPadding(5, 10, 0, 10);
            if (i == -1) {
                tv3.setText("Cantidad");
                tv3.setBackgroundColor(Color.parseColor("#477ea0"));
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, mediumTextSize);
                tv3.setTextColor(Color.parseColor("#DBDCDC"));
            } else {
                tv3.setBackgroundColor(Color.parseColor("#f8f8f8"));
                tv3.setText(String.valueOf(row.getCantidad()));
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, mediumTextSize);
            }

//endregion

            //region columna 3
            final TextView tv4 = new TextView(getContext());
            tv4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            tv4.setGravity(Gravity.CENTER);

            tv4.setPadding(5, 10, 0, 10);
            if (i == -1) {
                tv4.setText("Total");
                tv4.setBackgroundColor(Color.parseColor("#477ea0"));
                tv4.setTextSize(TypedValue.COMPLEX_UNIT_PX, mediumTextSize);
                tv4.setTextColor(Color.parseColor("#DBDCDC"));
            } else {
                tv4.setBackgroundColor(Color.parseColor("#ffffff"));

                tv4.setText(String.valueOf("$"+row.getPrecio()));
                tv4.setTextSize(TypedValue.COMPLEX_UNIT_PX, mediumTextSize);
            }

//endregion



            // add table row
            final TableRow tr = new TableRow(getContext());
            tr.setId(i + 1);
            TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT);
            trParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin);
            tr.setPadding(0,0,0,0);
            tr.setLayoutParams(trParams);



            tr.addView(tv2);
            tr.addView(tv3);
            tr.addView(tv4);


            if (i > -1) {

                tr.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        TableRow tr = (TableRow) v;
                        //do whatever action is needed

                    }
                });


            }
            mTableLayout.addView(tr, trParams);

            if (i > -1) {

                // add separator row
                final TableRow trSep = new TableRow(getContext());
                TableLayout.LayoutParams trParamsSep = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT);
                trParamsSep.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin);

                trSep.setLayoutParams(trParamsSep);
                TextView tvSep = new TextView(getContext());
                TableRow.LayoutParams tvSepLay = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT);
                tvSepLay.span = 3;
                tvSep.setLayoutParams(tvSepLay);
                tvSep.setBackgroundColor(Color.parseColor("#d9d9d9"));
                tvSep.setHeight(1);

                trSep.addView(tvSep);
                mTableLayout.addView(trSep, trParamsSep);
            }


        }
    }
}