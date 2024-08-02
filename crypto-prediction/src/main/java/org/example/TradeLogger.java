package org.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class TradeLogger {

    private static final String LOG_FILE = "trades.log";

    public static void logTrade (String symbol, Action action, double price, double volume){

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE,true))){
            LocalDateTime now = LocalDateTime.now();
            String logEntry = String.format("%s,%s,%s,%.6f,%.2f%n", now, symbol, action, price, volume);
            writer.write(logEntry);
        } catch (IOException e) {
            System.err.println("Error logging trade: " + e.getMessage());
        }
    }
}
