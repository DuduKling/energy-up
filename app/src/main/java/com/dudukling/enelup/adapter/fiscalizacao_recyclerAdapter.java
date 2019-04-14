package com.dudukling.enelup.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dudukling.enelup.R;
import com.dudukling.enelup.dao.fiscalizaDAO;
import com.dudukling.enelup.fiscalizacaoClandestinoFormActivity;
import com.dudukling.enelup.model.fiscaModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class fiscalizacao_recyclerAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<fiscaModel> fiscaList;

    private String[] listaFiltros = new String[4];

    public fiscalizacao_recyclerAdapter(Context context) {
        fiscalizaDAO dao = new fiscalizaDAO(context);
        this.fiscaList = dao.getFiscaList();
        dao.close();

        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_fiscaliza_clandestino, parent, false);

        return new aViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        aViewHolder holder = (aViewHolder) viewHolder;
        fiscaModel fisca  = fiscaList.get(position);

        holder.textViewFiscalizaClandestinoNome.setText(fisca.getNome());
        holder.textViewFiscalizaClandestinoEndereco.setText(fisca.getEndereco());
    }

    class aViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        final TextView textViewFiscalizaClandestinoNome;
        final TextView textViewFiscalizaClandestinoEndereco;

        aViewHolder(View view) {
            super(view);
            textViewFiscalizaClandestinoNome = view.findViewById(R.id.textViewFiscalizaClandestinoNome);
            textViewFiscalizaClandestinoEndereco = view.findViewById(R.id.textViewFiscalizaClandestinoEndereco);

            view.setOnCreateContextMenuListener(this);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    fiscaModel fisca  = fiscaList.get(position);

                    Intent goToFormActivity = new Intent(context, fiscalizacaoClandestinoFormActivity.class);
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
                    fiscaModel fisca  = fiscaList.get(position);

                    Intent goToFormActivity = new Intent(context, fiscalizacaoClandestinoFormActivity.class);
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
                    fiscaModel fisca  = fiscaList.get(position);

                    fiscalizaDAO dao = new fiscalizaDAO(context);
                    dao.delete(fisca);
                    dao.close();

                    if (!fiscaList.isEmpty()) {
                        for (int i = 0; i < fiscaList.size(); i++) {
                            fiscaModel fisca1 = fiscaList.get(i);
                            List<String> fiscaImages = fisca1.getImagesList();
                            if (!fiscaImages.isEmpty()) {
                                deleteImagesFromPhoneMemory(fisca1);
                            }
                        }
                    }

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

    public void deleteImagesFromPhoneMemory(fiscaModel fisca) {
        List<String> imagesListToDelete = fisca.getImagesList();
        for (int i = 0; i < imagesListToDelete.size(); i++) {
            File file = new File(imagesListToDelete.get(i));
            boolean deleted = file.delete();
            Log.d("TAG4", "delete() called: " + deleted);
        }

        String path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        File directory = new File(path);
        File[] files = directory.listFiles();
        for (File file : files) {
            boolean deleted = file.delete();
            Log.d("TAG4", "delete() called: " + deleted);
        }


    }

}