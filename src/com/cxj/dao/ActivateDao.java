package com.cxj.dao;


import com.cxj.domain.Activate;
import com.cxj.utils.JDBCUtils;

/**
 * @Author : cxj
 * @create 2021/12/5 12:04
 */
public class ActivateDao {
    /**
     * 增加一条记录
     * @param activate 一个对象
     */
    public void add(Activate activate){
        String sql = "insert into activate value(null,?,?,?)";
        Object params[]={
                activate.getCode(),
                new java.sql.Timestamp(activate.getExpiredate().getTime()),
                activate.getVipid(),
        };
        JDBCUtils.insert(sql,params);
    }

    /**
     * 根据code来读取记录
     * @param code 激活码
     * @return 返回一条记录
     */
    public Activate findByCode(String code){
        String sql = "select * from activate where code=?";
        return JDBCUtils.selectToBean(Activate.class,sql,code);
    }
}
