package com.iyuba.core.protocol.base;

import com.iyuba.core.network.xml.Utility;
import com.iyuba.core.network.xml.kXMLElement;
import com.iyuba.core.protocol.BaseXMLResponse;

public class StudyRecordResponse extends BaseXMLResponse {
    public String result;

    @Override
    protected boolean extractBody(kXMLElement headerEleemnt,
                                  kXMLElement bodyElement) {

        result = Utility.getSubTagContent(bodyElement, "result");
        return true;
    }

}
