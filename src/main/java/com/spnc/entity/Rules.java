package com.spnc.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 敏感词规则
 * </p>
 *
 * @author zh_haining
 * @since 2021-02-25
 */
@Setter
@Getter
@Builder
public class Rules implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 逻辑主键 自增
     */
    private Long id;

    /**
     * 规则编号
     */
    private String ruleCode;

    /**
     * 规则类型
     */
    private String ruleType;

    /**
     * 规则原文
     */
    private String ruleContent;

    /**
     * 是否启用； YES-启用； NO-未启用
     */
    private String valid;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


}
