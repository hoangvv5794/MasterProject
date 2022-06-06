package com.viettel.vtcc.master.data.industry_stock.process;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.vtcc.master.data.industry_stock.model.IndustryStockModel;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class IndustryProcess {
    public static void main(String[] args) throws IOException {
        FileOutputStream fop = new FileOutputStream("C:\\Users\\KGM\\Downloads\\edit_industry.json", true);
        ObjectMapper mapper = new ObjectMapper();
        List<String> list_line = FileUtils.readLines(new File("C:\\Users\\KGM\\Downloads\\industry.csv"), "UTF-8");
        list_line.forEach(line -> {
            String[] split = line.split(",");
            if (split.length == 9) {
                String stock_key = split[2];
                if (stock_key.length() == 3) {
                    String id = split[1];
                    String stock_name = split[3];
                    String exchange = split[4];
                    String industry_id = split[5];
                    String industry_name = split[6];
                    String broad_industry_id = split[7];
                    String broad_industry_name = split[8];
                    IndustryStockModel modelStock = IndustryStockModel.builder()
                            .id(id)
                            .stock_id(stock_key)
                            .stock_name(stock_name)
                            .exchange(exchange)
                            .industry_id(Long.parseLong(industry_id))
                            .industry_name(industry_name)
                            .broad_industry_id(Long.parseLong(broad_industry_id))
                            .broad_industry_name(broad_industry_name)
                            .build();
                    try {
                        String data = mapper.writeValueAsString(modelStock);
                        fop.write((data + "\n").getBytes(StandardCharsets.UTF_8));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        fop.close();
    }
}
