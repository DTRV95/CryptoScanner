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

        boolean isBelowSMA = currentPrice < sma;
        boolean isRSIInOversoldRange = rsi > 15 && rsi < 40;

        boolean isNearSupport = TechnicalIndicators.isNearSupport(prices, currentPrice);
        boolean isNearResistance = TechnicalIndicators.isNearResistance(prices, currentPrice);

        return (isBelowSMA && isRSIInOversoldRange && isNearSupport) || (isRSIInOversoldRange && isNearSupport && volume > TechnicalIndicators.calculateAverageVolume(volumes));
    }
}