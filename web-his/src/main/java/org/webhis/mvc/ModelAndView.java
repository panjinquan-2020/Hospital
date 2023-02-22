package org.webhis.mvc;

import java.util.HashMap;
import java.util.Map;

/**
 * 装载转发时的内容和携带的数据
 */
public class ModelAndView {
    //16.jsp
    private String viewName ;

    //转发携带的数据
    private Map<String,Object> datas = new HashMap<>();

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, Object> getDatas() {
        return datas;
    }

    public void setAttribute(String key,Object value){
        this.datas.put(key,value);
    }

    public ModelAndView(String viewName, Map<String, Object> datas) {
        this.viewName = viewName;
        this.datas = datas;
    }

    public ModelAndView() {
    }
}
