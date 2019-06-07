package com.revengemission.plugins.mybatis;


import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Mysql Truncate table plugin
 */
public class MysqlTruncateTablePlugin extends AbstractXmbgPlugin {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MysqlTruncateTablePlugin.class);

    Map<String, String> todo = new LinkedHashMap<>();

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        todo.clear();
        properties.forEach((k, v) -> {
            todo.put(StringUtils.trim(k.toString()), StringUtils.trim(v.toString()));
        });
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        Method method = new Method("truncateTable");
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        interfaze.addMethod(method);
        return true;
    }


    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        String tableName = getTableName(introspectedTable);

        XmlElement selectElement = new XmlElement("update");
        selectElement.addAttribute(new Attribute("id", "truncateTable"));
        String tempString = "TRUNCATE TABLE " + tableName;
        selectElement.addElement(
                new TextElement(tempString
                ));
        document.getRootElement().addElement(selectElement);
        return true;
    }


}
