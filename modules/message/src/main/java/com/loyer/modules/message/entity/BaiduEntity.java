package com.loyer.modules.message.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 百度API实体类（文档说明：https://ai.baidu.com/ai-doc/FACE/Zk37c1urr）
 *
 * @author kuangq
 * @date 2020-10-22 16:12
 */
@SuppressWarnings("ALL")
@Data
@NoArgsConstructor
public class BaiduEntity {

    @Data
    @NoArgsConstructor
    public static class BaseResult {
        private Double cached;
        private Double error_code;
        private String error_msg;
        private Long log_id;
        private Long timestamp;
    }

    @Data
    @NoArgsConstructor
    public static class OauthRequest {
        //固定为client_credentials
        private String grant_type;
        //应用的API Key
        private String client_id;
        //应用的Secret Key
        private String client_secret;
    }

    @Data
    @NoArgsConstructor
    public static class OauthResponse {
        //要获取的Access Token
        private String access_token;
        //Access Token的有效期(秒为单位，一般为1个月)
        private Double expires_in;
        //其他参数忽略，暂时不用
        private String refresh_token;
        private String scope;
        private String session_key;
        private String session_secret;
    }

    @Data
    @NoArgsConstructor
    public static class FaceDetectRequest {
        //图片信息(总数据大小应小于10M)，图片上传方式根据image_type来判断
        private String image;
        //图片类型：BASE64：图片的base64值，编码后的图片大小不超过2M；URL：图片的URL地址；FACE_TOKEN: 调用人脸检测接口时，会为每个人脸图片赋予一个唯一的FACE_TOKEN，同一张图片多次检测得到的FACE_TOKEN是同一个
        private String image_type;
        //包括age,beauty,expression,face_shape,gender,glasses,quality,face_type,spoofing,eye_status,emotion,mask,race,landmark,landmark150信息，逗号分隔，默认只返回face_token、人脸框、概率和旋转角度
        private String face_field;
        //最多处理人脸的数目，默认值为1，仅检测图片中面积最大的那个人脸；最大值10，检测图片中面积最大的几张人脸。
        private Integer max_face_num;
        //人脸的类型：LIVE表示生活照：通常为手机、相机拍摄的人像图片、或从网络获取的人像图片等；IDCARD表示身份证芯片照：二代身份证内置芯片中的人像照片；WATERMARK表示带水印证件照：一般为带水印的小图，如公安网小图；CERT表示证件照片：如拍摄的身份证、工卡、护照、学生证等证件图片；默认LIVE
        private String face_type;
        //从指定的group中进行查找 用逗号分隔，上限10个
        private String group_id_list;
        //图片质量控制：NONE: 不进行控制；LOW:较低的质量要求；NORMAL: 一般的质量要求；HIGH: 较高的质量要求；默认NONE，若图片质量不满足要求，则返回结果中会提示质量检测失败
        private String quality_control;
        //活体控制：NONE: 不进行控制；LOW:较低的活体要求(高通过率 低攻击拒绝率)；NORMAL: 一般的活体要求(平衡的攻击拒绝率, 通过率)；HIGH: 较高的活体要求(高攻击拒绝率 低通过率)；默认NONE
        private String liveness_control;
        //当需要对特定用户进行比对时，指定user_id进行比对。即人脸认证功能
        private String user_id;
        //查找后返回的用户数量。返回相似度最高的几个用户，默认为1，最多返回50个
        private String max_user_num;
        //人脸检测排序类型：0:代表检测出的人脸按照人脸面积从大到小排列；1:代表检测出的人脸按照距离图片中心从近到远排列；默认为0
        private String face_sort_type;
        //自定义参数，比对相似度
        private Double similarity;
    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class FaceDetectResponse extends BaseResult {
        private Result result;
        //人脸区域图片的base64（自定义）
        private String faceArea;

        @Data
        @NoArgsConstructor
        public static class Result {
            //人脸搜索接口主要获取人脸数
            private Integer face_num;
            //详情结果
            private List<FaceList> face_list;

            @Data
            @NoArgsConstructor
            public static class FaceList {
                //人脸图片的唯一标识 （人脸检测face_token有效期为60min）
                private String face_token;
                //人脸置信度，范围【0~1】，代表这是一张人脸的概率，0最小、1最大。其中返回0或1时，数据类型为Integer
                private Double face_probability;
                //年龄 ，当face_field包含age时返回
                private Integer age;
                //美丑，范围0-100，当face_fields包含beauty时返回
                private Double beauty;
                //判断图片是否为合成图，当ace_fields包含spoofing时返回
                private Double spoofing;
                //人脸在图片中的位置
                private Location location;
                //人脸旋转角度参数
                private Angle angle;
                //人脸质量信息。face_field包含quality时返回
                private Quality quality;
                //表情，当 face_field包含expression时返回
                private Expression expression;
                //脸型，当face_field包含face_shape时返回
                private FaceShape face_shape;
                //性别，face_field包含gender时返回
                private Gender gender;
                //是否带眼镜，face_field包含glasses时返回
                private Glasses glasses;
                //双眼状态（睁开/闭合） face_field包含eye_status时返回
                private EyeStatus eye_status;
                //情绪 face_field包含emotion时返回
                private Emotion emotion;
                //真实人脸/卡通人脸 face_field包含face_type时返回
                private FaceType face_type;
                //口罩识别 face_field包含mask时返回
                private Mask mask;
                //人种 face_field包含race时返回
                private Race race;
                //4个关键点位置，左眼中心、右眼中心、鼻尖、嘴中心。face_field包含landmark时返回
                private List<Landmark> landmark;
                //72个特征点位置 face_field包含landmark72时返回
                private List<Landmark72> landmark72;
                //150个特征点位置 face_field包含landmark150时返回
                private Landmark150 landmark150;
            }

            @Data
            @NoArgsConstructor
            public static class Location {
                //人脸区域离左边界的距离
                private Double left;
                //人脸区域离上边界的距离
                private Double top;
                //人脸区域的宽度
                private Integer width;
                //人脸区域的高度
                private Integer height;
                //人脸框相对于竖直方向的顺时针旋转角，[-180,180]
                private Integer rotation;
            }


            @Data
            @NoArgsConstructor
            public static class Angle {
                //三维旋转之左右旋转角[-90(左), 90(右)]
                private Double yaw;
                //三维旋转之俯仰角度[-90(上), 90(下)]
                private Double pitch;
                //平面内旋转角[-180(逆时针), 180(顺时针)]
                private Double roll;
            }

            @Data
            @NoArgsConstructor
            public static class Quality {
                //人脸各部分遮挡的概率，范围[0~1]，0表示完整，1表示不完整
                private Occlusion occlusion;
                //人脸模糊程度，范围[0~1]，0表示清晰，1表示模糊
                private Integer blur;
                //取值范围在[0~255], 表示脸部区域的光照程度 越大表示光照越好
                private Integer illumination;
                //人脸完整度，0或1, 0为人脸溢出图像边界，1为人脸都在图像边界内
                private Integer completeness;

                @Data
                @NoArgsConstructor
                public static class Occlusion {
                    //左眼遮挡比例，[0-1] ，1表示完全遮挡
                    private Double left_eye;
                    //右眼遮挡比例，[0-1] ， 1表示完全遮挡
                    private Integer right_eye;
                    //鼻子遮挡比例，[0-1] ， 1表示完全遮挡
                    private Double nose;
                    //嘴巴遮挡比例，[0-1] ， 1表示完全遮挡
                    private Integer mouth;
                    //左脸颊遮挡比例，[0-1] ， 1表示完全遮挡
                    private Double left_cheek;
                    //右脸颊遮挡比例，[0-1] ， 1表示完全遮挡
                    private Double right_cheek;
                    //下巴遮挡比例，，[0-1] ， 1表示完全遮挡
                    private Double chin_contour;
                }
            }

            @Data
            @NoArgsConstructor
            public static class Expression {
                //none:不笑；smile:微笑；laugh:大笑
                private String type;
                //表情置信度，范围【0~1】，0最小、1最大。
                private Integer probability;
            }

            @Data
            @NoArgsConstructor
            public static class FaceShape {
                //square: 正方形 triangle:三角形 oval: 椭圆 heart: 心形 round: 圆形
                private String type;
                //置信度，范围【0~1】，代表这是人脸形状判断正确的概率，0最小、1最大。
                private Double probability;
            }

            @Data
            @NoArgsConstructor
            public static class Gender {
                //male:男性 female:女性
                private String type;
                //性别置信度，范围【0~1】，0代表概率最小、1代表最大。
                private Integer probability;
            }

            @Data
            @NoArgsConstructor
            public static class Glasses {
                //none:无眼镜，common:普通眼镜，sun:墨镜
                private String type;
                //眼镜置信度，范围【0~1】，0代表概率最小、1代表最大。
                private Integer probability;
            }

            @Data
            @NoArgsConstructor
            public static class EyeStatus {
                //左眼状态 [0,1]取值，越接近0闭合的可能性越大
                private Double left_eye;
                //右眼状态 [0,1]取值，越接近0闭合的可能性越大
                private Integer right_eye;
            }

            @Data
            @NoArgsConstructor
            public static class Emotion {
                //angry:愤怒 disgust:厌恶 fear:恐惧 happy:高兴 sad:伤心 surprise:惊讶 neutral:无表情 pouty: 撅嘴 grimace:鬼脸
                private String type;
                //情绪置信度，范围0~1
                private Double probability;
            }

            @Data
            @NoArgsConstructor
            public static class FaceType {
                //human: 真实人脸 cartoon: 卡通人脸
                private String type;
                //人脸类型判断正确的置信度，范围【0~1】，0代表概率最小、1代表最大。
                private Double probability;
            }

            @Data
            @NoArgsConstructor
            public static class Mask {
                //没戴口罩/戴口罩 取值0或1 0代表没戴口罩 1 代表戴口罩
                private Integer type;
                //置信度，范围0~1
                private Double probability;
            }

            @Data
            @NoArgsConstructor
            public static class Race {
                //人种置信度，范围【0~1】，0代表概率最小、1代表最大。
                private Double probability;
                //yellow: 黄种人 white: 白种人 black:黑种人 arabs: 阿拉伯人
                private String type;
            }

            @Data
            @NoArgsConstructor
            public static class Landmark {
                private Double x;
                private Double y;
            }

            @Data
            @NoArgsConstructor
            public static class Landmark72 {
                private Double x;
                private Double y;
            }

            @Data
            @NoArgsConstructor
            public static class Landmark150 {
                private Coord cheek_left_1;
                private Coord cheek_left_2;
                private Coord cheek_left_3;
                private Coord cheek_left_4;
                private Coord cheek_left_5;
                private Coord cheek_left_6;
                private Coord cheek_left_7;
                private Coord cheek_left_8;
                private Coord cheek_left_9;
                private Coord cheek_left_10;
                private Coord cheek_left_11;
                private Coord cheek_right_1;
                private Coord cheek_right_2;
                private Coord cheek_right_3;
                private Coord cheek_right_4;
                private Coord cheek_right_5;
                private Coord cheek_right_6;
                private Coord cheek_right_7;
                private Coord cheek_right_8;
                private Coord cheek_right_9;
                private Coord cheek_right_10;
                private Coord cheek_right_11;
                private Coord chin_1;
                private Coord chin_2;
                private Coord chin_3;
                private Coord eye_left_corner_left;
                private Coord eye_left_corner_right;
                private Coord eye_left_eyeball_center;
                private Coord eye_left_eyeball_left;
                private Coord eye_left_eyeball_right;
                private Coord eye_left_eyelid_lower_1;
                private Coord eye_left_eyelid_lower_2;
                private Coord eye_left_eyelid_lower_3;
                private Coord eye_left_eyelid_lower_4;
                private Coord eye_left_eyelid_lower_5;
                private Coord eye_left_eyelid_lower_6;
                private Coord eye_left_eyelid_lower_7;
                private Coord eye_left_eyelid_upper_1;
                private Coord eye_left_eyelid_upper_2;
                private Coord eye_left_eyelid_upper_3;
                private Coord eye_left_eyelid_upper_4;
                private Coord eye_left_eyelid_upper_5;
                private Coord eye_left_eyelid_upper_6;
                private Coord eye_left_eyelid_upper_7;
                private Coord eye_right_corner_left;
                private Coord eye_right_corner_right;
                private Coord eye_right_eyeball_center;
                private Coord eye_right_eyeball_left;
                private Coord eye_right_eyeball_right;
                private Coord eye_right_eyelid_lower_1;
                private Coord eye_right_eyelid_lower_2;
                private Coord eye_right_eyelid_lower_3;
                private Coord eye_right_eyelid_lower_4;
                private Coord eye_right_eyelid_lower_5;
                private Coord eye_right_eyelid_lower_6;
                private Coord eye_right_eyelid_lower_7;
                private Coord eye_right_eyelid_upper_1;
                private Coord eye_right_eyelid_upper_2;
                private Coord eye_right_eyelid_upper_3;
                private Coord eye_right_eyelid_upper_4;
                private Coord eye_right_eyelid_upper_5;
                private Coord eye_right_eyelid_upper_6;
                private Coord eye_right_eyelid_upper_7;
                private Coord eyebrow_left_corner_left;
                private Coord eyebrow_left_corner_right;
                private Coord eyebrow_left_lower_1;
                private Coord eyebrow_left_lower_2;
                private Coord eyebrow_left_lower_3;
                private Coord eyebrow_left_upper_1;
                private Coord eyebrow_left_upper_2;
                private Coord eyebrow_left_upper_3;
                private Coord eyebrow_left_upper_4;
                private Coord eyebrow_left_upper_5;
                private Coord eyebrow_right_corner_left;
                private Coord eyebrow_right_corner_right;
                private Coord eyebrow_right_lower_1;
                private Coord eyebrow_right_lower_2;
                private Coord eyebrow_right_lower_3;
                private Coord eyebrow_right_upper_1;
                private Coord eyebrow_right_upper_2;
                private Coord eyebrow_right_upper_3;
                private Coord eyebrow_right_upper_4;
                private Coord eyebrow_right_upper_5;
                private Coord mouth_corner_left_inner;
                private Coord mouth_corner_left_outer;
                private Coord mouth_corner_right_inner;
                private Coord mouth_corner_right_outer;
                private Coord mouth_lip_lower_inner_1;
                private Coord mouth_lip_lower_inner_2;
                private Coord mouth_lip_lower_inner_3;
                private Coord mouth_lip_lower_inner_4;
                private Coord mouth_lip_lower_inner_5;
                private Coord mouth_lip_lower_inner_6;
                private Coord mouth_lip_lower_inner_7;
                private Coord mouth_lip_lower_inner_8;
                private Coord mouth_lip_lower_inner_9;
                private Coord mouth_lip_lower_inner_10;
                private Coord mouth_lip_lower_inner_11;
                private Coord mouth_lip_lower_outer_1;
                private Coord mouth_lip_lower_outer_2;
                private Coord mouth_lip_lower_outer_3;
                private Coord mouth_lip_lower_outer_4;
                private Coord mouth_lip_lower_outer_5;
                private Coord mouth_lip_lower_outer_6;
                private Coord mouth_lip_lower_outer_7;
                private Coord mouth_lip_lower_outer_8;
                private Coord mouth_lip_lower_outer_9;
                private Coord mouth_lip_lower_outer_10;
                private Coord mouth_lip_lower_outer_11;
                private Coord mouth_lip_upper_inner_1;
                private Coord mouth_lip_upper_inner_2;
                private Coord mouth_lip_upper_inner_3;
                private Coord mouth_lip_upper_inner_4;
                private Coord mouth_lip_upper_inner_5;
                private Coord mouth_lip_upper_inner_6;
                private Coord mouth_lip_upper_inner_7;
                private Coord mouth_lip_upper_inner_8;
                private Coord mouth_lip_upper_inner_9;
                private Coord mouth_lip_upper_inner_10;
                private Coord mouth_lip_upper_inner_11;
                private Coord mouth_lip_upper_outer_1;
                private Coord mouth_lip_upper_outer_2;
                private Coord mouth_lip_upper_outer_3;
                private Coord mouth_lip_upper_outer_4;
                private Coord mouth_lip_upper_outer_5;
                private Coord mouth_lip_upper_outer_6;
                private Coord mouth_lip_upper_outer_7;
                private Coord mouth_lip_upper_outer_8;
                private Coord mouth_lip_upper_outer_9;
                private Coord mouth_lip_upper_outer_10;
                private Coord mouth_lip_upper_outer_11;
                private Coord nose_bridge_1;
                private Coord nose_bridge_2;
                private Coord nose_bridge_3;
                private Coord nose_left_contour_1;
                private Coord nose_left_contour_2;
                private Coord nose_left_contour_3;
                private Coord nose_left_contour_4;
                private Coord nose_left_contour_5;
                private Coord nose_left_contour_6;
                private Coord nose_left_contour_7;
                private Coord nose_middle_contour;
                private Coord nose_right_contour_1;
                private Coord nose_right_contour_2;
                private Coord nose_right_contour_3;
                private Coord nose_right_contour_4;
                private Coord nose_right_contour_5;
                private Coord nose_right_contour_6;
                private Coord nose_right_contour_7;
                private Coord nose_tip;

                @Data
                @NoArgsConstructor
                public static class Coord {
                    private Double x;
                    private Double y;
                }
            }
        }
    }

