package com.untrustworthypillars.simplefoodlogger.formatting;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.widget.TextView;

import com.untrustworthypillars.simplefoodlogger.R;

public class ScrollableDialogTitle {

    public TextView ScrollableTitle;

    public ScrollableDialogTitle(Context context, String title) {
        ScrollableTitle = new TextView(context);
        ScrollableTitle.setSingleLine(true);
        ScrollableTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        ScrollableTitle.setMarqueeRepeatLimit(9999);
        ScrollableTitle.setTextSize(20);
        ScrollableTitle.setTextColor(context.getResources().getColor(R.color.black));
        ScrollableTitle.setPadding(25, 25, 25, 0);
        ScrollableTitle.setTypeface(Typeface.DEFAULT_BOLD);
        ScrollableTitle.setText(title);
        ScrollableTitle.setHorizontallyScrolling(true);
        ScrollableTitle.setSelected(true);
    }



}
