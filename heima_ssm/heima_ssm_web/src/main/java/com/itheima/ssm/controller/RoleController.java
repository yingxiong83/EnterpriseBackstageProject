package com.itheima.ssm.controller;

import com.itheima.ssm.domain.Permission;
import com.itheima.ssm.domain.Role;
import com.itheima.ssm.service.RoleService;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @RequestMapping("/findAll.do")
    public ModelAndView findAll() throws Exception {
        ModelAndView mv = new ModelAndView();
        List<Role> roleList = roleService.findAll();
        mv.addObject("roleList", roleList);
        mv.setViewName("role-list");
        return mv;
    }

    @RequestMapping("/save.do")
    public String save(Role role) throws Exception {
        roleService.save(role);
        return "redirect:findAll.do";
    }

    @RequestMapping("/findById.do")
    public ModelAndView findById(String id) throws Exception {
        ModelAndView mv = new ModelAndView();
        Role role = roleService.findById(id);
        mv.addObject("role", role);
        mv.setViewName("role-show");
        return mv;
    }


    @RequestMapping("/findNoPermissions.do")
    public ModelAndView findNoPermissions(String id) throws Exception {
        ModelAndView mv = new ModelAndView();
        List<Permission> permissionList = roleService.findNoPermissions(id);
        if (permissionList == null || permissionList.size() == 0) {
            mv.setViewName("role-permission-haveAll");
        } else {
            mv.addObject("roleId", id);
            mv.addObject("permissionList", permissionList);
            mv.setViewName("role-permission-add");
        }
        return mv;
    }

    @RequestMapping("/addPermissionToRole.do")
    public String addPermissionToRole(@RequestParam(name = "ids",required = true) String[] permissionIds,@RequestParam(name = "roleId",required = true) String roleId) throws Exception {
        roleService.addPermissionToRole(permissionIds, roleId);
        return "redirect:findAll.do";
    }

    @RequestMapping("/delete.do")
    public String delete(@RequestParam(name = "id",required = true) String roleId) throws Exception {
        roleService.delete(roleId);
        return "redirect:findAll.do";
    }
}
