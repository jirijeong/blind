package com.jsframe.blind.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;



@Entity
@Table(name="commenttbl")
@Data
public class Comment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name="c_no")
  private Long cno;

  @ManyToOne
  @JoinColumn(name="m_c_id")
  private Member mcid;

  @ManyToOne
  @JoinColumn(name="m_c_cname")
  private Member mccname;

  @ManyToOne
  @JoinColumn(name="b_c_no")
  private Board bcno;


  @Column(name="c_date")
  @CreationTimestamp
  private Timestamp cdate;


  @Column(name="c_like")
  private int clike;

  @Column(name="c_content", length = 200, nullable = false)
  private String ccontent;

  @Column(name="c_report")
  private int creport;
}
