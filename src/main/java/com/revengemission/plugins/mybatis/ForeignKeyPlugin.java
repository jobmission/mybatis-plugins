package com.revengemission.plugins.mybatis;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.VisitableElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 外键对象
 * 修改文件
 * 1、xxxExample.xml, BaseResultMap、Base_Column_List
 * 2、XXXEntity.java, 添加引用对象
 */
public class ForeignKeyPlugin extends AbstractXmbgPlugin {

    private static final Logger log = LoggerFactory.getLogger(ForeignKeyPlugin.class);

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        log.info("enter modelBaseRecordClassGenerated");
        String tableName = getTableName(introspectedTable);
        List<ForeignKeyItem> foreignKeyItemList = getForeignKeys(introspectedTable);
        log.info("table {} foreignKey size {}", tableName, foreignKeyItemList.size());
        for (ForeignKeyItem foreignKeyItem : foreignKeyItemList) {
            String domainNameName = tableNameToEntityName(foreignKeyItem.getPkTableName());
            String fieldName = lowerCaseFirstChar(domainNameName.replace("Entity", ""));
            Field field = new Field(fieldName, new FullyQualifiedJavaType(domainNameName));
            field.setVisibility(JavaVisibility.PRIVATE);
            topLevelClass.addField(field);

            Method setMethod = new Method("set" + domainNameName.replace("Entity", ""));
            setMethod.setVisibility(JavaVisibility.PUBLIC);
            setMethod.addBodyLine("this." + fieldName + " = " + fieldName + ";");
            setMethod.addParameter(new Parameter(new FullyQualifiedJavaType(domainNameName), fieldName));
            topLevelClass.addMethod(setMethod);

            Method getMethod = new Method("get" + domainNameName.replace("Entity", ""));
            getMethod.setVisibility(JavaVisibility.PUBLIC);
            getMethod.setReturnType(new FullyQualifiedJavaType(domainNameName));
            getMethod.addBodyLine("return " + fieldName + ";");
            topLevelClass.addMethod(getMethod);
        }
        return true;
    }

    @Override
    public boolean sqlMapBaseColumnListElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        log.info("enter sqlMapBaseColumnListElementGenerated");
        String tableName = getTableName(introspectedTable);
        List<ForeignKeyItem> foreignKeyItemList = getForeignKeys(introspectedTable);
        if (!foreignKeyItemList.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            introspectedTable.getPrimaryKeyColumns().forEach(column -> {
                stringBuilder.append(",").append("mt").append(".").append(column.getActualColumnName());
            });
            introspectedTable.getBaseColumns().forEach(column -> {
                stringBuilder.append(",").append("mt").append(".").append(column.getActualColumnName());
            });
            AtomicInteger atomicInteger = new AtomicInteger(1);

            foreignKeyItemList.forEach(foreignKeyItem -> {
                Map<String, String> tableColumnsMap = getTableColumns(foreignKeyItem.getPkTableName());
                tableColumnsMap.keySet().forEach(columnName -> {
                    stringBuilder.append(", childT").append(atomicInteger.get()).append(".").append(columnName).append(" as childT").append(atomicInteger.get()).append("_").append(columnName);
                });
                atomicInteger.incrementAndGet();
            });
            element.getElements().clear();
            element.addElement(new TextElement(stringBuilder.toString().replaceFirst(",", "")));
        }
        return true;
    }

    @Override
    public boolean sqlMapExampleWhereClauseElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        XmlElement chooseChild = findFirstMatchedXmlElement(element, "choose");
        if (chooseChild != null) {
            List<VisitableElement> tempList = chooseChild.getElements();
            chooseChild.getElements().forEach(whenElement -> {
                if (whenElement instanceof XmlElement xmlElement) {
                    for (int i = 0; i < xmlElement.getElements().size(); i++) {
                        VisitableElement visitableElement = xmlElement.getElements().get(i);
                        if (visitableElement instanceof TextElement textElement) {
                            String content = textElement.getContent();
                            xmlElement.getElements().remove(i);
                            xmlElement.getElements().add(i, new TextElement("and mt." + content.replace("and ", "")));
                        }
                    }

                }

            });
        }

        return true;
    }

    @Override
    public boolean sqlMapCountByExampleElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        String tableName = getTableName(introspectedTable);
        log.info("enter sqlMapCountByExampleElementGenerated {}", tableName);
        List<ForeignKeyItem> foreignKeyItemList = getForeignKeys(introspectedTable);
        if (!foreignKeyItemList.isEmpty()) {
            String oldStr = "select count(*) from " + tableName;
            String newStr = "select count(*) from " + tableName + " mt \n";
            Map<String, String> replacement = new LinkedHashMap<>();
            replacement.put(oldStr, newStr);
            replaceElement(element, replacement);
        }
        return true;
    }

    @Override
    public boolean sqlMapDeleteByExampleElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        String tableName = getTableName(introspectedTable);
        log.info("enter sqlMapDeleteByExampleElementGenerated {}", tableName);
        List<ForeignKeyItem> foreignKeyItemList = getForeignKeys(introspectedTable);
        if (!foreignKeyItemList.isEmpty()) {
            String oldStr = "delete from " + tableName;
            String newStr = "sedelete from " + tableName + " mt \n";
            Map<String, String> replacement = new LinkedHashMap<>();
            replacement.put(oldStr, newStr);
            replaceElement(element, replacement);
        }
        return true;
    }

    @Override
    public boolean sqlMapResultMapWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        log.info("enter sqlMapBaseColumnListElementGenerated");
        List<ForeignKeyItem> foreignKeyItemList = getForeignKeys(introspectedTable);
        if (!foreignKeyItemList.isEmpty()) {
            AtomicInteger atomicInteger = new AtomicInteger(1);
            foreignKeyItemList.forEach(foreignKeyItem -> {
                String domainName = tableNameToEntityName(foreignKeyItem.getPkTableName());
                String fieldName = lowerCaseFirstChar(domainName.replace("Entity", ""));
                XmlElement associationElement = new XmlElement("association");
                associationElement.addAttribute(new Attribute("property", fieldName));
                associationElement.addAttribute(new Attribute("javaType", context.getJavaModelGeneratorConfiguration().getTargetPackage() + "." + domainName));

                Map<String, String> tableColumnsMap = getTableColumns(foreignKeyItem.getPkTableName());
                tableColumnsMap.keySet().forEach(columnName -> {
                    if ("id".equals(columnName)) {
                        XmlElement associationChild = new XmlElement("id");
                        associationChild.addAttribute(new Attribute("property", "id"));
                        associationChild.addAttribute(new Attribute("column", "childT" + atomicInteger.get() + "_id"));
                        associationElement.addElement(associationChild);
                    } else {
                        XmlElement associationChild = new XmlElement("result");
                        associationChild.addAttribute(new Attribute("property", camelName(columnName)));
                        associationChild.addAttribute(new Attribute("column", "childT" + atomicInteger.get() + "_" + columnName));
                        associationElement.addElement(associationChild);
                    }
                });
                atomicInteger.incrementAndGet();
                element.addElement(associationElement);
            });

        }
        return true;
    }

    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        String tableName = getTableName(introspectedTable);
        log.info("enter sqlMapSelectByExampleWithoutBLOBsElementGenerated {}", tableName);
        List<ForeignKeyItem> foreignKeyItemList = getForeignKeys(introspectedTable);
        if (!foreignKeyItemList.isEmpty()) {
            AtomicInteger atomicInteger = new AtomicInteger(1);
            StringBuilder stringBuilder = new StringBuilder("from " + tableName + " mt\n");
            foreignKeyItemList.forEach(foreignKeyItem -> {
                String childTableName = "childT" + atomicInteger.get();
                stringBuilder.append("\tleft join ").append(foreignKeyItem.getPkTableName()).append(" ").append(childTableName).append(" on mt.").append(foreignKeyItem.getFkColumnName()).append(" = ").append(childTableName).append(".").append(foreignKeyItem.getPkColumnName()).append("\n");
                atomicInteger.incrementAndGet();
            });
            Map<String, String> replacement = new LinkedHashMap<>();
            replacement.put("from " + tableName, stringBuilder.toString());
            replaceElement(element, replacement);
        }
        return true;
    }
}
