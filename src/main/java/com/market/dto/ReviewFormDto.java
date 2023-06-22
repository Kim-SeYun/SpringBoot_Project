package com.market.dto;

import com.market.entity.Item;
import com.market.entity.Review;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ReviewFormDto {

    private Long id;

    @NotBlank(message = "내용 필수 입력 값입니다.")
    private String content;

    private String writer;

    private LocalDateTime reviewDate;

    private List<ReviewImgDto> reviewImgDtoList = new ArrayList<>();

    private List<Long> reviewImgIds = new ArrayList<>();

    private static ModelMapper modelMapper = new ModelMapper();

    public Review createReview(Long itemId) {
        Review review = modelMapper.map(this, Review.class);
        review.setItem(new Item());
        review.getItem().setId(itemId);
        return review;
    }

    public static ReviewFormDto of(Review review){
        return modelMapper.map(review, ReviewFormDto.class);
    }
}
