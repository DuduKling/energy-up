package com.dudukling.enelup.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dudukling.enelup.R;
import com.dudukling.enelup.dao.fiscaClandDAO;
import com.dudukling.enelup.fiscalizacao_clandestino.fiscaClandFormActivity;
import com.dudukling.enelup.model.fiscaClandModel;

import java.io.File;
import java.util.List;


public class fiscaCland_recyclerAdapter extends RecyclerView.Adapter {
    private Context context;
    public static fiscaCland_recyclerAdapter context2;
    public static List<fiscaClandModel> fiscaList;

    public fiscaCland_recyclerAdapter(Context context) {
        fiscaClandDAO dao = new fiscaClandDAO(context);
        this.fiscaList = dao.getFiscaList();
        dao.close();

        this.context = context;
        this.context2 = this;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fisca_cland_item, parent, false);

        return new aViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        aViewHolder holder = (aViewHolder) viewHolder;
        fiscaClandModel fisca  = fiscaList.get(position);

        holder.textViewFiscalizaClandestinoNome.setText(fisca.getNome());
        holder.textViewFiscalizaClandestinoEndereco.setText(fisca.getEndereco());

        if(fisca.getFlag_google_sheets().equals("1")){
//             holder.imageViewFiscalizaClandestinoStatusUpload.setBackgroundColor(Color.parseColor("#8BC34A"));
            holder.imageViewFiscalizaClandestinoStatusUpload.setVisibility(View.VISIBLE);
        }else{
//            holder.imageViewFiscalizaClandestinoStatusUpload.setBackgroundColor(Color.TRANSPARENT);
            holder.imageViewFiscalizaClandestinoStatusUpload.setVisibility(View.GONE);
        }
    }

    class aViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        final TextView textViewFiscalizaClandestinoNome;
        final TextView textViewFiscalizaClandestinoEndereco;
        private ImageView imageViewFiscalizaClandestinoStatusUpload;

        aViewHolder(View view) {
            super(view);
            textViewFiscalizaClandestinoNome = view.findViewById(R.id.textViewFiscalizaClandestinoNome);
            textViewFiscalizaClandestinoEndereco = view.findViewById(R.id.textViewFiscalizaClandestinoEndereco);
            imageViewFiscalizaClandestinoStatusUpload = view.findViewById(R.id.imageViewFiscalizaClandestinoStatusUpload);

            view.setOnCreateContextMenuListener(this);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    fiscaClandModel fisca  = fiscaList.get(position);

                    Intent goToFormActivity = new Intent(context, fiscaClandFormActivity.class);
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
                    fiscaClandModel fisca  = fiscaList.get(position);

                    Intent goToFormActivity = new Intent(context, fiscaClandFormActivity.class);
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
                    fiscaClandModel fisca  = fiscaList.get(position);

                    fiscaClandDAO dao = new fiscaClandDAO(context);
                    dao.delete(fisca);
                    dao.close();

                    if (!fiscaList.isEmpty()) {
                        fiscaClandModel fisca1 = fiscaList.get(position);
                        List<String> fiscaImages = fisca1.getImagesList();
                        if (!fiscaImages.isEmpty()) {
                            deleteImagesFromPhoneMemory(fisca1);
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

    private void deleteImagesFromPhoneMemory(fiscaClandModel fisca) {
        List<String> imagesListToDelete = fisca.getImagesList();
        for (int i = 0; i < imagesListToDelete.size(); i++) {
            File file = new File(imagesListToDelete.get(i));
            boolean deleted = file.delete();
            Log.d("TAG4", "delete() called: " + deleted);
        }

//        String path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
//        File directory = new File(path);
//        File[] files = directory.listFiles();
//        for (File file : files) {
//            boolean deleted = file.delete();
//            Log.d("TAG4", "delete() called: " + deleted);
//        }
    }

}