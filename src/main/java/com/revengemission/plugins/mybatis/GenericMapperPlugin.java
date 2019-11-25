package com.revengemission.plugins.mybatis;


import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.JavaFormatter;
import org.mybatis.generator.api.dom.java.*;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 自定义查询、更新，生成单独的 mapper 文件
 * 在启用Mybatis cache的情况下，会有缓存不同步问题，此时建议使用【MybatisCustomSqlPlugin】
 */
public class GenericMapperPlugin extends AbstractXmbgPlugin {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(GenericMapperPlugin.class);

    private String mapperName = "GenericMapper";
    private String queryForMapMethodName = "queryForMap";
    private String queryForListMethodName = "queryForList";
    private String queryForObjectMethodName = "queryForObject";
    private String updateMethodName = "update";
    private boolean withMapperAnnotation = true;
    private String pluginPackageRelativePath = "com/revengemission/plugins/mybatis/";


    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            if ("withMapperAnnotation".equalsIgnoreCase(entry.getKey().toString().trim())) {
                withMapperAnnotation = Boolean.parseBoolean(entry.getValue().toString().trim());
                break;
            }
        }

        createJavaFile("DynamicSqlSourceX");
        createJavaFile("XMLScriptBuilderX");
        createJavaFile("MatchAnyLangDriver");
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles() {
        FullyQualifiedJavaType mapTypeString = new FullyQualifiedJavaType("Map<String, Object>");
        FullyQualifiedJavaType listMapTypeString = new FullyQualifiedJavaType("List<Map<String, Object>>");
///        FullyQualifiedJavaType paramType = new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param");
        JavaFormatter javaFormatter = context.getJavaFormatter();
        FullyQualifiedJavaType interfaceType = new FullyQualifiedJavaType(context.getJavaClientGeneratorConfiguration().getTargetPackage() + "." + mapperName);
        Interface anInterface = new Interface(interfaceType);
        anInterface.setVisibility(JavaVisibility.PUBLIC);
        anInterface.addImportedType(FullyQualifiedJavaType.getNewMapInstance());

        if (withMapperAnnotation) {
            FullyQualifiedJavaType mapperJavaType = new FullyQualifiedJavaType("org.apache.ibatis.annotations.Mapper");
            anInterface.addImportedType(mapperJavaType);
            anInterface.addAnnotation("@Mapper");
        }
///        anInterface.addImportedType(paramType);
        anInterface.addImportedType(FullyQualifiedJavaType.getNewListInstance());

        FullyQualifiedJavaType langJavaType = new FullyQualifiedJavaType("org.apache.ibatis.annotations.Lang");
        anInterface.addImportedType(langJavaType);

        FullyQualifiedJavaType selectJavaType = new FullyQualifiedJavaType("org.apache.ibatis.annotations.Select");
        anInterface.addImportedType(selectJavaType);

        FullyQualifiedJavaType updateJavaType = new FullyQualifiedJavaType("org.apache.ibatis.annotations.Update");
        anInterface.addImportedType(updateJavaType);

        Method queryForMapMethod = new Method(queryForMapMethodName);
        queryForMapMethod.setAbstract(true);
        queryForMapMethod.addParameter(new Parameter(mapTypeString, "paramsMapWithSql"));
        queryForMapMethod.setReturnType(mapTypeString);
        queryForMapMethod.addAnnotation("@Select(\"<match_any>不需要修改这一行！paramsMapWithSql 中放入sql 语句以及需要的占位符参数</match_any>\")");
        queryForMapMethod.addAnnotation("@Lang(MatchAnyLangDriver.class)");
        anInterface.addMethod(queryForMapMethod);

        Method queryForListMethod = new Method(queryForListMethodName);
        queryForListMethod.setAbstract(true);
        queryForListMethod.addParameter(new Parameter(mapTypeString, "paramsMapWithSql"));
        queryForListMethod.setReturnType(listMapTypeString);
        queryForListMethod.addAnnotation("@Select(\"<match_any>不需要修改这一行！paramsMapWithSql 中放入sql 语句以及需要的占位符参数</match_any>\")");
        queryForListMethod.addAnnotation("@Lang(MatchAnyLangDriver.class)");
        anInterface.addMethod(queryForListMethod);

        Method queryForObjectMethod = new Method(queryForObjectMethodName);
        queryForObjectMethod.setAbstract(true);
        queryForObjectMethod.addParameter(new Parameter(mapTypeString, "paramsMapWithSql"));
        queryForObjectMethod.setReturnType(FullyQualifiedJavaType.getObjectInstance());
        queryForObjectMethod.addAnnotation("@Select(\"<match_any>不需要修改这一行！paramsMapWithSql 中放入sql 语句以及需要的占位符参数</match_any>\")");
        queryForObjectMethod.addAnnotation("@Lang(MatchAnyLangDriver.class)");
        anInterface.addMethod(queryForObjectMethod);

        Method updateMethod = new Method(updateMethodName);
        updateMethod.setAbstract(true);
///        String sqlAnnotation = "@Param(\"sql\")";
///        String mapAnnotation = "@Param(\"paramsMap\")";
///        updateMethod.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "sql", sqlAnnotation));
        updateMethod.addParameter(new Parameter(mapTypeString, "paramsMapWithSql"));
        updateMethod.setReturnType(FullyQualifiedJavaType.getIntInstance());
        updateMethod.addAnnotation("@Update(\"<match_any>不需要修改这一行！paramsMapWithSql 中放入sql 语句以及需要的占位符参数</match_any>\")");
        updateMethod.addAnnotation("@Lang(MatchAnyLangDriver.class)");
        anInterface.addMethod(updateMethod);

        GeneratedJavaFile generatedJavaFile = new GeneratedJavaFile(anInterface, context.getJavaClientGeneratorConfiguration().getTargetProject(), javaFormatter);

        List<GeneratedJavaFile> answer = new ArrayList<>(16);
        answer.add(generatedJavaFile);
        return answer;
    }

    /*
    @Override
    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles() {
        Document document = new Document(
            XmlConstants.MYBATIS3_MAPPER_PUBLIC_ID,
            XmlConstants.MYBATIS3_MAPPER_SYSTEM_ID);

        XmlElement root = new XmlElement("mapper");
        Attribute namespaceAttribute = new Attribute("namespace", context.getJavaClientGeneratorConfiguration().getTargetPackage() + "." + mapperName);
        root.addAttribute(namespaceAttribute);
        document.setRootElement(root);

        XmlElement queryForMapElement = new XmlElement("select");
        queryForMapElement.addAttribute(new Attribute("id", queryForMapMethodName));
        queryForMapElement.addAttribute(new Attribute("resultType", "HashMap"));
        queryForMapElement.addAttribute(new Attribute("parameterType", "Map"));

        queryForMapElement.addElement(
            new TextElement("${sql}"
            ));

        root.addElement(queryForMapElement);

        XmlElement queryForListElement = new XmlElement("select");
        queryForListElement.addAttribute(new Attribute("id", queryForListMethodName));
        queryForListElement.addAttribute(new Attribute("resultType", "Map"));
        queryForListElement.addAttribute(new Attribute("parameterType", "Map"));

        queryForListElement.addElement(
            new TextElement("${sql}"
            ));

        root.addElement(queryForListElement);

        XmlElement queryForObjectElement = new XmlElement("select");
        queryForObjectElement.addAttribute(new Attribute("id", queryForObjectMethodName));
        queryForObjectElement.addAttribute(new Attribute("resultType", "Object"));
        queryForObjectElement.addAttribute(new Attribute("parameterType", "Map"));

        queryForObjectElement.addElement(
            new TextElement("${sql}"
            ));

        root.addElement(queryForObjectElement);

        XmlElement upateElement = new XmlElement("update");
        upateElement.addAttribute(new Attribute("id", updateMethodName));
        upateElement.addAttribute(new Attribute("parameterType", "Map"));

        upateElement.addElement(
            new TextElement("${sql}"
            ));

        root.addElement(upateElement);

        GeneratedXmlFile gxf = new GeneratedXmlFile(document, properties
            .getProperty("fileName", mapperName + ".xml"),
            context.getSqlMapGeneratorConfiguration().getTargetPackage(),
            context.getSqlMapGeneratorConfiguration().getTargetProject(),
            false, context.getXmlFormatter());

        List<GeneratedXmlFile> answer = new ArrayList<>(16);
        answer.add(gxf);

        return answer;
    }
*/

    /**
     * 根据txt文件生成java文件
     *
     * @param txtFileName txt文件名和java文件名
     */
    void createJavaFile(String txtFileName) {
        String currentPath = System.getProperty("user.dir");
        String targetProject = context.getJavaClientGeneratorConfiguration().getTargetProject();
        String targetPackage = context.getJavaClientGeneratorConfiguration().getTargetPackage();
        Path targetClassFilePath = Paths.get(currentPath, targetProject, targetPackage.replace(".", File.separator));
        log.info("targetClassFilePath:" + targetClassFilePath.toString());
        try {
            StringBuffer buffer = new StringBuffer();
            String line = "";
            BufferedReader in = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(pluginPackageRelativePath + txtFileName + ".txt")));
            while ((line = in.readLine()) != null) {
                buffer.append(line);
                buffer.append("\r\n");
            }
            String input = buffer.toString();

            Files.write(Paths.get(targetClassFilePath.toString(), txtFileName + ".java"), ("package " + context.getJavaClientGeneratorConfiguration().getTargetPackage() + ";\r\n").getBytes("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            Files.write(Paths.get(targetClassFilePath.toString(), txtFileName + ".java"), input.getBytes("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("读取、写入文件错误：", e);
        }

    }


}
