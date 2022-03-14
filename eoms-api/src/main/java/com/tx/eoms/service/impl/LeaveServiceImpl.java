package com.tx.eoms.service.impl;

import com.tx.eoms.dao.LeaveDao;
import com.tx.eoms.exception.EomsException;
import com.tx.eoms.pojo.Leave;
import com.tx.eoms.service.LeaveService;
import com.tx.eoms.task.LeaveWorkFlowTask;
import com.tx.eoms.util.PageUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class LeaveServiceImpl implements LeaveService {

    @Resource
    private LeaveDao leaveDao;

    @Resource
    private LeaveWorkFlowTask leaveWorkFlowTask;

    /**
     * 查询请假分页数据
     */
    @Override
    public PageUtils searchLeaveByPage(Map<String, Object> condition) {
        List<Map<String, Object>> leaveList = leaveDao.searchLeaveByPage(condition);
        long count = leaveDao.searchLeaveCount(condition);
        Integer start = (Integer) condition.get("start");
        Integer length = (Integer) condition.get("length");
        return new PageUtils(leaveList, count, start, length);
    }

    /**
     * 判断是否有重复的请假时间
     */
    @Override
    public boolean searchConflict(Map<String, Object> params) {
        long count = leaveDao.searchConflict(params);
        return count > 0;
    }

    /**
     * 1. 添加请假记录
     * 2. 开启工作流
     */
    @Override
    public int AddLeave(Leave leave) {
        int row = leaveDao.AddLeave(leave);
        if (row == 1) {
            // 开启工作流
            leaveWorkFlowTask.startLeaveWorkFlow(leave.getId(), leave.getUserId(), leave.getDays());
        } else {
            throw new EomsException("添加请假记录失败");
        }
        return row;
    }

    /**
     * 删除请假记录
     */
    @Override
    public int deleteLeaveById(Map<String, Object> params) {
        // 查询instanceId
        String instanceId = leaveDao.searchLeaveInstanceId((Integer) params.get("id"));
        int rows = leaveDao.deleteLeaveById(params);
        if (rows == 1) {
            // 删除工作流
            leaveWorkFlowTask.deleteLeaveWorkflow(instanceId, "员工请假", "删除请假申请");
        } else {
            throw new EomsException("删除请假记录失败");
        }
        return rows;
    }

    /**
     * 根据id查询请假记录信息
     */
    @Override
    public Map<String, Object> searchLeaveInfoById(Map<String, Object> params) {
        return leaveDao.searchLeaveInfoById(params);
    }
}
