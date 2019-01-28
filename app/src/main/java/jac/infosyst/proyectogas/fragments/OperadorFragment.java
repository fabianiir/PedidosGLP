package jac.infosyst.proyectogas.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
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
import me.dm7.barcodescanner.core.ViewFinderView;

public class OperadorFragment  extends Fragment {


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

        ObtenerIMEI(rootView);

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
        }

        return rootView;
    }
}
