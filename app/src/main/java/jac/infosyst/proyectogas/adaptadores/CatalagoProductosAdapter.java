
package jac.infosyst.proyectogas.adaptadores;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import jac.infosyst.proyectogas.MainActivity;
import jac.infosyst.proyectogas.R;


import jac.infosyst.proyectogas.FragmentDrawer;
import jac.infosyst.proyectogas.fragments.DetallePedidoFragment;
import jac.infosyst.proyectogas.fragments.PedidosFragment;
import jac.infosyst.proyectogas.modelo.CatalagoProducto;
import jac.infosyst.proyectogas.modelo.ConfiguracionModelo;
import jac.infosyst.proyectogas.modelo.Estatus;
import jac.infosyst.proyectogas.modelo.ObjetoRes;
import jac.infosyst.proyectogas.modelo.Pedido;
import jac.infosyst.proyectogas.modelo.Pedidos;
import jac.infosyst.proyectogas.modelo.Producto;
import jac.infosyst.proyectogas.utils.Result;
import jac.infosyst.proyectogas.utils.SQLiteDBHelper;
import jac.infosyst.proyectogas.utils.ServicioUsuario;
import jac.infosyst.proyectogas.utils.Sessions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static jac.infosyst.proyectogas.R.id.tv_cantidad;


public class CatalagoProductosAdapter  extends RecyclerView.Adapter<CatalagoProductosAdapter.ViewHolder> {

    private List<CatalagoProducto> catalagoProductos;
    private Context mCtx;
    FragmentManager f_manager;
    private static final String TAG = "CatalagoProductosAdapter";
    private PopupWindow POPUP_WINDOW_CANTIDAD;

    private static SQLiteDBHelper sqLiteDBHelper = null;
    private static String DB_NAME = "proyectogas17.db";
    private static int DB_VERSION = 1;
    private String BASEURL = "";
    String strIP = "";

    public CatalagoProductosAdapter(List<CatalagoProducto> catalagoProductos, Context mCtx, FragmentManager f_manager) {
        this.catalagoProductos = catalagoProductos;
        this.mCtx = mCtx;
        this.f_manager = f_manager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_catalago_productos, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CatalagoProductosAdapter.ViewHolder holder, final int position) {
        CatalagoProducto catalagoProducto = catalagoProductos.get(position);
        holder.textViewProducto.setText( "" + catalagoProducto.getdescripcion());
        holder.textViewprecio_unitario.setText( "" + catalagoProducto.getprecio_unitario());

        holder.btnAgregarCatalagoProducto.setTag(catalagoProducto.getIdProducto());

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        holder.btnAgregarCatalagoProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);

