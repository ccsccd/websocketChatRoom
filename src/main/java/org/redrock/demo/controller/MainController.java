package org.redrock.demo.controller;


import org.redrock.demo.entity.User;
import org.redrock.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@SessionAttributes("user")
public class MainController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private UserService userService;
    @RequestMapping("/websocket")
    public String webSocket(@ModelAttribute("user")final User user,Model model){
        try{
            logger.info("跳转到websocket的页面上");
            model.addAttribute("username", user.getUsername());
            return "websocket";
        }
        catch (Exception e){
            logger.info("跳转到websocket的页面上发生异常，异常信息是："+e.getMessage());
            return "error";
        }
    }
    @GetMapping("/view/register")
    public String view(){
        return "register";
    }
    @GetMapping("/view/login")
    public String view2(){
        return "login";
    }
    @PostMapping("/register")
    @ResponseBody
    public Map<String,Object> register(@RequestParam("username")final String username,
                                       @RequestParam("password")final String password){
        Map<String,Object> res = new HashMap<>();
        User user=new User(username,password);
        String regex1 ="^[a-zA-Z0-9\\u4e00-\\u9fa5]{1,35}$";//无特殊符号
        String regex2 = "^(?![^a-zA-Z]+$)(?!\\D+$)[0-9a-zA-Z]{6,35}$";//6-35位数字混字母
        if(user.getUsername().matches(regex1) && user.getPassword().matches(regex2)){
            if (userService.insertUser(user)) {
                res.put("status",200);
                res.put("data","注册成功");
            }else{
                res.put("status",400);
                res.put("data","注册失败，用户名已存在");
            }
        }else {
            res.put("status",300);
            res.put("data","注册失败，注册信息不合法");
        }
        return res;
    }
    @PostMapping("/login")
    @ResponseBody
    public Map<String,Object> login(@RequestParam("username")final String username,
                                    @RequestParam("password")final String password,
                                    Model model,
                                    HttpServletRequest httpServletRequest){
        Map<String,Object> res = new HashMap<>();
        User user = null;
        user = userService.checkLogin(username, password);
        if (user != null) {
            res.put("status",200);
            res.put("data","登录成功");
            model.addAttribute("user",user);
            httpServletRequest.getSession();
        }else {
            res.put("status",400);
            res.put("data","登录失败，用户名或密码错误");
        }
        return res;
    }
//    @PostMapping("/check_friend")
//    @ResponseBody
//    public Map<String,Object> checkFreind(@RequestParam("fromUser")final String fromUser,
//                                    @RequestParam("toUser")final String toUser){
//        Map<String,Object> res = new HashMap<>();
//        if (userService.checkFriend(fromUser,toUser)) {
//            res.put("status",200);
//            res.put("data","已成为好友");
//        }else {
//            res.put("status",400);
//            res.put("data","不是好友");
//        }
//        return res;
//    }
}
