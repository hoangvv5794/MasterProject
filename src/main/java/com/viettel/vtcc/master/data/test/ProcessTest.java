package com.viettel.vtcc.master.data.test;

import com.viettel.vtcc.master.data.report.bigdata.kafka.KafkaPublisher;
import com.viettel.vtcc.master.data.utils.ConfigurationLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;

@Slf4j
public class ProcessTest {
    private static final KafkaPublisher kafkaPublisher = new KafkaPublisher();
    private static final String FOLDER_FORUM = ConfigurationLoader.getInstance().getAsString("folder.forum", "C:\\Users\\KGM\\Downloads\\data_forum\\data_forum\\raw_data\\");
    private static final String FOLDER_TELEGRAM = ConfigurationLoader.getInstance().getAsString("folder.telegram", "C:\\Users\\KGM\\Downloads\\stock_telegram\\order_data_by_month\\");

    public static void main(String[] args) {
        String date_process = "202108";
        // push forum msg to kafka topic forum_data
//        Thread thread = new Thread(() -> {
//            String file_forum = FOLDER_FORUM + "forum_data_" + date_process + ".txt";
//            try {
//                List<String> listLines = FileUtils.readLines(new File(file_forum), "UTF-8");
//                listLines.forEach(line -> {
//                    kafkaPublisher.push_data(line, "forum_data");
//                });
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//        thread.start();
        // push telegram msg to kafka topic telegram_data
        new Thread(() -> {
            String file_telegram = FOLDER_TELEGRAM + "telegram_data_" + date_process + ".txt";
            try {
                List<String> listLines = FileUtils.readLines(new File(file_telegram), "UTF-8");
                listLines.forEach(line -> {
                    kafkaPublisher.push_data(line, "telegram_data");
                });
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }).start();

    }
}
