package com.example.stockwatch;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class StockNameAdapter extends RecyclerView.Adapter<NameViewHolder> {

    private static final String TAG = "StockNameAdapter";

//    private HashMap<String, String> nameHash;

    private List<Stock> comNameList;
    private MainActivity mainAct;

    public StockNameAdapter(List<Stock> comNameList, MainActivity mainAct) {
        this.comNameList = comNameList;
        this.mainAct = mainAct;
    }

    @NonNull
    @Override
    public NameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stockname_list, parent, false);

        view.setOnClickListener(mainAct);
        view.setOnLongClickListener(mainAct);

        return new NameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NameViewHolder holder, int position) {

        Stock stock = comNameList.get(position);
        holder.symbol.setText(stock.getSymbol());
        holder.companyName.setText(stock.getCompanyName());
        holder.latestPrice.setText(String.format(Locale.getDefault(),"%.2f", stock.getLastestPrice()));
        holder.change.setText(String.format(Locale.getDefault(), "%.2f", stock.getChange()));
        holder.changePercent.setText(String.format(Locale.getDefault(), "(%.2f%%)", stock.getChangePrecent()));

        if(stock.getChange() >= 0){
            holder.symbol.setTextColor(Color.GREEN);
            holder.companyName.setTextColor(Color.GREEN);
            holder.latestPrice.setTextColor(Color.GREEN);

            holder.change.setTextColor(Color.GREEN);
            holder.change.setText(String.format(Locale.getDefault(), "▲ %.2f", stock.getChange()));
            holder.changePercent.setTextColor(Color.GREEN);
        } else {
            holder.symbol.setTextColor(Color.RED);
            holder.companyName.setTextColor(Color.RED);
            holder.latestPrice.setTextColor(Color.RED);
            holder.change.setTextColor(Color.RED);
            holder.change.setText(String.format(Locale.getDefault(), "▼ %.2f", stock.getChange()));
            holder.changePercent.setTextColor(Color.RED);

        }

    }

    @Override
    public int getItemCount() {
        return comNameList.size();
    }
}
