
### mybatis-generator-maven-plugin 插件扩展，新增实用方法
### 使用指南：
#### 1、mybatis-generator-maven-plugin 插件配置教程
[MyBatis Generator官方插件配置教程](http://www.mybatis.org/generator/configreference/plugin.html)
#### 2、MyBatis Generator 官方提供插件列表
[MyBatis Generator官方提供插件列表](http://www.mybatis.org/generator/reference/plugins.html)
#### 3、MyBatis Generator 插件定制官方教程
[MyBatis Generator插件定制官方教程](http://www.mybatis.org/generator/reference/pluggingIn.html)
#### 4、本系列插件使用方法
````
mybatis-generator-maven-plugin 中添加依赖

<dependency>
    <groupId>com.revengemission.plugins</groupId>
    <artifactId>mybatis-plugins</artifactId>
    <version>LATEST VERSION</version>
</dependency>

在 generatorConfig.xml 中配置插件

执行 mvn mybatis-generator:generate -X

````
````
批量插入,针对所有表生成方法
<plugin type="com.revengemission.plugins.mybatis.BatchInsertPlugin"/>
````

````
逻辑删除,针对所有表生成方法；表中需要有deleted字段
<plugin type="com.revengemission.plugins.mybatis.BatchLogicDeletePlugin"/>
````

````
批量更新,针对所有表生成方法
<plugin type="com.revengemission.plugins.mybatis.BatchUpdateByPrimaryKeyPlugin"/>
````

````
自定义查询,可连表，可返回单值
<plugin type="com.revengemission.plugins.mybatis.CustomSelectPlugin">
            <property
                    name="user_account_entity;selectUniqueByUsername"
                    value="single-row;String username;select * from user_account_entity where username=#{username}"/>
</plugin>
````

````
执行自定义查询和更新语句, 针对所有表生成方法
<plugin type="com.revengemission.plugins.mybatis.CustomSqlPlugin"/>
````

````
自定义查询,map传参数, 单独的mapper
<plugin type="com.revengemission.plugins.mybatis.GenericMapperPlugin">
    <property  name="withMapperAnnotation" value="true"/>
</plugin>
````


````
自定义更新方法
<plugin type="com.revengemission.plugins.mybatis.CustomUpdatePlugin">
            <property
                    name="user_account_entity;updateByUsername"
                    value="long id,String username;update user_account_entity set username=#{username} where id=#{id}"/>
</plugin>
````

````
mybatis model上添加注解
name:[表名;完整包名加类名]，如果所有表的model都加注解则name:[every_table;完整包名加类名]
value:[注解内容]
<plugin type="com.revengemission.plugins.mybatis.ModelAnnotationPlugin">
            <property
                    name="user_entity;com.fasterxml.jackson.annotation.JsonInclude"
                    value="@JsonInclude(JsonInclude.Include.NON_NULL)"/>
</plugin>
````

````
mybatis model field上添加注解
name:[表名;字段名;完整包名加类名]，如果所有表的model都加注解则name:[every_table;字段名;完整包名加类名]
value:[注解内容]
<plugin type="com.revengemission.plugins.mybatis.ModelFieldAnnotationPlugin">
            <property
                    name="user_entity;column_name;com.fasterxml.jackson.annotation.JsonIgnore"
                    value="@JsonIgnore"/>
</plugin>
````

````
修改mybatis-generator-core生成的update语句，如version=version+1
<plugin type="com.revengemission.plugins.mybatis.ModifyUpdateSqlPlugin">
            <property name="last_modified = #{lastModified,jdbcType=TIMESTAMP}"
                      value="  last_modified= now() "/>
            <property name="version = #{version,jdbcType=INTEGER}" value="  version = version+1"/>
</plugin>

````

````
根据字段组合查询唯一记录，返回单对象，针对所有表
<plugin type="com.revengemission.plugins.mybatis.SelectUniqueByExample"/>
````

````
生成TruncateTable语句, 针对所有表；如果表中有外键约束，执行truncateTable可能失败
<plugin type="com.revengemission.plugins.mybatis.MysqlTruncateTablePlugin"/>
````

````
增强example中order by 语句，防止注入，可以多字段排序，针对所有表
<plugin type="com.revengemission.plugins.mybatis.OrderByPlugin"/>
````





