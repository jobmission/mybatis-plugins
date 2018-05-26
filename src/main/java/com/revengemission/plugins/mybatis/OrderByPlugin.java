package com.revengemission.plugins.mybatis;


import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

/*
*
* Supplied Plugins
*
 *       http://www.mybatis.org/generator/reference/plugins.html
* */
public class OrderByPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        topLevelClass.addImportedType(new FullyQualifiedJavaType("java.util.Map"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("java.util.LinkedHashMap"));
        FullyQualifiedJavaType mapWrapper = new FullyQualifiedJavaType("Map<String,String>");
        FullyQualifiedJavaType listWrapper = new FullyQualifiedJavaType("List<String>");

        for (Field field : topLevelClass.getFields()) {
            if (StringUtils.equals(field.getName(), "orderByClause")) {
                topLevelClass.getFields().remove(field);
                break;
            }
        }

        Field orderByClause = new Field();
        orderByClause.setName("orderByClause");
        orderByClause.setVisibility(JavaVisibility.PRIVATE);
        orderByClause.setType(mapWrapper);
        topLevelClass.addField(orderByClause);

        Field tableFields = new Field();
        tableFields.setName("tableFields");
        tableFields.setVisibility(JavaVisibility.PRIVATE);
        tableFields.setType(listWrapper);
        topLevelClass.addField(tableFields);

        Method addOrderBy = new Method();
        addOrderBy.setVisibility(JavaVisibility.PUBLIC);
        addOrderBy.setName("addOrderBy");
        Parameter filedParameter = new Parameter(FullyQualifiedJavaType.getStringInstance(), "fieldName", false);
        Parameter orderParameter = new Parameter(FullyQualifiedJavaType.getStringInstance(), "sortOrder", false);
        addOrderBy.addParameter(filedParameter);
        addOrderBy.addParameter(orderParameter);
        addOrderBy.addBodyLine("if (tableFields.contains(fieldName) && (\"asc\".equalsIgnoreCase(sortOrder)) || \"desc\".equalsIgnoreCase(sortOrder)){");
        addOrderBy.addBodyLine("if (orderByClause!=null){");
        addOrderBy.addBodyLine("orderByClause.put(fieldName,sortOrder);");
        addOrderBy.addBodyLine("} else {");
        addOrderBy.addBodyLine("orderByClause=new LinkedHashMap<>();");
        addOrderBy.addBodyLine("orderByClause.put(fieldName,sortOrder);");
        addOrderBy.addBodyLine("}");
        addOrderBy.addBodyLine("}");
        topLevelClass.addMethod(addOrderBy);


        for (Method method : topLevelClass.getMethods()) {
            if (StringUtils.equals(method.getName(), "setOrderByClause")) {
                topLevelClass.getMethods().remove(method);
                break;
            }
        }

        for (Method method : topLevelClass.getMethods()) {
            if (StringUtils.equals(method.getName(), "getOrderByClause")) {
                method.getBodyLines().clear();
                method.addBodyLine("if (orderByClause !=null && orderByClause.size() > 0) {");
                method.addBodyLine("StringBuffer sb=new StringBuffer();");
                method.addBodyLine("orderByClause.forEach((k,v)->{sb.append(','+k+' '+v);});");
                method.addBodyLine("return sb.toString().replaceFirst(\",\",\"\");");
                method.addBodyLine("} else {");
                method.addBodyLine("return null;");
                method.addBodyLine("}");

            }

            if (StringUtils.equals(method.getName(), getEntityName(introspectedTable) + "Example")) {
                method.addBodyLine("tableFields=new ArrayList<>();");
                for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
                    method.addBodyLine("tableFields.add(\"" + introspectedColumn.getActualColumnName() + "\");");
                }
            }
        }
        return true;
    }

    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element,
                                                                     IntrospectedTable introspectedTable) {
        return true;
    }


    @Override
    public boolean sqlMapSelectByExampleWithBLOBsElementGenerated(XmlElement element,
                                                                  IntrospectedTable introspectedTable) {

        return true;
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

        if (objectName == null || objectName.equals("")) {
            objectName = tableNameToEntityName(getTableName(introspectedTable));
        }
        return objectName;
    }
}
