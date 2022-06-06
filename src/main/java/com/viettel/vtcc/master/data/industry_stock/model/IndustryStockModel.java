package com.viettel.vtcc.master.data.industry_stock.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class IndustryStockModel {
    private String id;
    private String stock_id;
    private String stock_name;
    private String exchange;
    private long industry_id;
    private String industry_name;
    private long broad_industry_id;
    private String broad_industry_name;
}
