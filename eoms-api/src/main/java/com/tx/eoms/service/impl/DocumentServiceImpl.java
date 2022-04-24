package com.tx.eoms.service.impl;

import cn.hutool.core.map.MapUtil;
import com.tx.eoms.dao.DocumentDao;
import com.tx.eoms.pojo.Document;
import com.tx.eoms.service.DocumentService;
import com.tx.eoms.util.PageUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class DocumentServiceImpl implements DocumentService {

    @Resource
    private DocumentDao documentDao;

    /**
     * 查询文件列表
     */
    @Override
    public PageUtils searchDocumentByPage(Map<String, Object> condition) {
        List<Map<String, Object>> documentList = documentDao.searchDocumentByPage(condition);
        long totalCount = documentDao.searchDocumentCount(condition);
        int start = MapUtil.getInt(condition, "start");
        int length = MapUtil.getInt(condition, "length");
        return new PageUtils(documentList, totalCount, start, length);
    }

    /**
     * 添加文件信息
     */
    @Override
    public int addDocument(Document document) {
        return documentDao.addDocument(document);
    }

    /**
     * 根据id查出相对路径path
     */
    @Override
    public String searchPathById(Integer id) {
        return documentDao.searchPathById(id);
    }

    @Override
    public int deleteDocument(Integer id) {
        return documentDao.deleteDocument(id);
    }
}
