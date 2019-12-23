package com.example.stockwatch;

import java.io.Serializable;


public class Stock implements Comparable<Stock> {

    private String symbol;
    private String companyName;
    private double lastestPrice;
    private double change;
    private double changePrecent;

    Stock(String sb, String com, double lp, double ch, double chP){
        this.symbol = sb;
        this.companyName = com;
        this.lastestPrice = lp;
        this.change = ch;
        this.changePrecent = chP;
    }

    public Stock() {

    }

//    Stock(String sb, String com){
//        this.symbol = sb;
//        this.companyName = com;
//    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public double getLastestPrice() {
        return lastestPrice;
    }

    public void setLastestPrice(double lastestPrice) {
        this.lastestPrice = lastestPrice;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public double getChangePrecent() {
        return changePrecent;
    }

    public void setChangePrecent(double changePrecent) {
        this.changePrecent = changePrecent;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "symbol='" + symbol + '\'' +
                ", companyName='" + companyName + '\'' +
                ", lastestPrice=" + lastestPrice +
                ", change=" + change +
                ", changePrecent=" + changePrecent +
                '}';
    }

    @Override
    public int compareTo(Stock st) {
        return this.getSymbol().compareTo(st.getSymbol());
    }
}
