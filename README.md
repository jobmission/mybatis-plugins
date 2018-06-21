
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
//生成TruncateTable语句
<plugin type="com.revengemission.plugins.mybatis.MysqlTruncateTablePlugin">
            <property name="category_entity" value=""/>
            <property name="brand_entity" value=""/>
</plugin>
````

````
//根据唯一值字段查询，返回单行
<plugin type="com.revengemission.plugins.mybatis.MybatisCustomSelectPlugin">
            <property
                    name="category_entity-selectUniqueByCode"
                    value="single-row;String code;select * from category_entity where code=#{code}"/>
</plugin>
````

````
//mybatis model上添加注解,name完整包名加类名，value注解内容
<plugin type="com.revengemission.plugins.mybatis.MybatisModelAnnotationPlugin">
            <property
                    name="com.fasterxml.jackson.annotation.JsonInclude"
                    value="@JsonInclude(JsonInclude.Include.NON_NULL)"/>
</plugin>
````


