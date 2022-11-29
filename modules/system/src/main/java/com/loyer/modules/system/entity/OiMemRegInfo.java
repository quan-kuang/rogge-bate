package com.loyer.modules.system.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * JPA用户
 *
 * @author kuangq
 * @date 2019-11-15 15:05
 */
@Entity
@Getter
@Setter
public class OiMemRegInfo {
    @Id
    private String guid;
    private String acountId;
    private String password;
    private String realName;
    private String idNum;
    private Timestamp birthday;
    private String gender;
    private String province;
    private String city;
    private String area;
    private String address;
    private String telephone;
    private String email;
    private String wxOpenId;
    private String postcode;
    private String avatar;
    private String nation;
    private String placeOrigin;
    private String qq;
    private String authStatus;
    private Timestamp authTime;
    private String source;
    private String trpId;
    private String regIp;
    private Timestamp createTime;
    private Timestamp updateTime;
    private String updater;
    private BigInteger age;
    private BigInteger isdeleted;
    private Timestamp synctime;
    private BigInteger seqNum;
    private Timestamp cT;
    private Timestamp uT;
    private String syncId;
    private String zfbId;
}