package com.tx.eoms.dao;

import com.tx.eoms.pojo.Attendance;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface AttendanceDao {

    /**
     * 判断改天是否已经签到过
     * @param params userId
     * @return 如果有签到记录，则id不为null
     */
    Integer isAlreadySignIn(Map<String, Object> params);

    /**
     * 签到
     */
    Integer signIn(Attendance attendance);

    /**
     * 查询签到结果
     */
    Map<String, Object> searchSignInResult(Map<String, Object> params);

    /**
     * 查询是否已经签退过了，只能签退一次
     */
    Integer isAlreadySignOut(Map<String, Object> params);

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

    /**
     * 查询当天签到的用户id
     */
    List<Integer> searchAllSignInUserByDate(Map<String, Object> params);

    /**
     * 查询个人考勤记录
     */
    List<Map<String, Object>> searchAttendanceRecord(Map<String, Object> params);

    long searchAttendanceRecordCount(Map<String, Object> params);
}