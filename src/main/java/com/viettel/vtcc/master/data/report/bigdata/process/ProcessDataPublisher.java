package com.viettel.vtcc.master.data.report.bigdata.process;

import com.viettel.vtcc.master.data.report.bigdata.kafka.KafkaPublisher;
import com.viettel.vtcc.master.data.utils.ConfigurationLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
public class ProcessDataPublisher {

    private static final KafkaPublisher kafkaPublisher = new KafkaPublisher();
    private static final String FOLDER_FORUM = ConfigurationLoader.getInstance().getAsString("folder.forum", "C:\\Users\\KGM\\Downloads\\data_forum\\data_forum\\raw_data\\");
    private static final String FOLDER_TELEGRAM = ConfigurationLoader.getInstance().getAsString("folder.telegram", "C:\\Users\\KGM\\Downloads\\stock_telegram\\order_data_by_month\\");

    public static void main(String[] args) {
        // push forum msg to kafka topic forum_data

        Thread thread = new Thread(() -> {
            while (true){
                for (File file : new File(FOLDER_FORUM).listFiles()) {
                    try {
                        try {
                            List<String> listLines = FileUtils.readLines(file, "UTF-8");
                            listLines.forEach(line -> kafkaPublisher.push_data(line, "forum_data"));
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        });
        thread.start();
        // push telegram msg to kafka topic telegram_data
        new Thread(() -> {
            while (true){
                for (File file : new File(FOLDER_TELEGRAM).listFiles()) {
                    try {
                        try {
                            List<String> listLines = FileUtils.readLines(file, "UTF-8");
                            listLines.forEach(line -> kafkaPublisher.push_data(line, "telegram_data"));
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }).start();
    }
}
