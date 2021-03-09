package com.spnc.entity;

import com.spnc.enums.RuleTypeEnum;
import lombok.Getter;
import lombok.Setter;
import org.wltea.expression.PreparedExpression;

import java.io.Serializable;
import java.util.List;

/**
 * @desc: rule 规则Bean
 * @author: zh_haining
 * @date: 2021/2/7 上午11:15
 */
@Setter
@Getter
public class RuleBean implements Serializable {
    private static final long serialVersionUID = 2025793760584174993L;
    private String ruleCode;
    private String ruleOrigin;
    private List<String> ruleItems;
    private int maxStep;
    //默认为TYPE_1
    private RuleTypeEnum ruleTypeEnum = RuleTypeEnum.TYPE_1;
    private String ikexpression = "";
    //IKExpression 预编译对象
    private PreparedExpression preparedExpression;
}
