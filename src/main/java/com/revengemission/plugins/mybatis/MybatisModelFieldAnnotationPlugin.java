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
 * Entity Model类中field添加注解
 */
public class MybatisModelFieldAnnotationPlugin extends AbstractXmbgPlugin {

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
            String[] temp = StringUtils.trim(k.toString()).split(";");
            if (temp.length == 3) {
                if (StringUtils.equalsIgnoreCase(currentTableName, temp[0]) || StringUtils.equalsIgnoreCase("every_table", temp[0])) {
                    if (StringUtils.equalsIgnoreCase(temp[1], currentColumnName)) {
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

