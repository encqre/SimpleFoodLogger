package lt.jasinevicius.simplefoodlogger.reusable;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatEditText;

import lt.jasinevicius.simplefoodlogger.R;

public class EditTextWithSuffix extends AppCompatEditText {
    TextPaint textPaint = new TextPaint();
    private String suffix = "";
    private float suffixPadding;

    public EditTextWithSuffix(Context context) {
        super(context);
    }

    public EditTextWithSuffix(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttributes(context, attrs, 0);
    }

    public EditTextWithSuffix(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttributes(context, attrs, defStyleAttr);
    }

    //this version works with hints as well
    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
        int suffixXPosition = (int) textPaint.measureText(getText().toString()) + getPaddingLeft();
        if (getText().toString().isEmpty() && getHint() != null) {
            suffixXPosition = (int) textPaint.measureText(getHint().toString()) + getPaddingLeft();
            textPaint.setColor(getCurrentHintTextColor());
        } else {
            textPaint.setColor(getCurrentTextColor());
        }
        c.drawText(suffix, Math.max(suffixXPosition, suffixPadding), getBaseline(), textPaint);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        textPaint.setColor(getCurrentTextColor());
        textPaint.setTextSize(getTextSize());
        textPaint.setTextAlign(Paint.Align.LEFT);
    }

    private void getAttributes(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EditTextWithSuffix, defStyleAttr, 0);
        if(a != null) {
            suffix = a.getString(R.styleable.EditTextWithSuffix_suffix);
            if(suffix == null) {
                suffix = "";
            }
            suffixPadding = a.getDimension(R.styleable.EditTextWithSuffix_suffixPadding, 0);
        }
        a.recycle();
    }
}