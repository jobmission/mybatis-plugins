package com.revengemission.plugins.mybatis;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 统计表的行数，生成单独的 mapper 文件
 */
public class TableStatisticsPlugin extends AbstractXmbgPlugin {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(TableStatisticsPlugin.class);

    private final String mapperName = "TableStatisticsMapper";
    private final String CLIENT_METHOD_NAME = "tableStatistics";
    AtomicInteger atomicIntegerJava = new AtomicInteger(0);
    AtomicInteger atomicIntegerXml = new AtomicInteger(0);

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles() {
        if (atomicIntegerJava.get() == 0) {
            FullyQualifiedJavaType listMapType = new FullyQualifiedJavaType("List<Map<String, Object>>");
            FullyQualifiedJavaType interfaceType = new FullyQualifiedJavaType(context.getClientGeneratorConfiguration().get().getTargetPackage() + "." + mapperName);
            Interface anInterface = new Interface(interfaceType);
            anInterface.setVisibility(JavaVisibility.PUBLIC);

            FullyQualifiedJavaType mapperJavaType = new FullyQualifiedJavaType("org.apache.ibatis.annotations.Mapper");
            anInterface.addImportedType(mapperJavaType);
            anInterface.addAnnotation("@Mapper");

            anInterface.addImportedType(new FullyQualifiedJavaType("java.util.Map"));
            anInterface.addImportedType(FullyQualifiedJavaType.getNewListInstance());

            Method queryForMapMethod = new Method(CLIENT_METHOD_NAME);
            queryForMapMethod.setAbstract(true);
            queryForMapMethod.setReturnType(listMapType);
            anInterface.addMethod(queryForMapMethod);

            GeneratedJavaFile generatedJavaFile = new GeneratedJavaFile(anInterface, context.getClientGeneratorConfiguration().get().getTargetProject(), false);
            List<GeneratedJavaFile> answer = new ArrayList<>(16);
            answer.add(generatedJavaFile);
            atomicIntegerJava.incrementAndGet();
            return answer;
        } else {
            System.out.println("skip java Generate，current count" + atomicIntegerJava.get());
            atomicIntegerJava.incrementAndGet();
            return super.contextGenerateAdditionalJavaFiles();
        }

    }

    @Override
    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles() {
        if (atomicIntegerXml.get() == 0) {
            XmlElement selectElement = new XmlElement("select");
            selectElement.addAttribute(new Attribute("id", CLIENT_METHOD_NAME));
            selectElement.addAttribute(new Attribute("resultType", "java.util.HashMap"));
            XmlElement chooseXmlElement = new XmlElement("choose");
            XmlElement whenPostgresqlElement = new XmlElement("when");
            whenPostgresqlElement.addAttribute(new Attribute("test", "_databaseId == 'postgresql'"));
            whenPostgresqlElement.addElement(new TextElement("SELECT"));
            whenPostgresqlElement.addElement(new TextElement("schemaname AS \"schema\","));
            whenPostgresqlElement.addElement(new TextElement("relname AS \"table_name\","));
            whenPostgresqlElement.addElement(new TextElement("n_live_tup AS \"table_rows\","));
            whenPostgresqlElement.addElement(new TextElement("n_dead_tup"));
            whenPostgresqlElement.addElement(new TextElement("FROM pg_stat_user_tables"));
            whenPostgresqlElement.addElement(new TextElement("ORDER BY n_live_tup DESC;"));

            XmlElement whenMysqlElement = new XmlElement("when");
            whenMysqlElement.addAttribute(new Attribute("test", "_databaseId == 'mysql'"));
            whenMysqlElement.addElement(new TextElement("SELECT"));
            whenMysqlElement.addElement(new TextElement("table_name AS \"table_name\","));
            whenMysqlElement.addElement(new TextElement("table_rows AS \"table_rows\","));
            whenMysqlElement.addElement(new TextElement("data_length AS \"data_size_byte\""));
            whenMysqlElement.addElement(new TextElement("FROM information_schema.tables"));
            whenMysqlElement.addElement(new TextElement("WHERE table_schema = DATABASE()"));
            whenMysqlElement.addElement(new TextElement("AND table_type = 'BASE TABLE'"));
            whenMysqlElement.addElement(new TextElement("ORDER BY table_rows DESC"));

            chooseXmlElement.addElement(whenPostgresqlElement);
            chooseXmlElement.addElement(whenMysqlElement);

            selectElement.addElement(chooseXmlElement);
            XmlElement mapperElement = new XmlElement("mapper");
            mapperElement.addAttribute(new Attribute("namespace", context.getClientGeneratorConfiguration().get().getTargetPackage() + "." + mapperName));
            mapperElement.addElement(selectElement);
            Document document = new Document("-//mybatis.org//DTD Mapper 3.0//EN", "https://mybatis.org/dtd/mybatis-3-mapper.dtd", mapperElement);
            GeneratedXmlFile generatedXmlFile = new GeneratedXmlFile(document, mapperName + ".xml", context.getSqlMapGeneratorConfiguration().get().getTargetPackage(), context.getSqlMapGeneratorConfiguration().get().getTargetProject(), false);
            atomicIntegerXml.incrementAndGet();
            return List.of(generatedXmlFile);
        } else {
            System.out.println("skip xml Generate，current count" + atomicIntegerXml.get());
            atomicIntegerXml.incrementAndGet();
            return super.contextGenerateAdditionalXmlFiles();
        }

    }
}
