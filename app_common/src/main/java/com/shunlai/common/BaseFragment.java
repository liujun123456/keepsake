package com.shunlai.common;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

/**
 * @author Liu
 * @Date 2021/4/1
 * @mobile 18711832023
 */
public abstract class BaseFragment extends Fragment {
    public Context mContext;
    private Toolbar toolBar;
    private LinearLayout barLayout;
    private RelativeLayout contentView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext=getActivity();
        View view=inflater.inflate(R.layout.activity_base, null);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        afterView();
    }

    private void initView(View mView){
        toolBar = mView.findViewById(R.id.base_toolbar);
        contentView = mView.findViewById(R.id.main_view);
        barLayout = mView.findViewById(R.id.base_barlayout);
        AppCompatActivity mActivity = (AppCompatActivity)getActivity();
        mActivity.setSupportActionBar(toolBar);
        if (createTitle() == 0) {
            barLayout.setVisibility(View.GONE);
        }else {
            View titleView = View.inflate(mContext, createTitle(), null);
            titleView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            toolBar.addView(titleView);
            toolBar.setBackgroundColor(setTitleColor());
        }

        if (createView() == 0){
            throw new RuntimeException("contentView can not be null!");
        }else {
            View mainView = View.inflate(mContext, createView(), null);
            mainView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            contentView.addView(mainView);
        }

    }
    public int  setTitleColor(){
        return R.color.defaultColor;
    }
    public abstract int createView();
    public abstract int createTitle();
    public abstract void  afterView();
}
