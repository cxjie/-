package com.cxj.dao;


import com.cxj.domain.Product;
import com.cxj.utils.JDBCUtils;

import java.util.List;
import java.util.Map;

public class ProductDao {
    //添加一条记录
    public void add(Product product ){
        String sql ="insert into product values(null,?,?,?,?,?,?,?,?,?)";
        Object params[]={
                product.getName(),
                product.getPrice(),
                product.getMarkprice(),
                product.getQuality(),
                product.getHit(),
                product.getTime(),
                product.getPhoto(),
                product.getContent(),
                product.getCategoryid(),
        };

        Number number = (Number) JDBCUtils.insert(sql,params);
        product.setProductid(number.intValue());
    }

    //获取所有记录
    public int findCount(){
//        String sql = "select count(*) from product";
//        Number number = (Number) JDBCUtils.selectScalar(sql);
//        return number.intValue();
        return findCount(null,null);
    }
    //带查询条件的，获取所有记录
    public int findCount(String skey,String svalue){
        //SELECT COUNT(*) FROM product WHERE NAME LIKE '%1%'
        StringBuilder sql = new StringBuilder("select count(*) from product");

        if(skey!=null&&(skey.trim().length()>0)&&svalue!=null&&(svalue.trim().length()>0)){
            //有查询条件
            sql.append(" where "+skey.toString()+"  like \"%"+svalue+"%\"  ");
        }
        Number number = (Number) JDBCUtils.selectScalar(sql.toString());
        return number.intValue();
    }
    //获取记录列表
    public List<Map<String,Object>> findAll(int startIndex,int size){
//        String sql = "SELECT p.`productid`,p.`content`,p.`hit`,p.`price`,p.`markprice`,p.`name`,p.`photo`,p.`categoryid`,p.`quality`,p.`time`,c.`name` AS cname FROM product AS p,category AS c WHERE p.`categoryid`=c.`categoryid` limit ?,?";
//        return JDBCUtils.select(sql,startIndex,size);
        return findAll(startIndex, size,null,null);
    }
    //获取记录列表,有查询条件
    public List<Map<String,Object>> findAll(int startIndex,int size,String skey,String svalue){
        //SELECT p.`productid`,p.`content`,p.`hit`,p.`price`,p.`markprice`,p.`name`,p.`photo`,p.`categoryid`,p.`quality`,p.`time`,c.`name` AS cname FROM product AS p,category AS c WHERE p.`categoryid`=c.`categoryid` AND p.NAME LIKE '%1%' LIMIT 0,2
        String sql = "SELECT p.`productid`,p.`content`,p.`hit`,p.`price`,p.`markprice`,p.`name`,p.`photo`,p.`categoryid`,p.`quality`,p.`time`,c.`name` AS cname FROM product AS p,category AS c WHERE p.`categoryid`=c.`categoryid`";
        StringBuilder str = new StringBuilder("");
        if(skey!=null&&(skey.trim().length()>0)&&svalue!=null&&(svalue.trim().length()>0)){
            //有查询条件, AND p.NAME LIKE '%1%' LIMIT 0,2
            str.append(" and p."+skey+"  like\"%"+svalue+"%\"   ");
        }
        return JDBCUtils.select(sql+str.toString()+" limit ?,?",startIndex,size);
    }

    //通过id获取product记录
    public Map<String, Object> findById(int productid) {
        String sql= "SELECT p.`productid`,p.`content`,p.`hit`,p.`price`,p.`markprice`,p.`name`,p.`photo`,p.`categoryid`,p.`quality`,p.`time`,c.`name` AS cname FROM product AS p,category AS c WHERE p.`categoryid`=c.`categoryid` AND productid = ?";
        return JDBCUtils.select(sql,productid).get(0);
    }

    //修改商品
    public void update(Product product) {
       String sql = "update product set name=?,price=?,markprice=?,quality=?,hit=?,time=?,photo=?,content=?,categoryid=? where productid=?";
       Object params[]={
         product.getName(),
         product.getPrice(),
         product.getMarkprice(),
         product.getQuality(),
         product.getHit(),
         product.getTime(),
         product.getPhoto(),
         product.getContent(),
         product.getCategoryid(),
         product.getProductid(),
       };
       JDBCUtils.update(sql,params);
    }

    //删除商品
    public void delete(int productid) {
      String sql= "delete from product where productid=?";
      JDBCUtils.update(sql,productid);
    }

    //删除多条记录
    public void deleteMore(String[] ids) {
    String sql = "delete from product where productid in(";//?,?,?)
        StringBuilder str = new StringBuilder("");
        for(int i=0;i<ids.length;i++){
            if(i==ids.length-1){
                str.append("?)");
            }else{
                str.append("?,");
            }
        }
        JDBCUtils.update(sql+str.toString(),ids);
    }
    //获取记录列表,有查询和排序
    public List<Map<String,Object>> findAll(int startIndex,int size,String skey,String svalue,String sortkey,String sort){
        //SELECT p.`productid`,p.`content`,p.`hit`,p.`price`,p.`markprice`,p.`name`,p.`photo`,p.`categoryid`,p.`quality`,p.`time`,c.`name` AS cname FROM product AS p,category AS c WHERE p.`categoryid`=c.`categoryid` AND p.NAME LIKE '%1%' LIMIT 0,2
        String sql = "SELECT p.`productid`,p.`content`,p.`hit`,p.`price`,p.`markprice`,p.`name`,p.`photo`,p.`categoryid`,p.`quality`,p.`time`,c.`name` AS cname FROM product AS p,category AS c WHERE p.`categoryid`=c.`categoryid`";
        StringBuilder str = new StringBuilder("");
        if(skey!=null&&(skey.trim().length()>0)&&svalue!=null&&(svalue.trim().length()>0)){
            //有查询条件, AND p.NAME LIKE '%1%' LIMIT 0,2
            str.append(" and p."+skey+"  like\"%"+svalue+"%\"   ");
        }
        if(sortkey!=null&&(sortkey.trim().length()>0)&&sort!=null&&(sort.trim().length()>0)){
            //有排序条件
            //升序：SELECT * FROM product ORDER BY price ASC LIMIT 0,12
            //降序：SELECT * FROM product ORDER BY price DESC LIMIT 0,12
            str.append(" order by "+sortkey+" "+sort+" ");
        }
        return JDBCUtils.select(sql+str.toString()+" limit ?,?",startIndex,size);
    }
}
