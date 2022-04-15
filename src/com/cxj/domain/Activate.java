package com.cxj.domain;

import java.util.Date;

//激活码
public class Activate {
    private int activateid;//主键
    private String code;//激活码
    private Date expiredate;//过期时间
    private int vipid;//用户vipid

    @Override
    public String toString() {
        return "Activate{" +
                "activateid=" + activateid +
                ", code='" + code + '\'' +
                ", expiredate=" + expiredate +
                ", vipid=" + vipid +
                '}';
    }

    public int getActivateid() {
        return activateid;
    }

    public void setActivateid(int activateid) {
        this.activateid = activateid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getExpiredate() {
        return expiredate;
    }

    public void setExpiredate(Date expiredate) {
        this.expiredate = expiredate;
    }

    public int getVipid() {
        return vipid;
    }

    public void setVipid(int vipid) {
        this.vipid = vipid;
    }
}
