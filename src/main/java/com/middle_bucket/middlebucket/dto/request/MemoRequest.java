package com.middle_bucket.middlebucket.dto.request;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MemoRequest {

    private String memoNumber;
    private LocalDate memoData;
    private String memoFrom;
    private String shortDescription;
    private String description;

}
