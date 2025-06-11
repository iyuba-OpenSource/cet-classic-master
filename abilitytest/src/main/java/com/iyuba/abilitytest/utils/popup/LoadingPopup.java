package com.iyuba.abilitytest.utils.popup;

import android.content.Context;
import android.view.View;

import com.iyuba.abilitytest.R;

import razerdp.basepopup.BasePopupWindow;

public class LoadingPopup extends BasePopupWindow {

    public LoadingPopup(Context context) {
        super(context);
        View view = createPopupById(R.layout.popup_loading);
        setContentView(view);


    }
}
