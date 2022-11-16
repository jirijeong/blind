package com.jsframe.blind.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "boardtbl")
@Data
public class Board {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name="b_no")
  private Long bno;

  @ManyToOne
  @JoinColumn(name = "m_b_id")
  private Member mbid;

  @Column(name = "b_title", nullable = false, length = 50)
  private String btitle;

  @Column(name = "b_content", length = 1000)
  private String bcontent;

  @Column(name = "b_category", nullable = false, length = 20)
  private String bcategory;

  @Column(name = "b_update")
  @UpdateTimestamp
  private Timestamp bupdate;

  @Column(name = "b_view" ,columnDefinition="default 0")
  private int bview;

  @Column(name = "b_like" ,columnDefinition="default 0")
  private int blike;

  @Column(name = "b_report" ,columnDefinition="default 0")
  private int breport;

  @Column(name = "b_comment" ,columnDefinition="default 0")
  private int bcomment;

  @Column(name = "b_date")
  @CreationTimestamp
  private Timestamp bdate;


}
