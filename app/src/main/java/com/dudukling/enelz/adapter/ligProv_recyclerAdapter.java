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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.dudukling.enelz.R;
import com.dudukling.enelz.form_ligProvActivity;
import com.dudukling.enelz.model.lpModel;

import java.util.ArrayList;
import java.util.List;


public class ligProv_recyclerAdapter extends RecyclerView.Adapter implements Filterable {
    private Context context;
    private List<lpModel> lpList;
    private List<lpModel> samplesFiltered;

    public ligProv_recyclerAdapter(List<lpModel> lpList, Context context) {
        this.lpList = lpList;
        this.context = context;
        this.samplesFiltered = lpList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lig_prov, parent, false);

        return new aViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        aViewHolder holder = (aViewHolder) viewHolder;
        lpModel lp  = samplesFiltered.get(position);

        holder.viewOrdem.setText(lp.getOrdem());
        holder.viewCliente.setText(lp.getCliente());
        holder.viewEndereco.setText(lp.getEndereco());
    }

    class aViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        final TextView viewEndereco;
        final TextView viewCliente;
        final TextView viewOrdem;

        aViewHolder(View view) {
            super(view);
            viewOrdem = view.findViewById(R.id.textViewOrdem);
            viewCliente = view.findViewById(R.id.textViewCliente);
            viewEndereco = view.findViewById(R.id.textViewEndereco);

            view.setOnCreateContextMenuListener(this);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    lpModel lp  = samplesFiltered.get(position);

                    Intent goToFormActivity = new Intent(context, form_ligProvActivity.class);
                    goToFormActivity.putExtra("lp", lp);
                    context.startActivity(goToFormActivity);
                }
            });
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem menuEdit = menu.add("Editar");
//            menuEdit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem item) {
//                    int position = getAdapterPosition();
//                    Sample sample  = samplesFiltered.get(position);
//
//                    Intent goToFormActivity = new Intent(context, formActivity.class);
//                    goToFormActivity
//                            .putExtra("sample", sample)
//                            .putExtra("type", "edit");
//                    context.startActivity(goToFormActivity);
//
//                    return false;
//                }
//            });
//
//            MenuItem menuDelete = menu.add("Delete");
//            menuDelete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem item) {
//                    int position = getAdapterPosition();
//                    Sample sample  = samplesFiltered.get(position);
//
//                    sampleDAO dao = new sampleDAO(context);
//                    formHelper.deleteImagesFromPhoneMemory(sample);
//                    dao.delete(sample);
//                    samplesFiltered = dao.getSamples();
//                    dao.close();
//
//                    notifyDataSetChanged();
//
//                    return false;
//                }
//            });
        }

    }

    @Override
    public int getItemCount() {
        return samplesFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString();

                List<lpModel> filtered = new ArrayList<>();

                if (query.isEmpty()) {
                    filtered = lpList;
                } else {
                    for (lpModel lp : lpList) {
                        if (lp.getOrdem().toLowerCase().contains(query.toLowerCase()) ||
                                lp.getCliente().toLowerCase().contains(query.toLowerCase()) ||
                                lp.getEndereco().toLowerCase().contains(query.toLowerCase())
                        ){
                            filtered.add(lp);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.count = filtered.size();
                results.values = filtered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                samplesFiltered = (List<lpModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
