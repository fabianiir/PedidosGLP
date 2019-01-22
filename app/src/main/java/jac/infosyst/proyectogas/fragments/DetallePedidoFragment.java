package jac.infosyst.proyectogas.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import jac.infosyst.proyectogas.R;
import jac.infosyst.proyectogas.utils.Sessions;


import java.io.File;
import java.text.SimpleDateFormat;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.Button;

import java.util.Date;
import java.util.Locale;

import com.github.gcacace.signaturepad.views.SignaturePad;


@SuppressLint("ValidFragment")
public class DetallePedidoFragment  extends Fragment{

    private TextView textViewCliente, textViewDireccion, textViewDescripcion, textViewEstatus, textViewDetalle
            , textViewFirma, textViewTotal, textViewObservaciones;

    /*firma*/
    RelativeLayout mContent;
    Button btnFirmar, btnLimpiarFirma, btnSurtirPedido;

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


        final String strIdPedido = ((Sessions) getActivity().getApplication()).getSesIdPedido();
        String strCliente = ((Sessions) getActivity().getApplication()).getSesCliente();
        String strDireccion = ((Sessions) getActivity().getApplication()).getsesDireccion();
        final String strDescripcion = ((Sessions) getActivity().getApplication()).getsesDescripcion();
        String strEstatus = ((Sessions) getActivity().getApplication()).getsesEstatus();
        String strDetalle = ((Sessions) getActivity().getApplication()).getsesDetalleProducto();
        String strFirma = ((Sessions) getActivity().getApplication()).getsesFirmaURL();
        String strTotal = ((Sessions) getActivity().getApplication()).getsesTotal();

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


        btnFirmar = (Button) rootView.findViewById(R.id.btnFirmar);
        btnFirmar.setEnabled(false);


        btnSurtirPedido = (Button) rootView.findViewById(R.id.btnSurtirPedido);
        btnSurtirPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iraSurtirPedido();
            }
        });


        signaturePad = (SignaturePad) rootView.findViewById(R.id.signaturePad);
        signaturePad.setEnabled(false);




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


    }





