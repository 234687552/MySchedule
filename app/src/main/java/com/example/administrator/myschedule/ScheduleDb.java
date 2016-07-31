package com.example.administrator.myschedule;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 一个DB包含一个表包含所有的记录
 * 保存数据到sql数据库
 * 保存到表、查询表
 * id 是每一个Item的标识
 */
public class ScheduleDb {
    //数据库名称
    private static final String DB_NAME = "my_schedule";
    //数据库版本
    private static final int VERSION = 1;
    private SQLiteDatabase db;

    public ScheduleDb(Context context) {
        //获取openHelper实例开始创建表
        ScheduleDbOpener dbOpener = new ScheduleDbOpener(context, DB_NAME, null, VERSION);
        db = dbOpener.getWritableDatabase();
    }

    /**
     * 新增一个数据
     */
    public void saveSchedule(Schedule schedule) {
        ContentValues values = new ContentValues();
        values.put("finish",schedule.getFinish());
        values.put("time", schedule.getTime());
        values.put("day", schedule.getDay());
        values.put("schedule_text", schedule.getScheduleText());
        values.put("type", schedule.getType());

        db.insert("Schedule", null, values);
        values.clear();
    }

    /**
     * 根据日子，类型读取某日数据整组清单
     */
    public List<Schedule> readSchedule(int day, String type) {
        List<Schedule> scheduleList = new ArrayList<Schedule>();
        if (type.equals("所有")) {
            Cursor cursor = db
                    .query("Schedule", null, "day = ? ", new String[]{String.valueOf(day)}, null, null, "day,time");
            if (cursor.moveToFirst()) {//一行取完结束进行如下操作
                do {
                    Schedule schedule = new Schedule();
                    schedule.setFinish(cursor.getInt(cursor.getColumnIndex("finish")));
                    schedule.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    schedule.setDay(cursor.getInt(cursor.getColumnIndex("day")));
                    schedule.setTime(cursor.getInt(cursor.getColumnIndex("time")));
                    schedule.setType(cursor.getString(cursor.getColumnIndex("type")));
                    schedule.setScheduleText(cursor.getString(cursor.getColumnIndex("schedule_text")));
                    scheduleList.add(schedule);
                } while (cursor.moveToNext());
            }
        } else {
            Cursor cursor = db
                    .query("Schedule", null, "day = ? and type = ? ", new String[]{String.valueOf(day), type}, null, null, "day,time");
            if (cursor.moveToFirst()) {//一行取完结束进行如下操作
                do {
                    Schedule schedule = new Schedule();
                    schedule.setFinish(cursor.getInt(cursor.getColumnIndex("finish")));
                    schedule.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    schedule.setDay(cursor.getInt(cursor.getColumnIndex("day")));
                    schedule.setTime(cursor.getInt(cursor.getColumnIndex("time")));
                    schedule.setType(cursor.getString(cursor.getColumnIndex("type")));
                    schedule.setScheduleText(cursor.getString(cursor.getColumnIndex("schedule_text")));
                    scheduleList.add(schedule);
                } while (cursor.moveToNext());
            }
        }
        return scheduleList;
    }

    /**
     * 根据id更新某一计划Item并保存
     */
    public void updateSchedule(int id, Schedule newSchedule) {
        ContentValues values = new ContentValues();
        values.put("finish",newSchedule.getFinish());
        values.put("time", newSchedule.getTime());
        values.put("day", newSchedule.getDay());
        values.put("schedule_text", newSchedule.getScheduleText());
        values.put("type", newSchedule.getType());
        db.update("Schedule", values, "id = ?",new String[]{String.valueOf(id)} );
        values.clear();
    }

    /**
     * 根据id删除某一计划Item
     */
    public void deletedSchedule(int id) {
        db.delete("Schedule", "id = ?", new String[]{String.valueOf(id)});
    }

    public void  deletedAll(){
        db.delete("Schedule", null, null);
    }


    /**
     * 获取所有列表
     */
    public List<Schedule> readSchedule() {
        List<Schedule> scheduleList = new ArrayList<Schedule>();
        Cursor cursor = db
                .query("Schedule", null, null, null, null, null, "day,time");
        if (cursor.moveToFirst()) {//一行取完结束进行如下操作
            do {
                Schedule schedule = new Schedule();
                schedule.setFinish(cursor.getInt(cursor.getColumnIndex("finish")));
                schedule.setId(cursor.getInt(cursor.getColumnIndex("id")));
                schedule.setDay(cursor.getInt(cursor.getColumnIndex("day")));
                schedule.setTime(cursor.getInt(cursor.getColumnIndex("time")));
                schedule.setType(cursor.getString(cursor.getColumnIndex("type")));
                schedule.setScheduleText(cursor.getString(cursor.getColumnIndex("schedule_text")));
                scheduleList.add(schedule);
            } while (cursor.moveToNext());
        }

        return scheduleList;
    }
    /**
     * 获取所有天数
     */
    public List<Integer> getAllDay() {

        List<Integer> dayList = new ArrayList<Integer>();
        Set<Integer> daySet=new LinkedHashSet<Integer>();
        Cursor cursor = db
                .query("Schedule", null, null, null, null, null, "day,time");
        if (cursor.moveToFirst()) {//一行取完结束进行如下操作
            do {
                daySet.add(cursor.getInt(cursor.getColumnIndex("day")));
            } while (cursor.moveToNext());
        }
        dayList.addAll(daySet);
        return dayList;
    }

    /**
     * 获取某一天已完成数和总数
     */
    public String countFinish(int day){
        List<Integer> dayList = new ArrayList<Integer>();
        List<Integer> finishList = new ArrayList<Integer>();
        Cursor cursorDay = db
                .query("Schedule", null, "day = ? ", new String[]{String.valueOf(day)}, null, null, "day,time");
        if (cursorDay.moveToFirst()) {//一行取完结束进行如下操作
            do {
                dayList.add(cursorDay.getInt(cursorDay.getColumnIndex("day")));
            } while (cursorDay.moveToNext());
        }

        Cursor cursorFinish = db
                .query("Schedule", null, "day = ? and finish = ? ", new String[]{String.valueOf(day),String.valueOf(1)}, null, null, "day,time");
        if (cursorFinish.moveToFirst()) {//一行取完结束进行如下操作
            do {
                finishList.add(cursorFinish.getInt(cursorFinish.getColumnIndex("day")));
            } while (cursorFinish.moveToNext());
        }
        return (finishList.size()+"/"+dayList.size());
    }




    private class ScheduleDbOpener extends SQLiteOpenHelper {
        //创建schedule表
        String CREATE_SCHEDULE = "create table Schedule (" +
                "id integer primary key autoincrement," +
                "day integer,"+
                "time integer,"+
                "finish integer,"+
                "schedule_text text," +
                "type text)";

        public ScheduleDbOpener(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_SCHEDULE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop if table exists Schedule");
            onCreate(db);
        }
    }

}
