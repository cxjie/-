package com.cxj.service;


import com.cxj.dao.CategoryDao;
import com.cxj.domain.Category;
import com.cxj.domain.Page;

import java.util.List;
import java.util.Map;

public class CategoryService {

    private CategoryDao categoryDao = new CategoryDao();

    public void update(Category c){
        categoryDao.update(c);
    }

    public Map<String,Object> findById(int id){
        return categoryDao.findById(id);
    }

    public void deleteMore(String id[]){
        categoryDao.deleteMore(id);

    }
    public void deleteById(int id){
        categoryDao.deleteById(id);
    }

    public void add(Category category){
        //调用dao层方法进行添加
        categoryDao.add(category);
    }

    //根据分类名称查找是否有该分类
    public boolean validateName(String name){
        //1.调用dao层方法，通过name获取Category对象
        //2.判断该对象是否为空，如果为空返回false,否则返回true
        Category category = categoryDao.findByName(name) ;
        if(category == null){
            return false;
        }
        return true;
    }

    //获取所有的分类记录
    public List<Map<String,Object>> findAll(){
        //直接调用dao层的findAll()方法
        return categoryDao.findAll();
    }

    //获取当前页对应的Page对象
    public Page findAll(int currentPage) {
        /*
            1.通过调用dao层的方法findTotalCount()获取分类表的总记录数
            2.创建Page对象
            3.根据起始记录和每页显示多少条记录，调用dao层的方法findAll()来获取当前页所有值
            4.把读取的记录集给Page对象赋值，并返回Page对象
         */
        //1.通过调用dao层的方法findTotalCount()获取分类表的总记录数
        int totalCount = categoryDao.findTotalCount();
        //2.创建Page对象
        Page page = new Page(currentPage,totalCount);
        //3.根据起始记录和每页显示多少条记录，调用dao层的方法findAll()来获取当前页所有值
        List<Map<String,Object>> list = categoryDao.findAll(page.getStartIndex(),page.getPageSize());
        page.setList(list);
        return page;
    }
}
