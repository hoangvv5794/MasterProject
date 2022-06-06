package com.viettel.vtcc.master.data.forum.model;

import lombok.Data;

import java.util.List;

@Data
public class ForumModel {
    private long id;
    private String date;
    private String text;
    private String from;
    private String from_id;
    private List<String> list_stock;
}
