package jac.infosyst.proyectogas.fragments;

import android.Manifest;
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
import android.widget.ImageView;
import android.widget.TextView;

import jac.infosyst.proyectogas.FragmentDrawer;
import jac.infosyst.proyectogas.MainActivity;
import jac.infosyst.proyectogas.R;
import jac.infosyst.proyectogas.utils.SQLiteDBHelper;

public class OperadorFragment  extends Fragment {

    private static SQLiteDBHelper sqLiteDBHelper = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.setFragmentController(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_operador, container, false);

        String foto = "", placas = "", nombre = "", imei = "", telefono = "";

        sqLiteDBHelper = new SQLiteDBHelper(getContext());
        SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();
        String sql = "SELECT * FROM usuario";
        Cursor record = db.rawQuery(sql, null);

        if (record.moveToFirst()) {
            nombre = record.getString(record.getColumnIndex("nombre"));
            placas = record.getString(record.getColumnIndex("placas"));
            foto = record.getString(record.getColumnIndex("foto"));
        }

        sql = "SELECT * FROM configuracion";
        record = db.rawQuery(sql, null);

        if (record.moveToFirst()) {
            imei = record.getString(record.getColumnIndex("imei"));
            telefono = record.getString(record.getColumnIndex("telefono"));
        }

        TextView textViewInterno;

        textViewInterno  = (TextView) rootView.findViewById(R.id.Nom_Operador);
        textViewInterno.setText(nombre);

        textViewInterno = (TextView) rootView.findViewById(R.id.Nom_Unidad);
        textViewInterno.setText(placas);

        ImageView imageViewPerfil = (ImageView) rootView.findViewById(R.id.profile_image) ;
        imageViewPerfil.setImageBitmap(FragmentDrawer.decodeBase64(foto));

        textViewInterno = (TextView) rootView.findViewById(R.id.tvTelefonoOperador2);
        textViewInterno.setText(telefono);

        textViewInterno = (TextView) rootView.findViewById(R.id.tvIMEIOperador2);
        textViewInterno.setText(imei);

        return rootView;
    }
}
