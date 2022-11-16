package com.jsframe.blind.service;

import com.jsframe.blind.entity.Board;
import com.jsframe.blind.entity.Comment;
import com.jsframe.blind.entity.Member;
import com.jsframe.blind.repository.BoardRepository;
import com.jsframe.blind.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
@Log
@RequiredArgsConstructor

public class CommentService {

  @Autowired
  private CommentRepository cRepo;

    @Autowired
    private BoardRepository bRepo;

    private ModelAndView mv;


  //댓글 작성
  @Transactional
  public String commentSave(Comment comment, HttpSession session, RedirectAttributes rttr) {
    log.info("commentSave()");
    String msg = null;
    String view = null;

    try {
      cRepo.save(comment);
      log.info("c_no : " + comment.getCno());
      comment.getBcno().setBcomment(comment.getBcno().getBcomment()+1);
      view = "redirect:/detail?bno=" + comment.getBcno().getBno();
      msg = "댓글이 작성되었습니다.";
    } catch (Exception e) {
      e.printStackTrace();
      view = "redirect:/detail?bno=" + comment.getBcno().getBno();
    }
    rttr.addFlashAttribute("msg", msg);
    return view;
  }//commentSave end

  @Transactional
  public String commentUpdate(Comment comment, HttpSession session, RedirectAttributes rttr) {
    log.info("commentUpdate()");
    String msg = null;
    String view = null;

    try {
      cRepo.save(comment);
      msg = "수정되었습니다.";
      view = "redirect:detail/";
    } catch (Exception e) {
      e.printStackTrace();
      msg = "다시 시도해주세요.";
      view = "redirect:/detail";
    }
    rttr.addFlashAttribute("msg", msg);
    return view;
  } //commentUpdate end

  @Transactional
  public String commentDelete(Long cno, HttpSession session, RedirectAttributes rttr) {
    log.info("commentDelete()");
    String msg = null;
    String view = null;

    Comment cmt = cRepo.findById(cno).get();

    //realPath??
    try {
      cRepo.deleteById(cno);
      msg = "삭제되었습니다.";
      view = "redirect:/detail?bno=" + cmt.getBcno().getBno();
    } catch (Exception e) {
      e.printStackTrace();
      msg = "다시 시도해주세요.";
      view = "redirect:/detail?bno=" + cmt.getBcno().getBno();
    }
    rttr.addFlashAttribute("msg", msg);
    return view;
  }    //commentDelete end

  public List<Comment> getCommentList(Integer cPageNum, String mid, Long bno) {
    log.info("getCommentList()");

    int listCnt = 5;
    if (cPageNum == null) {
      cPageNum = 1;
    }
    Pageable commentPageable = PageRequest.of(cPageNum - 1, listCnt, Sort.Direction.DESC, "cdate");

    Board board = bRepo.findById(bno).get();

    List<Comment> commentList = cRepo.findByBcno(board, commentPageable);


    return maskingName(commentList, mid);
  }


  //유저 id 가리기
  private List<Comment> maskingName(List<Comment> commentList, String mid) {

    for (Comment c : commentList) {
      Member user = c.getMcid();

      String userId = c.getMcid().getMid();

      if (mid != null) {
        if (mid.equals(userId)) {
          break;
        }
      }
      String maskedId = userId.substring(0, 1);

      for (int i = 1; i < userId.length(); i++) {
        maskedId += "*";
      }
      user.setMid(maskedId);

    }
    return commentList;
  }



    public List<Comment> getCommentList(Comment bcno, HttpSession session) {
    mv = new ModelAndView();
    log.info("getCommentList()");

    return null; //빨간줄 뜨는거 보기싫어서 임시로 null 집어넣음
    }
}
