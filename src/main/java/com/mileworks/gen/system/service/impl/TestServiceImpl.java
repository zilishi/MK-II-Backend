package com.mileworks.gen.system.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.IService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mileworks.gen.system.dao.TestMapper;
import com.mileworks.gen.system.domain.Test;
import com.mileworks.gen.system.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("testService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class TestServiceImpl extends ServiceImpl<TestMapper, Test> implements TestService {

    @Value("${MK.max.batch.insert.num}")
    private int batchInsertMaxNum;

    @Override
    public List<Test> findTests() {
        try {
            EntityWrapper<Test> testEntityWrapper = new EntityWrapper<>();
            testEntityWrapper.orderBy("create_time", false);

            return this.selectList(testEntityWrapper);
        } catch (Exception e) {
            log.error("获取信息失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public void batchInsert(List<Test> list) {
        int total = list.size();
        int max = batchInsertMaxNum;
        int count = total / max;
        int left = total % max;
        int length;
        if (left == 0) length = count;
        else length = count + 1;
        for (int i = 0; i < length; i++) {
            int start = max * i;
            int end = max * (i + 1);
            if (i != count) {
                log.info("正在插入第" + (start + 1) + " ~ " + end + "条记录 ······");
                this.insertBatch(list.subList(start, end));
            } else {
                end = total;
                log.info("正在插入第" + (start + 1) + " ~ " + end + "条记录 ······");
                this.insertBatch(list.subList(start, end));
            }
        }
    }
}
