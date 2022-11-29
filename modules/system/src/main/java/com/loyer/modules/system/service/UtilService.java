package com.loyer.modules.system.service;

import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.modules.system.entity.TableExplain;

import javax.servlet.http.HttpServletResponse;

/**
 * 工具Service
 *
 * @author kuangq
 * @date 2020-06-25 18:26
 */
public interface UtilService {

    ApiResult putVue(String projectName);

    ApiResult releaseVue(String projectName);

    ApiResult selectSchemaName();

    ApiResult selectTableExplain(TableExplain tableExplain);

    void generateCode(HttpServletResponse httpServletResponse, int[] tableOids);
}