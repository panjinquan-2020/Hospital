package org.webhis.mvc;

import java.util.HashMap;
import java.util.Map;

/**
 * 装载转发时携带的数据
 */
public class Model {

    //转发携带的数据
    private Map<String,Object> datas = new HashMap<>();


    public Map<String, Object> getDatas() {
        return datas;
    }

    public void setAttribute(String key,Object value){
        this.datas.put(key,value);
    }

    public Model( Map<String, Object> datas) {
        this.datas = datas;
    }

    public Model() {
    }
}
