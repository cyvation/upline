package com.java2e.springboot.bean;

import java.util.List;

/**
 * @describe:
 * @author:liangcan
 * @date: 2017-11-16 17:23
 */
public class FileNode {
    private String name;
    private String code;
    private String time;
    private String revert;
    private List<FileNode> children;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRevert() {
        return revert;
    }

    public void setRevert(String revert) {
        this.revert = revert;
    }

    public List<FileNode> getChildren() {
        return children;
    }

    public void setChildren(List<FileNode> children) {
        this.children = children;
    }

    public FileNode() {
    }
}
