package com.example.quont.queueband;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by quont on 6/19/2016.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.SongViewHolder>{

    List<Song> songs;
    Context context;
    RVAdapter(Context context, List<Song> songs){
        this.context = context;
        this.songs = songs;
    }

    @Override
    public RVAdapter.SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_layout, parent, false);
        SongViewHolder songViewHolder = new SongViewHolder(v);
        context = parent.getContext();
        return songViewHolder;
    }

    @Override
    public void onBindViewHolder(RVAdapter.SongViewHolder holder, int position) {
        holder.title.setText(songs.get(position).getTitle());
        holder.author.setText(songs.get(position).getAuthor());
        holder.source.setText(songs.get(position).getSource());
        holder.votes.setText(String.valueOf(songs.get(position).getVotes()));
        Picasso.with(context).load(songs.get(position).getArt()).resize(150, 150).centerCrop().into(holder.art);
        holder.upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.downvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }
    public static class SongViewHolder  extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView title;
        TextView author;
        TextView source;
        TextView votes;
        ImageView art;
        ImageButton upvote;
        ImageButton downvote;
        public SongViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            title = (TextView) itemView.findViewById(R.id.title);
            author = (TextView) itemView.findViewById(R.id.author);
            source = (TextView) itemView.findViewById(R.id.source);
            votes = (TextView) itemView.findViewById(R.id.votes);
            art = (ImageView) itemView.findViewById(R.id.imageView);
            upvote = (ImageButton) itemView.findViewById(R.id.upvote);
            downvote = (ImageButton) itemView.findViewById(R.id.downvote);

        }
    }
}
