package com.iyuba.CET4bible.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.iyuba.CET4bible.R;

/**
 * Created by YM on 2021/1/18 15:12
 */
public class PwdInputDialog extends Dialog{

		private Context context;
		private TextView mTvTitle;
		private TextView mTvContent;
		private EditText mEtInputPassword;
		private TextView mTvCancelBtn;
		private TextView mTvConfirmBtn;
		private String titleStr;
		private String contentStr;
		private String cancelStr;
		private String confirmStr;
		private boolean showInputPwd;

		public PwdInputDialog(@NonNull Context context) {
				super(context, R.style.AppDialog);
				this.context = context;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				getWindow().requestFeature(Window.FEATURE_NO_TITLE);
				setContentView(R.layout.dialog_layout);
				setStatusBar(getWindow());
				initView();
				initData();
		}

		private void setStatusBar(Window window) {
				if (window != null) {
						window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
						window.getDecorView().setPadding(0, 0, 0, 0);
						//window.getDecorView().setBackgroundColor(Color.WHITE);
						WindowManager.LayoutParams layoutParams = window.getAttributes();
						layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
						layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
								// 延伸显示区域到刘海
								WindowManager.LayoutParams lp = window.getAttributes();
								lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
								window.setAttributes(lp);
								// 设置页面全屏显示
								final View decorView = window.getDecorView();
								decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
						}
						window.setAttributes(layoutParams);
				}
		}

		private void initData() {
				if (!TextUtils.isEmpty(titleStr)){
						mTvTitle.setText(titleStr);
				}
				if (!TextUtils.isEmpty(contentStr)) {
						mTvContent.setText(contentStr);
				}
				if (!TextUtils.isEmpty(cancelStr)) {
						mTvCancelBtn.setText(cancelStr);
				}
				if (!TextUtils.isEmpty(confirmStr)) {
						mTvConfirmBtn.setText(confirmStr);
				}
				mEtInputPassword.setVisibility(showInputPwd ? View.VISIBLE : View.GONE);
				mTvContent.setVisibility(showInputPwd ? View.GONE : View.VISIBLE);
				mTvConfirmBtn.setTextColor(ContextCompat.getColor(context, showInputPwd ? R.color.orangered :R.color.dodgerblue));
		}

		public void setTitleStr(String titleStr) {
				this.titleStr = titleStr;
		}

		public void setContentStr(String contentStr) {
				this.contentStr = contentStr;
		}

		public void setCancelStr(String cancelStr) {
				this.cancelStr = cancelStr;
		}

		public void setConfirmStr(String confirmStr) {
				this.confirmStr = confirmStr;
		}

		public void setShowInputPwd(boolean showInputPwd) {
				this.showInputPwd = showInputPwd;
		}

		private void initView() {
				mTvTitle = findViewById(R.id.tv_title);
				mTvContent = findViewById(R.id.tv_content);
				mEtInputPassword = findViewById(R.id.et_input_password);
				mTvCancelBtn = findViewById(R.id.tv_cancel);
				mTvConfirmBtn = findViewById(R.id.tv_confirm);
				mTvCancelBtn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
								if (mListener != null) {
										mListener.onCancelClick();
								}
						}
				});
				mTvConfirmBtn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
								if (mListener != null) {
										if (mEtInputPassword.getVisibility() == View.VISIBLE) {
												mListener.onCheckInputPassWord(mEtInputPassword.getText().toString().trim());
										} else {
												mListener.onConfirmClick();
										}
								}
						}
				});
		}

		private OnBtnClickListener mListener;

		public void setListener(OnBtnClickListener listener) {
				mListener = listener;
		}

		public interface OnBtnClickListener {
				void onCancelClick();
				void onConfirmClick();
				void onCheckInputPassWord(String password);
		}

		/**
		 * 判定当前是否需要隐藏
		 */
		protected boolean isShouldHideKeyBord(View v, MotionEvent ev) {
				if ((v instanceof EditText)) {
						int[] l = {0, 0};
						v.getLocationInWindow(l);
						int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
						return !(ev.getX() > left && ev.getX() < right && ev.getY() > top && ev.getY() < bottom);
				}
				return false;
		}

		/**
		 * 隐藏软键盘
		 */
		private void hideSoftInput(IBinder token) {
				if (token != null) {
						InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
						manager.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
				}
		}

		@Override
		public boolean dispatchTouchEvent(MotionEvent ev) {
				if (ev.getAction() == MotionEvent.ACTION_DOWN ) {
						View view = getCurrentFocus();
						if (isShouldHideKeyBord(view, ev)) {
								hideSoftInput(view.getWindowToken());
						}
				}
				return super.dispatchTouchEvent(ev);
		}
}
