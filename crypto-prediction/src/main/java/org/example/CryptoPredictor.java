package org.example;

import java.util.List;

public class CryptoPredictor {

    public static boolean shouldBuy(List<Double> prices, int smaPeriod, int rsiPeriod, int shortEMAPeriod, int longEMAPeriod) {

        if (prices.size() < smaPeriod || prices.size() < rsiPeriod + 1 || prices.size() < longEMAPeriod + 1) {
            return false;
        }

        double currentPrice = prices.get(prices.size() - 1);
        double sma = TechnicalIndicators.calculateSMA(prices, smaPeriod);
        double rsi = TechnicalIndicators.calculateRSI(prices, rsiPeriod);

        boolean isAboveSMA = currentPrice > sma;
        boolean isRSIInOversoldRange = rsi > 10 && rsi < 60;

        return isAboveSMA && isRSIInOversoldRange;
    }
}