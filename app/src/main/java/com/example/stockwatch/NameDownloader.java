package com.example.stockwatch;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class NameDownloader extends AsyncTask<String, Void, String> {

    private static final String TAG = "NameDownloader";

    @SuppressLint("StaticFieldLeak")
    private MainActivity mainActivity;
    private static final String DATA_URL = "https://api.iextrading.com/1.0/ref-data/symbols";

    public static HashMap<String, String> stockHash = new HashMap<>();
    private static final ArrayList<String> matchedList = new ArrayList<>();

    NameDownloader(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(String... strings) {

        Uri dataUri = Uri.parse(DATA_URL);
        String urlToUse =  dataUri.toString();
        Log.d(TAG, "doInBackground: urlToUse >>>> " + urlToUse);

        StringBuilder sb = new StringBuilder();

        try {
            URL url = new URL(urlToUse);
            HttpURLConnection connec = (HttpURLConnection)url.openConnection();

            connec.setRequestMethod("GET");

            InputStream is = connec.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line;
            while((line = br.readLine()) != null){
                sb.append(line).append('\n');
            }

            Log.d(TAG, "doInBackground: READ >>> " + sb.toString());
            parseJSON(sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return sb.toString();

    }


    @Override
    protected void onPostExecute(String s) {

//        Toast.makeText(mainActivity,"Do nothing, just hold!", Toast.LENGTH_LONG).show();
//        if(nameList != null)
//            mainActivity.updatedData(nameList);

    }

    private HashMap<String, String> parseJSON(String str) {

        try {
            JSONArray dataResults = new JSONArray(str);

            for (int i = 0; i < dataResults.length(); i++) {
                JSONObject apiResult = (JSONObject) dataResults.get(i);

                String symbol = apiResult.getString("symbol");
                String name = apiResult.getString("name");
                stockHash.put(symbol, name);
            }
            Log.d(TAG, "parseJSON: stockhash" + stockHash);
            return stockHash;

        } catch(Exception e){
            Log.d(TAG, "parseJSON: ERROR" + e.getMessage());
            e.printStackTrace();
        }
        return null;

    }

    public static ArrayList<String> matchInput(String st){

        for(Map.Entry mapElement : stockHash.entrySet()){
            String symbol = (String)mapElement.getKey();
            String companyName = (String)mapElement.getValue();

            if(symbol.contains(st) || companyName.contains(st)){
                matchedList.add(symbol + " - " + companyName);
            }
        }

        Log.d(TAG, "matchInput: matchedList >>>>> "+matchedList);

        return matchedList;
    }
}
