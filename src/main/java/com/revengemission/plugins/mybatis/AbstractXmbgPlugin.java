package com.revengemission.plugins.mybatis;


import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.VisitableElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class AbstractXmbgPlugin extends PluginAdapter {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(AbstractXmbgPlugin.class);

    protected void generateTextBlockAppendTableName(String text, IntrospectedTable introspectedTable, XmlElement parent) {
        StringBuilder sb = new StringBuilder();
        sb.append(text);
        sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
        parent.addElement(new TextElement(sb.toString()));
    }


    protected void doIfNullCheck(String fieldPrefix, boolean ifNullCheck, XmlElement trimElement, StringBuilder sb, IntrospectedColumn introspectedColumn) {
        VisitableElement content;
        if (ifNullCheck) {
            content = wrapIfNullCheckForJavaProperty(fieldPrefix, new TextElement(sb.toString()), introspectedColumn);
        } else {
            content = new TextElement(sb.toString());
        }
        trimElement.addElement(content);
    }

    protected XmlElement wrapIfNullCheckForJavaProperty(String fieldPrefix, TextElement content, IntrospectedColumn introspectedColumn) {
        StringBuilder sb = new StringBuilder();
        XmlElement isNotNullElement = new XmlElement("if");
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
            return Character.toUpperCase(str.charAt(0)) + str.substring(1);
        }
    }

    public String lowerCaseFirstChar(String s) {
        if (Character.isLowerCase(s.charAt(0))) {
            return s;
        } else {
            return Character.toLowerCase(s.charAt(0)) + s.substring(1);
        }
    }

    protected VisitableElement replaceElement(XmlElement element, Map<String, String> replacement) {
        if (replacement != null && !replacement.isEmpty()) {
            Map<Integer, VisitableElement> tobeReplaced = new LinkedHashMap<>();

            for (int i = 0; i < element.getElements().size(); i++) {
                if (element.getElements().get(i) instanceof TextElement) {
                    TextElement element1 = (TextElement) element.getElements().get(i);
                    final Integer tempIndex = i;
                    replacement.forEach((k, v) -> {
                        String elementContent = element1.getContent();
                        if (elementContent.contains(k)) {
                            String newContent = elementContent.replace(k, v);
                            tobeReplaced.put(tempIndex, new TextElement(newContent));
                        }

                    });
                } else {
                    XmlElement xmlElement = (XmlElement) element.getElements().get(i);
                    replaceElement(xmlElement, replacement);
                }
            }
            if (!tobeReplaced.isEmpty()) {
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
        if (name != null && !name.isEmpty()) {
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
            if (result.isEmpty()) {
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

        if (objectName == null || objectName.trim().isEmpty()) {
            objectName = tableNameToEntityName(getTableName(introspectedTable));
        }
        return objectName;
    }

    String getTableColumnName(IntrospectedColumn introspectedColumn) {
        return introspectedColumn.getActualColumnName();
    }

    XmlElement findFirstMatchedXmlElement(XmlElement element, String xmlElementTag) {
        for (int i = 0; i < element.getElements().size(); i++) {
            if (element.getElements().get(i) instanceof XmlElement) {
                XmlElement child = (XmlElement) element.getElements().get(i);
                if (child.getName().equalsIgnoreCase(xmlElementTag)) {
                    return child;
                } else {
                    return findFirstMatchedXmlElement(child, xmlElementTag);
                }
            }
        }
        return null;
    }

    /**
     * get UniqueConstraintKeys
     *
     * @param introspectedTable
     * @return
     */
    Map<String, List<String>> getUniqueConstraintKeys(IntrospectedTable introspectedTable) {

        Map<String, List<String>> uniqueConstraintMap = new HashMap<>();
        try (Connection connection = context.getConnection()) {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            ResultSet rs = databaseMetaData.getIndexInfo(null, null, getTableName(introspectedTable), true, false);
            while (rs.next()) {
                String ascOrDesc = rs.getString("ASC_OR_DESC");
                int cardinality = rs.getInt("CARDINALITY");
                short ordinalPosition = rs.getShort("ORDINAL_POSITION");
                boolean nonUnique = rs.getBoolean("NON_UNIQUE");
                String indexQualifier = rs.getString("INDEX_QUALIFIER");
                String indexName = rs.getString("INDEX_NAME");
                short indexType = rs.getShort("TYPE");
                String columnName = rs.getString("COLUMN_NAME");
                log.info(" {} {}", indexName, columnName);
                List<String> list;
                if (uniqueConstraintMap.containsKey(indexName)) {
                    list = new ArrayList<>(uniqueConstraintMap.get(indexName));
                } else {
                    list = new ArrayList<>();
                }
                list.add(columnName);
                uniqueConstraintMap.put(indexName, list);
            }
        } catch (SQLException e) {
            log.error("SqlException in my plugin", e);
        }
        return uniqueConstraintMap;
    }

    List<String> getPrimaryKeys(IntrospectedTable introspectedTable) {
        List<String> primaryKeys = new ArrayList<>();
        try (Connection connection = context.getConnection()) {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            ResultSet rs = databaseMetaData.getPrimaryKeys(null, null, getTableName(introspectedTable));
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                primaryKeys.add(columnName);
            }
        } catch (SQLException e) {
            log.error("SqlException in my plugin", e);
        }
        return primaryKeys;
    }

    Map<String, List<ForeignKeyItem>> foreignKeysCacheMap = new HashMap<>();
    
    List<ForeignKeyItem> getForeignKeys(IntrospectedTable introspectedTable) {
        String tableName = getTableName(introspectedTable);
        if (foreignKeysCacheMap.containsKey(tableName)) {
            return foreignKeysCacheMap.get(tableName);
        }
        List<ForeignKeyItem> foreignKeyItemList = new LinkedList<>();
        try (Connection connection = context.getConnection()) {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            ResultSet importedKeyssResultSet = databaseMetaData.getImportedKeys(null, null, getTableName(introspectedTable));
            while (importedKeyssResultSet.next()) {
                String fkName = importedKeyssResultSet.getString("FK_NAME");
                String fkTableName = importedKeyssResultSet.getString("FKTABLE_NAME");
                String fkColumnName = importedKeyssResultSet.getString("FKCOLUMN_NAME");
                String pkTableName = importedKeyssResultSet.getString("PKTABLE_NAME");
                String pkColumnName = importedKeyssResultSet.getString("PKCOLUMN_NAME");
                ForeignKeyItem foreignKeyItem = new ForeignKeyItem(fkName, fkTableName, fkColumnName, pkTableName, pkColumnName);
                foreignKeyItemList.add(foreignKeyItem);
            }
        } catch (SQLException e) {
            log.error("SqlException in my plugin", e);
        }
        foreignKeysCacheMap.put(tableName, foreignKeyItemList);
        return foreignKeyItemList;
    }
    
    Map<String, Map<String, String>> tableColumnsCacheMap = new HashMap<>();

    Map<String, String> getTableColumns(String tableName) {
        if (tableColumnsCacheMap.containsKey(tableName)) {
            return tableColumnsCacheMap.get(tableName);
        }
        Map<String, String> columnsRemarkMap = new LinkedHashMap<>();
        try (Connection connection = context.getConnection()) {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            ResultSet columnsResultSet = databaseMetaData.getColumns(null, null, tableName, "%");
            while (columnsResultSet.next()) {
                String columnName = columnsResultSet.getString("COLUMN_NAME");
                String dataType = columnsResultSet.getString("TYPE_NAME");
                String remarks = columnsResultSet.getString("REMARKS");
                columnsRemarkMap.put(columnName, remarks);
                log.info("Column Name: {}, Data Type: {}, REMARKS: {}", columnName, dataType, remarks);
            }
        } catch (SQLException e) {
            log.error("SqlException in my plugin", e);
        }
        tableColumnsCacheMap.put(tableName, columnsRemarkMap);
        return columnsRemarkMap;
    }
}
