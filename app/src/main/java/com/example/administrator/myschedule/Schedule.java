package com.example.administrator.myschedule;

/**
 * 临时data
 */
public class Schedule {
    //日期 e.g 2016122
    private int  day;
    //时间  e.g 1022
    private int  time;
    //type类型
    private String type;
    //计划内容
    private String scheduleText;
    //id
    private int id;
    //完成是已经完成，默认没完成，为0；
    private int finish=0;

    public int getFinish() {
        return finish;
    }

    public void setFinish(int finish) {
        this.finish = finish;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getScheduleText() {
        return scheduleText;
    }

    public void setScheduleText(String scheduleText) {
        this.scheduleText = scheduleText;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
