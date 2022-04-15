package com.cxj.domain;

import java.util.Date;

//用户实体类
public class User {
    private int vipid;//id
    private String username;//用户名
    private String password;//密码
    private String sex;//性别
    private String email;//邮箱
    private String photo;//头像地址
    private long score;//积分等级
    private String questionn;//密码提示问题
    private String answer;//答案
    private Date lastlogintime;//最近一次登陆时间

    private boolean status;//当前是否激活

    @Override
    public String toString() {
        return "User{" +
                "vipid=" + vipid +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", sex='" + sex + '\'' +
                ", email='" + email + '\'' +
                ", photo='" + photo + '\'' +
                ", score=" + score +
                ", questionn='" + questionn + '\'' +
                ", answer='" + answer + '\'' +
                ", lastlogintime=" + lastlogintime +
                ", status=" + status +
                '}';
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getVipid() {
        return vipid;
    }

    public void setVipid(int vipid) {
        this.vipid = vipid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public String getQuestionn() {
        return questionn;
    }

    public void setQuestionn(String questionn) {
        this.questionn = questionn;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Date getLastlogintime() {
        return lastlogintime;
    }

    public void setLastlogintime(Date lastlogintime) {
        this.lastlogintime = lastlogintime;
    }
}
