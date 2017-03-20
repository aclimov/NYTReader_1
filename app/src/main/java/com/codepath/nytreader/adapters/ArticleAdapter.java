package com.codepath.nytreader.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.nytreader.views.DynamicHeightImageView;
import com.codepath.nytreader.R;
import com.codepath.nytreader.models.Article;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;


public class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int IMG = 0, NOIMG = 1;
    // Store a member variable for the contacts
    private List<Article> mArticles;
    // Store the context for easy access
    private Context mContext;

    // Pass in the contact array into the constructor
    public ArticleAdapter(Context context, List<Article> articles) {
        mArticles = articles;
        mContext = context;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case IMG:
                View v1 = inflater.inflate(R.layout.item_article, viewGroup, false);
                viewHolder = new ViewHolder_norm(v1);
                break;
            case NOIMG:
                View v2 = inflater.inflate(R.layout.item_article_noimg, viewGroup, false);
                viewHolder = new ViewHolder_noimg(v2);
                break;
        }

        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if (!TextUtils.isEmpty(mArticles.get(position).getThumbNail())) {
            return IMG;
        } else {
            return NOIMG;
        }
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        switch (viewHolder.getItemViewType()) {
            case IMG:
                ViewHolder_norm vh1 = (ViewHolder_norm) viewHolder;
                configureViewHolder1(vh1, position);
                break;
            case NOIMG:
                ViewHolder_noimg vh2 = (ViewHolder_noimg) viewHolder;
                configureViewHolder2(vh2, position);
                break;
        }
    }

    private void configureViewHolder1(ViewHolder_norm vh1, int position) {
        Article article = mArticles.get(position);
        if (article != null) {
            vh1.getTvTitle().setText(article.getHeadline());
            vh1.getTvDesk().setText(article.getNewsDesk());
            if(vh1.getTvPubDate()!=null){
            vh1.getTvPubDate().setText(article.getPubDate());}
            ImageView ivView = vh1.getIvThumbnail();

            Glide.with(mContext).load(article.getThumbNail())
                    .placeholder(R.drawable.thumbnailimg)
                   .into(ivView);
        }
    }

    private void configureViewHolder2(ViewHolder_noimg vh2, int position) {
        Article article = mArticles.get(position);
        if (article != null) {
            vh2.getTvHeadline().setText(article.getHeadline());
            vh2.getTvDesk().setText(article.getNewsDesk());
            vh2.getTvPubDate().setText(article.getPubDate());
        }
    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    public void clear() {
        int size = this.mArticles.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.mArticles.remove(0);
            }
            this.notifyItemRangeRemoved(0, size);
        }
    }

    public void add(Article article) {
        mArticles.add(article);
    }

    public Article getItem(int id) {
        return mArticles.get(id);
    }

    public static class ViewHolder_norm extends RecyclerView.ViewHolder implements Target {

        public TextView getTvTitle() {
            return tvTitle;
        }

        public DynamicHeightImageView getIvThumbnail() {
            return ivThumbnail;
        }

        private TextView tvTitle;

        private DynamicHeightImageView ivThumbnail;

        public TextView getTvDesk() {
            return tvDesk;
        }

        private TextView tvPubDate;

        public TextView getTvPubDate() {
            return tvPubDate;
        }

        private TextView tvDesk;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder_norm(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvDesk = (TextView) itemView.findViewById(R.id.tvDesk);
            tvPubDate = (TextView) itemView.findViewById(R.id.tvPubDate);

            ivThumbnail = (DynamicHeightImageView) itemView.findViewById(R.id.ivImage);
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            // Calculate the image ratio of the loaded bitmap
            float ratio = (float) bitmap.getHeight() / (float) bitmap.getWidth();
            // Set the ratio for the image
            ivThumbnail.setHeightRatio(ratio);
            // Load the image into the view
            ivThumbnail.setImageBitmap(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    }

    public class ViewHolder_noimg extends RecyclerView.ViewHolder {

        private TextView tvHeadline;

        public TextView getTvHeadline() {
            return tvHeadline;
        }

        public TextView getTvDesk() {
            return tvDesk;
        }

        private TextView tvDesk;

        private TextView tvPubDate;

        public TextView getTvPubDate() {
            return tvPubDate;
        }

        public ViewHolder_noimg(View v) {
            super(v);
            tvHeadline = (TextView) v.findViewById(R.id.tvHeadline);
            tvDesk = (TextView) v.findViewById(R.id.tvDesk);
            tvPubDate = (TextView) v.findViewById(R.id.tvPubDate);

        }
    }
}
