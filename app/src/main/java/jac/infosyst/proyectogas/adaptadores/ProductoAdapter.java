package jac.infosyst.proyectogas.adaptadores;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import jac.infosyst.proyectogas.MainActivity;
import jac.infosyst.proyectogas.R;

import jac.infosyst.proyectogas.fragments.PedidosFragment;
import jac.infosyst.proyectogas.fragments.SurtirPedidoFragment;
import jac.infosyst.proyectogas.modelo.ObjetoRes;
import jac.infosyst.proyectogas.modelo.Producto;
import jac.infosyst.proyectogas.utils.SQLiteDBHelper;
import jac.infosyst.proyectogas.utils.ServicioUsuario;
import jac.infosyst.proyectogas.utils.Sessions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.text.NumberFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.zip.Inflater;

public class ProductoAdapter  extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {

    private static final int DATABASE_VERSION = 1;
    protected static final String DATABASE_NAME = "proyectoGas";
    private List<Producto> productos;
    private Context mCtx;
    FragmentManager f_manager;
    private ArrayList<String> alProductos = new ArrayList<>();

    private static final String TAG = "ProductoAdapter";

    private static SQLiteDBHelper sqLiteDBHelper = null;
    private static String DB_NAME = "proyectogas17.db";
    private static int DB_VERSION = 1;

    ArrayList<Double> listPriceTotal;

    private String BASEURL = "";
    String strIP = "";

    private Fragment fragment;
    private boolean pendiente;

    int mCurrentPlayingPosition = -1;
    public ProductoAdapter(List<Producto> productos, Context mCtx, FragmentManager f_manager, boolean pendiente) {
        this.productos = productos;
        this.mCtx = mCtx;
        this.f_manager = f_manager;
        this.pendiente = pendiente;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(pendiente){
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_productos, parent, false);
            return new ViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_productosdisable, parent, false);
            return new ViewHolder(v);
        }
    }



    @Override
    public void onBindViewHolder(final ProductoAdapter.ViewHolder holder, final int position) {
        holder.getOldPosition();

        final Producto producto = productos.get(position);
        holder.textViewProducto.setText("" + producto.getdescripcion());
        NumberFormat format = NumberFormat.getCurrencyInstance();
        holder.textViewPrecio.setText(format.format(producto.getPrecio()));
        holder.textViewCantidad.setText("Can:" + producto.getCantidad());

        holder.btnRestarProducto.setTag(producto.getOidProducto());

        listPriceTotal = new ArrayList<Double>();
        listPriceTotal.add(producto.getPrecio());

        ((Sessions)mCtx.getApplicationContext()).setSesarrayPriceTotal(listPriceTotal);



        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCurrentPlayingPosition=position;
                ((Sessions)mCtx.getApplicationContext()).setSesOidProducto(productos.get(position).getOidProducto());

                String strIdProducto = String.valueOf(((Sessions)mCtx.getApplicationContext()).getSesOidProducto());

                sqLiteDBHelper = new SQLiteDBHelper(mCtx);
                SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();

                String sql3 = "SELECT * FROM productos WHERE oid = '" + strIdProducto + "'";

                Cursor cursor3 = db.rawQuery(sql3, null);
                String descripcion = "";

                if (cursor3.moveToFirst()) {
                    descripcion = cursor3.getString(cursor3.getColumnIndex("descripcion"));
                }







                    notifyDataSetChanged();

                    Toast.makeText(mCtx, "Producto seleccionado: " + descripcion, Toast.LENGTH_SHORT).show();





            }

        });

        if (mCurrentPlayingPosition==position){
            holder.relativeRow.setBackgroundColor(Color.parseColor("#004C7A"));
            holder.textViewCantidad.setTextColor(Color.parseColor("#ffffff"));
            holder.textViewPrecio.setTextColor(Color.parseColor("#ffffff"));
            holder.textViewProducto.setTextColor(Color.parseColor("#ffffff"));
        }
        else
        {
            holder.relativeRow.setBackgroundColor(Color.parseColor("#ffffff"));
            holder.textViewCantidad.setTextColor(Color.parseColor("#000000"));
            holder.textViewPrecio.setTextColor(Color.parseColor("#000000"));
            holder.textViewProducto.setTextColor(Color.parseColor("#000000"));
        }
        holder.btnRestarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Sessions)mCtx.getApplicationContext()).setSesOidProducto(productos.get(position).getOidProducto());


                if(((Sessions)mCtx.getApplicationContext()).getSesstrRestarProducto().equals("gone")){

                    Toast.makeText(mCtx, "No se puede restar el Producto!", Toast.LENGTH_SHORT).show();
                }else {
                    restarProducto(String.valueOf(((Sessions) mCtx.getApplicationContext()).getSesOidProducto()), 1, false, (int) productos.get(position).getPrecio(), String.valueOf(((Sessions) mCtx.getApplicationContext()).getsessToken()));
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        if(productos == null){
            return 0;
        }else{
            return productos.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewProducto,textViewPrecio, textViewCantidad ;
        public Button btnRestarProducto;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewProducto = (TextView) itemView.findViewById(R.id.textViewProducto);
            textViewPrecio = (TextView) itemView.findViewById(R.id.textViewPrecio);
            textViewCantidad = (TextView) itemView.findViewById(R.id.textViewCantidad);

            btnRestarProducto = (Button) itemView.findViewById(R.id.btnRestarProducto);

            parentLayout = itemView.findViewById(R.id.parent_layout_producto);
            relativeRow=itemView.findViewById(R.id.relative_producto);
        }
        LinearLayout parentLayout;
        final RelativeLayout relativeRow;
    }

    public void restarProducto(final String idProducto, final int cantidad, boolean surtido, final int precio, String token){
        sqLiteDBHelper = new SQLiteDBHelper(mCtx);
        final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();

        String sql = "SELECT * FROM configuracion";

        final Cursor record = db.rawQuery(sql, null);

        if (record.moveToFirst()) {
            strIP = record.getString(record.getColumnIndex("ip"));
        }

        BASEURL = strIP + "glpservices/webresources/glpservices/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServicioUsuario service = retrofit.create(ServicioUsuario.class);

        Call call = service.up_detalle(idProducto, cantidad, surtido,  precio, token);

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    ObjetoRes resObj = (ObjetoRes) response.body();

                    if(resObj.geterror().equals("false")) {
                        Toast.makeText(mCtx, resObj.getMessage() , Toast.LENGTH_SHORT).show();
                        db.delete(SQLiteDBHelper.Productos_Mod_Table, "oid = ?", new String[]{idProducto});
                        db.delete(SQLiteDBHelper.Productos_Table, "oid = ?", new String[]{idProducto});
                    } else {
                        Toast.makeText(mCtx, resObj.getMessage()  , Toast.LENGTH_SHORT).show();
                    }
                } else {
                    db.delete(SQLiteDBHelper.Productos_Mod_Table, "oid = ?", new String[]{idProducto});
                    db.delete(SQLiteDBHelper.Productos_Table, "oid = ?", new String[]{idProducto});
                }
            }
            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(mCtx, t.getMessage(), Toast.LENGTH_SHORT).show();
                sqLiteDBHelper = new SQLiteDBHelper(mCtx);
                SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("oid", idProducto);
                values.put("cantidad", cantidad);
                values.put("surtido", false);
                values.put("precio", precio);
                db.insert(SQLiteDBHelper.Productos_Mod_Table, null, values);
                Toast.makeText(mCtx, t.getMessage(), Toast.LENGTH_SHORT).show();

                db.delete(SQLiteDBHelper.Productos_Table, "oid = ?", new String[]{idProducto});
            }
        });
    }
}

