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

    private static final int idSequence = 1;
    private static final String CLIENT_METHOD_NAME = "resetSequence";

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
        Method method = new Method(CLIENT_METHOD_NAME);
        method.addJavaDocLine("/**");
        method.addJavaDocLine(" * 重置表的sequence，取传入值和表中最大值的较大值");
        method.addJavaDocLine(" *");
        method.addJavaDocLine(" * @param targetValue 设定值");
        method.addJavaDocLine(" * @return 设定后的值");
        method.addJavaDocLine(" */");
        method.setAbstract(true);
        method.addParameter(new Parameter(new FullyQualifiedJavaType("long"), "targetValue", "@Param(\"targetValue\")"));
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        interfaze.addMethod(method);
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

        XmlElement updateElement = new XmlElement("update");
        updateElement.addAttribute(new Attribute("id", CLIENT_METHOD_NAME));
        String mysqlString = "ALTER TABLE " + tableName + " AUTO_INCREMENT = ${targetValue}";
        String postgresString = "SELECT setval(" + tableName + "_" + primaryKey + "_seq::regclass, GREATEST(${targetValue}, (SELECT COALESCE(MAX(" + primaryKey + "), 1) FROM " + tableName + ")))";
        String sqliteString = "INSERT OR REPLACE INTO sqlite_sequence (name, seq) VALUES (" + tableName + ", GREATEST(${targetValue}, (SELECT COALESCE(MAX(" + primaryKey + "), 0) FROM " + tableName + ")))";

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

        XmlElement otherwiseElement = new XmlElement("otherwise");
        otherwiseElement.addElement(new TextElement("SELECT 1"));

        chooseXmlElement.addElement(whenPostgresqlElement);
        chooseXmlElement.addElement(whenMysqlElement);
        chooseXmlElement.addElement(whenSqliteElement);
        chooseXmlElement.addElement(otherwiseElement);

        updateElement.addElement(chooseXmlElement);
        document.getRootElement().addElement(updateElement);
        return true;
    }


}
