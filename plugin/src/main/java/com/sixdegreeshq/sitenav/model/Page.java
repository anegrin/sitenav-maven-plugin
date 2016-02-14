/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sixdegreeshq.sitenav.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author alessandro
 */
public class Page {

    private String path;

    public final Page parent;
    public final String alias;
    @Getter
    private List<Page> children = new ArrayList<Page>();
    private List<Path> paths = new ArrayList<Path>();

    public Page(Page parent, String path, String alias) {
        this.parent = parent;
        this.path = path;
        this.alias = alias;
    }

    public void addChild(Page page) {
        children.add(page);
    }

    public void addPath(Path path) {
        paths.add(path);
    }

    public List<Path> getPaths() {
        return paths.isEmpty() ? Collections.singletonList(new Path("*", path)) : paths;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(100);
        sb.append('(').append(path).append(',').append(alias).append(") => [");
        if (children != null) {
            for (Page page : children) {
                sb.append(page.toString()).append(',');
            }
        }

        sb.append("]");

        return sb.toString();
    }

    public static class Path {

        public final String lang;
        public final String value;

        public Path(String lang, String value) {
            this.lang = lang;
            this.value = value;
        }
    }

}
