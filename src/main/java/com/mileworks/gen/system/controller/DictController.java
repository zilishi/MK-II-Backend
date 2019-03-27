package com.mileworks.gen.system.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.mileworks.gen.common.annotation.Log;
import com.mileworks.gen.common.domain.QueryRequest;
import com.mileworks.gen.common.exception.MKException;
import com.mileworks.gen.system.domain.Dict;
import com.mileworks.gen.system.service.DictService;
import com.wuwenze.poi.ExcelKit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("dict")
public class DictController {

    private String message;

    @Autowired
    private DictService dictService;

    @GetMapping
    @RequiresPermissions("dict:view")
    public Page<Dict> DictList(QueryRequest request, Dict dict) {
        EntityWrapper<Dict> dictWrapper = new EntityWrapper<>();
        dictWrapper.orderBy(request.getSortField());

        if (StringUtils.isNotBlank(dict.getKeyy())) {
            dictWrapper.eq("keyy", Long.valueOf(dict.getKeyy()));
        }
        if (StringUtils.isNotBlank(dict.getValuee())) {
            dictWrapper.eq("valuee", dict.getValuee());
        }
        if (StringUtils.isNotBlank(dict.getTableName())) {
            dictWrapper.eq("table_name", dict.getTableName());
        }
        if (StringUtils.isNotBlank(dict.getFieldName())) {
            dictWrapper.eq("field_name", dict.getFieldName());
        }
        Page<Dict> jobLogPage = new Page<>(request.getPageNum(), request.getPageSize());
        return this.dictService.selectPage(jobLogPage, dictWrapper);
    }

    @Log("新增字典")
    @PostMapping
    @RequiresPermissions("dict:add")
    public void addDict(@Valid Dict dict) throws MKException {
        try {
            this.dictService.createDict(dict);
        } catch (Exception e) {
            message = "新增字典成功";
            log.error(message, e);
            throw new MKException(message);
        }
    }

    @Log("删除字典")
    @DeleteMapping("/{dictIds}")
    @RequiresPermissions("dict:delete")
    public void deleteDicts(@NotBlank(message = "{required}") @PathVariable String dictIds) throws MKException {
        try {
            String[] ids = dictIds.split(",");
            this.dictService.deleteDicts(ids);
        } catch (Exception e) {
            message = "删除字典成功";
            log.error(message, e);
            throw new MKException(message);
        }
    }

    @Log("修改字典")
    @PutMapping
    @RequiresPermissions("dict:update")
    public void updateDict(@Valid Dict dict) throws MKException {
        try {
            this.dictService.updateDict(dict);
        } catch (Exception e) {
            message = "修改字典成功";
            log.error(message, e);
            throw new MKException(message);
        }
    }

    @PostMapping("excel")
    @RequiresPermissions("dict:export")
    public void export(Dict dict, QueryRequest request, HttpServletResponse response) throws MKException {
        try {
            List<Dict> dicts = this.dictService.findDicts(request, dict);
            ExcelKit.$Export(Dict.class, response).downXlsx(dicts, false);
        } catch (Exception e) {
            message = "导出Excel失败";
            log.error(message, e);
            throw new MKException(message);
        }
    }
}
