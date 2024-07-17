package org.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AllCryptoScanner {

    public static List<PairAnalysis> findPairsToBuy(List<String> symbols, String interval, int smaPeriod, int rsiPeriod, int shortEMAPeriod, int longEMAPeriod, int signalPeriod) {

        List<PairAnalysis> pairsToBuy = new ArrayList<>();

        List<CompletableFuture<Void>> futures = symbols.stream().map(symbol -> CompletableFuture.runAsync(() -> {
            try {
                List<Double> prices = BinanceAPI.fetchHistoricalPrices(symbol, interval, 100);


                if (CryptoPredictor.shouldBuy(prices, smaPeriod, rsiPeriod, shortEMAPeriod, longEMAPeriod)) {
                    String formattedPair = formatSymbol(symbol);
                    double currentPrice = prices.get(prices.size() - 1);
                    double sma = TechnicalIndicators.calculateSMA(prices, smaPeriod);
                    double rsi = TechnicalIndicators.calculateRSI(prices, rsiPeriod);

                    if(formattedPair.endsWith("USDT") || formattedPair.endsWith("USDC") || formattedPair.endsWith("EUR")){
                            PairAnalysis analysis = new PairAnalysis(formattedPair, currentPrice, sma, rsi);
                            synchronized (pairsToBuy) {
                                pairsToBuy.add(analysis);
                            }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error fetching data for " + symbol + ": " + e.getMessage());
            }
        })).collect(Collectors.toList());

        futures.forEach(CompletableFuture::join);

        pairsToBuy.sort((a, b) -> Double.compare(b.getRsi(), a.getRsi()));

        return pairsToBuy;
    }

    private static String formatSymbol(String symbol) {
        return symbol.replace("BUSD", "BUS/D")
                .replace("USDT", "US/T");

    }

    public static class PairAnalysis {
        public String symbol;
        public double currentPrice;
        public double sma;
        public double rsi;

        public PairAnalysis(String symbol, double currentPrice, double sma, double rsi) {
            this.symbol = symbol;
            this.currentPrice = currentPrice;
            this.sma = sma;
            this.rsi = rsi;
        }

        public double getRsi() {
            return rsi;
        }

        @Override
        public String toString() {
            return String.format("Symbol: %s, Price: %.6f, SMA: %.6f, RSI: %.2f",
                    symbol, currentPrice, sma, rsi);
        }
    }
}
