package com.adryde.mobile.displaysdk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Classe per gestire il recycler view dell'activity LoadingMedia.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<String> foto;
    private ArrayList<String> ids;
    private ArrayList<String> filseSizes=new ArrayList<>();
    private MediaClickListner myListner;

    public RecyclerViewAdapter(Context context,MediaClickListner lstnr) {
        this.context = context;
        this.myListner=lstnr;
        foto= new ArrayList<>();
        ids= new ArrayList<>();
    }

    public void addFoto(ArrayList<String> f, ArrayList<String> id, ArrayList<String> fSizes)
    {
        if(foto!=null) {
            foto.addAll(f);
            ids.addAll(id);
            filseSizes.addAll(fSizes);
            notifyDataSetChanged();
        }
        else
        {
            foto=f;
            ids=id;
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view,parent,false);
        MyViewHolder myview = new MyViewHolder(view,myListner);
        return myview;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
      //  holder.image.setImageBitmap(BitmapUtils.decodeSampledBitmapFromResource(foto.get(position),100, 100));
        holder.name.setText(ids.get(position)+"\n"+filseSizes.get(position));
    }


    @Override
    public int getItemCount() {
        return foto.size();
    }

    /**
     * Classe che contine il singolo elemento della recycler view
     */
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView name;
        ImageView image;
        MediaClickListner myListner;
        public MyViewHolder(@NonNull View itemView, MediaClickListner lstn) {
            super(itemView);
            this.name = (TextView) itemView.findViewById(R.id.name);
            this.image = (ImageView) itemView.findViewById(R.id.image_view);
            this.myListner = lstn;
            this.image.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myListner.onMediaClick(foto.get(getAdapterPosition()),ids.get(getAdapterPosition()));
        }
    }

}
