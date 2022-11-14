package com.jsframe.blind.controller;


import com.jsframe.blind.entity.Member;
import com.jsframe.blind.service.MemberService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@Log
public class MemberController {
  @Autowired
  private MemberService mServ;


  @GetMapping("login")
  public String home(){
    log.info("home()");
    return "login";
  }
  @GetMapping("register")
  public String register(){
    log.info("register()");
    return "register";
  }

  @PostMapping("regProc")
  public String regProc(Member member, RedirectAttributes rttr){
    String view = mServ.reMember(member, rttr);
    return view;
  }

  @PostMapping("loginp")
  public String loginp(Member member, HttpSession session, RedirectAttributes rttr){
    String view = mServ.loginMember(member, session, rttr);
    return view;
  }
}
