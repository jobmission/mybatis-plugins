package com.revengemission.plugins.mybatis;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 插入数据时，重复键更新
 */
public class InsertOnUpdatePlugin extends AbstractXmbgPlugin {

    private static final String CLIENT_METHOD_NAME_SINGLE = "insertOnUpdate";
    private static final String CLIENT_METHOD_NAME_BATCH = "batchInsertOnUpdate";

    private static final String PROPERTY_PREFIX = "item.";

    Map<String, String> todo = new LinkedHashMap<>();

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        todo.clear();
        String currentTableName = getTableName(introspectedTable);
        properties.forEach((k, v) -> {
            if (currentTableName.equalsIgnoreCase(k.toString().trim())) {
                todo.put(currentTableName, v.toString().trim());
            }
        });
    }

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        todo.forEach((k, v) -> {
            String objectName = getEntityName(introspectedTable);

            Method methodSingle = new Method(CLIENT_METHOD_NAME_SINGLE);
            methodSingle.setAbstract(true);
            methodSingle.addParameter(new Parameter(new FullyQualifiedJavaType(objectName), "object"));
            methodSingle.setReturnType(FullyQualifiedJavaType.getIntInstance());
            interfaze.addMethod(methodSingle);

            Method methodBatch = new Method(CLIENT_METHOD_NAME_BATCH);
            methodBatch.setAbstract(true);
            FullyQualifiedJavaType type = new FullyQualifiedJavaType("java.util.List<" + objectName + ">");
            methodBatch.addParameter(new Parameter(type, "list"));
            methodBatch.setReturnType(FullyQualifiedJavaType.getIntInstance());
            interfaze.addMethod(methodBatch);
        });


        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        todo.forEach((k, v) -> {
            TextElement onUpdateElement = new TextElement("ON DUPLICATE KEY UPDATE ");
            TextElement updateClauseTextElement = new TextElement(v);

            XmlElement insertXmlElement = new XmlElement("insert");

            insertXmlElement.addAttribute(new Attribute("id", CLIENT_METHOD_NAME_SINGLE));
            insertXmlElement.addAttribute(new Attribute("parameterType", context.getJavaModelGeneratorConfiguration().getTargetPackage() + "." + getEntityName(introspectedTable)));

            generateTextBlockAppendTableName("insert into ", introspectedTable, insertXmlElement);

            generateActualColumnNamesWithParenthesis(introspectedTable.getNonBLOBColumns(), insertXmlElement);

            insertXmlElement.addElement(new TextElement("values "));


            generateParametersSeparateByCommaWithParenthesis(PROPERTY_PREFIX, introspectedTable.getNonBLOBColumns(), insertXmlElement);
            insertXmlElement.addElement(new TextElement("AS newRowValue"));
            insertXmlElement.addElement(onUpdateElement);
            insertXmlElement.addElement(updateClauseTextElement);

            document.getRootElement().addElement(insertXmlElement);


            XmlElement batchInsertXmlElement = new XmlElement("insert");

            batchInsertXmlElement.addAttribute(new Attribute("id", CLIENT_METHOD_NAME_BATCH));
            FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType("java.util.List");
            batchInsertXmlElement.addAttribute(new Attribute("parameterType", parameterType.getFullyQualifiedName()));

            generateTextBlockAppendTableName("insert into ", introspectedTable, batchInsertXmlElement);

            generateActualColumnNamesWithParenthesis(introspectedTable.getNonBLOBColumns(), batchInsertXmlElement);

            batchInsertXmlElement.addElement(new TextElement("values "));

            XmlElement foreach = new XmlElement("foreach");
            foreach.addAttribute(new Attribute("collection", "list"));
            foreach.addAttribute(new Attribute("item", "item"));
            foreach.addAttribute(new Attribute("index", "index"));
            foreach.addAttribute(new Attribute("separator", ","));

            generateParametersSeparateByCommaWithParenthesis(PROPERTY_PREFIX, introspectedTable.getNonBLOBColumns(), foreach);

            batchInsertXmlElement.addElement(foreach);
            batchInsertXmlElement.addElement(new TextElement("AS newRowValue"));
            batchInsertXmlElement.addElement(onUpdateElement);
            batchInsertXmlElement.addElement(updateClauseTextElement);

            document.getRootElement().addElement(batchInsertXmlElement);
        });

        return true;
    }


}
