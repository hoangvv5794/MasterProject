package com.viettel.vtcc.master.data.report.bigdata.process;

import com.google.gson.JsonObject;
import com.viettel.vtcc.master.data.ohlc.StockUtils;
import com.viettel.vtcc.master.data.report.bigdata.es.EsUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class ProcessVN30 {

    private static final String FILE_VNINDEX_30 = "vnindex30.txt";
    private static final SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");

    public static void main(String[] args) {
        try {
            EsUtils.initClient();
            String index_30 = FileUtils.readFileToString(new File(FILE_VNINDEX_30), "UTF-8");
            for (String index : index_30.split(",")) {
                try {
                    String watch_day = date_format.format(new Date());
                    float change = StockUtils.getChangeDay(index, watch_day);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("stock_id", index);
                    jsonObject.addProperty("value", change);
                    String data = jsonObject.toString();
                    log.info("write data to es {} {}", index, change);
                    EsUtils.indexDocument(data, "index_30");
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            EsUtils.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
