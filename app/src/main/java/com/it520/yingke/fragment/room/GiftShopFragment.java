package com.it520.yingke.fragment.room;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.it520.yingke.R;
import com.it520.yingke.adapter.GiftGridViewAdapter;
import com.it520.yingke.bean.GiftBean;
import com.it520.yingke.bean.GiftListBean;
import com.it520.yingke.http.RetrofitCallBackWrapper;
import com.it520.yingke.http.ServiceGenerator;
import com.it520.yingke.http.service.GiftShopService;
import com.it520.yingke.util.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;


/* 
 * ============================================================
 * Editor: MuMuXuan(6511631@qq.com)
 *  
 * Time: 2017-06-21 14:35 
 * 
 * Description: 
 *
 * Version: 1.0
 * ============================================================
 */

public class GiftShopFragment extends Fragment {


    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.ll_dots)
    LinearLayout mLlDots;
    @BindView(R.id.ll_golds)
    LinearLayout mLlGolds;
    @BindView(R.id.tv_send_store)
    TextView mTvSendStore;
    @BindView(R.id.ll_bottom)
    LinearLayout mLlBottom;
    @BindView(R.id.back)
    View mBack;
    @BindView(R.id.ll_content)
    LinearLayout mLlContent;
    protected Animation mAnimIn;
    protected Animation mAnimOut;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.frag_gift_shop, container, false);
        ButterKnife.bind(this, inflate);
        return inflate;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        requestGiftData();
        showContent();
    }

    private void requestGiftData() {
        GiftShopService service = ServiceGenerator.getSingleton().createService(GiftShopService.class);
        Call<GiftListBean> giftsData = service.getGiftsData();
        giftsData.enqueue(new RetrofitCallBackWrapper<GiftListBean>() {
            @Override
            public void onResponse(GiftListBean body) {
                Log.e(getClass().getSimpleName() + "xmg", "onResponse: " + "");
                loadGiftsData(body);
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
    }

    private void loadGiftsData(GiftListBean giftListBean) {
        //计算总页数，每页展示8个
        List<GiftBean> allGifts = giftListBean.getGifts();
        int pageSize = allGifts.size() / DEFAULT_GIFT_COUNT;
        int remainder = allGifts.size() % DEFAULT_GIFT_COUNT;//余数
        pageSize = pageSize+(remainder==0?0:1);//如果有余数，就+1
        ArrayList<GridView> gridViewList = new ArrayList<>();
        //准备各页的GridView用于展示
        for (int i = 0; i < pageSize; i++) {
            ArrayList<GiftBean> gifts = getCurrentPageGifts(allGifts,i);
            GridView gridView = new GridView(getContext());
            gridView.setNumColumns(4);
            gridView.setHorizontalSpacing(DisplayUtil.dip2px(getContext(),2));
            GiftGridViewAdapter giftGridViewAdapter = new GiftGridViewAdapter(gifts);
            gridView.setAdapter(giftGridViewAdapter);
            gridViewList.add(gridView);
        }
        //将各个GridView发给ViewPager来展示
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(gridViewList);
        mViewPager.setAdapter(myPagerAdapter);
    }

    public static final int DEFAULT_GIFT_COUNT = 8;

    /**
     * 获得当前页的礼物对应的集合
     * @param allGifts
     * @param currentIndex
     * @return
     */
    private ArrayList<GiftBean> getCurrentPageGifts(List<GiftBean> allGifts, int currentIndex) {
        ArrayList<GiftBean> giftBeanList = new ArrayList<>();
        int pageSize = allGifts.size() / DEFAULT_GIFT_COUNT;
        int remainder = allGifts.size() % DEFAULT_GIFT_COUNT;//余数
        pageSize = pageSize+(remainder==0?0:1);//如果有余数，就+1
        int currentPageGiftCount = 0;
        if(currentIndex!=pageSize-1||remainder==0){
            currentPageGiftCount = DEFAULT_GIFT_COUNT;
        }else{
            currentPageGiftCount = remainder;
        }
        for (int i = 0; i < currentPageGiftCount; i++) {
            giftBeanList.add(allGifts.get(currentIndex*DEFAULT_GIFT_COUNT+i));
        }
        return giftBeanList;
    }

    private void init() {
        mAnimIn = AnimationUtils.loadAnimation(getContext(), R.anim.gift_shop_in);
        mAnimOut = AnimationUtils.loadAnimation(getContext(), R.anim.gift_shop_out);
        mAnimOut.setAnimationListener(new MyOutAnimListener());
    }

    public void showContent() {
//        Toast.makeText(getContext(), "TEST", Toast.LENGTH_SHORT).show();
        if(isShowContent())
            return;
        //不可见时，开始可见并播放动画
        mLlContent.setVisibility(View.VISIBLE);
        mLlContent.startAnimation(mAnimIn);
    }

    private boolean isShowContent(){
        return mLlContent.getVisibility() == View.VISIBLE;
    }

    public void hideContent() {
        mLlContent.startAnimation(mAnimOut);
        //// TODO: 2017/6/21 需要将下面的一排按钮进行展示

    }

    public boolean backPressed() {
        if(isShowContent()){
            hideContent();
            return false;
        }
        //可以关闭当前Activity
        return true;
    }

    @OnClick({R.id.tv_send_store, R.id.back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_send_store:
                break;
            case R.id.back:
                hideContent();
                break;
        }
    }

    private class MyOutAnimListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mLlContent.setVisibility(View.GONE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    private class MyPagerAdapter extends PagerAdapter{


        ArrayList<GridView> gridViewList;

        public MyPagerAdapter(ArrayList<GridView> gridViewList) {
            this.gridViewList = gridViewList;
        }

        @Override
        public int getCount() {
            return gridViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            GridView gridView = gridViewList.get(position);
            container.addView(gridView);
            return gridView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}