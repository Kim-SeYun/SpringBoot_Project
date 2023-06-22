package com.market.service;

import com.market.dto.ReviewFormDto;
import com.market.dto.ReviewImgDto;
import com.market.entity.Review;
import com.market.entity.ReviewImg;
import com.market.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImgService reviewImgService;

    public List<Review> getReviewById(Long itemId) {
        return reviewRepository.findByItemId(itemId);
    }

    public void saveReview(ReviewFormDto reviewFormDto, List<MultipartFile> reviewImgFileList, Long itemId) throws Exception {
        Review review = reviewFormDto.createReview(itemId);
        reviewRepository.save(review);

        List<ReviewImgDto> reviewImgDtoList = reviewFormDto.getReviewImgDtoList();
        for (int i = 0; i < reviewImgDtoList.size(); i++) {
            ReviewImgDto reviewImgDto = reviewImgDtoList.get(i);
            MultipartFile reviewImgFile = reviewImgDto.getReviewImgFile();
            ReviewImg reviewImg = new ReviewImg();
            reviewImg.setReview(review);

            if (i == 0) {
                reviewImg.setRepimgYn("Y");
            } else {
                reviewImg.setRepimgYn("N");
            }

            reviewImgService.saveReviewImg(reviewImg, reviewImgFile);
        }
    }

}
