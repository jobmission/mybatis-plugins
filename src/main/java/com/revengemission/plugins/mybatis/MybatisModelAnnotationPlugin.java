package com.revengemission.plugins.mybatis;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by revenge mission on 18-6-17.
 */
public class MybatisModelAnnotationPlugin extends AbstractXmbgPlugin {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {

        Map<String, String> todo = new LinkedHashMap<>();
        properties.forEach((k, v) -> {
            todo.put(StringUtils.trim(k.toString()), StringUtils.trim(v.toString()));
        });

        todo.forEach((k, v) -> {
            topLevelClass.addImportedType(new FullyQualifiedJavaType(k));
            topLevelClass.addAnnotation(v);
        });

        return true;
    }
}

