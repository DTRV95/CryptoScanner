package org.example;

public class PairAnalysis {

    public String symbol;
    public double currentPrice;
    public double sma;
    public double rsi;
    public double volume;
    public double buyPrice;
    public double sellPrice;

    public PairAnalysis(String symbol, double currentPrice, double sma, double rsi, double volume, double buyPrice, double sellPrice) {
        this.symbol = symbol;
        this.currentPrice = currentPrice;
        this.sma = sma;
        this.rsi = rsi;
        this.volume = volume;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    public PairAnalysis(String symbol, double buyPrice, double sellPrice) {
        this.symbol = symbol;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    public double getRsi() {
        return rsi;
    }

    @Override
    public String toString() {
        return String.format("Symbol: %s, Buy at: %.6f, Sell at: %.6f",
                symbol, buyPrice, sellPrice);
    }
}