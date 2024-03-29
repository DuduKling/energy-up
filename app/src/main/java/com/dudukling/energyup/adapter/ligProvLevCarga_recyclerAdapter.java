package com.dudukling.energyup.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dudukling.energyup.ligacaoes_provisorias.ligProvLevCargaActivity;
import com.dudukling.energyup.R;
import com.dudukling.energyup.dao.ligProvDAO;
import com.dudukling.energyup.model.ligProvPotenciaModel;

import java.util.List;

public class ligProvLevCarga_recyclerAdapter extends RecyclerView.Adapter {
    private final int lpID;
    private final String typeofForm;
    private Context context;
    private List<ligProvPotenciaModel> ligProvPotenciaModel;

    public ligProvLevCarga_recyclerAdapter(List<ligProvPotenciaModel> ligProvPotenciaModel, ligProvLevCargaActivity context, int lpID, String typeofForm) {
        this.ligProvPotenciaModel = ligProvPotenciaModel;
        this.context = context;
        this.lpID = lpID;
        this.typeofForm = typeofForm;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.lig_prov_item_lev_carga, parent, false);

        return new aViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        aViewHolder holder = (aViewHolder) viewHolder;
        ligProvPotenciaModel pot  = ligProvPotenciaModel.get(position);

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
                    ligProvPotenciaModel oPotencia = ligProvPotenciaModel.get(position);
                    int oPotenciaID = oPotencia.getId();

                    ligProvDAO dao = new ligProvDAO(context);
                    dao.deletePotencia(oPotenciaID);
                    ligProvPotenciaModel = dao.getLPPotenciaList(lpID);
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
        return ligProvPotenciaModel.size();
    }

}

