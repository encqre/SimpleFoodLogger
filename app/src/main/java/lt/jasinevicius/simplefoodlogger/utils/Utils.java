package lt.jasinevicius.simplefoodlogger.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ScrollView;

import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.List;

import lt.jasinevicius.simplefoodlogger.R;

public class Utils {

    public static int dpToPixels(Context context, int dp) {
        return (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.getResources().getDisplayMetrics()
        );
    }

    public static int spToPixels(Context context, int sp) {
        return (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp,
            context.getResources().getDisplayMetrics()
        );
    }

    /**
     * Sets the specified image button to the given state, while modifying or
     * "graying-out" the icon as well
     *
     * @param enabled The state of the menu item
     * @param button The menu item to modify
     * @param iconResId The icon ID
     */
    public static void setImageButtonEnabled(
        Context ctx,
        boolean enabled,
        ImageButton button,
        int iconResId
    ) {
        button.setEnabled(enabled);
        Drawable originalIcon = ResourcesCompat.getDrawable(
            ctx.getResources(), iconResId, ctx.getTheme()
        );
        Drawable icon = enabled ? originalIcon : convertDrawableToGrayScale(originalIcon);
        button.setImageDrawable(icon);
    }

    /**
     * Mutates and applies a filter that converts the given drawable to a Gray image.
     *
     * @return a mutated version of the given drawable with a color filter
     *         applied.
     */
    public static Drawable convertDrawableToGrayScale(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Drawable res = drawable.mutate();
        res.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        return res;
    }

    /**
     * Scrolls down the scrollview to bottom.
     * Useful after keyboard comes up so that input field at the bottom are visible - for this case
     * use with onClick and onFocusChange of EditText
     * @param scrollView ScrollView of the fragment
     */
    public static void scrollUpLayout(ScrollView scrollView, int delayMillis){
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                View lastChild = scrollView.getChildAt(scrollView.getChildCount() - 1);
                int bottom = lastChild.getBottom() + scrollView.getPaddingBottom();
                int sy = scrollView.getScrollY();
                int sh = scrollView.getHeight();
                int delta = bottom - (sy + sh);
                scrollView.smoothScrollBy(0, delta);
            }
        }, delayMillis);
    }
}


