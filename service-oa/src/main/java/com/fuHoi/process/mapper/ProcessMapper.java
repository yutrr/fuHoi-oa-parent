package com.fuHoi.process.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fuHoi.model.process.Process;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fuHoi.vo.process.ProcessQueryVo;
import com.fuHoi.vo.process.ProcessVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 审批类型 Mapper 接口
 * </p>
 *
 * @author fuHoi
 * @since 2023-06-26
 */
@Mapper
public interface ProcessMapper extends BaseMapper<Process> {


    IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam,@Param("vo") ProcessQueryVo processQueryVo);
}
