package com.revengemission.plugins.mybatis;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

/**
 * TopN
 */
public class TopNByExamplePlugin extends AbstractXmbgPlugin {

    private static final String FIELD_NAME = "topN";
    private static final String CLIENT_METHOD_NAME = "topOneByExample";
    private static final String CLIENT_METHOD_NAME2 = "topNByExample";

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        Field field = new Field(FIELD_NAME, FullyQualifiedJavaType.getIntInstance());
        field.setVisibility(JavaVisibility.PRIVATE);
        field.setInitializationString("5");
        topLevelClass.addField(field);
        char c = FIELD_NAME.charAt(0);
        String camelName = Character.toUpperCase(c) + FIELD_NAME.substring(1);
        Method method = new Method("set" + camelName);
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addParameter(new Parameter(FullyQualifiedJavaType.getIntInstance(), FIELD_NAME));
        method.addBodyLine("this." + FIELD_NAME + "=" + FIELD_NAME + ";");
        topLevelClass.addMethod(method);
        method = new Method("get" + camelName);
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.addBodyLine("return " + FIELD_NAME + ";");
        topLevelClass.addMethod(method);

        return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        {
            Method method = new Method(CLIENT_METHOD_NAME);
            method.setAbstract(true);
            method.addParameter(new Parameter(new FullyQualifiedJavaType(introspectedTable.getExampleType()), "example"));
            method.setReturnType(new FullyQualifiedJavaType(getEntityName(introspectedTable)));
            interfaze.addMethod(method);
        }
        {
            Method method = new Method(CLIENT_METHOD_NAME2);
            method.setAbstract(true);
            method.addParameter(new Parameter(new FullyQualifiedJavaType(introspectedTable.getExampleType()), "example"));
            method.setReturnType(new FullyQualifiedJavaType("List<" + getEntityName(introspectedTable) + ">"));
            interfaze.addMethod(method);
        }
        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        XmlElement parentElement = document.getRootElement();
        {
            XmlElement selectTopOneElement = new XmlElement("select");
            selectTopOneElement.addAttribute(new Attribute("id", CLIENT_METHOD_NAME));
            selectTopOneElement.addAttribute(new Attribute("parameterType", introspectedTable.getExampleType()));
            if (!introspectedTable.getBLOBColumns().isEmpty()) {
                selectTopOneElement.addAttribute(new Attribute("resultMap", "ResultMapWithBLOBs"));
            } else {
                selectTopOneElement.addAttribute(new Attribute("resultMap", "BaseResultMap"));
            }
            addTopOneLimitStatement(selectTopOneElement, introspectedTable);
            addTopOneSqlserverStatement(selectTopOneElement, introspectedTable);
            addTopOneDB2Statement(selectTopOneElement, introspectedTable);
            parentElement.addElement(selectTopOneElement);
        }

        {
            XmlElement selectTopNElement = new XmlElement("select");
            selectTopNElement.addAttribute(new Attribute("id", CLIENT_METHOD_NAME2));
            selectTopNElement.addAttribute(new Attribute("parameterType", introspectedTable.getExampleType()));
            if (!introspectedTable.getBLOBColumns().isEmpty()) {
                selectTopNElement.addAttribute(new Attribute("resultMap", "ResultMapWithBLOBs"));
            } else {
                selectTopNElement.addAttribute(new Attribute("resultMap", "BaseResultMap"));
            }
            addTopNLimitStatement(selectTopNElement, introspectedTable);
            addTopNSqlserverStatement(selectTopNElement, introspectedTable);
            addTopNDB2Statement(selectTopNElement, introspectedTable);
            parentElement.addElement(selectTopNElement);
        }
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    void addTopOneLimitStatement(XmlElement parentElement, IntrospectedTable introspectedTable) {

        String tableName = introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime();
        XmlElement topOneElement = new XmlElement("if");
        topOneElement.addAttribute(new Attribute("test", "_databaseId == 'mysql' or _databaseId == 'postgresql' or _databaseId == 'sqlite'"));

        if (!introspectedTable.getBLOBColumns().isEmpty()) {
            topOneElement.addElement(new TextElement("select <include refid=\"Base_Column_List\" />, <include refid=\"Blob_Column_List\" /> from " + tableName + " mt"));
        } else {
            topOneElement.addElement(new TextElement("select <include refid=\"Base_Column_List\" /> from " + tableName + " mt"));
        }

        topOneElement.addElement(new TextElement("<include refid=\"Example_Where_Clause\" />"));

        XmlElement orderByElement = new XmlElement("if");
        orderByElement.addAttribute(new Attribute("test", "orderByClause != null"));
        orderByElement.addElement(new TextElement("order by ${orderByClause}"));
        topOneElement.addElement(orderByElement);

        topOneElement.addElement(new TextElement("limit 1"));

        parentElement.addElement(topOneElement);
    }

