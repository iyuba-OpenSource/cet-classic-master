package com.iyuba.core.me.pay;

import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.iyuba.core.util.MD5;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

public class OrderGenerateRequest extends BaseJsonObjectRequest {
    private static final String TAG = OrderGenerateRequest.class.getSimpleName();
    private static final String newApi = "http://vip.iyuba.cn/alipay.jsp?";
    //    public String productId;
    public String result;
    public String message;
    public String alipayTradeStr;

    public OrderGenerateRequest(String productId, String seller_email, String out_trade_no, String subject,
                                String total_fee, String body, String defaultbank, String app_id, String userId, String amount,
                                ErrorListener el, final RequestCallBack rc) {
//        this.productId = productId;
        super(Request.Method.POST, newApi + "WIDseller_email=" + seller_email + "&WIDout_trade_no="
                + out_trade_no + "&WIDsubject=" + subject + "&WIDtotal_fee=" + total_fee
                + "&WIDbody=" + body + "&WIDdefaultbank=" + defaultbank + "&WIDshow_url=" + ""
                + "&app_id=" + app_id + "&userId=" + userId + "&amount=" + amount + "&product_id=" + productId
                + "&code=" + generateCode(userId), el);
        setResListener(new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObjectRoot) {
                try {
                    result = jsonObjectRoot.getString("result");
                    message = jsonObjectRoot.getString("message");
                    if (isRequestSuccessful()) {
                        alipayTradeStr = jsonObjectRoot.getString("alipayTradeStr");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                rc.requestResult(OrderGenerateRequest.this);
            }
        });

    }

    private static String generateCode(String userId) {
        String code = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        code = MD5.getMD5ofStr(userId + "iyuba" + df.format(System.currentTimeMillis()));
        return code;
    }

    @Override
    public boolean isRequestSuccessful() {
        return "200".equals(result);
    }

}
