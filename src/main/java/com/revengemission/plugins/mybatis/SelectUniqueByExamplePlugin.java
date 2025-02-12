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

import java.util.List;

/**
 * 根据Example查询单条记录
 */
public class SelectUniqueByExamplePlugin extends AbstractXmbgPlugin {

    private static final String CLIENT_METHOD_NAME = "selectUniqueByExample";

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {

        Method method = new Method(CLIENT_METHOD_NAME);
        method.setAbstract(true);
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
        selectElement.addElement(new TextElement("select"));

        if (introspectedTable.getBLOBColumns().size() > 0) {
            selectElement.addAttribute(new Attribute("resultMap", "ResultMapWithBLOBs"));

            XmlElement includeBaseElement = new XmlElement("include");
            includeBaseElement.addAttribute(new Attribute("refid", "Base_Column_List"));
            selectElement.addElement(includeBaseElement);

            selectElement.addElement(new TextElement(","));

            XmlElement includeBlobElement = new XmlElement("include");
            includeBlobElement.addAttribute(new Attribute("refid", "Blob_Column_List"));
            selectElement.addElement(includeBlobElement);

            selectElement.addElement(new TextElement("from " + tableName + " mt"));
            selectElement.addElement(new TextElement("<include refid=\"Example_Where_Clause\" />"));

        } else {
            selectElement.addAttribute(new Attribute("resultMap", "BaseResultMap"));
            XmlElement includeBaseElement = new XmlElement("include");
            includeBaseElement.addAttribute(new Attribute("refid", "Base_Column_List"));
            selectElement.addElement(includeBaseElement);

            selectElement.addElement(new TextElement("from " + tableName + " mt"));
            selectElement.addElement(new TextElement("<include refid=\"Example_Where_Clause\" />"));
        }

        parentElement.addElement(selectElement);

        return super.sqlMapDocumentGenerated(document, introspectedTable);

    }


}
