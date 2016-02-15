${packageDeclaration}

public ${classModifier} class ${className} extends Page {
    /** ${alias} */
    public static final String a="${alias}";
    public static final String alias="${alias}";

    private static final String[] ps={${paths}};
    private static final String[] paths={${paths}};
/* all paths */
${pathsDeclarations}
/* /all paths */

    private static final String[] langs={${langs}};
/** ${children} */
    public static final Class[] children={${children}};

    private static java.util.Map<String, String> pathsMap=new java.util.HashMap<String, String>();

    static {
        for(int i = 0; i<langs.length; i++){
            pathsMap.put(langs[i], paths[i]);
        }
    }
    
    /**
     * alias for builder()
     *
     */
    public static final URLBuilder b(){
        return builder();
    }

    /**
     * create a new url builder using default locale
     *
     */
    public static final URLBuilder builder(){
        String lang = ${localeDeclaration}.getLanguage();
        return builder(lang);
    }
    /**
     * alias for builder(locale)
     *
     */
    public static final URLBuilder b(java.util.Locale locale){
        return builder(locale);
    }

    /**
     * create a new url builder using given locale
     *
     * @param locale desired locale
     */
    public static final URLBuilder builder(java.util.Locale locale){
        String lang = locale.getLanguage();
        return builder(lang);
    }
    /**
     * alias for builder(lang)
     *
     */
    public static final URLBuilder b(String lang){
        return builder(lang);
    }
    /**
     * create a new url builder using given language
     *
     * @param locale desired language
     */
    public static final URLBuilder builder(String lang){
        String path=null;
        if (pathsMap.get(lang)!=null){
            path = pathsMap.get(lang);
        } else if (pathsMap.get("*")!=null){
            path = pathsMap.get("*");
        } else {
            path = pathsMap.entrySet().iterator().next().getValue();
        }

        return new Page.URLBuilder(path);
    }

    ${childrenDeclaration}
}