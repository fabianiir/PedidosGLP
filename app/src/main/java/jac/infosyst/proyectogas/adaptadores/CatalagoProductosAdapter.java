
package jac.infosyst.proyectogas.adaptadores;


import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import jac.infosyst.proyectogas.R;


import jac.infosyst.proyectogas.FragmentDrawer;
import jac.infosyst.proyectogas.fragments.DetallePedidoFragment;
import jac.infosyst.proyectogas.fragments.PedidosFragment;
import jac.infosyst.proyectogas.modelo.CatalagoProducto;
import jac.infosyst.proyectogas.modelo.ConfiguracionModelo;
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

import java.util.List;
import java.util.ArrayList;


public class CatalagoProductosAdapter  extends RecyclerView.Adapter<CatalagoProductosAdapter.ViewHolder> {


    private List<CatalagoProducto> catalagoProductos;
    private Context mCtx;
    FragmentManager f_manager;
    private static final String TAG = "CatalagoProductosAdapter";

    private static SQLiteDBHelper sqLiteDBHelper = null;
    private static String DB_NAME = "proyectogas17.db";
    private static int DB_VERSION = 1;


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
        holder.textViewProducto.setText(""+catalagoProducto.getdescripcion());
        holder.textViewprecio_unitario.setText(""+catalagoProducto.getprecio_unitario());


        holder.btnAgregarCatalagoProducto.setTag(catalagoProducto.getIdProducto());

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(mCtx, ""+ catalagoProductos.get(position).getdescripcion(), Toast.LENGTH_SHORT).show();

            }
        });

        holder.btnAgregarCatalagoProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(mCtx, "Sumar producto: " + catalagoProductos.get(position).getdescripcion(), Toast.LENGTH_SHORT).show();
                storeSqLiteProductos(catalagoProductos.get(position).getprecio_unitario());

                //holder.parentLayout.setVisibility(view.GONE);

            }
        });

    }


/*cuando elige que si; desahabilitarlo, unico habilitado el de reimprimir ticket
* validacion lista pedidos, seleccionar uno*/




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


