package com.loyer.modules.system.service.impl;

import com.loyer.common.datasource.annotation.DataSourceAnnotation;
import com.loyer.common.datasource.entity.PageParams;
import com.loyer.common.datasource.entity.PageResult;
import com.loyer.common.datasource.utils.EntityManagerUtil;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.modules.system.entity.OiMemRegInfo;
import com.loyer.modules.system.mapper.repository.OiMemRegInfoJpa;
import com.loyer.modules.system.service.JpaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;

/**
 * TODO
 *
 * @author kuangq
 * @date 2019-11-05 17:05
 */
@Service
public class JpaServiceImpl implements JpaService {

    @Resource
    private EntityManagerUtil entityManagerUtil;

    @Resource
    private OiMemRegInfoJpa oiMemRegInfoJpa;

    /**
     * 身份证解密处理
     *
     * @author kuangq
     * @date 2019-11-15 14:54
     */
    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class)
    @DataSourceAnnotation(isToggle = false)
    @Override
    public ApiResult decryptCard(Map<String, Object> params) {
        String sql = "SELECT guid, real_name, id_num FROM oi_mem_reg_info";
        PageParams pageParams = new PageParams(1, 5, OiMemRegInfo.class);
        PageResult<OiMemRegInfo> oiMemRegInfoPageResult = entityManagerUtil.queryPageInfo(sql, pageParams);
        int index = oiMemRegInfoPageResult.getList().size();
        for (OiMemRegInfo oiMemRegInfo : oiMemRegInfoPageResult.getList()) {
            if (params != null && !params.isEmpty()) {
                oiMemRegInfo.setIdNum(params.values().stream().findFirst().orElse("").toString());
            }
            String updateSql = "UPDATE oi_mem_reg_info SET id_num = :idNum WHERE guid = :guid";
            System.out.printf("更新第%s条数据：%s %n", index, entityManagerUtil.dmlHandle(updateSql, oiMemRegInfo) > 0);
            index--;
        }
        return ApiResult.success();
    }

    /**
     * 根据账户查询用户信息
     *
     * @author kuangq
     * @date 2019-11-15 17:00
     */
    @DataSourceAnnotation(isToggle = false)
    @Override
    public ApiResult findByAcountId(String acountId) {
        return ApiResult.success(oiMemRegInfoJpa.findByAcountId(acountId));
    }

    /**
     * 根据姓名模糊查询用户信息
     *
     * @author kuangq
     * @date 2019-11-15 17:01
     */
    @DataSourceAnnotation(isToggle = false)
    @Override
    public ApiResult findByRealNameLike(String realName) {
        return ApiResult.success(oiMemRegInfoJpa.findByRealNameLike("%" + realName + "%"));
    }
}