package com.codepath.nytreader.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.ParcelConstructor;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by alex_ on 3/14/2017.
 */

@org.parceler.Parcel
public class Article  {

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadline() {
        return headline;
    }

    public String getThumbNail() {
        return thumbNail;
    }

    String headline;
    String thumbNail;
    String webUrl;
    String pubDate;
    String snippet;
    int twidth;

    public int getTwidth() {
        return twidth;
    }

    public int getTheight() {
        return theight;
    }

    int theight;
    public String getPubDate() {

        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date pubDate = (Date) formatter.parse(this.pubDate.substring(0,10));

            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MMM-dd");
            String dateStr = ft.format(pubDate);
            return dateStr;

        }catch(ParseException e)
        {
            return "";
        }

    }

    public String getNewsDesk() {
        return newsDesk;
    }

    public String getSnippet() {
        return snippet;
    }

    String newsDesk;


    public static Article fromJson(JSONObject jsonObject)
    {
        Article a = new Article();
        try {
            a.webUrl = jsonObject.getString("web_url");
            a.headline = jsonObject.getJSONObject("headline").getString("main");

            a.newsDesk = jsonObject.getString("section_name");
            a.pubDate = jsonObject.getString("pub_date");
            a.snippet=jsonObject.getString("snippet");
            JSONArray multimedia = jsonObject.getJSONArray("multimedia");
            if (multimedia.length() > 0) {
                JSONObject multimediaJson = multimedia.getJSONObject(0);
                String imgUrl = multimediaJson.getString("url");
                a.thumbNail = String.format("http://www.nytimes.com/%s", imgUrl);
                a.twidth = multimediaJson.getInt("width");
                a.theight = multimediaJson.getInt("height");
            } else {
                a.thumbNail = "";
            }
            return a;
        }catch(JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<Article> fromJSONArray(JSONArray array)
    {
        ArrayList<Article> result = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            JSONObject articleJson;
            try {
                articleJson = array.getJSONObject(i);
                Article article = Article.fromJson(articleJson);
                if (article != null) {
                    result.add(article);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return result;
    }
}
