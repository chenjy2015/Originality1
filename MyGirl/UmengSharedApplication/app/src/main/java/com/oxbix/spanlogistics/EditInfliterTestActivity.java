package com.oxbix.spanlogistics;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.umeng.socialize.utils.Log;

public class EditInfliterTestActivity extends Activity {

    private Button mSet;
    private EditText mEditText, mInput;
    private int maxLength;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_infliter_test);
        mSet = (Button) this.findViewById(R.id.id_set);
        mEditText = (EditText) this.findViewById(R.id.id_edit);
        mInput = (EditText) this.findViewById(R.id.id_input);
        init();
        initEvent();
    }

    public void init() {
        maxLength = 10;
    }

    public void initEvent() {
        mSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = mInput.getText().toString().trim();
                if (text != null && !text.equals("")) {
                    try {
                        maxLength = Integer.parseInt(text) == 0 ? 10 : Integer.parseInt(text);
                        mSet.setText("设置EditText输入长度为: " + maxLength + "位");
                        mEditText.setFilters(new InputFilter[]{new AdnNameLengthFilter(maxLength)});
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static int LENGTH_ZNAME = 15; //是中文模式下允许输入的最大字符数。
    public static int LENGTH_ENAME = 25; //是纯英文模式下允许输入的最大字符数。


    public class AdnNameLengthFilter implements InputFilter {
        private int nMax;

        public AdnNameLengthFilter(int maxLength) {
            this.nMax = maxLength;
        }

        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Log.w("Android_12", "source(" + start + "," + end + ")=" + source + ",dest(" + dstart + "," + dend + ")=" + dest);


            int keep = nMax - (dest.length() - (dend - dstart));

            if (keep <= 0) {
                return "";
            } else if (keep >= end - start) {
                return null; // keep original
            } else {
                return source.subSequence(start, start + keep);
            }

        }
    }

}
