package com.viettel.vtcc.master.data.ohlc;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import javax.management.timer.Timer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class StockUtils {
    private static final SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
    private static final OkHttpClient client = new OkHttpClient();
    private static final String FORMAT_URL_STOCK = "https://finfo-api.vndirect.com.vn/v4/stock_prices/?sort=date&size=20&q=code:__STOCK__ID__~date:gte:__DAY_START__~date:lte:__DAY_END__&page=1";


    public static Float getChangeDay(String stock_id, String watch_day) {
        try {
            try {
                // check watch_day => if watch_day is weekend => turn watch_day to Friday and processing
                Date date = date_format.parse(watch_day);
                int day_of_week = getDayNumberOld(date);
                if (day_of_week == 1) {
                    date = new Date(date.getTime() - 2 * Timer.ONE_DAY);
                }
                if (day_of_week == 7) {
                    date = new Date(date.getTime() - Timer.ONE_DAY);
                }
                watch_day = date_format.format(date);
                String date_start = date_format.format(new Date(date.getTime() - 7 * Timer.ONE_DAY));
                String date_end = date_format.format(new Date(date.getTime() + 7 * Timer.ONE_DAY));
                String url_request = FORMAT_URL_STOCK.replace("__STOCK__ID__", stock_id)
                        .replace("__DAY_START__", date_start)
                        .replace("__DAY_END__", date_end);
                log.info("request to get price {}", url_request);
                Request request = new Request.Builder()
                        .url(url_request)
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .build();
                String data_stock = client.newCall(request).execute().body().string();
                return parseStock(data_stock, watch_day, false).getMiddle();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return 0f;
    }


    public static Triple<List<Float>, Float, List<Float>> getPriceInDay(String stock_id, String watch_day) {
        try {
            // check watch_day => if watch_day is weekend => turn watch_day to Friday and processing
            Date date = date_format.parse(watch_day);
            int day_of_week = getDayNumberOld(date);
            if (day_of_week == 1) {
                date = new Date(date.getTime() - 2 * Timer.ONE_DAY);
            }
            if (day_of_week == 7) {
                date = new Date(date.getTime() - Timer.ONE_DAY);
            }
            watch_day = date_format.format(date);
            String date_start = date_format.format(new Date(date.getTime() - 7 * Timer.ONE_DAY));
            String date_end = date_format.format(new Date(date.getTime() + 7 * Timer.ONE_DAY));
            String url_request = FORMAT_URL_STOCK.replace("__STOCK__ID__", stock_id)
                    .replace("__DAY_START__", date_start)
                    .replace("__DAY_END__", date_end);
            log.info("request to get price {}", url_request);
            Request request = new Request.Builder()
                    .url(url_request)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .build();
            String data_stock = client.newCall(request).execute().body().string();
            return parseStock(data_stock, watch_day, true);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    private static Triple<List<Float>, Float, List<Float>> parseStock(String data_stock, String day_watch, boolean get_another_day) {
        try {
            JsonArray jsonArray = JsonParser.parseString(data_stock)
                    .getAsJsonObject()
                    .getAsJsonArray("data");
            // find watch_day
            int point_index = 0;
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                String date = jsonObject.get("date").getAsString();
                if (date.equalsIgnoreCase(day_watch)) {
                    point_index = i;
                }
            }
            // get 3 days previous and 3 days next to save
            JsonObject json_watch_day = jsonArray.get(point_index).getAsJsonObject();
            float change_watch_day = json_watch_day.get("change").getAsFloat();
            List<Float> three_day_following_change = new LinkedList<>();
            List<Float> three_day_previous_change = new LinkedList<>();
            if (get_another_day) {
                for (int i = point_index - 1; i > point_index - 4; i--) {
                    JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                    float change = jsonObject.get("change").getAsFloat();
                    three_day_following_change.add(change);
                }
                for (int i = point_index + 3; i > point_index ; i--) {
                    JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                    float change = jsonObject.get("change").getAsFloat();
                    three_day_previous_change.add(change);
                }
            }
            return new MutableTriple<>(three_day_previous_change, change_watch_day, three_day_following_change);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    private static int getDayNumberOld(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }
}
