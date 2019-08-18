package com.revengemission.plugins.mybatis;


import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.JavaFormatter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.XmlConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 自定义查询、更新，生成单独的mapper文件
 * 在启用Mybatis cache的情况下，会有缓存不同步问题，此时建议使用【MybatisCustomSqlPlugin】
 */
public class MybatisGenericMapperPlugin extends AbstractXmbgPlugin {

    private String mapperName = "GenericMapper";
    private String queryForMapMethodName = "queryForMap";
    private String queryForListMethodName = "queryForList";
    private String queryForObjectMethodName = "queryForObject";
    private String updateMethodName = "update";
    private boolean withMapperAnnotation = true;

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            if ("withMapperAnnotation".equalsIgnoreCase(entry.getKey().toString().trim())) {
                withMapperAnnotation = Boolean.parseBoolean(entry.getValue().toString().trim());
                break;
            }
        }
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles() {
        FullyQualifiedJavaType mapTypeString = new FullyQualifiedJavaType("Map<String, Object>");
        FullyQualifiedJavaType listMapTypeString = new FullyQualifiedJavaType("List<Map<String, Object>>");
        FullyQualifiedJavaType paramType = new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param");
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
        anInterface.addImportedType(paramType);
        anInterface.addImportedType(FullyQualifiedJavaType.getNewListInstance());


        Method queryForMapMetho = new Method(queryForMapMethodName);
        String sqlAnnotation = "@Param(\"sql\")";
        queryForMapMetho.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "sql", sqlAnnotation));
        String mapAnnotation = "@Param(\"paramsMap\")";
        queryForMapMetho.addParameter(new Parameter(mapTypeString, "paramsMap", mapAnnotation));
        queryForMapMetho.setReturnType(mapTypeString);
        anInterface.addMethod(queryForMapMetho);

        Method queryForListMethod = new Method(queryForListMethodName);
        queryForListMethod.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "sql", sqlAnnotation));
        queryForListMethod.addParameter(new Parameter(mapTypeString, "paramsMap", mapAnnotation));
        queryForListMethod.setReturnType(listMapTypeString);
        anInterface.addMethod(queryForListMethod);

        Method queryForObjectMethod = new Method(queryForObjectMethodName);
        queryForObjectMethod.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "sql", sqlAnnotation));
        queryForObjectMethod.addParameter(new Parameter(mapTypeString, "paramsMap", mapAnnotation));
        queryForObjectMethod.setReturnType(FullyQualifiedJavaType.getObjectInstance());
        anInterface.addMethod(queryForObjectMethod);

        Method updateMethod = new Method(updateMethodName);
        updateMethod.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "sql", sqlAnnotation));
        updateMethod.addParameter(new Parameter(mapTypeString, "paramsMap", mapAnnotation));
        updateMethod.setReturnType(FullyQualifiedJavaType.getIntInstance());
        anInterface.addMethod(updateMethod);

        GeneratedJavaFile generatedJavaFile = new GeneratedJavaFile(anInterface, context.getJavaClientGeneratorConfiguration().getTargetProject(), javaFormatter);

        List<GeneratedJavaFile> answer = new ArrayList<>(1);
        answer.add(generatedJavaFile);
        return answer;
    }

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

        List<GeneratedXmlFile> answer = new ArrayList<>(1);
        answer.add(gxf);

        return answer;
    }


}
