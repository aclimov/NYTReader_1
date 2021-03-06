package com.codepath.nytreader.activities;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.codepath.nytreader.R;
import com.codepath.nytreader.models.Article;

import org.parceler.Parcels;

public class ArticleActivity extends AppCompatActivity {
    Article article;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        article = (Article) Parcels.unwrap(getIntent().getParcelableExtra("article"));
        WebView wvArticle = (WebView) findViewById(R.id.wvArticle);
        wvArticle.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl( url);
                return true;
            }
        });
        wvArticle.loadUrl(article.getWebUrl()) ;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_article, menu);

        MenuItem item = menu.findItem(R.id.menu_item_share);
        ShareActionProvider miShare = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        // get reference to WebView
        WebView wvArticle = (WebView) findViewById(R.id.wvArticle);
        // pass in the URL currently being used by the WebView
        shareIntent.putExtra(Intent.EXTRA_TEXT, wvArticle.getUrl());

        miShare.setShareIntent(shareIntent);
        return super.onCreateOptionsMenu(menu);
    }
}
