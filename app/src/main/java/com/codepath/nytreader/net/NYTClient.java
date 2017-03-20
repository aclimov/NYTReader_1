package com.codepath.nytreader.net;

import android.util.Log;
import android.view.View;

import com.codepath.nytreader.models.Article;
import com.codepath.nytreader.models.Settings;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpException;
import cz.msebera.android.httpclient.client.HttpResponseException;
import cz.msebera.android.httpclient.util.TextUtils;


/**
 * Created by alex_ on 3/17/2017.
 */

public class NYTClient {
    static final String API_URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    static final String API_KEY = "f19c9d35b7f04ba48c5dc71a61236312";
    private AsyncHttpClient client;

    public NYTClient() {
        this.client = new AsyncHttpClient();
    }

    public void getArticles(final String query, int page, Settings settings, JsonHttpResponseHandler handler) {

        try {
            RequestParams params = new RequestParams();
            params.put("api-key", API_KEY);
            params.put("page", page);
            params.put("q", query);
            if (settings != null) {
                if (!TextUtils.isEmpty(settings.getSortOrder())) {
                    params.put("sort", settings.getSortOrder().toLowerCase());
                }
                if (!TextUtils.isEmpty(settings.getStartDateStr())) {
                    params.put("begin_date", settings.getStartDateStr());
                }

                if (!TextUtils.isEmpty(settings.getDeskValuesStr())) {
                    params.put("fq", String.format("news_desk:(%s)", settings.getDeskValuesStr()));
                }
            } else {
                params.put("sort", "newest");
            }

            client.get(API_URL, params, handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
