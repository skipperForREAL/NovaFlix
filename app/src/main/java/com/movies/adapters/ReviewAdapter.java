package com.movies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.movies.R;
import com.movies.models.ReviewResponse;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private final Context context;
    private final List<ReviewResponse.Review> reviewList;

    public ReviewAdapter(Context context, List<ReviewResponse.Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewResponse.Review review = reviewList.get(position);

        holder.txtAuthor.setText(review.getAuthor());
        holder.txtContent.setText(review.getContent());

        if (review.getAuthorDetails() != null && review.getAuthorDetails().getRating() != null) {
            holder.txtRating.setText("★ " + review.getAuthorDetails().getRating());
            holder.txtRating.setVisibility(View.VISIBLE);
        } else {
            holder.txtRating.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView txtAuthor, txtRating, txtContent;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAuthor = itemView.findViewById(R.id.txtReviewAuthor);
            txtRating = itemView.findViewById(R.id.txtReviewRating);
            txtContent = itemView.findViewById(R.id.txtReviewContent);
        }
    }
}