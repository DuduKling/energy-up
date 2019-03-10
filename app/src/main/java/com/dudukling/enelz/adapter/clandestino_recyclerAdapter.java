package com.dudukling.enelz.adapter;

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

import com.dudukling.enelz.R;
import com.dudukling.enelz.clandestinoFormActivity;
import com.dudukling.enelz.dao.lpDAO;
import com.dudukling.enelz.model.lpClandestino;

import java.util.List;

public class clandestino_recyclerAdapter extends RecyclerView.Adapter {
//    private final clandestinoFrag.OnItemSelectedListener listener;
    private Context context;

    private List<lpClandestino> lpClandest;

    public clandestino_recyclerAdapter(List<lpClandestino> lpClandest, Context context) {
        this.lpClandest = lpClandest;
        this.context = context;
//        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_clandestino, parent, false);

        return new aViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        aViewHolder holder = (aViewHolder) viewHolder;
        lpClandestino clandest  = lpClandest.get(position);

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
                    lpClandestino clandest  = lpClandest.get(position);

                    Intent goToFormActivity = new Intent(context, clandestinoFormActivity.class);
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
                    lpClandestino clandest  = lpClandest.get(position);

                    Intent goToFormActivity = new Intent(context, clandestinoFormActivity.class);
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
                lpClandestino clandest  = lpClandest.get(position);

                lpDAO dao = new lpDAO(context);
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
