package com.middle_bucket.middlebucket.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
@Getter
@Setter
public class TaskRequest {

    private String name;
    private String description;
    private String priority;
    private LocalDate dueDate;
    private Long assigneeId;
    private String status;

}
