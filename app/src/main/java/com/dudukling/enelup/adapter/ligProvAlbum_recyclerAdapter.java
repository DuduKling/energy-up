package com.dudukling.enelup.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dudukling.enelup.R;
import com.dudukling.enelup.dao.lpDAO;
import com.dudukling.enelup.ligacaoes_provisorias.ligProvImageActivity;
import com.dudukling.enelup.model.lpModel;
import com.dudukling.enelup.util.lpFormHelper;

import java.util.ArrayList;
import java.util.List;


public class ligProvAlbum_recyclerAdapter extends RecyclerView.Adapter {
    private final lpModel lp;
    private static List<String> imagesList;
    private Context context;

    public ligProvAlbum_recyclerAdapter(lpModel lp, Context context) {
        this.context = context;
        lpDAO dao = new lpDAO(context);
        imagesList = dao.getImagesDB(lp.getId());
        dao.close();
        this.lp = lp;
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
                    Intent goToImageActivity = new Intent(context, ligProvImageActivity.class);
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
                    int lpID  = lp.getId();

                    List<String> imageToDelete = new ArrayList<>();
                    imageToDelete.add(lp.getImagesList().get(position));

                    lpDAO dao = new lpDAO(context);
                    int imageID = dao.getImageIdDB(imagesList.get(position));
                    dao.deleteImage(lpID, imageID);

                    imagesList = dao.getImagesDB(lpID);
                    dao.close();

                    notifyDataSetChanged();

                    lpModel lpToDelete = new lpModel();
                    lpToDelete.setImagesList(imageToDelete);
                    lpFormHelper.deleteImagesFromPhoneMemory(lpToDelete);

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

}
