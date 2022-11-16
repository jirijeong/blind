package com.jsframe.blind.service;

import com.jsframe.blind.entity.Board;
import com.jsframe.blind.entity.BoardFiles;
import com.jsframe.blind.entity.Member;
import com.jsframe.blind.repository.BoardFileRepository;
import com.jsframe.blind.repository.BoardRepository;
import com.jsframe.blind.repository.MemberRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

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
  public ModelAndView getBoard(long bno) {
    log.info("getBoard()");
    mv = new ModelAndView();
    mv.setViewName("detail");

    //게시글을 DB에서 가져와서 담기
    Board board = bRepo.findById(bno).get();

    mv.addObject("board", board);


    //첨부파일(목록) 가져와서 담기(selet * from boardfiletbl where bfbid = ?)


    //bfbid로 검색해야하는데 자동완성되지 않으므로, 직접 메서드 생성
    List<BoardFiles> bfList = bfRepo.findByBfno(board);
    mv.addObject("bfList", bfList);


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

    Pageable bestPageable = PageRequest.of(pageNum - 1, listCnt);
    List<Board> topicList;


    //오늘 날짜 구하기
    Date today = new Date();
    Timestamp now = new Timestamp(today.getTime());
    Timestamp sixHoursAgo = new Timestamp(today.getTime() - (6 * 60 * 60 * 1000));
    Timestamp weekAgo = new Timestamp(today.getTime() - (7 * 24 * 60 * 60 * 1000));
    Board todayBest = null;
    Board weekBest = null;

    switch (category) {
      case "전체":
        topicList = bRepo.findAll(topicPageable);
        break;
      case "베스트":
        topicList = bRepo.findAllWeekBest(now, weekAgo, bestPageable);
        break;
      default:
        topicList = bRepo.findByBcategoryContains(category, topicPageable);
        todayBest = bRepo.findNowBest(category, now, sixHoursAgo);
        weekBest = bRepo.findWeekBest(category, now, weekAgo);

        if (pageNum == 1) {
          if (todayBest != null && weekBest == null) {
            topicList.add(0, todayBest);
          } else if (todayBest == null && weekBest != null) {
            topicList.add(0, weekBest);
          } else if (todayBest != null && weekBest != null) {
            topicList.add(0, todayBest);
            topicList.add(1, weekBest);
          }
        }
    }


    session.setAttribute("category", category);

    return maskingName(topicList);
  }

  //유저 id 가리기
  private List<Board> maskingName(List<Board> topicList) {

    for (Board b : topicList) {
      Member user = b.getMbid();
      String userId = b.getMbid().getMid();
      String maskedId = userId.substring(0, 1);

      for (int i = 1; i < userId.length(); i++) {
        maskedId += "*";
      }
      user.setMid(maskedId);
    }

    return topicList;
  }


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

