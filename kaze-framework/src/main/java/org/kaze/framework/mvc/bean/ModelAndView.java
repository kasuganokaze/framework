package org.kaze.framework.mvc.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * 封装模型和视图信息
 *
 * @author kaze
 * @since 2017/09/02
 */
public class ModelAndView {

    private String view;
    private Map<String, Object> modelMap;

    public ModelAndView() {
    }

    public ModelAndView(String view) {
        this.view = view;
    }

    public ModelAndView(String view, Map<String, Object> model) {
        this.view = view;
        if (model != null) {
            this.getModelMap().putAll(model);
        }
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getView() {
        return view;
    }

    public Map<String, Object> getModelMap() {
        if (modelMap == null) {
            modelMap = new HashMap<>();
        }
        return modelMap;
    }

    public void addObject(String key, Object value) {
        getModelMap().put(key, value);
    }

    public void addAllObjects(Map<String, ?> modelMap) {
        if (modelMap != null) {
            getModelMap().putAll(modelMap);
        }
    }

}
