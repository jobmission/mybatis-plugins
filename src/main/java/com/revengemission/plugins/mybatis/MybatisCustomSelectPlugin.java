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
 *       http://www.mybatis.org/generator/reference/plugins.html
*
* */
public class MybatisCustomSelectPlugin extends AbstractXmbgPlugin {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MybatisCustomSelectPlugin.class);

    Map<String, String> todo = new LinkedHashMap<>();

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        todo.clear();
        properties.forEach((k, v) -> {
            todo.put(StringUtils.trim(k.toString()), StringUtils.trim(v.toString()));
        });
    }

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String objectName = getEntityName(introspectedTable);
        String tableName = getTableName(introspectedTable);
        todo.forEach((k, v) -> {

            if (StringUtils.startsWith(k, tableName)) {
                int firstSemicolon = v.indexOf(";");
                int lastSemicolon = v.lastIndexOf(";");
                String returnType = v.substring(0, firstSemicolon);

                Map<String, String> result = getCustomerMapperParameters(v.substring(firstSemicolon + 1, lastSemicolon));
                Method method = new Method(k.replace(tableName, "").replace("-", ""));
                result.forEach((key, value) -> {
                    FullyQualifiedJavaType type = new FullyQualifiedJavaType(value);
                    String annotation = "@Param(\"" + key + "\")";
                    System.err.println("annotation=========" + annotation);
                    method.addParameter(new Parameter(type, key, annotation));
                });

                if (returnType.startsWith("single-")) {
                    if ("row".equals(returnType.replace("single-", ""))) {
                        method.setReturnType(new FullyQualifiedJavaType(objectName));
                    } else {
                        method.setReturnType(new FullyQualifiedJavaType(returnType.replace("single-", "")));
                    }
                } else if (returnType.startsWith("list-")) {
                    if ("row".equals(returnType.replace("list-", ""))) {
                        interfaze.addImportedType(new FullyQualifiedJavaType("java.util.List"));
                        method.setReturnType(new FullyQualifiedJavaType("List<" + objectName + ">"));
                    } else {
                        interfaze.addImportedType(new FullyQualifiedJavaType("java.util.List"));
                        interfaze.addImportedType(new FullyQualifiedJavaType("java.util.HashMap"));
                        method.setReturnType(new FullyQualifiedJavaType("List<" + returnType.replace("list-", "") + ">"));
                    }

                } else if (returnType.startsWith("hashMap-")) {
                    interfaze.addImportedType(new FullyQualifiedJavaType("java.util.HashMap"));
                    method.setReturnType(new FullyQualifiedJavaType("HashMap<String,Object>"));
                }

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
        todo.forEach((k, v) -> {

            if (StringUtils.startsWith(k, tableName)) {
                XmlElement selectElement = new XmlElement("select");
                selectElement.addAttribute(new Attribute("id", k.replace(tableName, "").replace("-", "")));
                int firstSemicolon = v.indexOf(";");
                int lastSemicolon = v.lastIndexOf(";");
                String tempString = v.substring(lastSemicolon + 1);
                String returnType = v.substring(0, firstSemicolon);
                if (returnType.startsWith("single-")) {
                    if ("row".equalsIgnoreCase(returnType.replace("single-", ""))) {
                        if (introspectedTable.getBLOBColumns().size() > 0) {
                            selectElement.addAttribute(new Attribute("resultMap", "ResultMapWithBLOBs"));
                        } else {
                            selectElement.addAttribute(new Attribute("resultMap", "BaseResultMap"));
                        }
                    } else {
                        selectElement.addAttribute(new Attribute("resultType", "java.lang." + returnType.replace("single-", "")));
                    }
                } else if (returnType.startsWith("list-")) {
                    if ("row".equalsIgnoreCase(returnType.replace("list-", ""))) {
                        if (introspectedTable.getBLOBColumns().size() > 0) {
                            selectElement.addAttribute(new Attribute("resultMap", "ResultMapWithBLOBs"));
                        } else {
                            selectElement.addAttribute(new Attribute("resultMap", "BaseResultMap"));
                        }
                    } else if ("hashmap".equalsIgnoreCase(returnType.replace("list-", ""))) {
                        selectElement.addAttribute(new Attribute("resultType", "hashmap"));
                    } else {
                        selectElement.addAttribute(new Attribute("resultType", returnType.replace("list-", "")));
                    }
                } else if (returnType.startsWith("hashMap-")) {
                    selectElement.addAttribute(new Attribute("resultType", "hashmap"));
                }


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
