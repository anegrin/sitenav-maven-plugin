/*
 * Copyright 2016 alessandro negrin <alessandro@sixdegreeshq.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        paths.remove(path);
        paths.add(path);
    }

    public List<Path> getPaths() {
        return paths.isEmpty() ? Collections.singletonList(new Path("*", path)) : paths;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(100);
        sb.append('(').append(getPaths()).append(',').append(alias).append(") => [");
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

        @Override
        public String toString() {
            return lang+":"+value;
        }

        @Override
        public boolean equals(Object obj) {
            try {
                return lang.equals(Path.class.cast(obj).lang);
            } catch (Throwable t){
                return false;
            }
        }
        
        
        
        
    }

}
