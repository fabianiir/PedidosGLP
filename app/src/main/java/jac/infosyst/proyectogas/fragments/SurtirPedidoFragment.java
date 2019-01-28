package jac.infosyst.proyectogas.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.util.ArrayList;

import jac.infosyst.proyectogas.R;
import jac.infosyst.proyectogas.adaptadores.ProductoAdapter;
import jac.infosyst.proyectogas.modelo.Productos;

import jac.infosyst.proyectogas.utils.ApiUtils;
import jac.infosyst.proyectogas.utils.ServicioUsuario;
import jac.infosyst.proyectogas.utils.Sessions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SurtirPedidoFragment  extends Fragment {
    private TextView textViewCliente, textViewDireccion, textViewDescripcion, textViewEstatus, textViewDetalle
            , textViewFirma, textViewTotal;

    Button btnFirmar, btnGuardar, btnReimpresionTicket, btnLimpiar;
    private PopupWindow POPUP_WINDOW_CONFIRMACION = null;
    View layout;
    LayoutInflater layoutInflater;
    String strIdPedido;

    private RecyclerView recyclerViewProductos;
    private RecyclerView.Adapter adapter;

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



    private static final String TAG = "SurtirPedidoFragment";

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
                mostrarConfirmacion("Confirmacion "+ strDetalle.toString()+ " Confirmar?");


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



                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }

            }
        });

        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = layoutInflater.inflate(R.layout.layout_popup, null);





        recyclerViewProductos = (RecyclerView) rootView.findViewById(R.id.recyclerViewProductos);
        recyclerViewProductos.setHasFixedSize(true);
        recyclerViewProductos.setLayoutManager(new LinearLayoutManager(getActivity()));
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiUtils.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServicioUsuario service = retrofit.create(ServicioUsuario.class);


        String strIdPedido = ((Sessions)getActivity().getApplication()).getSesIdPedido();

        Call<Productos> call = service.getProductos(strIdPedido);

        Toast.makeText(getActivity(), "Pedido por producto" + strIdPedido, Toast.LENGTH_SHORT).show();


        call.enqueue(new Callback<Productos>() {
            @Override
            public void onResponse(Call<Productos> call, Response<Productos> response) {
              //  adapter = new PedidoAdapter(response.body().getPedidos(), getActivity(), getFragmentManager() );
                //Toast.makeText(getActivity(), "CALL" + response.body().getPedidos(), Toast.LENGTH_SHORT).show();


                //adapter = new ProductoAdapter(response.body().getProductos(), getActivity(),  getFragmentManager());
                //recyclerViewProductos.setAdapter(adapter);

            }

            @Override
            public void onFailure(Call<Productos> call, Throwable t) {

            }

        });
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


        btnFirmar = (Button) rootView.findViewById(R.id.btnFirmar);
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
                btnFirmar.setEnabled(true);
                btnLimpiar.setEnabled(true);
            }

            @Override
            public void onClear() {
                //Event triggered when the pad is cleared
                btnFirmar.setEnabled(false);
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


        // Inflate the layout for this fragment
        return rootView;

    }

    public void mostrarConfirmacion(String mensaje){
        Toast.makeText(getActivity(), "Pedido guardado!", Toast.LENGTH_SHORT).show();
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
                pedidoConfirmado(strIdPedido);
                Toast.makeText(getActivity(), "SI guardado!", Toast.LENGTH_SHORT).show();
                POPUP_WINDOW_CONFIRMACION.dismiss();
            }
        });
    }



    public void pedidoConfirmado(String idPedido){
        btnReimpresionTicket.setVisibility(View.VISIBLE);



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




}
