package com.iyuba.core.protocol.message;

import com.iyuba.core.protocol.BaseJSONResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class ResponsePhoneNumRegister extends BaseJSONResponse {
    public String resultCode;
    public String message;
    public boolean isRegSuccess;

    public String uid;
    public String imgSrc;

    public String username;

    @Override
    protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {

        try {
            JSONObject jsonObjectRoot = new JSONObject(bodyElement);
            resultCode = jsonObjectRoot.getString("result");
            message = jsonObjectRoot.getString("message");
            isRegSuccess = resultCode.equals("111");

            uid = jsonObjectRoot.getString("uid");
            imgSrc = jsonObjectRoot.getString("imgSrc");
            username = jsonObjectRoot.getString("username");

        } catch (JSONException e1) {

            e1.printStackTrace();
        }
        return true;
    }

}
