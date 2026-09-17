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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 根据Example查询单条记录
 */
public class SelectUniqueByExamplePlugin extends AbstractXmbgPlugin {

    private static final String CLIENT_METHOD_NAME = "selectUniqueByExample";
    private static final String CLIENT_METHOD_NAME_UNIQUE = "selectUnique";

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {

        Map<String, List<String>> listMap = getUniqueConstraintKeys(introspectedTable);
        boolean flag = false;
        List<String> uniqueConstraintKeys = new ArrayList<>();
        if (listMap != null && !listMap.isEmpty()) {
            for (Map.Entry<String, List<String>> entry : listMap.entrySet()) {
                String uniqueConstraintName = entry.getKey();
                if (uniqueConstraintName.toLowerCase().startsWith("idx_") || uniqueConstraintName.toLowerCase().startsWith("index_") || uniqueConstraintName.toLowerCase().startsWith("uc_") || uniqueConstraintName.toLowerCase().startsWith("uk_")) {
                    flag = true;
                    uniqueConstraintKeys.addAll(entry.getValue());
                    break;
                }
            }
        }
        if (!flag) {
            return true;
        }
        Method method = new Method(CLIENT_METHOD_NAME);
        method.setAbstract(true);
        method.addParameter(new Parameter(new FullyQualifiedJavaType(introspectedTable.getExampleType()), "example"));
        method.setReturnType(new FullyQualifiedJavaType(getEntityName(introspectedTable)));
        interfaze.addMethod(method);

        Method methodUnique = new Method(CLIENT_METHOD_NAME_UNIQUE);
        methodUnique.setAbstract(true);
        methodUnique.setReturnType(new FullyQualifiedJavaType(getEntityName(introspectedTable)));

        uniqueConstraintKeys.forEach(columnName -> {
            String javaFieldName = getJavaFieldName(introspectedTable, columnName);
            methodUnique.addParameter(new Parameter(getColumnJavaType(introspectedTable, columnName), javaFieldName, "@Param(\"" + javaFieldName + "\")"));
        });
        interfaze.addMethod(methodUnique);
        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        Map<String, List<String>> listMap = getUniqueConstraintKeys(introspectedTable);
        boolean flag = false;
        List<String> uniqueConstraintKeys = new ArrayList<>();
        if (listMap != null && !listMap.isEmpty()) {
            for (Map.Entry<String, List<String>> entry : listMap.entrySet()) {
                String uniqueConstraintName = entry.getKey();
                if (uniqueConstraintName.toLowerCase().startsWith("idx_") || uniqueConstraintName.toLowerCase().startsWith("index_") || uniqueConstraintName.toLowerCase().startsWith("uc_") || uniqueConstraintName.toLowerCase().startsWith("uk_")) {
                    flag = true;
                    uniqueConstraintKeys.addAll(entry.getValue());
                    break;
                }
            }
        }
        if (!flag) {
            return true;
        }

        String tableName = introspectedTable.getAliasedFullyQualifiedRuntimeTableName();
        XmlElement parentElement = document.getRootElement();
        {
            XmlElement selectElement = new XmlElement("select");
            selectElement.addAttribute(new Attribute("id", CLIENT_METHOD_NAME));
            selectElement.addAttribute(new Attribute("parameterType", introspectedTable.getExampleType()));
            selectElement.addElement(new TextElement("select"));

            if (!introspectedTable.getBLOBColumns().isEmpty()) {
                selectElement.addAttribute(new Attribute("resultMap", "ResultMapWithBLOBs"));

                XmlElement includeBaseElement = new XmlElement("include");
                includeBaseElement.addAttribute(new Attribute("refid", "Base_Column_List"));
                selectElement.addElement(includeBaseElement);

                selectElement.addElement(new TextElement(","));

                XmlElement includeBlobElement = new XmlElement("include");
                includeBlobElement.addAttribute(new Attribute("refid", "Blob_Column_List"));
                selectElement.addElement(includeBlobElement);
            } else {
                selectElement.addAttribute(new Attribute("resultMap", "BaseResultMap"));
                XmlElement includeBaseElement = new XmlElement("include");
                includeBaseElement.addAttribute(new Attribute("refid", "Base_Column_List"));
                selectElement.addElement(includeBaseElement);
            }
            selectElement.addElement(new TextElement("from " + tableName + " mt"));
            List<ForeignKeyItem> foreignKeyItemList = getForeignKeys(introspectedTable);
            if (!foreignKeyItemList.isEmpty()) {
                for (int i = 0; i < foreignKeyItemList.size(); i++) {
                    selectElement.addElement(new TextElement("    left join " + foreignKeyItemList.get(i).getPkTableName() + " childT" + (i + 1) + " on mt." + foreignKeyItemList.get(i).getFkColumnName() + " = childT" + (i + 1) + "." + foreignKeyItemList.get(i).getPkColumnName()));
                }
            }
            selectElement.addElement(new TextElement("<include refid=\"Example_Where_Clause\" />"));
            parentElement.addElement(selectElement);
        }

        {
            XmlElement selectElement = new XmlElement("select");
            selectElement.addAttribute(new Attribute("id", CLIENT_METHOD_NAME_UNIQUE));
            selectElement.addElement(new TextElement("select"));

            if (!introspectedTable.getBLOBColumns().isEmpty()) {
                selectElement.addAttribute(new Attribute("resultMap", "ResultMapWithBLOBs"));

                XmlElement includeBaseElement = new XmlElement("include");
                includeBaseElement.addAttribute(new Attribute("refid", "Base_Column_List"));
                selectElement.addElement(includeBaseElement);

                selectElement.addElement(new TextElement(","));

                XmlElement includeBlobElement = new XmlElement("include");
                includeBlobElement.addAttribute(new Attribute("refid", "Blob_Column_List"));
                selectElement.addElement(includeBlobElement);
            } else {
                selectElement.addAttribute(new Attribute("resultMap", "BaseResultMap"));
                XmlElement includeBaseElement = new XmlElement("include");
                includeBaseElement.addAttribute(new Attribute("refid", "Base_Column_List"));
                selectElement.addElement(includeBaseElement);
            }
            selectElement.addElement(new TextElement("from " + tableName + " mt"));
            List<ForeignKeyItem> foreignKeyItemList = getForeignKeys(introspectedTable);
            if (!foreignKeyItemList.isEmpty()) {
                for (int i = 0; i < foreignKeyItemList.size(); i++) {
                    selectElement.addElement(new TextElement("    left join " + foreignKeyItemList.get(i).getPkTableName() + " childT" + (i + 1) + " on mt." + foreignKeyItemList.get(i).getFkColumnName() + " = childT" + (i + 1) + "." + foreignKeyItemList.get(i).getPkColumnName()));
                }
            }
            StringBuilder whereBuilder = new StringBuilder("where");
            boolean firstFlag = true;
            for (String uniqueConstraintKey : uniqueConstraintKeys) {
                if (firstFlag) {
                    whereBuilder.append(" ").append(uniqueConstraintKey).append(" = #{").append(getJavaFieldName(introspectedTable, uniqueConstraintKey)).append("}");
                    firstFlag = false;
                } else {
                    whereBuilder.append(" and ").append(uniqueConstraintKey).append(" = #{").append(getJavaFieldName(introspectedTable, uniqueConstraintKey)).append("}");
                }
            }
            selectElement.addElement(new TextElement(whereBuilder.toString()));
            parentElement.addElement(selectElement);
        }


        return super.sqlMapDocumentGenerated(document, introspectedTable);

    }


}
