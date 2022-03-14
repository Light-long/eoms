package com.tx.eoms.dao;

import com.tx.eoms.pojo.Leave;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface LeaveDao {

    /**
     * 分页查询请假数据
     */
    List<Map<String, Object>> searchLeaveByPage(Map<String, Object> condition);

    long searchLeaveCount(Map<String, Object> condition);

    /**
     * 将调用工作流生成instanceId插入leave表中
     */
    int updateLeaveInstanceId(Map<String, Object> params);

    /**
     * 判断是否有重复的请假时间
     */
    long searchConflict(Map<String, Object> params);

    /**
     * 添加请假记录
     */
    int AddLeave(Leave leave);

    /**
     * 根据id查询instanceId
     */
    String searchLeaveInstanceId(int id);

    /**
     * 删除会议记录
     */
    int deleteLeaveById(Map<String, Object> params);

    /**
     * 根据id查询请假记录信息
     */
    Map<String, Object> searchLeaveInfoById(Map<String, Object> params);
}