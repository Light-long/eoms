package com.tx.eoms.dao;

import com.tx.eoms.pojo.SysConfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysConfigDao {
    List<SysConfig> selectAllParams();
}