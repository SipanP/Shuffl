package com.shufflteam.shuffl;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private final List<com.shufflteam.shuffl.PlaylistCard> playlists;

    RecyclerAdapter(List<PlaylistCard> playlists) {
        this.playlists = playlists;
    }

    @NonNull
    @Override
    public com.shufflteam.shuffl.RecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_card_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(com.shufflteam.shuffl.RecyclerAdapter.MyViewHolder holder, final int position) {
        final com.shufflteam.shuffl.PlaylistCard playlist = playlists.get(position);
        holder.title.setText(playlist.getTitle());
        holder.image.setBackground(playlist.getImage());
        holder.cardView.setOnClickListener(v -> {
            String roomId = MainActivity.joinRoom();
            Intent intent = new Intent(v.getContext(), RoomActivity.class);
            intent.putExtra("roomId", roomId);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final ImageView image;
        private final CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleTextView);
            image = itemView.findViewById(R.id.iconImageView);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
