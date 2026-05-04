package com.middle_bucket.middlebucket.dto.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MemoRequest {

    private String memoNumber;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String memoDate;
    private String memoFrom;
    private String shortDescription;
    private String description;

}
