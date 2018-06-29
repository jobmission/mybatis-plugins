
### mybatis-generator-maven-plugin 插件扩展，新增实用方法，提高效率
### 使用方法：
#### 1.mybatis-generator-maven-plugin配置教程
[MyBatis Generator官方配置教程](http://www.mybatis.org/generator/configreference/xmlconfig.html)
#### 2.MyBatis Generator 官方提供插件列表
[MyBatis Generator官方提供插件列表](http://www.mybatis.org/generator/reference/plugins.html)
#### 3.MyBatis Generator 插件定制教程
[MyBatis Generator插件定制教程](http://www.mybatis.org/generator/reference/pluggingIn.html)
#### 4.本插件使用方法

````
批量插入,针对所有表
<plugin type="com.revengemission.plugins.mybatis.BatchInsertPlugin"/>
````

````
逻辑删除,针对所有表,表中需要有deleted字段
<plugin type="com.revengemission.plugins.mybatis.BatchLogicDeletePlugin"/>
````

````
批量更新,针对所有表
<plugin type="com.revengemission.plugins.mybatis.BatchUpdatePlugin"/>
````

````
无xml动态mapper,针对所有表,需要修改generatorConfiguration　targetRuntime
<plugin type="com.revengemission.plugins.mybatis.MyBatis3DynamicSQLPlugin"/>
````

````
自定义查询,可连表，可返回单值
<plugin type="com.revengemission.plugins.mybatis.MySQLCustomSelectPlugin">
            <property
                    name="user_account_entity-selectUniqueByUsername"
                    value="single-row;String username;select * from user_account_entity where username=#{username}"/>
</plugin>
````

````
自定义语句,针对所有表,执行select和update
<plugin type="com.revengemission.plugins.mybatis.MybatisCustomSqlPlugin"/>
````

````
自定义更新
<plugin type="com.revengemission.plugins.mybatis.MybatisCustomUpdatePlugin">
            <property
                    name="user_account_entity-updateByUsername"
                    value="long id,String username;update user_account_entity where username=#{username} where id=#{id}"/>
</plugin>
````

````
mybatis model上添加注解
name:[表名;完整包名加类名]，如果所有表的model都加注解则name:[every_table;完整包名加类名]
value:[注解内容]
<plugin type="com.revengemission.plugins.mybatis.MybatisModelAnnotationPlugin">
            <property
                    name="user_entity;com.fasterxml.jackson.annotation.JsonInclude"
                    value="@JsonInclude(JsonInclude.Include.NON_NULL)"/>
</plugin>
````

````
mybatis model field上添加注解
name:[表名;字段名;完整包名加类名]，如果所有表的model都加注解则name:[every_table;字段名;完整包名加类名]
value:[注解内容]
<plugin type="com.revengemission.plugins.mybatis.MybatisModelFieldAnnotationPlugin">
            <property
                    name="user_entity;column_name;com.fasterxml.jackson.annotation.JsonIgnore"
                    value="@JsonIgnore"/>
</plugin>
````

````
修改mybatis-generator-core生成的update语句，如version=version+1
<plugin type="com.revengemission.plugins.mybatis.MySQLModifyUpdateSqlPlugin">
            <property name="last_modified = #{lastModified,jdbcType=TIMESTAMP}"
                      value="  last_modified= now() "/>
            <property name="version = #{version,jdbcType=INTEGER}" value="  version = version+1"/>
</plugin>

````

````
根据字段组合查询唯一记录，返回单行
name:[mapper方法名]
value:[表名;字段类型 字段名,字段类型 字段名...]，every_table所有表
<plugin type="com.revengemission.plugins.mybatis.MybatisSelectOneByColumnsPlugin">
            <property
                    name="selectUniqueByCode"
                    value="category_entity;String code,long shop_id"/>
</plugin>
````

````
生成TruncateTable语句
<plugin type="com.revengemission.plugins.mybatis.MysqlTruncateTablePlugin">
            <property name="category_entity" value=""/>
            <property name="brand_entity" value=""/>
</plugin>
````

````
order by 语句，可以多字段排序,针对所有表
<plugin type="com.revengemission.plugins.mybatis.OrderByPlugin"/>
````





