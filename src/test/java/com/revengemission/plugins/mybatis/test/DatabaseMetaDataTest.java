package com.revengemission.plugins.mybatis.test;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DatabaseMetaDataTest {
    @Ignore
    @Test
    public void jdbcConnectionTest() {
        // 创建HikariCP配置
        String database = "feature_preview";
        String jdbcUrl = "jdbc:postgresql://localhost:5432/" + database;
        String username = "postgres";
        String password = "P@ssw0rd";
        String driverClassName = "org.postgresql.Driver";
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverClassName);

        // 设置连接池属性
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        // 创建数据源
        HikariDataSource dataSource = new HikariDataSource(config);

        try (Connection connection = dataSource.getConnection()) {
            // 使用连接进行数据库操作
            System.out.println("Connection successful!");
            // 获取数据库元数据
            DatabaseMetaData metaData = connection.getMetaData();
            // 使用元数据获取表名
            String catalog = null; // 数据库名，可以为null
            String schemaPattern = jdbcUrl.contains("mysql") ? database : "public"; // schema模式名，MySQL中为null
            String tableNamePattern = "%%"; // 表名匹配模式，这里是获取所有表
            ResultSet resultSet = metaData.getTables(catalog, schemaPattern, tableNamePattern, new String[]{"TABLE"});
            // 遍历结果集
            AtomicInteger atomicInteger = new AtomicInteger(0);
            while (resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                atomicInteger.incrementAndGet();
                System.out.println("============" + tableName);
                /*ResultSet columnsResultSet = metaData.getColumns(null, schemaPattern, tableName, "%");
                while (columnsResultSet.next()) {
                    String columnName = columnsResultSet.getString("COLUMN_NAME");
                    String jdbcDataType = columnsResultSet.getString("TYPE_NAME");
                    System.out.println("Column Name: " + columnName + ", JDBC Data Type: " + jdbcDataType + ", Java Type: " + CommonUtil.convertDbTypeToJavaType(jdbcDataType));
                }*/

                System.out.println("=======unique indexes");
                ResultSet indexResultSet = metaData.getIndexInfo(null, schemaPattern, tableName, true, false);
                Map<String, List<String>> indexMap = new HashMap<>();
                while (indexResultSet.next()) {
                    String indexName = indexResultSet.getString("INDEX_NAME");
                    String columnName = indexResultSet.getString("COLUMN_NAME");
                    short indexType = indexResultSet.getShort("TYPE");
                    List<String> columns;
                    if (indexMap.containsKey(indexName)) {
                        columns = indexMap.get(indexName);
                    } else {
                        columns = new ArrayList<>();
                    }
                    columns.add(columnName);
                    indexMap.put(indexName, columns);
                    System.out.println("index type: " + indexType + ", index name: " + indexName + ", column name: " + columnName);
                }

                System.out.println(indexMap);

                /*System.out.println("=======primary keys");
                ResultSet primaryKeysResultSet = metaData.getPrimaryKeys(null, schemaPattern, tableName);
                while (primaryKeysResultSet.next()) {
                    String columnName = primaryKeysResultSet.getString("COLUMN_NAME");
                    System.out.println("pk column Name: " + columnName);
                }*/
            }
            System.out.println("total tables:" + atomicInteger.get());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 关闭数据源
            dataSource.close();
        }
    }
}
