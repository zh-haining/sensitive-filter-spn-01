package com.spnc.ikexpression;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.spnc.enums.OperatorEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * @desc:
 * @author: zh_haining
 * @date: 2021/2/4 下午2:11
 */
public class IKExpression {
    public static String ikExpressionBuild(List<String> postExpress) {
        Stack<String> stack = new Stack<>();
        for (int i = 0; i < postExpress.size(); i++) {
            String term = postExpress.get(i);
            StringBuffer content = new StringBuffer();
            if (term.contains("\"")) {
                stack.push(term);
                continue;
            }
            String right = stack.pop();
            String left = stack.pop();
            if (OperatorEnum.AND.getOperater().equalsIgnoreCase(term)) {
                if (!left.contains("$")) {
                    content.append("$contains(positionMap, ")
                            .append(left)
                            .append(" ) && ");
                } else {
                    content.append(left).append(" && ");
                }
                if (!right.contains("$")) {
                    content.append("$contains(positionMap, ")
                            .append(right)
                            .append(" )");
                } else {
                    content.append(right);
                }
            } else if (OperatorEnum.AND_NOT.getOperater().equalsIgnoreCase(term)) {
                if (!left.contains("$")) {
                    content.append("$contains(positionMap, ")
                            .append(left)
                            .append(" ) && ");
                } else {
                    content.append(left).append(" && ");
                }
                content.append(" !( ")
                        .append(right)
                        .append(" )");
            } else if (OperatorEnum.OR.getOperater().equalsIgnoreCase(term)) {
                if (!left.contains("$")) {
                    content.append("$contains(positionMap, ")
                            .append(left)
                            .append(" ) || ");
                } else {
                    content.append(left).append(" || ");
                }
                if (!right.contains("$")) {
                    content.append("$contains(positionMap, ")
                            .append(right)
                            .append(" )");
                } else {
                    content.append(right);
                }
            } else if (term.startsWith(OperatorEnum.w.getOperater())) {
                int step = Integer.parseInt(term.split("/")[1].trim());
                content.append("$within(positionMap, ")
                        .append(left)
                        .append(" , ")
                        .append(right)
                        .append(" , ")
                        .append(step)
                        .append(" )");
            } else if (term.startsWith(OperatorEnum.W.getOperater())) {
                String leftX = "\"" + getKh(left, null) + "\"";
                String rightX = "\"" + getKh(right, null) + "\"";
                int step = Integer.parseInt(term.split("/")[1].trim());
                if (!left.contains("(")) { //兼容适配规则： "顺顺" W/30 ("出售" OR "出让")
                    left = "$contains(positionMap," + left + " )";
                }
                if (!right.contains("(")) {//兼容适配规则： ("出售" OR "出让") W/30 "顺顺"
                    right = "$contains(positionMap," + right + " )";
                }
                content.append(left)
                        .append(" && ")
                        .append(right)
                        .append(" && ")
                        .append("$withinUp(positionMap, ")
                        .append(leftX)
                        .append(" , ")
                        .append(rightX)
                        .append(" , ")
                        .append(step)
                        .append(" )");
            }
            content.insert(0, "(").append(")");
            stack.push(content.toString());
        }
        String expression = stack.pop();
        return expression;
    }

    /**
     * 中缀表达式转后缀表达式
     *
     * @param terms
     * @return
     */
    public static List<String> midToPost(List<String> terms) {
        Stack<String> stack = new Stack<>();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < terms.size(); i++) {
            if (terms.get(i).contains("\"")) { //如果不是操作符
                list.add(terms.get(i));
            } else if (Lists.newArrayList(
                    OperatorEnum.AND.getOperater(),
                    OperatorEnum.OR.getOperater(),
                    OperatorEnum.AND_NOT.getOperater()
            ).contains(terms.get(i))
                    || terms.get(i).startsWith(OperatorEnum.w.getOperater())
                    || terms.get(i).startsWith(OperatorEnum.W.getOperater())) {
                //如果stack为空
                if (stack.isEmpty()) {
                    stack.push(terms.get(i));
                    continue;
                }
                //不为空
                //上一个元素不为（，且当前运算符优先级小于上一个元素则，将比这个运算符优先级大的元素全部加入到队列中
                while (!stack.isEmpty() && !stack.lastElement().equals("(") && !comparePriority(terms.get(i), stack.lastElement())) {
                    list.add(stack.pop());
                }
                stack.push(terms.get(i));
            } else if (terms.get(i).equalsIgnoreCase("(")) {
                //遇到左小括号无条件加入
                stack.push(terms.get(i));
            } else if (terms.get(i).equalsIgnoreCase(")")) {
                //遇到右小括号，则寻找上一堆小括号，然后把中间的值全部放入队列中
                while (!("(").equals(stack.lastElement())) {
                    list.add(stack.pop());
                }
                //上述循环停止，这栈顶元素必为"("
                stack.pop();
            }
        }
        //将栈中剩余元素加入到队列中
        while (!stack.isEmpty()) {
            list.add(stack.pop());
        }
        return list;
    }

    /**
     * 比较运算符的优先级
     *
     * @param o1
     * @param o2
     * @return
     */
    public static boolean comparePriority(String o1, String o2) {
        return getPriorityValue(o1) > getPriorityValue(o2);
    }

    /**
     * 获得运算符的优先级
     *
     * @param str
     * @return
     */
    private static int getPriorityValue(String str) {
        switch (str) {
            case "AND":
                return 10;
            case "AND NOT":
                return 10;
            case "OR":
                return 9;
            case "W/":
                return 8;
            case "w/":
                return 8;
            default:
                throw new RuntimeException("没有该类型的运算符！");
        }
    }


    public static String getKh(String str, String seprator) {
        if (StringUtils.isEmpty(seprator)) {
            seprator = "###";
        }
        return Joiner.on(seprator).join(Arrays.stream(str.split(" ")).filter(item -> item.contains("\"")).collect(Collectors.toSet())).replace("\"", "");
    }

    public String contructTestStr(String rule) {
        List<String> termsx =
                Splitter.on(" ").omitEmptyStrings().trimResults()
                        .splitToList(
                                rule.replace("(", " ( ")
                                        .replace(")", " ) ")
                                        .replace("w/", " w/")
                                        .replace("W/", " W/")
                        );
        return termsx.stream().filter(p -> p.contains("\"")).map(p -> p.replace("\"", "")).collect(Collectors.joining());
    }

    /**
     * 判断两个数组元素之间的差值有没有小于等于指定值的
     *
     * @date 2021/2/4 下午7:23
     * @auther zh_haining
     */
    @Deprecated
    public static boolean jude(int[] a, int[] b, int d) {
        Map<Integer, String> map = Maps.newHashMap();
        Arrays.stream(a).forEach(p -> map.put(p, "a"));
        Arrays.stream(b).forEach(p -> map.put(p, "b"));
        List<Map.Entry<Integer, String>> collect = map.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toList());

        Map.Entry<Integer, String> tmp = null;
        for (Map.Entry<Integer, String> item : collect) {
            if (tmp == null) {
                tmp = item;
                continue;
            }
            if (item.getValue().equals(tmp.getValue())) {
                tmp = item;
                continue;
            }
            if (Math.abs(item.getKey() - tmp.getKey()) <= d) {
                return true;
            }
            tmp = item;
        }
        return false;
    }
}
