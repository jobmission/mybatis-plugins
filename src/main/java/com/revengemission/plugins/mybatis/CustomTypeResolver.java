package com.revengemission.plugins.mybatis;

import com.fasterxml.jackson.databind.JsonNode;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

public class CustomTypeResolver extends JavaTypeResolverDefaultImpl {
    public CustomTypeResolver() {
        super();
        // java.sql.Types, java.sql.JDBCType,
        this.typeMap.put(2000, new JdbcTypeInformation("JAVA_OBJECT", new FullyQualifiedJavaType("java.util.Map<String, Object>")));
        this.typeMap.put(1111, new JdbcTypeInformation("OTHER", new FullyQualifiedJavaType(JsonNode.class.getName())));
    }
}
