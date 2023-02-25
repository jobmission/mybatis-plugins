package com.revengemission.plugins.mybatis;


import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 派生字段、虚拟列、计算列
 * 未完待续
 */
public class DeriveFieldsPlugin extends AbstractXmbgPlugin {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(DeriveFieldsPlugin.class);

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        String currentTableName = getTableName(introspectedTable);
        log.info("enter modelExampleClassGenerated table {}", currentTableName);
        properties.forEach((k, v) -> {
            if (currentTableName.equalsIgnoreCase(k.toString().trim())) {
                String[] deriveColumnStatements = v.toString().trim().split(";");
                if (deriveColumnStatements.length > 0) {
                    for (String deriveColumnStatement : deriveColumnStatements) {
                        String[] strings = deriveColumnStatement.split(",");
                        if (strings.length == 2) {
                            String fieldName = strings[0];
                            Field deriveColumn = new Field(fieldName, FullyQualifiedJavaType.getStringInstance());
                            deriveColumn.setVisibility(JavaVisibility.PRIVATE);
                            topLevelClass.addField(deriveColumn);
                            log.info(String.format("table %s: add derive field %s", currentTableName, fieldName));

                            Method getMethod = new Method("get" + upperCaseFirstChar(fieldName));
                            getMethod.setVisibility(JavaVisibility.PUBLIC);
                            getMethod.addBodyLine(String.format("return %s;", fieldName));
                            getMethod.setReturnType(FullyQualifiedJavaType.getStringInstance());
                            topLevelClass.addMethod(getMethod);
                            log.info(String.format("table %s: add derive method get%s", currentTableName, upperCaseFirstChar(fieldName)));

                            Method setMethod = new Method("set" + upperCaseFirstChar(fieldName));
                            setMethod.setVisibility(JavaVisibility.PUBLIC);
                            Parameter nameParameter = new Parameter(FullyQualifiedJavaType.getStringInstance(), fieldName, false);
                            setMethod.addParameter(nameParameter);
                            topLevelClass.addMethod(setMethod);
                            setMethod.addBodyLine(String.format("this.%s = %s;", fieldName, fieldName));
                            log.info(String.format("table %s: add derive method set%s", currentTableName, upperCaseFirstChar(fieldName)));
                        }
                    }
                }
            }
        });
        return true;
    }
}
