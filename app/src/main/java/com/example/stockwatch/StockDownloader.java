package com.example.stockwatch;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class StockDownloader extends AsyncTask<String, Integer, String> {


    private static final String TAG = "StockDownloader";

    @SuppressLint("StaticFieldLeak")
    private MainActivity mainActivity;
    private static final String apiBase = "https://cloud.iexapis.com/stable/stock/";
    private static final String apiEndToken = "/quote?token=sk_6471354b390540418ce5fc7585c29b47";
    private static Stock newStock = new Stock();

    StockDownloader(MainActivity mainActivity) { this.mainActivity = mainActivity; }


    @Override
    protected String doInBackground(String... dataIn) {

        String apiEndPoint = dataIn[0];
        Uri.Builder buildURL = Uri.parse(apiBase).buildUpon();
        String end = apiEndPoint + apiEndToken;
        buildURL.appendEncodedPath(end);
        String urlToUse = buildURL.build().toString();

        StringBuilder sb = new StringBuilder();

        try {
            URL url = new URL(urlToUse);
            HttpURLConnection connec = (HttpURLConnection) url.openConnection();
            Log.d(TAG, "doInBackground: urlToUSE " + urlToUse);

            connec.setRequestMethod("GET");

            InputStream is = connec.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "doInBackground: >>>>> " + sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    private Stock parseJSON(String s) {

        Stock newStock = null;

        try{
            JSONObject jsonObject = new JSONObject(s);
            String symbol = jsonObject.getString("symbol");
            String companyName = jsonObject.getString("companyName");

            String lp = jsonObject.getString("latestPrice");
            Double latestPrice = 0.0;
            if(lp != null && !lp.trim().equals("null"))
                latestPrice = Double.parseDouble(lp);

            String ch = jsonObject.getString("change");
            Double change = 0.0;
            if(ch != null && !ch.trim().equals("null"))
                change = Double.parseDouble(ch);

            String chP = jsonObject.getString("changePercent");
            Double changePercent = 0.0;
            if(chP != null && !chP.trim().equals("null"))
                changePercent = Double.parseDouble(chP);
            
            newStock = new Stock(symbol, companyName, latestPrice, change, changePercent);

        } catch (Exception e){
            Log.d(TAG, "parseJSON: ERROR >>>>>> "+e.getMessage());
            e.printStackTrace();
        }

        return newStock;
    }

    @Override
    protected void onPostExecute(String s) {
        newStock = parseJSON(s);
        if(newStock != null){
            mainActivity.addFinData(newStock);
        }

    }

}