    void addTopNLimitStatement(XmlElement parentElement, IntrospectedTable introspectedTable) {

        String tableName = introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime();
        XmlElement topOneElement = new XmlElement("if");
        topOneElement.addAttribute(new Attribute("test", "_databaseId == 'mysql' or _databaseId == 'postgresql' or _databaseId == 'sqlite'"));

        if (!introspectedTable.getBLOBColumns().isEmpty()) {
            topOneElement.addElement(new TextElement("select <include refid=\"Base_Column_List\" />, <include refid=\"Blob_Column_List\" /> from " + tableName + " mt"));
        } else {
            topOneElement.addElement(new TextElement("select <include refid=\"Base_Column_List\" /> from " + tableName + " mt"));
        }

        topOneElement.addElement(new TextElement("<include refid=\"Example_Where_Clause\" />"));

        XmlElement orderByElement = new XmlElement("if");
        orderByElement.addAttribute(new Attribute("test", "orderByClause != null"));
        orderByElement.addElement(new TextElement("order by ${orderByClause}"));
        topOneElement.addElement(orderByElement);

        topOneElement.addElement(new TextElement("limit ${topN}"));

        parentElement.addElement(topOneElement);
    }

    void addTopOneSqlserverStatement(XmlElement parentElement, IntrospectedTable introspectedTable) {
        String tableName = introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime();

        XmlElement topOneElement = new XmlElement("if");
        topOneElement.addAttribute(new Attribute("test", "_databaseId == 'sqlserver'"));
        if (!introspectedTable.getBLOBColumns().isEmpty()) {
            topOneElement.addElement(new TextElement("select top 1 <include refid=\"Base_Column_List\" />, <include refid=\"Blob_Column_List\" /> from " + tableName + " mt"));
        } else {
            topOneElement.addElement(new TextElement("select top 1 <include refid=\"Base_Column_List\" /> from " + tableName + " mt"));
        }
        topOneElement.addElement(new TextElement("<include refid=\"Example_Where_Clause\" />"));

        XmlElement orderByElement = new XmlElement("if");
        orderByElement.addAttribute(new Attribute("test", "orderByClause != null"));
        orderByElement.addElement(new TextElement("order by ${orderByClause}"));
        topOneElement.addElement(orderByElement);

        parentElement.addElement(topOneElement);
    }

    void addTopNSqlserverStatement(XmlElement parentElement, IntrospectedTable introspectedTable) {
        String tableName = introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime();

        XmlElement topOneElement = new XmlElement("if");
        topOneElement.addAttribute(new Attribute("test", "_databaseId == 'sqlserver'"));
        if (!introspectedTable.getBLOBColumns().isEmpty()) {
            topOneElement.addElement(new TextElement("select top ${topN} <include refid=\"Base_Column_List\" />, <include refid=\"Blob_Column_List\" /> from " + tableName + " mt"));
        } else {
            topOneElement.addElement(new TextElement("select top ${topN} <include refid=\"Base_Column_List\" /> from " + tableName + " mt"));
        }
        topOneElement.addElement(new TextElement("<include refid=\"Example_Where_Clause\" />"));

        XmlElement orderByElement = new XmlElement("if");
        orderByElement.addAttribute(new Attribute("test", "orderByClause != null"));
        orderByElement.addElement(new TextElement("order by ${orderByClause}"));
        topOneElement.addElement(orderByElement);

        parentElement.addElement(topOneElement);
    }

    void addTopOneDB2Statement(XmlElement parentElement, IntrospectedTable introspectedTable) {
        String tableName = introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime();
        XmlElement topOneElement = new XmlElement("if");
        topOneElement.addAttribute(new Attribute("test", "_databaseId == 'db2'"));
        if (!introspectedTable.getBLOBColumns().isEmpty()) {
            topOneElement.addElement(new TextElement("select <include refid=\"Base_Column_List\" />, <include refid=\"Blob_Column_List\" /> from " + tableName + " mt"));
        } else {
            topOneElement.addElement(new TextElement("select <include refid=\"Base_Column_List\" /> from " + tableName + " mt"));
        }
        topOneElement.addElement(new TextElement("<include refid=\"Example_Where_Clause\" />"));
        topOneElement.addElement(new TextElement("fetch first 1 only"));
        parentElement.addElement(topOneElement);
    }

    void addTopNDB2Statement(XmlElement parentElement, IntrospectedTable introspectedTable) {
        String tableName = introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime();
        XmlElement topOneElement = new XmlElement("if");
        topOneElement.addAttribute(new Attribute("test", "_databaseId == 'db2'"));
        if (!introspectedTable.getBLOBColumns().isEmpty()) {
            topOneElement.addElement(new TextElement("select <include refid=\"Base_Column_List\" />, <include refid=\"Blob_Column_List\" /> from " + tableName + " mt"));
        } else {
            topOneElement.addElement(new TextElement("select <include refid=\"Base_Column_List\" /> from " + tableName + " mt"));
        }
        topOneElement.addElement(new TextElement("<include refid=\"Example_Where_Clause\" />"));
        topOneElement.addElement(new TextElement("fetch first ${topN} only"));
        parentElement.addElement(topOneElement);
    }

}
