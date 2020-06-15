package com.revengemission.plugins.mybatis;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 增强example Criterion,支持两个参数的函数
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

                Field additionalCondition = new Field("additionalCondition", FullyQualifiedJavaType.getIntInstance());
                additionalCondition.setVisibility(JavaVisibility.PRIVATE);
                additionalCondition.setInitializationString("0");
                innerClass.addField(additionalCondition);

                Method getAdditionalCondition = new Method("getAdditionalCondition");
                getAdditionalCondition.setVisibility(JavaVisibility.PUBLIC);
                getAdditionalCondition.setReturnType(FullyQualifiedJavaType.getIntInstance());
                getAdditionalCondition.addBodyLine("return additionalCondition;");
                innerClass.addMethod(getAdditionalCondition);

                Method criterionConstruct = new Method("Criterion");
                criterionConstruct.setConstructor(true);
                Parameter additionalConditionParameter = new Parameter(FullyQualifiedJavaType.getIntInstance(), "additionalCondition", false);
                Parameter functionNameParameter = new Parameter(FullyQualifiedJavaType.getStringInstance(), "functionName", false);
                Parameter valueParameter = new Parameter(FullyQualifiedJavaType.getObjectInstance(), "value", false);
                Parameter secondValueParameter = new Parameter(FullyQualifiedJavaType.getObjectInstance(), "secondValue", false);
                criterionConstruct.addParameter(additionalConditionParameter);
                criterionConstruct.addParameter(functionNameParameter);
                criterionConstruct.addParameter(valueParameter);
                criterionConstruct.addParameter(secondValueParameter);
                criterionConstruct.addBodyLine("super();");
                criterionConstruct.addBodyLine("this.additionalCondition = additionalCondition;");
                criterionConstruct.addBodyLine("this.condition = functionName;");
                criterionConstruct.addBodyLine("this.value = value;");
                criterionConstruct.addBodyLine("this.secondValue = secondValue;");
                innerClass.addMethod(criterionConstruct);

            } else if ("GeneratedCriteria".equals(innerClass.getType().getShortName())) {

                // 当字段value为null时不抛出异常，跳过、忽略添加该条件
                innerClass.getMethods().forEach(method -> {
                    if ("addCriterion".equals(method.getName()) && method.getParameters().size() == 3) {
                        for (int j = 0; j < method.getBodyLines().size(); j++) {
                            if (method.getBodyLines().get(j).contains("throw")) {
                                method.getBodyLines().remove(j);
                                method.getBodyLines().add(j, "return;");
                                break;
                            }
                        }
                    }

                });

                Method addCriterion = new Method("addCriterion");
                addCriterion.setVisibility(JavaVisibility.PROTECTED);
                Parameter additionalConditionParameter = new Parameter(FullyQualifiedJavaType.getIntInstance(), "additionalCondition", false);
                Parameter functionNameParameter = new Parameter(FullyQualifiedJavaType.getStringInstance(), "functionName", false);
                Parameter valueParameter = new Parameter(FullyQualifiedJavaType.getObjectInstance(), "value", false);
                Parameter secondValueParameter = new Parameter(FullyQualifiedJavaType.getObjectInstance(), "secondValue", false);
                addCriterion.addParameter(additionalConditionParameter);
                addCriterion.addParameter(functionNameParameter);
                addCriterion.addParameter(valueParameter);
                addCriterion.addParameter(secondValueParameter);
                addCriterion.addBodyLine("criteria.add(new Criterion(additionalCondition, functionName, value, secondValue));");
                innerClass.addMethod(addCriterion);

                Method andConditionValueMethod = new Method("andConditionValue");
                andConditionValueMethod.setVisibility(JavaVisibility.PUBLIC);
                Parameter searchConditionParameter = new Parameter(FullyQualifiedJavaType.getStringInstance(), "searchCondition", false);
                Parameter searchValueParameter = new Parameter(FullyQualifiedJavaType.getObjectInstance(), "searchValue", false);
                andConditionValueMethod.addParameter(searchConditionParameter);
                andConditionValueMethod.addParameter(searchValueParameter);
                andConditionValueMethod.addBodyLine("addCriterion(3, \"conditionValue\", searchCondition, searchValue);");
                andConditionValueMethod.addBodyLine("return (Criteria) this;");
                andConditionValueMethod.setReturnType(new FullyQualifiedJavaType("Criteria"));
                innerClass.addMethod(andConditionValueMethod);

                Method andFunctionLeftKeyMethod = new Method("andFunctionLeftKey");
                andFunctionLeftKeyMethod.setVisibility(JavaVisibility.PUBLIC);
                Parameter functionParameter = new Parameter(FullyQualifiedJavaType.getStringInstance(), "functionName", false);
                Parameter searchKeyParameter = new Parameter(FullyQualifiedJavaType.getStringInstance(), "searchKey", false);
                andFunctionLeftKeyMethod.addParameter(functionParameter);
                andFunctionLeftKeyMethod.addParameter(searchKeyParameter);
                andFunctionLeftKeyMethod.addParameter(searchValueParameter);
                andFunctionLeftKeyMethod.addBodyLine("addCriterion(5, functionName, searchKey, searchValue);");
                andFunctionLeftKeyMethod.addBodyLine("return (Criteria) this;");
                andFunctionLeftKeyMethod.setReturnType(new FullyQualifiedJavaType("Criteria"));
                innerClass.addMethod(andFunctionLeftKeyMethod);

                Method andFunctionRightKeyMethod = new Method("andFunctionRightKey");
                andFunctionRightKeyMethod.setVisibility(JavaVisibility.PUBLIC);
                andFunctionRightKeyMethod.addParameter(functionParameter);
                andFunctionRightKeyMethod.addParameter(searchKeyParameter);
                andFunctionRightKeyMethod.addParameter(searchValueParameter);
                andFunctionRightKeyMethod.addBodyLine("addCriterion(6, functionName, searchKey, searchValue);");
                andFunctionRightKeyMethod.addBodyLine("return (Criteria) this;");
                andFunctionRightKeyMethod.setReturnType(new FullyQualifiedJavaType("Criteria"));
                innerClass.addMethod(andFunctionRightKeyMethod);

            }
        }

        return true;
    }

    @Override
    public boolean sqlMapExampleWhereClauseElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {

        XmlElement chooseChild = findFirstMatchedXmlElement(element, "choose");
        if (chooseChild != null) {

            XmlElement keyValueElement = new XmlElement("when");
            keyValueElement.addAttribute(new Attribute("test", "criterion.additionalCondition == 3"));
            keyValueElement.addElement(new TextElement("and ${criterion.value} #{criterion.secondValue}"));
            chooseChild.addElement(keyValueElement);

            XmlElement functionLeftElement = new XmlElement("when");
            functionLeftElement.addAttribute(new Attribute("test", "criterion.additionalCondition == 5"));
            functionLeftElement.addElement(new TextElement("and ${criterion.condition} (${criterion.value}, #{criterion.secondValue})"));
            chooseChild.addElement(functionLeftElement);

            XmlElement functionRightElement = new XmlElement("when");
            functionRightElement.addAttribute(new Attribute("test", "criterion.additionalCondition == 6"));
            functionRightElement.addElement(new TextElement("and ${criterion.condition} (#{criterion.secondValue}, ${criterion.value})"));
            chooseChild.addElement(functionRightElement);

        }
        return true;
    }


}
