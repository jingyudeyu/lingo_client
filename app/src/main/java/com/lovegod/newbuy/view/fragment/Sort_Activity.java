package com.lovegod.newbuy.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.lovegod.newbuy.R;
import com.lovegod.newbuy.api.BaseObserver;
import com.lovegod.newbuy.api.NetWorks;
import com.lovegod.newbuy.bean.Commodity;
import com.lovegod.newbuy.bean.SortFrist;
import com.lovegod.newbuy.view.sorts.CategoryGoodsActivity;
import com.lovegod.newbuy.view.sorts.SortActivity;
import com.lovegod.newbuy.view.sorts.SortFirstAdapter;
import com.lovegod.newbuy.view.sorts.SortSecondAdapter;
import com.lovegod.newbuy.view.utils.MyRecyclerViewAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/7/26.
 */

public class Sort_Activity extends Fragment {
    Toolbar toolbar;
    private RecyclerView sort_recyclerview_first;
    private ArrayList<String> mDate = new ArrayList<>();
    private List<SortFrist> mDate1 = new ArrayList<SortFrist>();
    private List<SortFrist> mDate2= new ArrayList<SortFrist>();
    private List<Commodity> mCommodity=new ArrayList<>();

    private SortFirstAdapter m1Adapter;
    private SortSecondAdapter m2Adapter;
    SliderLayout sort_slider_ads;
    //  SwipeRefreshLayout sort_layout_refresh;
    RecyclerView sort_recyclerview_second;
    private boolean first = true;
    private RecyclerView.LayoutManager mLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_sort,container,false);
        sort_recyclerview_first = (RecyclerView) view.findViewById(R.id.sort_recyclerview_first);
        sort_recyclerview_second = (RecyclerView) view.findViewById(R.id.sort_recyclerview_second);
        //  sort_slider_ads = (SliderLayout) findViewById(R.id.sort_slider_ads);
        // sort_layout_refresh = (SwipeRefreshLayout) findViewById(R.id.sort_layout_refresh);
        //   sort_recyclerview_first = (RecyclerView) findViewById(R.id.sort_recyclerview_first);
        toolbar = (Toolbar) view.findViewById(R.id.sort_toolbar);
        toolbar.setTitle("分类");
        toolbar.setTitleTextColor(getResources().getColor(R.color.tv_white));
        mLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        NetWorks.getSortFirst(new BaseObserver<List<SortFrist>>() {
            @Override
            public void onHandleSuccess(final List<SortFrist> sortFrists) {

                for (int i = 0; i < sortFrists.size(); i++) {
                    mDate.add(sortFrists.get(i).getBig());
                }
                Set<String> set = new HashSet<String>();
                set.addAll(mDate);
                mDate.clear();
                mDate.addAll(set);

                sort_recyclerview_first.setLayoutManager(new LinearLayoutManager(getActivity()));
                m1Adapter = new SortFirstAdapter(getActivity(), mDate);
                sort_recyclerview_first.setAdapter(m1Adapter);
                if (first == true) {
                    first = false;
                    sort_recyclerview_second.setLayoutManager(new LinearLayoutManager(getActivity()));

                    for (int i = 0; i < sortFrists.size(); i++) {
                        if (sortFrists.get(i).getBig().equals(mDate.get(0))) {
                            mDate1.add(sortFrists.get(i));
                        }
                    }
                    m2Adapter = new SortSecondAdapter(getActivity(), mDate1);
                    sort_recyclerview_second.setAdapter(m2Adapter);
                    sort_recyclerview_second.setLayoutManager(mLayoutManager);

                    m2Adapter.setItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            if (mDate1 != null)
                                CategoryGoods(mDate1.get(position).getCgid(),mDate1.get(position).getSecend());
                            else
                                Toast.makeText(getActivity(), "mDate1空", Toast.LENGTH_SHORT).show();

                        }
                        @Override
                        public void onItemLongClick(View view, int position) {

                        }
                    });
                }
                m1Adapter.setItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        if (first == false) {
                            int mdatenum = mDate1.size();
                            mDate1.clear();
                            for (int i = 0; i < mdatenum; i++) {
                                m2Adapter.notifyItemRemoved(i);
                            }

                            sort_recyclerview_second.setLayoutManager(new LinearLayoutManager(getActivity()));
                            for (int i = 0; i < sortFrists.size(); i++) {
                                if (sortFrists.get(i).getBig().equals(mDate.get(position))) {
                                    mDate1.add(sortFrists.get(i));
                                }
                            }

                            m2Adapter = new SortSecondAdapter(getActivity(), mDate1);
                            sort_recyclerview_second.setAdapter(m2Adapter);
                            sort_recyclerview_second.setLayoutManager(mLayoutManager);
                            m2Adapter.notifyDataSetChanged();
                        }

                        m2Adapter.setItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                if (mDate1 != null)
                                    CategoryGoods(mDate1.get(position).getCgid(),mDate1.get(position).getSecend());
                                else
                                    Toast.makeText(getActivity(), "mDate1空", Toast.LENGTH_SHORT).show();

                            }
                            @Override
                            public void onItemLongClick(View view, int position) {

                            }
                        });
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                });

                //   m2Adapter.setItemClickListener(new SecondLisstener());
            }

            @Override
            public void onHandleError(List<SortFrist> sortFrists) {

            }
        });
        return view;
    }

    private void CategoryGoods(int cgid,String secondname) {
        final String name=secondname;
        NetWorks.getSecondGoods(cgid, new BaseObserver<List<Commodity>>() {
            @Override
            public void onHandleSuccess(List<Commodity> commodities) {
                mCommodity=commodities;
                Intent intent = new Intent(getActivity(), CategoryGoodsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("categoods", (Serializable) mCommodity);
                bundle.putString("sortname",name);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onHandleError(List<Commodity> commodities) {

            }
        });

    }
}
