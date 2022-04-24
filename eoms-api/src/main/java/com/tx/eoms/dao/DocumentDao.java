package com.tx.eoms.dao;

import com.tx.eoms.pojo.Document;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DocumentDao {

    /**
     * 查询文件列表
     */
    List<Map<String, Object>> searchDocumentByPage(Map<String, Object> condition);

    long searchDocumentCount(Map<String, Object> condition);

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