package com.zhhl.entry_exit.tour;

import java.util.List;

public class SearchData {

    /**
     * code : 1
     * data : [{"msg":"","createtime":"2017/11/15 8:33:42","code":"","sjhm":"7823729832","xb":"南","list":[],"mz":"汉","dylx":"不俗的活动","zzzp":"就阿嫂咖啡色地方阿嫂地方","hkszd":"散发送","xjd":"阿苏大发送","sfzh":"","qzsj":"2017/11/15 8:33:42","name":"anme","ywbh":"23234","sbid":""}]
     */

    private String code;
    private List<DataBean> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * msg :
         * createtime : 2017/11/15 8:33:42
         * code :
         * sjhm : 7823729832
         * xb : 南
         * list : []
         * mz : 汉
         * dylx : 不俗的活动
         * zzzp : 就阿嫂咖啡色地方阿嫂地方
         * hkszd : 散发送
         * xjd : 阿苏大发送
         * sfzh :
         * qzsj : 2017/11/15 8:33:42
         * name : anme
         * ywbh : 23234
         * sbid :
         */

        private String msg;
        private String createtime;
        private String code;
        private String sjhm;
        private String xb;
        private String mz;
        private String dylx;
        private String zzzp;
        private String hkszd;
        private String xjd;
        private String sfzh;
        private String qzsj;
        private String name;
        private String ywbh;
        private String sbid;
        private List<?> list;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getCreatetime() {
            return createtime;
        }

        public void setCreatetime(String createtime) {
            this.createtime = createtime;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getSjhm() {
            return sjhm;
        }

        public void setSjhm(String sjhm) {
            this.sjhm = sjhm;
        }

        public String getXb() {
            return xb;
        }

        public void setXb(String xb) {
            this.xb = xb;
        }

        public String getMz() {
            return mz;
        }

        public void setMz(String mz) {
            this.mz = mz;
        }

        public String getDylx() {
            return dylx;
        }

        public void setDylx(String dylx) {
            this.dylx = dylx;
        }

        public String getZzzp() {
            return zzzp;
        }

        public void setZzzp(String zzzp) {
            this.zzzp = zzzp;
        }

        public String getHkszd() {
            return hkszd;
        }

        public void setHkszd(String hkszd) {
            this.hkszd = hkszd;
        }

        public String getXjd() {
            return xjd;
        }

        public void setXjd(String xjd) {
            this.xjd = xjd;
        }

        public String getSfzh() {
            return sfzh;
        }

        public void setSfzh(String sfzh) {
            this.sfzh = sfzh;
        }

        public String getQzsj() {
            return qzsj;
        }

        public void setQzsj(String qzsj) {
            this.qzsj = qzsj;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getYwbh() {
            return ywbh;
        }

        public void setYwbh(String ywbh) {
            this.ywbh = ywbh;
        }

        public String getSbid() {
            return sbid;
        }

        public void setSbid(String sbid) {
            this.sbid = sbid;
        }

        public List<?> getList() {
            return list;
        }

        public void setList(List<?> list) {
            this.list = list;
        }
    }
}
