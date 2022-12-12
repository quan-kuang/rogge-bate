package com.loyer.modules.system.service.impl;

import com.loyer.common.core.constant.PrefixConst;
import com.loyer.common.core.entity.Captcha;
import com.loyer.common.core.entity.Menu;
import com.loyer.common.core.entity.Role;
import com.loyer.common.core.entity.User;
import com.loyer.common.core.utils.common.ParamsUtil;
import com.loyer.common.core.utils.encrypt.RsaUtil;
import com.loyer.common.core.utils.reflect.ContextUtil;
import com.loyer.common.core.utils.request.CookieUtil;
import com.loyer.common.datasource.entity.PageResult;
import com.loyer.common.datasource.utils.PageHelperUtil;
import com.loyer.common.dedicine.constant.SystemConst;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.dedicine.enums.HintEnum;
import com.loyer.common.dedicine.exception.BusinessException;
import com.loyer.common.dedicine.utils.GeneralUtil;
import com.loyer.common.redis.utils.CacheUtil;
import com.loyer.common.security.annotation.PermissionAnnotation;
import com.loyer.common.security.aspect.PermissionAspect;
import com.loyer.common.security.entity.LoginUser;
import com.loyer.common.security.enums.PermissionType;
import com.loyer.common.security.service.UserDetailsServiceImpl;
import com.loyer.common.security.utils.SecurityUtil;
import com.loyer.modules.system.enums.CaptchaType;
import com.loyer.modules.system.mapper.postgresql.DeptMapper;
import com.loyer.modules.system.mapper.postgresql.MenuMapper;
import com.loyer.modules.system.mapper.postgresql.RoleMapper;
import com.loyer.modules.system.mapper.postgresql.UserMapper;
import com.loyer.modules.system.service.OnLineUserService;
import com.loyer.modules.system.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;

