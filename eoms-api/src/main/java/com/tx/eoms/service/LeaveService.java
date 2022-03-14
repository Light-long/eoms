package com.tx.eoms.service;

import com.tx.eoms.pojo.Leave;
import com.tx.eoms.util.PageUtils;

import java.util.Map;

public interface LeaveService {

    /**
     * 查询请假分页数据
     */
    PageUtils searchLeaveByPage(Map<String, Object> condition);

    /**
     * 判断是否有重复的请假时间
     */
    boolean searchConflict(Map<String, Object> params);

    /**
     * 添加请假记录
     */
    int AddLeave(Leave leave);

    /**
     * 删除会议记录
     */
    int deleteLeaveById(Map<String, Object> params);

    /**
     * 根据id查询请假记录信息
     */
    Map<String, Object> searchLeaveInfoById(Map<String, Object> params);
}
