package com.example.administrator.myschedule;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

//choose　type dialog
public  class MyDialog extends DialogFragment implements View.OnClickListener {
    private ImageView typeAll;
    private ImageView typeStudy;
    private ImageView typeWork;
    private ImageView typeLive;

    @Override
    public void onClick(View v) {
        ItemClickListener listener= (ItemClickListener) getActivity();
        switch (v.getId()) {
            case R.id.type_all:
                listener.onItemClickListener("所有");
                getDialog().dismiss();
                break;
            case R.id.type_study:
                listener.onItemClickListener("学习");
                getDialog().dismiss();
                break;
            case R.id.type_work:
                listener.onItemClickListener("工作");
                getDialog().dismiss();
                break;
            case R.id.type_live:
                listener.onItemClickListener("生活");
                getDialog().dismiss();
                break;
        }
    }

    //接口返回数据给Activity刷新ui
    public interface ItemClickListener {
        void onItemClickListener(String type);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new
                ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.type_choose_dialog, container);
        typeAll = (ImageView) view.findViewById(R.id.type_all);
        typeStudy = (ImageView) view.findViewById(R.id.type_study);
        typeWork = (ImageView) view.findViewById(R.id.type_work);
        typeLive = (ImageView) view.findViewById(R.id.type_live);
        typeAll.setOnClickListener(this);
        typeStudy.setOnClickListener(this);
        typeWork.setOnClickListener(this);
        typeLive.setOnClickListener(this);
        return view;
    }

}