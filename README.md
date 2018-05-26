
## MyBatis Generator 插件，新增实用方法，简化sql
## 使用方法：
#### 1.MyBatis Generator 配置教程
http://www.mybatis.org/generator/configreference/xmlconfig.html
#### 2.MyBatis Generator 插件定制教程
http://www.mybatis.org/generator/configreference/plugin.html
#### 3.本插件使用方法，以MysqlTruncateTablePlugin为例

````
<plugin type="com.revengemission.plugins.mybatis.MysqlTruncateTablePlugin">
            <property name="category_entity" value=""/>
            <property name="brand_entity" value=""/>
</plugin>
//上边category_entity，brand_entity替换为要生成TruncateTable方法的表名即可
````