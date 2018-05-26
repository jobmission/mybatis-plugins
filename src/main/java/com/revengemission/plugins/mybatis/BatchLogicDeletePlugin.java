package com.revengemission.plugins.mybatis;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

/**
 * 逻辑删除
 */
public class BatchLogicDeletePlugin extends AbstractXmbgPlugin {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        Method method = new Method("deleteLogicByIds");
        method.addParameter(new Parameter(FullyQualifiedJavaType.getBooleanPrimitiveInstance(), "deleted", "@Param(\"deleted\")"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("Integer[]"), "ids", "@Param(\"ids\")"));
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        interfaze.addMethod(method);
        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        String tableName = introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime();//数据库表名

        XmlElement parentElement = document.getRootElement();

        // 产生分页语句前半部分
        XmlElement deleteLogicByIdsElement = new XmlElement("update");
        deleteLogicByIdsElement.addAttribute(new Attribute("id", "deleteLogicByIds"));

        deleteLogicByIdsElement.addElement(
                new TextElement(
                        "update " + tableName + " set deleted = #{deleted,jdbcType=INTEGER} where id in "
                                + " <foreach item=\"item\" index=\"index\" collection=\"ids\" open=\"(\" separator=\",\" close=\")\">#{item}</foreach> "
                ));

        parentElement.addElement(deleteLogicByIdsElement);

        return super.sqlMapDocumentGenerated(document, introspectedTable);

    }

}
