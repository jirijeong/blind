package com.jsframe.blind.service;

import com.jsframe.blind.entity.Board;
import com.jsframe.blind.entity.BoardFiles;
import com.jsframe.blind.entity.Member;
import com.jsframe.blind.repository.BoardFileRepository;
import com.jsframe.blind.repository.BoardRepository;
import com.jsframe.blind.repository.MemberRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.List;
import java.util.Optional;

@Service
@Log
public class BoardService {
    @Autowired
    private BoardRepository bRepo;
    @Autowired
    private BoardFileRepository bfRepo;

    @Autowired
    private MemberRepository mRepo;

    private ModelAndView mv;


//지인 !! 이따 수정할것
public ModelAndView getBoard(long bnum){
    log.info("getBoard()");
    mv = new ModelAndView();
        mv.setViewName("detail"); //>> 컨트롤러 디테일로 옮겼음

    //게시글을 DB에서 가져와서 담기
    Board board = bRepo.findById(bnum).get();
    mv.addObject("board",board);

    return mv;
}





    /////////suyeop//////////////////

    //게시글 출력
    public List<Board> getTopicList(String category, Integer pageNum, HttpSession session) {
        log.info("getTopicList()");

        int listCnt = 20;

        if (pageNum == null) {
            pageNum = 1;
        }

        Pageable topicPageable = PageRequest.of(pageNum - 1, listCnt, Sort.Direction.DESC, "bdate");
        List<Board> topicList;
        switch (category) {
            case "전체":
                topicList = bRepo.findAll(topicPageable);
                break;
            case "베스트":
                topicList = bRepo.findAllByOrderByBviewDesc(topicPageable);
                break;
            default:
                topicList = bRepo.findByBcategoryContains(category, topicPageable);
        }



        session.setAttribute("pageNum", pageNum);
        return topicList;
    }

    //날짜 처리


    //+ 좋아요






    //게시글 저장 메서드
@Transactional
    public String insertBoard(List<MultipartFile> files,
                              Board board,
                              HttpSession session,
                              RedirectAttributes rttr) {
        log.info("insertBoard()");
        String msg = null;
        String view = null;

        try {

      for (int i = 0; i < 100; i++) {

      Member mem = new Member();
      mem.setMid("유저"+i);
      mem.setMcname("회사" +i);
      mem.setMemail("유저" + i + "@email.com");
      mem.setMpwd("0000");
      mRepo.save(mem);
      Board board1 = new Board();
      board1.setMbId(mem);
      board1.setBcategory("라이프");
      board1.setBcontent("내용" + i);
      board1.setBtitle("제목"+i);
      board1.setBlike(0);
      board1.setBreport(0);
      board1.setBreport(0);
      board1.setBcomment(0);
      board1.setBview(i);
      bRepo.save(board1);


      }


            bRepo.save(board);
            fileUpload(files, session, board);

            view = "redirect:topic";
            msg = "저장 성공";

        } catch (Exception e) {
            e.printStackTrace();
            view = "redirect:write";
            msg = "저장 실패";
        }
        rttr.addFlashAttribute("msg", msg);
        return view;
    }









    private void fileUpload(List<MultipartFile> files,
                            HttpSession session, Board board) throws Exception {
        log.info("fileUpload()");

        //파일 저장 위치 지정. session 활용
        String realPath = session.getServletContext().getRealPath("/");//getServletContext : 연결중인 세션의 정보를 가져옴. getRealPath: 물리적인 경로("/" : 루트 경로)
        log.info("realpath : " + realPath);

        //파일 업로드용 폴더를 자동으로 생성하도록 처리(업로드용 폴더 : upload)
        //webapp은 웹으로 처리되는 것들을 저장하는 장소이므로, 저장용 폴더를 따로 생성해서 저장한다.
        realPath += "upload/";
        File folder = new File(realPath); //물리적인 장소(디스크 내의 경로 reapath)에 저장된 File을 메모리에 담아서 처리하는 형태로 처리된다.
        if (folder.isDirectory() == false) {//파일이 없을 경우, upload라는 명을 가진 파일만(폴더x) 있을경우
            folder.mkdir();//폴더 생성 메서드
        }


        for (MultipartFile mf : files) { //MultipartFile의 List이므로, 반복문을 통해 저장된 데이터를 가져온다.
            String orname = mf.getOriginalFilename(); //업로드 파일명 가져오기
            if (orname.equals("")) {//업로드하는 파일이 없는 상태
                return; //파일 저장 처리 중지
            }
            //파일 정보를 저장(boardfiletbl 테이블)
            BoardFiles bf = new BoardFiles();
            bf.setBfno(board);
            bf.setForiname(orname);
            String sysname = System.currentTimeMillis()//1970.1.1부터의 밀리초 값
                + orname.substring(orname.lastIndexOf("."));// + 확장자
            bf.setFsysname(sysname);

            //업로드하는 파일을 upload 폴더에 저장
            File file = new File(realPath + sysname);
            //파일 저장(upload폴더)
            mf.transferTo(file);//MultipartFile mf를 file변수에 저장

            //파일 정보를 DB에 저장
            bfRepo.save(bf);
        }//for end
    }//fileUpload() end


    //+ 조회수
    @Transactional
    public void addViewCount(Long bno) {
        log.info("addViewCount()");
        Board board = bRepo.findById(bno).get();
        board.setBview(board.getBview()+1);
        bRepo.save(board);
    }


    ////////////////////////////






} // class end
