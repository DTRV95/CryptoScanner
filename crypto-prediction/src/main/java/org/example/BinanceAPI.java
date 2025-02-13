package org.example;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BinanceAPI {

    private static final String BASE_URL = "https://api.binance.com";

    public static List<Double> fetchHistoricalPrices(String symbol, String interval, int limit) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String url = BASE_URL + "/api/v3/klines?symbol=" + symbol + "&interval=" + interval + "&limit=" + limit;

        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        String responseData = response.body().string();
        JSONArray jsonArray = new JSONArray(responseData);

        List<Double> prices = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONArray kline = jsonArray.getJSONArray(i);
            double closePrice = kline.getDouble(4);
            prices.add(closePrice);
        }
        return prices;
    }

    public static List<Double> fetchHistoricalVolumes(String symbol, String interval, int limit) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String url = BASE_URL + "/api/v3/klines?symbol=" + symbol + "&interval=" + interval + "&limit=" + limit;

        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        String responseData = response.body().string();
        JSONArray jsonArray = new JSONArray(responseData);

        List<Double> volumes = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONArray kline = jsonArray.getJSONArray(i);
            double volume = kline.getDouble(5);
            volumes.add(volume);
        }
        return volumes;
    }

    public static List<String> fetchAllSymbols() throws IOException {
        OkHttpClient client = new OkHttpClient();
        String url = BASE_URL + "/api/v3/exchangeInfo";

        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }

        String responseData = response.body().string();
        System.out.println("Response from Binance API: " + responseData);

        try {
            JSONObject jsonObject = new JSONObject(responseData);
            JSONArray symbolsArray = jsonObject.getJSONArray("symbols");

            List<String> symbols = new ArrayList<>();
            for (int i = 0; i < symbolsArray.length(); i++) {
                String symbol = symbolsArray.getJSONObject(i).getString("symbol");
                symbols.add(symbol);
            }
            return symbols;
        } catch (JSONException e) {
            System.err.println("Error parsing JSON response: " + responseData);
            throw e;
        }
    }
}
