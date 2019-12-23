package com.example.stockwatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;


public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "MainActivityTag";

    private ArrayList<Stock> stocksList = new ArrayList<>();

    private RecyclerView recyclerView;
    private StockNameAdapter stAdapter;

    private SwipeRefreshLayout swiper;
    private ConnectivityManager connecManger;

    private ArrayList<String> saveSymbols = new ArrayList<>();
    private StockDownloader stockDwld;
    private String symbolToGo;
    private String name;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);
        stAdapter = new StockNameAdapter(stocksList, this);

        recyclerView.setAdapter(stAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new NameDownloader(this).execute();

//        loadStock();
        boolean checkFirst = doNetCheck();
        ArrayList<Stock> tempo = loadStock();

        if(!checkFirst){
            for( Stock st : tempo){
                Log.d(TAG, "onCreate: TEMPO >>>> " + tempo);
                st.setLastestPrice(0.0);
                st.setChange(0.0);
                st.setChangePrecent(0.0);
                stocksList.add(st);
            }
            Collections.sort(stocksList);
            stAdapter.notifyDataSetChanged();
        }
        else {
            for (Stock st : tempo){
                stockInfo(String.format("%s - %s", st.getSymbol(), st.getCompanyName()));
            }
        }

        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!doNetCheck()) {
                    goNoNetworkDialog();
                    stAdapter.notifyDataSetChanged();
                    swiper.setRefreshing(false);
                } else {
                    doRefresh();
                    //loadStock();
                }
            }
        });



//        TEST >>>
//        new NameDownloader(this).execute();
//        new StockDownloader(this).execute("TSLA");
//
        //fake stock list
//        for(int i = 0; i < 20; i++){
//            Stock a = new Stock("AAPL "+i, 135.72, 0.38,0.28, "Apple Inc.");
//            comNameList.add(a);
//        }

//        doRead();

    }

    public void stockInfo(String sym){
        Log.d(TAG, "stockInfo: >>>>>>>> " );
        String[] symArray = sym.split("-");
        Log.d(TAG, "stockInfo: symArray >>>>> " + symArray);
        symbolToGo = symArray[0].trim();
        stockDwld = (StockDownloader)new StockDownloader(this).execute(symbolToGo, name);

    }

    @SuppressLint("ServiceCast")
    private boolean doNetCheck(){

        connecManger = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connecManger == null) {
//                Toast.makeText(this, "Cannot access ", Toast.LENGTH_LONG).show();
                return false;
            }

        NetworkInfo netInfo = connecManger.getActiveNetworkInfo();
        if(netInfo != null && netInfo.isConnected()){
            Log.d(TAG, "doNetCheck: YOU ARE CONNECTED TO THE INTERNET!");
            return true;
        } else {
            Log.d(TAG, "doNetCheck: NOT CONNECTED TO THE INTERNET!");
            return false;
        }
    }

    public void doRefresh(){

        stocksList.clear();
        ArrayList<Stock> temp = loadStock();
        for( Stock st : temp ){
            stockInfo(String.format("%s - %s", st.getSymbol(), st.getCompanyName()));
        }
        stAdapter.notifyDataSetChanged();
        swiper.setRefreshing(false);
        Toast.makeText(this, "List content updated!! ", Toast.LENGTH_LONG).show();
    }
    

    @Override
    public void onClick(View v) {

        int pos = recyclerView.getChildLayoutPosition(v);
        Stock st = stocksList.get(pos);

        String symbol = st.getSymbol();
        String url = "http://www.marketwatch.com/investing/stock/" + symbol;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);

//        Toast.makeText(this, "Show Market Watch site", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onLongClick(View v) {

        int pos = recyclerView.getChildLayoutPosition(v);
        final Stock st = stocksList.get(pos);
        final String symbol = st.getSymbol();
//        Log.d(TAG, "onLongClick: I WANT TO DELETE THIS SYMBOL >>>> "+ symbol);

        gotoDeletDialog(v, symbol);

