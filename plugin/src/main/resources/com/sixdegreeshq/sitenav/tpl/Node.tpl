${packageDeclaration}

public ${classModifier} class ${className} extends Page {
    /** ${alias} */
    public static final String _a="${alias}";

    private static final String[] _ps={${paths}};
/* all paths */
${pathsDeclarations}
/* /all paths */

    private static final String[] _langs={${langs}};

    private static java.util.Map<String, String> pathsMap=new java.util.HashMap<String, String>();

    static {
        for(int i = 0; i<_langs.length; i++){
            pathsMap.put(_langs[i], _ps[i]);
        }
    }
    
    public static final URLBuilder builder(){
        String lang = ${localeDeclaration}.getLanguage();
        return builder(lang);
    }
    public static final URLBuilder builder(java.util.Locale locale){
        String lang = locale.getLanguage();
        return builder(lang);
    }
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

    ${children}
}