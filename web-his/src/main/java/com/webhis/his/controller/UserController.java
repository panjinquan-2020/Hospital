package com.webhis.his.controller;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.extra.pinyin.PinyinUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.webhis.his.VO.pageVO;
import com.webhis.his.domain.User;
import com.webhis.his.service.UserService;
import com.webhis.his.service.impl.UserServiceImpl;
import com.webhis.his.util.StringUtil;
import com.webhis.his.util.TipUtil;
import org.webhis.mvc.ModelAndView;
import org.webhis.mvc.MvcFile;
import org.webhis.mvc.annotations.RequestMapping;
import org.webhis.mvc.annotations.RequestParam;

import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserController {
    private static Digester md5 = new Digester(DigestAlgorithm.MD5);
    private static String PASS="123";

    private UserService userService=new UserServiceImpl();
    //修改密码
    @RequestMapping("/updatePwd")
    public String updatePwd(@RequestParam("opass") String opass,
                            @RequestParam("npass") String npass,
                            @RequestParam("repass") String repass,HttpSession session){
//        if (opass==null||opass.equals("")){
//
//        }
//        if (npass==null||opass.equals("")){
//
//        }
//        if (repass==null||opass.equals("")){
//
//        }
        User user = (User) session.getAttribute("user");
        if (!user.getUpass().equals(opass)){
            //原密码不正确
            return "/view/user/updatePwd.jsp?f=9";
        }
        //原密码正确 判断两次新密码是否一致
        if(!npass.equals(repass)){
            return "/view/user/updatePwd.jsp?f=8";
        }
        user.setUpass(npass);
        userService.updatePwd(user.getUid(),npass);
        return "/view/user/updatePwdSuccess.jsp";
    }
    //查询用户
    @RequestMapping("/user/list")
    public ModelAndView list(@RequestParam("page") Integer page,
                             @RequestParam("rows") Integer rows,
                             @RequestParam("uname") String uname,
                             @RequestParam("phone") String phone){
        Map<String,Object> map=new HashMap<>();
        //为page和rows设置初始值
        if (page==null){
            page=1;
        }
        if (rows==null){
            rows=10;
        }
        map.put("page",page);
        map.put("rows",rows);
        map.put("uname",uname);
        map.put("phone",phone);
        pageVO vo = userService.list(map);
        //转发访问网页并携带数据
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/view/user/list.jsp");
        modelAndView.setAttribute("vo",vo);
        return modelAndView;
    }
    //新增用户
    @RequestMapping("/user/save")
    public ModelAndView save(User user, HttpSession session){
        ModelAndView mv = new ModelAndView();
        //需要先来一组为空判断
        if(StringUtil.isEmpty(user.getUname())){
            mv.setViewName("/view/user/add.jsp?f=uname");
            return mv;
        }

        User loginUser = (User) session.getAttribute("user");

        String result = userService.save(user,loginUser.getUid());

        if(StringUtil.isEmpty(result)){
            //执行正确，可以显示一个提示页面
            mv.setViewName("/view/user/addSuccess.jsp");
            return mv;
        }else{
            //有问题，重新显示保存页面，并提示
            mv.setViewName("/view/user/add.jsp?f="+result);
            mv.setAttribute("user",user);
            return mv;

        }
     }
    //删除用户
    @RequestMapping("/user/delete")
    public String delete(@RequestParam("uid") int uid,HttpSession session){
        User user = (User) session.getAttribute("user");
        Map<String,Long> param=new HashMap<>();
        param.put("uid", (long) uid);
        param.put("update_uid",user.getUid());
        userService.delete(param);
        return TipUtil.tip("删除成功","/user/list");
    }
    //删除用户
    @RequestMapping("/user/deletes")
    public String deletes(@RequestParam("uids") String uids,HttpSession session){
        User user = (User) session.getAttribute("user");
        Map<String,Object> param=new HashMap<>();
        param.put("uids",uids );
        param.put("update_uid",user.getUid());
        userService.deletes(param);
        return TipUtil.tip("删除成功","/user/list");
    }
    @RequestMapping(("/user/editSelect"))
    public ModelAndView editSelect(@RequestParam("uid") int uid){
        User user = userService.findById(uid);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/view/user/edit.jsp");
        modelAndView.setAttribute("user",user);
        return modelAndView;
    }
    @RequestMapping("/user/update")
    public ModelAndView update(User user,@RequestParam("uid") int uid,HttpSession session){
        System.out.println(user.toString());
        ModelAndView mv = new ModelAndView();
        //需要先补全一些数据
        //缺少修改人的信息，当前登录操作的用户，存在session中
        User loginUser = (User) session.getAttribute("user");
        System.out.println(loginUser.getUid());
        user.setUpdate_uid(loginUser.getUid());
        user.setUid((long)uid);
        String result = userService.update(user);

        if(StringUtil.isEmpty(result)){
            //执行正确，可以显示一个提示页面
            mv.setViewName(TipUtil.tip("修改成功","/user/list"));
            return mv;
        }else{
            //有问题，重新显示保存页面，并提示
            mv.setViewName("/view/user/edit.jsp?f="+result);
            mv.setAttribute("user",user);
            return mv;
        }
    }

    @RequestMapping("/user/import")
    public void imports(@RequestParam("excel") MvcFile excel,HttpSession session){
        InputStream inputStream = excel.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        reader.addHeaderAlias("用户名","uname");
        reader.addHeaderAlias("真实姓名","truename");
        reader.addHeaderAlias("年龄","age");
        reader.addHeaderAlias("性别","sex");
        reader.addHeaderAlias("电话","phone");
        reader.addHeaderAlias("邮箱","mail");
        User user = (User) session.getAttribute("user");
        List<User> users = reader.readAll(User.class);
        String tip = userService.saves(users, user.getUid());

    }
}
