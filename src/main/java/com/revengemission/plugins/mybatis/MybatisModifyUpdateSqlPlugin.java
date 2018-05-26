package com.revengemission.plugins.mybatis;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
* Supplied Plugins
*
 *       http://www.mybatis.org/generator/reference/plugins.html
*
* */
public class MybatisModifyUpdateSqlPlugin extends AbstractXmbgPlugin {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MybatisModifyUpdateSqlPlugin.class);

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean sqlMapUpdateByExampleSelectiveElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {
        Map<String, String> todo = new LinkedHashMap<>();

        properties.forEach((k, v) -> {
            logger.info("k ==" + k + ",v==========" + v);
            todo.put(StringUtils.trim(k.toString()), StringUtils.trim(v.toString()));
        });

        replaceElement(element, todo);

        element.getElements().forEach(element1 -> {
            logger.info("element ===============" + ReflectionToStringBuilder.toString(element1));
        });
        return true;
    }

    @Override
    public boolean sqlMapUpdateByExampleWithBLOBsElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {
        Map<String, String> todo = new LinkedHashMap<>();

        properties.forEach((k, v) -> {
            logger.info("k ==" + k + ",v==========" + v);
            todo.put(StringUtils.trim(k.toString()), StringUtils.trim(v.toString()));
        });

        replaceElement(element, todo);

        element.getElements().forEach(element1 -> {
            logger.info("element ===============" + ReflectionToStringBuilder.toString(element1));
        });
        return true;
    }

    @Override
    public boolean sqlMapUpdateByExampleWithoutBLOBsElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {
        Map<String, String> todo = new LinkedHashMap<>();

        properties.forEach((k, v) -> {
            logger.info("k ==" + k + ",v==========" + v);
            todo.put(StringUtils.trim(k.toString()), StringUtils.trim(v.toString()));
        });

        replaceElement(element, todo);

        element.getElements().forEach(element1 -> {
            logger.info("element ===============" + ReflectionToStringBuilder.toString(element1));
        });
        return true;
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {
        Map<String, String> todo = new LinkedHashMap<>();

        properties.forEach((k, v) -> {
            logger.info("k ==" + k + ",v==========" + v);
            todo.put(StringUtils.trim(k.toString()), StringUtils.trim(v.toString()));
        });

        replaceElement(element, todo);

        element.getElements().forEach(element1 -> {
            logger.info("element ===============" + ReflectionToStringBuilder.toString(element1));
        });
        return true;
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {

        Map<String, String> todo = new LinkedHashMap<>();

        properties.forEach((k, v) -> {
            logger.info("k ==" + k + ",v==========" + v);
            todo.put(StringUtils.trim(k.toString()), StringUtils.trim(v.toString()));
        });

        replaceElement(element, todo);

        element.getElements().forEach(element1 -> {
            logger.info("element ===============" + ReflectionToStringBuilder.toString(element1));
        });
        return true;
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {

        Map<String, String> todo = new LinkedHashMap<>();

        properties.forEach((k, v) -> {
            logger.info("k ==" + k + ",v==========" + v);
            todo.put(StringUtils.trim(k.toString()), StringUtils.trim(v.toString()));
        });

        replaceElement(element, todo);

        element.getElements().forEach(element1 -> {
            logger.info("element ===============" + ReflectionToStringBuilder.toString(element1));
        });
        return true;
    }


}
