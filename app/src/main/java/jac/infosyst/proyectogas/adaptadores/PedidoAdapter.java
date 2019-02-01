package jac.infosyst.proyectogas.adaptadores;


import android.content.Context;
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

import jac.infosyst.proyectogas.R;


import jac.infosyst.proyectogas.FragmentDrawer;
import jac.infosyst.proyectogas.fragments.DetallePedidoFragment;
import jac.infosyst.proyectogas.fragments.PedidosFragment;
import jac.infosyst.proyectogas.modelo.Pedido;
import jac.infosyst.proyectogas.modelo.Pedidos;
import jac.infosyst.proyectogas.utils.Sessions;

import java.util.List;
import java.util.ArrayList;


public class PedidoAdapter  extends RecyclerView.Adapter<PedidoAdapter.ViewHolder> {


    private List<Pedido> pedidos;
    private Context mCtx;
    FragmentManager f_manager;
    private ArrayList<String> alPedidos = new ArrayList<>();

    private static final String TAG = "PedidoAdapter";

    private SparseBooleanArray selectedItems;
    private final ArrayList<Integer> selected = new ArrayList<>();


   public PedidoAdapter(List<Pedido> pedidos, Context mCtx, FragmentManager f_manager) {
        this.pedidos = pedidos;
        this.mCtx = mCtx;
        // this.listener = listener;
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
       // holder.textViewtotal.setText(pedido.gettotal());


        //holder.parentLayout.setBackgroundColor(Color.parseColor("#567845"));
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
                Log.d(TAG, "onClick: alPedidos: " + pedidos.get(position).getcliente());
                Log.d(TAG, "onClick: textViewCliente: " + pedidos.get(position));


                ((Sessions)mCtx.getApplicationContext()).setSesIdPedido(pedidos.get(position).getOid());
                String strIdPedido = String.valueOf(((Sessions)mCtx.getApplicationContext()).getSesIdPedido());

                Toast.makeText(mCtx, "Se selecciono: " + strIdPedido, Toast.LENGTH_SHORT).show();

                ((Sessions)mCtx.getApplicationContext()).setSesNombre(pedidos.get(position).getcliente());
                ((Sessions)mCtx.getApplicationContext()).setSesCliente(pedidos.get(position).getcliente());
                ((Sessions)mCtx.getApplicationContext()).setsesPlacas(pedidos.get(position).getplacas());
                ((Sessions)mCtx.getApplicationContext()).setsesfechaprogramada(pedidos.get(position).getfechaprogramada());
                ((Sessions)mCtx.getApplicationContext()).setsesEstatus(pedidos.get(position).getestatus());
                ((Sessions)mCtx.getApplicationContext()).setsesDireccion(pedidos.get(position).getdireccion());
                ((Sessions)mCtx.getApplicationContext()).setsetsescp(pedidos.get(position).getcp());
                ((Sessions)mCtx.getApplicationContext()).setsestelefono(pedidos.get(position).gettelefono());
                ((Sessions)mCtx.getApplicationContext()).setsescomentarioscliente(pedidos.get(position).getcomentarios_cliente());
              //  ((Sessions)mCtx.getApplicationContext()).setsessumaiva(pedidos.get(position).getsuma_iva());
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


        public ViewHolder(View itemView) {
            super(itemView);

            textViewCliente = (TextView) itemView.findViewById(R.id.textViewCliente);
            textViewDescripcion = (TextView) itemView.findViewById(R.id.textViewDescripcion);
            textViewEstatus = (TextView) itemView.findViewById(R.id.textViewEstatus);
            textViewDireccion = (TextView) itemView.findViewById(R.id.textViewDireccion);
            textViewdetalleproducto = (TextView) itemView.findViewById(R.id.textViewdetalleproducto);
            textViewfirmaurl = (TextView) itemView.findViewById(R.id.textViewfirmaurl);
            textViewtotal = (TextView) itemView.findViewById(R.id.textViewtotal);

            parentLayout = itemView.findViewById(R.id.parent_layout);
            relativeRow = itemView.findViewById(R.id.relativeRow);




        }

        LinearLayout parentLayout;
        final RelativeLayout relativeRow;

    }

}

