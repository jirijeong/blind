package com.jsframe.blind.repository;

import com.jsframe.blind.entity.Board;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;
import java.util.List;

public interface BoardRepository extends CrudRepository<Board, Long> {
















  //////////suyeop

  //카테고리별 조회
  List<Board> findByBcategoryContains(String category, Pageable topicPageable);

  //전체조회
  List<Board> findAll(Pageable topicPageable);

  //조회수 순 조회
  @Query(value = "SELECT * FROM boardtbl WHERE b_date BETWEEN :weekAgo AND :now ORDER BY b_view DESC",
      nativeQuery = true )
  List<Board> findAllWeekBest(Timestamp now,Timestamp weekAgo, Pageable bestPageable);



  @Query(value = "SELECT * FROM boardtbl WHERE b_category=:category " +
      "AND b_view=(SELECT MAX(b_view) FROM boardtbl " +
      "WHERE  b_category=:category AND b_date BETWEEN :sixHoursAgo AND :now) LIMIT 1",
      nativeQuery = true )
  Board findNowBest(String category, Timestamp now, Timestamp sixHoursAgo);



  @Query(value = "SELECT * FROM boardtbl WHERE b_category=:category " +
      "AND b_view=(SELECT MAX(b_view) FROM boardtbl " +
      "WHERE  b_category=:category AND b_date BETWEEN :weekAgo AND :now) LIMIT 1"
      , nativeQuery = true )
  Board findWeekBest(String category, Timestamp now, Timestamp weekAgo);





/////////////////////




}

