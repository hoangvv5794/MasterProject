package com.viettel.vtcc.master.data.telegram.process;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.viettel.vtcc.master.data.telegram.model.ModelTelegram;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class StockDataSplitter {
    private static final String FOLDER_DATA = "C:\\Users\\KGM\\Downloads\\stock_telegram\\filter_empty_message";
    private static final String FILE_DATA_SORTED = "C:\\Users\\KGM\\Downloads\\stock_telegram\\filter_empty_message\\telegram_data_sorted.txt";
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final SimpleDateFormat date_format = new SimpleDateFormat("yyyyMM");
    private static final SimpleDateFormat date_input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final Map<ModelTelegram, Long> unsortMap = new HashMap<>();
    private static List<String> list_stock = new LinkedList<>();

    public static void main(String[] args) throws IOException {
        FileUtils.readLines(new File("C:\\Users\\KGM\\Downloads\\industry_stock_object.json"), "UTF-8")
                .forEach(line -> {
                    JsonObject jsonObject = JsonParser.parseString(line).getAsJsonObject();
                    String id = jsonObject.get("stock_id").getAsString();
                    list_stock.add(id);
                });
        splitDataByMonth();
    }

    private static void splitDataByMonth() throws IOException {
        List<String> listLines = FileUtils.readLines(new File(FILE_DATA_SORTED), "UTF-8");
        listLines.forEach(line -> {
            try {
                ModelTelegram modelTelegram = mapper.readValue(line, ModelTelegram.class);
                List<String> list_stock_contain = list_stock.stream()
                        .filter(stock -> modelTelegram.getText().contains(stock))
                        .collect(Collectors.toList());
                modelTelegram.setList_stock(list_stock_contain);
                String date = modelTelegram.getDate();
                String file_output = date_format.format(new Date(date_input.parse(date).getTime()));
                FileOutputStream fop = new FileOutputStream("telegram_data_" + file_output + ".txt", true);
                fop.write((mapper.writeValueAsString(modelTelegram) + "\n").getBytes(StandardCharsets.UTF_8));
                fop.close();
            } catch (ParseException | IOException e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    private static void orderDataByDate() throws IOException {
        File folder_data = new File(FOLDER_DATA);
        FileOutputStream fop = new FileOutputStream("telegram_data_sorted.txt", true);
        for (File file : folder_data.listFiles()) {
            try {
                List<String> listLines = FileUtils.readLines(file, "UTF-8");
                listLines.forEach(line -> {
                    try {
                        ModelTelegram modelTelegram = mapper.readValue(line, ModelTelegram.class);
                        String published_time = modelTelegram.getDate().replace("T", " ");
                        modelTelegram.setDate(published_time);
                        long time = date_input.parse(published_time).getTime();
                        unsortMap.put(modelTelegram, time);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                });
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        List<ModelTelegram> sorted = unsortMap.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        sorted.forEach(modelTelegram -> {
            try {
                fop.write((mapper.writeValueAsString(modelTelegram) + "\n").getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        fop.close();
    }

}
