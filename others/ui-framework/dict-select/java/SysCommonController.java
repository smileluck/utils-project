package com.ruoyi.web.controller.system;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.domain.vo.SelectVo;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.tap.domain.TapUser;
import com.ruoyi.tap.service.ITapUserService;
import org.apache.commons.collections.ListUtils;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/system/common")
public class SysCommonController extends BaseController {

    @Autowired
    private ITapUserService tapUserService;

    /**
     * 获取参数配置列表
     */
    @GetMapping("/select")
    public AjaxResult select(@RequestParam("type") String type, @RequestParam("name") String name) {
        SelectVo vo = null;
        switch (type) {
            case "user": {
                vo = SelectVo.createOf("id", "phone", tapUserService.lambdaQuery()
                        .select(TapUser::getId, TapUser::getPhone)
                        .likeRight(TapUser::getPhone, name).list());
                break;
            }
        }
        return success(vo);
    }

}