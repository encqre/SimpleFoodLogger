package com.untrustworthypillars.simplefoodlogger;

import java.util.UUID;

public class Food {

    private UUID mFoodId;
    private String mTitle;
    private String mCategory;
    private Float mKcal;
    private Float mProtein;
    private Float mCarbs;
    private Float mFat;
    private boolean mFavorite;


    public Food() {
        this(UUID.randomUUID());
    }

    public Food(UUID id) {
        mFoodId = id;
    }

    public UUID getFoodId() {
        return mFoodId;
    }


    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public Float getKcal() {
        return mKcal;
    }

    public void setKcal(Float kcal) {
        mKcal = kcal;
    }

    public Float getProtein() {
        return mProtein;
    }

    public void setProtein(Float protein) {
        mProtein = protein;
    }

    public Float getCarbs() {
        return mCarbs;
    }

    public void setCarbs(Float carbs) {
        mCarbs = carbs;
    }

    public Float getFat() {
        return mFat;
    }

    public void setFat(Float fat) {
        mFat = fat;
    }

    public boolean isFavorite() {
        return mFavorite;
    }

    public void setFavorite(boolean favorite) {
        mFavorite = favorite;
    }
}
