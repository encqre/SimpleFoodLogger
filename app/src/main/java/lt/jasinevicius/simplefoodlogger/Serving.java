package lt.jasinevicius.simplefoodlogger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Serving {

    public static final int TYPE_CUSTOM = 0;
    public static final int TYPE_DEFAULT = 1;

    private UUID servingId;
    private UUID foodId;
    private String name;
    private Float size;
    private int type;

    public Serving() {
        this(UUID.randomUUID());
    }

    public Serving(UUID id) {
        servingId = id;
    }

    public UUID getServingId() {
        return servingId;
    }

    public UUID getFoodId() {
        return foodId;
    }

    public void setFoodId(UUID foodId) {
        this.foodId = foodId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getSize() {
        return size;
    }

    public void setSize(Float size) {
        this.size = size;
    }

    public int getType(){
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Serving other = (Serving) o;

        return (
            servingId.toString().equals(other.getServingId().toString()) &&
            foodId.toString().equals(other.getFoodId().toString())
        );
    }

    /** Returns list difference - elements in 'a' that are not in 'b' */
    public static List<Serving> listDiff(List<Serving> a, List<Serving> b) {
        List<Serving> diff = new ArrayList<>();
        for (Serving serving : a) {
            boolean found = false;
            for (Serving other : b) {
                if (serving.equals(other)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                diff.add(serving);
            }
        }
        return diff;
    }

    /** Returns list of overlapping elements in both lists*/
    public static List<Serving> listOverlap(List<Serving> a, List<Serving> b) {
        List<Serving> overlap = new ArrayList<>();
        for (Serving serving : a) {
            boolean found = false;
            for (Serving other : b) {
                if (serving.equals(other)) {
                    found = true;
                    break;
                }
            }
            if (found) {
                overlap.add(serving);
            }
        }
        return overlap;
    }
}
