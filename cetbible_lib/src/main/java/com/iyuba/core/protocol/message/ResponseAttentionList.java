/**
 *
 */
package com.iyuba.core.protocol.message;

import com.iyuba.core.me.sqlite.mode.Attention231219;
import com.iyuba.core.protocol.BaseJSONResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author
 */
public class ResponseAttentionList extends BaseJSONResponse {

    public Attention231219 attention231219;

    @Override
    protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {

        attention231219 = new Attention231219();
        try {
            JSONObject jsonObjectRootRoot = new JSONObject(bodyElement);
            String result = jsonObjectRootRoot.getString("result");
            String message = jsonObjectRootRoot.getString("message");

            attention231219.setResult(result);
            attention231219.setMessage(message);
            if (result.equals("550")) {
                int num = jsonObjectRootRoot.getInt("num");
                attention231219.setNum(num);
                JSONArray data = jsonObjectRootRoot.getJSONArray("data");
                if (data != null && data.length() != 0) {
                    int size = data.length();

                    List<Attention231219.DataDTO> dataDTOList = new ArrayList<>();
                    for (int i = 0; i < size; i++) {

                        Attention231219.DataDTO fans = new Attention231219.DataDTO();
                        JSONObject jsonObject = ((JSONObject) data.opt(i));
                        String fusername = jsonObject.getString("fusername");
                        String doing = jsonObject.getString("doing");
                        String mutual = jsonObject.getString("mutual");
                        String dateline = jsonObject.getString("dateline");
                        String followuid = jsonObject.getString("followuid");
                        String vip = jsonObject.getString("vip");
                        String status = jsonObject.getString("status");

                        fans.setFusername(fusername);
                        fans.setDoing(doing);
                        fans.setMutual(mutual);
                        fans.setDateline(dateline);
                        fans.setFollowuid(followuid);
                        fans.setVip(vip);
                        fans.setStatus(status);

                        dataDTOList.add(fans);
                    }
                    attention231219.setData(dataDTOList);
                }
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
        return true;
    }

}
