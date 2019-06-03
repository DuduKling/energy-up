package com.dudukling.enelup.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dudukling.enelup.R;
import com.dudukling.enelup.dao.fiscaClandDAO;
import com.dudukling.enelup.fiscalizacao_clandestino.fiscaClandAlbumActivity;
import com.dudukling.enelup.imageActivity;
import com.dudukling.enelup.model.fiscaClandModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class fiscaClandAlbum_recyclerAdapter extends RecyclerView.Adapter {
    private final fiscaClandModel fisca;
    private static List<String> imagesList;
    private Context context;

    public fiscaClandAlbum_recyclerAdapter(fiscaClandModel fisca, fiscaClandAlbumActivity context) {
        this.context = context;
        fiscaClandDAO dao = new fiscaClandDAO(context);
        imagesList = dao.getImagesDB(fisca.getId());
        dao.close();
        this.fisca = fisca;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_album_lig_prov, parent, false);

        return new albumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        albumViewHolder holder = (albumViewHolder) viewHolder;

        Bitmap bitmap = BitmapFactory.decodeFile(imagesList.get(position));

        Bitmap smallerBitmap = resize(bitmap, 200, 200);

        holder.imageViewAlbum.setImageBitmap(smallerBitmap);
        holder.imageViewAlbum.setScaleType(ImageView.ScaleType.FIT_CENTER);
    }

    @Override
    public int getItemCount() {
            return imagesList.size();
    }

    public class albumViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private final ImageView imageViewAlbum;

        albumViewHolder(View view) {
            super(view);
            imageViewAlbum = view.findViewById(R.id.imageViewLPAlbum);

            view.setOnCreateContextMenuListener(this);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Intent goToImageActivity = new Intent(context, imageActivity.class);
                    goToImageActivity.putExtra("image_url", imagesList.get(position))
                            .putExtra("position", Integer.toString(position+1));
                    context.startActivity(goToImageActivity);
                }
            });

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem menuDelete = menu.add("Deletar imagem");
            menuDelete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int position = getAdapterPosition();
                    int fiscaID  = fisca.getId();

                    List<String> imageToDelete = new ArrayList<>();
                    imageToDelete.add(imagesList.get(position));

                    fiscaClandDAO dao = new fiscaClandDAO(context);
                    int imageID = dao.getImageIdDB(imagesList.get(position));
                    dao.deleteImage(fiscaID, imageID);

                    imagesList = dao.getImagesDB(fiscaID);
                    dao.close();

                    notifyDataSetChanged();

                    fiscaClandModel fiscaToDelete = new fiscaClandModel();
                    fiscaToDelete.setImagesList(imageToDelete);
                    deleteImagesFromPhoneMemory(fiscaToDelete);

                    return false;
                }
            });
        }

    }

    private static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float)maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float)maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

    private static void deleteImagesFromPhoneMemory(fiscaClandModel fisca) {
        List<String> imagesListToDelete = fisca.getImagesList();
        for(int i = 0; i < imagesListToDelete.size(); i++){
            File file = new File(imagesListToDelete.get(i));
            boolean deleted = file.delete();
            Log.d("TAG4", "delete() called: "+deleted);
        }
    }
}
