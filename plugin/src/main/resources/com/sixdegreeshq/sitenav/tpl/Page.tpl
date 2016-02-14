package ${packageName};

import java.io.Serializable;

public class Page implements Serializable {

    Page() {
    }

    public static class URLBuilder {

        private StringBuffer sb=new StringBuffer();

        URLBuilder(String path) {
            sb.append(path);
        }

        private URLBuilder(StringBuffer sb) {
            this.sb = sb;
        }

        public URLBuilder param(String name, Object value) {
        
            if (value==null) {
                return this;
            }

            if (sb.indexOf("?") == -1) {
                sb.append('?');
            } else {
                sb.append('&');
            }

            try {
                sb.append(java.net.URLEncoder.encode(name, "UTF-8"));
                sb.append('=');
                sb.append(java.net.URLEncoder.encode(""+value, "UTF-8"));
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }

            return new URLBuilder(sb);
        }

        public URLBuilder expand(Object... values) {

            for (Object value : values) {

                int start = sb.indexOf("{");
                if (start != -1) {
                    int end = sb.indexOf("}", start);
                    sb.delete(start, end+1);
                    sb.insert(start, ""+value);
                }
            }
            return new URLBuilder(sb);
        }

        public String build() {
            return sb.toString();
        }
    }

}