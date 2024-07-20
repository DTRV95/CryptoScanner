package org.example;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose an option:");
        System.out.println("1. Check a specific crypto pair");
        System.out.println("2. Scan all crypto pairs on Binance");
        int option = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (option == 1) {
            System.out.println("Tell the CryptoSymbol (ex:BTCUSDT): ");
            String symbol = scanner.nextLine().toUpperCase();

            System.out.println("Tell the TimeFrame (ex: 1d, 1h): ");
            String interval = scanner.nextLine();

            System.out.println("Number of periods for the SMA: ");
            int smaPeriod = scanner.nextInt();
            int rsiPeriod = 14; // Updated RSI period
            int shortEMAPeriod = 9; // Updated MACD short EMA period
            int longEMAPeriod = 21; // Updated MACD long EMA period
            int signalPeriod = 9; // MACD signal period

            System.out.println("Searching for data on Binance..");
            try {
                List<Double> prices = BinanceAPI.fetchHistoricalPrices(symbol, interval, 100);
                List<Double> volumes = BinanceAPI.fetchHistoricalVolumes(symbol, interval, 100);

                double sma = TechnicalIndicators.calculateSMA(prices, smaPeriod);
                boolean shouldBuy = CryptoPredictor.shouldBuy(prices, volumes, smaPeriod, rsiPeriod, shortEMAPeriod, longEMAPeriod);

                System.out.printf("SMA: %.6f%n", sma);
                System.out.printf("Current Price: %.6f%n", prices.get(prices.size() - 1));
                System.out.println("Should buy? " + (shouldBuy ? "Yes" : "No"));
            } catch (IOException e) {
                System.out.println("Error trying to find any data on Binance: " + e.getMessage());
            }
        } else if (option == 2) {
            String interval = "1h"; // Interval for fetching historical data

            int smaPeriod = 20; // Period for Simple Moving Average (SMA)
            int rsiPeriod = 14; // Period for Relative Strength Index (RSI)
            int shortEMAPeriod = 9; // Period for short Exponential Moving Average (EMA)
            int longEMAPeriod = 21; // Period for long Exponential Moving Average (EMA)
            int signalPeriod = 9; // Period for MACD Signal Line

            try {
                List<String> allSymbols = BinanceAPI.fetchAllSymbols();

                System.out.println("Scanning all crypto pairs on Binance...");
                long startTime = System.currentTimeMillis();

                List<AllCryptoScanner.PairAnalysis> pairsToBuy = AllCryptoScanner.findPairsToBuy(allSymbols, interval, smaPeriod, rsiPeriod, shortEMAPeriod, longEMAPeriod, signalPeriod);

                long endTime = System.currentTimeMillis();
                System.out.println("Scanning completed in " + (endTime - startTime) + " ms");

                if (pairsToBuy.isEmpty()) {
                    System.out.println("No pairs found that meet the buy criteria.");
                } else {
                    System.out.println("Pairs to consider buying:");
                    for (AllCryptoScanner.PairAnalysis analysis : pairsToBuy) {
                        System.out.println(analysis);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error fetching data from Binance: " + e.getMessage());
            }
        } else {
            System.out.println("Invalid option. Exiting.");
        }
    }
}
