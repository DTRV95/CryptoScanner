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
                    double buyPrice = TechnicalIndicators.findSupport(prices);
                    double sellPrice = TechnicalIndicators.findResistance(prices);

                    // Apply filters
                    if (formattedPair.endsWith("USDT") || formattedPair.endsWith("USDC") || formattedPair.endsWith("EUR")) {
                        PairAnalysis analysis = new PairAnalysis(formattedPair, currentPrice, sma, rsi, volume, buyPrice, sellPrice);
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
}