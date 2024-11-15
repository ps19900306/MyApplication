package com.nwq.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.nwq.baseutils.R;
import com.nwq.baseutils.databinding.ViewItemInputBinding;
import com.nwq.callback.CallBack;


public class InputItemView extends LinearLayoutCompat {
    public InputItemView(@NonNull Context context) {
        super(context);
    }

    private ViewItemInputBinding _bing;

    private CallBack<String> inputEdtListener;

    public void setInputEdtListener(CallBack<String> inputEdt) {
        this.inputEdtListener = inputEdt;
        _bing.inputEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                inputEdtListener.onCallBack(s.toString());
            }
        });
    }

    public InputItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setMyAttribute(context, attrs);
    }

    private void setMyAttribute(Context context, @Nullable AttributeSet attrs) {
        if (attrs == null)
            return;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.InputItemView);

        int titleStr = typedArray.getResourceId(R.styleable.InputItemView_title_str, R.string.app_name);

        _bing = ViewItemInputBinding.inflate(LayoutInflater.from(getContext()), this, true);
        _bing.titleTv.setText(titleStr);

        typedArray.recycle();

    }

    public void setText(String resId) {
        _bing.inputEdit.setText(resId);
    }

    public String getText() {
        return _bing.inputEdit.getText().toString();
    }
}
