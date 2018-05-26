package com.revengemission.plugins.mybatis;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
*
* Supplied Plugins
*
 *       http://www.mybatis.org/generator/reference/plugins.html
* */
public class MybatisCustomUpdatePlugin extends AbstractXmbgPlugin {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MybatisCustomUpdatePlugin.class);

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String tableName = getTableName(introspectedTable);
        Map<String, String> todo = new LinkedHashMap<>();
        properties.forEach((k, v) -> {
            todo.put(StringUtils.trim(k.toString()), StringUtils.trim(v.toString()));
        });


        todo.forEach((k, v) -> {

            if (StringUtils.startsWith(k, tableName)) {
                int firstSemicolon = v.indexOf(";");
                Map<String, String> result = getCustomerMapperParameters(v.substring(0, firstSemicolon));
                Method method = new Method(k.replace(tableName, "").replace("-", ""));
                result.forEach((key, value) -> {
                    FullyQualifiedJavaType type = new FullyQualifiedJavaType(value);
                    String annotation = "@Param(\"" + key + "\")";
                    System.err.println("annotation=========" + annotation);
                    method.addParameter(new Parameter(type, key, annotation));
                });

                method.setReturnType(FullyQualifiedJavaType.getIntInstance());

                interfaze.addMethod(method);
            }
        });


        return true;
    }

    private Map<String, String> getCustomerMapperParameters(String parameterString) {
        Map<String, String> result = new LinkedHashMap<>();
        String[] fields = parameterString.split(",");
        for (int i = 0; i < fields.length; i++) {
            String[] parameter = fields[i].split(" ");
            if (parameter.length == 2) {
                result.put(parameter[1], parameter[0]);
            }
        }
        return result;
    }


    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        String tableName = getTableName(introspectedTable);
        Map<String, String> todo = new LinkedHashMap<>();
        properties.forEach((k, v) -> {
            todo.put(StringUtils.trim(k.toString()), StringUtils.trim(v.toString()));
        });

        logger.info("customUpdate_" + todo.size());
        todo.forEach((k, v) -> {
            logger.info("customUpdate_ k ===============" + k);
            logger.info("customUpdate_ v ===============" + v);
        });

        todo.forEach((k, v) -> {

            if (StringUtils.startsWith(k, tableName)) {

                XmlElement selectElement = new XmlElement("update");
                selectElement.addAttribute(new Attribute("id", k.replace(tableName, "").replace("-", "")));
                int lastSemicolon = v.lastIndexOf(";");
                String tempString = v.substring(lastSemicolon + 1);

                selectElement.addElement(
                        new TextElement(tempString
                        ));
                XmlElement parentElement = document.getRootElement();
                parentElement.addElement(selectElement);
            }
        });


        return true;
    }


}
