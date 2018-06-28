package com.revengemission.plugins.mybatis;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 根据多列查询单条记录
 */
public class MybatisSelectOneByColumnsPlugin extends AbstractXmbgPlugin {

    Map<String, String> todo = new LinkedHashMap<>();

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        todo.clear();
        String currentTableName = getTableName(introspectedTable);
        properties.forEach((k, v) -> {
            String[] temp = StringUtils.split(StringUtils.trim(v.toString()), ";");
            if (temp.length == 2) {
                if (StringUtils.equalsIgnoreCase(currentTableName, temp[0]) || StringUtils.equalsIgnoreCase("every_table", temp[0])) {
                    if (checkColumns(StringUtils.trim(v.toString()), introspectedTable.getAllColumns())) {
                        todo.put(StringUtils.trim(k.toString()), StringUtils.trim(temp[1]));
                    }
                }
            }
        });
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        if (todo != null && todo.size() > 0) {
            todo.forEach((k, v) -> {
                Method method = new Method(k);
                String[] temp = StringUtils.split(StringUtils.trim(v.toString()), ",");
                for (int i = 0; i < temp.length; i++) {
                    String[] tempColumn = StringUtils.split(temp[i]);
                    method.addParameter(new Parameter(new FullyQualifiedJavaType(tempColumn[0]), camelName(tempColumn[1]), "@Param(\"" + camelName(tempColumn[1]) + "\")"));
                }
                method.setReturnType(new FullyQualifiedJavaType(getEntityName(introspectedTable)));
                interfaze.addMethod(method);
            });
        }

        return true;
    }

    boolean checkColumns(String columns, List<IntrospectedColumn> introspectedColumns) {
        List<String> a = new ArrayList<>();
        List<String> b = new ArrayList<>();
        String[] temp = StringUtils.split(StringUtils.trim(columns), ",");
        for (int i = 0; i < temp.length; i++) {
            String[] tempColumn = StringUtils.split(temp[i]);
            System.out.println("tempColumn:" + tempColumn);
            a.add(tempColumn[1]);
        }
        introspectedColumns.forEach(introspectedColumn -> {
            b.add(introspectedColumn.getActualColumnName());
        });
        System.out.println("columns:" + columns);
        System.out.println("a:" + a);
        System.out.println("b:" + b);
        return b.containsAll(a);
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        String tableName = introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime();
        XmlElement parentElement = document.getRootElement();

        if (todo != null && todo.size() > 0) {
            todo.forEach((k, v) -> {
                XmlElement customerSelectElement = new XmlElement("select");
                customerSelectElement.addAttribute(new Attribute("id", k));

                StringBuffer whereCondition = new StringBuffer(" where ");
                String[] temp = StringUtils.split(v, ",");
                for (int i = 0; i < temp.length; i++) {
                    String[] tempColumn = StringUtils.split(temp[i]);
                    if (i != 0) {
                        whereCondition.append(" and ");
                    }
                    whereCondition.append(tempColumn[1]).append(" = ").append("#{").append(camelName(tempColumn[1])).append("} ");
                }

                if (introspectedTable.getBLOBColumns().size() > 0) {
                    customerSelectElement.addAttribute(new Attribute("resultMap", "ResultMapWithBLOBs"));
                    customerSelectElement.addElement(
                            new TextElement(
                                    "select <include refid=\"Base_Column_List\" />\n" +
                                            "    ,\n" +
                                            "    <include refid=\"Blob_Column_List\" />\n\t from " + tableName + " \n\t" + whereCondition.toString()
                            ));
                } else {
                    customerSelectElement.addAttribute(new Attribute("resultMap", "BaseResultMap"));
                    customerSelectElement.addElement(
                            new TextElement(
                                    "select <include refid=\"Base_Column_List\" />\n\t from " + tableName + "\n\t" + whereCondition.toString()
                            ));
                }

                parentElement.addElement(customerSelectElement);
            });

        }

        return super.sqlMapDocumentGenerated(document, introspectedTable);

    }

}
