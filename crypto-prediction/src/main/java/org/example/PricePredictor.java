package org.example;

import java.util.List;
import java.util.stream.IntStream;

public class PricePredictor {

    public static double predictNextPrice(List<Double> prices) {
        if (prices == null || prices.size() < 2) {
            throw new IllegalArgumentException("Not enough data to make a prediction");
        }

        // Check for null values in the list
        if (prices.stream().anyMatch(price -> price == null)) {
            throw new IllegalArgumentException("Price list contains null values");
        }

        // Use a simple moving average for prediction
        int period = 5; // Choose an appropriate period for the moving average
        double movingAverage = calculateMovingAverage(prices, period);

        // Simple prediction assuming the next price will be close to the moving average
        return movingAverage;
    }

    private static double calculateMovingAverage(List<Double> prices, int period) {
        if (prices.size() < period) {
            period = prices.size();
        }

        return prices.stream()
                .skip(prices.size() - period)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(Double.NaN);
    }

    public static String estimateTimeToReachPrice(List<Double> prices, double targetPrice, String intervalType) {
        if (prices == null || prices.size() < 2) {
            throw new IllegalArgumentException("Not enough data to make a prediction");
        }

        // Check for null values in the list
        if (prices.stream().anyMatch(price -> price == null)) {
            throw new IllegalArgumentException("Price list contains null values");
        }

        double lastPrice = prices.get(prices.size() - 1);
        double averageRateOfChange = IntStream.range(1, prices.size())
                .mapToDouble(i -> prices.get(i) - prices.get(i - 1))
                .average()
                .orElse(0);

        if (averageRateOfChange == 0) {
            return "Price is not changing";
        }

        double timeToReach = (targetPrice - lastPrice) / averageRateOfChange;

        if (timeToReach < 0) {
            if ((averageRateOfChange > 0 && targetPrice < lastPrice) || (averageRateOfChange < 0 && targetPrice > lastPrice)) {
                return "Price will not be reached in the current trend";
            }
        }

        return String.format("%.2f %s", timeToReach, intervalType);
    }
}
