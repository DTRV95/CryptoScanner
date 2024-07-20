package org.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AllCryptoScanner {

    public static List<PairAnalysis> findPairsToBuy(List<String> symbols, String interval, int smaPeriod, int rsiPeriod,
                                                    int shortEMAPeriod, int longEMAPeriod, int signalPeriod) {
        List<PairAnalysis> pairsToBuy = new ArrayList<>();

        List<CompletableFuture<Void>> futures = symbols.stream().map(symbol -> CompletableFuture.runAsync(() -> {
            try {
                List<Double> prices = BinanceAPI.fetchHistoricalPrices(symbol, interval, 100);
                List<Double> volumes = BinanceAPI.fetchHistoricalVolumes(symbol, interval, 100);

                if (CryptoPredictor.shouldBuy(prices, volumes, smaPeriod, rsiPeriod, shortEMAPeriod, longEMAPeriod)) {
                    String formattedPair = symbol;
                    double currentPrice = prices.get(prices.size() - 1);
                    double sma = TechnicalIndicators.calculateSMA(prices, smaPeriod);
                    double rsi = TechnicalIndicators.calculateRSI(prices, rsiPeriod);
                    double volume = volumes.get(volumes.size() - 1);

                    // Apply filters
                    if (formattedPair.endsWith("USDT") || formattedPair.endsWith("USDC") || formattedPair.endsWith("EUR")) {
                        PairAnalysis analysis = new PairAnalysis(formattedPair, currentPrice, sma, rsi, volume);
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

        // Sort the pairsToBuy list by RSI in descending order
        pairsToBuy.sort((a, b) -> Double.compare(b.getRsi(), a.getRsi()));

        return pairsToBuy;
    }

    public static class PairAnalysis {
        public String symbol;
        public double currentPrice;
        public double sma;
        public double rsi;
        public double volume;

        public PairAnalysis(String symbol, double currentPrice, double sma, double rsi, double volume) {
            this.symbol = symbol;
            this.currentPrice = currentPrice;
            this.sma = sma;
            this.rsi = rsi;
            this.volume = volume;
        }

        public double getRsi() {
            return rsi;
        }

        @Override
        public String toString() {
            return String.format("Symbol: %s, Price: %.6f, SMA: %.6f, RSI: %.2f, Volume: %.2f",
                    symbol, currentPrice, sma, rsi, volume);
        }
    }
}