package com.tx.eoms.service.impl;

import cn.hutool.core.map.MapUtil;
import com.tx.eoms.dao.NoticeDao;
import com.tx.eoms.pojo.Notice;
import com.tx.eoms.service.NoticeService;
import com.tx.eoms.util.PageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class NoticeServiceImpl implements NoticeService {

    @Resource
    private NoticeDao noticeDao;

    /**
     * 查询公告栏中的公告
     */
    @Override
    public List<Map<String, Object>> searchNoticeHome() {
        return noticeDao.searchNoticeHome();
    }
    /**
     * 查询公告分页数据
     */
    @Override
    public PageUtils searchNoticeByPage(Map<String, Object> params) {
        List<Map<String, Object>> noticeList = noticeDao.searchNoticeByPage(params);
        long count = noticeDao.searchNoticeCount(params);
        Integer start = MapUtil.getInt(params, "start");
        Integer length = MapUtil.getInt(params, "length");
        return new PageUtils(noticeList, count, start, length);
    }

    /**
     * 添加公告
     */
    @Override
    public int addNotice(Notice notice) {
        return noticeDao.addNotice(notice);
    }

    /**
     * 根据id查询公告信息
     */
    @Override
    public Map<String, Object> searchNoticeById(int id) {
        return noticeDao.searchNoticeById(id);
    }

    /**
     * 更新公告
     */
    @Override
    public int updateNotice(Map<String, Object> params) {
        return noticeDao.updateNotice(params);
    }

    /**
     * 根据id删除公告
     */
    @Override
    public int deleteNoticeByIds(Integer[] ids) {
        return noticeDao.deleteNoticeByIds(ids);
    }
}
