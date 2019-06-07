package com.revengemission.plugins.mybatis;


import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.JavaFormatter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.XmlConstants;

import java.util.ArrayList;
import java.util.List;

/*
 *
 * 生成单独的mapper文件
 * Supplied Plugins
 *       http://www.mybatis.org/generator/reference/plugins.html
 *
 * */
public class MybatisCustomQueryPlugin extends AbstractXmbgPlugin {

    String mapperName = "CustomQueryMapper";
    String queryForMapMethodName = "queryForMap";
    String queryForListMethodName = "queryForList";
    String queryForObjectMethodName = "queryForObject";

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

        GeneratedXmlFile gxf = new GeneratedXmlFile(document, properties
                .getProperty("fileName", "CustomerQueryMapper.xml"), //$NON-NLS-1$ //$NON-NLS-2$
                context.getSqlMapGeneratorConfiguration().getTargetPackage(), //$NON-NLS-1$
                context.getSqlMapGeneratorConfiguration().getTargetProject(), //$NON-NLS-1$
                false, context.getXmlFormatter());

        List<GeneratedXmlFile> answer = new ArrayList<>(1);
        answer.add(gxf);

        return answer;
    }


}
