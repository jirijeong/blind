package com.jsframe.blind.controller;

import com.jsframe.blind.entity.Board;
import com.jsframe.blind.entity.Comment;
import com.jsframe.blind.repository.CommentRepository;

import com.jsframe.blind.service.BoardService;
import com.jsframe.blind.service.CommentService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@Log
public class CommentController {

  ModelAndView mv;


  @Autowired
  private CommentService cServ;

  @Autowired
  private CommentRepository cRepo;


  private BoardService bServ;
//    지인 : 이따 수정할게욥
//    @GetMapping("detail")
//    public ModelAndView detail(long bnum){
//        log.info("detail()");
//        //서비스에서 getBoard 가져옴(bnum 받아옴)
//        mv = bServ.getBoard(bnum);
//        //update에서도 써야해서 서비스 getBoard에서 옮겨옴
//        mv.setViewName("detail");
//
//        return mv;
//    }

  @PostMapping("cWriteProc")
  public String cWriteProc(Comment comment, HttpSession session, RedirectAttributes rttr) {
    log.info("cWriteProc()");
    String view = cServ.commentSave(comment, session, rttr);

    return view;
  }

  @PostMapping("cUpdateProc")
  public String cUpdateProc(Comment comment, HttpSession session, RedirectAttributes rttr) {
    log.info("cUpdateProc()");
    String view = cServ.commentUpdate(comment, session, rttr);

    return view;
  }

  @GetMapping("cDeleteProc")
  public String cDeleteProc(Long cno, HttpSession session, RedirectAttributes rttr) {
    log.info("cDeleteproc()");
    String view = cServ.commentDelete(cno, session, rttr);

    return view;

  }


  @GetMapping("getCommentList")
  public @ResponseBody List<Comment> commentList(Integer cPageNum, String mid, Long bno) {
    log.info("getCommentList()");
    log.info("mid = " + mid);

    List<Comment> commentList = cServ.getCommentList(cPageNum, mid, bno);


    return commentList;
  }


}