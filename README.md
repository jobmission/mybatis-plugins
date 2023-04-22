
## 使用指南：
### 1、mybatis-generator-maven-plugin 插件配置教程
[MyBatis Generator官方插件配置教程](http://www.mybatis.org/generator/configreference/plugin.html)
### 2、MyBatis Generator 官方提供插件列表
[MyBatis Generator官方提供插件列表](http://www.mybatis.org/generator/reference/plugins.html)
### 3、MyBatis Generator 插件定制官方教程
[MyBatis Generator插件定制官方教程](http://www.mybatis.org/generator/reference/pluggingIn.html)
### 4、本系列插件使用方法, mybatis-generator-maven-plugin 中添加依赖
````
<plugin>
    <groupId>org.mybatis.generator</groupId>
    <artifactId>mybatis-generator-maven-plugin</artifactId>
    <version>1.4.2</version>
    
    <dependencies>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>${mysql.version}</version>
        </dependency>
    
        <dependency>
            <groupId>com.revengemission.plugins</groupId>
            <artifactId>mybatis-plugins</artifactId>
            <version>LATEST VERSION</version>
        </dependency>
    </dependencies>
    
    <configuration>
        <overwrite>true</overwrite>
        <verbose>true</verbose>
    </configuration>
</plugin>


在 generatorConfig.xml 中配置需要的插件

然后执行 mvn mybatis-generator:generate -X

````
### 插件列表
1. 批量插入, 针对所有表生成方法
    ````
    <plugin type="com.revengemission.plugins.mybatis.BatchInsertPlugin"/>
    ````

2. 根据唯一约束批量upsert
    ````
    name:[表名]，value:[ON DUPLICATE KEY UPDATE后面的语句]
    <plugin type="com.revengemission.plugins.mybatis.InsertOnUpdatePlugin">
        <property name="user_entity" value="version = newRowValue.version + 1, remark = CONCAT_WS(',',newRowValue.remark,'插入时重复')"/>
    </plugin>
    ````
3. 逻辑删除, 针对所有表生成方法, 需配置逻辑删除字段和删除flag值
    ````
    <plugin type="com.revengemission.plugins.mybatis.CustomDeletePlugin">
         <property name="deletedFlagTableFiled" value="deleted"/>
         <property name="deletedFlagValue" value="1"/>
    </plugin>
    ````
4. 根据主键批量更新, 针对所有表生成方法
    ````
    <plugin type="com.revengemission.plugins.mybatis.BatchUpdateByPrimaryKeyPlugin"/>
    ````
5. 自定义查询, map传参数, 单独的mapper接口, 使用时注意过滤危险字符防止注入
    ````
    <plugin type="com.revengemission.plugins.mybatis.GenericMapperPlugin">
        <property name="withMapperAnnotation" value="true"/>
    </plugin>
    ````
6. mybatis model class上添加注解
    ````
    name:[表名;完整包名加类名], 如果所有表的model都加注解则name:[every_table;完整包名加类名]
    value:[注解内容]
    <plugin type="com.revengemission.plugins.mybatis.ModelAnnotationPlugin">
        <property name="user_entity;com.fasterxml.jackson.annotation.JsonInclude" value="@JsonInclude(JsonInclude.Include.NON_NULL)"/>
    </plugin>
    ````
7. mybatis model field上添加注解
    ````
    name:[表名;字段名;完整包名加类名], 如果所有表的model都加注解则name:[every_table;字段名;完整包名加类名]
    value:[注解内容]
    <plugin type="com.revengemission.plugins.mybatis.ModelFieldAnnotationPlugin">
        <property name="user_entity;column_name;com.fasterxml.jackson.annotation.JsonIgnore" value="@JsonIgnore"/>
    </plugin>
    ````
8. 修改mybatis-generator-core生成的update语句, 如 version = version+1
    ````
    <plugin type="com.revengemission.plugins.mybatis.ModifyUpdateSqlPlugin">
        <property name="last_modified = #{lastModified,jdbcType=TIMESTAMP}" value="last_modified= now()"/>
        <property name="last_modified = #{record.lastModified,jdbcType=TIMESTAMP}" value="last_modified= now()"/>
        <property name="version = #{version,jdbcType=INTEGER}" value="version = version + 1"/>
        <property name="version = #{record.version,jdbcType=INTEGER}" value="version = version + 1"/>
    </plugin>
    ````
9. 根据example查询约束字段唯一记录, 返回单对象, 针对所有表
    ````
    <plugin type="com.revengemission.plugins.mybatis.SelectUniqueByExamplePlugin"/>
    ````
10. 生成Truncate table语句, 针对所有表; 如果有外键约束, 执行truncate table可能失败
    ````
    <plugin type="com.revengemission.plugins.mybatis.MysqlTruncateTablePlugin"/>
    ````
11. 增强example中order by 语句, 防止注入, 可以多字段排序, 针对所有表
    ````
    <plugin type="com.revengemission.plugins.mybatis.OrderByPlugin"/>
    ````

12. 增强Example Criterion, 如使用find_in_set、json函数、正则表达式等, 针对所有表
    ````
    <plugin type="com.revengemission.plugins.mybatis.ExampleCriterionExtendPlugin"/>
    
    userEntityExample.createCriteria().andFunctionRightKey("find_in_set", "field_a", searchValue);
    
    userEntityExample.createCriteria().andConditionValue("FROM_UNIXTIME(field_b, '%Y-%m-%d') = ", "2020-01-01");
    ````





