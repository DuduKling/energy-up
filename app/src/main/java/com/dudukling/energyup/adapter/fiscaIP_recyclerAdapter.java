package com.dudukling.energyup.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dudukling.energyup.R;
import com.dudukling.energyup.dao.fiscaIPDAO;
import com.dudukling.energyup.fiscalizacao_ilumunacao_publica.fiscaIPFormActivity;
import com.dudukling.energyup.model.fiscaIPModel;

import java.util.List;

public class fiscaIP_recyclerAdapter extends RecyclerView.Adapter {
    private Context context;
    public static fiscaIP_recyclerAdapter context2;
    public static List<fiscaIPModel> fiscaList;

    public fiscaIP_recyclerAdapter(Context context) {
        fiscaIPDAO dao = new fiscaIPDAO(context);
        this.fiscaList = dao.getFiscaList();
        dao.close();

        this.context = context;
        this.context2 = this;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fisca_ip_item, parent, false);

        return new aViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        aViewHolder holder = (aViewHolder) viewHolder;
        fiscaIPModel fisca  = fiscaList.get(position);

        holder.textViewFiscaIPItemLuminaria.setText(fisca.getTipo_luminaria());
        holder.textViewFiscaIPItemPotencia.setText(fisca.getPotencia());
        holder.textViewFiscaIPItemEndereco.setText(fisca.getEndereco());
    }

    class aViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        final TextView textViewFiscaIPItemLuminaria;
        final TextView textViewFiscaIPItemPotencia;
        final TextView textViewFiscaIPItemEndereco;

        aViewHolder(View view) {
            super(view);
            textViewFiscaIPItemLuminaria = view.findViewById(R.id.textViewFiscaIPItemLuminaria);
            textViewFiscaIPItemPotencia = view.findViewById(R.id.textViewFiscaIPItemPotencia);
            textViewFiscaIPItemEndereco = view.findViewById(R.id.textViewFiscaIPItemEndereco);

            view.setOnCreateContextMenuListener(this);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    fiscaIPModel fisca  = fiscaList.get(position);

                    Intent goToFormActivity = new Intent(context, fiscaIPFormActivity.class);
                    goToFormActivity
                            .putExtra("fisca", fisca)
                            .putExtra("type", "readOnly");
                    context.startActivity(goToFormActivity);
                }
            });
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem menuEdit = menu.add("Editar");
            menuEdit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int position = getAdapterPosition();
                    fiscaIPModel fisca  = fiscaList.get(position);

                    Intent goToFormActivity = new Intent(context, fiscaIPFormActivity.class);
                    goToFormActivity
                            .putExtra("fisca", fisca)
                            .putExtra("type", "edit");
                    context.startActivity(goToFormActivity);

                    return false;
                }
            });

            MenuItem menuDelete = menu.add("Deletar");
            menuDelete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int position = getAdapterPosition();
                    fiscaIPModel fisca  = fiscaList.get(position);

                    fiscaIPDAO dao = new fiscaIPDAO(context);
                    dao.delete(fisca);
                    dao.close();

                    fiscaList = dao.getFiscaList();
                    notifyDataSetChanged();

                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return fiscaList.size();
    }

}
