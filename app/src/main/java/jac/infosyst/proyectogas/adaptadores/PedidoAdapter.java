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
        Pedido pedido = pedidos.get(position);
        holder.textViewCliente.setText(pedido.getcliente());
        holder.textViewDescripcion.setText(pedido.getdescripcion());
        holder.textViewEstatus.setText(pedido.getestatus());
        holder.textViewDireccion.setText(pedido.getdireccion());

        holder.textViewdetalleproducto.setText(pedido.getdetalleproducto());
        holder.textViewfirmaurl.setText(pedido.getfirmaurl());
        holder.textViewtotal.setText(Double.toString(pedido.gettotal()));

        //holder.parentLayout.setBackgroundColor(Color.parseColor("#567845"));
        if (!selected.contains(position)){
            // view not selected
            holder.relativeRow.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        else
            // view is selected
            holder.relativeRow.setBackgroundColor(Color.parseColor("#004C7A"));


        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: alPedidos: " + pedidos.get(position).getcliente());
                Log.d(TAG, "onClick: textViewCliente: " + pedidos.get(position));



                ((Sessions)mCtx.getApplicationContext()).setSesIdPedido(pedidos.get(position).getId());
                String strIdPedido = String.valueOf(((Sessions)mCtx.getApplicationContext()).getSesIdPedido());

                Toast.makeText(mCtx, "Se selecciono: " + strIdPedido, Toast.LENGTH_SHORT).show();

                ((Sessions)mCtx.getApplicationContext()).setSesCliente(pedidos.get(position).getcliente());
                ((Sessions)mCtx.getApplicationContext()).setsesDireccion(pedidos.get(position).getdireccion());
                ((Sessions)mCtx.getApplicationContext()).setsesDescripcion(pedidos.get(position).getdescripcion());
                ((Sessions)mCtx.getApplicationContext()).setsesEstatus(pedidos.get(position).getestatus());
                ((Sessions)mCtx.getApplicationContext()).setsesDetalleProducto(pedidos.get(position).getdetalleproducto());
                ((Sessions)mCtx.getApplicationContext()).setsesFirmaURL(pedidos.get(position).getfirmaurl());
                ((Sessions)mCtx.getApplicationContext()).setsesTotal(Double.toString(pedidos.get(position).gettotal()));


               // view.setBackgroundColor(Color.CYAN);
                holder.relativeRow.setBackgroundColor(Color.parseColor("#004C7A"));




                if (selected.isEmpty()){
                    selected.add(position);
                }else {
                    int oldSelected = selected.get(0);
                    selected.clear();
                    selected.add(position);
                    notifyItemChanged(oldSelected);
                }

                //holder.relativeRow.setSelected(selectedItems.get(position, false));


//                holder.relativeRow.setSelected(selectedItems.get(((Sessions)mCtx.getApplicationContext()).getSesIdPedido(), false));




/*
                DetallePedidoFragment dpf = new DetallePedidoFragment(mCtx);


                FragmentTransaction transaction = f_manager.beginTransaction();
                transaction.replace(R.id.container_body, dpf);
                transaction.commit();

*/




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

