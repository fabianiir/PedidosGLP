package jac.infosyst.proyectogas.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import jac.infosyst.proyectogas.R;
import jac.infosyst.proyectogas.modelo.Producto;
import jac.infosyst.proyectogas.modelo.Productos;
import jac.infosyst.proyectogas.utils.Sessions;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
public class DetallePedidoFragment  extends Fragment{

    private TextView textViewCliente, textViewDireccion, textViewDescripcion, textViewEstatus, textViewDetalle
            , textViewFirma, textViewTotal, textViewObservaciones;

    /*firma*/
    RelativeLayout mContent;
    Button btnFirmar, btnLimpiarFirma, btnSurtirPedido, btnComoLlegar;

    Button mClear, mGetSign, mCancel;
    File file;
    View view;

    Bitmap bitmap;


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
        Producto[] producto = ((Sessions) getActivity().getApplication()).getSesDetalleProductoSurtir();

        textViewObservaciones = (TextView) rootView.findViewById(R.id.textViewObservaciones);
        imageViewIncidencia = (ImageView) rootView.findViewById(R.id.imageViewIncidencia);

/*
        if (strDescripcion.equals("Recarga")) {
            textViewObservaciones.setVisibility(View.GONE);
            imageViewIncidencia.setVisibility(View.GONE);

        }
        if (strDescripcion.equals("Fuga")) {
            textViewObservaciones.setVisibility(View.VISIBLE);
            imageViewIncidencia.setVisibility(View.VISIBLE);

        }
*/
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
            strDetalle = "N/A";
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
        textViewDetalle.setText("Detalle Producto: " + strDetalle);
        textViewFirma = (TextView) rootView.findViewById(R.id.tvFirma);
        textViewFirma.setText("Firma: " + strFirma);
        textViewTotal = (TextView) rootView.findViewById(R.id.tvTotal);
        textViewTotal.setText("Total: " + strTotal);


      //  btnFirmar = (Button) rootView.findViewById(R.id.btnFirmar);
        //btnFirmar.setEnabled(false);


        btnSurtirPedido = (Button) rootView.findViewById(R.id.btnSurtirPedido);
        btnSurtirPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iraSurtirPedido();
            }
        });

        btnComoLlegar = (Button) rootView.findViewById(R.id.btnComoLlegar);
        btnComoLlegar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri intentUri = Uri.parse("geo:41.382,2.170?z=16&q=41.382,2.170(Esta+Es+La+Etiqueta)");
                Intent intent = new Intent(Intent.ACTION_VIEW, intentUri);
                startActivity(intent);
            }
        });


        signaturePad = (SignaturePad) rootView.findViewById(R.id.signaturePad);
        signaturePad.setEnabled(false);

        final String strDescripcion2 = ((Sessions) getActivity().getApplication()).getsesDescripcion();
        final String strIdPedido2 = ((Sessions) getActivity().getApplication()).getSesIdPedido();

        imageViewIncidencia = (ImageView) rootView.findViewById(R.id.imageViewIncidencia);
        imageViewIncidencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(getActivity(), "dentro de toma de foto" + strDescripcion2, Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //Check permissions for Android 6.0+
                    if (!checkExternalStoragePermission()) {
                        return;
                    }
                }

                values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "incidencia" + "123");
                values.put(MediaStore.Images.Media.DESCRIPTION, "tomada en: " + System.currentTimeMillis());
                imageUri = getActivity().getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);



               // imageUri = Uri.fromFile(directoryIncidencia);

              //  Toast.makeText(getActivity(), "Foto guardada en: " + imageUri, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, PICTURE_RESULT);
             //   Toast.makeText(getActivity(), "PICTURE_RESULT" + PICTURE_RESULT, Toast.LENGTH_SHORT).show();



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


        return rootView;

        }


        /*firmas*/
        /*
        firmaImage = (ImageView) rootView.findViewById(R.id.imageView1);


        String image_path = getActivity().getIntent().getStringExtra("imagePath");
        Bitmap bitmap = BitmapFactory.decodeFile(image_path);
        firmaImage.setImageBitmap(bitmap);
        mContent = (RelativeLayout) rootView.findViewById(R.id.canvasLayout);
        mSignature = new signature(getActivity().getApplicationContext(), null);
        mSignature.setBackgroundColor(Color.WHITE);
        // Dynamically generating Layout through java code
        mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        btnFirmar.setEnabled(false);
        view = mContent;
        btnFirmar.setOnClickListener(new OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             Log.v("log_tag", "Panel Saved");
                                             if (Build.VERSION.SDK_INT >= 23) {
                                                 isStoragePermissionGranted();
                                             } else {
                                                 view.setDrawingCacheEnabled(true);
                                                 mSignature.save(view, StoredPath);
                                                 Toast.makeText(getActivity().getApplicationContext(), "Successfully Saved", Toast.LENGTH_SHORT).show();

                                                 recreate();
                                             }
                                         }
                                     });


                // Method to create Directory, if the Directory doesn't exists
                file = new File(DIRECTORY);
        if (!file.exists()) {
            file.mkdir();
        }
*/





/*
        guardarFirmaLocalmente();
        btnFirmar.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                Log.v("log_tag", "Panel Saved");
                boolean error = captureSignature();
                if(!error){
                    mContent.setDrawingCacheEnabled(true);
                    save();
                    Bundle b = new Bundle();
                    b.putString("status", "done");
                    Intent intent = new Intent();
                    intent.putExtras(b);
                    getActivity().setResult(RESULT_OK,intent);

                    getActivity().finish();
                }
            }
        });*/



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