    @Data
    @NoArgsConstructor
    public static class FaceVerifyRequest {
        //图片信息(总数据大小应小于10M)，图片上传方式根据image_type来判断
        private String image;
        //图片类型：BASE64：图片的base64值，编码后的图片大小不超过2M；URL：图片的URL地址；FACE_TOKEN: 调用人脸检测接口时，会为每个人脸图片赋予一个唯一的FACE_TOKEN，同一张图片多次检测得到的FACE_TOKEN是同一个
        private String image_type;
        //包括age,beauty,expression,face_shape,gender,glasses,quality,face_type,spoofing,eye_status,emotion,mask,race,landmark信息，逗号分隔，默认只返回face_token、活体数、人脸框、概率和旋转角度
        private String face_field;
        //场景信息，程序会视不同的场景选用相对应的模型。当前支持的场景有COMMON(通用场景)，GATE(闸机场景)，默认使用COMMON
        private String option;
    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class FaceVerifyResponse extends BaseResult {
        private Result result;

        @Data
        @NoArgsConstructor
        public static class Result {
            //活体分数值
            private float face_liveness;
            //由服务端返回最新的阈值数据（随着模型的优化，阈值可能会变化），将此参数与返回的face_liveness进行比较，可以作为活体判断的依据，误识率越低，准确率越高，相应的拒绝率也越高
            private Thresholds thresholds;
            //每张图片的详细信息描述，如果只上传一张图片，则只返回一个结果。
            private List<FaceList> face_list;

            @Data
            @NoArgsConstructor
            public static class Thresholds {
                //frr_1e-2：百分之一误识率的阈值
                @SerializedName("frr_1e-2")
                private Double frr_1e_2;
                //frr_1e-3：千分之一误识率的阈值
                @SerializedName("frr_1e-3")
                private Double frr_1e_3;
                //frr_1e-4：万分之一误识率的阈值
                @SerializedName("frr_1e-4")
                private Double frr_1e_4;
            }

            @Data
            @NoArgsConstructor
            public static class FaceList {
                //活性度
                private Liveness liveness;
                //人脸图片的唯一标识 （人脸检测face_token有效期为60min）
                private String face_token;
                //人脸置信度，范围【0~1】，代表这是一张人脸的概率，0最小、1最大。其中返回0或1时，数据类型为Integer
                private Double face_probability;
                //年龄 ，当face_field包含age时返回
                private Integer age;
                //美丑，范围0-100，当face_fields包含beauty时返回
                private Double beauty;
                //判断图片是否为合成图，当ace_fields包含spoofing时返回
                private Double spoofing;
                //人脸在图片中的位置
                private FaceDetectResponse.Result.Location location;
                //人脸旋转角度参数
                private FaceDetectResponse.Result.Angle angle;
                //人脸质量信息。face_field包含quality时返回
                private FaceDetectResponse.Result.Quality quality;
                //表情，当 face_field包含expression时返回
                private FaceDetectResponse.Result.Expression expression;
                //脸型，当face_field包含face_shape时返回
                private FaceDetectResponse.Result.FaceShape face_shape;
                //性别，face_field包含gender时返回
                private FaceDetectResponse.Result.Gender gender;
                //是否带眼镜，face_field包含glasses时返回
                private FaceDetectResponse.Result.Glasses glasses;
                //双眼状态（睁开/闭合） face_field包含eye_status时返回
                private FaceDetectResponse.Result.EyeStatus eye_status;
                //情绪 face_field包含emotion时返回
                private FaceDetectResponse.Result.Emotion emotion;
                //真实人脸/卡通人脸 face_field包含face_type时返回
                private FaceDetectResponse.Result.FaceType face_type;
                //口罩识别 face_field包含mask时返回
                private FaceDetectResponse.Result.Mask mask;
                //人种 face_field包含race时返回
                private FaceDetectResponse.Result.Race race;
                //4个关键点位置，左眼中心、右眼中心、鼻尖、嘴中心。face_field包含landmark时返回
                private List<FaceDetectResponse.Result.Landmark> landmark;
                //72个特征点位置 face_field包含landmark时返回
                private List<FaceDetectResponse.Result.Landmark72> landmark72;

                @Data
                @NoArgsConstructor
                public static class Liveness {
                    //活性度分值
                    private Double livemapscore;
                }
            }
        }
    }

