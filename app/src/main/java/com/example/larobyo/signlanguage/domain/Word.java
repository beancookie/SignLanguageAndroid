package com.example.larobyo.signlanguage.domain;

public class Word {
    // Id
    private Integer id;
    // 中文名称
    private String chineseName;
    // 拼音
    private String pinYin;
    // 英文名
    private String englishName;
    // 语言描述
    private String description;
    // 图片
    private String pictureLocationPath;
    // 图片url
    private String pictureUrl;

    public Word() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public String getPinYin() {
        return pinYin;
    }

    public void setPinYin(String pinYin) {
        this.pinYin = pinYin;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPictureLocationPath() {
        return pictureLocationPath;
    }

    public void setPictureLocationPath(String pictureLocationPath) {
        this.pictureLocationPath = pictureLocationPath;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    @Override
    public String toString() {
        return "Word{" +
                "id=" + id +
                ", chineseName='" + chineseName + '\'' +
                ", pinYin='" + pinYin + '\'' +
                ", englishName='" + englishName + '\'' +
                ", description='" + description + '\'' +
                ", pictureLocationPath='" + pictureLocationPath + '\'' +
                ", pictureUrl='" + pictureUrl + '\'' +
                '}';
    }
}
