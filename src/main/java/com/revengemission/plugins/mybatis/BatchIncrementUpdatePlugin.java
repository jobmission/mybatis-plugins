package com.revengemission.plugins.mybatis;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.*;

public class BatchIncrementUpdatePlugin extends AbstractXmbgPlugin {

    private static final String BATCH_INCREMENT_UPDATE = "batchIncrementUpdate";

    Map<String, String> todo = new LinkedHashMap<>();

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
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
        String tableName = getTableName(introspectedTable);
        todo.forEach((k, v) -> {
            if (StringUtils.startsWith(k, tableName)) {
                Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();
                Method method = new Method(BATCH_INCREMENT_UPDATE);
                FullyQualifiedJavaType type = new FullyQualifiedJavaType("java.util.List<java.util.Map>");
                method.addParameter(new Parameter(type, "list"));
                method.setReturnType(FullyQualifiedJavaType.getIntInstance());
                importedTypes.add(type);
                interfaze.addMethod(method);
                interfaze.addImportedTypes(importedTypes);
            }
        });

        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        String tableName = getTableName(introspectedTable);
        todo.forEach((k, v) -> {
            if (StringUtils.startsWith(k, tableName)) {

                XmlElement update = new XmlElement("update");
                update.addAttribute(new Attribute("id", BATCH_INCREMENT_UPDATE));

                String parameterType = "java.util.List";

                update.addAttribute(new Attribute("parameterType", parameterType));

                XmlElement foreach = new XmlElement("foreach");
                foreach.addAttribute(new Attribute("collection", "list"));
                foreach.addAttribute(new Attribute("item", "item"));
                foreach.addAttribute(new Attribute("index", "index"));
                foreach.addAttribute(new Attribute("separator", ";"));

                foreach.addElement(new TextElement(v));
                if (v.indexOf("where") <= 0) {
                    generateWhereConditions("item.", introspectedTable.getPrimaryKeyColumns(), foreach);
                }

                update.addElement(foreach);

                document.getRootElement().addElement(update);
            }
        });

        return true;
    }

}
