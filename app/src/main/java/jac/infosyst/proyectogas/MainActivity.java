package jac.infosyst.proyectogas;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import jac.infosyst.proyectogas.ImpresoraBluetooth.UnicodeFormatter;
import jac.infosyst.proyectogas.fragments.PedidosFragment;
import jac.infosyst.proyectogas.fragments.OperadorFragment;
import jac.infosyst.proyectogas.modelo.Camion;
import jac.infosyst.proyectogas.modelo.CatalagoProducto;
import jac.infosyst.proyectogas.modelo.CatalogoEstatus;
import jac.infosyst.proyectogas.modelo.Chofer;
import jac.infosyst.proyectogas.modelo.Estatus;
import jac.infosyst.proyectogas.modelo.ObjetoRes;
import jac.infosyst.proyectogas.modelo.ObjetoRes2;
import jac.infosyst.proyectogas.modelo.ObjetoRes3;
import jac.infosyst.proyectogas.modelo.Pedido;
import jac.infosyst.proyectogas.modelo.Producto;
import jac.infosyst.proyectogas.modelo.Spinner;
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

    private PopupWindow POPUP_WINDOW_CONFIRMACION = null;
    View layout;

    boolean errorDescarga = false;
    String strRolUsuario;
    private String BASEURL = "";
    Sessions objSessions;
    private SQLiteDBHelper sqLiteDBHelper = null;
    String strIP = "";
    String Imei = "";

