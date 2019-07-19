package com.revengemission.plugins.mybatis;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Entity Model 类中field添加注解
 */
public class MybatisModelFieldAnnotationPlugin extends AbstractXmbgPlugin {

    private static final String EVERY_TABLE_NAME = "every_table";
    private static final String EVERY_FIELD_NAME = "every_field";

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelFieldGenerated(Field field,
                                       TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
                                       IntrospectedTable introspectedTable,
                                       Plugin.ModelClassType modelClassType) {

        String currentTableName = getTableName(introspectedTable);
        String currentColumnName = getTableColumnName(introspectedColumn);

        Map<String, String> todo = new LinkedHashMap<>();
        properties.forEach((k, v) -> {
            //截取property name，是因为字段上可能有多个注解，防止key重复覆盖
            String[] temp = StringUtils.trim(k.toString()).split(";");
            if (temp.length == 3) {
                if (StringUtils.equalsIgnoreCase(currentTableName, temp[0]) || StringUtils.equalsIgnoreCase(EVERY_TABLE_NAME, temp[0])) {
                    if (StringUtils.equalsIgnoreCase(temp[1], currentColumnName) || StringUtils.equalsIgnoreCase(temp[1], EVERY_FIELD_NAME)) {
                        todo.put(temp[2], StringUtils.trim(v.toString()));
                    }
                }
            }
        });

        todo.forEach((k, v) -> {
            topLevelClass.addImportedType(new FullyQualifiedJavaType(k));
            field.addAnnotation(v);
        });

        return true;
    }
}

