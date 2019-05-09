package com.mbyte.easy.admin.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mbyte.easy.admin.entity.CheckDetail;
import com.mbyte.easy.admin.mapper.CheckDetailMapper;
import com.mbyte.easy.admin.service.ICheckDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 *
 * @since 2019-04-21
 */
@Service
public class CheckDetailServiceImpl extends ServiceImpl<CheckDetailMapper, CheckDetail> implements ICheckDetailService {
    @Override
    public Page<CheckDetail> listPage(CheckDetail checkDetail, Page<CheckDetail> page) {
        return page.setRecords(this.baseMapper.listPage(checkDetail,page));
    }
}