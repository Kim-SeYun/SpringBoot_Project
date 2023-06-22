package com.market.dto;

import com.market.entity.ReviewImg;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ReviewImgDto {

    private Long id;

    private String imgName;

    private String oriImgName;

    private String imgUrl;

    private String repImgYn;

    private MultipartFile reviewImgFile;

    private static ModelMapper modelMapper = new ModelMapper();

    public static ReviewImgDto of(ReviewImg reviewImg){
        return modelMapper.map(reviewImg, ReviewImgDto.class);
    }
}
