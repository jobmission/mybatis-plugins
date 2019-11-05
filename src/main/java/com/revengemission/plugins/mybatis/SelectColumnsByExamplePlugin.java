package com.revengemission.plugins.mybatis;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

/**
 * 根据Example查询部分字段，减少字段返回
 */
public class SelectColumnsByExamplePlugin extends AbstractXmbgPlugin {

    private static final String CLIENT_METHOD_NAME = "selectColumnsByExample";
    private static final String CLIENT_METHOD_NAME_IDS = "selectIdsByExample";

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }


    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        Field commaSeparatedColumnsField = new Field();
        commaSeparatedColumnsField.setVisibility(JavaVisibility.PRIVATE);
        commaSeparatedColumnsField.setName("commaSeparatedColumns");
        commaSeparatedColumnsField.setType(FullyQualifiedJavaType.getStringInstance());
        topLevelClass.addField(commaSeparatedColumnsField);

        Method setCommaSeparatedColumns = new Method();
        setCommaSeparatedColumns.setVisibility(JavaVisibility.PUBLIC);
        setCommaSeparatedColumns.setName("setCommaSeparatedColumns");
        setCommaSeparatedColumns.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "commaSeparatedColumns"));
        setCommaSeparatedColumns.addBodyLine("this.commaSeparatedColumns = commaSeparatedColumns;");
        topLevelClass.addMethod(setCommaSeparatedColumns);

        Method getCommaSeparatedColumns = new Method();
        getCommaSeparatedColumns.setVisibility(JavaVisibility.PUBLIC);
        getCommaSeparatedColumns.setReturnType(FullyQualifiedJavaType.getStringInstance());
        getCommaSeparatedColumns.setName("getCommaSeparatedColumns");
        getCommaSeparatedColumns.addBodyLine("return commaSeparatedColumns;");
        topLevelClass.addMethod(getCommaSeparatedColumns);

        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        Method method = new Method(CLIENT_METHOD_NAME);
        method.addParameter(new Parameter(new FullyQualifiedJavaType(introspectedTable.getExampleType()), "example"));
        method.setReturnType(new FullyQualifiedJavaType("List<" + getEntityName(introspectedTable) + ">"));
        interfaze.addMethod(method);

        Method method2 = new Method(CLIENT_METHOD_NAME_IDS);
        method2.addParameter(new Parameter(new FullyQualifiedJavaType(introspectedTable.getExampleType()), "example"));
        method2.setReturnType(new FullyQualifiedJavaType("List<Long>"));
        interfaze.addMethod(method2);

        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        String tableName = introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime();
        XmlElement parentElement = document.getRootElement();

        XmlElement selectElement = new XmlElement("select");
        selectElement.addAttribute(new Attribute("id", CLIENT_METHOD_NAME));
        selectElement.addAttribute(new Attribute("parameterType", introspectedTable.getExampleType()));
        selectElement.addAttribute(new Attribute("resultMap", "BaseResultMap"));
        selectElement.addElement(new TextElement("select"));

        XmlElement ifNotNullElement = new XmlElement("if");
        ifNotNullElement.addAttribute(new Attribute("test", "commaSeparatedColumns != null"));
        ifNotNullElement.addElement(new TextElement("${commaSeparatedColumns}"));
        selectElement.addElement(ifNotNullElement);

        XmlElement ifNullElement = new XmlElement("if");
        ifNullElement.addAttribute(new Attribute("test", "commaSeparatedColumns == null"));
        ifNullElement.addElement(new TextElement("<include refid=\"Base_Column_List\" />"));
        selectElement.addElement(ifNullElement);

        selectElement.addElement(new TextElement("from " + tableName));
        selectElement.addElement(new TextElement("<include refid=\"Example_Where_Clause\" />"));

        parentElement.addElement(selectElement);


        XmlElement selectElement2 = new XmlElement("select");
        selectElement2.addAttribute(new Attribute("id", CLIENT_METHOD_NAME_IDS));
        selectElement2.addAttribute(new Attribute("parameterType", introspectedTable.getExampleType()));
        selectElement2.addAttribute(new Attribute("resultType", "java.lang.Long"));
        selectElement2.addElement(new TextElement("select "));
        selectElement2.addElement(new TextElement("id "));
        selectElement2.addElement(new TextElement("from " + tableName));
        selectElement2.addElement(new TextElement("<include refid=\"Example_Where_Clause\" />"));
        parentElement.addElement(selectElement2);

        return super.sqlMapDocumentGenerated(document, introspectedTable);

    }


}
