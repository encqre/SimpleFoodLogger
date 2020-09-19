package lt.jasinevicius.simplefoodlogger;

import java.util.UUID;

public class Food {

    private int mType;  // 0 - custom entry, 1 - common DB, 2 - extended DB
    private UUID mFoodId;
    private int mSortID; //Might be used in the future for sorting
    private String mTitle;
    private String mCategory;
    private Float mKcal;
    private Float mProtein;
    private Float mCarbs;
    private Float mFat;
    private boolean mFavorite;
    private boolean mHidden;
    private String mPortion1Name;
    private Float mPortion1SizeMetric;
    private Float mPortion1SizeImperial;
    private String mPortion2Name;
    private Float mPortion2SizeMetric;
    private Float mPortion2SizeImperial;
    private String mPortion3Name;
    private Float mPortion3SizeMetric;
    private Float mPortion3SizeImperial;


    public Food() {
        this(UUID.randomUUID());
    }

    public Food(UUID id) {
        mFoodId = id;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public UUID getFoodId() {
        return mFoodId;
    }

    public int getSortID() {
        return mSortID;
    }

    public void setSortID(int sortID) {
        mSortID = sortID;
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

    public boolean isHidden() {
        return mHidden;
    }

    public void setHidden(boolean hidden) {
        mHidden = hidden;
    }

    public String getPortion1Name() {
        return mPortion1Name;
    }

    public void setPortion1Name(String portion1Name) {
        mPortion1Name = portion1Name;
    }

    public Float getPortion1SizeMetric() {
        return mPortion1SizeMetric;
    }

    public void setPortion1SizeMetric(Float portion1SizeMetric) {
        mPortion1SizeMetric = portion1SizeMetric;
    }

    public Float getPortion1SizeImperial() {
        return mPortion1SizeImperial;
    }

    public void setPortion1SizeImperial(Float portion1SizeImperial) {
        mPortion1SizeImperial = portion1SizeImperial;
    }

    public String getPortion2Name() {
        return mPortion2Name;
    }

    public void setPortion2Name(String portion2Name) {
        mPortion2Name = portion2Name;
    }

    public Float getPortion2SizeMetric() {
        return mPortion2SizeMetric;
    }

    public void setPortion2SizeMetric(Float portion2SizeMetric) {
        mPortion2SizeMetric = portion2SizeMetric;
    }

    public Float getPortion2SizeImperial() {
        return mPortion2SizeImperial;
    }

    public void setPortion2SizeImperial(Float portion2SizeImperial) {
        mPortion2SizeImperial = portion2SizeImperial;
    }

    public String getPortion3Name() {
        return mPortion3Name;
    }

    public void setPortion3Name(String portion3Name) {
        mPortion3Name = portion3Name;
    }

    public Float getPortion3SizeMetric() {
        return mPortion3SizeMetric;
    }

    public void setPortion3SizeMetric(Float portion3SizeMetric) {
        mPortion3SizeMetric = portion3SizeMetric;
    }

    public Float getPortion3SizeImperial() {
        return mPortion3SizeImperial;
    }

    public void setPortion3SizeImperial(Float portion3SizeImperial) {
        mPortion3SizeImperial = portion3SizeImperial;
    }
}
