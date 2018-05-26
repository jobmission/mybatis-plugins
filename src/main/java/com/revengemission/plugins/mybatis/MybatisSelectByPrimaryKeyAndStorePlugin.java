package com.revengemission.plugins.mybatis;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

/**
 * 双键查询
 */
public class MybatisSelectByPrimaryKeyAndStorePlugin extends AbstractXmbgPlugin {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        Method method = new Method("selectByPrimaryKeyAndStore");
        method.addParameter(new Parameter(new FullyQualifiedJavaType("long"), "storeId", "@Param(\"storeId\")"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("long"), "id", "@Param(\"id\")"));
        method.setReturnType(new FullyQualifiedJavaType(getEntityName(introspectedTable)));
        interfaze.addMethod(method);
        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        String tableName = introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime();//数据库表名

        XmlElement parentElement = document.getRootElement();

        // 产生分页语句前半部分
        XmlElement deleteLogicByIdsElement = new XmlElement("select");
        deleteLogicByIdsElement.addAttribute(new Attribute("id", "selectByPrimaryKeyAndStore"));

        if (introspectedTable.getBLOBColumns().size() > 0) {
            deleteLogicByIdsElement.addAttribute(new Attribute("resultMap", "ResultMapWithBLOBs"));
            deleteLogicByIdsElement.addElement(
                    new TextElement(
                            "select <include refid=\"Base_Column_List\" />\n" +
                                    "    ,\n" +
                                    "    <include refid=\"Blob_Column_List\" />\n\t from " + tableName + "\n\t  where  store_id=#{storeId} and id = #{id}"
                    ));
        } else {
            deleteLogicByIdsElement.addAttribute(new Attribute("resultMap", "BaseResultMap"));
            deleteLogicByIdsElement.addElement(
                    new TextElement(
                            "select <include refid=\"Base_Column_List\" />\n\t from " + tableName + "\n\t  where store_id=#{storeId} and id = #{id}"
                    ));
        }

        parentElement.addElement(deleteLogicByIdsElement);

        return super.sqlMapDocumentGenerated(document, introspectedTable);

    }

}
