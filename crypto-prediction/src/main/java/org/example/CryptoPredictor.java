package org.example;

import java.util.List;

public class CryptoPredictor {

    public static boolean shouldBuy(List<Double> prices, List<Double> volumes, int smaPeriod, int rsiPeriod, int shortEMAPeriod, int longEMAPeriod) {
        if (prices.size() < smaPeriod || prices.size() < rsiPeriod + 1 || prices.size() < longEMAPeriod + 1) {
            return false;
        }

        double currentPrice = prices.get(prices.size() - 1);
        double sma = TechnicalIndicators.calculateSMA(prices, smaPeriod);
        double rsi = TechnicalIndicators.calculateRSI(prices, rsiPeriod);
        double volume = volumes.get(volumes.size() - 1);
        double[] macd = TechnicalIndicators.calculateMACD(prices, shortEMAPeriod, longEMAPeriod, 9); // Signal period fixed at 9

        boolean isBelowSMA = currentPrice < sma;
        boolean isRSIInOversoldRange = rsi > 15 && rsi < 30;
        boolean isMACDPositive = macd[0] > macd[1]; // MACD line is above signal line

        boolean isNearSupport = TechnicalIndicators.isNearSupport(prices, currentPrice);
        boolean isNearResistance = TechnicalIndicators.isNearResistance(prices, currentPrice);

        return (isBelowSMA && isRSIInOversoldRange && isMACDPositive && isNearSupport) || (isRSIInOversoldRange && isNearSupport && volume > TechnicalIndicators.calculateAverageVolume(volumes));
    }

    public static Action determineAction(List<Double> prices, List<Double> volumes, int smaPeriod, int rsiPeriod, int shortEMAPeriod, int longEMAPeriod, int signalPeriod) {
            if (prices.size() < smaPeriod || prices.size() < rsiPeriod + 1 || prices.size() < longEMAPeriod + 1) {
                return Action.HOLD;
            }

            double currentPrice = prices.get(prices.size() - 1);
            double sma = TechnicalIndicators.calculateSMA(prices, smaPeriod);
            double rsi = TechnicalIndicators.calculateRSI(prices, rsiPeriod);
            double[] macd = TechnicalIndicators.calculateMACD(prices, shortEMAPeriod, longEMAPeriod, signalPeriod);
            double[] bollingerBands = TechnicalIndicators.calculateBollingerBands(prices, smaPeriod, 2.0);

            boolean isTrendingUp = currentPrice > sma;
            boolean isRSIInOversoldRange = rsi > 15 && rsi < 30;
            boolean isRSIOverbought = rsi > 60;
            boolean isMACDPositive = macd[0] > macd[1];
            boolean isMACDNegative = macd[0] < macd[1];
            boolean isAboveUpperBollingerBand = currentPrice > bollingerBands[0];
            boolean isBelowLowerBollingerBand = currentPrice < bollingerBands[2];

            if (isTrendingUp && isRSIInOversoldRange && isMACDPositive && TechnicalIndicators.isNearSupport(prices, currentPrice)) {
                return Action.BUY;
            } else if ((!isTrendingUp && (isRSIOverbought || isMACDNegative || TechnicalIndicators.isNearResistance(prices, currentPrice) || isAboveUpperBollingerBand))) {
                return Action.SELL;
            } else {
                return Action.HOLD;
            }
        }
    }
