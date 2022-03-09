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
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 插入数据时，重复键更新
 */
public class InsertOnUpdatePlugin extends AbstractXmbgPlugin {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(InsertOnUpdatePlugin.class);

    private static final String CLIENT_METHOD_NAME_SINGLE = "insertOnUpdate";
    private static final String CLIENT_METHOD_NAME_BATCH = "batchInsertOnUpdate";

    private static final String PROPERTY_PREFIX = "item.";


    @Override
    public void initialized(IntrospectedTable introspectedTable) {

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
                methodSingle.addParameter(new Parameter(new FullyQualifiedJavaType(objectName), "object"));
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
                TextElement onUpdateElement = new TextElement("ON DUPLICATE KEY UPDATE ");
                TextElement updateClauseTextElement = new TextElement(v.toString().trim());

                XmlElement insertXmlElement = new XmlElement("insert");

                insertXmlElement.addAttribute(new Attribute("id", CLIENT_METHOD_NAME_SINGLE));
                insertXmlElement.addAttribute(new Attribute("parameterType", context.getJavaModelGeneratorConfiguration().getTargetPackage() + "." + getEntityName(introspectedTable)));

                generateTextBlockAppendTableName("insert into ", introspectedTable, insertXmlElement);

                generateActualColumnNamesWithParenthesis(introspectedTable.getNonBLOBColumns(), insertXmlElement);

                insertXmlElement.addElement(new TextElement("values "));


                generateParametersSeparateByCommaWithParenthesis("", introspectedTable.getNonBLOBColumns(), insertXmlElement);
                insertXmlElement.addElement(new TextElement("AS newRowValue (" + getFieldsString(introspectedTable.getNonBLOBColumns(), "_new") + ")"));
                insertXmlElement.addElement(onUpdateElement);
                insertXmlElement.addElement(updateClauseTextElement);

                document.getRootElement().addElement(insertXmlElement);


                XmlElement batchInsertXmlElement = new XmlElement("insert");

                batchInsertXmlElement.addAttribute(new Attribute("id", CLIENT_METHOD_NAME_BATCH));
                FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType("java.util.List");
                batchInsertXmlElement.addAttribute(new Attribute("parameterType", parameterType.getFullyQualifiedName()));

                batchInsertXmlElement.addElement(new TextElement("<if test=\"list != null and list.size() > 0\">"));
                generateTextBlockAppendTableName("insert into ", introspectedTable, batchInsertXmlElement);

                generateActualColumnNamesWithParenthesis(introspectedTable.getNonBLOBColumns(), batchInsertXmlElement);

                batchInsertXmlElement.addElement(new TextElement("values "));

                XmlElement foreach = new XmlElement("foreach");
                foreach.addAttribute(new Attribute("collection", "list"));
                foreach.addAttribute(new Attribute("item", "item"));
                foreach.addAttribute(new Attribute("index", "index"));
                foreach.addAttribute(new Attribute("separator", ","));

                generateParametersSeparateByCommaWithParenthesis(PROPERTY_PREFIX, introspectedTable.getNonBLOBColumns(), foreach);

                batchInsertXmlElement.addElement(foreach);
                batchInsertXmlElement.addElement(new TextElement("AS newRowValue (" + getFieldsString(introspectedTable.getNonBLOBColumns(), "_new") + ")"));
                batchInsertXmlElement.addElement(onUpdateElement);
                batchInsertXmlElement.addElement(updateClauseTextElement);
                batchInsertXmlElement.addElement(new TextElement("</if>"));
                batchInsertXmlElement.addElement(new TextElement("<if test=\"list == null or list.size() == 0\">"));
                batchInsertXmlElement.addElement(new TextElement("  select 0"));
                batchInsertXmlElement.addElement(new TextElement("</if>"));

                document.getRootElement().addElement(batchInsertXmlElement);
            }
        });

        return true;
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
