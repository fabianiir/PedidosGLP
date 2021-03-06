package jac.infosyst.proyectogas.adaptadores;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import jac.infosyst.proyectogas.MainActivity;
import jac.infosyst.proyectogas.R;

import jac.infosyst.proyectogas.FragmentDrawer;
import jac.infosyst.proyectogas.fragments.DetallePedidoFragment;
import jac.infosyst.proyectogas.fragments.PedidosFragment;
import jac.infosyst.proyectogas.fragments.ReimpresionPedidoFragment;
import jac.infosyst.proyectogas.modelo.Pedido;
import jac.infosyst.proyectogas.modelo.Pedidos;
import jac.infosyst.proyectogas.modelo.Producto;
import jac.infosyst.proyectogas.utils.SQLiteDBHelper;
import jac.infosyst.proyectogas.utils.Sessions;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;


public class PedidoAdapter  extends RecyclerView.Adapter<PedidoAdapter.ViewHolder> {

    private static final int DATABASE_VERSION = 1;
    protected static final String DATABASE_NAME = "proyectoGas";
    private List<Pedido> pedidos;
    private Context mCtx;
    FragmentManager f_manager;
    private ArrayList<String> alPedidos = new ArrayList<>();

    private static final String TAG = "PedidoAdapter";

    private SparseBooleanArray selectedItems;
    private final ArrayList<Integer> selected = new ArrayList<>();

    private static SQLiteDBHelper sqLiteDBHelper = null;
    private static String DB_NAME = "proyectogas17.db";
    private static int DB_VERSION = 1;

