package com.revengemission.plugins.mybatis;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.xml.*;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 插入数据时，重复键更新
 */
public class InsertOnUpdatePlugin extends AbstractXmbgPlugin {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(InsertOnUpdatePlugin.class);

    private static final String CLIENT_METHOD_NAME_SINGLE = "insertOnUpdate";
    private static final String CLIENT_METHOD_NAME_BATCH = "batchInsertOnUpdate";

    private static final String PROPERTY_PREFIX = "item.";

    Set<String> igonreSet = new HashSet<>();


    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        igonreSet.add("id");
        igonreSet.add("deleted");
        igonreSet.add("record_status");
        igonreSet.add("sort_priority");
        igonreSet.add("remark");
        igonreSet.add("date_created");
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
            }
        });
        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        String currentTableName = getTableName(introspectedTable);
        log.info("enter sqlMapDocumentGenerated table {}", currentTableName);
        properties.forEach((k, v) -> {
            if (currentTableName.equalsIgnoreCase(k.toString().trim())) {

                TextElement onUpdateElement = new TextElement("ON DUPLICATE KEY UPDATE");

                XmlElement insertXmlElement = new XmlElement("insert");
                insertXmlElement.addAttribute(new Attribute("id", CLIENT_METHOD_NAME_SINGLE));
                insertXmlElement.addAttribute(new Attribute("parameterType", context.getJavaModelGeneratorConfiguration().getTargetPackage() + "." + getEntityName(introspectedTable)));

                generateTextBlockAppendTableName("insert into ", introspectedTable, insertXmlElement);

                generateActualColumnNamesWithParenthesis(introspectedTable.getAllColumns(), insertXmlElement);

                insertXmlElement.addElement(new TextElement("values "));


                generateParametersSeparateByCommaWithParenthesis("", introspectedTable.getAllColumns(), insertXmlElement);
                insertXmlElement.addElement(new TextElement("AS newRowValue (" + getFieldsString(introspectedTable.getAllColumns(), "_new") + ")"));
                insertXmlElement.addElement(onUpdateElement);

                insertXmlElement.addElement(getUpdateClauseText(v.toString().trim(), introspectedTable.getAllColumns()));

                document.getRootElement().addElement(insertXmlElement);


                XmlElement batchInsertXmlElement = new XmlElement("insert");

                batchInsertXmlElement.addAttribute(new Attribute("id", CLIENT_METHOD_NAME_BATCH));
                FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType("java.util.List");
                batchInsertXmlElement.addAttribute(new Attribute("parameterType", parameterType.getFullyQualifiedName()));
                XmlElement ifListElement = new XmlElement("if");
                ifListElement.addAttribute(new Attribute("test", "list != null and list.size() > 0"));
                generateTextBlockAppendTableName("insert into ", introspectedTable, ifListElement);

                generateActualColumnNamesWithParenthesis(introspectedTable.getAllColumns(), ifListElement);

                ifListElement.addElement(new TextElement("values "));

                XmlElement foreach = new XmlElement("foreach");
                foreach.addAttribute(new Attribute("collection", "list"));
                foreach.addAttribute(new Attribute("item", "item"));
                foreach.addAttribute(new Attribute("index", "index"));
                foreach.addAttribute(new Attribute("separator", ","));

                generateParametersSeparateByCommaWithParenthesis(PROPERTY_PREFIX, introspectedTable.getAllColumns(), foreach);

                ifListElement.addElement(foreach);
                ifListElement.addElement(new TextElement("AS newRowValue (" + getFieldsString(introspectedTable.getAllColumns(), "_new") + ")"));
                ifListElement.addElement(onUpdateElement);
                ifListElement.addElement(getUpdateClauseText(v.toString().trim(), introspectedTable.getAllColumns()));
                batchInsertXmlElement.addElement(ifListElement);
                XmlElement ifNullElement = new XmlElement("if");
                ifNullElement.addAttribute(new Attribute("test", "list == null or list.size() == 0"));
                ifNullElement.addElement(new TextElement("select 0"));
                batchInsertXmlElement.addElement(ifNullElement);

                document.getRootElement().addElement(batchInsertXmlElement);
            }
        });

        return true;
    }

    private VisitableElement getUpdateClauseText(String v, List<IntrospectedColumn> columns) {
        XmlElement trimElement = new XmlElement("trim");
        trimElement.addAttribute(new Attribute("suffixOverrides", ","));

        if (v == null || "".equals(v.trim())) {
            StringBuilder sb = new StringBuilder();
            for (IntrospectedColumn introspectedColumn : columns) {
                if ("version".equals(introspectedColumn.getActualColumnName())) {
                    trimElement.addElement(new TextElement("version = version + 1,"));
                } else if (!igonreSet.contains(introspectedColumn.getActualColumnName())) {
                    trimElement.addElement(new TextElement(introspectedColumn.getActualColumnName() + " = newRowValue." + introspectedColumn.getActualColumnName() + "_new,"));
                }
            }
        } else {
            trimElement.addElement(new TextElement(v));
        }

        return trimElement;
    }

    private String getFieldsString(List<IntrospectedColumn> columns, String fieldSuffix) {
        StringBuilder sb = new StringBuilder();
        for (IntrospectedColumn introspectedColumn : columns) {
            sb.append(",");
            sb.append(introspectedColumn.getActualColumnName() + fieldSuffix);
        }
        return sb.toString().replaceFirst(",", "");
    }

}
