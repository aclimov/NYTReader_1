package com.codepath.nytreader.models;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import android.support.v7.app.AppCompatActivity;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.security.AccessController.getContext;

/**
 * Created by alex_ on 3/15/2017.
 */

@org.parceler.Parcel
public class Settings {

    public Settings()
    {
        deskValues = new ArrayList();
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setDeskValues(ArrayList<String> deskValues) {
        this.deskValues = deskValues;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public String getStartDateStr() {

        if(this.startDate==null){return "";}

        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
        String dateStr = ft.format(this.startDate);
        return dateStr;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public String getStartDateDisplay() {
        if(this.startDate!=null){
        SimpleDateFormat ft = new SimpleDateFormat("MM-dd-yyyy");
        String dateStr = ft.format(this.startDate);
        return dateStr;
        }
        else
        {return "";}
    }

    public String getDeskValuesStr() {
        StringBuilder result = new StringBuilder();
        String prefix = "%20";
        Set<String> uniqueVals = new HashSet<String>(deskValues);
        for (String myVal : uniqueVals) {
            result.append(prefix);

            result.append(myVal);
        }
        if(result.length()>0)
        {result.append(prefix);}
        return result.toString();
    }

    ArrayList<String> deskValues;
    String sortOrder;

    Date startDate;


    public ArrayList<String> getDeskValues()
    {
            return deskValues;
    }


  /*  private static Settings ReadSettings(File filePath)
    {
        ArrayList<String> fileRows = new ArrayList<>();
        Settings result = new Settings();

        File settingsFile=new File(filePath,"settings.txt");
        try {
            fileRows = new ArrayList<String>(FileUtils.readLines(settingsFile));
        }catch(IOException e) {
            e.printStackTrace();
            fileRows = new ArrayList<String>();
        }
        if(fileRows.size()==3)
        {
            result.sortOrder = fileRows.get(0);
            result.startDate = fileRows.get(1);
            result.deskValues = new ArrayList<String>(Arrays.asList( fileRows.get(2).split(",")));
        }
        return result;
    }

    private  void  writeSettings(File filePath)
    {
        File settingsFile=new File(filePath,"settings.txt");
        ArrayList<String> sb = new ArrayList<>();
        try {


            sb.add(sortOrder);
            sb.add(startDate);

            if(deskValues!=null) {
                sb.add(getDeskValuesStr());
            }

            FileUtils.writeLines(settingsFile,sb);
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    private  static void CleanSettings(File filePath)
    {
        File settingsFile=new File(filePath,"settings.txt");
        ArrayList<String> sb = new ArrayList<>();
        try {

            FileUtils.writeLines(settingsFile,sb);
        }catch(IOException e) {
            e.printStackTrace();
        }
    }*/

}
