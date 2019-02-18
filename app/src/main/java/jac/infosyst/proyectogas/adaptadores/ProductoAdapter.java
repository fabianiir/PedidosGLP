package jac.infosyst.proyectogas.adaptadores;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import jac.infosyst.proyectogas.R;

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

import java.util.List;
import java.util.ArrayList;

public class ProductoAdapter  extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {

    private static final int DATABASE_VERSION = 1;
    protected static final String DATABASE_NAME = "proyectoGas";
    private List<Producto> productos;
    private Context mCtx;
    FragmentManager f_manager;
    private ArrayList<String> alProductos = new ArrayList<>();

    private static final String TAG = "ProductoAdapter";

    private static SQLiteDBHelper sqLiteDBHelper = null;
    private static String DB_NAME = "proyectogas16.db";
    private static int DB_VERSION = 1;

    ArrayList<Double> listPriceTotal;

    private String BASEURL = "";
    String strIP = "";

    private Fragment fragment;

    public ProductoAdapter(List<Producto> productos, Context mCtx, FragmentManager f_manager) {
        this.productos = productos;
        this.mCtx = mCtx;
        this.f_manager = f_manager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_productos, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ProductoAdapter.ViewHolder holder, final int position) {
        final Producto producto = productos.get(position);
        holder.textViewProducto.setText(""+producto.getdescripcion());
        holder.textViewPrecio.setText("$"+producto.getPrecio());
        holder.textViewCantidad.setText("Can:"+producto.getCantidad());

        holder.btnRestarProducto.setTag(producto.getOidProducto());

        listPriceTotal = new ArrayList<Double>();
        listPriceTotal.add(producto.getPrecio());

        ((Sessions)mCtx.getApplicationContext()).setSesarrayPriceTotal(listPriceTotal);

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((Sessions)mCtx.getApplicationContext()).setSesOidProducto(productos.get(position).getOidProducto());
                String strIdProducto = String.valueOf(((Sessions)mCtx.getApplicationContext()).getSesOidProducto());

                Toast.makeText(mCtx, "Producto selecciono: " + strIdProducto, Toast.LENGTH_SHORT).show();
            }
        });
        holder.btnRestarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Sessions)mCtx.getApplicationContext()).setSesOidProducto(productos.get(position).getOidProducto());


                if(((Sessions)mCtx.getApplicationContext()).getSesstrRestarProducto().equals("gone")){

                    Toast.makeText(mCtx, "No se puede restar el Producto!", Toast.LENGTH_SHORT).show();
                }else {

                    Toast.makeText(mCtx, "Restar producto: " + String.valueOf(((Sessions) mCtx.getApplicationContext()).getSesOidProducto()), Toast.LENGTH_SHORT).show();

                    restarProducto(String.valueOf(((Sessions) mCtx.getApplicationContext()).getSesOidProducto()), 1, false, (int) productos.get(position).getPrecio(), "f87b5f10-12d2-428d-8bf1-606150f73185");
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return productos.size();
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
        }
        LinearLayout parentLayout;
    }

    public void restarProducto(String idProducto, int cantidad, boolean surtido, int precio, String token){
        sqLiteDBHelper = new SQLiteDBHelper(mCtx , DATABASE_NAME, null, DATABASE_VERSION);
        final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();

        String sql = "SELECT * FROM config WHERE id = 1 ORDER BY id DESC limit 1";

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
}

