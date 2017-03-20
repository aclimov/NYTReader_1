package com.codepath.nytreader.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.nytreader.R;
import com.codepath.nytreader.models.Article;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

import static android.R.attr.resource;
import static android.R.attr.thumbnail;

/**
 * Created by alex_ on 3/14/2017.
 */

public class ArticleArrayAdapter extends ArrayAdapter<Article> {

    public ArticleArrayAdapter(Context context, List<Article> articles) {
        super(context, android.R.layout.simple_list_item_1,articles)    ;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Article article =  getItem(position);
        if(convertView==null)
        {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_article_result,parent,false);
        }
        ImageView ivImage = (ImageView)convertView.findViewById(R.id.ivImage);
        ivImage.setImageResource(0);

        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        tvTitle.setText(article.getHeadline());

        TextView tvPubDate = (TextView) convertView.findViewById(R.id.tvPubDate);
        tvTitle.setText(article.getPubDate());
       /* TextView tvNewsDesk = (TextView) convertView.findViewById(R.id.tvNewsDesk);
        tvTitle.setText(article.getNewsDesk());*/


        String thumbnail =article.getThumbNail();

        if(TextUtils.isEmpty(thumbnail))
        {thumbnail=null;}

        Picasso.with(getContext()).load(thumbnail).fit().centerCrop()
                .placeholder(R.drawable.thumbnailimg)
                //.error(R.drawable.thumbnailimg)
                .into(ivImage);


        return convertView;
    }
}
