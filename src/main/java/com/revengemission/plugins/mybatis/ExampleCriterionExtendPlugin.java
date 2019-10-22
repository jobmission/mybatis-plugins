package com.revengemission.plugins.mybatis;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 增强example Criterion,目前已实现find_in_set等
 */
public class ExampleCriterionExtendPlugin extends AbstractXmbgPlugin {

    private static final Logger log = LoggerFactory.getLogger(ExampleCriterionExtendPlugin.class);

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        List<InnerClass> innerClasses = topLevelClass.getInnerClasses();
        for (int i = 0; i < innerClasses.size(); i++) {
            InnerClass innerClass = innerClasses.get(i);
            if ("Criterion".equals(innerClass.getType().getShortName())) {

                Field additionalCondition = new Field();
                additionalCondition.setVisibility(JavaVisibility.PRIVATE);
                additionalCondition.setName("additionalCondition");
                additionalCondition.setType(FullyQualifiedJavaType.getIntInstance());
                additionalCondition.setInitializationString("0");
                innerClass.addField(additionalCondition);

                Method getAdditionalCondition = new Method();
                getAdditionalCondition.setVisibility(JavaVisibility.PUBLIC);
                getAdditionalCondition.setName("getAdditionalCondition");
                getAdditionalCondition.setReturnType(FullyQualifiedJavaType.getIntInstance());
                getAdditionalCondition.addBodyLine("return additionalCondition;");
                innerClass.addMethod(getAdditionalCondition);

                Method criterionConstruct = new Method();
                criterionConstruct.setConstructor(true);
                criterionConstruct.setName("Criterion");
                Parameter additionalConditionParameter = new Parameter(FullyQualifiedJavaType.getIntInstance(), "additionalCondition", false);
                Parameter valueParameter = new Parameter(FullyQualifiedJavaType.getObjectInstance(), "value", false);
                Parameter secondValueParameter = new Parameter(FullyQualifiedJavaType.getObjectInstance(), "secondValue", false);
                criterionConstruct.addParameter(additionalConditionParameter);
                criterionConstruct.addParameter(valueParameter);
                criterionConstruct.addParameter(secondValueParameter);
                criterionConstruct.addBodyLine("super();");
                criterionConstruct.addBodyLine("this.additionalCondition = additionalCondition;");
                criterionConstruct.addBodyLine("this.value = value;");
                criterionConstruct.addBodyLine("this.secondValue = secondValue;");
                innerClass.addMethod(criterionConstruct);

            } else if ("GeneratedCriteria".equals(innerClass.getType().getShortName())) {

                Method addCriterion = new Method();
                addCriterion.setName("addCriterion");
                addCriterion.setVisibility(JavaVisibility.PROTECTED);
                Parameter additionalConditionParameter = new Parameter(FullyQualifiedJavaType.getIntInstance(), "additionalCondition", false);
                Parameter valueParameter = new Parameter(FullyQualifiedJavaType.getObjectInstance(), "fieldName", false);
                Parameter secondValueParameter = new Parameter(FullyQualifiedJavaType.getObjectInstance(), "searchValue", false);
                addCriterion.addParameter(additionalConditionParameter);
                addCriterion.addParameter(valueParameter);
                addCriterion.addParameter(secondValueParameter);
                addCriterion.addBodyLine("criteria.add(new Criterion(additionalCondition, fieldName, searchValue));");
                innerClass.addMethod(addCriterion);

                Method andFindInSetMethod = new Method();
                andFindInSetMethod.setVisibility(JavaVisibility.PUBLIC);
                andFindInSetMethod.setName("andFindInSet");
                Parameter fieldNameParameter = new Parameter(FullyQualifiedJavaType.getStringInstance(), "fieldName", false);
                Parameter searchValueParameter = new Parameter(FullyQualifiedJavaType.getObjectInstance(), "searchValue", false);
                andFindInSetMethod.addParameter(fieldNameParameter);
                andFindInSetMethod.addParameter(searchValueParameter);

                andFindInSetMethod.addBodyLine("addCriterion(1, fieldName, searchValue);");
                andFindInSetMethod.addBodyLine("return (Criteria) this;");

                andFindInSetMethod.setReturnType(new FullyQualifiedJavaType("Criteria"));
                innerClass.addMethod(andFindInSetMethod);

                Method andConditionValueMethod = new Method();
                andConditionValueMethod.setVisibility(JavaVisibility.PUBLIC);
                andConditionValueMethod.setName("andConditionValue");
                Parameter searchKeyParameter = new Parameter(FullyQualifiedJavaType.getStringInstance(), "searchCondition", false);
                andConditionValueMethod.addParameter(searchKeyParameter);
                andConditionValueMethod.addParameter(searchValueParameter);

                andConditionValueMethod.addBodyLine("addCriterion(3, searchCondition, searchValue);");
                andConditionValueMethod.addBodyLine("return (Criteria) this;");

                andConditionValueMethod.setReturnType(new FullyQualifiedJavaType("Criteria"));
                innerClass.addMethod(andConditionValueMethod);

            }
        }

        return true;
    }

    @Override
    public boolean sqlMapExampleWhereClauseElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {

        XmlElement chooseChild = findFirstMatchedElement(element, "choose");
        if (chooseChild != null) {
            XmlElement whenFindInSetElement = new XmlElement("when");
            whenFindInSetElement.addAttribute(new Attribute("test", "criterion.additionalCondition == 1"));

            String findInSetContent = "and find_in_set (#{criterion.secondValue}, ${criterion.value})";
            whenFindInSetElement.addElement(new TextElement(findInSetContent));

            chooseChild.addElement(whenFindInSetElement);

            XmlElement keyValueElement = new XmlElement("when");
            keyValueElement.addAttribute(new Attribute("test", "criterion.additionalCondition == 3"));

            String keyValueContent = "and ${criterion.value} #{criterion.secondValue}";
            keyValueElement.addElement(new TextElement(keyValueContent));

            chooseChild.addElement(keyValueElement);

        }

        return true;
    }


}
