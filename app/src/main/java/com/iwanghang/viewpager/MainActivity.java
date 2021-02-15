package com.iwanghang.viewpager;

import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewpager;
    private TextView tv_title;
    private LinearLayout ll_point_group;

    // 因为大家都了解ListView，先说一下ListView的使用，然后我们对比着，看一下ViewPager的使用
    // ListView的使用
    // 1、在布局文件中定义ListView
    // 2、在代码中实例化ListView
    // 3、准备数据
    // 4、设置适配器(BaseAdapter)-item布局-绑定数据

    // ArrayList不安全但是效率高
    // 这里是用于准备数据，这个就是图片数据的集合
    private ArrayList<ImageView> imageViews;
    // 图片资源ID集合
    private final int[] imageIds = {R.drawable.a,R.drawable.b,R.drawable.c,R.drawable.d,R.drawable.e};
    // 图片标题集合
    private final String[] imageDescriptions = {"元旦好","新年好","大家好","哈哈哈","嘻嘻嘻"};

    int prePosition = 0;

    // 拖动是否为真，辅助判断图片触摸事件
    private boolean isDragging = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewpager = (ViewPager) findViewById(R.id.viewpager);
        tv_title = (TextView) findViewById(R.id.tv_title);
        ll_point_group = (LinearLayout) findViewById(R.id.ll_point_group);

        // ViewPager的使用
        // 1、在布局文件中定义ViewPager
        // 2、在代码中实例化ViewPager
        // 3、准备数据
        // 4、设置适配器(PagerAdapter)-item布局-绑定数据
        imageViews = new ArrayList<>();
        for (int i = 0; i < imageIds.length;i++) {
            ImageView imageView = new ImageView(this);
            // 设置src会按比例填充，但是设置background会拉伸填充
            // 我们要的效果是拉伸填充，所以这里使用setBackgroundResource
            imageView.setBackgroundResource(imageIds[i]);
            // 添加到集合中
            imageViews.add(imageView);
            // 添加点点
            ImageView point = new ImageView(this);
            point.setBackgroundResource(R.drawable.point_selector);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(8,8);
            if (i==0){
                point.setEnabled(true); // 显示红色
            }else {
                point.setEnabled(false); // 显示灰色
                params.leftMargin = 8;
            }
            point.setLayoutParams(params);
            ll_point_group.addView(point);
        }
        viewpager.setAdapter(new MyPagerAdapter());
        viewpager.addOnPageChangeListener(new MyOnPageChangeListener());


        /**
         * 设置中间位置 向左向右都可以滑动1073741820次
         * 简单的说 这里设置一个很大的上限 让轮播图片看起来好像是无限循环
         * 实际没有用户会向左或者向右滑动1073741820次 所以就是无限循环的效果
         * 同时，要在MyPagerAdapter和MyOnPageChangeListener中做对应修改
         */
        // 保证imageViews的整数倍 Integer.MAX_VALUE=2147483647
        int item = Integer.MAX_VALUE/2 - Integer.MAX_VALUE/2%imageViews.size();
        //System.out.println(item); // 1073741820
        /**
         * setCurrentItem
         * 设置当前选定的页面。如果ViewPager已经通过它的第一
         * 布局与当前适配器将有一个平稳的动画之间的过渡
         * 当前项目和指定项目。
         */
        viewpager.setCurrentItem(item); // 设置当前选定的页面。即，中间位置。


        // 设置标题
        tv_title.setText(imageDescriptions[0]);

        // 发消息
        handler.sendEmptyMessageDelayed(0,2000);
    }

    /**
     * 实例化一个Handler 让轮播自动循环
     */
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int item = viewpager.getCurrentItem()+1;
            viewpager.setCurrentItem(item);

            // 延迟发消息
            handler.sendEmptyMessageDelayed(0,2000);
        }
    };

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener{

        /**
         * 当页面滚动了的时候回调这个方法
         * @param position 当前页面的位置
         * @param positionOffset 滑动页面的百分比
         * @param positionOffsetPixels 在屏幕上滑动的像素
         */
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        /**
         * 当某个页面被选中
         * @param position 被选中的页面的位置
         */
        @Override
        public void onPageSelected(int position) {
            int realPosition = position%imageViews.size();

            // 设置对应页面的文本信息
            //tv_title.setText(imageDescriptions[position]);
            tv_title.setText(imageDescriptions[realPosition]);
            // 设置点点的颜色
            ll_point_group.getChildAt(prePosition).setEnabled(false); // 上一页页面对应点点设置为灰色
            //ll_point_group.getChildAt(position).setEnabled(true); // 当前页面对应点点设置为红色
            ll_point_group.getChildAt(realPosition).setEnabled(true); // 当前页面对应点点设置为红色
            //prePosition = position; // 记录当前点点
            prePosition = realPosition; // 记录当前点点
        }

        /**
         * 当页面滚动 状态的变化 回调这个方法
         * 静止 -> 滑动
         * 滑动 -> 静止
         * 静止 -> 拖拽
         * @param state
         */
        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_DRAGGING){ // 拖动
                System.out.println("拖动为真");
                isDragging = true; // 拖动为真
            }else if (state == ViewPager.SCROLL_STATE_SETTLING){ // 持续(滚动)

            }else if (state == ViewPager.SCROLL_STATE_IDLE&&isDragging){ // 闲置(静止)
                System.out.println("拖动为假");
                isDragging = false; // 拖动为假
                handler.removeCallbacksAndMessages(null);
                handler.sendEmptyMessageDelayed(0,2000);
            }
        }
    }

    class MyPagerAdapter extends PagerAdapter{

        /**
         * @return 图片的总数
         */
        @Override
        public int getCount() {
            // return imageViews.size();
            return Integer.MAX_VALUE;
        }

        /**
         * 相当于getView方法
         * @param container ViewPager自身
         * @param position 当前实例化页面的位置
         * @return
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            int realPosition = position%imageViews.size();
            // ImageView imageView = imageViews.get(position);
            ImageView imageView = imageViews.get(realPosition);

            container.addView(imageView); // 把图片添加到ViewPager容器中

            /**
             * 添加触摸事件，当图片被触摸，停止自动播放，当触摸停止，继续自动播放
             * 像Demo这样，0.5秒时间间隔，可能有按不住的情况，动得太快了，这边是为了演示
             * 时间使用，建议轮播时间时间3-4
             * 但是，只在这里判断是不够，当用户开始滑动图片，优先调用ACTION_CANCEL，不会
             * 调用到ACTION_MOVE，这里需要详细了解可以Log一下这几个触摸事件看一下，系统是
             * 如何接受用户触摸事件的
             * 所以还需要在MyOnPageChangeListener的onPageScrollStateChanged
             * 的方法里，添加判断
             */
            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()){
                        case MotionEvent.ACTION_DOWN: // 按下
                            handler.removeCallbacksAndMessages(null);
                            break;
                        case MotionEvent.ACTION_UP: // 抬起
                            handler.removeCallbacksAndMessages(null);
                            handler.sendEmptyMessageDelayed(0,2000);
                            break;
                        case MotionEvent.ACTION_CANCEL: // 取消
                            break;
                        case MotionEvent.ACTION_MOVE: // 移动
                            break;
                    }
                    return false; // 返回为false事件继续，返回true事件销毁，因为要监听图片点击，所以不能销毁
                }
            });

            /**
             * 图片点击监听
             */
            imageView.setTag(realPosition);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int realPosition = (int) view.getTag();
                    
                    String text = imageDescriptions[realPosition];
                    Toast.makeText(MainActivity.this, "text = " + text, Toast.LENGTH_SHORT).show();
                }
            });
            
            
            
            
            
            return imageView;
        }
        
        /**
         * 比较view和object是否是同一个实力
         * @param view 页面
         * @param object instantiateItem返回的结果
         * @return
         */
        @Override
        public boolean isViewFromObject(View view, Object object) {
            if (view == object) {
                return true;
            }else {
                return false;
            }
        }

        /**
         * 释放资源
         * @param container viewpager
         * @param position 要释放的位置
         * @param object 要释放的页面
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }
}
