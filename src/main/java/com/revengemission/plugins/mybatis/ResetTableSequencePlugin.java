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
 * 重置表的Sequence
 */
public class ResetTableSequencePlugin extends AbstractXmbgPlugin {

    private static final String CLIENT_METHOD_NAME = "resetSequence";
    private static final String CLIENT_METHOD_NAME_GET = "getMaxPrimaryKeyValue";

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        List<String> primaryKeys = getPrimaryKeys(introspectedTable);
        if (primaryKeys == null || primaryKeys.size() != 1) {
            return true;
        }
        {
            Method method = new Method(CLIENT_METHOD_NAME_GET);
            method.addJavaDocLine("/**");
            method.addJavaDocLine(" * 获取表的主键的最大值");
            method.addJavaDocLine(" *");
            method.addJavaDocLine(" * @return 最大值");
            method.addJavaDocLine(" */");
            method.setAbstract(true);
            method.setReturnType(new FullyQualifiedJavaType("long"));
            interfaze.addMethod(method);
        }

        {
            Method method = new Method(CLIENT_METHOD_NAME);
            method.addJavaDocLine("/**");
            method.addJavaDocLine(" * 重置表的sequence，需自行处理未使用的最大值传进来");
            method.addJavaDocLine(" *");
            method.addJavaDocLine(" * @param targetValue 设定值");
            method.addJavaDocLine(" * @return 影响的行数");
            method.addJavaDocLine(" */");
            method.setAbstract(true);
            method.addParameter(new Parameter(new FullyQualifiedJavaType("long"), "targetValue", "@Param(\"targetValue\")"));
            method.setReturnType(new FullyQualifiedJavaType("Integer"));
            interfaze.addMethod(method);
        }

        return true;
    }


    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        String tableName = getTableName(introspectedTable);
        List<String> primaryKeys = getPrimaryKeys(introspectedTable);
        if (primaryKeys == null || primaryKeys.size() != 1) {
            return true;
        }
        String primaryKey = primaryKeys.getFirst();
        {
            XmlElement selectElement = new XmlElement("select");
            selectElement.addAttribute(new Attribute("id", CLIENT_METHOD_NAME_GET));
            selectElement.addAttribute(new Attribute("resultType", "long"));
            String selectStr = "select COALESCE(max(" + primaryKey + "), 0) from " + tableName;
            selectElement.addElement(new TextElement(selectStr));
            document.getRootElement().addElement(selectElement);
        }

        {
            XmlElement updateElement = new XmlElement("update");
            updateElement.addAttribute(new Attribute("id", CLIENT_METHOD_NAME));
            String postgresString = "ALTER SEQUENCE " + tableName + "_" + primaryKey + "_seq RESTART WITH ${targetValue}";
            String mysqlString = "ALTER TABLE " + tableName + " AUTO_INCREMENT = ${targetValue}";
            String sqliteString = "UPDATE sqlite_sequence SET seq = MAX(${targetValue}, (SELECT COALESCE(MAX(" + primaryKey + "), 0) FROM " + tableName + "))  WHERE name = '" + tableName + "'";
            String h2String = "ALTER TABLE " + tableName + " ALTER COLUMN " + primaryKey + " RESTART WITH ${targetValue}";

            XmlElement chooseXmlElement = new XmlElement("choose");
            XmlElement whenPostgresqlElement = new XmlElement("when");
            whenPostgresqlElement.addAttribute(new Attribute("test", "_databaseId == 'postgresql'"));
            whenPostgresqlElement.addElement(new TextElement(postgresString));

            XmlElement whenMysqlElement = new XmlElement("when");
            whenMysqlElement.addAttribute(new Attribute("test", "_databaseId == 'mysql'"));
            whenMysqlElement.addElement(new TextElement(mysqlString));

            XmlElement whenSqliteElement = new XmlElement("when");
            whenSqliteElement.addAttribute(new Attribute("test", "_databaseId == 'sqlite'"));
            whenSqliteElement.addElement(new TextElement(sqliteString));

            XmlElement whenH2Element = new XmlElement("when");
            whenH2Element.addAttribute(new Attribute("test", "_databaseId == 'h2'"));
            whenH2Element.addElement(new TextElement(h2String));

            XmlElement otherwiseElement = new XmlElement("otherwise");
            otherwiseElement.addElement(new TextElement("SELECT -2"));

            chooseXmlElement.addElement(whenPostgresqlElement);
            chooseXmlElement.addElement(whenMysqlElement);
            chooseXmlElement.addElement(whenSqliteElement);
            chooseXmlElement.addElement(whenH2Element);
            chooseXmlElement.addElement(otherwiseElement);

            updateElement.addElement(chooseXmlElement);
            document.getRootElement().addElement(updateElement);
        }

        return true;
    }


}
