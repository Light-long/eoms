package com.tx.eoms.service;

import com.tx.eoms.pojo.Attendance;

import java.util.Map;

public interface AttendanceService {

    /**
     * 判断是否可以签到
     */
    Map<String, Object> validCanSignIn(Map<String, Object> params);

    /**
     * 签到
     */
    Integer signIn(Attendance attendance);

    /**
     * 查询签到结果
     */
    Map<String, Object> searchSignInResult(Map<String, Object> params);

    /**
     * 检验是否能签退
     */
    Map<String, Object> validCanSignOut(Map<String, Object> params);

    /**
     * 签退
     */
    Integer signOut(Attendance attendance);

    /**
     * 查询签退结果
     */
    Map<String, Object> searchSignOutResult(Map<String, Object> params);

    /**
     * 查询当月的考勤统计
     */
    Map<String, Object> searchAttendanceInMonth(Map<String, Object> params);
}
