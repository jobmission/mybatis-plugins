package com.revengemission.plugins.mybatis;


import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

/**
 * Truncate table plugin，针对不同数据库截断表并重置自增序列
 */
public class TruncateTablePlugin extends AbstractXmbgPlugin {

    private static final String CLIENT_METHOD_NAME = "truncateTable";

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {

        Method method = new Method(CLIENT_METHOD_NAME);
        method.addJavaDocLine("/**");
        method.addJavaDocLine(" * 截断表并重置自增序列（按数据库类型区分实现）");
        method.addJavaDocLine(" *");
        method.addJavaDocLine(" * @return 影响的行数");
        method.addJavaDocLine(" */");
        method.setAbstract(true);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        interfaze.addMethod(method);
        return true;
    }


    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        String tableName = getTableName(introspectedTable);
        List<String> primaryKeys = getPrimaryKeys(introspectedTable);
        String primaryKey = primaryKeys.isEmpty() ? null : primaryKeys.getFirst();

        XmlElement updateElement = new XmlElement("update");
        updateElement.addAttribute(new Attribute("id", CLIENT_METHOD_NAME));

        XmlElement chooseXmlElement = new XmlElement("choose");
        // postgresql：TRUNCATE ... RESTART IDENTITY 截断的同时重置序列，多列可一次指定
        chooseXmlElement.addElement(buildDatabaseWhenElement("postgresql",
                "TRUNCATE TABLE " + tableName + " RESTART IDENTITY"));
        // mysql：TRUNCATE TABLE 本身即重置 AUTO_INCREMENT
        chooseXmlElement.addElement(buildDatabaseWhenElement("mysql",
                "TRUNCATE TABLE " + tableName));
        // sqlite：无 TRUNCATE 语句，DELETE 清空后重置 sqlite_sequence 计数
        if (primaryKey != null) {
            chooseXmlElement.addElement(buildDatabaseWhenElement("sqlite",
                    "DELETE FROM " + tableName
                            + "; UPDATE sqlite_sequence SET seq = 0 WHERE name = '" + tableName + "'"));
        } else {
            chooseXmlElement.addElement(buildDatabaseWhenElement("sqlite",
                    "DELETE FROM " + tableName));
        }
        // h2：TRUNCATE TABLE ... RESTART IDENTITY
        chooseXmlElement.addElement(buildDatabaseWhenElement("h2",
                "TRUNCATE TABLE " + tableName + " RESTART IDENTITY"));
        // sqlserver：TRUNCATE TABLE 本身即重置标识种子
        chooseXmlElement.addElement(buildDatabaseWhenElement("sqlserver",
                "TRUNCATE TABLE " + tableName));

        XmlElement otherwiseElement = new XmlElement("otherwise");
        otherwiseElement.addElement(new TextElement("DELETE FROM " + tableName));
        chooseXmlElement.addElement(otherwiseElement);

        updateElement.addElement(chooseXmlElement);
        document.getRootElement().addElement(updateElement);
        return true;
    }

    private XmlElement buildDatabaseWhenElement(String databaseId, String sql) {
        XmlElement whenElement = new XmlElement("when");
        whenElement.addAttribute(new Attribute("test", "_databaseId == '" + databaseId + "'"));
        whenElement.addElement(new TextElement(sql));
        return whenElement;
    }

}