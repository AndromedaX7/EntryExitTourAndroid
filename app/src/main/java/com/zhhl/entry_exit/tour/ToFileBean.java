package com.zhhl.entry_exit.tour;

public class ToFileBean {
    /**
     * success : 0
     * state : CRJ1202
     * message : 发证现场人像信息不能为空
     */

    private int success;
    private String state;
    private String message;

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
