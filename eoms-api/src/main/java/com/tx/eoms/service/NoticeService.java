package com.tx.eoms.service;

import com.tx.eoms.pojo.Notice;
import com.tx.eoms.util.PageUtils;

import java.util.List;
import java.util.Map;

public interface NoticeService {

    /**
     * 查询公告栏中的公告
     */
    List<Map<String, Object>> searchNoticeHome();

    /**
     * 查询公告分页数据
     */
    PageUtils searchNoticeByPage(Map<String, Object> params);

    /**
     * 添加公告
     */
    int addNotice(Notice notice);

    /**
     * 根据id查询公告信息
     */
    Map<String, Object> searchNoticeById(int id);

    /**
     * 更新公告
     */
    int updateNotice(Map<String, Object> params);

    /**
     * 根据id删除公告
     */
    int deleteNoticeByIds(Integer[] ids);
}
