package com.middle_bucket.middlebucket.dto.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DailyReportRequest {
    private  String reportDate;
    private String content;
}
