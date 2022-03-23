package com.tx.eoms.dao;

import com.tx.eoms.pojo.Notice;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface NoticeDao {

    /**
     * 查询公告栏中的公告
     */
    List<Map<String, Object>> searchNoticeHome();

    /**
     * 查询公告分页数据
     */
    List<Map<String, Object>> searchNoticeByPage(Map<String, Object> params);

    long searchNoticeCount(Map<String, Object> params);

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