//      for (int i = 0; i < 100; i++) {
//
//      Member mem = new Member();
//      mem.setMid("유저"+i);
//      mem.setMcname("회사명" +i);
//      mem.setMemail("유저" + i + "@email.com");
//      mem.setMpwd("0000");
//
//      mRepo.save(mem);
//      Board board1 = new Board();
//      board1.setMbid(mem);
//      board1.setBcategory("블라블라");
//      board1.setBcontent("내용" + i);
//      board1.setBtitle("제목"+i);
//      board1.setBlike(0);
//      board1.setBreport(0);
//      board1.setBreport(0);
//      board1.setBcomment(0);
//      board1.setBview(i);
//      bRepo.save(board1);
//
//
//      }


      bRepo.save(board);
      fileUpload(files, session, board);

      view = "redirect:/";
      msg = "저장 완료";

    } catch (Exception e) {
      e.printStackTrace();
      view = "redirect:write";
      msg = "저장 실패";
    }
    rttr.addFlashAttribute("msg", msg);
    return view;
  }

  //파일업로드
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
      System.out.println("realPath = " + realPath);
      //파일 정보를 DB에 저장
      bfRepo.save(bf);
    }//for end
  }//fileUpload() end


  //조회수 ++
  @Transactional
  public void addViewCount(Long bno, HttpSession session) {
    log.info("addViewCount()");
    Board board = bRepo.findById(bno).get();

    session.setAttribute("board", board);

    board.setBview(board.getBview() + 1);
    bRepo.save(board);
  }


  //게시글 수정
  @Transactional
  public String boardUpdate(List<MultipartFile> files,
                            Board board,
                            HttpSession session,
                            RedirectAttributes rttr) {
    log.info("boardUpdate()");
    String msg = null;
    String view = null;
    try {
      bRepo.save(board); //insert, update 겸용(pk에 해당하는 값이 있다면, update, 없으면 insert)

      fileUpload(files, session, board);//신규 파일 업로드

      msg = "수정 성공";
      view = "redirect:detail?bnum=" + board.getBno();
    } catch (Exception e) {
      e.printStackTrace();
      msg = "수정 실패";
      view = "redirect:updateFrm?bnum" + board.getBno();
    }
    rttr.addFlashAttribute("msg", msg);
    return view;
  }


  ////////////////////////////

  //파일 다운로드 처리) //org.springframework.core.io.Resource
  public ResponseEntity<Resource> fileDownload(BoardFiles bfile, HttpSession session) throws IOException {
    log.info("fileDownload()");
    //파일 저장 경로 구하기
    String realpath = session.getServletContext().getRealPath("/");
    //DispatcherServlet에는 session이 동작하는데 필요한 정보(context)를 함께 가지고 있으므로, ServletContext에서 루트경로(~/webapp까지의 경로)를 구할 수 있다.
    realpath += "upload/" + bfile.getFsysname();


    //resource를 관리하는 inputstream으로 통로 연결. // 통로만 연결하는 inputstream도있고, 관련 처리를 함께 하는 inputstream도 있따.
    InputStreamResource fResource = new InputStreamResource(new FileInputStream(realpath));

    //original file name이 한글일 경우 제대로 동작하지않을 수도 있으므로 utf-8로 인코딩 처리
    //비영어권 언어를 사용할 경우, 인코딩은 필수로 처리한다.
    String fileName = URLEncoder.encode(bfile.getForiname(), "UTF-8");

    return ResponseEntity.ok() //- 상태코드 200을 처리하는 메서드
        .contentType(MediaType.APPLICATION_OCTET_STREAM) //통신으로 전송되는 데이터(application에서 만들어진 데이터)인데, 8비트로 된 데이터(바이너리 데이터)의 파일임을 contentType으로 지정
        .cacheControl(CacheControl.noCache())//캐시로 처리하지 않도록 지정(임시저장하지 않도록)
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
        //서버에서 넘어오는 데이터는 파일덩어리가 아니라 비트열로 넘어오는데,
        // 어디부터 어디까지가 파일인지를 패킷에 담아 보내준다.
        // 이걸 모아서 하나의 파일로 완성하는데, 비트열로 넘어온 데이터라서 따로 이름 담겨있진 않다.
        // 그래서 파일이름을 다시 지정해줘야한다.
        .body(fResource); //파일의 실제 데이터
  }


  //게시글 및 관련 파일 삭제
  @Transactional
  public String boardDelete(long bno,
                            HttpSession session,
                            RedirectAttributes rttr) {

    log.info("boardDelete()");
    String msg = null;
    String view = null;
    Board board = new Board();
    board.setBno(bno);

    String realPath = session.getServletContext().getRealPath("/");
    realPath += "upload/";

    List<BoardFiles> bfList = bfRepo.findByBfno(board);


    try {
      //파일 삭제
      for (BoardFiles bf : bfList) {
        String delPath = realPath + bf.getFsysname();
        File file = new File(delPath);

        if (file.exists()) {
          file.delete();//파일이 있으면 삭제
        }
      }

      //파일 정보 삭제
      bfRepo.deleteByBfno(board);
      //게시글 삭제
      bRepo.deleteById(bno);
      msg = "삭제 성공";
      view = "redirect:/";
    } catch (Exception e) {
      e.printStackTrace();
      msg = "삭제 실패";
      view = "redirect:detail?bno=" + bno;
    }
    rttr.addFlashAttribute("msg", msg);

    return view;
  }


} // class end
