package com.example.administrator.myschedule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity implements MyDialog.ItemClickListener {
    private ScheduleDb scheduleDb;

    //记录以前选好显示的计划类型
    private String chooseType = "所有";
    //新建按钮
    private ImageView imgNew;
    //主页面的计划列表list
    private ListView lstSchedule;
    private ScheduleAdapter scheduleAdapter;
    private List<Schedule> scheduleList = new ArrayList<Schedule>();
    //滑动两个页面容器
    private ViewPager viewPager;
    private List<View> pageViews;
    //历史页面的计划列表
    private ExpandableListView expandableList;
    private List<Integer> dayList = new ArrayList<Integer>();
    private HistoryAdapter historyAdapter;
    //接受Message刷新列表
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            refreshSchedule(todayDate, chooseType);
            super.handleMessage(msg);
        }
    };
    //当天时间
    private int todayDate = 0;
    private ImageView imageHistory;
    private TextView textHistory;
    private ImageView imageToday;
    private TextView textToday;
    private MyDialog myDialog;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Intent mIntent = getIntent();
        Schedule schedule = new Schedule();
        schedule.setType(mIntent.getStringExtra("type"));
        schedule.setDay(mIntent.getIntExtra("day", 0));
        schedule.setScheduleText(mIntent.getStringExtra("schedule_text"));
        schedule.setTime(mIntent.getIntExtra("time", 0));
        if (mIntent.getBooleanExtra("isNew", false)) {
            //新增加的返回的保存进数据库
            scheduleDb.saveSchedule(schedule);
        } else {
            //不是新增加的更新到数据库
            scheduleDb.updateSchedule(mIntent.getIntExtra("id", 0), schedule);
        }
        viewPager.setCurrentItem(1);
        refreshSchedule(todayDate, chooseType);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_page);
        getSupportActionBar().hide();//隐藏标题栏
        //防止多个activity绑定Fragment导致溢出，所以保证只有一个MyDialog
        myDialog = new MyDialog();
        //获取当天日期
        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH) + 1;
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        todayDate = mYear * 10000 + mMonth * 100 + mDay;
        //新建按钮
        imgNew = (ImageView) findViewById(R.id.img_new);
        imgNew.getBackground().setAlpha(130); //ImageView 半透明
        imgNew.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                myDialog.show(getFragmentManager(), "MyDialog");
                return true;
            }
        });
        //页面上面的变色
        imageHistory = (ImageView) findViewById(R.id.image_history);
        textHistory = (TextView) findViewById(R.id.text_history);
        imageToday = (ImageView) findViewById(R.id.image_today);
        textToday = (TextView) findViewById(R.id.text_today);

        //获取数据库操作
        scheduleDb = new ScheduleDb(this);

        /**
         *设置页面滑动容器
         */
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        View view1 = LayoutInflater.from(this).inflate(R.layout.history_list, null);
        View view2 = LayoutInflater.from(this).inflate(R.layout.today_list, null);
        pageViews = new ArrayList<View>();
        pageViews.add(view1);
        pageViews.add(view2);
        PagerAdapter pagerAdapter = new PagerAdapter() {

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ((ViewPager) container).addView(pageViews.get(position));
                return pageViews.get(position);
            }
        };
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {  //1代表的是今天列表
                    //新建图标的动画
//                    imgNew.setImageResource(R.mipmap.new_button2);
                    RotateAnimation animation = new RotateAnimation(0f, 90f, Animation.RELATIVE_TO_SELF,
                            0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    animation.setDuration(200);
                    animation.setFillAfter(true);
                    imgNew.setAnimation(animation);

                    imageHistory.setImageResource(R.mipmap.history_default);
                    textHistory.setTextColor(Color.parseColor("#d0d0d0"));
                    imageToday.setImageResource(R.mipmap.today_selected);
                    textToday.setTextColor(Color.parseColor("#03a9f4"));
                } else {     //0代表的是历史列表

                    //新建图标的动画
//                    imgNew.setImageResource(R.mipmap.new_button1);
                    RotateAnimation animation = new RotateAnimation(90f, 0f, Animation.RELATIVE_TO_SELF,
                            0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    animation.setDuration(200);
                    animation.setFillAfter(true);
                    imgNew.setAnimation(animation);

                    imageHistory.setImageResource(R.mipmap.history_selected);
                    textHistory.setTextColor(Color.parseColor("#03a9f4"));
                    imageToday.setImageResource(R.mipmap.today_default);
                    textToday.setTextColor(Color.parseColor("#d0d0d0"));
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        viewPager.setCurrentItem(1);//默认进入在今天页面


        /**
         *设置今天页面计划列表
         */

        //判断是否第一次打开
        isFirstTime();
        scheduleList = scheduleDb.readSchedule();
        scheduleAdapter = new ScheduleAdapter(this, R.layout.schedule_list_item, scheduleList);
        lstSchedule = (ListView) view2.findViewById(R.id.schedule_list);
        lstSchedule.setAdapter(scheduleAdapter);


        /**
         *设置历史页面计划列表
         */
        expandableList = (ExpandableListView) view1.findViewById(R.id.expandable_list);
        expandableList.setGroupIndicator(null);
        historyAdapter = new HistoryAdapter();
        expandableList.setAdapter(historyAdapter);

        //刷新今天和历史数据
        refreshSchedule(todayDate, chooseType);
    }

    private void isFirstTime() {
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        boolean isFirstOpen = pref.getBoolean("isFirstOpen", true);
        if (isFirstOpen) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("isFirstOpen", false);
            editor.commit();
            Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();

            Schedule schedule = new Schedule();
            schedule.setDay(todayDate);
            schedule.setType("工作");
            schedule.setScheduleText("向右滑查看所有列表");
            scheduleDb.saveSchedule(schedule);
            schedule.setType("生活");
            schedule.setScheduleText("向左滑切换完成/未完成");
            scheduleDb.saveSchedule(schedule);
            schedule.setType("学习");
            schedule.setScheduleText("单击修改查看该计划");
            scheduleDb.saveSchedule(schedule);
            schedule.setType("工作");
            schedule.setScheduleText("长按直接删除该条计划");
            scheduleDb.saveSchedule(schedule);
            schedule.setType("生活");
            schedule.setScheduleText("点击下面+号新建计划");
            scheduleDb.saveSchedule(schedule);
            schedule.setType("学习");
            schedule.setScheduleText("长按下面+号分类查看");
            scheduleDb.saveSchedule(schedule);
        }
    }

    //新建计划事件
    public void NewSchedule(View view) {
        Intent intent = new Intent(MainActivity.this, TextActivity.class);
        intent.putExtra("isNew", true);
        startActivity(intent);
        overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
    }

    //通过日期更新
    private void refreshSchedule(int day, String type) {
        dayList.clear();
        dayList = scheduleDb.getAllDay();
        scheduleList.clear();
        scheduleList.addAll(scheduleDb.readSchedule(day, type));
        historyAdapter.notifyDataSetChanged();
        scheduleAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClickListener(String type) {
        chooseType=type;
        refreshSchedule(todayDate, type);
    }


    //历史列表ExpandableList的adapter
    private class HistoryAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return dayList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView parentView = new TextView(MainActivity.this);
            parentView.setText(dayList.get(groupPosition) / 10000 + "年" + dayList.get(groupPosition) % 10000 / 100 + "月" + dayList.get(groupPosition) % 10000 % 100 + "日");
            RelativeLayout rl = new RelativeLayout(MainActivity.this);
            rl.setPadding(20, 20, 20, 20);
            rl.addView(parentView);

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            TextView sizeText = new TextView(MainActivity.this);
            sizeText.setText(scheduleDb.countFinish(dayList.get(groupPosition)));
            sizeText.setLayoutParams(lp);
            rl.addView(sizeText);
            return rl;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            MyListView list = new MyListView(MainActivity.this);
            List<Schedule> historyList = new ArrayList<Schedule>();
            historyList = scheduleDb.readSchedule(dayList.get(groupPosition), "所有");
            ScheduleAdapter adapter = new ScheduleAdapter(MainActivity.this, R.layout.schedule_list_item, historyList);
            adapter.isHistory = true;
            list.setAdapter(adapter);
            list.setPadding(80, 0, 40, 0);
            return list;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        //重写listView 让childrenView就是一个listView
        private class MyListView extends ListView {
            public MyListView(Context context) {
                super(context);
            }

            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                        MeasureSpec.AT_MOST);
                super.onMeasure(widthMeasureSpec, expandSpec);
            }
        }
    }

    //今天列表ScheduleList和历史列表展开列表LIst的adapter
    private class ScheduleAdapter extends ArrayAdapter<Schedule> {
        private int resourceId;
        public boolean isHistory = false;
        private float cx;
        private boolean isLongClick = false;

        public ScheduleAdapter(Context context, int resource, List<Schedule> objects) {
            super(context, resource, objects);
            resourceId = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Schedule schedule = getItem(position);//获取当前项的schedule实例
            final View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//每一项都加载我们处理好的布局
            LinearLayout ll = (LinearLayout) view.findViewById(R.id.ll);
            if (isHistory) {
                ll.setBackgroundResource(R.drawable.bg_history_item_coners);
            }
            final TextView scheduleText = (TextView) view.findViewById(R.id.schedule_text);
            final TextView scheduleType = (TextView) view.findViewById(R.id.schedule_type);
            final TextView scheduleTime = (TextView) view.findViewById(R.id.schedule_time);
            scheduleText.setText(schedule.getScheduleText());
            scheduleType.setText(schedule.getType());
            String hour = schedule.getTime() / 100 > 9 ? (schedule.getTime() / 100 + "") : ("0" + schedule.getTime() / 100);
            String min = schedule.getTime() % 100 > 9 ? (schedule.getTime() % 100 + "") : ("0" + schedule.getTime() % 100);
            scheduleTime.setText(hour + ":" + min);
            if (schedule.getFinish() == 1) {
                if (isHistory) {
                    scheduleText.setTextColor(Color.parseColor("#D0D0D0"));
                    scheduleType.setTextColor(Color.parseColor("#D0D0D0"));
                    scheduleTime.setTextColor(Color.parseColor("#D0D0D0"));
                } else {
                    scheduleText.setTextColor(Color.parseColor("#EAEAEE"));
                    scheduleType.setTextColor(Color.parseColor("#EAEAEE"));
                    scheduleTime.setTextColor(Color.parseColor("#EAEAEE"));
                }
            }
            /**
             * 监听list每一个Item的长按事件
             */
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!isLongClick) {
                        isLongClick = true;
                        scheduleDb.deletedSchedule(schedule.getId());
                        view.setBackgroundColor(Color.parseColor("#03a9f4"));

                        handler.sendMessage(handler.obtainMessage());
                    }
                    return false;
                }
            });
            /**
             * 监听list每一个Item的滑动事件
             */
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEvent.ACTION_DOWN == event.getAction()) {
                        //记录初始touch位置
                        cx = event.getX();
                    } else if (MotionEvent.ACTION_UP == event.getAction()) {

                        //为每一个Item设置单击修改。
                        if (!isLongClick) {
                            Intent intent = new Intent(MainActivity.this, TextActivity.class);
                            intent.putExtra("id", schedule.getId());
                            //把Schedule所有内容都传过去，Intent测试可传输大小为1M，每个汉字占2B（两个字节）
                            intent.putExtra("type", schedule.getType());
                            intent.putExtra("time", schedule.getTime());
                            intent.putExtra("day", schedule.getDay());
                            intent.putExtra("schedule_text", schedule.getScheduleText());
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//从右向左进入动作
                        } else
                            isLongClick = false;

                    } else if (MotionEvent.ACTION_CANCEL == event.getAction()) {
                        //今天页面支持左滑，历史页面右滑 完成/未完成切换
                        cx = event.getX() - cx;//相对位移
                        if (!isLongClick) {
                            if (isHistory) {
                                //判断左滑还是右滑，数值是靠实验得到的
                                if (cx > 100) {
                                    schedule.setFinish(schedule.getFinish() == 1 ? 0 : 1);
                                }
                            } else {
                                if (cx < -20) {
                                    schedule.setFinish(schedule.getFinish() == 1 ? 0 : 1);
                                }
                            }

                            scheduleDb.updateSchedule(schedule.getId(), schedule);
                            //发送message刷新列表

                            handler.sendMessage(handler.obtainMessage());
                        } else
                            isLongClick = false;

                    }

                    return false;
                }
            });
            return view;
        }
    }


}








