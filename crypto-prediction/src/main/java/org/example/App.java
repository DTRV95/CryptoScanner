package org.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("Choose an option:");
            System.out.println("1. Check a specific crypto pair");
            System.out.println("2. Scan all crypto pairs on Binance");
            System.out.println("3. Analyze your pairs");
            System.out.println("4. Check market trend");
            System.out.println("5. Exit");
            int option = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (option) {
                case 1:
                    checkSpecificPair(scanner);
                    break;
                case 2:
                    scanAllCryptoPairs();
                    break;
                case 3:
                    analyzeYourPairs(scanner);
                    break;
                case 4:
                    checkMarketTrend(scanner);
                    break;
                case 5:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void checkSpecificPair(Scanner scanner) {
        System.out.println("Tell the CryptoSymbol (ex:BTCUSDT): ");
        String symbol = scanner.nextLine().toUpperCase();

        System.out.println("Tell the TimeFrame (ex: 1d, 1h): ");
        String interval = scanner.nextLine();

        int smaPeriod = 20;
        int rsiPeriod = 14;
        int shortEMAPeriod = 12;
        int longEMAPeriod = 26;
        int signalPeriod = 9;

        System.out.println("Searching for data on Binance...");
        try {
            List<Double> prices = BinanceAPI.fetchHistoricalPrices(symbol, interval, 100);
            List<Double> volumes = BinanceAPI.fetchHistoricalVolumes(symbol, interval, 100);

            double sma = TechnicalIndicators.calculateSMA(prices, smaPeriod);
            double rsi = TechnicalIndicators.calculateRSI(prices, rsiPeriod);
            double currentPrice = prices.get(prices.size() - 1);
            double volume = volumes.get(volumes.size() - 1);
            double buyPrice = TechnicalIndicators.findSupport(prices);
            double sellPrice = TechnicalIndicators.findResistance(prices);
            boolean shouldBuy = CryptoPredictor.shouldBuy(prices, volumes, smaPeriod, rsiPeriod, shortEMAPeriod, longEMAPeriod);
            boolean shouldSell = CryptoPredictor.shouldSell(prices, rsiPeriod, shortEMAPeriod, longEMAPeriod);

            System.out.printf("SMA: %.6f%n", sma);
            System.out.printf("Current Price: %.6f%n", currentPrice);
            System.out.printf("RSI: %.2f%n", rsi);
            System.out.printf("Volume: %.2f%n", volume);
            System.out.printf("Buy at: %.6f%n", buyPrice);
            System.out.printf("Sell at: %.6f%n", sellPrice);
            System.out.println("Should buy? " + (shouldBuy ? "Yes" : "No"));
            System.out.println("Should sell? " + (shouldSell ? "Yes" : "No"));
        } catch (IOException e) {
            System.out.println("Error trying to find any data on Binance: " + e.getMessage());
        }
    }

    private static void scanAllCryptoPairs() {
        String interval = "1h";

        int smaPeriod = 20;
        int rsiPeriod = 14;
        int shortEMAPeriod = 12;
        int longEMAPeriod = 26;
        int signalPeriod = 9;

        try {
            List<String> allSymbols = BinanceAPI.fetchAllSymbols();

            System.out.println("Scanning all crypto pairs on Binance...");
            long startTime = System.currentTimeMillis();

            List<PairAnalysis> pairsToBuy = AllCryptoScanner.findPairsToBuy(allSymbols, interval, smaPeriod, rsiPeriod, shortEMAPeriod, longEMAPeriod, signalPeriod);

            long endTime = System.currentTimeMillis();
            System.out.println("Scanning completed in " + (endTime - startTime) + " ms");

            if (pairsToBuy.isEmpty()) {
                System.out.println("No pairs found that meet the buy criteria.");
            } else {
                System.out.println("Pairs to consider buying:");
                for (PairAnalysis analysis : pairsToBuy) {
                    System.out.println(analysis);
                }
            }
        } catch (IOException e) {
            System.out.println("Error fetching data from Binance: " + e.getMessage());
        }
    }

    private static void analyzeYourPairs(Scanner scanner) {
        System.out.println("Enter the number of pairs you want to analyze:");
        int numPairs = scanner.nextInt();
        scanner.nextLine();

        List<PairAnalysis> userPairs = new ArrayList<>();

        for (int i = 0; i < numPairs; i++) {
            System.out.println("Enter the CryptoSymbol (ex:BTCUSDT): ");
            String symbol = scanner.nextLine().toUpperCase();

            System.out.println("Enter the TimeFrame (ex: 1d, 1h): ");
            String interval = scanner.nextLine();

            System.out.println("Enter the price you bought at: ");
            double buyPrice = scanner.nextDouble();
            scanner.nextLine();

            try {
                List<Double> prices = BinanceAPI.fetchHistoricalPrices(symbol, interval, 100);

                double sellPrice = TechnicalIndicators.findResistance(prices);

                userPairs.add(new PairAnalysis(symbol, buyPrice, sellPrice));
            } catch (IOException e) {
                System.out.println("Error trying to find any data on Binance: " + e.getMessage());
            }
        }

        if (userPairs.isEmpty()) {
            System.out.println("No pairs found that meet the criteria.");
        } else {
            System.out.println("Pairs to consider selling:");
            for (PairAnalysis analysis : userPairs) {
                System.out.println(analysis);
            }
        }
    }

    private static void checkMarketTrend(Scanner scanner) {
        System.out.println("Choose an option:");
        System.out.println("4.1. Check a specific crypto pair");
        System.out.println("4.2. Check market trend for all pairs");
        String option = scanner.nextLine();

        switch (option) {
            case "4.1":
                checkSpecificPairMarketTrend(scanner);
                break;
            case "4.2":
                checkGeneralMarketTrend();
                break;
            default:
                System.out.println("Invalid option. Returning to main menu.");
        }
    }

    private static void checkSpecificPairMarketTrend(Scanner scanner) {
        System.out.println("Tell the CryptoSymbol (ex:BTCUSDT): ");
        String symbol = scanner.nextLine().toUpperCase();

        System.out.println("Tell the TimeFrame (ex: 1d, 1h): ");
        String interval = scanner.nextLine();

        int smaPeriod = 20;

        System.out.println("Searching for data on Binance...");
        try {
            List<Double> prices = BinanceAPI.fetchHistoricalPrices(symbol, interval, 100);

            double sma = TechnicalIndicators.calculateSMA(prices, smaPeriod);
            double currentPrice = prices.get(prices.size() - 1);
            boolean isTrendingUp = currentPrice > sma;

            System.out.printf("SMA: %.6f%n", sma);
            System.out.printf("Current Price: %.6f%n", currentPrice);
            System.out.println("Market trend: " + (isTrendingUp ? "Up" : "Down"));
            System.out.println("Should buy? " + (isTrendingUp ? "Yes" : "No"));
            System.out.println("Should sell? " + (!isTrendingUp ? "Yes" : "No"));
        } catch (IOException e) {
            System.out.println("Error trying to find any data on Binance: " + e.getMessage());
        }
    }

    private static void checkGeneralMarketTrend() {
        String interval = "1d";
        int smaPeriod = 20;

        try {
            List<String> allSymbols = BinanceAPI.fetchAllSymbols();
            int upCount = 0;
            int downCount = 0;

            System.out.println("Calculating market trend for all crypto pairs on Binance...");
            long startTime = System.currentTimeMillis();

            for (String symbol : allSymbols) {
                try {
                    List<Double> prices = BinanceAPI.fetchHistoricalPrices(symbol, interval, 100);
                    double sma = TechnicalIndicators.calculateSMA(prices, smaPeriod);
                    double currentPrice = prices.get(prices.size() - 1);
                    if (currentPrice > sma) {
                        upCount++;
                    } else {
                        downCount++;
                    }
                } catch (IOException e) {
                    System.out.println("Error fetching data for " + symbol + ": " + e.getMessage());
                }
            }

            long endTime = System.currentTimeMillis();
            System.out.println("Market trend calculation completed in " + (endTime - startTime) + " ms");
            System.out.println("Number of pairs trending up: " + upCount);
            System.out.println("Number of pairs trending down: " + downCount);
            System.out.println("Overall market trend: " + (upCount > downCount ? "Up" : "Down"));
        } catch (IOException e) {
            System.out.println("Error fetching data from Binance: " + e.getMessage());
        }
    }
}
