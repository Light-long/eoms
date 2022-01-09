package com.tx.eoms.service.impl;

import com.tx.eoms.dao.DeptDao;
import com.tx.eoms.exception.EomsException;
import com.tx.eoms.pojo.Dept;
import com.tx.eoms.service.DeptService;
import com.tx.eoms.util.PageUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class DeptServiceImpl implements DeptService {

    @Resource
    private DeptDao deptDao;

    /**
     * 查询所有部门信息
     */
    @Override
    public List<Map<String, Object>> searchAllDept() {
        return deptDao.searchAllDept();
    }

    /**
     * 条件分页查询部门信息
     * @param condition 查询条件
     */
    @Override
    public PageUtils searchDeptByPage(Map<String, Object> condition) {
        List<Map<String, Object>> deptList = deptDao.searchDeptByPage(condition);
        long deptCount = deptDao.searchDeptCount(condition);
        Integer start = (Integer) condition.get("start");
        Integer length = (Integer) condition.get("length");
        return new PageUtils(deptList, deptCount, start, length);
    }

    /**
     * 添加部门
     */
    @Override
    public int addDept(Dept dept) {
        return deptDao.addDept(dept);
    }

    /**
     * 更新部门信息
     */
    @Override
    public int updateDept(Dept dept) {
        return deptDao.updateDept(dept);
    }

    /**
     * 根据id删除部门
     */
    @Override
    public int deleteDeptByIds(Integer[] ids) {
        if (!deptDao.searchCanDelete(ids)) {
            throw new EomsException("无法删除有关联用户的部门");
        }
        return deptDao.deleteDeptByIds(ids);
    }

    /**
     * 根据ID查询部门
     */
    @Override
    public Map<String, Object> searchDeptById(Integer id) {
        return deptDao.searchDeptById(id);
    }
}
