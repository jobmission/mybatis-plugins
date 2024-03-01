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

        Field allowLettersPattern = new Field("allowLettersPattern", FullyQualifiedJavaType.getStringInstance());
        allowLettersPattern.setVisibility(JavaVisibility.PRIVATE);
        allowLettersPattern.setInitializationString("\"[_0-9a-zA-Z]+\"");
        topLevelClass.addField(allowLettersPattern);

        Field orderByClause = new Field("orderByClause", mapWrapper);
        orderByClause.setVisibility(JavaVisibility.PRIVATE);
        topLevelClass.addField(orderByClause);

        Field tableFields = new Field("tableFields", setWrapper);
        tableFields.setVisibility(JavaVisibility.PRIVATE);
        topLevelClass.addField(tableFields);

        Method underlineName = new Method("underlineName");
        underlineName.setVisibility(JavaVisibility.PRIVATE);
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

        Method addOrderBySpecial = new Method("addOrderBySpecial");
        addOrderBySpecial.setVisibility(JavaVisibility.PUBLIC);
        Parameter fieldParameter = new Parameter(FullyQualifiedJavaType.getStringInstance(), "fieldName", false);
        Parameter orderParameter = new Parameter(FullyQualifiedJavaType.getStringInstance(), "sortOrder", false);
        addOrderBySpecial.addParameter(fieldParameter);
        addOrderBySpecial.addParameter(orderParameter);
        addOrderBySpecial.addBodyLine("if (fieldName.matches(allowLettersPattern)) {");
        addOrderBySpecial.addBodyLine("String sortDirection = \"desc\";");
        addOrderBySpecial.addBodyLine("if ((\"asc\".equalsIgnoreCase(sortOrder))) {");
        addOrderBySpecial.addBodyLine("sortDirection = \"asc\";");
        addOrderBySpecial.addBodyLine("}");
        addOrderBySpecial.addBodyLine("if (orderByClause != null) {");
        addOrderBySpecial.addBodyLine("orderByClause.put(fieldName, sortDirection);");
        addOrderBySpecial.addBodyLine("} else {");
        addOrderBySpecial.addBodyLine("orderByClause = new LinkedHashMap<>();");
        addOrderBySpecial.addBodyLine("orderByClause.put(fieldName, sortDirection);");
        addOrderBySpecial.addBodyLine("}");
        addOrderBySpecial.addBodyLine("}");
        topLevelClass.addMethod(addOrderBySpecial);

        /**
         * 特殊字段 限制字母和下划线
         */
        Method addOrderBy = new Method("addOrderBy");
        addOrderBy.setVisibility(JavaVisibility.PUBLIC);
        addOrderBy.addParameter(fieldParameter);
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

        /**
         * 重载addOrderBy
         */
        Method addOrderBy2 = new Method("addOrderBy");
        addOrderBy2.setVisibility(JavaVisibility.PUBLIC);
        addOrderBy2.addJavaDocLine("/**");
        addOrderBy2.addJavaDocLine(" * 重载addOrderBy");
        addOrderBy2.addJavaDocLine(" *");
        addOrderBy2.addJavaDocLine(" * @param orderBys 排序子句，不要带 order by");
        addOrderBy2.addJavaDocLine(" */");
        Parameter orderByClauseParameter = new Parameter(FullyQualifiedJavaType.getStringInstance(), "orderBys", false);
        addOrderBy2.addParameter(orderByClauseParameter);

        addOrderBy2.addBodyLine("if (orderBys == null || \"\".equals(orderBys.trim())) {");
        addOrderBy2.addBodyLine("return;");
        addOrderBy2.addBodyLine("}");

        addOrderBy2.addBodyLine("String[] orders = orderBys.trim().split(\",\");");
        addOrderBy2.addBodyLine("for (String order : orders) {");
        addOrderBy2.addBodyLine("String[] fieldOrder = order.trim().split(\" \");");
        addOrderBy2.addBodyLine("if (fieldOrder.length == 2) {");
        addOrderBy2.addBodyLine("addOrderBy(fieldOrder[0], fieldOrder[1]);");
        addOrderBy2.addBodyLine("}");
        addOrderBy2.addBodyLine("}");
        topLevelClass.addMethod(addOrderBy2);

        /**
         * 重载addOrderBy
         */
        Method addOrderBy3 = new Method("addOrderBy");
        addOrderBy3.setVisibility(JavaVisibility.PUBLIC);
        addOrderBy3.addJavaDocLine("/**");
        addOrderBy3.addJavaDocLine(" * 重载addOrderBy");
        addOrderBy3.addJavaDocLine(" *");
        addOrderBy3.addJavaDocLine(" * @param orderBys 字段名,asc|desc");
        addOrderBy3.addJavaDocLine(" */");
        Parameter orderByClauseMapParameter = new Parameter(mapWrapper, "orderBys", false);
        addOrderBy3.addParameter(orderByClauseMapParameter);

        addOrderBy3.addBodyLine("if (orderBys == null || orderBys.size() == 0) {");
        addOrderBy3.addBodyLine("return;");
        addOrderBy3.addBodyLine("}");

        addOrderBy3.addBodyLine("orderBys.forEach((k, v) -> {");
        addOrderBy3.addBodyLine("addOrderBy(k.trim(), v);");
        addOrderBy3.addBodyLine("});");

        topLevelClass.addMethod(addOrderBy3);

        for (Method method : topLevelClass.getMethods()) {
            if ("setOrderByClause".equals(method.getName())) {
                topLevelClass.getMethods().remove(method);
                break;
            }
        }

        /**
         * clearOrderBy
         */
        Method clearOrderBy = new Method("clearOrderBy");
        clearOrderBy.setVisibility(JavaVisibility.PUBLIC);
        clearOrderBy.addJavaDocLine("/**");
        clearOrderBy.addJavaDocLine(" * clearOrderBy");
        clearOrderBy.addJavaDocLine(" */");

        clearOrderBy.addBodyLine("if (orderByClause != null) {");
        clearOrderBy.addBodyLine("orderByClause.clear();");
        clearOrderBy.addBodyLine("}");


        topLevelClass.addMethod(clearOrderBy);

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
