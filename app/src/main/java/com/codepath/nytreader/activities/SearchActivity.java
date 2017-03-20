package com.codepath.nytreader.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepath.nytreader.helpers.EndlessRecyclerViewScrollListener;
import com.codepath.nytreader.helpers.ItemClickSupport;
import com.codepath.nytreader.adapters.ArticleAdapter;
import com.codepath.nytreader.R;
import com.codepath.nytreader.dialogs.SettingsFragment;
import com.codepath.nytreader.models.Article;
import com.codepath.nytreader.models.Settings;
import com.codepath.nytreader.net.NYTClient;
import com.codepath.nytreader.net.NetCommon;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.TextUtils;

public class SearchActivity extends AppCompatActivity implements SettingsFragment.EditSettingsDialogListener {

    private final int REQUEST_CODE = 1;

    RecyclerView rvArticles;
    ArrayList<Article> articles;
    ArticleAdapter adapter;
    Settings settings;
    String queryText;

    MenuItem miActionProgressItem;
    EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //hideProgressBar();
        rvArticles = (RecyclerView) findViewById(R.id.rvArticles);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        articles = new ArrayList<>();
        adapter = new ArticleAdapter(this, articles);
        rvArticles.setAdapter(adapter);
        StaggeredGridLayoutManager linearLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvArticles.setLayoutManager(linearLayoutManager);

        ItemClickSupport.addTo(rvArticles).setOnItemClickListener(
                (recyclerView, position, v) -> {

                    Article article = adapter.getItem(position);
                    if (isChromeInstalled()) {
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_name);
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, article.getWebUrl());

                        int requestCode = 100;
                        PendingIntent pendingIntent = PendingIntent.getActivity(SearchActivity.this,
                                requestCode,
                                intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);

                        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                        builder.setActionButton(bitmap, "Share Link", pendingIntent, true);
                        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
                        CustomTabsIntent customTabsIntent = builder.build();
                        customTabsIntent.launchUrl(SearchActivity.this, Uri.parse(article.getWebUrl()));
                    } else {
                        Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                        i.putExtra("article", Parcels.wrap(article));
                        startActivity(i);
                    }
                }
        );
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(page);
            }
        };
        rvArticles.addOnScrollListener(scrollListener);

        fetchArticles(0,false);
    }

    private void fetchArticles(int page, final Boolean append) {

        showProgressBar();
        if (!NetCommon.isNetworkAvailable(SearchActivity.this)) {
            Toast.makeText(SearchActivity.this, "Network is not available", Toast.LENGTH_LONG).show();
        }

        if (!NetCommon.isOnline()) {
            Toast.makeText(SearchActivity.this, "Internet is not available", Toast.LENGTH_LONG).show();
        }


            NYTClient client = new NYTClient();
            client.getArticles(queryText, page, settings, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        JSONArray docs;
                        if (response != null) {
                            // Get the docs json array
                            docs = response.getJSONObject("response").getJSONArray("docs");
                            // Parse json array into array of model objects
                            articles = Article.fromJSONArray(docs);
                            // Remove all books from the adapter
                            if (!append) {

                                adapter.clear();
                            }
                            // Load model objects into the adapter
                            for (Article article : articles) {
                                adapter.add(article); // add book through the adapter
                            }
                            adapter.notifyDataSetChanged();

                            if (articles.size() == 0&&page==0) {
                                Toast.makeText(SearchActivity.this, "Couldn't find any articles. Please use other words.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            throw new JSONException("response is null");
                        }
                        hideProgressBar();
                    } catch (JSONException e) {

                        e.printStackTrace();
                        hideProgressBar();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                }
            });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                // pbList.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(query)) {
                    queryText = query;
                    clearRecyclerView();
                    fetchArticles(0, false);
                }
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599

                searchView.clearFocus();
                searchItem.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void clearRecyclerView() {

        articles.clear();
        adapter.clear();
        adapter.notifyDataSetChanged(); // or notifyItemRangeRemoved
        scrollListener.resetState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            showSettingsDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSettingsDialog() {
        FragmentManager fm = getSupportFragmentManager();
        SettingsFragment settingsFragment = SettingsFragment.newInstance(settings);
        settingsFragment.show(fm, "fragment_settings");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract name value from result extras
            settings = (Settings) Parcels.unwrap(data.getExtras().getParcelable("settings"));
            fetchArticles(0, false);
        }
    }

    public void loadNextDataFromApi(int page) {
        fetchArticles(page, true);
    }

    @Override
    public void onFinishEditDialog(Settings settingsObj) {
        settings = settingsObj;
        Toast.makeText(this, "Settings were applied", Toast.LENGTH_SHORT).show();
        clearRecyclerView();
        fetchArticles(0,false);
    }

    private boolean isChromeInstalled() {
        try {
            PackageManager pm = this.getPackageManager();
            pm.getPackageInfo("com.android.chrome", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        // Extract the action-view from the menu item
        ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }

    public void showProgressBar() {
        // Show progress item
        if(miActionProgressItem!=null) {
            miActionProgressItem.setVisible(true);
        }
    }

    public void hideProgressBar() {
        // Hide progress item
        if(miActionProgressItem!=null) {
            miActionProgressItem.setVisible(false);
        }
    }
}
