package com.iyuba.cet6.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.iyuba.CET4bible.MyApplication;
import com.iyuba.core.eventbus.WXPayEventbus;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = WXAPIFactory.createWXAPI(this, "wx189fde5f93808eba");
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

        Log.d("12313", "BaseReq");
    }

    @Override
    public void onResp(BaseResp baseResp) {

        if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {

            if (baseResp.errCode == 0) {
                Toast.makeText(MyApplication.getInstance(), "支付成功", Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(new WXPayEventbus(baseResp.errCode));
            } else {
                Toast.makeText(MyApplication.getInstance(), "支付失败", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}