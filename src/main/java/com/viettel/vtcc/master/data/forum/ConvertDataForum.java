package com.viettel.vtcc.master.data.forum;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.vtcc.master.data.forum.model.ForumModel;
import com.viettel.vtcc.master.data.industry_stock.model.IndustryStockModel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ConvertDataForum {
    private static final String PATH_INPUT = "C:\\Users\\KGM\\Downloads\\data_forum\\data_forum\\raw_data";
    private static final String PATH_OUTPUT = "C:\\Users\\KGM\\Downloads\\data_forum\\data_forum\\forum_data_contain_stock";
    private static final String FILE_INPUT = "orm_article_2204.txt";
    private static final String FILE_INPUT_INDUSTRY = "C:\\Users\\KGM\\Downloads\\industry_stock_object.json";
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final List<IndustryStockModel> list_industry_stock = new LinkedList<>();

    public static void main(String[] args) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(PATH_OUTPUT + "\\" + FILE_INPUT, true);
            FileUtils
                    .readLines(new File(FILE_INPUT_INDUSTRY), "UTF-8")
                    .forEach(line -> {
                        try {
                            IndustryStockModel industryStockModel = mapper.readValue(line, IndustryStockModel.class);
                            list_industry_stock.add(industryStockModel);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    });
            log.info("load stock industry {}", list_industry_stock.size());
            List<String> listLines = FileUtils.readLines(new File(PATH_INPUT + "\\" + FILE_INPUT), "UTF-8");
            listLines.forEach(line -> {
                try {
                    RawForum rawForum = mapper.readValue(line, RawForum.class);
                    ForumModel forumModel = new ForumModel();
                    forumModel.setId(Long.parseLong(rawForum.getId()));
                    forumModel.setFrom(rawForum.getTitle());
                    forumModel.setText(rawForum.getContent());
                    forumModel.setDate(rawForum.getPublished_time());
                    List<String> list_stock = list_industry_stock
                            .stream()
                            .filter(industryStockModel -> rawForum.getContent().contains(industryStockModel.getStock_id()))
                            .map(IndustryStockModel::getStock_id)
                            .collect(Collectors.toList());
                    if (!list_stock.isEmpty()) {
                        forumModel.setList_stock(list_stock);
                        try {
                            String data_output = mapper.writeValueAsString(forumModel);
                            fileOutputStream.write((data_output + "\n").getBytes(StandardCharsets.UTF_8));
                        } catch (IOException e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class RawForum {
        private String id;
        private String published_time;
        private String content;
        private String title;
    }
}
