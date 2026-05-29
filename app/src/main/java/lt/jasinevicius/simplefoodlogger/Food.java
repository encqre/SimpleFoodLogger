package lt.jasinevicius.simplefoodlogger;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Food {

    public static final int TYPE_CUSTOM = 0;
    public static final int TYPE_DEFAULT = 1;
    public static final int TYPE_DEFAULT_MODIFIED = 2;
    public static final int TYPE_DEFAULT_HIDDEN = 3;
    public static final int TYPE_DEFAULT_MODIFIED_HIDDEN = 4;

    public static final int PRIORITY_CUSTOM = 0;
    public static final int PRIORITY_COMMON_MODIFIED = 100;
    public static final int PRIORITY_COMMON = 200;
    public static final int PRIORITY_DEFAULT_MODIFIED = 900;
    public static final int PRIORITY_DEFAULT = 1000;

    private UUID foodId;
    private String name;
    private Float kcal;
    private Float protein;
    private Float carbs;
    private Float fat;
    private int type;
    private int priority; // for now - to display more relevant default food higher. Lower is better
    private boolean isFavorite;
    private int consumedCount;
    private Date lastConsumed;
    private List<Tag> tags;
    private List<Serving> servings;


    public Food() {
        this(UUID.randomUUID());
    }

    public Food(UUID id) {
        foodId = id;
    }

    public UUID getFoodId() {
        return foodId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getKcal() {
        return kcal;
    }

    public void setKcal(Float kcal) {
        this.kcal = kcal;
    }

    public Float getProtein() {
        return protein;
    }

    public void setProtein(Float protein) {
        this.protein = protein;
    }

    public Float getCarbs() {
        return carbs;
    }

    public void setCarbs(Float carbs) {
        this.carbs = carbs;
    }

    public Float getFat() {
        return fat;
    }

    public void setFat(Float fat) {
        this.fat = fat;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public int getConsumedCount() {
        return consumedCount;
    }

    public void setConsumedCount(int consumedCount) {
        this.consumedCount = consumedCount;
    }

    public Date getLastConsumed() {
        return lastConsumed;
    }

    public void setLastConsumed(Date lastConsumed) {
        this.lastConsumed = lastConsumed;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
    }

    public List<Serving> getServings() {
        return servings;
    }

    public void setServings(List<Serving> servings) {
        this.servings = servings;
    }

    public void addServing(Serving serving) {
        servings.add(serving);
    }

    public void removeServing(Serving serving) {
        servings.remove(serving);
    }
}
