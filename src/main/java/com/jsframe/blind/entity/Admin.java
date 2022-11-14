package com.jsframe.blind.entity;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="admintbl")
@Data
public class Admin {
  @Id
  @Column(name="a_id")
  private String aid;

  @Column(name="a_pwd", nullable = false, length = 100)
  private String apwd;
}
