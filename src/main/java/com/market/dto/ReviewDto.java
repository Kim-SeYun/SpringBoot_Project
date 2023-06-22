package com.market.dto;

import com.market.entity.Item;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewDto {

    private Long id;

    private Item item;

    private String content;

    private String writer;

//    private int rating;

    private LocalDateTime reviewDate;
}
