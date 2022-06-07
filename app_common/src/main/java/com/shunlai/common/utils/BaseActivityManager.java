package com.shunlai.common.utils;


import android.app.Activity;

import java.util.Stack;

public class BaseActivityManager {
    private Stack<Activity> activityStack;
    private static BaseActivityManager instance;

    private BaseActivityManager() {
        activityStack = new Stack<Activity>();
    }

    public static BaseActivityManager getInstance() {
        if (null == instance)
            instance = new BaseActivityManager();
        return instance;
    }

    /**
     * 添加Activity到堆栈中
     *
     * @param activity
     * @return
     */
    public boolean addActivity(Activity activity) {
        if (null == activity)
            return false;
        return activityStack.add(activity);
    }

    /**
     * 获取当前activity
     *
     * @return
     */
    public Activity currentActivity() {
        return activityStack.lastElement();
    }

    /**
     * 查询栈内是否有指定Activity
     *
     * @return boolean
     */
    public boolean findActivityForCls(Class<? extends Activity> cls) {
        int stackSize = activityStack.size();
        for (int index = stackSize - 1; index > -1; index--) {
            Activity activity = activityStack.get(index);
            if (null != activity && activity.getClass() == cls) {
                return true;
            }
        }

        return false;
    }

    /**
     * 结束指定activity
     *
     * @param activity
     */
    public void finishActivity(Activity activity) {
        if (null != activity) {
            activity.finish();
            activityStack.removeElement(activity);
            activity = null;
        }
    }

    /**
     * 结束指定的一组activity
     *
     * @param activities
     */
    public void finishActivity(Class<? extends Activity>[] activities) {
        if (null == activities || 0 == activities.length)
            return;
        for (Class<? extends Activity> activity : activities) {
            finishActivity(activity);
        }
    }

    /**
     * 结束指定类型的activity
     *
     * @param cls
     */
    public void finishActivity(Class<? extends Activity> cls) {
        int stackSize = activityStack.size();
        for (int index = stackSize - 1; index > -1; index--) {
            Activity activity = activityStack.get(index);
            if (null != activity && activity.getClass() == cls) {
                activity.finish();
                activityStack.remove(index);
                activity = null;
            }

        }
    }

    /*
    *关闭传进来的activity之后的所有的activiy（不包括传进来的activiy，不包括刚刚打开的activity）,传下划线
    @pramas includeCurrent  是否包括当前的activity
    */
    public void closeActivityAfterParams(Class cls, boolean includeCurrent) {

        int index = -1;
        int stackSize = activityStack.size();
        Stack<Activity> activityStack_clone = (Stack<Activity>) activityStack.clone();
        for (int i = 0; i < stackSize; i++) {
            if (activityStack_clone.get(i).getClass() == cls) {
                index = i;
            }
            if (index != -1 && (includeCurrent ? (i >= index) : (i > index))) {
                //清除掉指定activity后面的所有activity
                Activity activity = activityStack_clone.get(i);
                activity.finish();
                activityStack.remove(activity);
            }
        }
    }


    /**
     * 结束所有的activity
     */
    public void finishAllActivity() {
        int acSize = activityStack.size();
        for (int i = 0; i < acSize; i++) {
            Activity activity = activityStack.get(i);
            if (null != activity) {
                activity.finish();
                activity = null;
            }
        }
        activityStack.clear();
    }

    /**
     * 获取堆栈中activity数
     *
     * @return
     */
    public int getActivityStackSize() {
        return activityStack.size();
    }


    /**
     * 移除堆栈
     *
     * @param cls
     */
    public void removeActivity(Activity cls) {
        int stackSize = activityStack.size();
        for (int index = stackSize - 1; index > -1; index--) {
            Activity activity = activityStack.get(index);
            if (null != activity && activity.getClass().getName().equals(cls.getClass().getName())) {
                activityStack.remove(index);
                break;
            }

        }
    }
    public Activity getTopActivity() {
        if (activityStack.size() == 0) return null;
        return activityStack.get(activityStack.size() - 1);
    }

    public String getTopActivityName() {
        if (activityStack.size() == 0) return null;
        return activityStack.get(activityStack.size() - 1).getClass().getName();
    }

    public Stack<Activity> getActivityStack() {
        return activityStack;
    }
}
