package com.revengemission.plugins.mybatis;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.JavaFormatter;
import org.mybatis.generator.api.dom.DefaultJavaFormatter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class TableConstantsPlugin extends AbstractXmbgPlugin {

    private static final Logger log = LoggerFactory.getLogger(TableConstantsPlugin.class);

    private String tableNamePattern = "%%";
    private String codePackageName = "tables";

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles() {
        properties.forEach((k, v) -> {
            if (k != null && k.equals("tableNamePattern")) {
                tableNamePattern = v.toString();
            } else if (k != null && k.equals("codePackageName")) {
                codePackageName = v.toString();
            }
        });

        log.info("enter contextGenerateAdditionalJavaFiles()");
        String tableConstants = "TableConstants";
        FullyQualifiedJavaType className = new FullyQualifiedJavaType(codePackageName + "." + tableConstants);
        //TopLevelEnumeration enumClass = new TopLevelEnumeration(className);
        TopLevelClass topLevelClass = new TopLevelClass(className);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);


        try (Connection connection = context.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getTables(null, "public", tableNamePattern, new String[]{"TABLE"});
            while (resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                String tableRemarks = resultSet.getString("REMARKS");
                if (null == tableRemarks || tableRemarks.trim().isEmpty()) {
                    tableRemarks = tableName;
                }
                Field field = new Field(tableName.toUpperCase(), FullyQualifiedJavaType.getStringInstance());
                field.setVisibility(JavaVisibility.PUBLIC);
                field.setStatic(true);
                field.setFinal(true);
                field.setInitializationString("\"" + tableRemarks + "\"");
                topLevelClass.addField(field);
            }
            JavaFormatter javaFormatter = new DefaultJavaFormatter();
            GeneratedJavaFile generatedJavaFile = new GeneratedJavaFile(
                    topLevelClass,
                    "src/main/java",
                    javaFormatter
            );
            return Collections.singletonList(generatedJavaFile);
        } catch (SQLException e) {
            log.error("SqlException in my plugin", e);
            return List.of();
        }
    }
}
