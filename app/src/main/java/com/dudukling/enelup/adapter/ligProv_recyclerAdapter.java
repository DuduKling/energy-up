package com.dudukling.enelup.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.dudukling.enelup.R;
import com.dudukling.enelup.dao.lpDAO;
import com.dudukling.enelup.ligProvFormActivity;
import com.dudukling.enelup.model.lpModel;

import java.util.ArrayList;
import java.util.List;


public class ligProv_recyclerAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<lpModel> lpList;
    private List<lpModel> lpListFiltered = new ArrayList<>();

    private String[] listaFiltros = new String[4];

    public ligProv_recyclerAdapter(Context context) {
        lpDAO dao = new lpDAO(context);
        this.lpList = dao.getLPList();
        dao.close();

        this.context = context;
//        this.lpListFiltered = lpList;
        makeFilter();
    }

    public String[] getListaFiltros() {
        return listaFiltros;
    }
    public void setListaFiltros(String[] listaFiltros) {
        this.listaFiltros = listaFiltros;
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
        lpModel lp  = lpListFiltered.get(position);

        holder.viewOrdem.setText(lp.getOrdem());
        holder.viewCliente.setText(lp.getCliente());
        holder.viewEndereco.setText(lp.getEndereco());
        if(!lp.getUserObservacao().equals("") && !lp.getImagesList().isEmpty()){
            holder.imageViewStatus.setBackgroundColor(Color.parseColor("#8BC34A"));
        }else if(!lp.getUserObservacao().equals("") || !lp.getImagesList().isEmpty()) {
                holder.imageViewStatus.setBackgroundColor(Color.parseColor("#FFC107"));
        }else {
            holder.imageViewStatus.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    class aViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        final TextView viewEndereco;
        final TextView viewCliente;
        final TextView viewOrdem;
        final ImageView imageViewStatus;

        aViewHolder(View view) {
            super(view);
            viewOrdem = view.findViewById(R.id.textViewOrdem);
            viewCliente = view.findViewById(R.id.textViewCliente);
            viewEndereco = view.findViewById(R.id.textViewEndereco);
            imageViewStatus = view.findViewById(R.id.imageViewStatusOrdem);

            view.setOnCreateContextMenuListener(this);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    lpModel lp  = lpListFiltered.get(position);

                    Intent goToFormActivity = new Intent(context, ligProvFormActivity.class);
                    goToFormActivity
                            .putExtra("lp", lp)
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
                    lpModel lp  = lpListFiltered.get(position);

                    Intent goToFormActivity = new Intent(context, ligProvFormActivity.class);
                    goToFormActivity
                            .putExtra("lp", lp)
                            .putExtra("type", "edit");
                    context.startActivity(goToFormActivity);

                    return false;
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return lpListFiltered.size();
    }

//    public Filter getFilter() {
//        return new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence charSequence) {
//                String query = charSequence.toString();
//
//                List<lpModel> filtered = new ArrayList<>();
//
//                if (query.isEmpty()) {
//                    filtered = lpList;
//                } else {
//                    for (lpModel lp : lpList) {
//                        if (lp.getOrdem().toLowerCase().contains(query.toLowerCase()) ||
//                                lp.getCliente().toLowerCase().contains(query.toLowerCase()) ||
//                                lp.getEndereco().toLowerCase().contains(query.toLowerCase())
//                        ){
//                            filtered.add(lp);
//                        }
//                    }
//                }
//
//                FilterResults results = new FilterResults();
//                results.count = filtered.size();
//                results.values = filtered;
//                return results;
//            }
//
//            @Override
//            protected void publishResults(CharSequence charSequence, FilterResults results) {
//
//                lpListFiltered = (List<lpModel>) results.values;
//                notifyDataSetChanged();
//            }
//        };
//    }

    public void updateStaticFilter(Spinner spinner, String tipo, int position) {
        switch (tipo){
            case "localidade":
                if(position==0){
                    listaFiltros[1] = null;
                }else{
                    listaFiltros[1] = spinner.getSelectedItem().toString().substring(spinner.getSelectedItem().toString().indexOf(" - ") + 3).toLowerCase();
                }
                break;
            case "tipo":
                if(position==0){
                    listaFiltros[2] = null;
                }else{
                    listaFiltros[2] = spinner.getSelectedItem().toString().substring(spinner.getSelectedItem().toString().indexOf("RIA ") + 4).toLowerCase();
                }
                break;
            case "etapa":
                if(position==0){
                    listaFiltros[3] = null;
                }else{
                    listaFiltros[3] = spinner.getSelectedItem().toString().substring(0,3).toLowerCase();
                }

                break;
        }

        makeFilter();
    }

    public void updateSearchFilter(String query) {
        if(query == null || query.equals("")){
            listaFiltros[0] = null;
        }else{
            listaFiltros[0] = query.toLowerCase();
        }
        makeFilter();
    }

    private void makeFilter() {
        lpListFiltered = new ArrayList<>();
        List<lpModel> filtered = new ArrayList<>();

        if(listaFiltros[1]==null && listaFiltros[2]==null && listaFiltros[3]==null && listaFiltros[0]==null) {
            filtered = lpList;
        }
        else if(listaFiltros[1]!=null && listaFiltros[2]==null && listaFiltros[3]==null && listaFiltros[0]==null){
            for(lpModel lp : lpList) {
                if(
                    lp.getLocalidade().toLowerCase().equals(listaFiltros[1])
                ){
                    filtered.add(lp);
                }
            }
        }
        else if(listaFiltros[1]==null && listaFiltros[2]!=null && listaFiltros[3]==null && listaFiltros[0]==null){
            for (lpModel lp : lpList) {
                if(
                    lp.getTipoOrdem().toLowerCase().contains(listaFiltros[2])
                ){
                    filtered.add(lp);
                }
            }
        }
        else if(listaFiltros[1]==null && listaFiltros[2]==null && listaFiltros[3]!=null && listaFiltros[0]==null){
            for (lpModel lp : lpList) {
                if(
                   lp.getEtapa().toLowerCase().equals(listaFiltros[3])
                ){
                    filtered.add(lp);
                }
            }
        }
        else if(listaFiltros[1]==null && listaFiltros[2]==null && listaFiltros[3]==null && listaFiltros[0]!=null){
            for (lpModel lp : lpList) {
                if(
                    lp.getOrdem().toLowerCase().contains(listaFiltros[0]) ||
                    lp.getCliente().toLowerCase().contains(listaFiltros[0]) ||
                    lp.getEndereco().toLowerCase().contains(listaFiltros[0])
                ){
                    filtered.add(lp);
                }
            }
        }
        else if(listaFiltros[1]!=null && listaFiltros[2]!=null && listaFiltros[3]==null && listaFiltros[0]==null){
            for (lpModel lp : lpList) {
                if(
                    lp.getLocalidade().toLowerCase().equals(listaFiltros[1]) &&
                    lp.getTipoOrdem().toLowerCase().contains(listaFiltros[2])
                ){
                    filtered.add(lp);
                }
            }
        }
        else if(listaFiltros[1]!=null && listaFiltros[2]==null && listaFiltros[3]!=null && listaFiltros[0]==null){
            for (lpModel lp : lpList) {
                if(
                    lp.getLocalidade().toLowerCase().equals(listaFiltros[1]) &&
                    lp.getEtapa().toLowerCase().equals(listaFiltros[3])
                ){
                    filtered.add(lp);
                }
            }
        }
        else if(listaFiltros[1]!=null && listaFiltros[2]==null && listaFiltros[3]==null && listaFiltros[0]!=null){
            for (lpModel lp : lpList) {
                if(
                    lp.getLocalidade().toLowerCase().equals(listaFiltros[1]) &&
                    (
                        lp.getOrdem().toLowerCase().contains(listaFiltros[0]) ||
                        lp.getCliente().toLowerCase().contains(listaFiltros[0]) ||
                        lp.getEndereco().toLowerCase().contains(listaFiltros[0])
                    )
                ){
                    filtered.add(lp);
                }
            }
        }
        else if(listaFiltros[1]==null && listaFiltros[2]!=null && listaFiltros[3]!=null && listaFiltros[0]==null){
            for (lpModel lp : lpList) {
                if(
                    lp.getTipoOrdem().toLowerCase().contains(listaFiltros[2]) &&
                    lp.getEtapa().toLowerCase().equals(listaFiltros[3])
                ){
                    filtered.add(lp);
                }
            }
        }
        else if(listaFiltros[1]==null && listaFiltros[2]!=null && listaFiltros[3]==null && listaFiltros[0]!=null){
            for (lpModel lp : lpList) {
                if(
                    lp.getTipoOrdem().toLowerCase().contains(listaFiltros[2]) &&
                    (
                        lp.getOrdem().toLowerCase().contains(listaFiltros[0]) ||
                        lp.getCliente().toLowerCase().contains(listaFiltros[0]) ||
                        lp.getEndereco().toLowerCase().contains(listaFiltros[0])
                    )
                ){
                    filtered.add(lp);
                }
            }
        }
        else if(listaFiltros[1]==null && listaFiltros[2]==null && listaFiltros[3]!=null && listaFiltros[0]!=null){
            for (lpModel lp : lpList) {
                if(
                    lp.getEtapa().toLowerCase().equals(listaFiltros[3]) &&
                    (
                        lp.getOrdem().toLowerCase().contains(listaFiltros[0]) ||
                        lp.getCliente().toLowerCase().contains(listaFiltros[0]) ||
                        lp.getEndereco().toLowerCase().contains(listaFiltros[0])
                    )
                ){
                    filtered.add(lp);
                }
            }
        }
        else if(listaFiltros[1]!=null && listaFiltros[2]!=null && listaFiltros[3]!=null && listaFiltros[0]==null){
            for (lpModel lp : lpList) {
                if(
                    lp.getLocalidade().toLowerCase().equals(listaFiltros[1]) &&
                    lp.getTipoOrdem().toLowerCase().contains(listaFiltros[2]) &&
                    lp.getEtapa().toLowerCase().equals(listaFiltros[3])
                ){
                    filtered.add(lp);
                }
            }
        }
        else if(listaFiltros[1]!=null && listaFiltros[2]!=null && listaFiltros[3]==null && listaFiltros[0]!=null){
            for (lpModel lp : lpList) {
                if(
                    lp.getLocalidade().toLowerCase().equals(listaFiltros[1]) &&
                    lp.getTipoOrdem().toLowerCase().contains(listaFiltros[2]) &&
                    (
                        lp.getOrdem().toLowerCase().contains(listaFiltros[0]) ||
                        lp.getCliente().toLowerCase().contains(listaFiltros[0]) ||
                        lp.getEndereco().toLowerCase().contains(listaFiltros[0])
                    )
                ){
                    filtered.add(lp);
                }
            }
        }
        else if(listaFiltros[1]!=null && listaFiltros[2]==null && listaFiltros[3]!=null && listaFiltros[0]!=null){
            for (lpModel lp : lpList) {
                if(
                    lp.getLocalidade().toLowerCase().equals(listaFiltros[1]) &&
                    lp.getEtapa().toLowerCase().equals(listaFiltros[3]) &&
                    (
                        lp.getOrdem().toLowerCase().contains(listaFiltros[0]) ||
                        lp.getCliente().toLowerCase().contains(listaFiltros[0]) ||
                        lp.getEndereco().toLowerCase().contains(listaFiltros[0])
                    )
                ){
                    filtered.add(lp);
                }
            }
        }
        else if(listaFiltros[1]==null && listaFiltros[2]!=null && listaFiltros[3]!=null && listaFiltros[0]!=null){
            for (lpModel lp : lpList) {
                if(
                    lp.getTipoOrdem().toLowerCase().contains(listaFiltros[2]) &&
                    lp.getEtapa().toLowerCase().equals(listaFiltros[3]) &&
                    (
                        lp.getOrdem().toLowerCase().contains(listaFiltros[0]) ||
                        lp.getCliente().toLowerCase().contains(listaFiltros[0]) ||
                        lp.getEndereco().toLowerCase().contains(listaFiltros[0])
                    )
                ){
                    filtered.add(lp);
                }
            }
        }
        else if(listaFiltros[1]!=null && listaFiltros[2]!=null && listaFiltros[3]!=null && listaFiltros[0]!=null){
            for (lpModel lp : lpList) {
                if(
                    lp.getLocalidade().toLowerCase().equals(listaFiltros[1]) &&
                    lp.getTipoOrdem().toLowerCase().contains(listaFiltros[2]) &&
                    lp.getEtapa().toLowerCase().equals(listaFiltros[3]) &&
                    (
                        lp.getOrdem().toLowerCase().contains(listaFiltros[0]) ||
                        lp.getCliente().toLowerCase().contains(listaFiltros[0]) ||
                        lp.getEndereco().toLowerCase().contains(listaFiltros[0])
                    )
                ){
                    filtered.add(lp);
                }
            }
        }

        if(!filtered.isEmpty()){
            lpListFiltered = filtered;
            notifyDataSetChanged();
        }

    }

    public void refreshList() {
        lpDAO dao = new lpDAO(context);
        lpList = dao.getLPList();
        dao.close();
        notifyDataSetChanged();
        makeFilter();
    }
}
