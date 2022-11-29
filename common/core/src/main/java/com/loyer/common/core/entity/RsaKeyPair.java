package com.loyer.common.core.entity;

import com.loyer.common.dedicine.utils.Base64Util;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.KeyPair;

/**
 * 密匙对
 *
 * @author kuangq
 * @date 2021-11-12 16:04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("RAS密匙对")
public class RsaKeyPair {

    @ApiModelProperty("公钥用于加密")
    private String publicKey;

    @ApiModelProperty("私钥用于解密")
    private String privateKey;

    public RsaKeyPair(KeyPair keyPair) {
        this.publicKey = Base64Util.encode(keyPair.getPublic().getEncoded());
        this.privateKey = Base64Util.encode(keyPair.getPrivate().getEncoded());
    }
}