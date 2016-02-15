/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sixdegreeshq.sitenav;

import com.sixdegreeshq.sitenav.model.Page;
import com.sixdegreeshq.sitenav.model.Page.Path;
import java.util.Stack;
import lombok.Getter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author alessandro
 */
class SitenavHandler extends DefaultHandler {

    @Getter
    private Page root;
    private Stack<Page> stack = new Stack<Page>();
    private StringBuffer sb = new StringBuffer();
    private boolean isInPaths;
    private boolean capture;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);

        if ("page".equals(qName) || "root".equals(qName)) {
            String pAttr = attributes.getValue("path");
            String path = "root".equals(qName) ? null : pAttr;
            String alias = attributes.getValue("alias");

            Page current = stack.isEmpty() ? null : stack.peek();
            String pathToAppend = current != null ? getPath(current, "*") : null;
            String fullPath = null;
            if (path != null) {
                fullPath = pathToAppend != null ? pathToAppend + "/" + path : "/" + path;
            }
            Page newPage = new Page(current, fullPath, alias != null ? alias : path);
            stack.push(newPage);

            if (current != null && current.getPaths().size() > 1) {
                for (Path p : current.getPaths()) {
                    if (!p.lang.equals("*")) {
                        Path subPath = new Path(p.lang, p.value+"/"+path);
                        newPage.addPath(subPath);
                    }
                }
            }

            if (current != null) {
                current.addChild(newPage);
            }
        } else if ("paths".equals(qName)) {
            isInPaths = true;
        } else if (isInPaths) {
            capture = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if ("paths".equals(qName)) {
            isInPaths = false;
        } else if (!isInPaths && !stack.isEmpty()) {
            root = stack.pop();
        } else if (isInPaths) {
            Page current = stack.peek();
            String pathToAppend = current != null ? getPath(current.parent, qName) : null;
            String fullPath = pathToAppend != null ? pathToAppend + "/" + sb.toString() : "/" + sb.toString();
            Path path = new Path(qName, fullPath);
            current.addPath(path);
            capture = false;
        }

        sb.delete(0, sb.length());
    }

    public String getPath(Page page, String lang) {
        if (page != null) {
            for (Path p : page.getPaths()) {
                if (lang.equals(p.lang)) {
                    return p.value != null ? p.value : "";
                }
            }

            for (Path p : page.getPaths()) {
                if (lang.equals("*")) {
                    return p.value != null ? p.value : "";
                }
            }
        }

        return null;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        if (capture) {
            sb.append(ch, start, length);
        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        stack.clear();
        sb.delete(0, sb.length());
    }

}
