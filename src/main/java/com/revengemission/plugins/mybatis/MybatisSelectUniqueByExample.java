package com.revengemission.plugins.mybatis;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

/**
 * 根据Example查询单条记录
 */
public class MybatisSelectUniqueByExample extends AbstractXmbgPlugin {

    private static final String CLIENT_METHOD_NAME = "selectUniqueByExample";

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        Method method = new Method(CLIENT_METHOD_NAME);
        method.addParameter(new Parameter(new FullyQualifiedJavaType(introspectedTable.getExampleType()), "example"));
        method.setReturnType(new FullyQualifiedJavaType(getEntityName(introspectedTable)));
        interfaze.addMethod(method);

        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        String tableName = introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime();
        XmlElement parentElement = document.getRootElement();

        XmlElement selectElement = new XmlElement("select");
        selectElement.addAttribute(new Attribute("id", CLIENT_METHOD_NAME));
        selectElement.addAttribute(new Attribute("parameterType", introspectedTable.getExampleType()));

        if (introspectedTable.getBLOBColumns().size() > 0) {
            selectElement.addAttribute(new Attribute("resultMap", "ResultMapWithBLOBs"));
            TextElement textElement = new TextElement(
                    "select <include refid=\"Base_Column_List\" />\n" +
                            "    ,\n" +
                            "    <include refid=\"Blob_Column_List\" />\n\t from " + tableName + " \n\t" +
                            "    <include refid=\"Example_Where_Clause\" />"
            );

            selectElement.addElement(textElement);
        } else {
            selectElement.addAttribute(new Attribute("resultMap", "BaseResultMap"));
            TextElement textElement = new TextElement(
                    "select <include refid=\"Base_Column_List\" />\n\t" +
                            "from " + tableName + " \n\t" +
                            "<include refid=\"Example_Where_Clause\" />"
            );
            selectElement.addElement(textElement);
        }

        parentElement.addElement(selectElement);

        return super.sqlMapDocumentGenerated(document, introspectedTable);

    }


}
