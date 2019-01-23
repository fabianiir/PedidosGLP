package jac.infosyst.proyectogas.LectorQR;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.zxing.Result;

import java.util.Timer;
import java.util.TimerTask;

import jac.infosyst.proyectogas.MainActivity;
import jac.infosyst.proyectogas.R;
import jac.infosyst.proyectogas.LectorNFC.NFC;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Escaner extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    Button btnNFC;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escaner);
        btnNFC = (Button) findViewById(R.id.btn_NFC);

        btnNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Escaner.this, NFC.class);
                startActivity(intent);
            }
        });
    }

    public void btnEscanear(View v){
    mScannerView = new ZXingScannerView(this);
    setContentView(mScannerView);
    mScannerView.setResultHandler(this);
    mScannerView.startCamera();


    mScannerView.setAutoFocus(true);
    }

    @Override
    public void handleResult(Result result) {


        Log.v("HandleResult", result.getText());
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Resultado del Scan");
        builder.setMessage(result.getText());
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();


        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                alertDialog.dismiss(); // when the task active then close the dialog
                t.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
            }
        }, 20000);


        Intent intent = new Intent(Escaner.this, MainActivity.class);
        startActivity(intent);



    }
}
