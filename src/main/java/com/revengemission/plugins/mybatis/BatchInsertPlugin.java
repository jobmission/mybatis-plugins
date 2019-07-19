package com.revengemission.plugins.mybatis;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

/**
 * 批量插入
 */
public class BatchInsertPlugin extends AbstractXmbgPlugin {

    private static final String CLIENT_METHOD_NAME = "batchInsert";

    private static final String PROPERTY_PREFIX = "item.";

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String objectName = getEntityName(introspectedTable);
        Method method = new Method(CLIENT_METHOD_NAME);
        FullyQualifiedJavaType type = new FullyQualifiedJavaType("java.util.List<" + objectName + ">");
        method.addParameter(new Parameter(type, "list"));
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        interfaze.addMethod(method);
        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        XmlElement insertXmlElement = new XmlElement("insert");

        insertXmlElement.addAttribute(new Attribute("id", CLIENT_METHOD_NAME));
        FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType("java.util.List");
        insertXmlElement.addAttribute(new Attribute("parameterType", parameterType.getFullyQualifiedName()));

        generateTextBlockAppendTableName("insert into ", introspectedTable, insertXmlElement);

        generateActualColumnNamesWithParenthesis(introspectedTable.getNonBLOBColumns(), insertXmlElement);

        insertXmlElement.addElement(new TextElement(" values "));

        XmlElement foreach = new XmlElement("foreach");
        foreach.addAttribute(new Attribute("collection", "list"));
        foreach.addAttribute(new Attribute("item", "item"));
        foreach.addAttribute(new Attribute("index", "index"));
        foreach.addAttribute(new Attribute("separator", ","));

        generateParametersSeparateByCommaWithParenthesis(PROPERTY_PREFIX, introspectedTable.getNonBLOBColumns(), foreach);

        insertXmlElement.addElement(foreach);

        document.getRootElement().addElement(insertXmlElement);

        return true;
    }

}
