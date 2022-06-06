package com.viettel.vtcc.master.data.label.model;

import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;

@Data
public class LabelModel {
    private String text;
    private String type;
    private String date;
    private String stock;
    private float change;
    private List<Float> three_day_previous_change;
    private List<Float> three_day_following_change;

    @Override
    public String toString() {
        return new StringBuilder()
                .append(text).append("\t")
                .append(type).append("\t")
                .append(date).append("\t")
                .append(stock).append("\t")
                .append(StringUtils.join(three_day_previous_change, ";")).append("\t")
                .append(change).append("\t")
                .append(StringUtils.join(three_day_following_change, ";")).append("\t")
                .toString();
    }
}
