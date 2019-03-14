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
import android.widget.ImageButton;
import android.widget.TextView;

import com.dudukling.enelz.LigProvLevCargaActivity;
import com.dudukling.enelz.R;
import com.dudukling.enelz.clandestinoFormActivity;
import com.dudukling.enelz.dao.lpDAO;
import com.dudukling.enelz.model.lpClandestino;
import com.dudukling.enelz.model.lpPotencia;

import java.util.List;

public class levCarga_recyclerAdapter extends RecyclerView.Adapter {
    private final int lpID;
    private final String typeofForm;
    private Context context;
    private List<lpPotencia> lpPotencia;

    public levCarga_recyclerAdapter(List<lpPotencia> lpPotencia, LigProvLevCargaActivity context, int lpID, String typeofForm) {
        this.lpPotencia = lpPotencia;
        this.context = context;
        this.lpID = lpID;
        this.typeofForm = typeofForm;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_calc_lev_carga, parent, false);

        return new aViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        aViewHolder holder = (aViewHolder) viewHolder;
        lpPotencia pot  = lpPotencia.get(position);

        holder.textViewLevCargaQuantidade.setText(pot.getQuantidade());
        holder.textViewLevCargaDescricao.setText(pot.getDescricao());
        holder.textViewLevCargaPotencia.setText(pot.getPotencia() + " W");

        if(typeofForm.equals("readOnly")){
            holder.imageButtonLevCargaDelete.setVisibility(View.GONE);
        }else{
            holder.imageButtonLevCargaDelete.setVisibility(View.VISIBLE);
        }
    }

    class aViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        final TextView textViewLevCargaQuantidade;
        final TextView textViewLevCargaDescricao;
        final TextView textViewLevCargaPotencia;
        final ImageButton imageButtonLevCargaDelete;

        aViewHolder(View view) {
            super(view);
            textViewLevCargaQuantidade = view.findViewById(R.id.textViewLevCargaQuantidade);
            textViewLevCargaDescricao = view.findViewById(R.id.textViewLevCargaDescricao);
            textViewLevCargaPotencia = view.findViewById(R.id.textViewLevCargaPotencia);
            imageButtonLevCargaDelete = view.findViewById(R.id.imageButtonLevCargaDelete);

            ImageButton imageButtonLevCargaDelete = view.findViewById(R.id.imageButtonLevCargaDelete);
            imageButtonLevCargaDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    lpPotencia oPotencia = lpPotencia.get(position);
                    int oPotenciaID = oPotencia.getId();

                    lpDAO dao = new lpDAO(context);
                    dao.deletePotencia(oPotenciaID);
                    lpPotencia = dao.getLPPotenciaList(lpID);
                    dao.close();

                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo) {

        }

    }

    @Override
    public int getItemCount() {
        return lpPotencia.size();
    }

}

