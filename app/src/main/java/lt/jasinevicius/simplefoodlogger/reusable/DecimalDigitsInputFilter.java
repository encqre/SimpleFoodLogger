package lt.jasinevicius.simplefoodlogger.reusable;

import android.text.InputFilter;
import android.text.Spanned;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DecimalDigitsInputFilter implements InputFilter {

    Pattern mPattern;

    public DecimalDigitsInputFilter(int digitsBeforeZero,int digitsAfterZero) {
        mPattern=Pattern.compile("^([0-9]{0,"+digitsBeforeZero+"}|[0-9]{0,"+digitsBeforeZero+"}\\.[0-9]{0,"+digitsAfterZero+"})$");
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//        android.util.Log.d("FILTRAS", "source:" + source + "\nstart:"+start+ "\nend:"+end+ "\ndest:"+dest+ "\ndstart:"+dstart+ "\ndend:"+dend);

        String keepStart = dest.toString().substring(0,dstart);
        String keepEnd = dest.toString().substring(dend, dest.length());
        String replacedString = dest.toString().substring(dstart, dend);
        String replacementString = source.toString().substring(start,end);
        String predicted = keepStart + replacementString + keepEnd;
//        android.util.Log.d("FILTRAS", "prediction:" + predicted);

        Matcher matcher=mPattern.matcher(predicted);
        if (!matcher.matches()) {
            if (source.length() > 0 && replacedString.length() > 0) {
                return replacedString;
            }
            return "";
        }
        return null;
    }

}
