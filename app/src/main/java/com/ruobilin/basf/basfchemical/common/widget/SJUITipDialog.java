package com.ruobilin.basf.basfchemical.common.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ruobilin.basf.basfchemical.R;
import com.ruobilin.basf.basfchemical.utils.SizeUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public class SJUITipDialog extends Dialog {

    public SJUITipDialog(Context context) {
        this(context, R.style.SJ_TipDialog);
    }

    public SJUITipDialog(Context context, int themeResId) {
        super(context, themeResId);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDialogWidth();
    }

    private void initDialogWidth() {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams wmLp = window.getAttributes();
            wmLp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            window.setAttributes(wmLp);
        }
    }

    /**
     * <p>
     * 提供了一个图标和一行文字的样式, 其中图标有几种类型可选。见 {@link IconType}
     * </p>
     *
     * @see CustomBuilder
     */
    public static class Builder {

        /**
         * 显示 Loading 图标
         */
        public static final int ICON_TYPE_LOADING = 1;


        @IntDef({ICON_TYPE_LOADING})
        @Retention(RetentionPolicy.SOURCE)
        public @interface IconType {
        }

        private
        @IconType
        int mCurrentIconType = ICON_TYPE_LOADING;

        private Context mContext;

        private CharSequence mTipWord;

        public Builder(Context context) {
            mContext = context;
        }

        /**
         * 设置 icon 显示的内容
         *
         * @see IconType
         */
        public Builder setIconType(@IconType int iconType) {
            mCurrentIconType = iconType;
            return this;
        }

        /**
         * 设置显示的文案
         */
        public Builder setTipWord(CharSequence tipWord) {
            mTipWord = tipWord;
            return this;
        }

        /**
         * 创建 Dialog
         *
         * @return 创建的 Dialog
         */
        public SJUITipDialog create() {
            SJUITipDialog dialog = new SJUITipDialog(mContext);
            dialog.setContentView(R.layout.sj_tip_dialog_layout);
            ViewGroup contentWrap = (ViewGroup) dialog.findViewById(R.id.contentWrap);

            if (mCurrentIconType == ICON_TYPE_LOADING) {
                SJUILoadingView loadingView = new SJUILoadingView(mContext);
                loadingView.setColor(Color.WHITE);
                loadingView.setSize((int) SizeUtils.dp2px(52));
                LinearLayout.LayoutParams loadingViewLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                loadingView.setLayoutParams(loadingViewLP);
                contentWrap.addView(loadingView);

            }
            if (mTipWord != null && mTipWord.length() > 0) {
                TextView tipView = new TextView(mContext);
                LinearLayout.LayoutParams tipViewLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                tipView.setLayoutParams(tipViewLP);

                tipView.setEllipsize(TextUtils.TruncateAt.END);
                tipView.setGravity(Gravity.CENTER);
                tipView.setMaxLines(2);
                tipView.setTextColor(ContextCompat.getColor(mContext, R.color.fontWhite));
                tipView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                tipView.setText(mTipWord);

                contentWrap.addView(tipView);
            }
            return dialog;
        }

    }

    /**
     * 传入自定义的布局并使用这个布局生成 TipDialog
     */
    public static class CustomBuilder {
        private Context mContext;
        private int mContentLayoutId;

        public CustomBuilder(Context context) {
            mContext = context;
        }

        public CustomBuilder setContent(@LayoutRes int layoutId) {
            mContentLayoutId = layoutId;
            return this;
        }

        /**
         * 创建 Dialog
         *
         * @return 创建的 Dialog
         */
        public SJUITipDialog create() {
            SJUITipDialog dialog = new SJUITipDialog(mContext);
            dialog.setContentView(R.layout.sj_tip_dialog_layout);
            ViewGroup contentWrap = (ViewGroup) dialog.findViewById(R.id.contentWrap);
            LayoutInflater.from(mContext).inflate(mContentLayoutId, contentWrap, true);
            return dialog;
        }
    }
}
