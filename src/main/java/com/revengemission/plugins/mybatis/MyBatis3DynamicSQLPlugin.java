package com.revengemission.plugins.mybatis;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.config.PropertyRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * MyBatis Dynamic SQL
 * http://www.mybatis.org/mybatis-dynamic-sql/docs/introduction.html
 * <p>
 * targetRuntime="MyBatis3DynamicSQL"
 */
public class MyBatis3DynamicSQLPlugin extends AbstractXmbgPlugin {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }


    private String calculateClassName(IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType mapperType = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType());
        FullyQualifiedJavaType recordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        return mapperType.getPackageName() + "." + recordType.getShortNameWithoutTypeArguments() + "FieldHelper"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    private String calculateSupportClassName(IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType mapperType = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType());
        FullyQualifiedJavaType recordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        return mapperType.getPackageName() + "." + recordType.getShortNameWithoutTypeArguments() + "DynamicSqlSupport"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(
            IntrospectedTable introspectedTable) {

        List<GeneratedJavaFile> answer = new ArrayList<>();


        Method existField = new Method("existField");
        existField.setVisibility(JavaVisibility.PUBLIC);
        existField.setStatic(true);
        existField.setReturnType(new FullyQualifiedJavaType("boolean"));

        Parameter fieldName = new Parameter(FullyQualifiedJavaType.getStringInstance(), "fieldName", false);
        existField.addParameter(fieldName);

        StringBuffer stringBufferGetField = new StringBuffer();
        stringBufferGetField.append("Map<String, SqlColumn> allColumns = new HashMap<>();\n");
        for (IntrospectedColumn field : introspectedTable.getAllColumns()) {
            stringBufferGetField.append("        allColumns.put(\"" + field.getJavaProperty() + "\", " + field.getJavaProperty() + ");\n");
        }
        stringBufferGetField.append("        if (allColumns.containsKey(fieldName)) {\n");
        stringBufferGetField.append("            return true;\n");
        stringBufferGetField.append("        } else {\n");
        stringBufferGetField.append("            return false;\n");
        stringBufferGetField.append("        }");
        existField.addBodyLine(stringBufferGetField.toString());

        Method getSortField = new Method("getSortField");
        getSortField.setVisibility(JavaVisibility.PUBLIC);
        getSortField.setStatic(true);
        getSortField.setReturnType(new FullyQualifiedJavaType("SortSpecification"));

        Parameter sortFieldParameter = new Parameter(FullyQualifiedJavaType.getStringInstance(), "sortField", false);
        Parameter sortOrderParameter = new Parameter(FullyQualifiedJavaType.getStringInstance(), "sortOrder", false);
        getSortField.addParameter(sortFieldParameter);
        getSortField.addParameter(sortOrderParameter);

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Map<String, SqlColumn> allColumns = new HashMap<>();\n");
        for (IntrospectedColumn field : introspectedTable.getAllColumns()) {
            stringBuffer.append("        allColumns.put(\"" + field.getJavaProperty() + "\", " + field.getJavaProperty() + ");\n");
        }
        stringBuffer.append("        if (allColumns.containsKey(sortField)) {\n");
        stringBuffer.append("            if (StringUtils.equalsIgnoreCase(\"asc\", sortOrder)) {\n");
        stringBuffer.append("                return allColumns.get(sortField);\n");
        stringBuffer.append("            } else {\n");
        stringBuffer.append("                return allColumns.get(sortField).descending();\n");
        stringBuffer.append("            }\n");
        stringBuffer.append("        } else {\n");
        stringBuffer.append("            return id.descending();\n");
        stringBuffer.append("        }");
        getSortField.addBodyLine(stringBuffer.toString());

        TopLevelClass topLevelClass = new TopLevelClass(calculateClassName(introspectedTable));
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        topLevelClass.setFinal(true);
        topLevelClass.addImportedType(new FullyQualifiedJavaType("org.apache.commons.lang3.StringUtils")); //$NON-NLS-1$
        topLevelClass.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.SortSpecification")); //$NON-NLS-1$
        topLevelClass.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.SqlColumn")); //$NON-NLS-1$
        topLevelClass.addImportedType(new FullyQualifiedJavaType("java.util.HashMap")); //$NON-NLS-1$
        topLevelClass.addImportedType(new FullyQualifiedJavaType("java.util.Map")); //$NON-NLS-1$
        topLevelClass.addStaticImport(calculateSupportClassName(introspectedTable) + ".*"); //$NON-NLS-1$
        topLevelClass.addMethod(getSortField);
        topLevelClass.addMethod(existField);

        GeneratedJavaFile gjf = new GeneratedJavaFile(topLevelClass,
                context.getJavaClientGeneratorConfiguration()
                        .getTargetProject(),
                context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                context.getJavaFormatter());
        answer.add(gjf);

        return answer;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String objectName = getEntityName(introspectedTable);

        Method selectUniqueByExample = new Method("selectUniqueByExample");
        selectUniqueByExample.addAnnotation("@Generated(\"org.mybatis.generator.api.MyBatisGenerator\")");
        selectUniqueByExample.setDefault(true);
        selectUniqueByExample.setReturnType(new FullyQualifiedJavaType("QueryExpressionDSL<MyBatis3SelectModelAdapter<" + objectName + ">>"));
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("return SelectDSL.selectWithMapper(this::selectOne");
        for (IntrospectedColumn field : introspectedTable.getAllColumns()) {
            stringBuffer.append(", ");
            stringBuffer.append(field.getJavaProperty());
        }
        stringBuffer.append(")\n                .from(");
        stringBuffer.append(toLowerCaseFirstChar(objectName));
        stringBuffer.append(");");
        selectUniqueByExample.addBodyLine(stringBuffer.toString());
        interfaze.addMethod(selectUniqueByExample);
        return true;
    }

}
