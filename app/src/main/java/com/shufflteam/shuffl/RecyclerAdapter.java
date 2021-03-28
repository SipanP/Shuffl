package com.shufflteam.shuffl;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>{

    private List<PlaylistCard> playlistCards;

    RecyclerAdapter(List<PlaylistCard> playlistCards){
        this.playlistCards = playlistCards;
    }

    @Override
    public com.shufflteam.shuffl.RecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_card_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(com.shufflteam.shuffl.RecyclerAdapter.MyViewHolder holder, final int position) {
        final PlaylistCard playlistCard = playlistCards.get(position);
        holder.title.setText(playlistCard.getTitle());
        holder.image.setBackgroundResource(playlistCard.getImage());
    }

    @Override
    public int getItemCount() {
        return playlistCards.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView image;
        private CardView cardView;
        public MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
