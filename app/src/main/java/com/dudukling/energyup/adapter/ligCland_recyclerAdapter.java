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
import com.dudukling.energyup.ligacoes_clandestinas.ligClandFormActivity;
import com.dudukling.energyup.dao.ligProvDAO;
import com.dudukling.energyup.model.ligClandModel;

import java.util.List;

public class ligCland_recyclerAdapter extends RecyclerView.Adapter {
//    private final ligClandFrag.OnItemSelectedListener listener;
    private Context context;

    private List<ligClandModel> lpClandest;

    public ligCland_recyclerAdapter(List<ligClandModel> lpClandest, Context context) {
        this.lpClandest = lpClandest;
        this.context = context;
//        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.lig_cland_item, parent, false);

        return new aViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        aViewHolder holder = (aViewHolder) viewHolder;
        ligClandModel clandest  = lpClandest.get(position);

        holder.viewClandestNome.setText("Clandestino #"+clandest.getId());
        holder.viewClandestEndereco.setText(clandest.getEndereco());
        holder.viewClandestOrdem.setText(clandest.getOrdem());
    }

    class aViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        final TextView viewClandestEndereco;
        final TextView viewClandestNome;
        final TextView viewClandestOrdem;

        aViewHolder(View view) {
            super(view);
            viewClandestNome = view.findViewById(R.id.textViewClandestNome);
            viewClandestEndereco = view.findViewById(R.id.textViewClandestEndereco);
            viewClandestOrdem = view.findViewById(R.id.textViewClandestOrdem);

            view.setOnCreateContextMenuListener(this);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    ligClandModel clandest  = lpClandest.get(position);

                    Intent goToFormActivity = new Intent(context, ligClandFormActivity.class);
                    goToFormActivity
                            .putExtra("clandest", clandest)
                            .putExtra("lpOrdem", "")
                            .putExtra("type", "readOnly");
                    context.startActivity(goToFormActivity);

//                    listener.onItemSelected(clandest, 0, "readOnly");
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
                    ligClandModel clandest  = lpClandest.get(position);

                    Intent goToFormActivity = new Intent(context, ligClandFormActivity.class);
                    goToFormActivity
                            .putExtra("clandest", clandest)
                            .putExtra("lpOrdem", "")
                            .putExtra("type", "edit");
                    context.startActivity(goToFormActivity);

//                    listener.onItemSelected(clandest, 0, "readOnly");

                    return false;
                }
            });

        MenuItem menuDelete = menu.add("Delete");
        menuDelete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int position = getAdapterPosition();
                ligClandModel clandest  = lpClandest.get(position);

                ligProvDAO dao = new ligProvDAO(context);
                dao.deleteClandestino(clandest);
                lpClandest = dao.getClandestinoList();
                dao.close();

                notifyDataSetChanged();

                return false;
            }
        });
        }

    }

    @Override
    public int getItemCount() {
        return lpClandest.size();
    }

}
