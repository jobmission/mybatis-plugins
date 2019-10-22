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
        topLevelClass.addImportedType(new FullyQualifiedJavaType("java.util.Set"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("java.util.LinkedHashMap"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("java.util.HashSet"));
        FullyQualifiedJavaType mapWrapper = new FullyQualifiedJavaType("Map<String,String>");
        FullyQualifiedJavaType setWrapper = new FullyQualifiedJavaType("Set<String>");

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
        tableFields.setType(setWrapper);
        topLevelClass.addField(tableFields);

        Method underlineName = new Method();
        underlineName.setVisibility(JavaVisibility.PRIVATE);
        underlineName.setName("underlineName");
        Parameter nameParameter = new Parameter(FullyQualifiedJavaType.getStringInstance(), "name", false);
        underlineName.addParameter(nameParameter);
        underlineName.addBodyLine("StringBuilder result = new StringBuilder();");
        underlineName.addBodyLine("if (name != null && name.length() > 0) {");
        underlineName.addBodyLine("result.append(name, 0, 1);");
        underlineName.addBodyLine("for (int i = 1; i < name.length(); i++) {");
        underlineName.addBodyLine("String s = name.substring(i, i + 1);");
        underlineName.addBodyLine("if (s.equals(s.toUpperCase()) && !Character.isDigit(s.charAt(0))) {");
        underlineName.addBodyLine("result.append(\"_\");");
        underlineName.addBodyLine("}");
        underlineName.addBodyLine("result.append(s.toLowerCase());");
        underlineName.addBodyLine("}");
        underlineName.addBodyLine("}");
        underlineName.addBodyLine("return result.toString();");
        underlineName.setReturnType(FullyQualifiedJavaType.getStringInstance());
        topLevelClass.addMethod(underlineName);

        Method addOrderBy = new Method();
        addOrderBy.setVisibility(JavaVisibility.PUBLIC);
        addOrderBy.setName("addOrderBy");
        Parameter filedParameter = new Parameter(FullyQualifiedJavaType.getStringInstance(), "fieldName", false);
        Parameter orderParameter = new Parameter(FullyQualifiedJavaType.getStringInstance(), "sortOrder", false);
        addOrderBy.addParameter(filedParameter);
        addOrderBy.addParameter(orderParameter);
        addOrderBy.addBodyLine("boolean findFieldName = false;");
        addOrderBy.addBodyLine("if (tableFields.contains(fieldName)) {");
        addOrderBy.addBodyLine("findFieldName = true;");
        addOrderBy.addBodyLine("} else {");
        addOrderBy.addBodyLine("fieldName = underlineName(fieldName);");
        addOrderBy.addBodyLine("if (tableFields.contains(fieldName)) {");
        addOrderBy.addBodyLine("findFieldName = true;");
        addOrderBy.addBodyLine("}");
        addOrderBy.addBodyLine("}");
        addOrderBy.addBodyLine("if (findFieldName) {");
        addOrderBy.addBodyLine("String sortDirection = \"desc\";");
        addOrderBy.addBodyLine("if ((\"asc\".equalsIgnoreCase(sortOrder))) {");
        addOrderBy.addBodyLine("sortDirection = \"asc\";");
        addOrderBy.addBodyLine("}");
        addOrderBy.addBodyLine("if (orderByClause != null) {");
        addOrderBy.addBodyLine("orderByClause.put(fieldName, sortDirection);");
        addOrderBy.addBodyLine("} else {");
        addOrderBy.addBodyLine("orderByClause = new LinkedHashMap<>();");
        addOrderBy.addBodyLine("orderByClause.put(fieldName, sortDirection);");
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
                method.getBodyLines().clear();
                method.addBodyLine("oredCriteria = new ArrayList<>();");
                method.addBodyLine("tableFields = new HashSet<>();");
                for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
                    method.addBodyLine("tableFields.add(\"" + introspectedColumn.getActualColumnName() + "\");");
                }
            }
        }
        return true;
    }


}
