package com.market.service;

import com.market.entity.Review;
import com.market.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public List<Review> getReviewById(Long itemId){
        List<Review> reviews = reviewRepository.findByItemId(itemId);
        return reviews;
    }

    public void saveReview(Review review){
        reviewRepository.save(review);
    }
}
