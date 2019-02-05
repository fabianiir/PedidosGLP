package jac.infosyst.proyectogas.ImpresoraBluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import jac.infosyst.proyectogas.R;

public class Impresora extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    BluetoothDevice bluetoothDevice;

    OutputStream outputStream;
    InputStream inputStream;
    Thread thread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    TextView lblPrinterName;
    EditText textBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impresora);

        // Create object of controls
        Button btnConnect = (Button) findViewById(R.id.btnConnect);
        Button btnDisconnect = (Button) findViewById(R.id.btnDisconnect);
        Button btnPrint = (Button) findViewById(R.id.btnPrint);
        Spinner spinner = (Spinner) findViewById(R.id.pin);

        textBox = (EditText) findViewById(R.id.txtText);

        lblPrinterName = (TextView) findViewById(R.id.lblPrinterName);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        List<String> s = new ArrayList<String>();
        for(BluetoothDevice bt : pairedDevices){
            s.add(bt.getName());}

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, s);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    FindBluetoothDevice();
                    openBluetoothPrinter();

                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    disconnectBT();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    printData();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });

    }

    void FindBluetoothDevice(){

        try{

            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(bluetoothAdapter==null){
                lblPrinterName.setText("Dispositivo Bluetooth no encontrado");
            }
            if(bluetoothAdapter.isEnabled()){
                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBT,0);
            }

            Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();

            if(pairedDevice.size()>0){
                for(BluetoothDevice pairedDev:pairedDevice){

                    // My Bluetooth printer name is MTP-3
                    if(pairedDev.getName().equals("MTP-3")){
                        bluetoothDevice=pairedDev;
                        lblPrinterName.setText("Impresora bluetooth adjunta: "+pairedDev.getName());
                        break;
                    }
                }
            }

            lblPrinterName.setText("Impresora Bluetooth adjuntada");
        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

    // Open Bluetooth Printer

    void openBluetoothPrinter() throws IOException {
        try{

            //Standard uuid from string //
            UUID uuidSting = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            bluetoothSocket=bluetoothDevice.createRfcommSocketToServiceRecord(uuidSting);
            bluetoothSocket.connect();
            outputStream=bluetoothSocket.getOutputStream();
            inputStream=bluetoothSocket.getInputStream();

            beginListenData();

        }catch (Exception ex){

        }
    }

    void beginListenData(){
        try{

            final Handler handler =new Handler();
            final byte delimiter=10;
            stopWorker =false;
            readBufferPosition=0;
            readBuffer = new byte[1024];

            thread=new Thread(new Runnable() {
                @Override
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker){
                        try{
                            int byteAvailable = inputStream.available();
                            if(byteAvailable>0){
                                byte[] packetByte = new byte[byteAvailable];
                                inputStream.read(packetByte);

                                for(int i=0; i<byteAvailable; i++){
                                    byte b = packetByte[i];
                                    if(b==delimiter){
                                        byte[] encodedByte = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer,0,
                                                encodedByte,0,
                                                encodedByte.length
                                        );
                                        final String data = new String(encodedByte,"US-ASCII");
                                        readBufferPosition=0;
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                lblPrinterName.setText(data);
                                            }
                                        });
                                    }else{
                                        readBuffer[readBufferPosition++]=b;
                                    }
                                }
                            }
                        }catch(Exception ex){
                            stopWorker=true;
                        }
                    }

                }
            });

            thread.start();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    // Printing Text to Bluetooth Printer //
    void printData() throws  IOException{
        try{




            String BILL = "";

            BILL = "             SONIGAS S.A. DE C.V.    \n"
                    + "            R.F.C. SON-990722-EQ3     \n " +
                    "   BLVD. ADOLFO LOPEZ MATEOS OTE. NO. 1603    \n" +
                    "        FRACC. INDUSTRIAL PEDRO CARRIZO      \n" +
                    "              C.P.37500 LEON GTO      \n" +
                    "            TEL 01 (477) 771 34 05   \n";
            BILL = BILL
                    + "-----------------------------------------------\n";

            BILL = BILL
                    + "UNIDAD/OPERADOR: R-01 JOSE MARTIN PEREZ\n" +
                    "FECHA: 19/SEPTIEMBRE/2019\n";


            BILL = BILL
                    + "-----------------------------------------------\n";


            BILL = BILL
                    + "CLIENTE: XAVIER LOPEZ HERNANDEZ \n";

            BILL = BILL
                    + "DOMICILIO: AV. LAZARO CARDENAS #2201, COL. DOCTORES \n";



            BILL = BILL
                    + "-----------------------------------------------\n";




            BILL = BILL + String.format("%1$-10s %2$10s %3$13s %4$10s", "PRODUCTO", "CANT", "PRECIO", "IMPORTE");
            BILL = BILL + "\n";
            BILL = BILL
                    + "-----------------------------------------------";
            BILL = BILL + "\n " + String.format("%1$-10s %2$10s %3$11s %4$10s", "GAS L.P.", "300", "10", "$300.00");
            BILL = BILL + "\n " + String.format("%1$-10s %2$10s %3$11s %4$10s", "GAS L.P.", "10", "50", "$500.00");


            BILL = BILL
                    + "\n-----------------------------------------------";
            BILL = BILL + "\n\n ";

            BILL = BILL + "                         SUBTOTAL:" + "   " + "$800.00" + "\n";
            BILL = BILL + "                            I.V.A.:" + "   " + "$128.00" + "\n";
            BILL = BILL + "                             TOTAL:" + "   " + "$928.00" + "\n";

            BILL = BILL
                    + "-----------------------------------------------\n";
            BILL = BILL + "\n\n ";
            outputStream.write(BILL.getBytes());
            //This is printer specific code you can comment ==== > Start

            // Setting height
            int gs = 29;
            outputStream.write(intToByteArray(gs));
            int h = 104;
            outputStream.write(intToByteArray(h));
            int n = 162;
            outputStream.write(intToByteArray(n));

            // Setting Width
            int gs_width = 29;
            outputStream.write(intToByteArray(gs_width));
            int w = 119;
            outputStream.write(intToByteArray(w));
            int n_width = 2;
            outputStream.write(intToByteArray(n_width));



            lblPrinterName.setText("Imprimiendo Ticket...");
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    // Disconnect Printer //
    void disconnectBT() throws IOException{
        try {
            stopWorker=true;
            outputStream.close();
            inputStream.close();
            bluetoothSocket.close();
            lblPrinterName.setText("Impresora Desconectada");
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();

        for (int k = 0; k < b.length; k++) {
            System.out.println("Selva  [" + k + "] = " + "0x"
                    + UnicodeFormatter.byteToHex(b[k]));
        }

        return b[3];
    }

}
