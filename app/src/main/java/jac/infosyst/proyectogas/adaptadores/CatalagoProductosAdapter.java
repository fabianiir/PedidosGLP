
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
import jac.infosyst.proyectogas.modelo.ObjetoRes;
import jac.infosyst.proyectogas.modelo.Pedido;
import jac.infosyst.proyectogas.modelo.Pedidos;
import jac.infosyst.proyectogas.modelo.Producto;
import jac.infosyst.proyectogas.utils.ApiUtils;
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
                Toast.makeText(mCtx, ""+ catalagoProductos.get(position).getdescripcion(), Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(mCtx, "Sumar producto: " + catalagoProductos.get(position).getIdProducto(), Toast.LENGTH_SHORT).show();
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
        Toast.makeText(mCtx, " storeSqLiteProductos:" + price, Toast.LENGTH_SHORT).show();

        sqLiteDBHelper = new SQLiteDBHelper(mCtx, DB_NAME, null, DB_VERSION);

        final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();
        ContentValues productosVal = new ContentValues();

        productosVal.put("precio", price);
        productosVal.put("Oid", String.valueOf(((Sessions)mCtx.getApplicationContext()).getSesIdPedido()));
        productosVal.put("activo", "uno");

        db.insert("productos", null, productosVal);
    }

    public void sumarProducto(int cantidad, int precio, String pedidoId, String productoId, String token){

        sqLiteDBHelper = new SQLiteDBHelper(mCtx, DB_NAME, null, DB_VERSION);

        SQLiteDatabase dbConn3 = sqLiteDBHelper.getWritableDatabase();
        String sql = "SELECT * FROM config WHERE id = 1 ORDER BY id DESC limit 1";

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

        Toast.makeText(mCtx, "/" + cantidad + "/" + precio +  "/" + pedidoId +  "/" + productoId +  "/" + token  , Toast.LENGTH_SHORT).show();

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    ObjetoRes resObj = (ObjetoRes) response.body();

                    if(resObj.geterror().equals("false")) {
                        Toast.makeText(mCtx, resObj.getMessage() , Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(mCtx, resObj.getMessage()  , Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(mCtx, "error agregar producto! " , Toast.LENGTH_SHORT).show();
                }


            }
            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(mCtx, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void restarProducto(int idProducto){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiUtils.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServicioUsuario service = retrofit.create(ServicioUsuario.class);

        Call<Result> call = service.actualizarProducto(idProducto);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Toast.makeText(mCtx, response.body().getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(mCtx, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}


