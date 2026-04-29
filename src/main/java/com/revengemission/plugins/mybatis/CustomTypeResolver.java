package com.revengemission.plugins.mybatis;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;
import org.mybatis.generator.internal.util.StringUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.JDBCType;
import java.sql.Types;
import java.util.Properties;

public class CustomTypeResolver extends JavaTypeResolverDefaultImpl {
    private static final Logger log = LoggerFactory.getLogger(CustomTypeResolver.class);

    public CustomTypeResolver() {
        super();
    }

    @Override
    public void addConfigurationProperties(Properties properties) {
        super.addConfigurationProperties(properties);
        boolean forceNumberToDecimal = StringUtility.isTrue(properties.getProperty("forceNumberToDecimal"));
        if (forceNumberToDecimal) {
            this.typeMap.put(Types.NUMERIC, new JdbcTypeInformation(JDBCType.DECIMAL.name(), new FullyQualifiedJavaType(BigDecimal.class.getName())));
        }
        // Map<String, Object>
        boolean forceJavaObjectToMap = StringUtility.isTrue(properties.getProperty("forceJavaObjectToMap"));
        if (forceJavaObjectToMap) {
            this.typeMap.put(Types.JAVA_OBJECT, new JdbcTypeInformation(JDBCType.JAVA_OBJECT.name(), new FullyQualifiedJavaType("java.util.Map<String, Object>")));
        }
        // pgsql json
        boolean forceOtherToJson = StringUtility.isTrue(properties.getProperty("forceOtherToJson"));
        if (forceOtherToJson) {
            if (isJackson3()) {
                this.typeMap.put(Types.OTHER, new JdbcTypeInformation(JDBCType.OTHER.name(), new FullyQualifiedJavaType(tools.jackson.databind.JsonNode.class.getName())));
            } else {
                this.typeMap.put(Types.OTHER, new JdbcTypeInformation(JDBCType.OTHER.name(), new FullyQualifiedJavaType(com.fasterxml.jackson.databind.JsonNode.class.getName())));
            }
        }
        // mysql json
        boolean forceLongVarcharToJson = StringUtility.isTrue(properties.getProperty("forceLongVarcharToJson"));
        if (forceLongVarcharToJson) {
            if (isJackson3()) {
                this.typeMap.put(Types.LONGVARCHAR, new JdbcTypeInformation(JDBCType.OTHER.name(), new FullyQualifiedJavaType(tools.jackson.databind.JsonNode.class.getName())));
            } else {
                this.typeMap.put(Types.LONGVARCHAR, new JdbcTypeInformation(JDBCType.OTHER.name(), new FullyQualifiedJavaType(com.fasterxml.jackson.databind.JsonNode.class.getName())));
            }
        }
    }

    boolean isJackson3() {
        try {
            // Try to load a Jackson 3 specific class
            Class.forName("tools.jackson.databind.ObjectMapper");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
