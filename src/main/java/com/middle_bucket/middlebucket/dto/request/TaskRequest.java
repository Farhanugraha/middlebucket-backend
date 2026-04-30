package com.middle_bucket.middlebucket.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskRequest {

    private String name;
    private String description;
    private String priority;
    private LocalDate dueDate;
    private Long assigneeId;

}
