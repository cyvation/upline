package com.java2e.springboot.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @describe:
 * @author:liangcan
 * @date: 2017-11-15 10:16
 */
@Component
@ConfigurationProperties(prefix="upline.path")
public class UplinePathProperties {
    private String parent;
    private String manager;
    private String center;
    private String learning;
    private String learnspace;
    private String workspace;
    private String wechat;
    private String tomcat;

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
    }

    public String getLearning() {
        return learning;
    }

    public void setLearning(String learning) {
        this.learning = learning;
    }

    public String getLearnspace() {
        return learnspace;
    }

    public void setLearnspace(String learnspace) {
        this.learnspace = learnspace;
    }

    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getTomcat() {
        return tomcat;
    }

    public void setTomcat(String tomcat) {
        this.tomcat = tomcat;
    }
}
