package com.webhis.his.VO;

import java.util.List;
import java.util.Map;

//分页相关的对象
public class pageVO {
    private List<?> data;
    private Long total;
    private Integer rows;
    private Integer page;
    private Map<String,Object> filter;
    private Integer max;

    public pageVO(List<?> data, Long total, Integer rows, Integer page, Map<String, Object> filter, Integer max) {
        this.data = data;
        this.total = total;
        this.rows = rows;
        this.page = page;
        this.filter = filter;
        this.max = max;
    }

    public pageVO() {
    }

    public List<?> getData() {
        return data;
    }

    public void setData(List<?> data) {
        this.data = data;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Map<String, Object> getFilter() {
        return filter;
    }

    public void setFilter(Map<String, Object> filter) {
        this.filter = filter;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }
}
