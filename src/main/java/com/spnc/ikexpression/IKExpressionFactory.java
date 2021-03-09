package com.spnc.ikexpression;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import lombok.Getter;
import org.wltea.expression.function.FunctionLoader;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @desc:
 * @author: zh_haining
 * @date: 2021/2/5 上午11:47
 */
public class IKExpressionFactory {

    @Getter
    private static List<String> rules;

    /**
     * IKExpression 添加自定义函数
     *
     * @date 2021/2/5 下午1:07
     * @auther zh_haining
     */
    public static void addFunction() throws Exception {
        //定义表达式
        Method methodContains = IkFunctions.class.getMethod("contains", Object.class, String.class);
        FunctionLoader.addFunction("contains", new IkFunctions(), methodContains);

        Method methodWithIn = IkFunctions.class.getMethod("within", Object.class, String.class, String.class, int.class);
        FunctionLoader.addFunction("within", new IkFunctions(), methodWithIn);

        Method methodWithinUp = IkFunctions.class.getMethod("withinUp", Object.class, String.class, String.class, int.class);
        FunctionLoader.addFunction("withinUp", new IkFunctions(), methodWithinUp);
    }

    /**
     * 解析原始rule -> IKExpression可识别的expression
     *
     * @date 2021/2/5 下午1:03
     * @auther zh_haining
     */
    public List<String> getRuleExpression(List<String> rules) throws Exception {
        if (null == rules || rules.size() < 1) {
            throw new RuntimeException("规则为空，请检查");
        }
        List<String> expressions = Lists.newArrayList();
        for (String rule : rules) {
            //中缀表达式转换为后缀表达式
            List<String> termsx =
                    Splitter.on(" ").omitEmptyStrings().trimResults()
                            .splitToList(
                                    rule.replace("(", " ( ")
                                            .replace(")", " ) ")
                                            .replace("w/", " w/")
                                            .replace("W/", " W/")
                            );

            List<String> terms = Lists.newArrayList();
            for (int i = 0; i < termsx.size(); i++) {
                String term = termsx.get(i);
                if (term.contains("w/")) {
                    if (!termsx.get(i - 1).contains("\"")) {
                        term = term.toUpperCase();
                    }
                }
                terms.add(term);
            }
            System.out.println("中缀表达式：" + Joiner.on(" , ").join(terms));
            //存放后缀表达式各字符的List
            List<String> postExpression = IKExpression.midToPost(terms);
            System.out.println("后缀表达式:" + Joiner.on(" ").join(postExpression));
            String expression = IKExpression.ikExpressionBuild(postExpression);
            System.out.println("ik-expression：" + expression);
            expressions.add(expression);
        }
        return expressions;
    }

}
