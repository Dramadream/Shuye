package kiven.com.shuye.bean;

import org.litepal.crud.DataSupport;

/**
 * Author:          Kevin <BR/>
 * CreatedTime:     2018/1/21 14:51 <BR/>
 * Desc:            TODO <BR/>
 * <p/>
 * ModifyTime:      <BR/>
 * ModifyItems:     <BR/>
 */
public class Item extends DataSupport {


    private Long time;// 时间
    private String type;// 种类
    private Integer account;//喝奶或喝水的量
    private boolean paperDiaper;// 换尿裤
    private boolean shit;//拉屎
    private String supplementFood;//辅食

    @Override
    public String toString() {
        return "Item{" +
                "time=" + time +
                ", type='" + type + '\'' +
                ", account=" + account +
                ", paperDiaper=" + paperDiaper +
                ", shit=" + shit +
                ", supplementFood='" + supplementFood + '\'' +
                '}';
    }

    public Item() {
    }

    public Item(Long time, String type, Integer account, boolean paperDiaper, boolean shit, String supplementFood) {
        this.time = time;
        this.type = type;
        this.account = account;
        this.paperDiaper = paperDiaper;
        this.shit = shit;
        this.supplementFood = supplementFood;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getAccount() {
        return account;
    }

    public void setAccount(Integer account) {
        this.account = account;
    }

    public boolean isPaperDiaper() {
        return paperDiaper;
    }

    public void setPaperDiaper(boolean paperDiaper) {
        this.paperDiaper = paperDiaper;
    }

    public boolean isShit() {
        return shit;
    }

    public void setShit(boolean shit) {
        this.shit = shit;
    }

    public String getSupplementFood() {
        return supplementFood;
    }

    public void setSupplementFood(String supplementFood) {
        this.supplementFood = supplementFood;
    }
}
