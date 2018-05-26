package com.revengemission.plugins.mybatis;


import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractXmbgPlugin extends PluginAdapter {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AbstractXmbgPlugin.class);

  /*---------------------
   * Generate Helper Method For Xml Mapper
   *---------------------*/

    protected void generateTextBlock(String text, XmlElement parent) {
        parent.addElement(new TextElement(text));
    }

    protected void generateTextBlockAppendTableName(String text, IntrospectedTable introspectedTable, XmlElement parent) {
        StringBuilder sb = new StringBuilder();
        sb.append(text);
        sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
        parent.addElement(new TextElement(sb.toString()));
    }


    protected void generateParameterForSet(String fieldPrefix, List<IntrospectedColumn> columns, XmlElement dynamicElement) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < columns.size(); i++) {
            IntrospectedColumn introspectedColumn = columns.get(i);
            sb.setLength(0);

            if (StringUtils.equals(introspectedColumn.getActualColumnName(), "version")) {
                sb.append("  version = version+1");
            } else if (StringUtils.equals(introspectedColumn.getActualColumnName(), "last_modified")) {
                sb.append("  last_modified = now()");
            } else if (StringUtils.equals(introspectedColumn.getActualColumnName(), "last_modified")) {
                sb.append("  modified_date = now()");
            } else {
                sb.append("  ");
                sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn));
                sb.append(" = ");
                sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, fieldPrefix));
            }
            if (i != columns.size() - 1) {
                sb.append(',');
            }
            TextElement tempElement = new TextElement(sb.toString());
            dynamicElement.addElement(tempElement);
        }


    }

    protected void generateParameterForSet(String fieldPrefix, boolean ifNullCheck, List<IntrospectedColumn> columns, XmlElement dynamicElement) {
        XmlElement trimElement = new XmlElement("trim");
        trimElement.addAttribute(new Attribute("suffixOverrides", ","));

        StringBuilder sb = new StringBuilder();
        for (IntrospectedColumn introspectedColumn : columns) {
            sb.setLength(0);
            sb.append("  ");
            sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, fieldPrefix));
            sb.append(',');

            doIfNullCheck(fieldPrefix, ifNullCheck, trimElement, sb, introspectedColumn);
        }

        dynamicElement.addElement(trimElement);
    }

    protected void doIfNullCheck(String fieldPrefix, boolean ifNullCheck, XmlElement trimElement, StringBuilder sb, IntrospectedColumn introspectedColumn) {
        Element content;
        if (ifNullCheck) {
            content = wrapIfNullCheckForJavaProperty(fieldPrefix, new TextElement(sb.toString()), introspectedColumn);
        } else {
            content = new TextElement(sb.toString());
        }
        trimElement.addElement(content);
    }

    protected XmlElement wrapIfNullCheckForJavaProperty(String fieldPrefix, Element content, IntrospectedColumn introspectedColumn) {
        StringBuilder sb = new StringBuilder();
        XmlElement isNotNullElement = new XmlElement("if");
        sb.setLength(0);
        sb.append(introspectedColumn.getJavaProperty(fieldPrefix));
        sb.append(" != null");
        isNotNullElement.addAttribute(new Attribute("test", sb.toString()));
        isNotNullElement.addElement(content);
        return isNotNullElement;
    }

    protected void generateParametersSeparateByComma(List<IntrospectedColumn> columns, XmlElement parent) {
        generateParametersSeparateByComma("", false, columns, parent);
    }

    protected void generateParametersSeparateByComma(String fieldPrefix, List<IntrospectedColumn> columns, XmlElement parent) {
        generateParametersSeparateByComma(fieldPrefix, false, columns, parent);
    }

    protected void generateParametersSeparateByComma(String fieldPrefix, boolean ifNullCheck, List<IntrospectedColumn> columns, XmlElement parent) {
        generateParametersSeparateByComma(fieldPrefix, ifNullCheck, false, columns, parent);
    }

    protected void generateParametersSeparateByComma(String fieldPrefix, boolean ifNullCheck, boolean withParenthesis, List<IntrospectedColumn> columns, XmlElement parent) {
        XmlElement trimElement = new XmlElement("trim");
        trimElement.addAttribute(new Attribute("suffixOverrides", ","));
        if (withParenthesis) {
            trimElement.addAttribute(new Attribute("prefix", "("));
            trimElement.addAttribute(new Attribute("suffix", ")"));
        }

        StringBuilder sb = new StringBuilder();
        for (IntrospectedColumn introspectedColumn : columns) {
            sb.setLength(0);
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, fieldPrefix));
            sb.append(",");

            doIfNullCheck(fieldPrefix, ifNullCheck, trimElement, sb, introspectedColumn);
        }
        parent.addElement(trimElement);
    }

    protected void generateParametersSeparateByCommaWithParenthesis(List<IntrospectedColumn> columns, XmlElement parent) {
        generateParametersSeparateByCommaWithParenthesis("", false, columns, parent);
    }

    protected void generateParametersSeparateByCommaWithParenthesis(String fieldPrefix, List<IntrospectedColumn> columns, XmlElement parent) {
        generateParametersSeparateByCommaWithParenthesis(fieldPrefix, false, columns, parent);
    }

    protected void generateParametersSeparateByCommaWithParenthesis(String fieldPrefix, boolean ifNullCheck, List<IntrospectedColumn> columns, XmlElement parent) {
        generateParametersSeparateByComma(fieldPrefix, ifNullCheck, true, columns, parent);
    }

    protected void generateActualColumnNamesWithParenthesis(List<IntrospectedColumn> columns, XmlElement parent) {
        generateActualColumnNamesWithParenthesis("", false, columns, parent);
    }

    protected void generateActualColumnNamesWithParenthesis(String fieldPrefix, boolean ifNullCheck, List<IntrospectedColumn> columns, XmlElement parent) {
        generateActualColumnNamesWithParenthesis(fieldPrefix, null, ifNullCheck, columns, parent);
    }

    protected void generateActualColumnNamesWithParenthesis(String fieldPrefix, String columnPrefix, boolean ifNullCheck, List<IntrospectedColumn> columns, XmlElement parent) {
        XmlElement trimElement = new XmlElement("trim");
        trimElement.addAttribute(new Attribute("suffixOverrides", ","));
        trimElement.addAttribute(new Attribute("prefix", "("));
        trimElement.addAttribute(new Attribute("suffix", ")"));

        StringBuilder sb = new StringBuilder();
        for (IntrospectedColumn introspectedColumn : columns) {
            sb.setLength(0);
            sb.append(columnPrefix == null ? "" : columnPrefix);
            sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn));
            sb.append(",");

            doIfNullCheck(fieldPrefix, ifNullCheck, trimElement, sb, introspectedColumn);
        }

        parent.addElement(trimElement);
    }

    protected void generateWhereConditions(String fieldPrefix, List<IntrospectedColumn> columns, XmlElement parent) {
        generateWhereConditions(fieldPrefix, false, columns, parent);
    }

    protected void generateWhereConditions(String fieldPrefix, boolean ifNullCheck, List<IntrospectedColumn> columns, XmlElement parent) {
        generateWhereConditions(fieldPrefix, null, ifNullCheck, columns, parent);
    }

    protected void generateWhereConditions(String fieldPrefix, String columnPrefix, boolean ifNullCheck, List<IntrospectedColumn> columns, XmlElement parent) {
        XmlElement trimElement = new XmlElement("trim");
        trimElement.addAttribute(new Attribute("suffixOverrides", ","));

        StringBuilder sb = new StringBuilder();
        for (IntrospectedColumn introspectedColumn : columns) {
            sb.setLength(0);
            sb.append(columnPrefix == null ? "" : columnPrefix);
            sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, fieldPrefix));
            sb.append(",");

            doIfNullCheck(fieldPrefix, ifNullCheck, trimElement, sb, introspectedColumn);
        }

        XmlElement where = new XmlElement("where");
        where.addElement(trimElement);
        parent.addElement(where);
    }

  /*---------------
   * Generate Helper Method For Java Source
   **--------------*/

    protected void generateGetterFor(String field, FullyQualifiedJavaType type, InnerClass innerClass) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("get" + capitalize(field));
        method.setReturnType(type);
        method.addBodyLine("return this." + field + ";");
        innerClass.addMethod(method);
    }

    protected void generateSetterFor(String field, FullyQualifiedJavaType type, InnerClass innerClass) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("set" + capitalize(field));
        method.addParameter(new Parameter(type, field));
        method.addBodyLine("this." + field + " = " + field + ";");
        innerClass.addMethod(method);
    }

    protected void generateGetterSetterFor(String field, FullyQualifiedJavaType type, InnerClass innerClass) {
        generateSetterFor(field, type, innerClass);
        generateGetterFor(field, type, innerClass);
    }

    protected Field generateFieldDeclarationFor(String field, FullyQualifiedJavaType type, InnerClass innerClass) {
        Field ff = new Field();
        ff.setVisibility(JavaVisibility.PROTECTED);
        ff.setType(type);
        ff.setName(field);
        innerClass.addField(ff);
        return ff;
    }

    protected Field generateFieldDeclarationWithGetterSetterFor(String field, FullyQualifiedJavaType type, InnerClass innerClass) {
        generateGetterSetterFor(field, type, innerClass);
        return generateFieldDeclarationFor(field, type, innerClass);
    }

    protected Field generateFieldDeclarationWithFluentApiFor(String field, FullyQualifiedJavaType type, InnerClass innerClass) {
        generateFluentGetterFor(field, type, innerClass);
        generateFluentSetterFor(field, type, innerClass);
        return generateFieldDeclarationFor(field, type, innerClass);
    }

    protected void generateFluentSetterFor(String field, FullyQualifiedJavaType type, InnerClass innerClass) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName(field);
        method.addParameter(new Parameter(type, field));
        method.addBodyLine("this." + field + " = " + field + ";");
        method.addBodyLine("return this;");
        method.setReturnType(innerClass.getType());
        innerClass.addMethod(method);
    }

    protected void generateFluentGetterFor(String field, FullyQualifiedJavaType type, InnerClass innerClass) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName(field);
        method.addBodyLine("return this." + field + ";");
        method.setReturnType(type);
        innerClass.addMethod(method);
    }

    protected void generateBuilderFor(TopLevelClass topLevelClass) {
        if (topLevelClass == null || topLevelClass.getFields() == null || topLevelClass.getFields().isEmpty()) return;
        generateBuilderFor(topLevelClass.getType().getShortName(), topLevelClass, topLevelClass.getFields());
    }

    protected void generateBuilderFor(String builderName, TopLevelClass topLevelClass, List<Field> fields) {
        if (fields == null || fields.isEmpty()) return;

        FullyQualifiedJavaType builderType = new FullyQualifiedJavaType(builderName);

        InnerClass builder = new InnerClass(builderType);
        builder.setVisibility(JavaVisibility.PUBLIC);
        builder.setStatic(false);

        // build method and builder field
        Method method = new Method();
        method.setName("build");
        method.setVisibility(JavaVisibility.PUBLIC);

        for (Field field : fields) {
            generateFieldDeclarationWithFluentApiFor(field.getName(), field.getType(), builder);
            method.addBodyLine(topLevelClass.getType().getShortName() + ".this." + field.getName() + " = " + field.getName() + ";");
        }

        method.addBodyLine("return " + topLevelClass.getType().getShortName() + ".this;");
        method.setReturnType(topLevelClass.getType());
        builder.addMethod(method);

        // builder owner's builder()
        method = new Method();
        method.setName(uncapitalize(builderName));
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addBodyLine("return new " + builderName + "();");
        method.setReturnType(builderType);
        topLevelClass.addMethod(method);

        topLevelClass.addInnerClass(builder);
    }


    public String capitalize(final String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }

        char firstChar = str.charAt(0);
        if (Character.isTitleCase(firstChar)) {
            // already capitalized
            return str;
        }

        return new StringBuilder(strLen)
                .append(Character.toTitleCase(firstChar))
                .append(str.substring(1))
                .toString();
    }

    public String uncapitalize(final String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }

        char firstChar = str.charAt(0);
        if (Character.isLowerCase(firstChar)) {
            // already uncapitalized
            return str;
        }

        return new StringBuilder(strLen)
                .append(Character.toLowerCase(firstChar))
                .append(str.substring(1))
                .toString();
    }

    //首字母转小写
    public String toLowerCaseFirstChar(String s) {
        if (Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    protected Element replaceElement(XmlElement element, Map<String, String> todo) {
        if (todo != null && todo.size() > 0) {
            Map<Integer, Element> tobeReplaced = new LinkedHashMap<>();

            for (int i = 0; i < element.getElements().size(); i++) {
                if (element.getElements().get(i) instanceof TextElement) {
                    TextElement element1 = (TextElement) element.getElements().get(i);
                    final Integer tempIndex = i;
                    todo.forEach((k, v) -> {
                        if (StringUtils.contains(element1.getContent(), k)) {
                            logger.info("match ===============" + k);
                            if (StringUtils.indexOf(element1.getContent(), ",") > 0) {
                                tobeReplaced.put(tempIndex, new TextElement(v + ","));
                            } else {
                                tobeReplaced.put(tempIndex, new TextElement(v));
                            }

                        }

                    });
                }


            }
            if (tobeReplaced.size() > 0) {
                tobeReplaced.forEach((k, v) -> {
                    element.getElements().remove(k.intValue());
                    element.getElements().add(k, v);
                });
            }

        }
        return element;
    }

    private String tableNameToEntityName(String tableName) {
        StringBuilder result = new StringBuilder();
        if (tableName == null || tableName.isEmpty()) {
            return "";
        } else if (!tableName.contains("_")) {
            return tableName;
        }
        String camels[] = tableName.split("_");
        for (String camel : camels) {
            if (camel.isEmpty()) {
                continue;
            }
            result.append(camel.substring(0, 1).toUpperCase());
            result.append(camel.substring(1));
        }
        return result.toString();
    }

    protected String getTableName(IntrospectedTable introspectedTable) {
        return introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime();
    }

    protected String getEntityName(IntrospectedTable introspectedTable) {
        String objectName = introspectedTable.getTableConfiguration().getDomainObjectName();

        if (StringUtils.isEmpty(objectName)) {
            objectName = tableNameToEntityName(getTableName(introspectedTable));
        }
        return objectName;
    }
}
