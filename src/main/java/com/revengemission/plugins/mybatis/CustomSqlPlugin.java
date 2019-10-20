package com.revengemission.plugins.mybatis;


import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

/**
 * 自定义sql
 */
public class CustomSqlPlugin extends AbstractXmbgPlugin {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        Method customSqlUpdateMethod = new Method("executeCustomSqlUpdate");
        customSqlUpdateMethod.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "customUpdateSql", "@Param(\"customUpdateSql\")"));
        customSqlUpdateMethod.setReturnType(FullyQualifiedJavaType.getIntInstance());
        interfaze.addMethod(customSqlUpdateMethod);

        Method customSqlSelectMethod = new Method("executeCustomSqlSelect");
        customSqlSelectMethod.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "customSelectSql", "@Param(\"customSelectSql\")"));
        customSqlSelectMethod.setReturnType(new FullyQualifiedJavaType("java.util.List<java.util.HashMap>"));
        interfaze.addMethod(customSqlSelectMethod);
        return true;
    }


    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        XmlElement customSqlUpdateXmlElement = new XmlElement("update");
        customSqlUpdateXmlElement.addAttribute(new Attribute("id", "executeCustomSqlUpdate"));

        customSqlUpdateXmlElement.addAttribute(new Attribute("parameterType", "java.lang.String"));
        customSqlUpdateXmlElement.addElement(new TextElement("${customUpdateSql}"));
        document.getRootElement().addElement(customSqlUpdateXmlElement);

        XmlElement customSqlSelectXmlElement = new XmlElement("select");
        customSqlSelectXmlElement.addAttribute(new Attribute("id", "executeCustomSqlSelect"));
        customSqlSelectXmlElement.addAttribute(new Attribute("resultType", "java.util.HashMap"));

        customSqlSelectXmlElement.addAttribute(new Attribute("parameterType", "java.lang.String"));
        customSqlSelectXmlElement.addElement(new TextElement("${customSelectSql}"));
        document.getRootElement().addElement(customSqlSelectXmlElement);

        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }


}
