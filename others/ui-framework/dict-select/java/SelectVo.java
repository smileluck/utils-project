package com.ruoyi.system.domain.vo;

import java.util.List;

public class SelectVo {
    /**
     * 查询列表
     */
    private List list;

    /**
     * 值key
     */
    private String valueKey;

    /**
     * 名称KEy
     */
    private String labelKey;

    private SelectVo() {

    }

    public static SelectVo createOf(String valueKey, String labelKey, List list) {
        SelectVo selectVo = new SelectVo();
        selectVo.setLabelKey(labelKey);
        selectVo.setValueKey(valueKey);
        selectVo.setList(list);
        return selectVo;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    public String getValueKey() {
        return valueKey;
    }

    public void setValueKey(String valueKey) {
        this.valueKey = valueKey;
    }

    public String getLabelKey() {
        return labelKey;
    }

    public void setLabelKey(String labelKey) {
        this.labelKey = labelKey;
    }
}
