package com.revengemission.plugins.mybatis;

import com.fasterxml.jackson.databind.JsonNode;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;
import org.mybatis.generator.internal.util.StringUtility;

import java.sql.JDBCType;
import java.sql.Types;
import java.util.Properties;

public class CustomTypeResolver extends JavaTypeResolverDefaultImpl {
    public CustomTypeResolver() {
        super();
    }

    @Override
    public String calculateJdbcTypeName(IntrospectedColumn introspectedColumn) {
        //introspectedColumn.getJdbcTypeName() 疑似固定是OTHER
        //introspectedColumn.getJdbcType() 不同数据库返回不一样
        return super.calculateJdbcTypeName(introspectedColumn);
    }

    @Override
    public void addConfigurationProperties(Properties properties) {
        super.addConfigurationProperties(properties);
        // Map<String, Object>
        boolean forceJavaObjectToMap = StringUtility.isTrue(properties.getProperty("forceJavaObjectToMap"));
        if (forceJavaObjectToMap) {
            System.out.println("===forceJavaObjectToMap=true");
            this.typeMap.put(Types.JAVA_OBJECT, new JdbcTypeInformation(JDBCType.JAVA_OBJECT.name(), new FullyQualifiedJavaType("java.util.Map<String, Object>")));
        }
        // pgsql json
        boolean forceOtherToJson = StringUtility.isTrue(properties.getProperty("forceOtherToJson"));
        if (forceOtherToJson) {
            System.out.println("===forceOtherToJson=true");
            this.typeMap.put(Types.OTHER, new JdbcTypeInformation(JDBCType.OTHER.name(), new FullyQualifiedJavaType(JsonNode.class.getName())));
        }
        // mysql json
        boolean forceLongVarcharToJson = StringUtility.isTrue(properties.getProperty("forceLongVarcharToJson"));
        if (forceLongVarcharToJson) {
            System.out.println("===forceLongVarcharToJson=true");
            this.typeMap.put(Types.LONGVARCHAR, new JdbcTypeInformation(JDBCType.OTHER.name(), new FullyQualifiedJavaType(JsonNode.class.getName())));
        }
    }
}
