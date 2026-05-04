package com.middle_bucket.middlebucket.dto.response;


import com.middle_bucket.middlebucket.entity.Memo;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class MemoResponse {

    private Long id;
    private String memoNumber;
    private LocalDate memoDate;
    private String memoFrom;
    private String shortDescription;
    private String description;
    private Long authorId;
    private String authorName;
    private LocalDateTime createdAt;

    public static MemoResponse from (Memo memo){
        MemoResponse dto = new MemoResponse();
        dto.setId(memo.getId());
        dto.setMemoNumber(memo.getMemoNumber());
        dto.setMemoDate(memo.getMemoDate());
        dto.setMemoFrom(memo.getMemoFrom());
        dto.setShortDescription(memo.getShortDescription());
        dto.setDescription(memo.getDescription());
        dto.setCreatedAt(memo.getCreatedAt());

        if (memo.getAuthor() != null) {
            dto.setAuthorId(memo.getAuthor().getId());
            dto.setAuthorName(memo.getAuthor().getName());
        }
        return dto;
    }
}
