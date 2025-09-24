package com.lpy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lpy.entity.Storage;
import com.lpy.mapper.StorageMapper;
import com.lpy.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StorageServiceImpl implements StorageService {

    @Autowired
    private StorageMapper storageMapper;

    @Override
    public void decrease(Long productId, Integer count) {
        log.info("------->storage-service中扣减库存开始");

        QueryWrapper<Storage> wrapper = new QueryWrapper<>();
        wrapper.eq("product_id", productId);
        Storage storage = storageMapper.selectOne(wrapper);

        if (storage != null && storage.getResidue() >= count) {
            storage.setUsed(storage.getUsed() + count);
            storage.setResidue(storage.getResidue() - count);
            storageMapper.updateById(storage);
        } else {
            throw new RuntimeException("库存不足");
        }

        log.info("------->storage-service中扣减库存结束");
    }
}