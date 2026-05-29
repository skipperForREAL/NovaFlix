package com.movies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.movies.R;
import com.movies.models.CreditsResponse;

import java.util.List;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {

    private Context context;
    private List<CreditsResponse.Cast> castList;

    public CastAdapter(Context context, List<CreditsResponse.Cast> castList) {
        this.context = context;
        this.castList = castList;
    }

    @NonNull
    @Override
    public CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cast, parent, false);
        return new CastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CastViewHolder holder, int position) {
        CreditsResponse.Cast cast = castList.get(position);

        holder.txtName.setText(cast.getName());
        holder.txtCharacter.setText(cast.getCharacter());

        String imageUrl = "https://image.tmdb.org/t/p/w185" + cast.getProfilePath();
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgCast);
    }

    @Override
    public int getItemCount() {
        return castList.size();
    }

    public static class CastViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCast;
        TextView txtName, txtCharacter;

        public CastViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCast = itemView.findViewById(R.id.imgCast);
            txtName = itemView.findViewById(R.id.txtCastName);
            txtCharacter = itemView.findViewById(R.id.txtCharacter);
        }
    }
}