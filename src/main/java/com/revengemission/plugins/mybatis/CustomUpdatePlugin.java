package com.revengemission.plugins.mybatis;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义更新
 */
public class CustomUpdatePlugin extends AbstractXmbgPlugin {

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
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        todo.forEach((k, v) -> {
            int firstSemicolon = v.indexOf(";");
            Map<String, String> result = getCustomerMapperParameters(v.substring(0, firstSemicolon));
            Method method = new Method(k);
            method.setAbstract(true);
            result.forEach((key, value) -> {
                FullyQualifiedJavaType type = new FullyQualifiedJavaType(value);
                String annotation = "@Param(\"" + key + "\")";
                method.addParameter(new Parameter(type, key, annotation));
            });

            method.setReturnType(FullyQualifiedJavaType.getIntInstance());

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

            XmlElement selectElement = new XmlElement("update");
            selectElement.addAttribute(new Attribute("id", k));
            int lastSemicolon = v.lastIndexOf(";");
            String tempString = v.substring(lastSemicolon + 1);

            selectElement.addElement(
                new TextElement(tempString
                ));
            XmlElement parentElement = document.getRootElement();
            parentElement.addElement(selectElement);
        });

        return true;
    }


}
