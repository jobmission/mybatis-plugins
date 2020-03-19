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

        Field commaSeparatedColumnsField = new Field("commaSeparatedColumns", FullyQualifiedJavaType.getStringInstance());
        commaSeparatedColumnsField.setVisibility(JavaVisibility.PRIVATE);
        commaSeparatedColumnsField.addJavaDocLine("/**");
        commaSeparatedColumnsField.addJavaDocLine(" * 期望返回部分字段，以逗号分割开");
        commaSeparatedColumnsField.addJavaDocLine(" */");

        topLevelClass.addField(commaSeparatedColumnsField);

        Method setCommaSeparatedColumns = new Method("setCommaSeparatedColumns");
        setCommaSeparatedColumns.setVisibility(JavaVisibility.PUBLIC);
        setCommaSeparatedColumns.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "commaSeparatedColumns"));
        setCommaSeparatedColumns.addBodyLine("this.commaSeparatedColumns = commaSeparatedColumns;");
        setCommaSeparatedColumns.addJavaDocLine("/**");
        setCommaSeparatedColumns.addJavaDocLine(" * @param commaSeparatedColumns 期望返回部分字段，以逗号分割开");
        setCommaSeparatedColumns.addJavaDocLine(" */");
        topLevelClass.addMethod(setCommaSeparatedColumns);

        Method getCommaSeparatedColumns = new Method("getCommaSeparatedColumns");
        getCommaSeparatedColumns.setVisibility(JavaVisibility.PUBLIC);
        getCommaSeparatedColumns.setReturnType(FullyQualifiedJavaType.getStringInstance());
        getCommaSeparatedColumns.addBodyLine("return commaSeparatedColumns;");
        topLevelClass.addMethod(getCommaSeparatedColumns);

        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {

        Method method = new Method(CLIENT_METHOD_NAME);
        method.setAbstract(true);
        method.addParameter(new Parameter(new FullyQualifiedJavaType(introspectedTable.getExampleType()), "example"));
        method.setReturnType(new FullyQualifiedJavaType("List<" + getEntityName(introspectedTable) + ">"));
        interfaze.addMethod(method);

        Method method2 = new Method(CLIENT_METHOD_NAME_IDS);
        method2.setAbstract(true);
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
