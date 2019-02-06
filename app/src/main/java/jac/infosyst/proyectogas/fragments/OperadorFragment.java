package jac.infosyst.proyectogas.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;

import jac.infosyst.proyectogas.R;
import jac.infosyst.proyectogas.modelo.Imagen;
import jac.infosyst.proyectogas.modelo.ObjetoRes;
import jac.infosyst.proyectogas.modelo.Usuario;
import jac.infosyst.proyectogas.modelo.UsuarioInfo;
import jac.infosyst.proyectogas.utils.SQLiteDBHelper;
import jac.infosyst.proyectogas.utils.ServicioUsuario;
import jac.infosyst.proyectogas.utils.Sessions;
import me.dm7.barcodescanner.core.ViewFinderView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OperadorFragment  extends Fragment {
    private TextView textViewInterno;
    private ImageView imageViewPerfil;
    private String BASEURL = "";
    Sessions objSessions;
    Bitmap decodedByte;
    String strIP = "";
    String strtoken = "";
    String archivo = "";

    private static SQLiteDBHelper sqLiteDBHelper = null;
    private static String DB_NAME = "proyectogas11.db";
    private static int DB_VERSION = 1;


    public OperadorFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_operador, container, false);

        Sessions strSess = new Sessions();
        sqLiteDBHelper = new SQLiteDBHelper(getActivity(), DB_NAME, null, DB_VERSION);
        final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();


        String sql = "SELECT * FROM config WHERE id = 1 ORDER BY id DESC limit 1";

        final int recordCount = db.rawQuery(sql, null).getCount();
        //  Toast.makeText(getActivity(), "count:" + recordCount, Toast.LENGTH_SHORT).show();


        final Cursor record = db.rawQuery(sql, null);

        if (record.moveToFirst()) {
            strIP = record.getString(record.getColumnIndex("ip"));
        }

        sqLiteDBHelper = new SQLiteDBHelper(getActivity(), DB_NAME, null, DB_VERSION);

        final SQLiteDatabase db3 = sqLiteDBHelper.getWritableDatabase();


        String sql3 = "SELECT * FROM usuario ORDER BY id DESC limit 1";


        final int recordCount3 = db.rawQuery(sql3, null).getCount();
        //  Toast.makeText(getActivity(), "CONTADOR PEDIDOS: " + recordCount3, Toast.LENGTH_LONG).show();

        SQLiteDatabase dbConn3 = sqLiteDBHelper.getWritableDatabase();

        Cursor cursor3 = dbConn3.rawQuery(sql3, null);

        if (cursor3.moveToFirst()) {
            strtoken = cursor3.getString(cursor3.getColumnIndex("token"));
        }

        BASEURL = "http://"+ strIP+ ":8060/glpservices/webresources/glpservices/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ServicioUsuario service = retrofit.create(ServicioUsuario.class);

        //Call call = service.Foto(UsuarioInfo.getOid(),"2", strtoken);
        Call call = service.Foto(UsuarioInfo.getOid(),"2", "2bd65cb6-fea1-4c74-9add-f6ff31f50ca5");
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    ObjetoRes resObj = (ObjetoRes) response.body();
                    if (resObj.geterror().equals("false")) {
                        List<Imagen> arrayListImagen = Arrays.asList(resObj.getImagen());

                        archivo = arrayListImagen.get(0).getArchivo();
                        decodedByte  = decodeBase64(archivo);

                        textViewInterno = (TextView) rootView.findViewById(R.id.Nom_Operador);
                        textViewInterno.setText(UsuarioInfo.getNombre());

                        textViewInterno = (TextView) rootView.findViewById(R.id.Nom_Unidad);
                        textViewInterno.setText(UsuarioInfo.getPlacas());

                        imageViewPerfil = (ImageView) rootView.findViewById(R.id.profile_image) ;
                        imageViewPerfil.setImageBitmap(decodedByte);
                        ObtenerIMEI(rootView);
                    }
                }
            }
            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });

        return rootView;
    }

    public View ObtenerIMEI(View rootView)
    {
        int permissionCheck = ContextCompat.checkSelfPermission(
                getContext(), Manifest.permission.READ_PHONE_STATE );
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso.");
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE }, 225);
        } else {
            Log.i("Mensaje", "Se tiene permiso!");
        }


        String myIMEI = "";

        TelephonyManager mTelephony = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephony.getDeviceId() != null){
            myIMEI = mTelephony.getDeviceId();

            final TextView textView= (TextView) rootView.findViewById(R.id.tvIMEIOperador2);
            textView.setText(myIMEI);
            insertaImeiSqLite(myIMEI);
        }

        return rootView;
    }

    public void insertaImeiSqLite(String emai){
        sqLiteDBHelper = new SQLiteDBHelper(getActivity(), DB_NAME, null, DB_VERSION);

        final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();


        ContentValues values2 = new ContentValues();

        values2.put("emai", emai);

        db.insert("dispositivo", null, values2);

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
            try {
                options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length, options);
                return bitmap;
            } catch(Exception excepetion) {
                return null;
            }
        }
    }
}
