package com.revengemission.plugins.mybatis;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 修改自动生成的update语句
 */
public class ModifyUpdateSqlPlugin extends AbstractXmbgPlugin {


    @Override
    public void initialized(IntrospectedTable introspectedTable) {

    }

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean sqlMapUpdateByExampleSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        Map<String, String> replacement = new LinkedHashMap<>();
        properties.forEach((k, v) -> {
            replacement.put(k.toString().trim(), v.toString().trim());
        });
        replaceElement(element, replacement);
        return true;
    }

    @Override
    public boolean sqlMapUpdateByExampleWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        Map<String, String> replacement = new LinkedHashMap<>();
        properties.forEach((k, v) -> {
            replacement.put(k.toString().trim(), v.toString().trim());
        });
        replaceElement(element, replacement);
        return true;
    }

    @Override
    public boolean sqlMapUpdateByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        Map<String, String> replacement = new LinkedHashMap<>();
        properties.forEach((k, v) -> {
            replacement.put(k.toString().trim(), v.toString().trim());
        });
        replaceElement(element, replacement);
        return true;
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        Map<String, String> replacement = new LinkedHashMap<>();
        properties.forEach((k, v) -> {
            replacement.put(k.toString().trim(), v.toString().trim());
        });
        replaceElement(element, replacement);
        return true;
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        Map<String, String> replacement = new LinkedHashMap<>();
        properties.forEach((k, v) -> {
            replacement.put(k.toString().trim(), v.toString().trim());
        });
        replaceElement(element, replacement);
        return true;
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        Map<String, String> replacement = new LinkedHashMap<>();
        properties.forEach((k, v) -> {
            replacement.put(k.toString().trim(), v.toString().trim());
        });
        replaceElement(element, replacement);
        return true;
    }


}
