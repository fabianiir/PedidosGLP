package jac.infosyst.proyectogas.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import jac.infosyst.proyectogas.R;
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
    private String BASEURL = "";
    Sessions objSessions;
    String strIP = "";
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
        View rootView = inflater.inflate(R.layout.fragment_operador, container, false);

        textViewInterno = (TextView) rootView.findViewById(R.id.Nom_Operador);
        textViewInterno.setText(UsuarioInfo.getNombre());

        textViewInterno = (TextView) rootView.findViewById(R.id.Nom_Unidad);
        textViewInterno.setText(UsuarioInfo.getPlacas());
        ObtenerIMEI(rootView);

        return rootView;
    }

    public  String FotoPerfil()
    {
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

        BASEURL = "http://"+ strIP+ ":8060/glpservices/webresources/glpservices/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ServicioUsuario service = retrofit.create(ServicioUsuario.class);

        Call call = service.camion(Integer.parseInt(UsuarioInfo.getOid()));

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    ObjetoRes resObj = (ObjetoRes) response.body();
                    if (resObj.geterror().equals("false")) {

                    }
                }
            }
            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
        return "Hola";
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


}
