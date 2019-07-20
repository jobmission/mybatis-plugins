package com.revengemission.plugins.mybatis;


import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

import java.util.*;

/**
 * 批量更新
 */
public class BatchUpdateByPrimaryKeyPlugin extends AbstractXmbgPlugin {

    private static final String CLIENT_METHOD_NAME = "batchUpdateByPrimaryKey";

    private static final String PROPERTY_PREFIX = "item.";

    Map<String, String> todo = new LinkedHashMap<>();

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        todo.clear();
        properties.forEach((k, v) -> {
            todo.put(k.toString().trim(), v.toString().trim());
        });
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String objectName = getEntityName(introspectedTable);
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();
        Method method = new Method(CLIENT_METHOD_NAME);
        FullyQualifiedJavaType type = new FullyQualifiedJavaType("java.util.List<" + objectName + ">");
        method.addParameter(new Parameter(type, "list"));
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        importedTypes.add(type);
        interfaze.addMethod(method);
        interfaze.addImportedTypes(importedTypes);
        return true;
    }


    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        XmlElement update = new XmlElement("update");
        update.addAttribute(new Attribute("id", CLIENT_METHOD_NAME));

        String parameterType = "java.util.List";

        update.addAttribute(new Attribute("parameterType", parameterType));

        XmlElement foreach = new XmlElement("foreach");
        foreach.addAttribute(new Attribute("collection", "list"));
        foreach.addAttribute(new Attribute("item", "item"));
        foreach.addAttribute(new Attribute("index", "index"));
        foreach.addAttribute(new Attribute("separator", ";"));

        generateTextBlockAppendTableName("update ", introspectedTable, foreach);

        TextElement setElement = new TextElement("set");
        foreach.addElement(setElement);

//        version = version + 1
        generateParameterForSet(PROPERTY_PREFIX, introspectedTable.getNonPrimaryKeyColumns(), foreach);
        replaceElement(foreach, todo);

        generateWhereConditions(PROPERTY_PREFIX, introspectedTable.getPrimaryKeyColumns(), foreach);

        update.addElement(foreach);
        document.getRootElement().addElement(update);

        return true;
    }


    protected void generateParameterForSet(String fieldPrefix, List<IntrospectedColumn> columns, XmlElement dynamicElement) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < columns.size(); i++) {
            IntrospectedColumn introspectedColumn = columns.get(i);
            sb.setLength(0);

            if ("version".equalsIgnoreCase(introspectedColumn.getActualColumnName())) {
                sb.append("  version = version + 1");
            } else if ("last_modified".equalsIgnoreCase(introspectedColumn.getActualColumnName())) {
                sb.append("  last_modified = now()");
            } else if ("modified_date".equalsIgnoreCase(introspectedColumn.getActualColumnName())) {
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

}