/**
 * 用户ServiceImpl
 *
 * @author kuangq
 * @date 2020-05-13 10:04
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private DeptMapper deptMapper;

    @Resource
    private MenuMapper menuMapper;

    @Resource
    private OnLineUserService onLineUserService;

    @Resource
    private UserDetailsServiceImpl userDetailsService;

    /**
     * 登录
     *
     * @author kuangq
     * @date 2020-05-15 15:39
     */
    @Override
    public ApiResult login(LoginUser loginUser) {
        try {
            //验证码校验
            checkCaptcha(loginUser.getCaptcha());
            //RSA解密前端传入密码
            String password = RsaUtil.decrypt(loginUser.getPassword());
            //获取用户信息并做登录校验
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser.getUsername(), password);
            //密码认证
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            //获取用户对象
            loginUser = (LoginUser) authentication.getPrincipal();
            //登录设置
            return ApiResult.success(loginAfter(loginUser));
        } catch (Exception e) {
            //判断异常类型
            Throwable cause = e.getCause();
            if (e instanceof BadCredentialsException) {
                return ApiResult.hintEnum(HintEnum.HINT_1043, e.getMessage());
            } else if (cause instanceof BusinessException) {
                throw (BusinessException) cause;
            }
            throw e;
        }
    }

    /**
     * 登录成功后的数据赋值
     *
     * @author kuangq
     * @date 2021-06-15 15:37
     */
    private LoginUser loginAfter(LoginUser loginUser) {
        User user = loginUser.getUser();
        //更新登录时间
        user.setLoginTime(new Timestamp(System.currentTimeMillis()));
        //通过代理异步执行
        ContextUtil.getBean(getClass()).saveLoginTime(userMapper, user);
        //设置用户数据权限
        setPermission(user);
        //设置用户权限菜单
        setMenuList(user);
        //创建登录用户的认证凭据
        String token = SecurityUtil.createToken(loginUser);
        //异步存储登录信息
        onLineUserService.asyncSaveOnLineUser(CookieUtil.getHttpServletRequest(), token, user);
        return loginUser;
    }

    /**
     * 短信登录
     *
     * @author kuangq
     * @date 2021-06-15 15:37
     */
    @Override
    public ApiResult messageLogin(User user) {
        //根据手机号查询用户信息
        User result = userDetailsService.selectUser("phone", user.getAccount());
        //校验验证码失效
        String key = PrefixConst.CAPTCHA + user.getAccount();
        if (!CacheUtil.KEY.has(key)) {
            throw new BusinessException(HintEnum.HINT_1086);
        }
        //校验短信验证码
        if (!CacheUtil.STRING.get(key).equals(user.getPassword())) {
            throw new BusinessException(HintEnum.HINT_1087);
        }
        //删除缓存
        CacheUtil.KEY.delete(key);
        //登录设置
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(result);
        return ApiResult.success(loginAfter(loginUser));
    }

    /**
     * 校验验证码
     *
     * @author kuangq
     * @date 2020-11-14 14:29
     */
    private void checkCaptcha(Captcha captcha) {
        //特殊处理，绕过后台验证码校验
        if (SystemConst.LOYER.equals(captcha.getNonceStr())) {
            return;
        }
        //校验验证码失效
        String key = PrefixConst.CAPTCHA + captcha.getNonceStr();
        if (!CacheUtil.KEY.has(key)) {
            throw new BusinessException(HintEnum.HINT_1086);
        }
        //文字验证
        if (CaptchaType.CODE.value().equals(captcha.getType())) {
            //获取缓存验证码
            String value = CacheUtil.STRING.get(key);
            //比对输入验证码是否一致（不区分大小写）
            if (!value.equalsIgnoreCase(captcha.getValue())) {
                throw new BusinessException(HintEnum.HINT_1087);
            }
        }
        //拼图验证
        else if (CaptchaType.PUZZLE.value().equals(captcha.getType())) {
            //获取缓存验证码
            Integer value = CacheUtil.STRING.get(key);
            //根据移动距离判断验证是否成功
            if (Math.abs(value - Integer.parseInt(captcha.getValue())) > SystemConst.ALLOW_DEVIATION) {
                throw new BusinessException(HintEnum.HINT_1087);
            }
        }
        //删除缓存
        CacheUtil.KEY.delete(key);
    }

    /**
     * 保存登录时间，内部方法调用需要通过代理，并该方法属于public
     *
     * @author kuangq
     * @date 2020-09-16 12:36
     */
    @Async
    public void saveLoginTime(UserMapper userMapper, User user) {
        userMapper.updateUser(user);
    }

    /**
     * 设置用户相关权限菜单
     *
     * @author kuangq
     * @date 2020-10-16 10:54
     */
    private void setMenuList(User user) {
        Menu menu = new Menu();
        menu.setParams(PermissionAspect.getPermissionFilter(user));
        user.setMenuList(menuMapper.selectMenu(menu));
    }

    /**
     * 设置用户数据权限
     *
     * @author kuangq
     * @date 2020-10-16 10:56
     */
    private void setPermission(User user) {
        //获取用户全部角色
        user.setRoleList(roleMapper.selectRoleByUserId(user.getUuid()));
        //定义所属角色ID组
        List<String> roleIds = new ArrayList<>();
        //定义自定义权限部门的角色ID组
        List<String> customDeptIds = new ArrayList<>();
        //定义拥有的角色数据权限范围
        Set<String> permissionTypes = new HashSet<>();
        //定义拥有数据权限的部门ID组
        Set<String> permissionDeptSet = new HashSet<>();
        //遍历处理正常使用的角色
        for (Role role : user.getRoleList()) {
            if (role.getStatus()) {
                String roleId = role.getUuid();
                roleIds.add(roleId);
                String permissionType = role.getPermissionType();
                permissionTypes.add(permissionType);
                if (PermissionType.CUSTOM.getValue().equals(permissionType)) {
                    customDeptIds.add(roleId);
                }
            }
        }
        //设置用户角色组
        user.setRoleIds(roleIds);
        //拥有全部数据权限（无需查询相关部门）
        if (permissionTypes.contains(PermissionType.ALL.getValue())) {
            user.setPermissionScope(Integer.parseInt(PermissionType.ALL.getValue()));
            return;
        }
        //只拥有本人数据权限（无需查询相关部门）
        if (permissionTypes.contains(PermissionType.ONLY_SELF.getValue()) && permissionTypes.size() == 1) {
            user.setPermissionScope(Integer.parseInt(PermissionType.ONLY_SELF.getValue()));
            return;
        }
        //设置自身所属部门权限
        permissionDeptSet.add(user.getDeptId());
        //排序后取出最大权限
        //noinspection ResultOfMethodCallIgnored
        permissionTypes.stream().sorted(Comparator.reverseOrder());
        user.setPermissionScope(Integer.parseInt(permissionTypes.iterator().next()));
        //拥有部门及部门以下的数据权限（根据自身所属部门级联查询下级部门）
        if (permissionTypes.contains((PermissionType.DEPT_AND_BELOW.getValue()))) {
            permissionDeptSet.addAll(deptMapper.selectCascade(user.getDeptId()));
        }
        //拥有自定义部门的数据权限（从permission表中关联查询权限部门）
        if (permissionTypes.contains((PermissionType.CUSTOM.getValue())) && customDeptIds.size() > 0) {
            permissionDeptSet.addAll(roleMapper.selectRoleLink(ParamsUtil.listToArray(customDeptIds), "dept"));
        }
        //设置用户数据权限
        user.setPermissionDeptSet(permissionDeptSet);
    }

    /**
     * 保存用户信息
     *
     * @author kuangq
     * @date 2020-06-09 9:43
     */
    @Override
    @PermissionAnnotation
    @Transactional(transactionManager = "dataSourceTransactionManager", rollbackFor = Exception.class)
    public ApiResult saveUser(User user) {
        //新增用户添加uuid
        if (StringUtils.isBlank(user.getUuid())) {
            user.setUuid(GeneralUtil.getUuid());
            //RSA解密前端传入密码
            String password = RsaUtil.decrypt(user.getPassword());
            //做BCryptPassword加密
            user.setPassword(SecurityUtil.encrypt(password));
        } else {
            //uuid不为空则为修改，先清空角色关联表
            userMapper.deleteUserLink(user.getUuid());
        }
        if (user.getRoleIds().size() > 0) {
            //插入角色信息
            userMapper.saveUserLink(user);
        }
        //保存用户信息
        return ApiResult.success(userMapper.saveUser(user));
    }

    /**
     * 查询用户信息
     *
     * @author kuangq
     * @date 2020-06-09 9:41
     */
    @Override
    @PermissionAnnotation
    public ApiResult selectUser(User user) {
        //判断是否为分页查询
        if (PageHelperUtil.isPaging(user)) {
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            PageResult<User> pageResult = PageHelperUtil.pagingQuery(userMapper, methodName, user);
            setRoleIds(pageResult.getList());
            return ApiResult.success(pageResult);
        } else {
            List<User> userList = userMapper.selectUser(user);
            //设置用户列表的角色ID
            setRoleIds(userList);
            return ApiResult.success(userList);
        }
    }

    /**
     * 查询用户列表的角色ID
     *
     * @author kuangq
     * @date 2020-10-16 11:14
     */
    private void setRoleIds(List<User> userList) {
        for (User user : userList) {
            user.setRoleIds(userMapper.selectUserLink(user.getUuid()));
        }
    }

    /**
     * 删除用户信息
     *
     * @author kuangq
     * @date 2020-06-09 9:41
     */
    @Override
    @Transactional(transactionManager = "dataSourceTransactionManager", rollbackFor = Exception.class)
    public ApiResult deleteUser(String... uuids) {
        for (String uuid : uuids) {
            //禁止操作管理员账号
            if (SystemConst.ADMIN.equals(uuid)) {
                return ApiResult.hintEnum(HintEnum.HINT_1051, uuid);
            }
            //删除角色关联菜单表
            userMapper.deleteUserLink(uuid);
        }
        return ApiResult.success(userMapper.deleteUser(uuids));
    }

    /**
     * 修改用户信息
     *
     * @author kuangq
     * @date 2020-07-30 13:08
     */
    @Override
    public ApiResult updateUser(User user) {
        //禁止操作管理员账号
        if (SystemConst.ADMIN.equals(user.getUuid())) {
            return ApiResult.hintEnum(HintEnum.HINT_1051, user.getUuid());
        }
        //密码校验
        Boolean flag = checkPassword(user);
        if (flag != null && !flag) {
            return ApiResult.hintEnum(HintEnum.HINT_1082);
        }
        //做BCryptPassword加密
        if (StringUtils.isNotBlank(user.getPassword())) {
            user.setPassword(SecurityUtil.encrypt(RsaUtil.decrypt(user.getPassword())));
        }
        return ApiResult.success(userMapper.updateUser(user));
    }

    /**
     * 校验密码
     *
     * @author kuangq
     * @date 2020-10-18 23:17
     */
    private Boolean checkPassword(User user) {
        //前端传入旧密码的KEY值
        String oldPasswordKey = "oldPassword";
        //判断为本人操作
        User operator = SecurityUtil.getLoginUser().getUser();
        if (!operator.getUuid().equals(user.getUuid())) {
            return null;
        }
        //根据附件参数判断是否进行密码修改操作
        Map<String, Object> params = user.getParams();
        if (params == null || !params.containsKey(oldPasswordKey)) {
            return null;
        }
        //解密旧密码
        String oldPassword = RsaUtil.decrypt(params.get(oldPasswordKey).toString());
        return SecurityUtil.compare(operator.getPassword(), oldPassword);
    }

    /**
     * 精确查询单条用户信息
     *
     * @author kuangq
     * @date 2020-09-16 14:31
     */
    @Override
    public ApiResult selectUserById(String uuid) {
        User user = new User();
        user.setUuid(uuid);
        return ApiResult.success(selectUserNew(user));
    }

    /**
     * 精确查询单条用户信息
     *
     * @author kuangq
     * @date 2020-06-27 21:25
     */
    @Override
    public ApiResult selectUserBy(User user) {
        user = selectUserNew(user);
        SecurityUtil.refresh(user);
        return ApiResult.success(user);
    }

    /**
     * 获取最新用户信息
     *
     * @author kuangq
     * @date 2020-09-16 14:40
     */
    private User selectUserNew(User user) {
        //根据UUID查询用户信息
        List<User> userList = userMapper.selectUser(user);
        if (userList == null || userList.size() == 0) {
            throw new BusinessException(HintEnum.HINT_1041);
        }
        user = userList.get(0);
        //根据角色获取用户数据权限
        setPermission(user);
        //查询相关权限菜单
        setMenuList(user);
        return user;
    }

    /**
     * 校验用户是否存在
     *
     * @author kuangq
     * @date 2020-07-30 14:17
     */
    @Override
    public ApiResult checkUserExists(String account) {
        User user = new User();
        user.setAccount(account);
        List<User> userList = userMapper.selectUser(user);
        return ApiResult.success(userList != null && userList.size() > 0);
    }

    /**
     * 实人认证
     *
     * @author kuangq
     * @date 2020-06-27 19:31
     */
    @Override
    public ApiResult auth(User user) {
        //禁止操作管理员账号
        if (SystemConst.ADMIN.equals(user.getUuid())) {
            return ApiResult.hintEnum(HintEnum.HINT_1051, user.getAccount());
        }
        //修改认证信息
        user.setAuth(true);
        return ApiResult.success(userMapper.updateUser(user));
    }
}