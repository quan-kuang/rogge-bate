package com.loyer.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * 微信公众号发送通知消息
 *
 * @author kuangq
 * @date 2023-3-22 16:28
 */
@Data
@NoArgsConstructor
public class MassMessage {

    @JSONField(name = "touser")
    private List<String> receiver;

    @JSONField(name = "images")
    private ImageInfo imageInfo;

    @JSONField(name = "msgtype")
    private String msgType;

    @Data
    @NoArgsConstructor
    public static class ImageInfo {

        @JSONField(name = "media_ids")
        private List<String> mediaIdList;

        //是否打开评论，0不打开，1打开
        @JSONField(name = "need_open_comment")
        private Integer needOpenComment;

        //是否粉丝才可评论，0所有人可评论，1粉丝才可评论
        @JSONField(name = "only_fans_can_comment")
        private Integer onlyFansCanComment;

        //推荐语，不填则默认为“分享图片”
        private String recommend;

        public void setNeedOpenComment(boolean needOpenComment) {
            this.needOpenComment = needOpenComment ? 1 : 0;
        }

        public void setOnlyFansCanComment(boolean onlyFansCanComment) {
            this.onlyFansCanComment = onlyFansCanComment ? 1 : 0;
        }
    }
}