package com.zhhl.entry_exit.tour;

public class FaceResult2 {
    /**
     * msg : 请求成功
     * data : {"sim":50,"errorcode":-1004,"errormsg":"arg error, may be type not right","lib_time":2}
     * status : 0
     */

    private String msg;
    private DataBean data;
    private int status;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static class DataBean {
        /**
         * sim : 50
         * errorcode : -1004
         * errormsg : arg error, may be type not right
         * lib_time : 2
         */

        private int sim;
        private int errorcode;
        private String errormsg;
        private int lib_time;

        public int getSim() {
            return sim;
        }

        public void setSim(int sim) {
            this.sim = sim;
        }

        public int getErrorcode() {
            return errorcode;
        }

        public void setErrorcode(int errorcode) {
            this.errorcode = errorcode;
        }

        public String getErrormsg() {
            return errormsg;
        }

        public void setErrormsg(String errormsg) {
            this.errormsg = errormsg;
        }

        public int getLib_time() {
            return lib_time;
        }

        public void setLib_time(int lib_time) {
            this.lib_time = lib_time;
        }
    }
}
