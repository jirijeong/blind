package com.jsframe.blind.service;


import com.jsframe.blind.entity.Member;
import com.jsframe.blind.repository.SignUpRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Service
@Log
public class MemberService {
  @Autowired
  private SignUpRepository sRepo;

  public String reMember(Member member, RedirectAttributes rttr) {
    log.info("regMember()");
    String msg = null;
    String view = null;

    try {
      sRepo.save(member);
      msg = "가입성공";
      view = "redirect:/";
    } catch (Exception e) {
      e.printStackTrace();
      msg = "가입실패";
      view = "redirect:register";
    }
    rttr.addFlashAttribute("msg", msg);
    return view;
  }

  public String loginMember(Member member, HttpSession session, RedirectAttributes rttr){
    log.info("loginMember()");
    String msg = null;
    String view = null;//로그인 성공 후 보여질 페이지 이름

    Member dbmem = sRepo.findById(member.getMid()).get();

    if(dbmem != null){
      String userPw = member.getMpwd();//사용자가 입력한 비밀번호
      String dbpw = dbmem.getMpwd();//DB에 저장된 비밀번호
      if(userPw.equals(dbpw)){
        msg = "로그인 성공";
        view = "redirect:main";//로그인 성공 후 페이지(메인)
        session.setAttribute("member", dbmem);
      }
      else {
        msg = "비밀번호 맞지않음";
        view = "redirect:/";//로그인이 있는 페이지로 이동
      }
    }
    else {
      msg = "가입된 아이디가 아닙니다.";
      view = "redirect:/";//로그인이 있는 페이지로 이동
    }
    rttr.addFlashAttribute("msg", msg);
    return view;
  }
}//end



