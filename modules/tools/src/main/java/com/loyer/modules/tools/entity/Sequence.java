package com.loyer.modules.tools.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 序列实体类
 *
 * @author kuangq
 * @date 2021-06-27 13:24
 */
@Data
@NoArgsConstructor
public class Sequence {

    //序列号
    private Long value;

    //创建时间
    private Date createTime;
}