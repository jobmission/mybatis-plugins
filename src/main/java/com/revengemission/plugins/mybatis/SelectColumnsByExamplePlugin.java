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
 * 根据Example查询部分字段及group by 语句支持
 */
public class SelectColumnsByExamplePlugin extends AbstractXmbgPlugin {

    private static final String CLIENT_METHOD_NAME = "selectColumnsByExample";
    private static final String CLIENT_METHOD_NAME_IDS = "selectIdsByExample";
    private static final String CLIENT_METHOD_NAME_AGGREGATE_QUERY = "aggregateQueryByExample";
    private static final String CLIENT_METHOD_NAME_AGGREGATE_STATISTICS = "aggregateStatisticsByExample";

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        Field commaSeparatedColumnsField = new Field("commaSeparatedColumns", FullyQualifiedJavaType.getStringInstance());
        commaSeparatedColumnsField.setVisibility(JavaVisibility.PRIVATE);
        commaSeparatedColumnsField.addJavaDocLine("/**");
        commaSeparatedColumnsField.addJavaDocLine(" * 期望返回字段，以逗号分割开");
        commaSeparatedColumnsField.addJavaDocLine(" */");

        topLevelClass.addField(commaSeparatedColumnsField);

        Method setCommaSeparatedColumns = new Method("setCommaSeparatedColumns");
        setCommaSeparatedColumns.setVisibility(JavaVisibility.PUBLIC);
        setCommaSeparatedColumns.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "commaSeparatedColumns"));
        setCommaSeparatedColumns.addBodyLine("this.commaSeparatedColumns = commaSeparatedColumns;");
        setCommaSeparatedColumns.addJavaDocLine("/**");
        setCommaSeparatedColumns.addJavaDocLine(" * @param commaSeparatedColumns 期望返回字段，以逗号分割开");
        setCommaSeparatedColumns.addJavaDocLine(" */");
        topLevelClass.addMethod(setCommaSeparatedColumns);

        Method getCommaSeparatedColumns = new Method("getCommaSeparatedColumns");
        getCommaSeparatedColumns.setVisibility(JavaVisibility.PUBLIC);
        getCommaSeparatedColumns.setReturnType(FullyQualifiedJavaType.getStringInstance());
        getCommaSeparatedColumns.addBodyLine("return commaSeparatedColumns;");
        topLevelClass.addMethod(getCommaSeparatedColumns);

        Field aggregateByClause = new Field("aggregateByClause", FullyQualifiedJavaType.getStringInstance());
        aggregateByClause.setVisibility(JavaVisibility.PRIVATE);
        aggregateByClause.addJavaDocLine("/**");
        aggregateByClause.addJavaDocLine(" * aggregate query clause 语句, 注意未做防注入处理");
        aggregateByClause.addJavaDocLine(" */");

        topLevelClass.addField(aggregateByClause);

        Method setAggregateByClause = new Method("setAggregateByClause");
        setAggregateByClause.setVisibility(JavaVisibility.PUBLIC);
        setAggregateByClause.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "aggregateByClause"));
        setAggregateByClause.addBodyLine("this.aggregateByClause = aggregateByClause;");
        setAggregateByClause.addJavaDocLine("/**");
        setAggregateByClause.addJavaDocLine(" * @param aggregateByClause aggregate query 语句, 注意未做防注入处理");
        setAggregateByClause.addJavaDocLine(" */");
        topLevelClass.addMethod(setAggregateByClause);

        Method getAggregateByClause = new Method("getAggregateByClause");
        getAggregateByClause.setVisibility(JavaVisibility.PUBLIC);
        getAggregateByClause.setReturnType(FullyQualifiedJavaType.getStringInstance());
        getAggregateByClause.addBodyLine("return aggregateByClause;");
        topLevelClass.addMethod(getAggregateByClause);

        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        interfaze.addImportedType(new FullyQualifiedJavaType("java.util.Map"));
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

        Method method3 = new Method(CLIENT_METHOD_NAME_AGGREGATE_QUERY);
        method3.addJavaDocLine("/**");
        method3.addJavaDocLine(" * 聚合查询");
        method3.addJavaDocLine(" */");
        method3.setAbstract(true);
        method3.addParameter(new Parameter(new FullyQualifiedJavaType(introspectedTable.getExampleType()), "example"));
        method3.setReturnType(new FullyQualifiedJavaType("List<Map<String, Object>>"));
        interfaze.addMethod(method3);

        Method method4 = new Method(CLIENT_METHOD_NAME_AGGREGATE_STATISTICS);
        method4.addJavaDocLine("/**");
        method4.addJavaDocLine(" * 聚合统计,sum、count、max、min 等");
        method4.addJavaDocLine(" */");
        method4.setAbstract(true);
        method4.addParameter(new Parameter(new FullyQualifiedJavaType(introspectedTable.getExampleType()), "example"));
        method4.setReturnType(new FullyQualifiedJavaType("Map<String, Object>"));
        interfaze.addMethod(method4);

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

        XmlElement orderByElement = new XmlElement("if");
        orderByElement.addAttribute(new Attribute("test", "orderByClause != null"));
        orderByElement.addElement(new TextElement("order by ${orderByClause}"));
        selectElement.addElement(orderByElement);

        parentElement.addElement(selectElement);


        XmlElement selectElement2 = new XmlElement("select");
        selectElement2.addAttribute(new Attribute("id", CLIENT_METHOD_NAME_IDS));
        selectElement2.addAttribute(new Attribute("parameterType", introspectedTable.getExampleType()));
        selectElement2.addAttribute(new Attribute("resultType", "java.lang.Long"));
        selectElement2.addElement(new TextElement("select "));
        selectElement2.addElement(new TextElement("id "));
        selectElement2.addElement(new TextElement("from " + tableName));
        selectElement2.addElement(new TextElement("<include refid=\"Example_Where_Clause\" />"));
        selectElement2.addElement(orderByElement);
        parentElement.addElement(selectElement2);

        XmlElement selectElement3 = new XmlElement("select");
        selectElement3.addAttribute(new Attribute("id", CLIENT_METHOD_NAME_AGGREGATE_QUERY));
        selectElement3.addAttribute(new Attribute("parameterType", introspectedTable.getExampleType()));
        selectElement3.addAttribute(new Attribute("resultType", "java.util.HashMap"));
        selectElement3.addElement(new TextElement("select "));
        selectElement3.addElement(new TextElement("${commaSeparatedColumns} "));
        selectElement3.addElement(new TextElement("from " + tableName));
        selectElement3.addElement(new TextElement("<include refid=\"Example_Where_Clause\" />"));
        selectElement3.addElement(new TextElement("${aggregateByClause} "));
        selectElement3.addElement(orderByElement);
        parentElement.addElement(selectElement3);

        XmlElement selectElement4 = new XmlElement("select");
        selectElement4.addAttribute(new Attribute("id", CLIENT_METHOD_NAME_AGGREGATE_STATISTICS));
        selectElement4.addAttribute(new Attribute("parameterType", introspectedTable.getExampleType()));
        selectElement4.addAttribute(new Attribute("resultType", "java.util.HashMap"));
        selectElement4.addElement(new TextElement("select "));
        selectElement4.addElement(new TextElement("${commaSeparatedColumns} "));
        selectElement4.addElement(new TextElement("from " + tableName));
        selectElement4.addElement(new TextElement("<include refid=\"Example_Where_Clause\" />"));
        selectElement4.addElement(new TextElement("${aggregateByClause} "));
        selectElement4.addElement(orderByElement);
        parentElement.addElement(selectElement4);

        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

}
