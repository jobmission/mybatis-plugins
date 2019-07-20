package com.revengemission.plugins.mybatis;

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
            String[] temp = k.toString().trim().split(";");
            if (temp.length == 3) {
                if (currentTableName.equalsIgnoreCase(temp[0]) || EVERY_TABLE_NAME.equalsIgnoreCase(temp[0])) {
                    if (currentColumnName.equalsIgnoreCase(temp[1]) || EVERY_FIELD_NAME.equalsIgnoreCase(temp[1])) {
                        todo.put(temp[2], v.toString().trim());
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

