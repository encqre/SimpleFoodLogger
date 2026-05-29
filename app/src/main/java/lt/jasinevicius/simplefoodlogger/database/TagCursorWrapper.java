package lt.jasinevicius.simplefoodlogger.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import lt.jasinevicius.simplefoodlogger.Tag;
import lt.jasinevicius.simplefoodlogger.database.DbSchema.Tags;

import java.util.UUID;

public class TagCursorWrapper extends CursorWrapper {
    public TagCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Tag getTag() {
        String tagId = getString(getColumnIndex(Tags.Cols.TAG_ID));
        String name = getString(getColumnIndex(Tags.Cols.NAME));
        int type = getInt(getColumnIndex(Tags.Cols.TYPE));
        int orderId = getInt(getColumnIndex(Tags.Cols.ORDER_ID));
        String color = getString(getColumnIndex(Tags.Cols.COLOR));

        Tag tag = new Tag(UUID.fromString(tagId));
        tag.setName(name);
        tag.setType(type);
        tag.setOrderId(orderId);
        tag.setColor(color);

        return tag;
    }
}
