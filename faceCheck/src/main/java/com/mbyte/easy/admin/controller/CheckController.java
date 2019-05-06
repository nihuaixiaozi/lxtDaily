package com.mbyte.easy.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mbyte.easy.admin.entity.Check;
import com.mbyte.easy.admin.entity.CheckDetail;
import com.mbyte.easy.admin.service.ICheckDetailService;
import com.mbyte.easy.admin.service.ICheckService;
import com.mbyte.easy.common.controller.BaseController;
import com.mbyte.easy.common.web.AjaxResult;
import com.mbyte.easy.entity.SysUser;
import com.mbyte.easy.mapper.SysUserMapper;
import com.mbyte.easy.util.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
* <p>
* 前端控制器
* </p>
*
* @since 2019-03-11
*/
@Controller
@RequestMapping("/admin/check")
public class CheckController extends BaseController  {

    private String prefix = "admin/check/";

    @Autowired
    private ICheckService checkService;
    @Autowired
    private ICheckDetailService checkDetailService;
    @Autowired
    private SysUserMapper sysUserMapper;

    /**
    * 查询列表
    *
    * @param model
    * @param pageNo
    * @param pageSize
    * @param check
    * @return
    */
    @RequestMapping
    public String index(Model model,@RequestParam(value = "pageNo", required = false, defaultValue = "1") Integer pageNo,@RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize, String creTimeSpace, Check check) {
        Page<Check> page = new Page<Check>(pageNo, pageSize);
        QueryWrapper<Check> queryWrapper = new QueryWrapper<Check>();

        if(check.getTitle() != null  && !"".equals(check.getTitle() + "")) {
            queryWrapper = queryWrapper.like("title",check.getTitle());
         }


        if(check.getImg() != null  && !"".equals(check.getImg() + "")) {
            queryWrapper = queryWrapper.like("img",check.getImg());
         }


        if(check.getCreTime() != null  && !"".equals(check.getCreTime() + "")) {
            queryWrapper = queryWrapper.like("cre_time",check.getCreTime());
         }

        IPage<Check> pageInfo = checkService.page(page, queryWrapper);
        model.addAttribute("creTimeSpace", creTimeSpace);
        model.addAttribute("searchInfo", check);
        model.addAttribute("pageInfo", new PageInfo(pageInfo));
        return prefix+"check-list";
    }

    /**
    * 添加跳转页面
    * @return
    */
    @GetMapping("addBefore")
    public String addBefore(){
        return prefix+"add";
    }
    /**
    * 添加
    * @param check
    * @return
    */
    @PostMapping("add")
    @ResponseBody
    public AjaxResult add(Check check, MultipartFile file){
        checkService.insertCheck(check);
        //获取所有用户信息
        List<SysUser> sysUserList = sysUserMapper.selectByUser(null);
        //获取合照信息
        String filePath = FileUtil.uploadFile(file);
        check.setImg(FileUtil.uploadSuffixPath+filePath);
        //未缺勤的用户名
        List<String> userNameList = BaiDuUtil.searchMul(FileUtil.uploadLocalPath+filePath);
        List<CheckDetail> detailList = new ArrayList<>();
        //考勤，修改状态
        for (SysUser sysUser : sysUserList) {
            if(sysUser.getRoles().get(0).getId().equals(Utility.ROLE_STu)){
                if(userNameList.contains(sysUser.getUsername())){
                    detailList.add(new CheckDetail().setCheckId(check.getId())
                            .setUserId(sysUser.getId())
                            .setStatus(Constants.CHECK_YES)
                    );
                }else{
                    detailList.add(new CheckDetail().setCheckId(check.getId())
                            .setUserId(sysUser.getId())
                            .setStatus(Constants.CHECK_NO)
                    );
                }
            }
        }
        checkDetailService.saveBatch(detailList);
        return success();
    }
    /**
    * 添加跳转页面
    * @return
    */
    @GetMapping("editBefore/{id}")
    public String editBefore(Model model,@PathVariable("id")Long id){
        model.addAttribute("check",checkService.getById(id));
        return prefix+"edit";
    }
    /**
    * 添加
    * @param check
    * @return
    */
    @PostMapping("edit")
    @ResponseBody
    public AjaxResult edit(Check check){
        return toAjax(checkService.updateById(check));
    }
    /**
    * 删除
    * @param id
    * @return
    */
    @GetMapping("delete/{id}")
    @ResponseBody
    public AjaxResult delete(@PathVariable("id") Long id){
        return toAjax(checkService.removeById(id));
    }
    /**
    * 批量删除
    * @param ids
    * @return
    */
    @PostMapping("deleteAll")
    @ResponseBody
    public AjaxResult deleteAll(@RequestBody List<Long> ids){
        return toAjax(checkService.removeByIds(ids));
    }

}

