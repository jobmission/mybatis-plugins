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
import org.mybatis.generator.api.dom.xml.VisitableElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 插入数据时，重复键更新
 */
public class InsertOnUpdatePlugin extends AbstractXmbgPlugin {

    private static final Logger log = LoggerFactory.getLogger(InsertOnUpdatePlugin.class);

    private static final String CLIENT_METHOD_NAME_SINGLE = "insertOnUpdate";
    private static final String CLIENT_METHOD_NAME_BATCH = "batchInsertOnUpdate";

    private static final String PROPERTY_PREFIX = "item.";

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        String tableName = getTableName(introspectedTable);
        log.info("enter initialized {}", tableName);
    }

    @Override
    public boolean validate(List<String> list) {
        log.info("enter validate {}", list == null ? 0 : list.size());
        return true;
    }

    //uniqueFields=a,b;updateFields=c,d,e,f
    protected List<String> getUniqueFields(String value) {
        List<String> uniqueFields = new ArrayList<>();
        String[] strings = value.trim().split(";");
        for (String s : strings) {
            if (s.startsWith("uniqueFields")) {
                String[] fields = s.split("=");
                if (fields.length == 2) {
                    String[] fieldsArray = fields[1].split(",");
                    for (String field : fieldsArray) {
                        if (!uniqueFields.contains(field.trim())) {
                            uniqueFields.add(field.trim());
                        }
                    }
                }
            }
        }
        return uniqueFields;
    }

    //updateFields=a,b;updateFields=c,d,e,f
    protected Set<String> getUpdateFields(String value) {
        Set<String> updateFields = new LinkedHashSet<>();
        String[] strings = value.trim().split(";");
        for (String s : strings) {
            if (s.startsWith("updateFields")) {
                String[] fields = s.split("=");
                if (fields.length == 2) {
                    String[] fieldsArray = fields[1].split(",");
                    for (String field : fieldsArray) {
                        updateFields.add(field.trim());
                    }
                }
            }
        }
        return updateFields;
    }

    //uniqueFields=code;updateIgnoreFields=a,b;updateFields=c,d,e,f
    protected Set<String> getUpdateIgnoreFields(String value) {
        Set<String> updateIgnoreFields = new LinkedHashSet<>();
        String[] strings = value.trim().split(";");
        for (String s : strings) {
            if (s.startsWith("updateIgnoreFields")) {
                String[] fields = s.split("=");
                if (fields.length == 2) {
                    String[] fieldsArray = fields[1].split(",");
                    for (String field : fieldsArray) {
                        updateIgnoreFields.add(field.trim());
                    }
                }
            }
        }
        if (updateIgnoreFields.isEmpty()) {
            updateIgnoreFields.add("id");
            updateIgnoreFields.add("deleted");
            updateIgnoreFields.add("record_status");
            updateIgnoreFields.add("sort_priority");
            updateIgnoreFields.add("remark");
            updateIgnoreFields.add("date_created");
        }
        return updateIgnoreFields;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        String currentTableName = getTableName(introspectedTable);
        log.info("enter clientGenerated table {}", currentTableName);
        String objectName = getEntityName(introspectedTable);

        Method methodSingle = new Method(CLIENT_METHOD_NAME_SINGLE);
        methodSingle.setAbstract(true);
        methodSingle.addParameter(new Parameter(new FullyQualifiedJavaType(objectName), "row"));
        methodSingle.setReturnType(FullyQualifiedJavaType.getIntInstance());
        interfaze.addMethod(methodSingle);

        Method methodBatch = new Method(CLIENT_METHOD_NAME_BATCH);
        methodBatch.setAbstract(true);
        FullyQualifiedJavaType type = new FullyQualifiedJavaType("java.util.List<" + objectName + ">");
        methodBatch.addParameter(new Parameter(type, "list"));
        methodBatch.setReturnType(FullyQualifiedJavaType.getIntInstance());
        interfaze.addMethod(methodBatch);
        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        String currentTableName = getTableName(introspectedTable);
        log.info("enter sqlMapDocumentGenerated table {}", currentTableName);
        AtomicReference<Boolean> findFlag = new AtomicReference<>(false);
        properties.forEach((k, v) -> {
            if (currentTableName.equalsIgnoreCase(k.toString().trim())) {
                findFlag.set(true);
                List<String> uniqueFields = getUniqueFields((String) v);

                List<IntrospectedColumn> notAutoIncrementColumnList = introspectedTable.getAllColumns().stream().filter(introspectedColumn -> !introspectedColumn.isAutoIncrement()).toList();

                TextElement mysqlOnUpdateElement = new TextElement("ON DUPLICATE KEY UPDATE");
                TextElement postgresqlOnConflictElement = new TextElement("ON CONFLICT (" + String.join(", ", uniqueFields) + ")");
                XmlElement insertXmlElement = new XmlElement("insert");
                insertXmlElement.addAttribute(new Attribute("id", CLIENT_METHOD_NAME_SINGLE));
                insertXmlElement.addAttribute(new Attribute("parameterType", context.getJavaModelGeneratorConfiguration().getTargetPackage() + "." + getEntityName(introspectedTable)));

                generateTextBlockAppendTableName("insert into ", introspectedTable, insertXmlElement);

                generateActualColumnNamesWithParenthesis(notAutoIncrementColumnList, insertXmlElement);

                insertXmlElement.addElement(new TextElement("values "));


                generateParametersSeparateByCommaWithParenthesis("", notAutoIncrementColumnList, insertXmlElement);
                XmlElement mysqlIfElement = new XmlElement("if");
                mysqlIfElement.addAttribute(new Attribute("test", "_databaseId == 'mysql'"));

                ///mysqlIfElement.addElement(new TextElement("AS newRowValue (" + getFieldsString(notAutoIncrementColumnList, "_new") + ")"));
                mysqlIfElement.addElement(new TextElement("AS newRowValue"));

                mysqlIfElement.addElement(mysqlOnUpdateElement);

                mysqlIfElement.addElement(getMysqlUpdateClauseText(v.toString().trim(), uniqueFields, introspectedTable));
                insertXmlElement.addElement(mysqlIfElement);

                XmlElement postgresqlIfElement = new XmlElement("if");
                postgresqlIfElement.addAttribute(new Attribute("test", "_databaseId == 'postgresql' or _databaseId == 'sqlite'"));
                postgresqlIfElement.addElement(postgresqlOnConflictElement);
                postgresqlIfElement.addElement(new TextElement("DO UPDATE SET"));

                postgresqlIfElement.addElement(getPostgresqlUpdateClauseText(v.toString().trim(), uniqueFields, introspectedTable));
                insertXmlElement.addElement(postgresqlIfElement);

                document.getRootElement().addElement(insertXmlElement);


                XmlElement batchInsertXmlElement = new XmlElement("insert");
                batchInsertXmlElement.addAttribute(new Attribute("id", CLIENT_METHOD_NAME_BATCH));
                FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType("java.util.List");
                batchInsertXmlElement.addAttribute(new Attribute("parameterType", parameterType.getFullyQualifiedName()));


                XmlElement ifListElement = new XmlElement("if");
                ifListElement.addAttribute(new Attribute("test", "list != null and list.size() > 0"));
                generateTextBlockAppendTableName("insert into ", introspectedTable, ifListElement);

                generateActualColumnNamesWithParenthesis(notAutoIncrementColumnList, ifListElement);

                ifListElement.addElement(new TextElement("values "));

                XmlElement foreach = new XmlElement("foreach");
                foreach.addAttribute(new Attribute("collection", "list"));
                foreach.addAttribute(new Attribute("item", "item"));
                foreach.addAttribute(new Attribute("index", "index"));
                foreach.addAttribute(new Attribute("separator", ","));

                generateParametersSeparateByCommaWithParenthesis(PROPERTY_PREFIX, notAutoIncrementColumnList, foreach);

                ifListElement.addElement(foreach);
                XmlElement batchMysqlIfElement = new XmlElement("if");
                batchMysqlIfElement.addAttribute(new Attribute("test", "_databaseId == 'mysql'"));

                ////batchMysqlIfElement.addElement(new TextElement("AS newRowValue (" + getFieldsString(notAutoIncrementColumnList, "_new") + ")"));
                batchMysqlIfElement.addElement(new TextElement("AS newRowValue"));
                batchMysqlIfElement.addElement(mysqlOnUpdateElement);
                batchMysqlIfElement.addElement(getMysqlUpdateClauseText(v.toString().trim(), uniqueFields, introspectedTable));
                ifListElement.addElement(batchMysqlIfElement);

                XmlElement batchPostgresqlIfElement = new XmlElement("if");
                batchPostgresqlIfElement.addAttribute(new Attribute("test", "_databaseId == 'postgresql' or _databaseId == 'sqlite'"));

                batchPostgresqlIfElement.addElement(postgresqlOnConflictElement);
                batchPostgresqlIfElement.addElement(new TextElement("DO UPDATE SET"));
                batchPostgresqlIfElement.addElement(getPostgresqlUpdateClauseText(v.toString().trim(), uniqueFields, introspectedTable));
                ifListElement.addElement(batchPostgresqlIfElement);

                XmlElement ifNullElement = new XmlElement("if");
                ifNullElement.addAttribute(new Attribute("test", "list == null or list.size() == 0"));
                ifNullElement.addElement(new TextElement("select 0"));
                batchInsertXmlElement.addElement(ifNullElement);
                batchInsertXmlElement.addElement(ifListElement);
                document.getRootElement().addElement(batchInsertXmlElement);
            }
        });

        if (!findFlag.get()) {
            Map<String, List<String>> uniqueConstraintKeysMap = getUniqueConstraintKeys(introspectedTable);
            List<String> uniqueColumns = new ArrayList<>();
            List<String> pkColumns = new ArrayList<>();
            if (uniqueConstraintKeysMap != null && !uniqueConstraintKeysMap.isEmpty()) {
                uniqueConstraintKeysMap.forEach((k, v) -> {
                    if (k.startsWith("pk_") || k.startsWith("PRIMARY")) {
                        pkColumns.addAll(v);
                    } else {
                        uniqueColumns.clear();
                        uniqueColumns.addAll(v);
                    }
                });
            }
            List<String> uniqueFields = null;
            if (!uniqueColumns.isEmpty()) {
                uniqueFields = new ArrayList<>(uniqueColumns);
            } else if (!pkColumns.isEmpty()) {
                uniqueFields = new ArrayList<>(pkColumns);
            }
            if (uniqueFields != null && !uniqueFields.isEmpty()) {

                List<IntrospectedColumn> notAutoIncrementColumnList = introspectedTable.getAllColumns().stream().filter(introspectedColumn -> !introspectedColumn.isAutoIncrement()).toList();

                TextElement mysqlOnUpdateElement = new TextElement("ON DUPLICATE KEY UPDATE");
                TextElement postgresqlOnConflictElement = new TextElement("ON CONFLICT (" + String.join(", ", uniqueFields) + ")");
                XmlElement insertXmlElement = new XmlElement("insert");
                insertXmlElement.addAttribute(new Attribute("id", CLIENT_METHOD_NAME_SINGLE));
                insertXmlElement.addAttribute(new Attribute("parameterType", context.getJavaModelGeneratorConfiguration().getTargetPackage() + "." + getEntityName(introspectedTable)));

                generateTextBlockAppendTableName("insert into ", introspectedTable, insertXmlElement);

                generateActualColumnNamesWithParenthesis(notAutoIncrementColumnList, insertXmlElement);

                insertXmlElement.addElement(new TextElement("values "));


                generateParametersSeparateByCommaWithParenthesis("", notAutoIncrementColumnList, insertXmlElement);
                XmlElement mysqlIfElement = new XmlElement("if");
                mysqlIfElement.addAttribute(new Attribute("test", "_databaseId == 'mysql'"));
                mysqlIfElement.addElement(new TextElement("AS newRowValue"));

                mysqlIfElement.addElement(mysqlOnUpdateElement);

                mysqlIfElement.addElement(getMysqlUpdateClauseText("", uniqueFields, introspectedTable));
                insertXmlElement.addElement(mysqlIfElement);

                XmlElement postgresqlIfElement = new XmlElement("if");
                postgresqlIfElement.addAttribute(new Attribute("test", "_databaseId == 'postgresql' or _databaseId == 'sqlite'"));
                postgresqlIfElement.addElement(postgresqlOnConflictElement);
                postgresqlIfElement.addElement(new TextElement("DO UPDATE SET"));

                postgresqlIfElement.addElement(getPostgresqlUpdateClauseText("", uniqueFields, introspectedTable));
                insertXmlElement.addElement(postgresqlIfElement);

                document.getRootElement().addElement(insertXmlElement);


                XmlElement batchInsertXmlElement = new XmlElement("insert");
                batchInsertXmlElement.addAttribute(new Attribute("id", CLIENT_METHOD_NAME_BATCH));
                FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType("java.util.List");
                batchInsertXmlElement.addAttribute(new Attribute("parameterType", parameterType.getFullyQualifiedName()));


                XmlElement ifListElement = new XmlElement("if");
                ifListElement.addAttribute(new Attribute("test", "list != null and list.size() > 0"));
                generateTextBlockAppendTableName("insert into ", introspectedTable, ifListElement);

                generateActualColumnNamesWithParenthesis(notAutoIncrementColumnList, ifListElement);

                ifListElement.addElement(new TextElement("values "));

                XmlElement foreach = new XmlElement("foreach");
                foreach.addAttribute(new Attribute("collection", "list"));
                foreach.addAttribute(new Attribute("item", "item"));
                foreach.addAttribute(new Attribute("index", "index"));
                foreach.addAttribute(new Attribute("separator", ","));

                generateParametersSeparateByCommaWithParenthesis(PROPERTY_PREFIX, notAutoIncrementColumnList, foreach);

                ifListElement.addElement(foreach);
                XmlElement batchMysqlIfElement = new XmlElement("if");
                batchMysqlIfElement.addAttribute(new Attribute("test", "_databaseId == 'mysql'"));

                ////batchMysqlIfElement.addElement(new TextElement("AS newRowValue (" + getFieldsString(notAutoIncrementColumnList, "_new") + ")"));
                batchMysqlIfElement.addElement(new TextElement("AS newRowValue"));
                batchMysqlIfElement.addElement(mysqlOnUpdateElement);
                batchMysqlIfElement.addElement(getMysqlUpdateClauseText("", uniqueFields, introspectedTable));
                ifListElement.addElement(batchMysqlIfElement);

                XmlElement batchPostgresqlIfElement = new XmlElement("if");
                batchPostgresqlIfElement.addAttribute(new Attribute("test", "_databaseId == 'postgresql' or _databaseId == 'sqlite'"));

                batchPostgresqlIfElement.addElement(postgresqlOnConflictElement);
                batchPostgresqlIfElement.addElement(new TextElement("DO UPDATE SET"));
                batchPostgresqlIfElement.addElement(getPostgresqlUpdateClauseText("", uniqueFields, introspectedTable));
                ifListElement.addElement(batchPostgresqlIfElement);

                XmlElement ifNullElement = new XmlElement("if");
                ifNullElement.addAttribute(new Attribute("test", "list == null or list.size() == 0"));
                ifNullElement.addElement(new TextElement("select 0"));
                batchInsertXmlElement.addElement(ifNullElement);
                batchInsertXmlElement.addElement(ifListElement);
                document.getRootElement().addElement(batchInsertXmlElement);

            }

        }

        return true;
    }

    private VisitableElement getMysqlUpdateClauseText(String v, List<String> uniqueFields, IntrospectedTable introspectedTable) {
        String tableName = getTableName(introspectedTable);
        Set<String> updateFields = getUpdateFields(v);
        Set<String> igonreSet = getUpdateIgnoreFields(v);
        XmlElement trimElement = new XmlElement("trim");
        trimElement.addAttribute(new Attribute("suffixOverrides", ","));

        if (updateFields.isEmpty()) {
            for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
                if (introspectedColumn.isAutoIncrement()) {
                    continue;
                }
                String columnName = introspectedColumn.getActualColumnName();
                if (uniqueFields.contains(columnName)) {
                    continue;
                }
                if ("version".equals(columnName)) {
                    trimElement.addElement(new TextElement("version = " + tableName + ".version + 1,"));
                } else if ("last_modified".equals(columnName)) {
                    trimElement.addElement(new TextElement("last_modified = now(),"));
                } else if (!igonreSet.contains(columnName)) {
                    trimElement.addElement(new TextElement(columnName + " = newRowValue." + columnName + ","));
                }
            }
        } else {
            StringBuilder sb = new StringBuilder();
            for (String updateField : updateFields) {
                if ("version".equals(updateField)) {
                    sb.append(", ").append("version = ").append(tableName).append(".version + 1");
                } else if ("last_modified".equals(updateField)) {
                    sb.append(", ").append("last_modified = now()");
                } else {
                    sb.append(", ").append(updateField).append(" = newRowValue.").append(updateField);
                }
            }
            trimElement.addElement(new TextElement(sb.toString().replaceFirst(", ", "")));
        }

        return trimElement;
    }

    private VisitableElement getPostgresqlUpdateClauseText(String v, List<String> uniqueFields, IntrospectedTable introspectedTable) {
        String tableName = getTableName(introspectedTable);
        Set<String> updateFields = getUpdateFields(v);
        Set<String> igonreSet = getUpdateIgnoreFields(v);
        XmlElement trimElement = new XmlElement("trim");
        trimElement.addAttribute(new Attribute("suffixOverrides", ","));

        if (updateFields.isEmpty()) {
            for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
                if (introspectedColumn.isAutoIncrement()) {
                    continue;
                }
                String columnName = introspectedColumn.getActualColumnName();
                if (uniqueFields.contains(columnName)) {
                    continue;
                }
                if ("version".equals(columnName)) {
                    trimElement.addElement(new TextElement("version = " + tableName + ".version + 1,"));
                } else if ("last_modified".equals(columnName)) {
                    trimElement.addElement(new TextElement("last_modified = now(),"));
                } else if (!igonreSet.contains(columnName)) {
                    trimElement.addElement(new TextElement(columnName + " = EXCLUDED." + columnName + ","));
                }
            }
        } else {
            StringBuilder sb = new StringBuilder();
            for (String updateField : updateFields) {
                if ("version".equals(updateField)) {
                    sb.append(", ").append("version = ").append(tableName).append(".version + 1");
                } else if ("last_modified".equals(updateField)) {
                    sb.append(", ").append("last_modified =  now()");
                } else {
                    sb.append(", ").append(updateField).append(" = ").append("EXCLUDED.").append(updateField);
                }
            }
            trimElement.addElement(new TextElement(sb.toString().replaceFirst(", ", "")));
        }

        return trimElement;
    }

    private String getFieldsString(List<IntrospectedColumn> columns, String fieldSuffix) {
        StringBuilder sb = new StringBuilder();
        for (IntrospectedColumn introspectedColumn : columns) {
            sb.append(",").append(introspectedColumn.getActualColumnName()).append(fieldSuffix);
        }
        return sb.toString().replaceFirst(",", "");
    }

}
