package com.revengemission.plugins.mybatis;


import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractXmbgPlugin extends PluginAdapter {


    protected void generateTextBlockAppendTableName(String text, IntrospectedTable introspectedTable, XmlElement parent) {
        StringBuilder sb = new StringBuilder();
        sb.append(text);
        sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
        parent.addElement(new TextElement(sb.toString()));
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


    public String upperCaseFirstChar(final String str) {
        if (Character.isUpperCase(str.charAt(0))) {
            return str;
        } else {
            return (new StringBuilder()).append(Character.toUpperCase(str.charAt(0))).append(str.substring(1)).toString();
        }
    }

    public String lowerCaseFirstChar(String s) {
        if (Character.isLowerCase(s.charAt(0))) {
            return s;
        } else {
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
        }
    }

    protected Element replaceElement(XmlElement element, Map<String, String> todo) {
        if (todo != null && todo.size() > 0) {
            Map<Integer, Element> tobeReplaced = new LinkedHashMap<>();

            for (int i = 0; i < element.getElements().size(); i++) {
                if (element.getElements().get(i) instanceof TextElement) {
                    TextElement element1 = (TextElement) element.getElements().get(i);
                    final Integer tempIndex = i;
                    todo.forEach((k, v) -> {
                        if (element1.getContent().indexOf(k) >= 0) {
                            if (element1.getContent().indexOf(",") > 0) {
                                tobeReplaced.put(tempIndex, new TextElement(v + ","));
                            } else {
                                tobeReplaced.put(tempIndex, new TextElement(v));
                            }

                        }

                    });
                } else {
                    XmlElement xmlElement = (XmlElement) element.getElements().get(i);
                    replaceElement(xmlElement, todo);
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

    protected String tableNameToEntityName(String tableName) {
        return upperCaseFirstChar(camelName(tableName));
    }

    public String underlineName(String name) {
        StringBuilder result = new StringBuilder();
        if (name != null && name.length() > 0) {
            result.append(name, 0, 1);
            for (int i = 1; i < name.length(); i++) {
                String s = name.substring(i, i + 1);
                if (s.equals(s.toUpperCase()) && !Character.isDigit(s.charAt(0))) {
                    result.append("_");
                }
                result.append(s.toLowerCase());
            }
        }
        return result.toString();
    }

    public String camelName(String name) {
        StringBuilder result = new StringBuilder();
        if (name == null || name.isEmpty()) {
            return "";
        } else if (!name.contains("_")) {
            return name.substring(0, 1).toLowerCase() + name.substring(1);
        }
        String[] camels = name.split("_");
        for (String camel : camels) {
            if (camel.isEmpty()) {
                continue;
            }
            if (result.length() == 0) {
                result.append(camel.toLowerCase());
            } else {
                result.append(camel.substring(0, 1).toUpperCase());
                result.append(camel.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }

    String getTableName(IntrospectedTable introspectedTable) {
        return introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime();
    }

    String getEntityName(IntrospectedTable introspectedTable) {
        String objectName = introspectedTable.getTableConfiguration().getDomainObjectName();

        if (objectName == null || "".equals(objectName.trim())) {
            objectName = tableNameToEntityName(getTableName(introspectedTable));
        }
        return objectName;
    }

    String getTableColumnName(IntrospectedColumn introspectedColumn) {
        return introspectedColumn.getActualColumnName();
    }
}
