package com.revengemission.plugins.mybatis;


import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义查询，指定类型
 */
public class MybatisCustomSelectPlugin extends AbstractXmbgPlugin {

    Map<String, String> todo = new LinkedHashMap<>();

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        todo.clear();
        String currentTableName = getTableName(introspectedTable);
        properties.forEach((k, v) -> {
            String[] temp = k.toString().trim().split(";");
            if (temp.length == 2) {
                if (currentTableName.equalsIgnoreCase(temp[0])) {
                    todo.put(temp[1].trim(), v.toString().trim());
                }
            }
        });
    }

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String objectName = getEntityName(introspectedTable);
        todo.forEach((k, v) -> {
            int firstSemicolon = v.indexOf(";");
            int lastSemicolon = v.lastIndexOf(";");
            String returnType = v.substring(0, firstSemicolon);

            Map<String, String> result = getCustomerMapperParameters(v.substring(firstSemicolon + 1, lastSemicolon));
            Method method = new Method(k);
            result.forEach((key, value) -> {
                FullyQualifiedJavaType type = new FullyQualifiedJavaType(value);
                String annotation = "@Param(\"" + key + "\")";
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
        });


        return true;
    }

    private Map<String, String> getCustomerMapperParameters(String parameterString) {
        Map<String, String> result = new LinkedHashMap<>();
        String[] fields = parameterString.split(",");
        for (String field : fields) {
            String[] parameter = field.split(" ");
            if (parameter.length == 2) {
                result.put(parameter[1], parameter[0]);
            }
        }
        return result;
    }


    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        todo.forEach((k, v) -> {

            XmlElement selectElement = new XmlElement("select");
            selectElement.addAttribute(new Attribute("id", k));
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
        });

        return true;
    }


}
