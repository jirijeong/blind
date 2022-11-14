package com.jsframe.blind.repository;

import com.jsframe.blind.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BoardRepository extends CrudRepository<Board, Long> {
















  //////////suyeop

  //카테보리별 조회
  List<Board> findByBcategoryContains(String category, Pageable topicPageable);

  //전체조회
  List<Board> findAll(Pageable topicPageable);

  //조회수 순 조회
  List<Board> findAllByOrderByBviewDesc(Pageable topicPageable);


/////////////////////




}

