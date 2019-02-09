package jac.infosyst.proyectogas;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.UUID;

import jac.infosyst.proyectogas.FragmentDrawer;

import jac.infosyst.proyectogas.ImpresoraBluetooth.UnicodeFormatter;
import jac.infosyst.proyectogas.fragments.PedidosFragment;
import jac.infosyst.proyectogas.fragments.OperadorFragment;
import jac.infosyst.proyectogas.modelo.ObjetoRes;
import jac.infosyst.proyectogas.utils.SQLiteDBHelper;
import jac.infosyst.proyectogas.utils.ServicioUsuario;
import jac.infosyst.proyectogas.utils.Sessions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
     //   implements NavigationView.OnNavigationItemSelectedListener {
    implements FragmentDrawer.FragmentDrawerListener{

    private static String TAG = MainActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

   // Bundle bundle;
    PedidosFragment pedidoObj;

    String strRolUsuario;
    private String BASEURL = "";
    Sessions objSessions;
    private SQLiteDBHelper sqLiteDBHelper = null;
    private String DB_NAME = "proyectogas17.db";
    private int DB_VERSION = 1;
    String strIP = "";


///Variables Impresora
    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    BluetoothDevice bluetoothDevice;

    static OutputStream outputStream;
    InputStream inputStream;
    Thread thread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
               //     FindBluetoothDevice();
                    //openBluetoothPrinter();

                }catch (Exception ex){
                    ex.printStackTrace();
                }

            }
            }).start();


        objSessions = new Sessions();

        strRolUsuario = ((Sessions)getApplicationContext()).getsesUsuarioRol();

        sqLiteDBHelper = new SQLiteDBHelper(getApplicationContext(), DB_NAME, null, DB_VERSION);
        final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();


        String sql = "SELECT * FROM config WHERE id = 1 ORDER BY id DESC limit 1";

        final int recordCount = db.rawQuery(sql, null).getCount();

        final Cursor record = db.rawQuery(sql, null);

        if (record.moveToFirst()) {
            strIP = record.getString(record.getColumnIndex("ip"));
            objSessions.setSesstrIpServidor(strIP);
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);

        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        displayView(0);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position)
    {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);

        switch (position) {

            case 0:
                title = getString(R.string.title_pedidos);
                fragment = new PedidosFragment();
                break;
            case 1:
                Log.d(TAG,"OperadorFragment: ");
                fragment = new OperadorFragment();
                title = getString(R.string.title_operador);
                break;
            case 2:
                fragment = new MapsActivity();
                title = getString(R.string.title_mapa);
                break;
            case 3:
                if (strRolUsuario.equals("Administrador")) {
                    Intent i = new Intent(MainActivity.this, Configuracion.class);
                    startActivity(i);
                    ((Activity) MainActivity.this).overridePendingTransition(0, 0);
                }
                if(strRolUsuario.equals("Operador")){

                    title = getString(R.string.title_pedidosrealizados);
                    fragment = new PedidosFragment();
                }

                break;

            case 4:
                if (strRolUsuario.equals("Administrador")) {
                    title = getString(R.string.title_pedidosrealizados);
                    fragment = new PedidosFragment();

                    Intent i2 = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i2);
                    ((Activity) MainActivity.this).overridePendingTransition(0,0);
                }
                if(strRolUsuario.equals("Operador")){
                    Log.v(TAG,"token: " + position);
                   // insertBitacora(false, "emai", objSessions.getsessIDuser(), objSessions.getsessIDcamion() , objSessions.getsessToken() );

                    Intent i2 = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i2);
                    ((Activity) MainActivity.this).overridePendingTransition(0,0);

                }

                break;

            case 5:
                Log.v(TAG,"token: " + position);
              //  insertBitacora(false, "emai", objSessions.getsessIDuser(), objSessions.getsessIDcamion() , objSessions.getsessToken() );

                Intent i2 = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i2);
                ((Activity) MainActivity.this).overridePendingTransition(0,0);
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);

        }
    }

    public void insertBitacora(boolean evento, String emai, String chofer_id, String camion_id , String token){

        BASEURL = strIP + "glpservices/webresources/glpservices/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServicioUsuario service = retrofit.create(ServicioUsuario.class);

        Call call = service.bitacora(evento, emai, chofer_id , camion_id , token);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    ObjetoRes resObj = (ObjetoRes) response.body();

                    if(resObj.geterror().equals("false")){

                        Log.d(TAG,"token: " + resObj.gettoken());
                    } else {
                        Toast.makeText(getApplicationContext(), resObj.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error! Intenta Nuevamente", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    void FindBluetoothDevice(){

        try{

            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(bluetoothAdapter==null){

                //lblPrinterName.setText("Dispositivo Bluetooth no encontrado");
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
                        //lblPrinterName.setText("Impresora bluetooth adjunta: "+pairedDev.getName());
                        break;
                    }
                }
                openBluetoothPrinter();
            }

            //lblPrinterName.setText("Impresora Bluetooth adjuntada");
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
                                                //lblPrinterName.setText(data);
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
    public static void printData(String cliente, String direccion, String total,String chofer, String unidad, String fecha, boolean reImpresion) throws  IOException{
        try{




            String BILL = "";

            BILL = "             SONIGAS S.A. DE C.V.    \n"
                    + "            R.F.C. SON-990722-EQ3     \n " +
                    "   BLVD. ADOLFO LOPEZ MATEOS OTE. NO. 1603    \n" +
                    "        FRACC. INDUSTRIAL PEDRO CARRIZO      \n" +
                    "              C.P.37500 LEON GTO      \n" +
                    "            TEL 01 (477) 771 34 05   \n";
if(reImpresion){

    BILL = BILL
            + "            R E  I M P R E S I O N        \n";

}

            BILL = BILL
                    + "-----------------------------------------------\n";

            BILL = BILL
                    + "OPERADOR:"+chofer.toUpperCase()+"\n" +
                    "UNIDAD:"+unidad.toUpperCase()+"\n"+
                    "FECHA:"+fecha+ "\n";
            if(reImpresion){

                BILL = BILL
                        + "            R E  I M P R E S I O N        \n";

            }


            BILL = BILL
                    + "-----------------------------------------------\n";


            BILL = BILL
                    + "CLIENTE:"+cliente.toUpperCase()+" \n";

            BILL = BILL
                    + "DOMICILIO: "+direccion.toUpperCase()+"\n";



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
            BILL = BILL + "\n";
            if(reImpresion){
                BILL = BILL
                        + "            R E  I M P R E S I O N        \n";

            }

            BILL = BILL + "                         SUBTOTAL:" + "   " + "$800.00" + "\n";
            BILL = BILL + "                            I.V.A.:" + "   " + "$128.00" + "\n";
            BILL = BILL + "                             TOTAL:" + "   " + "$"+total+ ".00\n";

            BILL = BILL
                    + "-----------------------------------------------\n\n";
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



           // lblPrinterName.setText("Imprimiendo Ticket...");
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
            //lblPrinterName.setText("Impresora Desconectada");
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
