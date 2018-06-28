package com.revengemission.plugins.mybatis;


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.slf4j.LoggerFactory;

import java.util.*;

/*
* 批量更新
* */
public class BatchUpdatePlugin extends AbstractXmbgPlugin {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AbstractXmbgPlugin.class);

    private static final String BATCH_UPDATE = "batchUpdate";

    private static final String PROPERTY_PREFIX = "item.";

    Map<String, String> todo = new LinkedHashMap<>();

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        todo.clear();
        properties.forEach((k, v) -> {
            todo.put(StringUtils.trim(k.toString()), StringUtils.trim(v.toString()));
        });
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String objectName = getEntityName(introspectedTable);
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();
        Method method = new Method(BATCH_UPDATE);
        FullyQualifiedJavaType type = new FullyQualifiedJavaType("java.util.List<" + objectName + ">");
        method.addParameter(new Parameter(type, "list"));
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        importedTypes.add(type);
        interfaze.addMethod(method);
        interfaze.addImportedTypes(importedTypes);
        return true;
    }


    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        XmlElement update = new XmlElement("update");
        update.addAttribute(new Attribute("id", BATCH_UPDATE));

        String parameterType = "java.util.List";

        update.addAttribute(new Attribute("parameterType", parameterType));

        XmlElement foreach = new XmlElement("foreach");
        foreach.addAttribute(new Attribute("collection", "list"));
        foreach.addAttribute(new Attribute("item", "item"));
        foreach.addAttribute(new Attribute("index", "index"));
        foreach.addAttribute(new Attribute("separator", ";"));

        generateTextBlockAppendTableName("update ", introspectedTable, foreach);

        TextElement setElement = new TextElement("set"); //$NON-NLS-1$
        foreach.addElement(setElement);

        generateParameterForSet(PROPERTY_PREFIX, introspectedTable.getNonPrimaryKeyColumns(), foreach);
        replaceElement(foreach, todo);

        generateWhereConditions(PROPERTY_PREFIX, introspectedTable.getPrimaryKeyColumns(), foreach);

        update.addElement(foreach);
        document.getRootElement().addElement(update);

        return true;
    }


}
