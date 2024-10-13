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
        for (InnerClass innerClass : innerClasses) {
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

                Field thirdValue = new Field("thirdValue", FullyQualifiedJavaType.getObjectInstance());
                thirdValue.setVisibility(JavaVisibility.PRIVATE);
                innerClass.addField(thirdValue);

                Method getThirdValue = new Method("getThirdValue");
                getThirdValue.setVisibility(JavaVisibility.PUBLIC);
                getThirdValue.setReturnType(FullyQualifiedJavaType.getObjectInstance());
                getThirdValue.addBodyLine("return thirdValue;");
                innerClass.addMethod(getThirdValue);


                Parameter additionalConditionParameter = new Parameter(FullyQualifiedJavaType.getIntInstance(), "additionalCondition", false);
                Parameter conditionParameter = new Parameter(FullyQualifiedJavaType.getStringInstance(), "condition", false);
                Parameter valueParameter = new Parameter(FullyQualifiedJavaType.getObjectInstance(), "value", false);
                Parameter secondValueParameter = new Parameter(FullyQualifiedJavaType.getObjectInstance(), "secondValue", false);
                Parameter thirdValueParameter = new Parameter(FullyQualifiedJavaType.getObjectInstance(), "thirdValue", false);

                Method criterionConstruct = new Method("Criterion");
                criterionConstruct.setConstructor(true);
                criterionConstruct.setVisibility(JavaVisibility.PROTECTED);
                criterionConstruct.addParameter(additionalConditionParameter);
                criterionConstruct.addParameter(conditionParameter);
                criterionConstruct.addParameter(valueParameter);
                criterionConstruct.addParameter(secondValueParameter);
                criterionConstruct.addBodyLine("super();");
                criterionConstruct.addBodyLine("this.additionalCondition = additionalCondition;");
                criterionConstruct.addBodyLine("this.condition = condition;");
                criterionConstruct.addBodyLine("this.value = value;");
                criterionConstruct.addBodyLine("this.secondValue = secondValue;");
                innerClass.addMethod(criterionConstruct);

                {
                    Method criterionThirdConstruct = new Method("Criterion");
                    criterionThirdConstruct.setConstructor(true);
                    criterionThirdConstruct.setVisibility(JavaVisibility.PROTECTED);
                    criterionThirdConstruct.addParameter(additionalConditionParameter);
                    criterionThirdConstruct.addParameter(conditionParameter);
                    criterionThirdConstruct.addParameter(valueParameter);
                    criterionThirdConstruct.addParameter(secondValueParameter);
                    criterionThirdConstruct.addParameter(thirdValueParameter);
                    criterionThirdConstruct.addBodyLine("super();");
                    criterionThirdConstruct.addBodyLine("this.additionalCondition = additionalCondition;");
                    criterionThirdConstruct.addBodyLine("this.condition = condition;");
                    criterionThirdConstruct.addBodyLine("this.value = value;");
                    criterionThirdConstruct.addBodyLine("this.secondValue = secondValue;");
                    criterionThirdConstruct.addBodyLine("this.thirdValue = thirdValue;");
                    innerClass.addMethod(criterionThirdConstruct);
                }

            } else if ("GeneratedCriteria".equals(innerClass.getType().getShortName())) {

                innerClass.getMethods().forEach(method -> {
                    //equalTo 当字段value为null时不抛出异常，跳过、忽略添加该条件
                    if ("addCriterion".equals(method.getName()) && method.getParameters().size() == 3) {
                        for (int j = 0; j < method.getBodyLines().size(); j++) {
                            if (method.getBodyLines().get(j).contains("throw")) {
                                method.getBodyLines().remove(j);
                                method.getBodyLines().add(j, "return;");
                                break;
                            }
                        }
                    }

                    // between and 当字段value为null时不抛出异常，跳过、忽略添加该条件
                    if ("addCriterion".equals(method.getName()) && method.getParameters().size() == 4) {
                        String types = "String_Object_Object_String_";
                        StringBuilder stringBuilder = new StringBuilder();
                        method.getParameters().forEach(e -> {
                            stringBuilder.append(e.getType().getShortName()).append("_");
                        });
                        if (types.contentEquals(stringBuilder)) {
                            for (int j = 0; j < method.getBodyLines().size(); j++) {
                                if (method.getBodyLines().get(j).contains("throw")) {
                                    method.getBodyLines().remove(j);
                                    method.getBodyLines().add(j, "return;");
                                    break;
                                }
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

                {
                    Method thirdAddCriterion = new Method("addCriterion");
                    thirdAddCriterion.setVisibility(JavaVisibility.PROTECTED);
                    Parameter thirdValueParameter = new Parameter(FullyQualifiedJavaType.getObjectInstance(), "thirdValue", false);
                    thirdAddCriterion.addParameter(additionalConditionParameter);
                    thirdAddCriterion.addParameter(functionNameParameter);
                    thirdAddCriterion.addParameter(valueParameter);
                    thirdAddCriterion.addParameter(secondValueParameter);
                    thirdAddCriterion.addParameter(thirdValueParameter);
                    thirdAddCriterion.addBodyLine("criteria.add(new Criterion(additionalCondition, functionName, value, secondValue, thirdValue));");
                    innerClass.addMethod(thirdAddCriterion);
                }

                Method andConditionValueMethod = new Method("andConditionValue");
                andConditionValueMethod.setVisibility(JavaVisibility.PUBLIC);
                Parameter searchConditionParameter = new Parameter(FullyQualifiedJavaType.getStringInstance(), "searchCondition", false);
                Parameter searchValueParameter = new Parameter(FullyQualifiedJavaType.getObjectInstance(), "searchValue", false);
                andConditionValueMethod.addParameter(searchConditionParameter);
                andConditionValueMethod.addParameter(searchValueParameter);
                andConditionValueMethod.addBodyLine("addCriterion(searchCondition, searchValue, \"\");");
                andConditionValueMethod.addBodyLine("return (Criteria) this;");
                andConditionValueMethod.setReturnType(new FullyQualifiedJavaType("Criteria"));
                innerClass.addMethod(andConditionValueMethod);

                {
                    Method andConditionJsonFieldValueMethod = new Method("andConditionJsonFieldValue");
                    andConditionJsonFieldValueMethod.setVisibility(JavaVisibility.PUBLIC);
                    Parameter jsonColumnConditionParameter = new Parameter(FullyQualifiedJavaType.getStringInstance(), "jsonColumn", false);
                    Parameter jsonFieldValueParameter = new Parameter(FullyQualifiedJavaType.getStringInstance(), "jsonField", false);
                    Parameter jsonConditionParameter = new Parameter(FullyQualifiedJavaType.getStringInstance(), "condition", false);
                    Parameter jsonSearchValueParameter = new Parameter(FullyQualifiedJavaType.getObjectInstance(), "searchValue", false);
                    andConditionJsonFieldValueMethod.addParameter(jsonColumnConditionParameter);
                    andConditionJsonFieldValueMethod.addParameter(jsonFieldValueParameter);
                    andConditionJsonFieldValueMethod.addParameter(jsonConditionParameter);
                    andConditionJsonFieldValueMethod.addParameter(jsonSearchValueParameter);
                    andConditionJsonFieldValueMethod.addBodyLine("addCriterion(4, condition, jsonColumn, jsonField, searchValue);");
                    andConditionJsonFieldValueMethod.addBodyLine("return (Criteria) this;");
                    andConditionJsonFieldValueMethod.setReturnType(new FullyQualifiedJavaType("Criteria"));
                    innerClass.addMethod(andConditionJsonFieldValueMethod);

                    Method andConditionJsonFieldContainsMethod = new Method("andConditionJsonFieldContains");
                    andConditionJsonFieldContainsMethod.setVisibility(JavaVisibility.PUBLIC);
                    andConditionJsonFieldContainsMethod.addParameter(jsonColumnConditionParameter);
                    andConditionJsonFieldContainsMethod.addParameter(jsonFieldValueParameter);
                    andConditionJsonFieldContainsMethod.addParameter(jsonSearchValueParameter);
                    andConditionJsonFieldContainsMethod.addBodyLine("addCriterion(7, \"JSON_CONTAINS\", jsonColumn, jsonField, searchValue);");
                    andConditionJsonFieldContainsMethod.addBodyLine("return (Criteria) this;");
                    andConditionJsonFieldContainsMethod.setReturnType(new FullyQualifiedJavaType("Criteria"));
                    innerClass.addMethod(andConditionJsonFieldContainsMethod);
                }


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

                introspectedTable.getAllColumns().forEach(introspectedColumn -> {
                    String javaProperty = introspectedColumn.getJavaProperty();
                    String columnName = introspectedColumn.getActualColumnName();
                    Parameter expValue = new Parameter(FullyQualifiedJavaType.getStringInstance(), "regexp", false);
                    Method andRegexpMethod = new Method("and" + upperCaseFirstChar(javaProperty) + "Regexp");
                    andRegexpMethod.setVisibility(JavaVisibility.PUBLIC);
                    andRegexpMethod.addParameter(expValue);
                    andRegexpMethod.addBodyLine("addCriterion(\"" + columnName + " regexp\", " + "regexp, \"" + javaProperty + "\");");
                    andRegexpMethod.addBodyLine("return (Criteria) this;");
                    andRegexpMethod.setReturnType(new FullyQualifiedJavaType("Criteria"));
                    innerClass.addMethod(andRegexpMethod);
                });

            }
        }

        return true;
    }

    @Override
    public boolean sqlMapExampleWhereClauseElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {

        XmlElement chooseChild = findFirstMatchedXmlElement(element, "choose");
        if (chooseChild != null) {

            /*XmlElement keyValueElement = new XmlElement("when");
            keyValueElement.addAttribute(new Attribute("test", "criterion.additionalCondition == 3"));
            keyValueElement.addElement(new TextElement("and ${criterion.condition} #{criterion.value}"));
            chooseChild.addElement(keyValueElement);*/

            {
                {
                    XmlElement jsonKeyValueElement = new XmlElement("when");
                    jsonKeyValueElement.addAttribute(new Attribute("test", "criterion.additionalCondition == 4"));
                    XmlElement jsonIfMysqlElement = new XmlElement("if");
                    jsonIfMysqlElement.addAttribute(new Attribute("test", "_databaseId == 'mysql'"));
                    jsonIfMysqlElement.addElement(new TextElement("and ${criterion.value}->>'$.${criterion.secondValue}' ${criterion.condition} #{criterion.thirdValue}"));
                    jsonKeyValueElement.addElement(jsonIfMysqlElement);

                    XmlElement jsonIfPostgresqlElement = new XmlElement("if");
                    jsonIfPostgresqlElement.addAttribute(new Attribute("test", "_databaseId == 'postgresql'"));
                    jsonIfPostgresqlElement.addElement(new TextElement("and ${criterion.value}->>'${criterion.secondValue}' ${criterion.condition} #{criterion.thirdValue}"));
                    jsonKeyValueElement.addElement(jsonIfPostgresqlElement);

                    XmlElement jsonIfNotMysqlElement = new XmlElement("if");
                    jsonIfNotMysqlElement.addAttribute(new Attribute("test", "_databaseId != 'mysql' and _databaseId != 'postgresql'"));
                    jsonIfNotMysqlElement.addElement(new TextElement("<!--"));
                    jsonIfNotMysqlElement.addElement(new TextElement("  otherwise."));
                    jsonIfNotMysqlElement.addElement(new TextElement("-->"));
                    jsonKeyValueElement.addElement(jsonIfNotMysqlElement);
                    chooseChild.addElement(jsonKeyValueElement);
                }

                {
                    XmlElement jsonKeyValueElement = new XmlElement("when");
                    jsonKeyValueElement.addAttribute(new Attribute("test", "criterion.additionalCondition == 7"));
                    XmlElement jsonIfMysqlElement = new XmlElement("if");
                    jsonIfMysqlElement.addAttribute(new Attribute("test", "_databaseId == 'mysql'"));
                    XmlElement sameIfMysqlElement = new XmlElement("if");
                    sameIfMysqlElement.addAttribute(new Attribute("test", "criterion.value == criterion.secondValue"));
                    sameIfMysqlElement.addElement(new TextElement("and JSON_CONTAINS(${criterion.value}->>'$', #{criterion.thirdValue})"));
                    jsonIfMysqlElement.addElement(sameIfMysqlElement);
                    XmlElement notSameIfMysqlElement = new XmlElement("if");
                    notSameIfMysqlElement.addAttribute(new Attribute("test", "criterion.value != criterion.secondValue"));
                    notSameIfMysqlElement.addElement(new TextElement("and JSON_CONTAINS(${criterion.value}->>'$.${criterion.secondValue}', #{criterion.thirdValue})"));
                    jsonIfMysqlElement.addElement(notSameIfMysqlElement);
                    jsonKeyValueElement.addElement(jsonIfMysqlElement);

                    XmlElement jsonIfPostgresqlElement = new XmlElement("if");
                    jsonIfPostgresqlElement.addAttribute(new Attribute("test", "_databaseId == 'postgresql'"));
                    XmlElement sameIfPostgresqlElement = new XmlElement("if");
                    sameIfPostgresqlElement.addAttribute(new Attribute("test", "criterion.value == criterion.secondValue"));
                    sameIfPostgresqlElement.addElement(new TextElement("and ${criterion.value}::jsonb @> #{criterion.thirdValue}::jsonb"));
                    jsonIfPostgresqlElement.addElement(sameIfPostgresqlElement);
                    XmlElement notSameIfPostgresqlElement = new XmlElement("if");
                    notSameIfPostgresqlElement.addAttribute(new Attribute("test", "criterion.value != criterion.secondValue"));
                    notSameIfPostgresqlElement.addElement(new TextElement("and (${criterion.value}->>'${criterion.secondValue}')::jsonb @> #{criterion.thirdValue}::jsonb"));
                    jsonIfPostgresqlElement.addElement(notSameIfPostgresqlElement);
                    jsonKeyValueElement.addElement(jsonIfPostgresqlElement);

                    XmlElement jsonIfNotMysqlElement = new XmlElement("if");
                    jsonIfNotMysqlElement.addAttribute(new Attribute("test", "_databaseId != 'mysql' and _databaseId != 'postgresql'"));
                    jsonIfNotMysqlElement.addElement(new TextElement("<!--"));
                    jsonIfNotMysqlElement.addElement(new TextElement("  otherwise."));
                    jsonIfNotMysqlElement.addElement(new TextElement("-->"));
                    jsonKeyValueElement.addElement(jsonIfNotMysqlElement);
                    chooseChild.addElement(jsonKeyValueElement);
                }
            }

            XmlElement functionLeftElement = new XmlElement("when");
            functionLeftElement.addAttribute(new Attribute("test", "criterion.additionalCondition == 5"));
            functionLeftElement.addElement(new TextElement("and ${criterion.condition} (${criterion.value}, #{criterion.secondValue})"));
            chooseChild.addElement(functionLeftElement);

            XmlElement functionRightElement = new XmlElement("when");
            functionRightElement.addAttribute(new Attribute("test", "criterion.additionalCondition == 6"));
            functionRightElement.addElement(new TextElement("and ${criterion.condition} (#{criterion.secondValue}, ${criterion.value})"));
            chooseChild.addElement(functionRightElement);

            /*XmlElement otherwiseElement = new XmlElement("otherwise");
            otherwiseElement.addElement(new TextElement("<!--"));
            otherwiseElement.addElement(new TextElement("  otherwise."));
            otherwiseElement.addElement(new TextElement("-->"));
            chooseChild.addElement(otherwiseElement);*/

        }
        return true;
    }

    private boolean containsBLOBColumn(IntrospectedTable introspectedTable) {
        return !introspectedTable.getBLOBColumns().isEmpty();
    }


}
