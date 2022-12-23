package com.loyer.common.dedicine.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信报警
 *
 * @author kuangq
 * @date 2022-12-23 10:03
 */
@Data
@NoArgsConstructor
public class WeChatAlarm {

    private String title;

    private String content;

    private String date;
}