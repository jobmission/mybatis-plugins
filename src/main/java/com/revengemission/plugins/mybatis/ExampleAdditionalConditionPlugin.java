package com.revengemission.plugins.mybatis;


import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;

import java.util.List;

/**
 * 增强example,如添加find_in_set、json查询语句等，注意未处理注入问题 !!!
 * 参照ExampleCriterionExtendPlugin 扩展新功能
 */
@Deprecated
public class ExampleAdditionalConditionPlugin extends AbstractXmbgPlugin {

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        List<InnerClass> innerClasses = topLevelClass.getInnerClasses();
        for (int i = 0; i < innerClasses.size(); i++) {
            InnerClass innerClass = innerClasses.get(i);
            if ("GeneratedCriteria".equals(innerClass.getType().getShortName())) {
                Method andAdditionalCondition = new Method();
                andAdditionalCondition.setVisibility(JavaVisibility.PUBLIC);
                andAdditionalCondition.setName("andAdditionalCondition");
                Parameter nameParameter = new Parameter(FullyQualifiedJavaType.getStringInstance(), "additionalCondition", false);
                andAdditionalCondition.addParameter(nameParameter);
                andAdditionalCondition.addBodyLine("addCriterion(additionalCondition);");
                andAdditionalCondition.addBodyLine("return (Criteria) this;");
                andAdditionalCondition.setReturnType(new FullyQualifiedJavaType("Criteria"));
                innerClass.addMethod(andAdditionalCondition);
                break;
            }
        }

        return true;
    }


}
