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

import java.util.List;
import java.util.Map;

/**
 * 逻辑删除，配置property,deletedFlagFiled和deletedFlagValue
 */
public class CustomDeletePlugin extends AbstractXmbgPlugin {

    private static final String CLIENT_METHOD_NAME_LOGICAL_DELETE = "logicalDeleteById";
    private static final String CLIENT_METHOD_NAME_LOGICAL_DELETE_IDS = "logicalDeleteByIds";
    private static final String CLIENT_METHOD_NAME_DELETE_IDS = "deleteByIds";

    private String deletedFlagTableFiled = "deleted";
    private Object deletedFlagValue = 1;

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            if ("deletedFlagTableFiled".equalsIgnoreCase(entry.getKey().toString().trim())) {
                deletedFlagTableFiled = entry.getValue().toString().trim();
            } else if ("deletedFlagValue".equalsIgnoreCase(entry.getKey().toString().trim())) {
                deletedFlagValue = entry.getValue();
            }
        }
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {

        if (hasColumn(introspectedTable, deletedFlagTableFiled)) {
            Method logicalDeleteByIdMethod = new Method(CLIENT_METHOD_NAME_LOGICAL_DELETE);
            logicalDeleteByIdMethod.setAbstract(true);
            logicalDeleteByIdMethod.addParameter(new Parameter(new FullyQualifiedJavaType("long"), "id", "@Param(\"id\")"));
            logicalDeleteByIdMethod.setReturnType(FullyQualifiedJavaType.getIntInstance());
            interfaze.addMethod(logicalDeleteByIdMethod);

            Method logicalDeleteByIdsMethod = new Method(CLIENT_METHOD_NAME_LOGICAL_DELETE_IDS);
            logicalDeleteByIdsMethod.setAbstract(true);
            logicalDeleteByIdsMethod.addParameter(new Parameter(new FullyQualifiedJavaType("long[]"), "ids", "@Param(\"ids\")"));
            logicalDeleteByIdsMethod.setReturnType(FullyQualifiedJavaType.getIntInstance());
            interfaze.addMethod(logicalDeleteByIdsMethod);
        }

        Method deleteByIdsMethod = new Method(CLIENT_METHOD_NAME_DELETE_IDS);
        deleteByIdsMethod.setAbstract(true);
        deleteByIdsMethod.addParameter(new Parameter(new FullyQualifiedJavaType("long[]"), "ids", "@Param(\"ids\")"));
        deleteByIdsMethod.setReturnType(FullyQualifiedJavaType.getIntInstance());
        interfaze.addMethod(deleteByIdsMethod);
        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        String tableName = introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime();
        XmlElement parentElement = document.getRootElement();
        if (hasColumn(introspectedTable, deletedFlagTableFiled)) {

            XmlElement logicalDeleteByIdElement = new XmlElement("update");
            logicalDeleteByIdElement.addAttribute(new Attribute("id", CLIENT_METHOD_NAME_LOGICAL_DELETE));
            logicalDeleteByIdElement.addElement(new TextElement("update " + tableName));
            logicalDeleteByIdElement.addElement(new TextElement("set " + deletedFlagTableFiled + " = " + deletedFlagValue));
            logicalDeleteByIdElement.addElement(new TextElement("where id = #{id}"));

            parentElement.addElement(logicalDeleteByIdElement);


            XmlElement logicalDeleteByIdsElement = new XmlElement("update");
            logicalDeleteByIdsElement.addAttribute(new Attribute("id", CLIENT_METHOD_NAME_LOGICAL_DELETE_IDS));
            logicalDeleteByIdsElement.addElement(new TextElement("update " + tableName));
            logicalDeleteByIdsElement.addElement(new TextElement("set " + deletedFlagTableFiled + " = " + deletedFlagValue));
            logicalDeleteByIdsElement.addElement(new TextElement("where id in"));

            XmlElement foreachElement = new XmlElement("foreach");
            foreachElement.addAttribute(new Attribute("item", "item"));
            foreachElement.addAttribute(new Attribute("index", "index"));
            foreachElement.addAttribute(new Attribute("collection", "ids"));
            foreachElement.addAttribute(new Attribute("open", "("));
            foreachElement.addAttribute(new Attribute("separator", ","));
            foreachElement.addAttribute(new Attribute("close", ")"));
            foreachElement.addElement(new TextElement("#{item}"));
            logicalDeleteByIdsElement.addElement(foreachElement);
            parentElement.addElement(logicalDeleteByIdsElement);

        }

        XmlElement deleteByIdsElement = new XmlElement("delete");
        deleteByIdsElement.addAttribute(new Attribute("id", CLIENT_METHOD_NAME_DELETE_IDS));
        deleteByIdsElement.addElement(new TextElement("delete from " + tableName));
        deleteByIdsElement.addElement(new TextElement("where id in"));

        XmlElement foreachElement = new XmlElement("foreach");
        foreachElement.addAttribute(new Attribute("item", "item"));
        foreachElement.addAttribute(new Attribute("index", "index"));
        foreachElement.addAttribute(new Attribute("collection", "ids"));
        foreachElement.addAttribute(new Attribute("open", "("));
        foreachElement.addAttribute(new Attribute("separator", ","));
        foreachElement.addAttribute(new Attribute("close", ")"));
        foreachElement.addElement(new TextElement("#{item}"));
        deleteByIdsElement.addElement(foreachElement);
        parentElement.addElement(deleteByIdsElement);

        return super.sqlMapDocumentGenerated(document, introspectedTable);

    }

    private boolean hasColumn(IntrospectedTable introspectedTable, String column) {
        boolean flag = false;
        List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();
        for (int i = 0; i < allColumns.size(); i++) {
            if (column.equals(allColumns.get(i).getActualColumnName())) {
                flag = true;
            }
        }
        return flag;
    }

}
