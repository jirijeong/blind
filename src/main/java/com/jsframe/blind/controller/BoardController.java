package com.jsframe.blind.controller;


import com.jsframe.blind.entity.Board;
import lombok.extern.java.Log;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.jsframe.blind.service.BoardService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;


@Controller
@Log
public class BoardController {

    ModelAndView mv;
    @Autowired
    private BoardService bServ;

//    @GetMapping("/")
//    public ModelAndView getList(Integer pageNum, HttpSession session) {
//        log.info("getList()");
//     //   mv = bSev.getBoardList(pageNum, session);
//        return mv;
//    }



































    ///////////////   suyeop   //////////////////
    //테스트용 홈
    @GetMapping("home")
    public String home(){
        log.info("home()");
        return "homeTest";
    }

    @GetMapping("topic")
    public ModelAndView topic(String category) {
        log.info("topic()");
        mv = new ModelAndView();
        mv.addObject("category",category);
        mv.setViewName("topic");

        return mv;
    }


    @GetMapping("getTopicList")
    public @ResponseBody List<Board> topic(String category, Integer pageNum, HttpSession session){
        log.info("getTopicList()");



        //오늘기준 조회수 최다 게시글


        // Board weekBest = bServ.getWeekBest();
        //일주일 내 조회수 최다 게시글

        //해당 카테고리의 글목록 가져오기(20개)
        List<Board> topicList = bServ.getTopicList(category, pageNum, session);


        return topicList;
    }

    @GetMapping("writeTest")
    public String writeFrm() {
        log.info("write()");
        return "writeTest";
    }

    @PostMapping("writeProc")
    public String writeProc(@RequestPart List<MultipartFile> files,
                            Board board,
                            HttpSession session,
                            RedirectAttributes rttr) {

        log.info("writeProc()");
        String view = bServ.insertBoard(files, board, session, rttr);
        return view;

    }


    @GetMapping("detail")
    public ModelAndView detail(Long bno){
        log.info("detail()");
        bServ.addViewCount(bno);

        mv=new ModelAndView();

        return mv;
    }
    //////////////////////////////////////////

}


