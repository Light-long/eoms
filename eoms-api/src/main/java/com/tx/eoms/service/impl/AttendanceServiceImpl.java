package com.tx.eoms.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import com.tx.eoms.config.init.SystemConstants;
import com.tx.eoms.dao.AttendanceDao;
import com.tx.eoms.pojo.Attendance;
import com.tx.eoms.service.AttendanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class AttendanceServiceImpl implements AttendanceService {
    
    @Resource
    private AttendanceDao attendanceDao;

    @Resource
    private SystemConstants systemConstants;

    /**
     * 判断是否可以签到
     */
    @Override
    public Map<String, Object> validCanSignIn(Map<String, Object> params) {
        // 否建返回结果
        Map<String, Object> result = new HashMap<>();
        // 判断是否是休息日
        String today = "工作日";
        if (DateUtil.date().isWeekend()) {
            today = "休息日";
        }
        if (Objects.equals("休息日", today)) {
            result.put("message", "休息日不用签到");
            result.put("flag", false);
            return result;
        } else {
            // 打卡时间
            DateTime now = DateUtil.date();
            String start = DateUtil.today() + " " + systemConstants.signInStartTime;
            DateTime signInStartTime = DateUtil.parse(start);
            // 判断是否已经打过卡了
            HashMap<String, Object> map = new HashMap<>();
            map.put("userId", MapUtil.getInt(params, "userId"));
            map.put("date", MapUtil.getStr(params, "date"));
            if (attendanceDao.isAlreadySignIn(map) != null) {
                result.put("message", "不能重复签到");
                result.put("flag", false);
                return result;
            } else if (now.isBefore(signInStartTime)){
                result.put("message", "签到还未开始");
                result.put("flag", false);
                return result;
            } else {
                result.put("message", "可以签到");
                result.put("flag", true);
                return result;
            }
        }
    }

    /**
     * 签到
     */
    @Override
    public Integer signIn(Attendance attendance) {
        return attendanceDao.signIn(attendance);
    }

    /**
     * 查询签到结果
     */
    @Override
    public Map<String, Object> searchSignInResult(Map<String, Object> params) {
        return attendanceDao.searchSignInResult(params);
    }

    /**
     * 检验是否能签退
     */
    @Override
    public Map<String, Object> validCanSignOut(Map<String, Object> params) {
       Map<String, Object> result = new HashMap<>();
        // 判断是否已经签到了，没有签到不能签退
        if (attendanceDao.isAlreadySignIn(params) == null) {
            result.put("message", "还没有签到不能签退");
            result.put("flag", false);
        } else {
            // 判断是否已经签退过了
            if (attendanceDao.isAlreadySignOut(params) != null) {
                result.put("message", "不能重复签退");
                result.put("flag", false);
            } else {
                result.put("message", "可以签退");
                result.put("flag", true);
            }
        }
        return result;
    }

    /**
     * 签退
     */
    @Override
    public Integer signOut(Attendance attendance) {
        return attendanceDao.signOut(attendance);
    }

    /**
     * 查询签退结果
     */
    @Override
    public Map<String, Object> searchSignOutResult(Map<String, Object> params) {
        return attendanceDao.searchSignOutResult(params);
    }
}