    @Data
    @NoArgsConstructor
    public static class OcrIdCardRequest {
        //图片类型，base64或者url
        @NotBlank(message = "图片类型不能为空")
        private String imageType;
        //图像数据，base64编码后进行urlencode，要求base64编码和urlencode后大小不超过4M，最短边至少15px，最长边最大4096px,支持jpg/jpeg/png/bmp格式
        @NotBlank(message = "图片信息不能为空")
        private String image;
        //图片完整URL，URL长度不超过1024字节，URL对应的图片base64编码后大小不超过4M，最短边至少15px，最长边最大4096px,支持jpg/jpeg/png/bmp格式，当image字段存在时url字段失效
        private String url;
        //front：人像面，back：国徽面，如果传参指定方向与图片相反，支持正常识别，返回参数image_status字段为reversed_side
        private String id_card_side = "front";
        //是否开启身份证风险类型(身份证复印件、临时身份证、身份证翻拍、修改过的身份证)功能，默认不开启，即：false。
        private Boolean detect_risk = false;
        //是否检测头像内容，默认不检测。可选值：true-检测头像并返回头像的 base64 编码及位置信息
        private Boolean detect_photo = false;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Data
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class OcrIdCardResponse extends BaseResult {
        private WordsResult words_result;
        private String risk_type;
        private Integer words_result_num;
        private Location photoLocation;
        private Integer idcard_number_type;
        private String image_status;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Data
        @NoArgsConstructor
        public static class WordsResult {
            @JSONField(name = "姓名")
            private Words name;
            @JSONField(name = "民族")
            private Words nation;
            @JSONField(name = "住址")
            private Words address;
            @JSONField(name = "公民身份号码")
            private Words idCard;
            @JSONField(name = "出生")
            private Words birth;
            @JSONField(name = "性别")
            private Words gender;
            @JSONField(name = "失效日期")
            private Words expiryDate;
            @JSONField(name = "签发机关")
            private Words issueOrgan;
            @JSONField(name = "签发日期")
            private Words issueDate;

            @Data
            @NoArgsConstructor
            public static class Words {
                private String words;
                private Location location;
            }
        }

        @Data
        @NoArgsConstructor
        public static class Location {
            private int top;
            private int left;
            private int width;
            private int height;
        }
    }
}