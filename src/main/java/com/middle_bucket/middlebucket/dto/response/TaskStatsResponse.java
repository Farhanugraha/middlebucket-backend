package com.middle_bucket.middlebucket.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TaskStatsResponse {

    private long todo;
    private long inProgress;
    private long pendingReview;
    private long done;
    private long total;

}
