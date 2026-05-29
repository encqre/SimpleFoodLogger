package lt.jasinevicius.simplefoodlogger;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Tag implements Parcelable {

    public static final int TYPE_CUSTOM = 0;
    public static final int TYPE_DEFAULT = 1;
    public static final int TYPE_DEFAULT_HIDDEN = 2;

    private UUID tagId;
    private String name;
    private int type;
    private int orderId;
    private String color;


    public Tag() {
        this(UUID.randomUUID());
    }

    public Tag(UUID id) {
        tagId = id;
    }

    public UUID getTagId() {
        return tagId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType(){
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getOrderId(){
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Tag other = (Tag) o;

        return (
            tagId.toString().equals(other.getTagId().toString()) &&
            name.equals(other.getName())
        );
    }

    /** Return list difference - elements in 'a' that are not in 'b' */
    public static List<Tag> listDiff(List<Tag> a, List<Tag> b) {
        List<Tag> diff = new ArrayList<>();
        for (Tag tag : a) {
            boolean found = false;
            for (Tag other : b) {
                if (tag.equals(other)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                diff.add(tag);
            }
        }
        return diff;
    }

    // Parcelable interface implementation
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(tagId.toString());
        parcel.writeString(name);
        parcel.writeInt(type);
        parcel.writeInt(orderId);
        parcel.writeString(color);
    }

    public static final Parcelable.Creator<Tag> CREATOR = new Parcelable.Creator<Tag>() {
        public Tag createFromParcel(Parcel in) {
            return new Tag(in);
        }

        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };

    public Tag(Parcel in) {
        tagId = UUID.fromString(in.readString());
        name = in.readString();
        type = in.readInt();
        orderId = in.readInt();
        color = in.readString();
    }
}