   public PedidoAdapter(List<Pedido> pedidos, Context mCtx, FragmentManager f_manager) {
        this.pedidos = pedidos;
        this.mCtx = mCtx;

        this.f_manager = f_manager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_pedidos, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final PedidoAdapter.ViewHolder holder, final int position) {
            final Pedido pedido = pedidos.get(position);
        holder.textViewCliente.setText(pedido.getcliente());
        holder.textViewDescripcion.setText(pedido.getplacas());
        holder.textViewEstatus.setText(pedido.getcp());
        holder.textViewDireccion.setText(pedido.getdireccion());

        holder.textViewdetalleproducto.setText(pedido.getnombre());
        holder.textViewfirmaurl.setText(pedido.gettelefono());
        holder.iconoPedido.setColorFilter(Color.BLUE);

        if(pedido.gettipo_pedido().equals("Fuga"))
        {
            holder.iconoPedido.setColorFilter(Color.RED);

        }


            if (pedido.getHobbies() != null) {

            for (Producto hobby : pedido.getHobbies()) {
                Log.d("pedido: ", pedido.getOid());
            }
        }else{
            ((Sessions)mCtx.getApplicationContext()).setsesTotal("0");

                Log.d("pedido productos null: ", pedido.getOid());
        }

        if (!selected.contains(position)){
            // view not selected
            holder.relativeRow.setBackgroundColor(Color.parseColor("#ffffff"));
            holder.textViewCliente.setTextColor(Color.parseColor("#000000"));
            holder.textViewDescripcion.setTextColor(Color.parseColor("#000000"));
            holder.textViewDireccion.setTextColor(Color.parseColor("#000000"));
            holder.textViewEstatus.setTextColor(Color.parseColor("#000000"));
        }
        else
            // view is selected
            holder.relativeRow.setBackgroundColor(Color.parseColor("#004C7A"));


        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Sessions)mCtx.getApplicationContext()).setSestipo_pedido(pedidos.get(position).gettipo_pedido());
                ((Sessions)mCtx.getApplicationContext()).setSesubicacion_latitude(pedidos.get(position).getubicacion_lat());
                ((Sessions)mCtx.getApplicationContext()).setSesubicacion_longitude(pedidos.get(position).getubicacion_long());

                Log.d(TAG, "onClick: alPedidos: " + pedidos.get(position).getcliente());
                Log.d(TAG, "onClick: textViewCliente: " + pedidos.get(position));

                ((Sessions)mCtx.getApplicationContext()).setSesIdPedido(pedidos.get(position).getOid());
                String strIdPedido = String.valueOf(((Sessions)mCtx.getApplicationContext()).getSesIdPedido());


                ((Sessions)mCtx.getApplicationContext()).setSesNombre(pedidos.get(position).getcliente());
                ((Sessions)mCtx.getApplicationContext()).setSesCliente(pedidos.get(position).getcliente());
                ((Sessions)mCtx.getApplicationContext()).setsesPlacas(pedidos.get(position).getplacas());
                ((Sessions)mCtx.getApplicationContext()).setsesfechaprogramada(pedidos.get(position).getfechaprogramada());
                ((Sessions)mCtx.getApplicationContext()).setsesEstatus(pedidos.get(position).getestatus());
                ((Sessions)mCtx.getApplicationContext()).setsesDireccion(pedidos.get(position).getdireccion());
                ((Sessions)mCtx.getApplicationContext()).setsetsescp(pedidos.get(position).getcp());
                ((Sessions)mCtx.getApplicationContext()).setsestelefono(pedidos.get(position).gettelefono());
                ((Sessions)mCtx.getApplicationContext()).setsescomentarioscliente(pedidos.get(position).getcomentarios_cliente());
                ((Sessions)mCtx.getApplicationContext()).setSesDetalleProductoSurtir(pedidos.get(position).getHobbies());
                ((Sessions)mCtx.getApplicationContext()).setsesTotal(pedidos.get(position).gettotal());

               // view.setBackgroundColor(Color.CYAN);
                holder.relativeRow.setBackgroundColor(Color.parseColor("#004C7A"));
                holder.textViewCliente.setTextColor(Color.parseColor("#ffffff"));
                holder.textViewDescripcion.setTextColor(Color.parseColor("#ffffff"));
                holder.textViewDireccion.setTextColor(Color.parseColor("#ffffff"));
                holder.textViewEstatus.setTextColor(Color.parseColor("#ffffff"));


                if (selected.isEmpty()){
                    selected.add(position);
                }else {
                    int oldSelected = selected.get(0);
                    selected.clear();
                    selected.add(position);
                    notifyItemChanged(oldSelected);
                }

                if(MainActivity.getDispositivoEncontrado())
                {
                    Toast.makeText(mCtx,"Impresora conectada", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(mCtx,"Impresora no conectada", Toast.LENGTH_SHORT).show();
                }
                if(((Sessions)mCtx.getApplicationContext()).getSesIdPedido().equals("null")) {

                    Toast.makeText(mCtx,  "Debe seleccionar un Pedido!" , Toast.LENGTH_SHORT).show();
                }
                else{
                    if(((Sessions)mCtx.getApplicationContext()).getBoolPedidosRealizados()){
                        MainActivity mainActivity = (MainActivity) mCtx;
                        Fragment fragment = new Fragment();
                        ReimpresionPedidoFragment rpf = new ReimpresionPedidoFragment(mCtx);
                        FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container_body, rpf);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }else {
                        MainActivity mainActivity = (MainActivity) mCtx;
                        Fragment fragment = new Fragment();
                        DetallePedidoFragment dpf = new DetallePedidoFragment(mCtx);
                        FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container_body, dpf);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                }
            }
        });
    }

    public void setFragment(Fragment frag){
        FragmentTransaction transaction = f_manager.beginTransaction();
        transaction.replace(R.id.container_body, frag);
    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewCliente;
        public TextView textViewDescripcion;
        public TextView textViewEstatus;
        public TextView textViewDireccion;
        public TextView textViewdetalleproducto;
        public TextView textViewfirmaurl;
        public TextView textViewtotal;
        public ImageView iconoPedido;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewCliente = (TextView) itemView.findViewById(R.id.textViewCliente);
            textViewDescripcion = (TextView) itemView.findViewById(R.id.textViewDescripcion);
            textViewEstatus = (TextView) itemView.findViewById(R.id.textViewEstatus);
            textViewDireccion = (TextView) itemView.findViewById(R.id.textViewDireccion);
            textViewdetalleproducto = (TextView) itemView.findViewById(R.id.textViewdetalleproducto);
            textViewfirmaurl = (TextView) itemView.findViewById(R.id.textViewfirmaurl);
            textViewtotal = (TextView) itemView.findViewById(R.id.textViewtotal);
            iconoPedido=(ImageView)itemView.findViewById(R.id.iconoPedido);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            relativeRow = itemView.findViewById(R.id.relativeRow);
        }

        LinearLayout parentLayout;
        final RelativeLayout relativeRow;

    }

    public void storeSqLiteProductos(String oidPedido, String oidProducto, int cantidad, boolean surtido, double precio, String descripcion){
        Toast.makeText(mCtx, " storeSqLiteProductos:" + descripcion, Toast.LENGTH_SHORT).show();

        sqLiteDBHelper = new SQLiteDBHelper(mCtx);

        final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();
        ContentValues productosVal = new ContentValues();
        productosVal.put("OidPedido", oidPedido);
        productosVal.put("OidProducto", oidProducto);
        productosVal.put("cantidad", cantidad);
        productosVal.put("surtido", surtido);
        productosVal.put("precio", precio);
        productosVal.put("descripcion", descripcion);
        productosVal.put("activo", "uno");

        db.insert("productos", null, productosVal);
    }
}