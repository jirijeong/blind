package com.jsframe.blind.repository;

import com.jsframe.blind.entity.Board;
import com.jsframe.blind.entity.BoardFiles;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BoardFileRepository
    extends CrudRepository<BoardFiles, Long> {
  //  List<BoardFiles> findByBfid(Board board);







  //suyeop/////
  //게시글(board)에 해당하는 파일목록을 가져오는 메서드(외래키로 검색해야해서, 메서드를 새로 생성)
  List<BoardFiles> findByBfno(Board board); //외래키(bfbid)가 필요하므로 board를 매개변수로 넣음

  //게시글에 해당하는 파일목록 삭제
  List<BoardFiles> deleteByBfno(Board board);
  //////////////
}
