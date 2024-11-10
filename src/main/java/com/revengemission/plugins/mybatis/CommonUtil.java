package com.revengemission.plugins.mybatis;

public class CommonUtil {

    //数据库表字段类型转换成java字段类型
    public static String convertDbTypeToJavaType(String dbType) {
        return switch (dbType.toUpperCase()) {
            case "INT", "INT4", "INTEGER" -> "Integer";
            case "INT8", "BIGINT" -> "Long";
            case "TINYINT" -> "Byte";
            case "SMALLINT" -> "Short";
            case "FLOAT", "REAL" -> "Float";
            case "DOUBLE" -> "Double";
            case "DECIMAL", "NUMERIC" -> "BigDecimal";
            case "VARCHAR", "CHAR", "TEXT", "TINYTEXT", "MEDIUMTEXT", "LONGTEXT", "LONGVARCHAR", "CLOB" -> "String";
            case "DATE" -> "LocalDate";
            case "DATETIME", "TIMESTAMPTZ", "TIMESTAMP" -> "LocalDateTime";
            case "TIME" -> "LocalTime";
            case "BOOLEAN", "BIT" -> "Boolean";
            case "BLOB", "LONGBLOB", "BINARY", "VARBINARY" -> "byte[]";
            case "JSON" -> "JsonNode";
            default -> "Object";
        };
    }
}

