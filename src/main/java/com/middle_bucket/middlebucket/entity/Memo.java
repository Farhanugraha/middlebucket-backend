package com.middle_bucket.middlebucket.entity;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "memos")
@Data

public class Memo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "memo_number", nullable = false, unique = true)
    private String memoNumber;

    @Column(name = "memo_date", nullable = false)
    private LocalDate memoDate;

    @Column(name = "memo_from", nullable = false)
    private String memoFrom;

    @Column(name = "short_description", nullable = false)
    private String shortDescription;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void  onCreate(){
        createdAt = LocalDateTime.now();
    }



}
