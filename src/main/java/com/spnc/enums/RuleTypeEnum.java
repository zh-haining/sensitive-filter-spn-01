package com.spnc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @desc:
 * @author: zh_haining
 * @date: 2021/2/7 下午12:57
 */
@AllArgsConstructor
@Getter
public enum RuleTypeEnum {
    TYPE_1("TYPE_1","DEFAULT")
    ,;
    private String name;
    private String desc;
}
