package com.revengemission.plugins.mybatis;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
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
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        String objectName = getEntityName(introspectedTable);
        Method method = new Method(CLIENT_METHOD_NAME);
        method.setAbstract(true);
        FullyQualifiedJavaType type = new FullyQualifiedJavaType("java.util.List<" + objectName + ">");
        method.addParameter(new Parameter(type, "list"));
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        interfaze.addMethod(method);
        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        XmlElement insertXmlElement = new XmlElement("insert");
        List<IntrospectedColumn> notAutoIncrementColumnList = introspectedTable.getAllColumns().stream().filter(introspectedColumn -> !introspectedColumn.isAutoIncrement()).toList();

        insertXmlElement.addAttribute(new Attribute("id", CLIENT_METHOD_NAME));
        FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType("java.util.List");
        insertXmlElement.addAttribute(new Attribute("parameterType", parameterType.getFullyQualifiedName()));

        insertXmlElement.addElement(new TextElement("<if test=\"list != null and list.size() > 0\">"));
        generateTextBlockAppendTableName("insert into ", introspectedTable, insertXmlElement);

        generateActualColumnNamesWithParenthesis(notAutoIncrementColumnList, insertXmlElement);

        insertXmlElement.addElement(new TextElement(" values "));

        XmlElement foreach = new XmlElement("foreach");
        foreach.addAttribute(new Attribute("collection", "list"));
        foreach.addAttribute(new Attribute("item", "item"));
        foreach.addAttribute(new Attribute("index", "index"));
        foreach.addAttribute(new Attribute("separator", ","));

        generateParametersSeparateByCommaWithParenthesis(PROPERTY_PREFIX, notAutoIncrementColumnList, foreach);
        insertXmlElement.addElement(foreach);
        insertXmlElement.addElement(new TextElement("</if>"));
        insertXmlElement.addElement(new TextElement("<if test=\"list == null or list.size() == 0\">"));
        insertXmlElement.addElement(new TextElement("  select 0"));
        insertXmlElement.addElement(new TextElement("</if>"));

        document.getRootElement().addElement(insertXmlElement);

        return true;
    }

}