//        Toast.makeText(this, "Long Click works!", Toast.LENGTH_SHORT).show();
        return true;
    }
    public void gotoDeletDialog(View v, final String symbol){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.baseline_delete_outline_black_48);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                for(int i = 0; i < stocksList.size(); i++){
                    if(symbol.equals(stocksList.get(i).getSymbol())){
                        stocksList.remove(stocksList.get(i));
                    }
                }
                stAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do nothing
                Log.d(TAG, "onClick: CANCEL SELECTED!!!" );
            }
        });

        builder.setMessage("Delete Stock Symbol " + symbol + "?");
        builder.setTitle("Delete Stock");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void addFinData(Stock newStock) {

        stocksList.add(newStock);
        Collections.sort(stocksList);
        doWrite();
        stAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onPause() {

        super.onPause();

            doWrite();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.addmenu:
                gotoDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void gotoDialog(){

        if(!doNetCheck()){
            goNoNetworkDialog();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            final EditText editText = new EditText(this);
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.setGravity(Gravity.CENTER_HORIZONTAL);
            builder.setView(editText);
//        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
            editText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    String input = editText.getText().toString().trim();

                    Log.d(TAG, "onClick: saveSymbols " + saveSymbols);

                    //call static function from NameDownloader
                    final ArrayList<String> tempSelectionList = NameDownloader.matchInput(input);
                    Log.d(TAG, "onClick: tempSelectionList SIZE >>>> " + tempSelectionList.size());

                    if (tempSelectionList.size() == 0) {
                        gotoNotFoundDialog(input);

                    } else if (tempSelectionList.size() == 1 && stocksList.size() != 0) {

                        for (int i = 0; i < stocksList.size(); i++) {
                            if (stocksList.get(i).getSymbol().equals(tempSelectionList.get(0)))
                                goDupleDialog(input);
                            else gotoAddStockDialog(tempSelectionList);
                        }
                    } else {
                        gotoAddStockDialog(tempSelectionList);
                        saveSymbols.add(input);
                    }
                    tempSelectionList.clear();
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainActivity.this, "CANCEL choose!", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setMessage("Please enter a Stock Symbol");
            builder.setTitle("Stock Selection");

            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

    public void gotoAddStockDialog(ArrayList<String> selList){

        Log.d(TAG, "gotoAddStockDialog: selList >> " + selList.size());

        final CharSequence[] sArray = new CharSequence[selList.size()];
        for(int i = 0; i < selList.size(); i++){
            sArray[i] = selList.get(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Make a selection");

        builder.setItems(sArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String key = sArray[which].toString();
                Log.d(TAG, "onClick: KEY >>>>> "+ key);  //AMZN - AMAZON.COM INC
                String[] keys = key.split(" - ");
                Log.d(TAG, "onClick: format key >>>>> "+ keys[0]); //AMZN
                String finalKey = keys[0];

                Log.d(TAG, "onClick: stocksList SIZE : " +stocksList.size());
//                Log.d(TAG, "onClick: stocksList >>>>>> " + stocksList);

                if(stocksList.size() == 0){
                    new StockDownloader(MainActivity.this).execute(finalKey);
                }
                else {
                    if(isDuple(finalKey))
                        goDupleDialog(finalKey);
                    else
                        new StockDownloader(MainActivity.this).execute(finalKey);
                }

            }
        });
        builder.setNegativeButton("NEVERMIND", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //nevermind
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean isDuple(String key){

        for (int i = 0; i < stocksList.size(); i++) {
            if (stocksList.get(i).getSymbol().equals(key))
                return true;
        }
        return false;
    }

    private void goDupleDialog(String sym){

        Log.d(TAG, "goDupleDialog: dupleDialog !!!!! " );

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.sharp_warning_black_48);
        builder.setMessage("Stock Symbol "+ sym + " is already displayed");
        builder.setTitle("Duplicate Stock");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void gotoNotFoundDialog(String input){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Data for stock symbol");
        builder.setTitle("Symbol Not Found: " + input);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void goNoNetworkDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Stocks Cannot Be Added Without A Network Connection");
        builder.setTitle("No Network Connection");
        AlertDialog dialog = builder.create();
        dialog.show();
        return;
    }

    private void doWrite() {

        JSONArray jsonArray = new JSONArray();

        for (Stock s : stocksList) {
            try {
                JSONObject nameJSON = new JSONObject();
                nameJSON.put("symbolText", s.getSymbol());
                nameJSON.put("latestPText", s.getLastestPrice());
                nameJSON.put("chText", s.getChange());
                nameJSON.put("chPText", s.getChangePrecent());
                nameJSON.put("comName", s.getCompanyName());

                jsonArray.put(nameJSON);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        String jsonText = jsonArray.toString();
        Log.d(TAG, "doWrite: " + jsonText);

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter((
                    openFileOutput("myStock.json", Context.MODE_PRIVATE)));

            outputStreamWriter.write(jsonText);
            outputStreamWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<Stock> loadStock() {

        stocksList.clear();
        ArrayList<Stock> temp = new ArrayList<>();

        try {
            InputStream is = openFileInput("myStock.json");

            if(is != null) {
                InputStreamReader inreader = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(inreader);

                String receiveString = "";
                StringBuilder sb = new StringBuilder();

                while ((receiveString = br.readLine()) != null) {
                    sb.append(receiveString);
                }

                String jsonText = sb.toString();

                try {
                    JSONArray jsonArray = new JSONArray(jsonText);
                    Log.d(TAG, "loadStock: >>>> " + jsonArray.length());

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String symbol = jsonObject.getString("symbolText");
                        String latestPrice = jsonObject.getString("latestPText");
                        String change = jsonObject.getString("chText");
                        String changePercent = jsonObject.getString("chPText");
                        String comName = jsonObject.getString("comName");

                        double lp = Double.parseDouble(latestPrice);
                        double ch = Double.parseDouble(change);
                        double chp = Double.parseDouble(changePercent);

                        Stock st = new Stock(symbol, comName, lp, ch, chp);
//                        stocksList.add(st);
                        temp.add(st);
                        Collections.sort(temp);
                        stAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return temp;

    }

}
