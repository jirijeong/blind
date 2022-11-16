package com.jsframe.blind.controller;


import com.jsframe.blind.entity.Board;
import com.jsframe.blind.entity.BoardFiles;
import lombok.extern.java.Log;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.jsframe.blind.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;


@Controller
@Log
public class BoardController {

  ModelAndView mv;
  @Autowired
  private BoardService bServ;


  @GetMapping("/")
  public ModelAndView topic(String category, HttpSession session) {
    log.info("topic()");
    mv = new ModelAndView();
    try {
   //   log.info("session before try = "+ session.getAttribute("board").toString());
      session.getAttribute("board");
      session.removeAttribute("board");
    //  log.info("session after try = "+ session.getAttribute("board").toString());
    }catch (Exception e){
      //e.printStackTrace();
    }finally {


      if (category != null) {

        mv.addObject("category", category);
      }
      mv.setViewName("topic");

    }
    return mv;

  }


  ///////////////   suyeop   //////////////////
  //테스트용 홈


//
//  @GetMapping("topic")
//  public ModelAndView topic(String category) {
//    log.info("topic()");
//    mv = new ModelAndView();
//    mv.addObject("category", category);
//    mv.setViewName("topic");
//
//    return mv;
//  }


  @GetMapping("getTopicList")
  public @ResponseBody List<Board> topic(String category, Integer pageNum, HttpSession session) {
    log.info("getTopicList()");
    System.out.println("category = " + category);


    //해당 카테고리의 글목록 가져오기(20개)
    List<Board> topicList = bServ.getTopicList(category, pageNum, session);


//
//    if(topicList.get(0)==null){
//      System.out.println("topicList = " + topicList);
//      topicList =null;
//    }


    return topicList;
  }

  //글 작성
  @GetMapping("writePost")
  public String writeFrm() {
    log.info("write()");
    return "writePost";
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
//글 보기


  //글 수정
  @GetMapping("updateFrm")
  public ModelAndView updateFrm(long bno) {
    //상세보기페이지에서도 글 데이터를 가져와서 수정메서드로 보낼 수 있지만, 과정이 매우 번거롭다.
    //bnum만 가져와서 DB를 검색해오는게 편함

    log.info("updateFrm()");
    mv = bServ.getBoard(bno);
    mv.setViewName("updateFrm");
    return mv;
  }

  @PostMapping("updateProc")
  public String updateProc(List<MultipartFile> files, Board board, HttpSession session, RedirectAttributes rttr) {
    log.info("updateProc()");
    String view = bServ.boardUpdate(files, board, session, rttr);

    return view;
  }


  //파일 다운로드
  @GetMapping("download")
  public ResponseEntity<Resource> fileDownload(BoardFiles bfile, HttpSession session) throws IOException {
    ResponseEntity<Resource> resp = bServ.fileDownload(bfile, session);
    return resp;
  }


  @GetMapping("detail")
  public ModelAndView detail(Long bno, HttpSession session) {
    log.info("detail()");
    bServ.addViewCount(bno, session);


    mv = bServ.getBoard(bno);
    mv.setViewName("detail");

    return mv;
  }


  @GetMapping("delete")
  public String deleteBoard(long bno, HttpSession session, RedirectAttributes rttr) {
    log.info("deleteBoard()");
    String view = bServ.boardDelete(bno, session, rttr);

    return view;

  }


}