                final LayoutInflater inflater = (LayoutInflater) mCtx.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

View viewAlert = inflater.inflate(R.layout.layout_popup_cantidad,null);
                final EditText cantidad= (EditText) viewAlert.findViewById(R.id.tv_cantidad);
                builder.setView(viewAlert).setPositiveButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (!cantidad.getText().toString().isEmpty())
                                {


                                    int cantidadProducto = Integer.parseInt(cantidad.getText().toString());
                                    sumarProducto( cantidadProducto, (int) catalagoProductos.get(position).getprecio_unitario(), ((Sessions)mCtx.getApplicationContext()).getSesIdPedido(),
                                            catalagoProductos.get(position).getIdProducto(),  ((Sessions)mCtx.getApplicationContext()).getsessToken());


                                }
                                else{
                                    dialog.dismiss();
                                }

                                 }
                        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();


                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return catalagoProductos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewProducto, textViewprecio_unitario;
        public Button btnAgregarCatalagoProducto;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewProducto = (TextView) itemView.findViewById(R.id.textViewProducto);
            textViewprecio_unitario = (TextView) itemView.findViewById(R.id.textViewprecio_unitario);

            btnAgregarCatalagoProducto = (Button) itemView.findViewById(R.id.btnAgregarCatalagoProducto);

            parentLayout = itemView.findViewById(R.id.parent_layout_catalago_producto);
        }
        LinearLayout parentLayout;
    }


    public void storeSqLiteProductos(double price){

        sqLiteDBHelper = new SQLiteDBHelper(mCtx);

        final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();
        ContentValues productosVal = new ContentValues();

        productosVal.put("precio", price);
        productosVal.put("Oid", String.valueOf(((Sessions)mCtx.getApplicationContext()).getSesIdPedido()));
        productosVal.put("activo", "uno");


        db.insert("productos", null, productosVal);
    }

    public void sumarProducto(final int cantidad, final int precio, final String pedidoId, final String productoId, String token){

        sqLiteDBHelper = new SQLiteDBHelper(mCtx);

        SQLiteDatabase dbConn3 = sqLiteDBHelper.getWritableDatabase();
        String sql = "SELECT * FROM configuracion";

        final Cursor record = dbConn3.rawQuery(sql, null);

        if (record.moveToFirst()) {
            strIP = record.getString(record.getColumnIndex("ip"));
        }

        BASEURL = strIP + "glpservices/webresources/glpservices/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServicioUsuario service = retrofit.create(ServicioUsuario.class);

        Call call = service.sumarProducto(cantidad, precio, pedidoId, productoId, ((Sessions)mCtx.getApplicationContext()).getsessToken());
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    ObjetoRes resObj = (ObjetoRes) response.body();

                    if(resObj.geterror().equals("false")) {
                        sqLiteDBHelper = new SQLiteDBHelper(mCtx);
                        SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();
                        String sql = "SELECT * FROM cat_productos WHERE oid = '" + productoId + "'";
                        Cursor cursorFr = db.rawQuery(sql, null);
                        String descripcion = "";
                        Cursor cursorPr = db.rawQuery(sql, null);
                        if (cursorFr.getCount() > 0) {
                            for (cursorFr.moveToFirst(); !cursorFr.isAfterLast(); cursorFr.moveToNext()) {
                                descripcion = cursorFr.getString(cursorPr.getColumnIndex("descripcion"));
                                sql = "SELECT * FROM productos WHERE pedido = '" + pedidoId + "' AND descripcion = '" + cursorFr.getString(cursorPr.getColumnIndex("descripcion")) + "'";
                                cursorPr = db.rawQuery(sql, null);
                            }
                        }
                        if (cursorPr.getCount() > 0){
                            int CantidadSuma = 0;
                            for (cursorPr.moveToFirst(); !cursorPr.isAfterLast(); cursorPr.moveToNext()) {
                                CantidadSuma = Integer.parseInt(cursorPr.getString(cursorPr.getColumnIndex("cantidad"))) + cantidad;
                            }


                            sql = "SELECT * FROM cat_productos WHERE oid = '" + productoId + "'";
                            cursorPr = db.rawQuery(sql, null);

                            for (cursorPr.moveToFirst(); !cursorPr.isAfterLast(); cursorPr.moveToNext()) {
                                int precioMult = CantidadSuma * Integer.parseInt(cursorPr.getString(cursorPr.getColumnIndex("precio_unitario")));

                                ContentValues values = new ContentValues();
                                values.put("oid", resObj.getMessage());
                                values.put("cantidad", CantidadSuma);
                                values.put("surtido", true);
                                values.put("precio", precioMult);
                                values.put("pedido_id", pedidoId);
                                values.put("producto_id", productoId);
                                db.insert(SQLiteDBHelper.Productos_Mod_Table, null, values);

                                values = new ContentValues();
                                values.put("oid", resObj.getMessage());
                                values.put("cantidad", CantidadSuma);
                                values.put("surtido", true);
                                values.put("precio", precioMult);
                                values.put("descripcion", cursorPr.getString(cursorPr.getColumnIndex("descripcion")));
                                values.put("pedido", pedidoId);
                                db.update(SQLiteDBHelper.Productos_Table, values, "pedido = ? AND descripcion = ?", new String[] { pedidoId, descripcion });
                            }
                        }else {
                            int CantidadSuma = 0;
                            for (cursorFr.moveToFirst(); !cursorFr.isAfterLast(); cursorFr.moveToNext()) {
                                CantidadSuma = cantidad;
                                int precioMult = CantidadSuma * Integer.parseInt(cursorFr.getString(cursorFr.getColumnIndex("precio_unitario")));

                                ContentValues values = new ContentValues();
                                values.put("oid", resObj.getMessage());
                                values.put("cantidad", cantidad);
                                values.put("surtido", true);
                                values.put("precio", precioMult);
                                values.put("pedido_id", pedidoId);
                                values.put("producto_id", productoId);
                                db.insert(SQLiteDBHelper.Productos_Mod_Table, null, values);

                                sql = "SELECT * FROM cat_productos WHERE oid = '" + productoId + "'";
                                cursorPr = db.rawQuery(sql, null);

                                values = new ContentValues();
                                values.put("oid", resObj.getMessage());
                                values.put("cantidad", cantidad);
                                values.put("surtido", true);
                                values.put("precio", precioMult);
                                values.put("descripcion", cursorFr.getString(cursorFr.getColumnIndex("descripcion")));
                                values.put("pedido", pedidoId);

                                db.insert(SQLiteDBHelper.Productos_Table, null, values);
                            }
                        }
                        Toast.makeText(mCtx, "Producto agregado." , Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mCtx, resObj.getMessage()  , Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(mCtx, "error al agregar producto! " , Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call call, Throwable t) {
                sqLiteDBHelper = new SQLiteDBHelper(mCtx);
                SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();
                String sql = "SELECT * FROM cat_productos WHERE oid = '" + productoId + "'";
                Cursor cursorFr = db.rawQuery(sql, null);
                String descripcion = "";
                Cursor cursorPr = db.rawQuery(sql, null);
                if (cursorFr.getCount() > 0) {
                    for (cursorFr.moveToFirst(); !cursorFr.isAfterLast(); cursorFr.moveToNext()) {
                        descripcion = cursorFr.getString(cursorPr.getColumnIndex("descripcion"));
                        sql = "SELECT * FROM productos WHERE pedido = '" + pedidoId + "' AND descripcion = '" + cursorFr.getString(cursorPr.getColumnIndex("descripcion")) + "'";
                        cursorPr = db.rawQuery(sql, null);
                    }
                }
                if (cursorPr.getCount() > 0){
                    int CantidadSuma = 0;
                    for (cursorPr.moveToFirst(); !cursorPr.isAfterLast(); cursorPr.moveToNext()) {
                        CantidadSuma = Integer.parseInt(cursorPr.getString(cursorPr.getColumnIndex("cantidad"))) + cantidad;
                    }


                    sql = "SELECT * FROM cat_productos WHERE oid = '" + productoId + "'";
                    cursorPr = db.rawQuery(sql, null);

                    for (cursorPr.moveToFirst(); !cursorPr.isAfterLast(); cursorPr.moveToNext()) {
                        int precioMult = CantidadSuma * Integer.parseInt(cursorPr.getString(cursorPr.getColumnIndex("precio_unitario")));

                        ContentValues values = new ContentValues();
                        values.put("oid", random());
                        values.put("cantidad", CantidadSuma);
                        values.put("surtido", true);
                        values.put("precio", precioMult);
                        values.put("pedido_id", pedidoId);
                        values.put("producto_id", productoId);
                        db.insert(SQLiteDBHelper.Productos_Mod_Table, null, values);

                        values = new ContentValues();
                        values.put("oid", random());
                        values.put("cantidad", CantidadSuma);
                        values.put("surtido", true);
                        values.put("precio", precioMult);
                        values.put("descripcion", cursorPr.getString(cursorPr.getColumnIndex("descripcion")));
                        values.put("pedido", pedidoId);
                        db.update(SQLiteDBHelper.Productos_Table, values, "pedido = ? AND descripcion = ?", new String[] { pedidoId, descripcion });
                    }
                }else {
                    int CantidadSuma = 0;
                    for (cursorFr.moveToFirst(); !cursorFr.isAfterLast(); cursorFr.moveToNext()) {
                        int precioMult = CantidadSuma * Integer.parseInt(cursorFr.getString(cursorFr.getColumnIndex("precio_unitario")));

                        ContentValues values = new ContentValues();
                        values.put("oid", random());
                        values.put("cantidad", cantidad);
                        values.put("surtido", true);
                        values.put("precio", precioMult);
                        values.put("pedido_id", pedidoId);
                        values.put("producto_id", productoId);
                        db.insert(SQLiteDBHelper.Productos_Mod_Table, null, values);

                        sql = "SELECT * FROM cat_productos WHERE oid = '" + productoId + "'";
                        cursorPr = db.rawQuery(sql, null);

                        values = new ContentValues();
                        values.put("oid", random());
                        values.put("cantidad", cantidad);
                        values.put("surtido", true);
                        values.put("precio", precioMult);
                        values.put("descripcion", cursorFr.getString(cursorFr.getColumnIndex("descripcion")));
                        values.put("pedido", pedidoId);

                        db.insert(SQLiteDBHelper.Productos_Table, null, values);
                    }
                }
                Toast.makeText(mCtx, "Producto agregado." , Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(16);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}