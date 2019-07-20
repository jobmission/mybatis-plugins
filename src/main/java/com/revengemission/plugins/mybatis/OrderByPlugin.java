package com.revengemission.plugins.mybatis;


import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;

import java.util.List;

/**
 * 增强order by 语句，预防注入
 */
public class OrderByPlugin extends AbstractXmbgPlugin {

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
            if ("orderByClause".equals(field.getName())) {
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
        addOrderBy.addBodyLine("if (tableFields.contains(fieldName) && (\"asc\".equalsIgnoreCase(sortOrder)) || \"desc\".equalsIgnoreCase(sortOrder)) {");
        addOrderBy.addBodyLine("if (orderByClause != null) {");
        addOrderBy.addBodyLine("orderByClause.put(fieldName, sortOrder);");
        addOrderBy.addBodyLine("} else {");
        addOrderBy.addBodyLine("orderByClause = new LinkedHashMap<>();");
        addOrderBy.addBodyLine("orderByClause.put(fieldName, sortOrder);");
        addOrderBy.addBodyLine("}");
        addOrderBy.addBodyLine("}");
        topLevelClass.addMethod(addOrderBy);


        for (Method method : topLevelClass.getMethods()) {
            if ("setOrderByClause".equals(method.getName())) {
                topLevelClass.getMethods().remove(method);
                break;
            }
        }

        for (Method method : topLevelClass.getMethods()) {
            if ("getOrderByClause".equals(method.getName())) {
                method.getBodyLines().clear();
                method.addBodyLine("if (orderByClause != null && orderByClause.size() > 0) {");
                method.addBodyLine("StringBuffer sb = new StringBuffer();");
                method.addBodyLine("orderByClause.forEach((k, v) -> {");
                method.addBodyLine("sb.append(',' + k + ' ' + v);");
                method.addBodyLine("});");
                method.addBodyLine("return sb.toString().replaceFirst(\",\", \"\");");
                method.addBodyLine("} else {");
                method.addBodyLine("return null;");
                method.addBodyLine("}");

            }

            //获取构造函数
            if (method.isConstructor()) {
///                System.out.println("类名：" + topLevelClass.getType().getShortName());
                method.getBodyLines().clear();
                method.addBodyLine("oredCriteria = new ArrayList<>();");
                method.addBodyLine("tableFields = new ArrayList<>();");
                for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
                    method.addBodyLine("tableFields.add(\"" + introspectedColumn.getActualColumnName() + "\");");
                }
            }
        }
        return true;
    }


}
