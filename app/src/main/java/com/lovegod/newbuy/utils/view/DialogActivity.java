package com.lovegod.newbuy.utils.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lovegod.newbuy.R;
import com.lovegod.newbuy.bean.Commodity;
import com.lovegod.newbuy.view.goods.GoodActivity;


/**
 * Created by gxc on 2017/12/15.
 */

public class DialogActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blutooth_dialog);
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
        WindowManager.LayoutParams p = getWindow().getAttributes();
        //p.height = (int) (d.getHeight() * 0.3); // 高度设置为屏幕的0.3
        p.width = (int) (d.getWidth() * 0.95); // 宽度设置为屏幕的0.7
        getWindow().setAttributes(p);
        Bundle bundle=getIntent().getExtras();
       final Commodity commodity= (Commodity) bundle.getSerializable("commodity");
        Button blueOk = (Button) findViewById(R.id.blue_ok);
        Button blueNo = (Button) findViewById(R.id.blue_no);
        ImageView blueGoodImg = (ImageView) findViewById(R.id.blue_good_img);
        TextView blueGoodName = (TextView) findViewById(R.id.blue_good_name);
        TextView blueGoodMoney = (TextView) findViewById(R.id.blue_good_money);
        TextView blueGoodNum = (TextView) findViewById(R.id.blue_good_num);
        setFinishOnTouchOutside(false);//设置点击窗体以外的地方dialog不消失

        Glide.with(this).load(commodity.getLogo()).into(blueGoodImg);
        blueGoodName.setText(commodity.getProductname());
        blueGoodMoney.setText(commodity.getPrice() + "元");
        blueGoodNum.setText("销售量："+commodity.getSalesvolu() + "");

        blueNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        blueOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("commodity", commodity);
                Intent intent1 = new Intent(DialogActivity.this, GoodActivity.class);
                intent1.putExtras(bundle);
                //intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
               finish();
            }
        });
    }
}
