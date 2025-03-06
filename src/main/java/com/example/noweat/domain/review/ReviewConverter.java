package com.example.noweat.domain.review;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ReviewConverter implements AttributeConverter<StarRating, Integer> {

    @Override
    public Integer convertToDatabaseColumn(StarRating starRating) {
        return starRating.ordinal() + 1;
    }

    @Override
    public StarRating convertToEntityAttribute(Integer integer) {
        StarRating findRating = null;
        StarRating[] starRatings = StarRating.values();
        for(StarRating starRating : starRatings){
            if(starRating.ordinal() == integer - 1){
                findRating = starRating;
            }
        }
        return findRating;
    }
}
