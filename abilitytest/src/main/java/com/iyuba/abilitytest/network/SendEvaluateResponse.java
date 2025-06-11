package com.iyuba.abilitytest.network;


import java.util.List;

/**
 * @author yq QQ:1032006226
 * @name talkshow
 * @class name：com.iyuba.talkshow.data.model.result
 * @class describe
 * @time 2018/12/19 19:54
 * @change
 * @chang time
 * @class describe
 */
//@Root(name = "response", strict = false)
public class SendEvaluateResponse {

    /**
     * data : {"sentence":"take care of everyone when they got sick.","total_score":"0.37157287157287155","word_count":8,"URL":"http://ai."+WebConstant.WEB_SUFFIX+"data/projects/speech/wav/type/50166753094991.wav","words":[{"content":"TAKE","index":0,"score":1.9696969696969693},{"content":"CARE","index":1,"score":0},{"content":"OF","index":2,"score":0},{"content":"EVERYONE","index":3,"score":1.002886002886003},{"content":"WHEN","index":4,"score":0},{"content":"THEY","index":5,"score":0},{"content":"GOT","index":6,"score":0},{"content":"SICK","index":7,"score":0}]}
     */

    private String result;
    private String message;

    private DataBean data;
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }



    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private String URL;
        /**
         * sentence : take care of everyone when they got sick.
         * total_score : 0.37157287157287155
         * word_count : 8
         * URL : http://ai."+WebConstant.WEB_SUFFIX+"data/projects/speech/wav/type/50166753094991.wav
         * words : [{"content":"TAKE","index":0,"score":1.9696969696969693},{"content":"CARE","index":1,"score":0},{"content":"OF","index":2,"score":0},{"content":"EVERYONE","index":3,"score":1.002886002886003},{"content":"WHEN","index":4,"score":0},{"content":"THEY","index":5,"score":0},{"content":"GOT","index":6,"score":0},{"content":"SICK","index":7,"score":0}]
         */

        private String sentence;
        private String filepath;
        private Double score;
        private String total_score;
        private List<WordsBean> words;

        public String getSentence() {
            return sentence;
        }

        public void setSentence(String sentence) {
            this.sentence = sentence;
        }

        public String getTotal_score() {
            return total_score;
        }

        public void setTotal_score(String total_score) {
            this.total_score = total_score;
        }

        public String getURL() {
            return URL;
        }

        public void setURL(String URL) {
            this.URL = URL;
        }

        public List<WordsBean> getWords() {
            return words;
        }

        public void setWords(List<WordsBean> words) {
            this.words = words;
        }

        public String getFilepath() {
            return filepath;
        }

        public void setFilepath(String filepath) {
            this.filepath = filepath;
        }

        public Double getScores() {
            return score;
        }

        public void setScores(Double scores) {
            this.score = scores;
        }

        public static class WordsBean {
            /**
             * content : TAKE
             * index : 0
             * score : 1.9696969696969693
             */

            private String content;
            private String delete;
            private String insert;
            private String pron;
            private String pron2;
            private String substitute_orgi;
            private String substitute_user;
            private String user_pron;
            private String user_pron2;
            private int index;
            private double score;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public int getIndex() {
                return index;
            }

            public void setIndex(int index) {
                this.index = index;
            }

            public double getScore() {
                return score;
            }

            public void setScore(double score) {
                this.score = score;
            }

            public String getDelete() {
                return delete;
            }

            public void setDelete(String delete) {
                this.delete = delete;
            }

            public String getInsert() {
                return insert;
            }

            public void setInsert(String insert) {
                this.insert = insert;
            }

            public String getPron() {
                return pron;
            }

            public void setPron(String pron) {
                this.pron = pron;
            }

            public String getPron2() {
                return pron2;
            }

            public void setPron2(String pron2) {
                this.pron2 = pron2;
            }

            public String getSubstitute_orgi() {
                return substitute_orgi;
            }

            public void setSubstitute_orgi(String substitute_orgi) {
                this.substitute_orgi = substitute_orgi;
            }

            public String getSubstitute_user() {
                return substitute_user;
            }

            public void setSubstitute_user(String substitute_user) {
                this.substitute_user = substitute_user;
            }

            public String getUser_pron() {
                return user_pron;
            }

            public void setUser_pron(String user_pron) {
                this.user_pron = user_pron;
            }

            public String getUser_pron2() {
                return user_pron2;
            }

            public void setUser_pron2(String user_pron2) {
                this.user_pron2 = user_pron2;
            }
        }
    }
}
