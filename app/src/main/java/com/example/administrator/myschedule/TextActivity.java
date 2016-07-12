package com.example.administrator.myschedule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Administrator on 2016/6/30 0030.
 */
public class TextActivity extends Activity implements View.OnClickListener {
    private TextView chooseTime;
    private TextView chooseDate;
    //时间选择
    private TimePicker timePicker;
    //日期选择
    private DatePicker dataPicker;
    //计划类型
    private Spinner editType;
    //计划正文
    private EditText editSchedule;
    private TextView editDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_text);

        editType = (Spinner) findViewById(R.id.edit_type);
        editSchedule = (EditText) findViewById(R.id.edit_schedule);
        timePicker = (TimePicker) findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);
        dataPicker = (DatePicker) findViewById(R.id.data_picker);
        chooseTime = (TextView) findViewById(R.id.btn_time);
        chooseDate = (TextView) findViewById(R.id.btn_date);
        editDate = (TextView) findViewById(R.id.edit_date);



        chooseDate.setOnClickListener(this);
        chooseTime.setOnClickListener(this);


        //判断是否为新建，否则显示当前内容
        Intent intent = getIntent();
        if (!intent.getBooleanExtra("isNew", false)) {
            if (intent.getStringExtra("type")!=null){
                switch (intent.getStringExtra("type")) {
                    case "工作":
                        editType.setSelection(0);
                        break;
                    case "学习":
                        editType.setSelection(1);
                        break;
                    case "生活":
                        editType.setSelection(2);
                        break;
                }
            }

            editSchedule.setText(intent.getStringExtra("schedule_text"));
            editSchedule.setSelection(intent.getStringExtra("schedule_text").length());
            int day = intent.getIntExtra("day", 0);
            editDate.setText(day / 10000 + "年" + (day % 10000 / 100) + "月" + day % 10000 % 100 + "日");
            dataPicker.init(day / 10000, day % 10000 / 100 - 1, day % 10000 % 100, new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    editDate.setText(year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日");
                }
            });
            int time = intent.getIntExtra("time", 0);
            timePicker.setCurrentHour(time / 100);
            timePicker.setCurrentMinute(time % 100);

        }else {
            //新建时初始化title默认时间
            Calendar calendar = Calendar.getInstance();
            int mYear = calendar.get(Calendar.YEAR);
            int mMonth = calendar.get(Calendar.MONTH) + 1;
            int mDay = calendar.get(Calendar.DAY_OF_MONTH);
            editDate.setText(mYear + "年" + mMonth + "月" + mDay + "日");
            //随后跟随选择器改变
            dataPicker.init(mYear, mMonth-1, mDay, new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    editDate.setText(year + "年" + (monthOfYear-1) + "月" + dayOfMonth + "日");
                }
            });
        }

    }

    public void Back(View view) {
        finish();
    }

    public void Save(View view) {
        if (editSchedule.getText().length()<=0){
            Toast.makeText(TextActivity.this, "请输入具体事情", Toast.LENGTH_SHORT).show();
        }else {
            int inResourceId=R.anim.push_down_in;
            int outResourceId=R.anim.push_down_out;
            Intent intent = new Intent(TextActivity.this,MainActivity.class);
            if (!getIntent().getBooleanExtra("isNew",false)){
                intent.putExtra("isNew",false);
                intent.putExtra("id", getIntent().getIntExtra("id", 0));
                inResourceId=R.anim.slide_in_left;
                outResourceId=R.anim.slide_out_right;
            }else
                intent.putExtra("isNew",true);
            intent.putExtra("schedule_text", editSchedule.getText().toString());
            intent.putExtra("day", dataPicker.getYear() * 10000 + (dataPicker.getMonth() + 1) * 100 + dataPicker.getDayOfMonth());
            intent.putExtra("time", timePicker.getCurrentHour() * 100 + timePicker.getCurrentMinute());
            intent.putExtra("type", editType.getSelectedItem().toString());
            startActivity(intent);
            overridePendingTransition(inResourceId, outResourceId);
            finish();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_date:
                timePicker.setVisibility(View.GONE);
                dataPicker.setVisibility(View.VISIBLE);
                chooseDate.setText("设置日期：");
                chooseTime.setText("◀设置日期");
                break;
            case R.id.btn_time:
                timePicker.setVisibility(View.VISIBLE);
                dataPicker.setVisibility(View.GONE);
                chooseDate.setText("设置日期▶");
                chooseTime.setText("设置时间：");
                break;
        }
    }

}
