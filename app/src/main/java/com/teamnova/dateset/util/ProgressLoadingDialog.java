package com.teamnova.dateset.util;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import androidx.annotation.NonNull;

import com.teamnova.dateset.R;


public class ProgressLoadingDialog extends Dialog {

    public ProgressLoadingDialog(@NonNull Context context) {
        super(context);
        // 다이어로그 제목을 보이지 않게 지정
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_loading_progress);
    }
}
