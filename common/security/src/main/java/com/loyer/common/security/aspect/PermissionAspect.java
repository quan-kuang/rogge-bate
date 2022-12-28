package com.loyer.common.security.aspect;

import com.loyer.common.core.entity.BaseEntity;
import com.loyer.common.core.entity.User;
import com.loyer.common.security.enums.PermissionType;
import com.loyer.common.security.utils.SecurityUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 数据权限切面
 *
 * @author kuangq
 * @date 2020-07-22 10:54
 */
@Aspect
@Component
public class PermissionAspect {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 获取用户权限过滤条件
     *
     * @author kuangq
     * @date 2020-09-17 22:39
     */
    public static Map<String, Object> getPermissionFilter(User user) {
        Map<String, Object> params = new HashMap<>(8);
        //设置用户ID
        params.put("userId", user.getUuid());
        //设置用户名称
        params.put("userName", user.getName());
        //设置用户部门ID
        params.put("userDeptId", user.getDeptId());
        //存入管理员标示
        params.put("isAdmin", user.checkIsAdmin());
        //管理员权限无需设置权限过滤SQL
        if (user.checkIsAdmin()) {
            return params;
        }
        //设置用户过滤SQL（存入自身及其下级创建的用户ID）
        String inUserIds = "(WITH RECURSIVE temp AS (SELECT a.uuid, a.creator_id FROM users a WHERE a.creator_id = '%s' UNION ALL SELECT b.uuid, b.creator_id FROM users b INNER JOIN temp c ON b.creator_id = c.uuid) SELECT uuid FROM temp UNION SELECT '%s')";
        inUserIds = String.format(inUserIds, user.getUuid(), user.getUuid());
        params.put("inUserIds", inUserIds);
        //设置角色过滤SQL
        params.put("inRoleIds", createInSql(new HashSet<>(user.getRoleIds())));
        //设置数据权限范围
        Integer permissionScope = user.getPermissionScope();
        params.put("permissionScope", permissionScope);
        //仅本人数据权限
        if (PermissionType.ONLY_SELF.getValue().equals(permissionScope.toString())) {
            params.put("scopeSql", String.format("'%s'", user.getUuid()));
        }
        //拥有部分权限
        else if (!PermissionType.ALL.getValue().equals(permissionScope.toString())) {
            params.put("scopeSql", createInSql(user.getPermissionDeptSet()));
        }
        return params;
    }

    /**
     * 创建inSql语句
     *
     * @author kuangq
     * @date 2020-09-17 22:18
     */
    private static String createInSql(Set<String> stringSet) {
        String inSql = "";
        if (stringSet.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder("(");
            for (String str : stringSet) {
                stringBuilder.append("'").append(str).append("', ");
            }
            inSql = StringUtils.substringBeforeLast(stringBuilder.toString(), ", ") + ")";
        }
        return inSql;
    }

    /**
     * 根据PermissionAnnotation注解编织切入点
     *
     * @author kuangq
     * @date 2020-07-22 10:54
     */
    @Pointcut("@annotation(com.loyer.common.security.annotation.PermissionAnnotation)")
    private void permissionPointcut() {
    }

    /**
     * 请求执行之前
     *
     * @author kuangq
     * @date 2020-07-22 10:54
     */
    @Before("permissionPointcut()")
    private void doBefore(JoinPoint joinPoint) {
        //获取用户信息
        User user = SecurityUtil.getLoginUser().getUser();
        //获取实体类基类
        BaseEntity baseEntity = joinPoint.getArgs().length == 0 ? new BaseEntity() : (BaseEntity) joinPoint.getArgs()[0];
        Map<String, Object> params = baseEntity.getParams() == null ? new HashMap<>(8) : baseEntity.getParams();
        //设置权限过滤条件
        params.putAll(getPermissionFilter(user));
        baseEntity.setParams(params);
        logger.info("【isAdmin】{}、【数据权限】{}", user.checkIsAdmin(), params);
    }
}