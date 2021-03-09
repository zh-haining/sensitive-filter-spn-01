package com.spnc.ikexpression;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.spnc.entity.RuleBean;
import com.spnc.enums.OperatorEnum;
import com.spnc.enums.RuleTypeEnum;
import com.spnc.tools.ComplexDFAUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.wltea.expression.ExpressionEvaluator;
import org.wltea.expression.PreparedExpression;
import org.wltea.expression.datameta.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @desc: 过滤引擎
 * @author: zh_haining
 * @date: 2021/2/7 上午11:20
 */
public class FilterEngine {

    @Getter
    @Setter
    private List<RuleBean> type1Rules = Lists.newArrayList();

    public void init() throws Exception {
        //0、IK加载自定义函数
        IKExpressionFactory.addFunction();

        //1、加载规则库
        List<String> originRules = this.loadRules();

        //3、解析规则
        List<RuleBean> ruleBeans = this.wordDecode(originRules);

        //4、构建DFA数据模型
        Set<String> wordsSet = this.fetchComplexRulesWords(ruleBeans);
        this.contructComplexDAF(wordsSet);
        this.type1Rules = ruleBeans.stream().filter(p -> p.getRuleTypeEnum().equals(RuleTypeEnum.TYPE_1)).collect(Collectors.toList());
    }

    /**
     * 加载规则
     *
     * @date 2021/3/9 下午1:27
     * @author zh_haining
     */
    public List<String> loadRules() {
        List<String> rules = Lists.newArrayList();
        rules.add("(\"集团公关\" OR \"公关部\" OR (\"公关\" w/5 (\"元\" OR \"钱\" OR \"人民币\"))) AND NOT (\"媒体公关部\" OR \"政策\")");
        return rules;
    }

    /**
     * 词库解析：将原始rules 解析为ruleBean
     *
     * @date 2021/2/7 上午11:24
     * @auther zh_haining
     */
    public List<RuleBean> wordDecode(List<String> originRules) throws Exception {
        List<RuleBean> ruleBeans = Lists.newArrayList();

        for (String rule : originRules) {
            RuleBean ruleBean = new RuleBean();
            int maxStep = 0;
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
                    if (!termsx.get(i - 1).contains("\"") || !termsx.get(i + 1).contains("\"")) {
                        term = term.toUpperCase();
                    }
                    String[] split = term.split("/");
                    maxStep = Math.max(maxStep, Integer.parseInt(split[1].trim()));
                } else if (term.contains("W/")) {
                    if (termsx.get(i - 1).contains("\"") && termsx.get(i + 1).contains("\"")) {
                        term = term.toLowerCase();
                    }
                    String[] split = term.split("/");
                    maxStep = Math.max(maxStep, Integer.parseInt(split[1].trim()));
                }
                //处理 AND NOT 运算符
                if (term.contains(OperatorEnum.AND.getOperater()) && "NOT".equals(termsx.get(i + 1).trim())) {
                    //如果是AND NOT 则合并处理
                    term = "AND NOT"; //写死，防止AND 和 NOT之前有多个空格引起不必要的问题
                    i++;
                }
                terms.add(term);
            }
            if (terms.contains(OperatorEnum.AND.getOperater())
                    || terms.contains(OperatorEnum.AND_NOT.getOperater())) {
            }
            ruleBean.setRuleCode(RandomStringUtils.random(8, false, true));
            ruleBean.setRuleOrigin(rule);
            ruleBean.setRuleItems(terms);
            ruleBean.setMaxStep(maxStep);
            //构建IKExpression
            ruleBean.setIkexpression(contrctIKExpression(terms));
            //构建IKExpression预编译对象
            List<Variable> variables = new ArrayList<Variable>();
            Map<String, List<Integer>> positionMap = Maps.newHashMap();
            variables.add(Variable.createVariable("positionMap", positionMap));
            PreparedExpression pe = ExpressionEvaluator.preparedCompile(ruleBean.getIkexpression(), variables);
            ruleBean.setPreparedExpression(pe);
            ruleBeans.add(ruleBean);
        }
        return ruleBeans;
    }

    /**
     * 获取复杂rule词库,用于构建DFA算法
     *
     * @date 2021/2/7 下午1:14
     * @auther zh_haining
     */
    public Set<String> fetchComplexRulesWords(List<RuleBean> rules) {
        Set<String> complexWords = Sets.newHashSet();
        if (null == rules || rules.size() < 1) {
            return complexWords;
        }
        for (RuleBean rule : rules) {
            rule.getRuleItems().stream().filter(p -> p.contains("\"")).forEach(p -> {
                String a = p.replace("\"", "");
                complexWords.add(a);
            });
        }
        return complexWords;
    }

    /**
     * 构建复杂 ruleDAF 数据模型
     *
     * @date 2021/2/7 下午1:18
     * @auther zh_haining
     */
    public void contructComplexDAF(Set<String> words) {
        ComplexDFAUtil.init(words);
    }

    /**
     * -> IKExpression可识别的expression
     *
     * @date 2021/2/7 下午1:28
     * @auther zh_haining
     */
    public String contrctIKExpression(List<String> ruleItems) throws Exception {
        List<String> postExpression = IKExpression.midToPost(ruleItems);//存放后缀表达式各字符的List
        return IKExpression.ikExpressionBuild(postExpression);
    }

}
