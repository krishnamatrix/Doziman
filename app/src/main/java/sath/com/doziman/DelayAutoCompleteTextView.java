package sath.com.doziman;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

/**
 * Created by Krishna on 12/20/2015.
 */
public class DelayAutoCompleteTextView extends AutoCompleteTextView {

    // initialization
    int threshold;
    int delay = 0;
    Handler handler = new Handler();
    Runnable run;

    // constructor
    public DelayAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void performFiltering(final CharSequence text, final int keyCode) {
        // get threshold
        threshold = this.getThreshold();

        // perform filter on null to hide dropdown
        doFiltering(null, keyCode);

        // stop execution of previous handler
        handler.removeCallbacks(run);

        // creation of new runnable and prevent filtering of texts which length
        // does not meet threshold
        run = new Runnable() {
            public void run() {
            if (text.length() > threshold) {
                doFiltering(text, keyCode);
            }
            }
        };
        // restart handler
        handler.postDelayed(run, delay);
    }
    // starts the actual filtering
    private void doFiltering(CharSequence text, int keyCode) {
        super.performFiltering(text, keyCode);
    }
}
