package com.example.stockwatch;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NameViewHolder extends RecyclerView.ViewHolder {

    TextView symbol;
    TextView latestPrice;
    TextView change;
    TextView changePercent;
    TextView companyName;

    NameViewHolder(@NonNull View view) {
        super(view);
        symbol = view.findViewById(R.id.symbol);
        latestPrice = view.findViewById(R.id.latestPrice);
        change = view.findViewById(R.id.change);
        changePercent = view.findViewById(R.id.changePercent);
        companyName = view.findViewById(R.id.comName);

    }
}
