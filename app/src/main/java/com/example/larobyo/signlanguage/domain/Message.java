package com.example.larobyo.signlanguage.domain;

import java.util.Date;

public class Message {
    // Id
    private Integer id;
    // 创建时间
    private Date createDate;
    // 留言内容
    private String content;
    // 联系邮箱
    private String email;

    public Message() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
