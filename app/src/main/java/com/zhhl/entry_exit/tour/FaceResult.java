package com.zhhl.entry_exit.tour;

import android.os.Parcel;
import android.os.Parcelable;

public class FaceResult {


    /**
     * obj : {"status":0,"data":{"token":"ab","errormsg":"arg error, may be type not right","lib_time":8,"sim":50,"errorcode":-1004},"msg":"请求成功"}
     * attributes : null
     * msg : 操作成功
     * success : true
     */

    private ObjBean obj;
    private Object attributes;
    private String msg;
    private boolean success;

    public ObjBean getObj() {
        return obj;
    }

    public void setObj(ObjBean obj) {
        this.obj = obj;
    }

    public Object getAttributes() {
        return attributes;
    }

    public void setAttributes(Object attributes) {
        this.attributes = attributes;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public static class ObjBean {
        /**
         * status : 0
         * data : {"token":"ab","errormsg":"arg error, may be type not right","lib_time":8,"sim":50,"errorcode":-1004}
         * msg : 请求成功
         */

        private int status;
        private DataBean data;
        private String msg;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public static class DataBean implements Parcelable {
            /**
             * token : ab
             * errormsg : arg error, may be type not right
             * lib_time : 8
             * sim : 50
             * errorcode : -1004
             */

            private String token;
            private String errormsg;
            private int lib_time;
            private double sim;
            private int errorcode;

            public String getToken() {
                return token;
            }

            public void setToken(String token) {
                this.token = token;
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

            public double getSim() {
                return sim;
            }

            public void setSim(double sim) {
                this.sim = sim;
            }

            public int getErrorcode() {
                return errorcode;
            }

            public void setErrorcode(int errorcode) {
                this.errorcode = errorcode;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.token);
                dest.writeString(this.errormsg);
                dest.writeInt(this.lib_time);
                dest.writeDouble(this.sim);
                dest.writeInt(this.errorcode);
            }

            public DataBean() {
            }

            protected DataBean(Parcel in) {
                this.token = in.readString();
                this.errormsg = in.readString();
                this.lib_time = in.readInt();
                this.sim = in.readDouble();
                this.errorcode = in.readInt();
            }

            public static final Parcelable.Creator<DataBean> CREATOR = new Parcelable.Creator<DataBean>() {
                @Override
                public DataBean createFromParcel(Parcel source) {
                    return new DataBean(source);
                }

                @Override
                public DataBean[] newArray(int size) {
                    return new DataBean[size];
                }
            };
        }
    }
}