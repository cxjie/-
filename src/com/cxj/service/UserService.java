package com.cxj.service;


import com.cxj.dao.ActivateDao;
import com.cxj.dao.UserDao;
import com.cxj.domain.Activate;
import com.cxj.domain.User;
import com.cxj.exception.UserException;

import java.util.Date;

public class UserService {

    private UserDao userDao = new UserDao();
    private ActivateDao activateDao = new ActivateDao();

    public void regist(User user){
        //1.调用dao层add方法进行注册
        UserDao.add(user);
    }

    public User login(String username,String password) throws UserException {
        /*
           1.判断用户是否存在，调用dao层的findByUsername方法
           2.存在，findByUsername返回user对象，不存在，抛异常
           3.user的password和传递过来的参数password进行比较，密码是否正确，不正确抛异常
         */
        User user = userDao.findByUsername(username);
        if(user==null)
            throw new UserException("用户名不存在！") ;
        if(!(user.getPassword().equals(password))){
            throw new UserException("密码错误！") ;
        }
        if(!user.isStatus()){
            throw new UserException("请先激活账户再登录！") ;
        }
        return user;
    }

    //将激活码写入数据库
    public void addActivate(Activate activate){
        activateDao.add(activate);

    }
    public void activate(String code) throws UserException {
        /*
           1.查看激活码是否正确，不正确的话，抛出异常，正确的话，是否过期，获取vipid
           2.根据vipid查询用户表是否激活，如果已经激活过，抛出异常，不能重复激活
           3.没有激活过，激活vipid
         */
        Activate activate = activateDao.findByCode(code);
        if(activate ==null){
            throw new UserException("激活码错误！");
        }
        if(activate.getExpiredate().getTime()<(new Date()).getTime()){
            throw new UserException("激活码已经过期！");
        }
        int vipid = activate.getVipid();
        if(userDao.findStatus(vipid)){
            throw new UserException("不能重复激活！");
        }
        //激活
        userDao.updateStatus(vipid);
    }
//判断用户名是否被占用
    public boolean validateUsername(String username){
        /*
           1.调用findByUsername方法
           2.根据返回user,判断user对象是否为空，没有被占用，返回true
           3.不为空，返回false
         */
        User user = userDao.findByUsername(username );
        if(user==null){
            return true;
        }
        return false;
    }
    //更新最近一次登陆时间
    public void updateLastLoginTime(User user){
        user.setLastlogintime(new Date());
        //调用dao层来更新Lastlogintime
        userDao.updateLastLoginTime(user.getLastlogintime(),user.getVipid() );
    }
}