// region variables impresora
    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    BluetoothDevice bluetoothDevice;



    // Boolean DispositivoEncontrado=false;
    public static Boolean ConexionEstablecida=false;



    public static void setConexionEstablecida(Boolean conexionEstablecida) {
        ConexionEstablecida = conexionEstablecida;
    }

    static OutputStream outputStream;
    InputStream inputStream;
    Thread thread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    public static int fragmentController;

    public static int getFragmentController() {
        return fragmentController;
    }

    public static void setFragmentController(int fragmentController) {
        MainActivity.fragmentController = fragmentController;
    }

    @Override
    public void onBackPressed(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            switch (fragmentController){
                case 0:
                    LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    layout = layoutInflater.inflate(R.layout.layout_popup, null);

                    DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
                    int width = displayMetrics.widthPixels;
                    int height = displayMetrics.heightPixels;

                    layout.setVisibility(View.VISIBLE);
                    POPUP_WINDOW_CONFIRMACION = new PopupWindow(this);
                    POPUP_WINDOW_CONFIRMACION.setContentView(layout);
                    POPUP_WINDOW_CONFIRMACION.setWidth(width);
                    POPUP_WINDOW_CONFIRMACION.setHeight(height);
                    POPUP_WINDOW_CONFIRMACION.setFocusable(true);

                    POPUP_WINDOW_CONFIRMACION.setBackgroundDrawable(null);

                    POPUP_WINDOW_CONFIRMACION.showAtLocation(layout, Gravity.CENTER, 1, 1);

                    TextView txtMessage = (TextView) layout.findViewById(R.id.layout_popup_txtMessage);
                    txtMessage.setText("¿Desea cerrar la aplicación?");

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
                            POPUP_WINDOW_CONFIRMACION.dismiss();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("EXIT", true);
                            startActivity(intent);
                        }
                    });
                    break;
                case 1:
                    super.onBackPressed();
                    break;
                case 2:
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    }

    //endregion
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getBooleanExtra("User", false)) {
            setContentView(R.layout.activity_main);

//region Ejecucion hilo Impresora
            //region Ejecucion hilo Impresora
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        FindBluetoothDevice();
                        //openBluetoothPrinter();

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }).start();
            //endregion


            //endregion
            sqLiteDBHelper = new SQLiteDBHelper(getApplicationContext());
            final SQLiteDatabase db1 = sqLiteDBHelper.getWritableDatabase();

            String sql1 = "SELECT * FROM cat_estatus";
            Cursor record1 = db1.rawQuery(sql1, null);
            if(record1.moveToFirst()) {
                Estatus estatus1 = new Estatus();
                for (record1.moveToFirst(); !record1.isAfterLast(); record1.moveToNext()) {
                    if (record1.getString(record1.getColumnIndex("nombre")).equals("Pendiente")) {
                        estatus1.setPendienteId(record1.getString(record1.getColumnIndex("oid")));
                    } else if (record1.getString(record1.getColumnIndex("nombre")).equals("Surtido")) {
                        estatus1.setSurtidoId(record1.getString(record1.getColumnIndex("oid")));
                    } else if (record1.getString(record1.getColumnIndex("nombre")).equals("Cancelado")) {
                        estatus1.setCanceladoId(record1.getString(record1.getColumnIndex("oid")));
                    }
                }
            }
            objSessions = new Sessions();

            strRolUsuario = ((Sessions) getApplicationContext()).getsesUsuarioRol();

            sqLiteDBHelper = new SQLiteDBHelper(getApplicationContext());

            final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();

            String sql = "SELECT * FROM configuracion";

            Cursor record = db.rawQuery(sql, null);

            if (record.moveToFirst()) {
                strIP = record.getString(record.getColumnIndex("ip"));
                Imei = record.getString(record.getColumnIndex("imei"));
                objSessions.setSesstrIpServidor(strIP);
            }

            mToolbar = findViewById(R.id.toolbar);

            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            drawerFragment = (FragmentDrawer)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);

            drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
            drawerFragment.setDrawerListener(MainActivity.this);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    guardar_pedidos_productos();
                    obtener_pedidos();
                    handler.postDelayed(this, 600000);
                }
            }, 10000);  //the time is in miliseconds

            String oid = "", token = "", camion = "";

            sql = "SELECT * FROM usuario";

            record = db.rawQuery(sql, null);

            if (record.moveToFirst()) {
                oid = record.getString(record.getColumnIndex("oid"));
                camion = record.getString(record.getColumnIndex("camion"));
                token = record.getString(record.getColumnIndex("token"));
            }

            ((Sessions) getApplicationContext().getApplicationContext()).setStrImei(Imei);
            ((Sessions) getApplicationContext().getApplicationContext()).setStrChoferId(oid);
            ((Sessions) getApplicationContext().getApplicationContext()).setStrCamionId(camion);
            ((Sessions) getApplicationContext().getApplicationContext()).setsessToken(token);

            displayView(0);
        }else {
            sqLiteDBHelper = new SQLiteDBHelper(getApplicationContext());
            final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();
            String sql = "SELECT * FROM configuracion";

            Cursor record = db.rawQuery(sql, null);

            if (record.moveToFirst()) {
                strIP = record.getString(record.getColumnIndex("ip"));
                Imei = record.getString(record.getColumnIndex("imei"));
            }

            String admin = "";

            sql = "SELECT * FROM usuario";

            record = db.rawQuery(sql, null);

            if (record.moveToFirst()) {
                admin = record.getString(record.getColumnIndex("token"));
            }

            String strcamion = Chofer.getCamion();

            BASEURL = strIP + "glpservices/webresources/glpservices/";
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASEURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            final ServicioUsuario service = retrofit.create(ServicioUsuario.class);

            sql = "SELECT * FROM pedidos";

            record = db.rawQuery(sql, null);
            if(record.getCount() <= 0) {
                if (admin == null) {
                    Call call = service.camion(Integer.parseInt(strcamion));
                    call.enqueue(new Callback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            if (response.isSuccessful()) {
                                ObjetoRes obj_camion = (ObjetoRes) response.body();
                                if (obj_camion.geterror().equals("false")) {
                                    List<Camion> arrayListCamion = Arrays.asList(obj_camion.getcamion());

                                    String oid = "", nombre = "", foto = "", camion = "";
                                    boolean admin = false;

                                    String sql = "SELECT * FROM usuario";

                                    Cursor record = db.rawQuery(sql, null);

                                    if (record.moveToFirst()) {
                                        oid = record.getString(record.getColumnIndex("oid"));
                                        nombre = record.getString(record.getColumnIndex("nombre"));
                                        camion = record.getString(record.getColumnIndex("camion"));
                                        foto = record.getString(record.getColumnIndex("foto"));
                                        admin = Boolean.parseBoolean(record.getString(record.getColumnIndex("admin")));
                                    }

                                    ContentValues values = new ContentValues();
                                    values.put("nombre", nombre);
                                    values.put("placas", arrayListCamion.get(0).getplacas());
                                    values.put("camion", arrayListCamion.get(0).getId());
                                    values.put("foto", foto);
                                    values.put("token", "");
                                    values.put("admin", admin);

                                    db.update(SQLiteDBHelper.Usuario_Table, values, "oid = ?", new String[]{oid});

                                    sql = "SELECT * FROM usuario";

                                    record = db.rawQuery(sql, null);

                                    if (record.moveToFirst()) {
                                        oid = record.getString(record.getColumnIndex("oid"));
                                        camion = record.getString(record.getColumnIndex("camion"));
                                    }

                                    call = service.bitacora(true, Imei, oid, camion, null, ((Sessions) getApplicationContext().getApplicationContext()).getStrFireTOken());
                                    call.enqueue(new Callback() {
                                        @Override
                                        public void onResponse(Call call, Response response) {
                                            if (response.isSuccessful()) {
                                                ObjetoRes obj_bitacora = (ObjetoRes) response.body();
                                                if (obj_bitacora.geterror().equals("false")) {

                                                    String oid = "", nombre = "", placas = "", foto = "", token = "", camion = "";
                                                    boolean admin = false;

                                                    String sql = "SELECT * FROM usuario";

                                                    Cursor record = db.rawQuery(sql, null);

                                                    if (record.moveToFirst()) {
                                                        oid = record.getString(record.getColumnIndex("oid"));
                                                        nombre = record.getString(record.getColumnIndex("nombre"));
                                                        placas = record.getString(record.getColumnIndex("placas"));
                                                        camion = record.getString(record.getColumnIndex("camion"));
                                                        foto = record.getString(record.getColumnIndex("foto"));
                                                        token = record.getString(record.getColumnIndex("token"));
                                                        admin = Boolean.parseBoolean(record.getString(record.getColumnIndex("admin")));
                                                    }

                                                    ContentValues values = new ContentValues();
                                                    values.put("oid", oid);
                                                    values.put("nombre", nombre);
                                                    values.put("placas", placas);
                                                    values.put("camion", camion);
                                                    values.put("foto", foto);
                                                    values.put("token", obj_bitacora.gettoken());
                                                    values.put("admin", admin);

                                                    db.update(SQLiteDBHelper.Usuario_Table, values, "oid = ?", new String[]{oid});

                                                    record = db.rawQuery(sql, null);

                                                    if (record.moveToFirst()) {
                                                        oid = record.getString(record.getColumnIndex("oid"));
                                                        camion = record.getString(record.getColumnIndex("camion"));
                                                        token = record.getString(record.getColumnIndex("token"));
                                                    }

                                                    ((Sessions) getApplicationContext().getApplicationContext()).setStrImei(Imei);
                                                    ((Sessions) getApplicationContext().getApplicationContext()).setStrChoferId(oid);
                                                    ((Sessions) getApplicationContext().getApplicationContext()).setStrCamionId(camion);
                                                    ((Sessions) getApplicationContext().getApplicationContext()).setsessToken(token);

                                                    sql = "SELECT * FROM cat_estatus";
                                                    record = db.rawQuery(sql, null);

                                                    if (record.getCount() <= 0) {
                                                        call = service.getCatalogoEstatus(token);
                                                        call.enqueue(new Callback() {
                                                            @Override
                                                            public void onResponse(Call call, Response response) {
                                                                if (response.isSuccessful()) {
                                                                    ObjetoRes3 obj_estatus = (ObjetoRes3) response.body();
                                                                    if (obj_estatus.geterror().equals("false")) {
                                                                        List<CatalogoEstatus> arrayListEstatus = Arrays.asList(obj_estatus.getCatalogoEstatus());
                                                                        Estatus estatus = new Estatus();

                                                                        for (CatalogoEstatus catalogoEstatus : arrayListEstatus) {
                                                                            ContentValues values = new ContentValues();
                                                                            values.put("oid", catalogoEstatus.getIdProducto());
                                                                            values.put("nombre", catalogoEstatus.getdescripcion());
                                                                            db.insert(SQLiteDBHelper.CatEstatus_Table, null, values);
                                                                        }

                                                                        for (int i = 0; i < arrayListEstatus.size(); i++) {
                                                                            if (arrayListEstatus.get(i).getdescripcion().equals("Pendiente")) {
                                                                                estatus.setPendienteId(arrayListEstatus.get(i).getIdProducto());
                                                                            } else if (arrayListEstatus.get(i).getdescripcion().equals("Surtido")) {
                                                                                estatus.setSurtidoId(arrayListEstatus.get(i).getIdProducto());
                                                                            } else if (arrayListEstatus.get(i).getdescripcion().equals("Cancelado")) {
                                                                                estatus.setCanceladoId(arrayListEstatus.get(i).getIdProducto());
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onFailure(Call call, Throwable t) {
                                                                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                                builder.setMessage("Hubo un error en la descarga de datos iniciales, se cerrará la aplicación")
                                                                        .setCancelable(false)
                                                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                            public void onClick(DialogInterface dialog, int id) {
                                                                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                                intent.putExtra("EXIT", true);
                                                                                startActivity(intent);
                                                                            }
                                                                        });
                                                                AlertDialog alert = builder.create();
                                                                alert.show();
                                                            }
                                                        });
                                                    }

                                                    sql = "SELECT * FROM cat_motivo_cancelacion";
                                                    record = db.rawQuery(sql, null);

                                                    if (record.getCount() <= 0) {
                                                        call = service.obtenerMotivosCancelacion(token);
                                                        call.enqueue(new Callback() {
                                                            @Override
                                                            public void onResponse(Call call, Response response) {
                                                                if (response.isSuccessful()) {
                                                                    ObjetoRes obj_estatus = (ObjetoRes) response.body();
                                                                    if (obj_estatus.geterror().equals("false")) {
                                                                        List<Spinner> arrayListMotCancelacion = Arrays.asList(obj_estatus.getmotivoscancelacion());
                                                                        Estatus estatus = new Estatus();

                                                                        for (Spinner spinner : arrayListMotCancelacion) {
                                                                            ContentValues values = new ContentValues();
                                                                            values.put("oid", spinner.getoid());
                                                                            values.put("nombre", spinner.getnombre());
                                                                            db.insert(SQLiteDBHelper.CatMotCanc_Table, null, values);
                                                                        }
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onFailure(Call call, Throwable t) {
                                                                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                                builder.setMessage("Hubo un error en la descarga de datos iniciales, se cerrará la aplicación")
                                                                        .setCancelable(false)
                                                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                            public void onClick(DialogInterface dialog, int id) {
                                                                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                                intent.putExtra("EXIT", true);
                                                                                startActivity(intent);
                                                                            }
                                                                        });
                                                                AlertDialog alert = builder.create();
                                                                alert.show();
                                                            }
                                                        });
                                                    }

                                                    sql = "SELECT * FROM cat_productos";
                                                    record = db.rawQuery(sql, null);

                                                    if (record.getCount() <= 0) {
                                                        call = service.getCatalagoProductos(token);
                                                        call.enqueue(new Callback() {
                                                            @Override
                                                            public void onResponse(Call call, Response response) {
                                                                if (response.isSuccessful()) {
                                                                    ObjetoRes2 obj_estatus = (ObjetoRes2) response.body();
                                                                    if (obj_estatus.geterror().equals("false")) {
                                                                        List<CatalagoProducto> arrayListProductos = Arrays.asList(obj_estatus.getcatalogoProductos());
                                                                        Estatus estatus = new Estatus();

                                                                        for (CatalagoProducto producto : arrayListProductos) {
                                                                            ContentValues values = new ContentValues();
                                                                            values.put("oid", producto.getIdProducto());
                                                                            values.put("descripcion", producto.getdescripcion());
                                                                            values.put("unidad", producto.getunidad());
                                                                            values.put("precio_unitario", producto.getprecio_unitario());
                                                                            db.insert(SQLiteDBHelper.CatProductos_Table, null, values);
                                                                        }
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onFailure(Call call, Throwable t) {
                                                                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                                builder.setMessage("Hubo un error en la descarga de datos iniciales, se cerrará la aplicación")
                                                                        .setCancelable(false)
                                                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                            public void onClick(DialogInterface dialog, int id) {
                                                                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                                intent.putExtra("EXIT", true);
                                                                                startActivity(intent);
                                                                            }
                                                                        });
                                                                AlertDialog alert = builder.create();
                                                                alert.show();
                                                            }
                                                        });
                                                    }

                                                    call = service.getPedidos(oid, "Pendiente", token);
                                                    call.enqueue(new Callback() {
                                                        @Override
                                                        public void onResponse(Call call, Response response) {
                                                            if (response.isSuccessful()) {
                                                                ObjetoRes resObj = (ObjetoRes) response.body();

                                                                if (resObj.geterror().equals("false")) {
                                                                    if (resObj.getpedido() != null) {
                                                                        List<Pedido> list = Arrays.asList(resObj.getpedido());

                                                                        for (Pedido pedido : list) {

                                                                            ContentValues values = new ContentValues();
                                                                            values.put("oid", pedido.getOid());
                                                                            String datetime = pedido.getfechaprogramada();
                                                                            datetime.replace('T', ' ');
                                                                            values.put("fecha_hora_programada", datetime);
                                                                            values.put("cliente", pedido.getcliente());
                                                                            values.put("direccion", pedido.getdireccion());
                                                                            values.put("cp", pedido.getcp());
                                                                            values.put("telefono", pedido.gettelefono());
                                                                            values.put("lat", pedido.getubicacion_lat());
                                                                            values.put("lon", pedido.getubicacion_long());
                                                                            values.put("comentario_cliente", pedido.getcomentarios_cliente());
                                                                            if (pedido.getsuma_iva() == null) {
                                                                                values.put("suma_iva", 0);
                                                                            } else {
                                                                                values.put("suma_iva", Double.parseDouble(pedido.getsuma_iva()));
                                                                            }
                                                                            if (pedido.gettotal() == null) {
                                                                                values.put("total", 0);
                                                                            } else {
                                                                                values.put("total", Double.parseDouble(pedido.gettotal()));
                                                                            }
                                                                            values.put("empresa", pedido.getEmpresa());
                                                                            values.put("tipo_pedido", pedido.gettipo_pedido());
                                                                            values.put("forma_pago", pedido.getForma_pago());
                                                                            values.put("estatus", pedido.getestatus());
                                                                            db.insert(SQLiteDBHelper.Pedidos_Table, null, values);

                                                                            if (pedido.getHobbies() != null) {

                                                                                List<Producto> listproductos = Arrays.asList(pedido.getHobbies());

                                                                                for (Producto producto : listproductos) {
                                                                                    ContentValues valuesProd = new ContentValues();
                                                                                    valuesProd.put("oid", producto.getOidProducto());
                                                                                    valuesProd.put("cantidad", producto.getCantidad());
                                                                                    valuesProd.put("surtido", producto.getsurtido());
                                                                                    valuesProd.put("precio", producto.getPrecio());
                                                                                    valuesProd.put("descripcion", producto.getdescripcion());
                                                                                    valuesProd.put("pedido", pedido.getOid());
                                                                                    db.insert(SQLiteDBHelper.Productos_Table, null, valuesProd);
                                                                                }
                                                                            }
                                                                        }

                                                                    } else {
                                                                        Toast.makeText(getApplicationContext(), "No existen Pedidos!", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                } else {
                                                                    Toast.makeText(getApplicationContext(), "No hay pedidos nuevos!", Toast.LENGTH_SHORT).show();
                                                                }
                                                            } else {
                                                                Toast.makeText(getApplicationContext(), "error! ", Toast.LENGTH_SHORT).show();
                                                            }
                                                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                                            java.util.Date date = new Date();
                                                            String fecha = dateFormat.format(date);

                                                            ContentValues values = new ContentValues();
                                                            values.put("fecha", fecha);
                                                            db.insert(SQLiteDBHelper.Synchro_Table, null, values);

                                                            setContentView(R.layout.activity_main);

                                                            //region Ejecucion hilo Impresora
                                                            new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    try {
                                                                        FindBluetoothDevice();
                                                                        //openBluetoothPrinter();

                                                                    } catch (Exception ex) {
                                                                        ex.printStackTrace();
                                                                    }

                                                                }
                                                            }).start();
                                                            //endregion

                                                            objSessions = new Sessions();

                                                            strRolUsuario = ((Sessions) getApplicationContext()).getsesUsuarioRol();

                                                            sqLiteDBHelper = new SQLiteDBHelper(getApplicationContext());

                                                            String sql = "SELECT * FROM configuracion";

                                                            Cursor record = db.rawQuery(sql, null);

                                                            if (record.moveToFirst()) {
                                                                strIP = record.getString(record.getColumnIndex("ip"));
                                                                Imei = record.getString(record.getColumnIndex("imei"));
                                                                objSessions.setSesstrIpServidor(strIP);
                                                            }

                                                            mToolbar = (Toolbar) findViewById(R.id.toolbar);

                                                            setSupportActionBar(mToolbar);
                                                            getSupportActionBar().setDisplayShowHomeEnabled(true);

                                                            drawerFragment = (FragmentDrawer)
                                                                    getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);

                                                            drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
                                                            drawerFragment.setDrawerListener(MainActivity.this);

                                                            String oid = "", token = "", camion = "";
                                                            boolean admin = false;

                                                            sql = "SELECT * FROM usuario";

                                                            record = db.rawQuery(sql, null);

                                                            if (record.moveToFirst()) {
                                                                oid = record.getString(record.getColumnIndex("oid"));
                                                                camion = record.getString(record.getColumnIndex("camion"));
                                                                token = record.getString(record.getColumnIndex("token"));
                                                            }

                                                            ((Sessions) getApplicationContext().getApplicationContext()).setStrImei(Imei);
                                                            ((Sessions) getApplicationContext().getApplicationContext()).setStrChoferId(oid);
                                                            ((Sessions) getApplicationContext().getApplicationContext()).setStrCamionId(camion);
                                                            ((Sessions) getApplicationContext().getApplicationContext()).setsessToken(token);

                                                            final Handler handler = new Handler();
                                                            handler.postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    obtener_pedidos();
                                                                    guardar_pedidos_productos();
                                                                    handler.postDelayed(this, 600000);
                                                                }
                                                            }, 600000);  //the time is in miliseconds

                                                            displayView(0);
                                                        }

                                                        @Override
                                                        public void onFailure(Call call, Throwable t) {
                                                            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                            builder.setMessage("Hubo un error en la descarga de datos iniciales, se cerrará la aplicación")
                                                                    .setCancelable(false)
                                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                        public void onClick(DialogInterface dialog, int id) {
                                                                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                            intent.putExtra("EXIT", true);
                                                                            startActivity(intent);
                                                                        }
                                                                    });
                                                            AlertDialog alert = builder.create();
                                                            alert.show();
                                                        }
                                                    });
                                                } else {
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                    builder.setMessage("IMEI no registrado, se cerrará la aplicación")
                                                            .setCancelable(false)
                                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int id) {
                                                                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                    intent.putExtra("EXIT", true);
                                                                    startActivity(intent);
                                                                }
                                                            });
                                                    AlertDialog alert = builder.create();
                                                    alert.show();
                                                }
                                            } else {
                                                Toast.makeText(getApplicationContext(), "response.success.bitacora!", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call call, Throwable t) {
                                            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                            builder.setMessage("Hubo un error en la descarga de datos iniciales, se cerrará la aplicación")
                                                    .setCancelable(false)
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            intent.putExtra("EXIT", true);
                                                            startActivity(intent);
                                                        }
                                                    });
                                            AlertDialog alert = builder.create();
                                            alert.show();
                                        }
                                    });
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setMessage("El Tag del camión que está intentando ingresar no es válido, la aplicación se cerrará...")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    intent.putExtra("EXIT", true);
                                                    startActivity(intent);
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setMessage("El Tag del camión que está intentando ingresar no es válido, la aplicación se cerrará...")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.putExtra("EXIT", true);
                                                startActivity(intent);
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        }

                        @Override
                        public void onFailure(Call call, Throwable t) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage("Error en la descarga inicial")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.putExtra("EXIT", true);
                                            startActivity(intent);
                                        }
                                    });
                            AlertDialog alert1 = builder.create();
                            alert1.show();
                        }
                    });
                } else {

                    String oid = "", nombre = "", placas = "", foto = "", token = "", camion = "";

                    sql = "SELECT * FROM usuario";

                    record = db.rawQuery(sql, null);

                    if (record.moveToFirst()) {
                        oid = record.getString(record.getColumnIndex("oid"));
                        token = record.getString(record.getColumnIndex("token"));
                    }


                    sql = "SELECT * FROM cat_estatus";
                    record = db.rawQuery(sql, null);

                    if (record.getCount() <= 0) {
                        Call call = service.getCatalogoEstatus(token);
                        call.enqueue(new Callback() {
                            @Override
                            public void onResponse(Call call, Response response) {
                                if (response.isSuccessful()) {
                                    ObjetoRes3 obj_estatus = (ObjetoRes3) response.body();
                                    if (obj_estatus.geterror().equals("false")) {
                                        List<CatalogoEstatus> arrayListEstatus = Arrays.asList(obj_estatus.getCatalogoEstatus());
                                        Estatus estatus = new Estatus();

                                        for (CatalogoEstatus catalogoEstatus : arrayListEstatus) {
                                            ContentValues values = new ContentValues();
                                            values.put("oid", catalogoEstatus.getIdProducto());
                                            values.put("nombre", catalogoEstatus.getdescripcion());
                                            db.insert(SQLiteDBHelper.CatEstatus_Table, null, values);
                                        }

                                        for (int i = 0; i < arrayListEstatus.size(); i++) {
                                            if (arrayListEstatus.get(i).getdescripcion().equals("Pendiente")) {
                                                estatus.setPendienteId(arrayListEstatus.get(i).getIdProducto());
                                            } else if (arrayListEstatus.get(i).getdescripcion().equals("Surtido")) {
                                                estatus.setSurtidoId(arrayListEstatus.get(i).getIdProducto());
                                            } else if (arrayListEstatus.get(i).getdescripcion().equals("Cancelado")) {
                                                estatus.setCanceladoId(arrayListEstatus.get(i).getIdProducto());
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call call, Throwable t) {
                                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setMessage("Hubo un error en la descarga de datos iniciales, se cerrará la aplicación")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.putExtra("EXIT", true);
                                                startActivity(intent);
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        });
                    }

                    sql = "SELECT * FROM cat_motivo_cancelacion";
                    record = db.rawQuery(sql, null);

                    if (record.getCount() <= 0) {
                        Call call = service.obtenerMotivosCancelacion(token);
                        call.enqueue(new Callback() {
                            @Override
                            public void onResponse(Call call, Response response) {
                                if (response.isSuccessful()) {
                                    ObjetoRes obj_estatus = (ObjetoRes) response.body();
                                    if (obj_estatus.geterror().equals("false")) {
                                        List<Spinner> arrayListMotCancelacion = Arrays.asList(obj_estatus.getmotivoscancelacion());
                                        Estatus estatus = new Estatus();

                                        for (Spinner spinner : arrayListMotCancelacion) {
                                            ContentValues values = new ContentValues();
                                            values.put("oid", spinner.getoid());
                                            values.put("nombre", spinner.getnombre());
                                            db.insert(SQLiteDBHelper.CatMotCanc_Table, null, values);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call call, Throwable t) {
                                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setMessage("Hubo un error en la descarga de datos iniciales, se cerrará la aplicación")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.putExtra("EXIT", true);
                                                startActivity(intent);
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        });
                    }

                    sql = "SELECT * FROM cat_productos";
                    record = db.rawQuery(sql, null);

                    if (record.getCount() <= 0) {
                        Call call = service.getCatalagoProductos(token);
                        call.enqueue(new Callback() {
                            @Override
                            public void onResponse(Call call, Response response) {
                                if (response.isSuccessful()) {
                                    ObjetoRes2 obj_estatus = (ObjetoRes2) response.body();
                                    if (obj_estatus.geterror().equals("false")) {
                                        List<CatalagoProducto> arrayListProductos = Arrays.asList(obj_estatus.getcatalogoProductos());
                                        Estatus estatus = new Estatus();

                                        for (CatalagoProducto producto : arrayListProductos) {
                                            ContentValues values = new ContentValues();
                                            values.put("oid", producto.getIdProducto());
                                            values.put("descripcion", producto.getdescripcion());
                                            values.put("unidad", producto.getunidad());
                                            values.put("precio_unitario", producto.getprecio_unitario());
                                            db.insert(SQLiteDBHelper.CatProductos_Table, null, values);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call call, Throwable t) {
                                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setMessage("Hubo un error en la descarga de datos iniciales, se cerrará la aplicación")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.putExtra("EXIT", true);
                                                startActivity(intent);
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        });
                    }

                    Call call = service.getPedidos(oid, "Pendiente", token);
                    call.enqueue(new Callback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            if (response.isSuccessful()) {
                                ObjetoRes resObj = (ObjetoRes) response.body();

                                if (resObj.geterror().equals("false")) {
                                    if (resObj.getpedido() != null) {
                                        List<Pedido> list = Arrays.asList(resObj.getpedido());

                                        for (Pedido pedido : list) {

                                            ContentValues values = new ContentValues();
                                            values.put("oid", pedido.getOid());
                                            String datetime = pedido.getfechaprogramada();
                                            datetime.replace('T', ' ');
                                            values.put("fecha_hora_programada", datetime);
                                            values.put("cliente", pedido.getcliente());
                                            values.put("direccion", pedido.getdireccion());
                                            values.put("cp", pedido.getcp());
                                            values.put("telefono", pedido.gettelefono());
                                            values.put("lat", pedido.getubicacion_lat());
                                            values.put("lon", pedido.getubicacion_long());
                                            values.put("comentario_cliente", pedido.getcomentarios_cliente());
                                            if (pedido.getsuma_iva() == null) {
                                                values.put("suma_iva", 0);
                                            } else {
                                                values.put("suma_iva", Double.parseDouble(pedido.getsuma_iva()));
                                            }
                                            if (pedido.gettotal() == null) {
                                                values.put("total", 0);
                                            } else {
                                                values.put("total", Double.parseDouble(pedido.gettotal()));
                                            }
                                            values.put("empresa", pedido.getEmpresa());
                                            values.put("tipo_pedido", pedido.gettipo_pedido());
                                            values.put("forma_pago", pedido.getForma_pago());
                                            values.put("estatus", pedido.getestatus());
                                            db.insert(SQLiteDBHelper.Pedidos_Table, null, values);

                                            if (pedido.getHobbies() != null) {

                                                List<Producto> listproductos = Arrays.asList(pedido.getHobbies());

                                                for (Producto producto : listproductos) {
                                                    ContentValues valuesProd = new ContentValues();
                                                    valuesProd.put("oid", producto.getOidProducto());
                                                    valuesProd.put("cantidad", producto.getCantidad());
                                                    valuesProd.put("surtido", producto.getsurtido());
                                                    valuesProd.put("precio", producto.getPrecio());
                                                    valuesProd.put("descripcion", producto.getdescripcion());
                                                    valuesProd.put("pedido", pedido.getOid());
                                                    db.insert(SQLiteDBHelper.Productos_Table, null, valuesProd);
                                                }
                                            }
                                        }

                                    } else {
                                        Toast.makeText(getApplicationContext(), "No existen Pedidos!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "No hay pedidos nuevos!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "error! ", Toast.LENGTH_SHORT).show();
                            }
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            java.util.Date date = new Date();
                            String fecha = dateFormat.format(date);

                            ContentValues values = new ContentValues();
                            values.put("fecha", fecha);
                            db.insert(SQLiteDBHelper.Synchro_Table, null, values);

                            setContentView(R.layout.activity_main);

                            //region Ejecucion hilo Impresora
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        FindBluetoothDevice();
                                        //openBluetoothPrinter();

                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }

                                }
                            }).start();
                            //endregion

                            objSessions = new Sessions();

                            strRolUsuario = ((Sessions) getApplicationContext()).getsesUsuarioRol();

                            sqLiteDBHelper = new SQLiteDBHelper(getApplicationContext());

                            String sql = "SELECT * FROM configuracion";

                            Cursor record = db.rawQuery(sql, null);

                            if (record.moveToFirst()) {
                                strIP = record.getString(record.getColumnIndex("ip"));
                                Imei = record.getString(record.getColumnIndex("imei"));
                                objSessions.setSesstrIpServidor(strIP);
                            }

                            mToolbar = (Toolbar) findViewById(R.id.toolbar);

                            setSupportActionBar(mToolbar);
                            getSupportActionBar().setDisplayShowHomeEnabled(true);

                            drawerFragment = (FragmentDrawer)
                                    getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);

                            drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
                            drawerFragment.setDrawerListener(MainActivity.this);

                            String oid = "", token = "", camion = "";
                            boolean admin = false;

                            sql = "SELECT * FROM usuario";

                            record = db.rawQuery(sql, null);

                            if (record.moveToFirst()) {
                                oid = record.getString(record.getColumnIndex("oid"));
                                camion = record.getString(record.getColumnIndex("camion"));
                                token = record.getString(record.getColumnIndex("token"));
                            }

                            ((Sessions) getApplicationContext().getApplicationContext()).setStrImei(Imei);
                            ((Sessions) getApplicationContext().getApplicationContext()).setStrChoferId(oid);
                            ((Sessions) getApplicationContext().getApplicationContext()).setStrCamionId(camion);
                            ((Sessions) getApplicationContext().getApplicationContext()).setsessToken(token);

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    obtener_pedidos();
                                    guardar_pedidos_productos();
                                    handler.postDelayed(this, 600000);
                                }
                            }, 600000);  //the time is in miliseconds

                            displayView(0);
                        }

                        @Override
                        public void onFailure(Call call, Throwable t) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage("Hubo un error en la descarga de datos iniciales, se cerrará la aplicación")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.putExtra("EXIT", true);
                                            startActivity(intent);
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    });
                }
            }else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                java.util.Date date = new Date();
                String fecha = dateFormat.format(date);

                ContentValues values = new ContentValues();
                values.put("fecha", fecha);
                db.insert(SQLiteDBHelper.Synchro_Table, null, values);

                setContentView(R.layout.activity_main);

                //region Ejecucion hilo Impresora
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FindBluetoothDevice();
                            //openBluetoothPrinter();

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }
                }).start();
                //endregion

                objSessions = new Sessions();

                strRolUsuario = ((Sessions) getApplicationContext()).getsesUsuarioRol();

                sqLiteDBHelper = new SQLiteDBHelper(getApplicationContext());

                sql = "SELECT * FROM configuracion";

                record = db.rawQuery(sql, null);

                if (record.moveToFirst()) {
                    strIP = record.getString(record.getColumnIndex("ip"));
                    Imei = record.getString(record.getColumnIndex("imei"));
                    objSessions.setSesstrIpServidor(strIP);
                }

                mToolbar = (Toolbar) findViewById(R.id.toolbar);

                setSupportActionBar(mToolbar);
                getSupportActionBar().setDisplayShowHomeEnabled(true);

                drawerFragment = (FragmentDrawer)
                        getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);

                drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
                drawerFragment.setDrawerListener(MainActivity.this);

                String oid = "", token = "", camion = "";

                sql = "SELECT * FROM usuario";

                record = db.rawQuery(sql, null);

                if (record.moveToFirst()) {
                    oid = record.getString(record.getColumnIndex("oid"));
                    camion = record.getString(record.getColumnIndex("camion"));
                    token = record.getString(record.getColumnIndex("token"));
                }

                ((Sessions) getApplicationContext().getApplicationContext()).setStrImei(Imei);
                ((Sessions) getApplicationContext().getApplicationContext()).setStrChoferId(oid);
                ((Sessions) getApplicationContext().getApplicationContext()).setStrCamionId(camion);
                ((Sessions) getApplicationContext().getApplicationContext()).setsessToken(token);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        obtener_pedidos();
                        handler.postDelayed(this, 600000);
                    }
                }, 600000);  //the time is in miliseconds

                displayView(0);
            }
        }
    }

    public void obtener_pedidos(){

        Toast.makeText(getApplicationContext(), "Actualizando...", Toast.LENGTH_SHORT).show();

        final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();
        String sql = "SELECT * FROM configuracion";
        Cursor record = db.rawQuery(sql, null);

        if (record.moveToFirst()) {
            strIP = record.getString(record.getColumnIndex("ip"));
            Imei = record.getString(record.getColumnIndex("imei"));
        }

        String oid = "", token = "";
        sql = "SELECT * FROM usuario";
        record = db.rawQuery(sql, null);

        if (record.moveToFirst()) {
            oid = record.getString(record.getColumnIndex("oid"));
            token = record.getString(record.getColumnIndex("token"));
        }

        BASEURL = strIP + "glpservices/webresources/glpservices/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServicioUsuario service = retrofit.create(ServicioUsuario.class);

        Call call = service.getPedidos(oid, "Pendiente", token);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    ObjetoRes resObj = (ObjetoRes) response.body();
                    if (resObj.geterror().equals("false")) {
                        if (resObj.getpedido() != null) {
                            List<Pedido> list = Arrays.asList(resObj.getpedido());
                            db.execSQL("DELETE FROM '" + SQLiteDBHelper.Pedidos_Table + "'");
                            db.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = '" + SQLiteDBHelper.Pedidos_Table + "'");
                            db.execSQL("DELETE FROM '" + SQLiteDBHelper.Productos_Table + "'");
                            db.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = '" + SQLiteDBHelper.Productos_Table + "'");
                            for (Pedido pedido : list) {
                                ContentValues values = new ContentValues();
                                values.put("oid", pedido.getOid());
                                String datetime = pedido.getfechaprogramada();
                                datetime.replace('T', ' ');
                                values.put("fecha_hora_programada", datetime);
                                values.put("cliente", pedido.getcliente());
                                values.put("direccion", pedido.getdireccion());
                                values.put("cp", pedido.getcp());
                                values.put("telefono", pedido.gettelefono());
                                values.put("lat", pedido.getubicacion_lat());
                                values.put("lon", pedido.getubicacion_long());
                                values.put("comentario_cliente", pedido.getcomentarios_cliente());
                                if (pedido.getsuma_iva() == null) {
                                    values.put("suma_iva", 0);
                                } else {
                                    values.put("suma_iva", Double.parseDouble(pedido.getsuma_iva()));
                                }
                                if (pedido.gettotal() == null) {
                                    values.put("total", 0);
                                } else {
                                    values.put("total", Double.parseDouble(pedido.gettotal()));
                                }
                                values.put("empresa", pedido.getEmpresa());
                                values.put("tipo_pedido", pedido.gettipo_pedido());
                                values.put("forma_pago", pedido.getForma_pago());
                                values.put("estatus", pedido.getestatus());
                                db.insert(SQLiteDBHelper.Pedidos_Table, null, values);

                                if (pedido.getHobbies() != null) {

                                    List<Producto> listproductos = Arrays.asList(pedido.getHobbies());

                                    for (Producto producto : listproductos) {
                                        ContentValues valuesProd = new ContentValues();
                                        valuesProd.put("oid", producto.getOidProducto());
                                        valuesProd.put("cantidad", producto.getCantidad());
                                        valuesProd.put("surtido", producto.getsurtido());
                                        valuesProd.put("precio", producto.getPrecio());
                                        valuesProd.put("descripcion", producto.getdescripcion());
                                        valuesProd.put("pedido", pedido.getOid());
                                        db.insert(SQLiteDBHelper.Productos_Table, null, valuesProd);
                                    }
                                }
                                final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();
                                String sql = "SELECT * FROM pedidos_modificados";
                                Cursor record = db.rawQuery(sql, null);
                                if(record.getCount()>0) {
                                    for (record.moveToFirst(); !record.isAfterLast(); record.moveToNext()) {
                                        try {
                                            db.delete(SQLiteDBHelper.Pedidos_Table,"oid = ?", new String[] {record.getString(record.getColumnIndex("oid"))});
                                        }catch(Exception ex){

                                        }
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "No existen Pedidos!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "No hay pedidos nuevos", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "error! ", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getApplicationContext(), "No hay conexión a Internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    String oid2[] = new  String[100];
    public void guardar_pedidos_productos(){
        BASEURL = strIP + "glpservices/webresources/glpservices/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();
        String sql = "SELECT * FROM pedidos_modificados";
        Cursor record = db.rawQuery(sql, null);
        if(record.getCount()>0) {
            for (record.moveToFirst(); !record.isAfterLast(); record.moveToNext()) {

                String token = "";
                sql = "SELECT * FROM usuario";
                Cursor recordUss = db.rawQuery(sql, null);

                if (recordUss.moveToFirst()) {
                    token = recordUss.getString(recordUss.getColumnIndex("token"));
                }

                ServicioUsuario userService = retrofit.create(ServicioUsuario.class);

                Call call = userService.up_pedido(record.getString(record.getColumnIndex("oid")),
                        record.getString(record.getColumnIndex("hora")),
                        record.getString(record.getColumnIndex("fecha")),
                        "",
                        record.getString(record.getColumnIndex("comentario_chofer")),
                        "",
                        "",
                        0,
                        0,
                        record.getString(record.getColumnIndex("pago_id")),
                        record.getString(record.getColumnIndex("motivo_cancelacion_id")),
                        record.getString(record.getColumnIndex("estatus_id")),
                        "Up_8",
                        token);

                call.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        if (response.isSuccessful()) {
                            ObjetoRes resObj = (ObjetoRes) response.body();
                            if (resObj.geterror().equals("false")) {
                                db.delete(SQLiteDBHelper.Pedidos_Table, "oid = ?", new String[]{resObj.getMessage()});
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                    }
                });
            }
        }

        sql = "SELECT * FROM productos_modificados";
        record = db.rawQuery(sql, null);
        int j = 0;
        if(record.getCount()>0){
            for (record.moveToFirst(); !record.isAfterLast(); record.moveToNext()) {

                sql = "SELECT * FROM configuracion";

                Cursor recordConf = db.rawQuery(sql, null);

                if (recordConf.moveToFirst()) {
                    strIP = recordConf.getString(recordConf.getColumnIndex("ip"));
                }

                String token = "";
                sql = "SELECT * FROM usuario";
                Cursor recordUss = db.rawQuery(sql, null);

                if (recordUss.moveToFirst()) {
                    token = recordUss.getString(recordUss.getColumnIndex("token"));
                }

                ServicioUsuario service = retrofit.create(ServicioUsuario.class);
                if(Boolean.getBoolean(record.getString(record.getColumnIndex("surtido")))){
                    oid2[j] = record.getString(record.getColumnIndex("oid"));
                    Call call = service.sumarProducto(Integer.parseInt(record.getString(record.getColumnIndex("cantidad"))),
                            Integer.parseInt(record.getString(record.getColumnIndex("precio"))),
                            record.getString(record.getColumnIndex("pedido_id")),
                            record.getString(record.getColumnIndex("producto_id")),
                            token);
                    call.enqueue(new Callback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            for(int i = 0;i < oid2.length; i++) {
                                try{
                                    db.delete(SQLiteDBHelper.Pedidos_Table, "oid = ?", new String[]{oid2[i]});
                                }catch(Exception e)
                                {

                                }
                            }
                        }
                        @Override
                        public void onFailure(Call call, Throwable t) {

                        }
                    });
                }else {
                    Call call = service.up_detalle(record.getString(record.getColumnIndex("oid")),
                            Integer.parseInt(record.getString(record.getColumnIndex("cantidad"))), false,
                            Integer.parseInt(record.getString(record.getColumnIndex("precio"))),
                            token);
                    call.enqueue(new Callback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            if (response.isSuccessful()) {
                                ObjetoRes resObj = (ObjetoRes) response.body();
                                if (resObj.geterror().equals("false")) {
                                    db.delete(SQLiteDBHelper.Pedidos_Table, "oid = ?", new String[]{resObj.getMessage()});
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call call, Throwable t) {

                        }
                    });
                }
                j++;
            }
        }

        sql = "SELECT * FROM pedidos_modificados";
        record = db.rawQuery(sql, null);
        if(record.getCount()>0){
            for (record.moveToFirst(); !record.isAfterLast(); record.moveToNext()) {

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.main, menu);
        if (ConexionEstablecida==true) {
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_conection_enable));
        }
        else{
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_conection_disable));}

      /*if(DispositivoEncontrado==false) {
    } */


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
            /*if(DispositivoEncontrado==true)
            {-*/
               // item.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_conection_enable));

            Toast.makeText(MainActivity.this, "Action clicked", Toast.LENGTH_SHORT).show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position)
    {
        displayView(position);
    }

    private void displayView(final int position) {
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
            //case 2:
            //    fragment = new MapsActivity();
            //    title = getString(R.string.title_mapa);
            //    break;
            case 2:
                if (strRolUsuario.equals("Admin")) {
                    Intent i = new Intent(MainActivity.this, Configuracion.class);
                    startActivity(i);
                    ((Activity) MainActivity.this).overridePendingTransition(0, 0);
                }
                if(strRolUsuario.equals("Operador")){
                    title = getString(R.string.title_pedidosrealizados);
                    fragment = new PedidosFragment();
                }

                break;

            case 3:
                if (strRolUsuario.equals("Admin")) {
                    title = getString(R.string.title_pedidosrealizados);
                    fragment = new PedidosFragment();
                }
                if(strRolUsuario.equals("Operador")){
                    LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    layout = layoutInflater.inflate(R.layout.layout_popup, null);

                    DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
                    int width = displayMetrics.widthPixels;
                    int height = displayMetrics.heightPixels;

                    layout.setVisibility(View.VISIBLE);
                    POPUP_WINDOW_CONFIRMACION = new PopupWindow(this);
                    POPUP_WINDOW_CONFIRMACION.setContentView(layout);
                    POPUP_WINDOW_CONFIRMACION.setWidth(width);
                    POPUP_WINDOW_CONFIRMACION.setHeight(height);
                    POPUP_WINDOW_CONFIRMACION.setFocusable(true);

                    POPUP_WINDOW_CONFIRMACION.setBackgroundDrawable(null);

                    POPUP_WINDOW_CONFIRMACION.showAtLocation(layout, Gravity.CENTER, 1, 1);

                    TextView txtMessage = (TextView) layout.findViewById(R.id.layout_popup_txtMessage);
                    txtMessage.setText("¿Desea cerrar sesión?");

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
                            POPUP_WINDOW_CONFIRMACION.dismiss();
                            Log.v(TAG,"token: " + position);
                            String strImei, strChofer, strCamion, strToken;

                            final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();

                            strImei = ((Sessions)getApplicationContext()).getStrImei();
                            strChofer = ((Sessions)getApplicationContext()).getStrChoferId();
                            strCamion = ((Sessions)getApplicationContext()).getStrCamionId();
                            strToken = ((Sessions)getApplicationContext()).getsessToken();

                            insertBitacora(false, strImei, strChofer, strCamion ,strToken);
                        }
                    });
                }
                break;

            case 4:
                LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                layout = layoutInflater.inflate(R.layout.layout_popup, null);

                DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
                int width = displayMetrics.widthPixels;
                int height = displayMetrics.heightPixels;

                layout.setVisibility(View.VISIBLE);
                POPUP_WINDOW_CONFIRMACION = new PopupWindow(this);
                POPUP_WINDOW_CONFIRMACION.setContentView(layout);
                POPUP_WINDOW_CONFIRMACION.setWidth(width);
                POPUP_WINDOW_CONFIRMACION.setHeight(height);
                POPUP_WINDOW_CONFIRMACION.setFocusable(true);

                POPUP_WINDOW_CONFIRMACION.setBackgroundDrawable(null);

                POPUP_WINDOW_CONFIRMACION.showAtLocation(layout, Gravity.CENTER, 1, 1);

                TextView txtMessage = (TextView) layout.findViewById(R.id.layout_popup_txtMessage);
                txtMessage.setText("¿Desea cerrar sesión?");

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
                        POPUP_WINDOW_CONFIRMACION.dismiss();
                        Log.v(TAG,"token: " + position);
                        String strImei, strChofer, strCamion, strToken;

                        final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();

                        strImei = ((Sessions)getApplicationContext()).getStrImei();
                        strChofer = ((Sessions)getApplicationContext()).getStrChoferId();
                        strCamion = ((Sessions)getApplicationContext()).getStrCamionId();
                        strToken = ((Sessions)getApplicationContext()).getsessToken();

                        insertBitacora(false, strImei, null, null,strToken);
                    }
                });
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

                        Call call = service.bitacora(evento, emai, chofer_id , camion_id , token, ((Sessions) getApplicationContext().getApplicationContext()).getStrFireTOken());
                        call.enqueue(new Callback() {
                            @Override
                            public void onResponse(Call call, Response response) {
                                if(response.isSuccessful()){
                                    ObjetoRes resObj = (ObjetoRes) response.body();

                                    if(resObj.geterror().equals("false")){

                                        final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();

                                        db.execSQL("DELETE FROM '" + SQLiteDBHelper.Usuario_Table + "'");
                                        db.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = '" + SQLiteDBHelper.Usuario_Table + "'");
                                        db.execSQL("DELETE FROM '" + SQLiteDBHelper.Pedidos_Table + "'");
                                        db.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = '" + SQLiteDBHelper.Pedidos_Table + "'");
                                        db.execSQL("DELETE FROM '" + SQLiteDBHelper.Productos_Table + "'");
                                        db.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = '" + SQLiteDBHelper.Productos_Table + "'");

                                        Log.d(TAG,"token: " + resObj.gettoken());

                                        Intent i2 = new Intent(MainActivity.this, LoginActivity.class);
                                        startActivity(i2);
                                        finish();
                                        ((Activity) MainActivity.this).overridePendingTransition(0,0);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setMessage("EL cierre de sesión no puede alcanzar el servidor, intente de nuevo")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    void FindBluetoothDevice(){

        try{
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(bluetoothAdapter==null){

               // DispositivoEncontrado=false;
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
                       // DispositivoEncontrado=true;
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
                    + "CLIENTE:" + cliente.toUpperCase()+" \n";

            BILL = BILL
                    + "DOMICILIO: " + direccion.toUpperCase()+"\n";



            BILL = BILL
                    + "-----------------------------------------------\n";




            BILL = BILL + String.format("%1$-10s %2$10s %3$13s %4$10s", "PRODUCTO", "CANT", "PRECIO", "IMPORTE");
            BILL = BILL + "\n";
            BILL = BILL
                    + "-----------------------------------------------";
            Producto[] producto = Sessions.getImpProductos() ;

            if(producto != null && producto.length != 0){
                for (int i=0; i < producto.length; i++)
                    BILL = BILL + "\n" +String.format("%1$-10s %2$10s %3$11s %4$10s", producto[i].getdescripcion(), producto[i].getCantidad(), "0", producto[i].getPrecio());
            }

            BILL = BILL
                    + "\n-----------------------------------------------";
            BILL = BILL + "\n";
            if(reImpresion){
                BILL = BILL
                        + "            R E  I M P R E S I O N        \n";

            }

            double subtotal = (Double.parseDouble(total)/1.16);
            double IVA = Double.parseDouble(total) - subtotal;

            BILL = BILL + "                          SUBTOTAL:" + "   $" + String.valueOf(subtotal) + "\n";
            BILL = BILL + "                            I.V.A.:" + "   $" + String.valueOf(IVA) + "\n";
            BILL = BILL + "                             TOTAL:" + "   $" + String.valueOf(Double.parseDouble(total)) + "\n";

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
