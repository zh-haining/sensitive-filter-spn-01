package com.spnc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @desc: 操作符
 * @author: zh_haining
 * @date: 2021/2/7 上午11:38
 */
@AllArgsConstructor
@Getter
public enum OperatorEnum {
    AND("AND", " && ", "逻辑与"),
    OR("OR", " && ", "逻辑或"),
    AND_NOT("AND NOT", " ! ", "逻辑非"),
    W("W/", "W/", "间隔步长"),
    w("w/", " w/ ", "间隔步长"),
    ;

    private String operater;
    private String code;
    private String desc;
}
