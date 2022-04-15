package com.cxj.dao;


import com.cxj.domain.User;
import com.cxj.utils.JDBCUtils;

import java.util.Date;

public class UserDao {

    //添加一条记录
    public static void add(User user) {
        String sql = "insert into users values(null,?,?,?,?,?,?,?,?,?,?)";
        Object params[]={
                user.getUsername(),
                user.getPassword(),
                user.getSex(),
                user.getEmail(),
                user.getPhoto(),
                user.getScore(),
                user.getQuestionn(),
                user.getAnswer(),
                user.getLastlogintime(),
                user.isStatus(),
        };
       int id = ((Number) JDBCUtils.insert(sql,params)).intValue();//返回主键
        user.setVipid(id);
    }
    //查找用户名为username的记录
    public User findByUsername(String username){
        String sql = "select * from users where username = ?";
        User user = JDBCUtils.selectToBean(User.class,sql,username);
        return user;
    }
    //根据vipid查询用户是否激活
    public boolean findStatus(int vipid){
        String sql = "select status from users where vipid =?";
        return JDBCUtils.selectScalar(sql,vipid);
    }
    //更新用户状态为true
    public void updateStatus(int vipid){
        String sql = "update users set status=true where vipid=?";
        JDBCUtils.update(sql,vipid);
    }
     //更新最近一次登陆时间
    public void updateLastLoginTime(Date lastlogintime, int vipid) {
        String sql = "update users set lastlogintime=? where vipid=?";
        Object params[] ={
                new java.sql.Timestamp(lastlogintime.getTime()),
                vipid,
        };
        JDBCUtils.update(sql,params);
    }
}
