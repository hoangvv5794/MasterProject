package com.viettel.vtcc.master.data.telegram.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ModelTelegram {
    private long id;
    private String date;
    private String from;
    private String from_id;
    private String text;
    private List<String> list_stock;
}
