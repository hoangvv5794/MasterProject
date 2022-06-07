package com.viettel.vtcc.master.data.ohlc;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.io.FileUtils;

import javax.management.timer.Timer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class CollectDataStock {

    private static final OkHttpClient client = new OkHttpClient();
    private static final String URL_PATH = "https://finfo-api.vndirect.com.vn/v4/stock_prices/?sort=date&size=400&q=code:__STOCK_ID__~date:gte:2021-05-01~date:lte:2022-05-15&page=1";
    private static final String FILE_STOCK = "C:\\Users\\KGM\\Downloads\\stock_telegram\\industry_stock_id_broad_band.txt";
    private static final String FOLDER_OHLC = "C:\\Users\\KGM\\Downloads\\stock_telegram\\ohlc\\";

    public static void main(String[] args) throws IOException {
        List<String> list_lines = FileUtils.readLines(new File(FILE_STOCK), "UTF-8");
        for (String line : list_lines) {
            try {
                String stock_id = line.split("\t")[0];
                log.info("process {}", stock_id);
                FileOutputStream fop = new FileOutputStream(FOLDER_OHLC + "\\" + stock_id + ".txt", true);
                String url = URL_PATH.replace("__STOCK_ID__", stock_id);
                Request request = new Request.Builder()
                        .url(url)
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .build();
                String data_response = client.newCall(request).execute().body().string();
                fop.write(data_response.getBytes(StandardCharsets.UTF_8));
                fop.close();
                Thread.sleep(2 * Timer.ONE_SECOND);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
