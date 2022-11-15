package com.jsframe.blind.service;

import com.jsframe.blind.entity.Board;
import com.jsframe.blind.entity.Comment;
import com.jsframe.blind.repository.BoardRepository;
import com.jsframe.blind.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.unbescape.html.HtmlEscape;

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

        view = "redirect:/detail";
        msg = "댓글이 작성되었습니다.";
    } catch (Exception e) {
        e.printStackTrace();
        view = "redirect:/detail";
    }
    rttr.addFlashAttribute("msg", msg);
    return view;
}//commentSave end

@Transactional
public String commentUpdate(Comment comment, HttpSession session, RedirectAttributes rttr){
    log.info("commentUpdate()");
    String msg = null;
    String view = null;

    try {
        cRepo.save(comment);
        msg = "수정되었습니다.";
        view = "redirect:detail/";
    } catch (Exception e){
        e.printStackTrace();
        msg = "다시 시도해주세요.";
        view = "redirect:/detail";
    }
    rttr.addFlashAttribute("msg",msg);
    return view;
} //commentUpdate end

@Transactional
public String commentDelete(Long cno, HttpSession session, RedirectAttributes rttr){
    log.info("commentDelete()");
    String msg = null;
    String view = null;

    Comment comment = new Comment();
    comment.setCno(cno);

    //realPath??
    try {
        cRepo.deleteById(cno);
        msg = "삭제되었습니다.";
        view = "redirect:/detail";
    } catch (Exception e){
        e.printStackTrace();
        msg = "다시 시도해주세요.";
        view = "redirect:/detail";
    }
    rttr.addFlashAttribute("msg",msg);
    return view;
}    //commentDelete end


    public List<Comment> getCommentList(Comment bcno, HttpSession session) {
    mv = new ModelAndView();
    log.info("getCommentList()");

    return null; //빨간줄 뜨는거 보기싫어서 임시로 null 집어넣음
    }
}
