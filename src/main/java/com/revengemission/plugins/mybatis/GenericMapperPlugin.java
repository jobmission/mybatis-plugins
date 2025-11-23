package com.revengemission.plugins.mybatis;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.JavaFormatter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义查询、更新，生成单独的 mapper 文件
 */
public class GenericMapperPlugin extends AbstractXmbgPlugin {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(GenericMapperPlugin.class);

    private String mapperName = "GenericMapper";
    private String queryForMapMethodName = "queryForMap";
    private String queryForListMethodName = "queryForList";
    private String queryForObjectMethodName = "queryForObject";
    private String countMethodName = "count";
    private String deleteMethodName = "delete";
    private String insertMethodName = "insert";
    private String updateMethodName = "update";
    private String encoding = "UTF-8";


    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        String javaFileEncoding = introspectedTable.getContext().getProperties().getProperty("javaFileEncoding");
        if (javaFileEncoding != null && !javaFileEncoding.trim().isEmpty()) {
            encoding = javaFileEncoding;
        }
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles() {
        FullyQualifiedJavaType longType = new FullyQualifiedJavaType("long");
        FullyQualifiedJavaType mapType = new FullyQualifiedJavaType("Map<String, Object>");
        FullyQualifiedJavaType listMapType = new FullyQualifiedJavaType("List<Map<String, Object>>");
        JavaFormatter javaFormatter = context.getJavaFormatter();
        FullyQualifiedJavaType interfaceType = new FullyQualifiedJavaType(context.getJavaClientGeneratorConfiguration().getTargetPackage() + "." + mapperName);
        Interface anInterface = new Interface(interfaceType);
        anInterface.setVisibility(JavaVisibility.PUBLIC);

        FullyQualifiedJavaType mapperJavaType = new FullyQualifiedJavaType("org.apache.ibatis.annotations.Mapper");
        anInterface.addImportedType(mapperJavaType);
        anInterface.addAnnotation("@Mapper");
        FullyQualifiedJavaType selectJavaType = new FullyQualifiedJavaType("org.apache.ibatis.annotations.Select");
        anInterface.addImportedType(selectJavaType);

        FullyQualifiedJavaType updateJavaType = new FullyQualifiedJavaType("org.apache.ibatis.annotations.Update");
        anInterface.addImportedType(updateJavaType);

        FullyQualifiedJavaType insertJavaType = new FullyQualifiedJavaType("org.apache.ibatis.annotations.Insert");
        anInterface.addImportedType(insertJavaType);

        FullyQualifiedJavaType deleteJavaType = new FullyQualifiedJavaType("org.apache.ibatis.annotations.Delete");
        anInterface.addImportedType(deleteJavaType);

        anInterface.addImportedType(new FullyQualifiedJavaType("java.util.Map"));
        anInterface.addImportedType(FullyQualifiedJavaType.getNewListInstance());

        Method queryForMapMethod = new Method(queryForMapMethodName);
        queryForMapMethod.setAbstract(true);
        queryForMapMethod.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "sql"));
        queryForMapMethod.addParameter(new Parameter(mapType, "parameters"));
        queryForMapMethod.setReturnType(mapType);
        queryForMapMethod.addAnnotation("@Select(\"${sql}\")");
        anInterface.addMethod(queryForMapMethod);

        Method queryForListMethod = new Method(queryForListMethodName);
        queryForListMethod.setAbstract(true);
        queryForListMethod.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "sql"));
        queryForListMethod.addParameter(new Parameter(mapType, "parameters"));
        queryForListMethod.setReturnType(listMapType);
        queryForListMethod.addAnnotation("@Select(\"${sql}\")");
        anInterface.addMethod(queryForListMethod);

        Method queryForObjectMethod = new Method(queryForObjectMethodName);
        queryForObjectMethod.setAbstract(true);
        queryForObjectMethod.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "sql"));
        queryForObjectMethod.addParameter(new Parameter(mapType, "parameters"));
        queryForObjectMethod.setReturnType(FullyQualifiedJavaType.getObjectInstance());
        queryForObjectMethod.addAnnotation("@Select(\"${sql}\")");
        anInterface.addMethod(queryForObjectMethod);

        Method countMethod = new Method(countMethodName);
        countMethod.setAbstract(true);
        countMethod.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "sql"));
        countMethod.addParameter(new Parameter(mapType, "parameters"));
        countMethod.setReturnType(longType);
        countMethod.addAnnotation("@Select(\"${sql}\")");
        anInterface.addMethod(countMethod);

        Method updateMethod = new Method(updateMethodName);
        updateMethod.setAbstract(true);
        updateMethod.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "sql"));
        updateMethod.addParameter(new Parameter(mapType, "parameters"));
        updateMethod.setReturnType(FullyQualifiedJavaType.getIntInstance());
        updateMethod.addAnnotation("@Update(\"${sql}\")");
        anInterface.addMethod(updateMethod);

        Method insertMethod = new Method(insertMethodName);
        insertMethod.setAbstract(true);
        insertMethod.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "sql"));
        insertMethod.addParameter(new Parameter(mapType, "parameters"));
        insertMethod.setReturnType(FullyQualifiedJavaType.getIntInstance());
        insertMethod.addAnnotation("@Insert(\"${sql}\")");
        anInterface.addMethod(insertMethod);

        Method deleteMethod = new Method(deleteMethodName);
        deleteMethod.setAbstract(true);
        deleteMethod.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "sql"));
        deleteMethod.addParameter(new Parameter(mapType, "parameters"));
        deleteMethod.setReturnType(FullyQualifiedJavaType.getIntInstance());
        deleteMethod.addAnnotation("@Delete(\"${sql}\")");
        anInterface.addMethod(deleteMethod);

        GeneratedJavaFile generatedJavaFile = new GeneratedJavaFile(anInterface, context.getJavaClientGeneratorConfiguration().getTargetProject(), encoding, javaFormatter);
        List<GeneratedJavaFile> answer = new ArrayList<>(16);
        answer.add(generatedJavaFile);
        return answer;
    }


}
