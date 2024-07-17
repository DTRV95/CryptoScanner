package org.example;

import java.util.List;

public class TechnicalIndicators {

    public static double calculateSMA(List<Double> prices, int period) {
        if (prices.size() < period) {
            throw new IllegalArgumentException("Not enough data to calculate SMA");
        }

        double sum = 0.0;
        for (int i = prices.size() - period; i < prices.size(); i++) {
            sum += prices.get(i);
        }
        return sum / period;
    }

    public static double calculateRSI(List<Double> prices, int period) {
        if (prices.size() < period + 1) {
            throw new IllegalArgumentException("Not enough data to calculate RSI");
        }

        double gain = 0.0, loss = 0.0;
        for (int i = prices.size() - period; i < prices.size(); i++) {
            double change = prices.get(i) - prices.get(i - 1);
            if (change > 0) {
                gain += change;
            } else {
                loss -= change;
            }
        }
        gain /= period;
        loss /= period;

        if (loss == 0) {
            return 100.0;
        }

        double rs = gain / loss;
        return 100 - (100 / (1 + rs));
    }

    public static double[] calculateMACD(List<Double> prices, int shortPeriod, int longPeriod, int signalPeriod) {
        double[] macdLine = new double[prices.size() - longPeriod + 1];
        double[] signalLine = new double[macdLine.length - signalPeriod + 1];

        for (int i = longPeriod - 1; i < prices.size(); i++) {
            double shortEMA = calculateEMA(prices, i - shortPeriod + 1, i + 1, shortPeriod);
            double longEMA = calculateEMA(prices, i - longPeriod + 1, i + 1, longPeriod);
            macdLine[i - longPeriod + 1] = shortEMA - longEMA;
        }

        for (int i = signalPeriod - 1; i < macdLine.length; i++) {
            signalLine[i - signalPeriod + 1] = calculateEMA(macdLine, i - signalPeriod + 1, i + 1, signalPeriod);
        }

        return new double[]{macdLine[macdLine.length - 1], signalLine[signalLine.length - 1]};
    }

    private static double calculateEMA(List<Double> prices, int start, int end, int period) {
        double multiplier = 2.0 / (period + 1);
        double ema = prices.get(start);
        for (int i = start + 1; i < end; i++) {
            ema += (prices.get(i) - ema) * multiplier;
        }
        return ema;
    }

    private static double calculateEMA(double[] prices, int start, int end, int period) {
        double multiplier = 2.0 / (period + 1);
        double ema = prices[start];
        for (int i = start + 1; i < end; i++) {
            ema += (prices[i] - ema) * multiplier;
        }
        return ema;
    }

    public static double[] calculateBollingerBands(List<Double> prices, int period, double multiplier) {
        if (prices.size() < period) {
            throw new IllegalArgumentException("Not enough data to calculate Bollinger Bands");
        }

        double sma = calculateSMA(prices, period);
        double variance = 0.0;
        for (int i = prices.size() - period; i < prices.size(); i++) {
            variance += Math.pow(prices.get(i) - sma, 2);
        }
        variance /= period;
        double stdDev = Math.sqrt(variance);

        return new double[]{sma - multiplier * stdDev, sma + multiplier * stdDev};
    }
}