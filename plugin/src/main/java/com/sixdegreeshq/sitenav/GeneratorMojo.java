/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sixdegreeshq.sitenav;

import com.sixdegreeshq.sitenav.model.Page;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import lombok.Getter;
import lombok.Setter;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.xml.sax.InputSource;

/**
 *
 * @author alessandro
 */
@Mojo(name = "generator", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class GeneratorMojo extends AbstractMojo {

    /**
     * site map xml resource location in classpath
     *
     */
    @Getter
    @Setter
    @Parameter(defaultValue = "sitenav.xml")
    private String inputResourceLocation = "sitenav.xml";

    /**
     * package for autogenerated classes
     *
     */
    @Getter
    @Setter
    @Parameter(defaultValue = "R")
    private String outputPackage = "R";

    /**
     * folder for generated sources
     *
     */
    @Getter
    @Setter
    @Parameter(defaultValue = "target/generated-sources/java")
    private File outputFolder = new File("target/generated-sources/java");

    /**
     * if running tests
     *
     */
    @Getter
    @Setter
    @Parameter(defaultValue = "false")
    private boolean testing;

    /**
     * java code to resolve locale (if not specified the generated code will 
     * automatically choose between org.springframework.context.i18n.LocaleContextHolder.getLocale() 
     * (is spring class is present)
     * and java.util.Locale.getDefault()
     *
     * @parameter
     */
    @Getter
    @Setter
    @Parameter
    private String localeResolutionCode;

    private Log log = getLog();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {

            MavenProject project = MavenProject.class.cast(getPluginContext().get("project"));

            project.addCompileSourceRoot(outputFolder.getAbsolutePath());

            if (testing) {
                project.addTestCompileSourceRoot(outputFolder.getAbsolutePath());
            } else {
                project.addCompileSourceRoot(outputFolder.getAbsolutePath());
            }

            ClassLoader cl = getProjectClassLoader(project);

            SitenavHandler sitenavHandler = new SitenavHandler();

            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(new InputSource(cl.getResourceAsStream(inputResourceLocation)), sitenavHandler);

            File packageDir = new File(outputFolder, outputPackage.replace('.', '/'));
            packageDir.mkdirs();

            if (sitenavHandler.getRoot().getChildren() != null) {
                String pageSB = readResource("/com/sixdegreeshq/sitenav/tpl/Page.tpl");

                String pageContent = pageSB.toString().replace("${packageName}", outputPackage);
                FileOutputStream pageFOS = new FileOutputStream(new File(packageDir, "Page.java"));
                pageFOS.write(pageContent.getBytes());
                pageFOS.close();

                for (Page topLevelNode : sitenavHandler.getRoot().getChildren()) {
                    String className = topLevelNode.alias;
                    File javaFile = new File(packageDir, className + ".java");
                    PrintWriter pw = new PrintWriter(javaFile, "UTF-8");

                    String nodeSB = readResource("/com/sixdegreeshq/sitenav/tpl/Node.tpl");;
                    String nodeContent = nodeSB.toString()
                            .replace("${localeDeclaration}", getLocaleDeclaration())
                            .replace("${className}", className)
                            .replace("${packageDeclaration}", "package " + outputPackage + ";")
                            .replace("${alias}", className)
                            .replace("${langs}", toString(topLevelNode.getPaths(), true))
                            .replace("${paths}", toString(topLevelNode.getPaths(), false))
                            .replace("${pathsDeclarations}", getDeclarations(topLevelNode.getPaths()))
                            .replace("${classModifier}", "");

                    nodeContent = nodeContent.replace("${children}", navigate(topLevelNode));

                    FileOutputStream nodeFOS = new FileOutputStream(new File(packageDir, className + ".java"));
                    nodeFOS.write(nodeContent.getBytes());
                    nodeFOS.close();
                }
            }

        } catch (Throwable t) {
            t.printStackTrace();
            throw new MojoExecutionException(t.getMessage(), t);
        }

    }

    private String readResource(String resource) throws IOException {
        Reader pageReader = new InputStreamReader(getClass().getResourceAsStream(resource));
        StringBuffer pageSB = new StringBuffer(1024);
        char[] buffer = new char[4096];
        int read = -1;
        while ((read = pageReader.read(buffer)) != -1) {
            pageSB.append(buffer, 0, read);
        }
        pageReader.close();
        return pageSB.toString();
    }

    private String navigate(Page page) throws IOException {

        StringBuffer code = new StringBuffer(1024);

        if (page.getChildren() != null) {
            for (Page child : page.getChildren()) {

                String nodeSB = readResource("/com/sixdegreeshq/sitenav/tpl/Node.tpl");

                String nodeContent = nodeSB.toString()
                        .replace("${localeDeclaration}", getLocaleDeclaration())
                        .replace("${className}", child.alias)
                        .replace("${packageDeclaration}", "")
                        .replace("${alias}", child.alias)
                        .replace("${langs}", toString(child.getPaths(), true))
                        .replace("${paths}", toString(child.getPaths(), false))
                        .replace("${pathsDeclarations}", getDeclarations(child.getPaths()))
                        .replace("${classModifier}", "static");

                nodeContent = nodeContent.replace("${children}", navigate(child));

                code.append('\n').append(nodeContent).append('\n');
            }
        }

        return code.toString();

    }

    private String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    private CharSequence toString(List<Page.Path> paths, boolean lang) {
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        for (Page.Path path : paths) {
            if (!first) {
                sb.append(',');
            }
            first = false;
            sb.append('"').append(lang ? path.lang : path.value).append('"');
        }

        return sb.toString();
    }

    private CharSequence getLocaleDeclaration() {

        if (localeResolutionCode != null) {
            return localeResolutionCode;
        }

        try {
            Class.forName("org.springframework.context.i18n.LocaleContextHolder");
            return "org.springframework.context.i18n.LocaleContextHolder.getLocale()";
        } catch (Throwable t) {
            return "java.util.Locale.getDefault()";
        }
    }

    /**
     * got from
     * https://github.com/querydsl/querydsl/blob/master/querydsl-maven-plugin/src/main/java/com/querydsl/maven/AbstractExporterMojo.java
     *
     */
    @SuppressWarnings("unchecked")
    private ClassLoader getProjectClassLoader(MavenProject project) throws DependencyResolutionRequiredException,
            MalformedURLException {
        List<String> classpathElements;
        if (testing) {
            classpathElements = project.getTestClasspathElements();
            for (Resource testResource : project.getTestResources()) {
                classpathElements.add(testResource.getDirectory());
            }

        } else {
            classpathElements = project.getCompileClasspathElements();
        }

        for (Resource testResource : project.getResources()) {
            classpathElements.add(testResource.getDirectory());
        }

        List<URL> urls = new ArrayList<URL>(classpathElements.size());
        for (String element : classpathElements) {
            File file = new File(element);
            if (file.exists()) {
                urls.add(file.toURI().toURL());
            }
        }
        return new URLClassLoader(urls.toArray(new URL[urls.size()]), getClass().getClassLoader());
    }

    private CharSequence getDeclarations(List<Page.Path> paths) {
        StringBuffer sb = new StringBuffer();
        if (paths != null) {
            for (Page.Path path : paths) {
                sb.append("/** ").append(path.value).append(" */").append('\n');
                sb.append("public static final String _p");
                if (!"*".equals(path.lang)) {
                    sb.append('_').append(path.lang);
                }
                sb.append("=\"").append(path.value).append("\";").append('\n');
            }
        }

        return sb;
    }
}
