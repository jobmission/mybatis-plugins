package com.revengemission.plugins.mybatis;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.xml.*;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 插入数据时，重复键更新
 */
public class InsertOnUpdateSelectivePlugin extends AbstractXmbgPlugin {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(InsertOnUpdateSelectivePlugin.class);

    private static final String CLIENT_METHOD_NAME_SINGLE = "insertOnUpdateSelective";
    private static final String CLIENT_METHOD_NAME_SINGLE2 = "insertOnUpdateIgnoreFields";
    private static final String CLIENT_METHOD_NAME_SINGLE3 = "insertOnUpdateSelectiveAndIgnoreFields";
    private static final String CLIENT_METHOD_NAME_BATCH = "batchInsertOnUpdateSelective";
    private static final String CLIENT_METHOD_NAME_BATCH2 = "batchInsertOnUpdateIgnoreFields";
    private static final String CLIENT_METHOD_NAME_BATCH3 = "batchInsertOnUpdateSelectiveAndIgnoreFields";

    private static final String PROPERTY_PREFIX = "item.";

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
//        log.info("enter initialized {}", getTableName(introspectedTable));
    }

    @Override
    public boolean validate(List<String> list) {
        log.info("enter validate {}", list == null ? 0 : list.size());
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        String currentTableName = getTableName(introspectedTable);
        log.info("enter clientGenerated table {}", currentTableName);
        properties.forEach((k, v) -> {
            if (currentTableName.equalsIgnoreCase(k.toString().trim())) {
                String objectName = getEntityName(introspectedTable);
                {
                    Method methodSingle = new Method(CLIENT_METHOD_NAME_SINGLE);
                    methodSingle.setAbstract(true);
                    methodSingle.addParameter(new Parameter(new FullyQualifiedJavaType(objectName), "row"));
                    methodSingle.setReturnType(FullyQualifiedJavaType.getIntInstance());
                    interfaze.addMethod(methodSingle);
                }
                {
                    Method methodSingle = new Method(CLIENT_METHOD_NAME_SINGLE2);
                    methodSingle.setAbstract(true);
                    methodSingle.addParameter(new Parameter(new FullyQualifiedJavaType(objectName), "row"));
                    methodSingle.setReturnType(FullyQualifiedJavaType.getIntInstance());
                    interfaze.addMethod(methodSingle);
                }
                {
                    Method methodSingle = new Method(CLIENT_METHOD_NAME_SINGLE3);
                    methodSingle.setAbstract(true);
                    methodSingle.addParameter(new Parameter(new FullyQualifiedJavaType(objectName), "row"));
                    methodSingle.setReturnType(FullyQualifiedJavaType.getIntInstance());
                    interfaze.addMethod(methodSingle);
                }
                {
                    Method methodBatch = new Method(CLIENT_METHOD_NAME_BATCH);
                    methodBatch.setAbstract(true);
                    FullyQualifiedJavaType type = new FullyQualifiedJavaType("java.util.List<" + objectName + ">");
                    methodBatch.addParameter(new Parameter(type, "list"));
                    methodBatch.setReturnType(FullyQualifiedJavaType.getIntInstance());
                    interfaze.addMethod(methodBatch);
                }
                {
                    Method methodBatch = new Method(CLIENT_METHOD_NAME_BATCH2);
                    methodBatch.setAbstract(true);
                    FullyQualifiedJavaType type = new FullyQualifiedJavaType("java.util.List<" + objectName + ">");
                    methodBatch.addParameter(new Parameter(type, "list"));
                    methodBatch.setReturnType(FullyQualifiedJavaType.getIntInstance());
                    interfaze.addMethod(methodBatch);
                }
                {
                    Method methodBatch = new Method(CLIENT_METHOD_NAME_BATCH3);
                    methodBatch.setAbstract(true);
                    FullyQualifiedJavaType type = new FullyQualifiedJavaType("java.util.List<" + objectName + ">");
                    methodBatch.addParameter(new Parameter(type, "list"));
                    methodBatch.setReturnType(FullyQualifiedJavaType.getIntInstance());
                    interfaze.addMethod(methodBatch);
                }
            }
        });

        return true;
    }

    //uniqueFields=a,b;updateFields=c,d,e,f
    protected Set<String> getUniqueFields(String value) {
        Set<String> uniqueFields = new LinkedHashSet<>();
        String[] strings = value.trim().split(";");
        for (String s : strings) {
            if (s.startsWith("uniqueFields")) {
                String[] fields = s.split("=");
                if (fields.length == 2) {
                    String[] fieldsArray = fields[1].split(",");
                    for (String field : fieldsArray) {
                        uniqueFields.add(field.trim());
                    }
                }
            }
        }
        return uniqueFields;
    }

    //uniqueFields=a,b;updateFields=c,d,e,f
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
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        String currentTableName = getTableName(introspectedTable);
        log.info("enter sqlMapDocumentGenerated table {}", currentTableName);
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            Object k = entry.getKey();
            Object v = entry.getValue();
            if (currentTableName.equalsIgnoreCase(k.toString().trim())) {
                List<IntrospectedColumn> notAutoIncrementColumnList = introspectedTable.getAllColumns().stream().filter(introspectedColumn -> !introspectedColumn.isAutoIncrement()).toList();

                TextElement onMysqlUpdateElement = new TextElement("ON DUPLICATE KEY UPDATE");
                Set<String> uniqueFields = getUniqueFields((String) v);
                TextElement postgresqlOnConflictElement = new TextElement("ON CONFLICT (" + String.join(", ", uniqueFields) + ")");
                {

                    XmlElement insertXmlElement = new XmlElement("insert");
                    insertXmlElement.addAttribute(new Attribute("id", CLIENT_METHOD_NAME_SINGLE));
                    insertXmlElement.addAttribute(new Attribute("parameterType", context.getJavaModelGeneratorConfiguration().getTargetPackage() + "." + getEntityName(introspectedTable)));

                    generateTextBlockAppendTableName("insert into ", introspectedTable, insertXmlElement);
                    generateActualColumnNamesWithParenthesis(notAutoIncrementColumnList, insertXmlElement);
                    insertXmlElement.addElement(new TextElement("values "));
                    generateParametersSeparateByCommaWithParenthesis("", notAutoIncrementColumnList, insertXmlElement);

                    /// mysql
                    XmlElement mysqlIfElement = new XmlElement("if");
                    mysqlIfElement.addAttribute(new Attribute("test", "_databaseId == 'mysql'"));
                    ///mysqlIfElement.addElement(new TextElement("AS newRowValue (" + getFieldsString(notAutoIncrementColumnList, "_new") + ")"));
                    mysqlIfElement.addElement(new TextElement("AS newRowValue"));
                    mysqlIfElement.addElement(onMysqlUpdateElement);
                    mysqlIfElement.addElement(getMysqlUpdateSelectiveClauseText(introspectedTable));
                    insertXmlElement.addElement(mysqlIfElement);
                    /// postgresql
                    XmlElement postgresqlIfElement = new XmlElement("if");
                    postgresqlIfElement.addAttribute(new Attribute("test", "_databaseId == 'postgresql' or _databaseId == 'sqlite'"));
                    postgresqlIfElement.addElement(postgresqlOnConflictElement);
                    postgresqlIfElement.addElement(new TextElement("DO UPDATE SET"));
                    postgresqlIfElement.addElement(getPostgresqlUpdateSelectiveClauseText(introspectedTable));
                    insertXmlElement.addElement(postgresqlIfElement);

                    document.getRootElement().addElement(insertXmlElement);

                }

                {

                    XmlElement insertXmlElement = new XmlElement("insert");
                    insertXmlElement.addAttribute(new Attribute("id", CLIENT_METHOD_NAME_SINGLE2));
                    insertXmlElement.addAttribute(new Attribute("parameterType", context.getJavaModelGeneratorConfiguration().getTargetPackage() + "." + getEntityName(introspectedTable)));
                    generateTextBlockAppendTableName("insert into ", introspectedTable, insertXmlElement);
                    generateActualColumnNamesWithParenthesis(notAutoIncrementColumnList, insertXmlElement);
                    insertXmlElement.addElement(new TextElement("values "));
                    generateParametersSeparateByCommaWithParenthesis("", notAutoIncrementColumnList, insertXmlElement);

                    /// mysql
                    XmlElement mysqlIfElement = new XmlElement("if");
                    mysqlIfElement.addAttribute(new Attribute("test", "_databaseId == 'mysql'"));
                    ///mysqlIfElement.addElement(new TextElement("AS newRowValue (" + getFieldsString(notAutoIncrementColumnList, "_new") + ")"));
                    mysqlIfElement.addElement(new TextElement("AS newRowValue"));
                    mysqlIfElement.addElement(onMysqlUpdateElement);
                    mysqlIfElement.addElement(getMysqlUpdateIgnoreClauseText(v.toString(), introspectedTable));
                    insertXmlElement.addElement(mysqlIfElement);
                    /// posrgresql
                    XmlElement postgresqlIfElement = new XmlElement("if");
                    postgresqlIfElement.addAttribute(new Attribute("test", "_databaseId == 'postgresql' or _databaseId == 'sqlite'"));
                    postgresqlIfElement.addElement(postgresqlOnConflictElement);
                    postgresqlIfElement.addElement(new TextElement("DO UPDATE SET"));
                    postgresqlIfElement.addElement(getPostgresqlUpdateIgnoreClauseText(v.toString(), introspectedTable));
                    insertXmlElement.addElement(postgresqlIfElement);

                    document.getRootElement().addElement(insertXmlElement);

                }


                {

                    XmlElement insertXmlElement = new XmlElement("insert");
                    insertXmlElement.addAttribute(new Attribute("id", CLIENT_METHOD_NAME_SINGLE3));
                    insertXmlElement.addAttribute(new Attribute("parameterType", context.getJavaModelGeneratorConfiguration().getTargetPackage() + "." + getEntityName(introspectedTable)));

                    generateTextBlockAppendTableName("insert into ", introspectedTable, insertXmlElement);
                    generateActualColumnNamesWithParenthesis(notAutoIncrementColumnList, insertXmlElement);
                    insertXmlElement.addElement(new TextElement("values "));
                    generateParametersSeparateByCommaWithParenthesis("", notAutoIncrementColumnList, insertXmlElement);

                    /// mysql
                    XmlElement mysqlIfElement = new XmlElement("if");
                    mysqlIfElement.addAttribute(new Attribute("test", "_databaseId == 'mysql'"));
                    ///mysqlIfElement.addElement(new TextElement("AS newRowValue (" + getFieldsString(notAutoIncrementColumnList, "_new") + ")"));
                    mysqlIfElement.addElement(new TextElement("AS newRowValue"));
                    mysqlIfElement.addElement(onMysqlUpdateElement);
                    VisitableElement elementList = getMysqlUpdateSelectiveAndIgnoreClauseText(v.toString(), introspectedTable);
                    mysqlIfElement.addElement(elementList);
                    insertXmlElement.addElement(mysqlIfElement);
                    /// posrgresql
                    XmlElement postgresqlIfElement = new XmlElement("if");
                    postgresqlIfElement.addAttribute(new Attribute("test", "_databaseId == 'postgresql' or _databaseId == 'sqlite'"));
                    postgresqlIfElement.addElement(postgresqlOnConflictElement);
                    postgresqlIfElement.addElement(new TextElement("DO UPDATE SET"));
                    postgresqlIfElement.addElement(getPostgresqlUpdateSelectiveAndIgnoreClauseText(v.toString(), introspectedTable));
                    insertXmlElement.addElement(postgresqlIfElement);

                    document.getRootElement().addElement(insertXmlElement);

                }

                {
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

                    /// mysql
                    XmlElement mysqlIfElement = new XmlElement("if");
                    mysqlIfElement.addAttribute(new Attribute("test", "_databaseId == 'mysql'"));
                    ///mysqlIfElement.addElement(new TextElement("AS newRowValue (" + getFieldsString(notAutoIncrementColumnList, "_new") + ")"));
                    mysqlIfElement.addElement(new TextElement("AS newRowValue"));
                    mysqlIfElement.addElement(onMysqlUpdateElement);
                    VisitableElement elementList = getMysqlUpdateSelectiveClauseText(introspectedTable);
                    mysqlIfElement.addElement(elementList);

                    ifListElement.addElement(mysqlIfElement);

                    /// postgresql
                    XmlElement postgresqlIfElement = new XmlElement("if");
                    postgresqlIfElement.addAttribute(new Attribute("test", "_databaseId == 'postgresql' or _databaseId == 'sqlite'"));
                    postgresqlIfElement.addElement(postgresqlOnConflictElement);
                    postgresqlIfElement.addElement(new TextElement("DO UPDATE SET"));
                    VisitableElement postgresqlElementList = getPostgresqlUpdateSelectiveClauseText(introspectedTable);
                    postgresqlIfElement.addElement(postgresqlElementList);

                    ifListElement.addElement(postgresqlIfElement);
                    batchInsertXmlElement.addElement(ifListElement);


                    XmlElement ifNullElement = new XmlElement("if");
                    ifNullElement.addAttribute(new Attribute("test", "list == null or list.size() == 0"));
                    ifNullElement.addElement(new TextElement("select 0"));
                    batchInsertXmlElement.addElement(ifNullElement);

                    document.getRootElement().addElement(batchInsertXmlElement);
                }

                {
                    XmlElement batchInsertXmlElement = new XmlElement("insert");
                    batchInsertXmlElement.addAttribute(new Attribute("id", CLIENT_METHOD_NAME_BATCH2));
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

                    /// mysql
                    XmlElement mysqlIfElement = new XmlElement("if");
                    mysqlIfElement.addAttribute(new Attribute("test", "_databaseId == 'mysql'"));
                    ///mysqlIfElement.addElement(new TextElement("AS newRowValue (" + getFieldsString(notAutoIncrementColumnList, "_new") + ")"));
                    mysqlIfElement.addElement(new TextElement("AS newRowValue"));
                    mysqlIfElement.addElement(onMysqlUpdateElement);
                    mysqlIfElement.addElement(getMysqlUpdateIgnoreClauseText(v.toString(), introspectedTable));
                    ifListElement.addElement(mysqlIfElement);

                    /// postgresql
                    XmlElement postgresqlIfElement = new XmlElement("if");
                    postgresqlIfElement.addAttribute(new Attribute("test", "_databaseId == 'postgresql' or _databaseId == 'sqlite'"));
                    postgresqlIfElement.addElement(postgresqlOnConflictElement);
                    postgresqlIfElement.addElement(new TextElement("DO UPDATE SET"));
                    VisitableElement postgresqlElementList = getPostgresqlUpdateIgnoreClauseText(v.toString(), introspectedTable);
                    postgresqlIfElement.addElement(postgresqlElementList);

                    ifListElement.addElement(postgresqlIfElement);
                    batchInsertXmlElement.addElement(ifListElement);

                    XmlElement ifNullElement = new XmlElement("if");
                    ifNullElement.addAttribute(new Attribute("test", "list == null or list.size() == 0"));
                    ifNullElement.addElement(new TextElement("select 0"));
                    batchInsertXmlElement.addElement(ifNullElement);

                    document.getRootElement().addElement(batchInsertXmlElement);
                }

                {
                    XmlElement batchInsertXmlElement = new XmlElement("insert");
                    batchInsertXmlElement.addAttribute(new Attribute("id", CLIENT_METHOD_NAME_BATCH3));
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

                    /// mysql
                    XmlElement mysqlIfElement = new XmlElement("if");
                    mysqlIfElement.addAttribute(new Attribute("test", "_databaseId == 'mysql'"));
                    ///mysqlIfElement.addElement(new TextElement("AS newRowValue (" + getFieldsString(notAutoIncrementColumnList, "_new") + ")"));
                    mysqlIfElement.addElement(new TextElement("AS newRowValue"));
                    mysqlIfElement.addElement(onMysqlUpdateElement);
                    mysqlIfElement.addElement(getMysqlUpdateSelectiveAndIgnoreClauseText(v.toString(), introspectedTable));
                    ifListElement.addElement(mysqlIfElement);


                    /// postgresql
                    XmlElement postgresqlIfElement = new XmlElement("if");
                    postgresqlIfElement.addAttribute(new Attribute("test", "_databaseId == 'postgresql' or _databaseId == 'sqlite'"));
                    postgresqlIfElement.addElement(postgresqlOnConflictElement);
                    postgresqlIfElement.addElement(new TextElement("DO UPDATE SET"));
                    VisitableElement postgresqlElementList = getPostgresqlUpdateSelectiveAndIgnoreClauseText(v.toString(), introspectedTable);
                    postgresqlIfElement.addElement(postgresqlElementList);

                    ifListElement.addElement(postgresqlIfElement);
                    batchInsertXmlElement.addElement(ifListElement);

                    XmlElement ifNullElement = new XmlElement("if");
                    ifNullElement.addAttribute(new Attribute("test", "list == null or list.size() == 0"));
                    ifNullElement.addElement(new TextElement("select 0"));
                    batchInsertXmlElement.addElement(ifNullElement);

                    document.getRootElement().addElement(batchInsertXmlElement);
                }
            }
        }


        return true;
    }

    private VisitableElement getMysqlUpdateSelectiveClauseText(IntrospectedTable introspectedTable) {
        XmlElement trimElement = new XmlElement("trim");
        trimElement.addAttribute(new Attribute("suffixOverrides", ","));

        introspectedTable.getAllColumns().forEach(introspectedColumn -> {
            if (!introspectedColumn.isAutoIncrement()) {
                String columnName = introspectedColumn.getActualColumnName();
                trimElement.addElement(new TextElement(columnName + " = IF(newRowValue." + columnName + " is null, " + columnName + ", newRowValue." + columnName + "),"));
            }
        });
        return trimElement;
    }

    private VisitableElement getPostgresqlUpdateSelectiveClauseText(IntrospectedTable introspectedTable) {
        XmlElement trimElement = new XmlElement("trim");
        trimElement.addAttribute(new Attribute("suffixOverrides", ","));

        introspectedTable.getAllColumns().forEach(introspectedColumn -> {
            if (!introspectedColumn.isAutoIncrement()) {
                String columnName = introspectedColumn.getActualColumnName();
                trimElement.addElement(new TextElement(columnName + " = IF(EXCLUDED." + columnName + " is null, " + columnName + ", EXCLUDED." + columnName + "),"));
            }
        });
        return trimElement;
    }

    private VisitableElement getMysqlUpdateIgnoreClauseText(String value, IntrospectedTable introspectedTable) {
        String tableName = getTableName(introspectedTable);
        Set<String> igonreSet = getUpdateIgnoreFields(value);

        XmlElement trimElement = new XmlElement("trim");
        trimElement.addAttribute(new Attribute("suffixOverrides", ","));

        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            if (introspectedColumn.isAutoIncrement()) {
                continue;
            }
            String columnName = introspectedColumn.getActualColumnName();
            if (!igonreSet.contains(columnName)) {
                if ("version".equals(columnName)) {
                    trimElement.addElement(new TextElement("version = " + tableName + ".version + 1,"));
                } else if ("last_modified".equals(columnName)) {
                    trimElement.addElement(new TextElement("last_modified = now(),"));
                } else {
                    trimElement.addElement(new TextElement(columnName + " = newRowValue." + columnName + ","));
                }
            }
        }
        return trimElement;
    }

    private VisitableElement getPostgresqlUpdateIgnoreClauseText(String value, IntrospectedTable introspectedTable) {
        String tableName = getTableName(introspectedTable);
        Set<String> igonreSet = getUpdateIgnoreFields(value);

        XmlElement trimElement = new XmlElement("trim");
        trimElement.addAttribute(new Attribute("suffixOverrides", ","));

        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            if (introspectedColumn.isAutoIncrement()) {
                continue;
            }
            String columnName = introspectedColumn.getActualColumnName();
            if (!igonreSet.contains(columnName)) {
                if ("version".equals(columnName)) {
                    trimElement.addElement(new TextElement("version = " + tableName + ".version + 1,"));
                } else if ("last_modified".equals(columnName)) {
                    trimElement.addElement(new TextElement("last_modified = now(),"));
                } else {
                    trimElement.addElement(new TextElement(columnName + " = EXCLUDED." + columnName + ","));
                }
            }
        }
        return trimElement;
    }

    private VisitableElement getMysqlUpdateSelectiveAndIgnoreClauseText(String value, IntrospectedTable introspectedTable) {
        String tableName = getTableName(introspectedTable);
        Set<String> igonreSet = getUpdateIgnoreFields(value);

        XmlElement trimElement = new XmlElement("trim");
        trimElement.addAttribute(new Attribute("suffixOverrides", ","));

        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            if (introspectedColumn.isAutoIncrement()) {
                continue;
            }
            String columnName = introspectedColumn.getActualColumnName();
            if (!igonreSet.contains(columnName)) {
                if ("version".equals(columnName)) {
                    trimElement.addElement(new TextElement("version = " + tableName + ".version + 1,"));
                } else if ("last_modified".equals(columnName)) {
                    trimElement.addElement(new TextElement("last_modified = now(),"));
                } else {
                    trimElement.addElement(new TextElement(columnName + " = IF(newRowValue." + columnName + " is null, " + columnName + ", newRowValue." + columnName + "),"));
                }
            }
        }
        return trimElement;
    }

    private VisitableElement getPostgresqlUpdateSelectiveAndIgnoreClauseText(String value, IntrospectedTable introspectedTable) {
        String tableName = getTableName(introspectedTable);
        Set<String> igonreSet = getUpdateIgnoreFields(value);

        XmlElement trimElement = new XmlElement("trim");
        trimElement.addAttribute(new Attribute("suffixOverrides", ","));

        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            if (introspectedColumn.isAutoIncrement()) {
                continue;
            }
            String columnName = introspectedColumn.getActualColumnName();
            if (!igonreSet.contains(columnName)) {
                if ("version".equals(columnName)) {
                    trimElement.addElement(new TextElement("version = " + tableName + ".version + 1,"));
                } else if ("last_modified".equals(columnName)) {
                    trimElement.addElement(new TextElement("last_modified = now(),"));
                } else {
                    trimElement.addElement(new TextElement(columnName + " = IF(EXCLUDED." + columnName + " is null, " + columnName + ", EXCLUDED." + columnName + "),"));
                }
            }
        }
        return trimElement;
    }
}
