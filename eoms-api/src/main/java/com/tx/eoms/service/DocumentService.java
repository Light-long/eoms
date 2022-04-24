package com.tx.eoms.service;

import com.tx.eoms.pojo.Document;
import com.tx.eoms.util.PageUtils;

import java.util.List;
import java.util.Map;

public interface DocumentService {

    /**
     * 查询文件列表
     */
    PageUtils searchDocumentByPage(Map<String, Object> condition);

    /**
     * 添加文件信息
     */
    int addDocument(Document document);

    /**
     * 根据id查出相对路径path
     */
    String searchPathById(Integer id);

    int deleteDocument(Integer id);
}
