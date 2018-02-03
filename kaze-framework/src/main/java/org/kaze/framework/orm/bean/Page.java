package org.kaze.framework.orm.bean;

import java.util.List;

public class Page {

    private Integer pageNum;
    private Integer rows;
    private Integer total;
    private List<?> list;

    public Page(Integer pageNum, Integer rows, Integer total, List<?> list) {
        this.pageNum = pageNum;
        this.rows = rows;
        this.total = total;
        this.list = list;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }

}
