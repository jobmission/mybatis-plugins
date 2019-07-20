package com.revengemission.plugins.mybatis;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Entity Model 类上添加注解，可添加多个注解
 */
public class MybatisModelAnnotationPlugin extends AbstractXmbgPlugin {

    private static final String EVERY_TABLE_NAME = "every_table";

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }


    private void addAnnotations(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String currentTableName = getTableName(introspectedTable);

        //<table name ,<annotationClass,annotationValue>>
        Map<String, Map<String, String>> todo = new LinkedHashMap<>();
        properties.forEach((k, v) -> {
            //截取property name，是因为类上可能有多个注解，防止key重复覆盖
            String[] temp = k.toString().trim().split(";");
            if (temp.length == 2) {
                if (todo.containsKey(temp[0])) {
                    todo.get(temp[0]).put(temp[1], v.toString().trim());
                } else {
                    Map<String, String> annotationMap = new HashMap<>(16);
                    annotationMap.put(temp[1], v.toString().trim());
                    todo.put(temp[0], annotationMap);
                }
            }
        });


        todo.forEach((k, v) -> {
            if (currentTableName.equalsIgnoreCase(k) || EVERY_TABLE_NAME.equalsIgnoreCase(k)) {
                v.forEach((annotationClass, annotationValue) -> {
                    topLevelClass.addImportedType(new FullyQualifiedJavaType(annotationClass));
                    topLevelClass.addAnnotation(annotationValue);
                });
            }
        });
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {

        addAnnotations(topLevelClass, introspectedTable);
        return true;
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass,
                                                      IntrospectedTable introspectedTable) {

        addAnnotations(topLevelClass, introspectedTable);
        return true;
    }
}

