package ${packageName};

import java.io.Serializable;

public class Page implements Serializable {

    Page() {
    }

    /**
     * this is a url builder, WARN: by default it's not threadsafe;
     * call threadsafe() first if you need to it be threadsafe (but you can
     * just ask for a new builder...)
     *
     */
    public static class URLBuilder {

        private StringBuffer sb=new StringBuffer();
        private boolean threadsafe = false;

        URLBuilder(String path) {
            sb.append(path);
        }

        private URLBuilder(StringBuffer sb) {
            this.sb = sb;
        }

        private URLBuilder(StringBuffer sb, boolean threadsafe) {
            this.sb = sb;
            this.threadsafe = threadsafe;
        }

        /**
         * create threadsafe builder
         *
         */
        public URLBuilder threasafe(){
            return new URLBuilder(new StringBuffer(sb), true);
        }
         
        /**
         * alias for param(name, value)
         *
         */
        public URLBuilder p(String name, Object value) {
            return param(name, value);
        }
        
        /**
         * alias for param(name, values)
         *
         */
        public URLBuilder p(String name, Object...values) {
            return param(name, values);
        }
        
        /**
         * append a param (if value is not null)
         *
         * @param name param name
         * @param values param values
         * @return this
         */
        public URLBuilder param(String name, Object...values) {
            URLBuilder returnMe = this;
            for(Object value : values){
                if (value!=null){
                    returnMe = returnMe.param(name, value);
                }
            }
            
            return threadsafe ? new URLBuilder(new StringBuffer(sb), true) : returnMe;
        }
        
        /**
         * append a param (if value is not null)
         *
         * @param name param name
         * @param value param value
         * @return this
         */
        public URLBuilder param(String name, Object value) {
        
            if (name==null || value==null) {
                return this;
            }
            
            if (value.getClass().isArray()){
                return param(name, (Object[]) value);
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

            return threadsafe ? new URLBuilder(new StringBuffer(sb), true) : this;
        }

        /**
         * alias for expand(values)
         *
         */
        public URLBuilder e(Object... values) {
            return expand(values);
        }
        
        /**
         * expand {pathVaribale}  
         *
         * @param values path variables values
         * @return this
         */
        public URLBuilder expand(Object... values) {

            for (Object value : values) {

                int start = sb.indexOf("{");
                if (start != -1) {
                    int end = sb.indexOf("}/", start);
                    if (end==-1){
                        end=sb.length()-1;
                    }
                    sb.delete(start, end+1);
                    sb.insert(start, ""+value);
                }
            }
            return threadsafe ? new URLBuilder(new StringBuffer(sb), true) : this;
        }

        /**
         * alias for build()
         *
         */
        public String b() {
            return build();
        }
        
        /**
         * build the url
         *
         * @return the final url
         */
        public String build() {
            return sb.toString();
        }
    }

}