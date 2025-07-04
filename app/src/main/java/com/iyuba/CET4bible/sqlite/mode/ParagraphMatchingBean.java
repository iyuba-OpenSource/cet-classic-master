package com.iyuba.CET4bible.sqlite.mode;

import java.io.Serializable;

/**
 * ParagraphMatchingBean
 *
 * @author wayne
 * @date 2017/12/22
 */
public class ParagraphMatchingBean implements Serializable {
    public String year;
    public int index;
    public String title;
    public String original;
    public String translation;
    public String question;
    public String answer;
    public String explanation;


    @Override
    public String toString() {
        return "ParagraphMatchingBean{" +
                "year='" + year + '\'' +
                ", index=" + index +
                ", title='" + title + '\'' +
                ", original='" + original + '\'' +
                ", translation='" + translation + '\'' +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", explanation='" + explanation + '\'' +
                '}';
    }
}
