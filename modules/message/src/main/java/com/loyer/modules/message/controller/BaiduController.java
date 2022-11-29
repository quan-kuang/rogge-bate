package com.loyer.modules.message.controller;

import com.loyer.common.core.annotation.OperateLogAnnotation;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.modules.message.entity.BaiduEntity;
import com.loyer.modules.message.utils.VeriFaceUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 百度模块
 *
 * @author kuangq
 * @date 2020-10-22 20:32
 */
@Api(tags = "百度模块")
@RestController
@RequestMapping("baidu")
public class BaiduController {

    private static final String FACE_FIELD = "age,beauty,expression,face_shape,gender,glasses,quality,face_type,spoofing,eye_status,emotion,mask,race";

    @OperateLogAnnotation
    @ApiOperation("人脸检测")
    @PostMapping("faceDetect")
    public ApiResult faceDetect(@RequestParam String image, @RequestParam(defaultValue = "URL") String imageType, @RequestParam(defaultValue = FACE_FIELD) String faceField) {
        return VeriFaceUtil.faceDetect(image, imageType, faceField);
    }

    @OperateLogAnnotation
    @ApiOperation("活体检测")
    @PostMapping("faceVerify")
    public ApiResult faceVerify(@RequestParam String image, @RequestParam(defaultValue = "URL") String imageType, @RequestParam(defaultValue = FACE_FIELD) String faceField) {
        return VeriFaceUtil.faceVerify(image, imageType, faceField);
    }

    @OperateLogAnnotation
    @ApiOperation("身份证识别")
    @PostMapping("ocrIdCard")
    public ApiResult ocrIdCard(@Validated @RequestBody BaiduEntity.OcrIdCardRequest ocrIdCardRequest) {
        return VeriFaceUtil.ocrIdCard(ocrIdCardRequest);
    }
}