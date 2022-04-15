package com.cxj.service;


import com.cxj.dao.PCADao;

import java.util.List;
import java.util.Map;

public class PCAService {
    private PCADao pcaDao = new PCADao();

    public List<Map<String,Object>> getProvinces(){
        //通过调用dao层的方法来获取省份列表
        return pcaDao.getProvinces();
    }

    public List<Map<String, Object>> getCities(String provinceid) {
       return pcaDao.getCities(provinceid);
    }

    public List<Map<String, Object>> getAreas(String cityid) {
        return pcaDao.getAreas(cityid);
    }
}
