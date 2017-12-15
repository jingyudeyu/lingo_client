package com.lovegod.newbuy.receiver;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lovegod.newbuy.MyApplication;
import com.lovegod.newbuy.R;
import com.lovegod.newbuy.api.BaseObserver;
import com.lovegod.newbuy.api.NetWorks;
import com.lovegod.newbuy.bean.Commodity;
import com.lovegod.newbuy.bean.Goods;
import com.lovegod.newbuy.service.BlutoothCus;
import com.lovegod.newbuy.utils.system.SpUtils;
import com.lovegod.newbuy.utils.view.DialogActivity;
import com.lovegod.newbuy.view.goods.GoodActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;


/**
 * Created by 123 on 2017/4/4.
 */

public class BlutoothReceiver extends BroadcastReceiver {

    //获取手机系统
    private static final String KEY_MIUI_VERSION_CODE="ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME="ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE="ro.miui.internal.storage";


    Goods goodss = new Goods();

    @Override
    public void onReceive(final Context context, final Intent intent) {

        final BlutoothCus blutoothCus = (BlutoothCus) intent.getSerializableExtra("device");
        BlutoothCus blutoothCus1 = (BlutoothCus) SpUtils.getObject(context, "ble");


//        Point point = (Point) intent.getSerializableExtra("point");

        if (blutoothCus1 != null) {
            if (blutoothCus1.getAddress().equals(blutoothCus.getAddress())) {
                long i = (blutoothCus.getDate().getTime() - blutoothCus1.getDate().getTime()) / (24 * 60 * 60 * 100);
                if (i > 3 * 60) {   //设置三分钟后才可继续推送
                    if (blutoothCus.getRssi() > -80) {
                        SpUtils.removeKey(context, "ble");
                        SpUtils.putObject(context, "ble", blutoothCus);
                        Log.v("推送蓝牙", blutoothCus.getAddress());
//                        Log.v("推送坐标", point.getX() + " " + point.getY());

                        Random random=new Random();
                        NetWorks.findCommodity(random.nextInt(10)+1, new BaseObserver<Commodity>() {
//                        NetWorks.getPushCommodity(blutoothCus.getAddress(), point.getX(), point.getY(),1, new BaseObserver<Commodity>() {
                            @Override
                            public void onHandleSuccess(final Commodity commodity) {
                                //设置点击通知栏的动作为启动另外一个广播
                                if (commodity != null) {
                                    int size = MyApplication.mList.size();
                                    if (size > 0) {
                                        //显示弹窗
                                        // 获得广播发送的数据

                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("commodity", commodity);
                                        Intent intent1=new Intent(context, DialogActivity.class);
                                        intent1.putExtras(bundle);
                                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent1);

                                     /*  AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                                        //  dialogBuilder.setTitle("猜您喜欢");
                                        //  dialogBuilder.setMessage(commodity.getProductname());
                                        dialogBuilder.setCancelable(false);
                                        View view = LayoutInflater.from(context).inflate(R.layout.blutooth_dialog,null);
                                        final AlertDialog alertDialog = dialogBuilder.create();
                                        alertDialog.setView(view);
                                        Button blueOk = (Button) view.findViewById(R.id.blue_ok);
                                        Button blueNo = (Button) view.findViewById(R.id.blue_no);
                                        ImageView blueGoodImg = (ImageView) view.findViewById(R.id.blue_good_img);
                                        TextView blueGoodName = (TextView) view.findViewById(R.id.blue_good_name);
                                        TextView blueGoodMoney = (TextView) view.findViewById(R.id.blue_good_money);
                                        TextView blueGoodNum = (TextView) view.findViewById(R.id.blue_good_num);
                                        Glide.with(context).load(commodity.getLogo()).into(blueGoodImg);
                                        blueGoodName.setText(commodity.getProductname());
                                        blueGoodMoney.setText(commodity.getPrice() + "元");
                                        blueGoodNum.setText("销售量："+commodity.getSalesvolu() + "");
                                        blueNo.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                alertDialog.dismiss();
                                            }
                                        });
                                        blueOk.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Bundle bundle = new Bundle();
                                                bundle.putSerializable("commodity", commodity);
                                                Intent intent1 = new Intent(context, GoodActivity.class);
                                                intent1.putExtras(bundle);
                                                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                                context.startActivity(intent1);
                                                alertDialog.dismiss();
                                            }
                                        });
                                        Properties prop=new Properties();
                                        try {
                                            prop.load(new FileInputStream(new File(Environment.getRootDirectory(),"build.prop")));
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        if (prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null || prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null) {
                                            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
                                        } else {
                                            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                                        }

                                        WindowManager.LayoutParams layoutParams = alertDialog.getWindow().getAttributes();
                                        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                                        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                                        alertDialog.getWindow().setAttributes(layoutParams);
                                        alertDialog.show();*/


                                    } else {
                                        //显示通知栏
                                        Intent broadcastIntent = new Intent(context, NotificationReceiver.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("commodity", commodity);
                                        broadcastIntent.putExtras(bundle);
                                        PendingIntent pendingIntent = PendingIntent.
                                                getBroadcast(context, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                                        builder.setContentTitle(commodity.getProductname())
                                                .setContentText("进入应用查看详情")
                                                .setTicker("进入应用查看详情")
                                                .setAutoCancel(true)
                                                .setDefaults(Notification.DEFAULT_ALL)
                                                .setContentIntent(pendingIntent)
                                                .setSmallIcon(android.R.drawable.ic_lock_idle_charging);
                                        Log.i("定位推送", "显示推送消息");
                                        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                        manager.notify(2, builder.build());
                                    }
                                }
                            }

                            @Override
                            public void onHandleError(Commodity commodity) {

                            }
                        });
                    } else {
                        Log.v("蓝牙RSSI", "值达不到");
                    }
                }
            } else {
                if (blutoothCus.getRssi() > -80) {
                    SpUtils.removeKey(context, "ble");
                    SpUtils.putObject(context, "ble", blutoothCus);
                    Log.v("推送蓝牙", blutoothCus.getAddress());
//                    Log.v("推送坐标", point.getX() + " " + point.getY());
                    Random random=new Random();
                    NetWorks.findCommodity(random.nextInt(10)+1, new BaseObserver<Commodity>() {
//                    NetWorks.getPushCommodity(blutoothCus.getAddress(), point.getX(), point.getY(),1, new BaseObserver<Commodity>() {
                        @Override
                        public void onHandleSuccess(final Commodity commodity) {
                            //设置点击通知栏的动作为启动另外一个广播
                            if (commodity != null) {
                                int size = MyApplication.mList.size();
                                if (size > 0) {
                                    //显示弹窗
                                    // 获得广播发送的数据
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("commodity", commodity);
                                    Intent intent1=new Intent(context, DialogActivity.class);
                                    intent1.putExtras(bundle);
                                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent1);
                                 /*  AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                                  //  dialogBuilder.setTitle("猜您喜欢");
                                  //  dialogBuilder.setMessage(commodity.getProductname());
                                    dialogBuilder.setCancelable(false);
                                    View view = LayoutInflater.from(context).inflate(R.layout.blutooth_dialog,null);
                                    final AlertDialog alertDialog = dialogBuilder.create();
                                    alertDialog.setView(view);
                                    Button blueOk = (Button) view.findViewById(R.id.blue_ok);
                                    Button blueNo = (Button) view.findViewById(R.id.blue_no);
                                    ImageView blueGoodImg = (ImageView) view.findViewById(R.id.blue_good_img);
                                    TextView blueGoodName = (TextView) view.findViewById(R.id.blue_good_name);
                                    TextView blueGoodMoney = (TextView) view.findViewById(R.id.blue_good_money);
                                    TextView blueGoodNum = (TextView) view.findViewById(R.id.blue_good_num);
                                    Glide.with(context).load(commodity.getLogo()).into(blueGoodImg);
                                    blueGoodName.setText(commodity.getProductname());
                                    blueGoodMoney.setText(commodity.getPrice() + "元");
                                    blueGoodNum.setText("销售量："+commodity.getSalesvolu() + "");
                                    blueNo.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            alertDialog.dismiss();
                                        }
                                    });
                                    blueOk.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Bundle bundle = new Bundle();
                                            bundle.putSerializable("commodity", commodity);
                                            Intent intent1 = new Intent(context, GoodActivity.class);
                                            intent1.putExtras(bundle);
                                            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            context.startActivity(intent1);
                                            alertDialog.dismiss();
                                        }
                                    });
                                    alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                                    WindowManager.LayoutParams layoutParams = alertDialog.getWindow().getAttributes();
                                    layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                                    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                                    alertDialog.getWindow().setAttributes(layoutParams);
                                    alertDialog.show();*/


                                } else {
                                    //显示通知栏
                                    Intent broadcastIntent = new Intent(context, NotificationReceiver.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("commodity", commodity);
                                    broadcastIntent.putExtras(bundle);
                                    PendingIntent pendingIntent = PendingIntent.
                                            getBroadcast(context, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                                    builder.setContentTitle(commodity.getProductname())
                                            .setContentText("进入应用查看详情")
                                            .setTicker("进入应用查看详情")
                                            .setAutoCancel(true)
                                            .setDefaults(Notification.DEFAULT_ALL)
                                            .setContentIntent(pendingIntent)
                                            .setSmallIcon(android.R.drawable.ic_lock_idle_charging);
                                    Log.i("定位推送", "显示推送消息");
                                    NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                    manager.notify(2, builder.build());
                                }
                            }
                        }

                        @Override
                        public void onHandleError(Commodity commodity) {

                        }
                    });
                } else {
                    Log.v("蓝牙RSSI", "值达不到");
                }
            }
        } else {
            if (blutoothCus.getRssi() > -80) {
                SpUtils.putObject(context, "ble", blutoothCus);
                Log.v("推送蓝牙", blutoothCus.getAddress());
//                Log.v("推送坐标", point.getX() + " " + point.getY());
                Random random=new Random();
                NetWorks.findCommodity(random.nextInt(10)+1, new BaseObserver<Commodity>() {
//                NetWorks.getPushCommodity(blutoothCus.getAddress(), point.getX(), point.getY(),1, new BaseObserver<Commodity>() {
                    @Override
                    public void onHandleSuccess(final Commodity commodity) {
                        //设置点击通知栏的动作为启动另外一个广播
                        if (commodity != null) {
                            int size = MyApplication.mList.size();
                            if (size > 0) {
                                //显示弹窗
                                // 获得广播发送的数据
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("commodity", commodity);
                                Intent intent1=new Intent(context, DialogActivity.class);
                                intent1.putExtras(bundle);
                                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent1);
                              /*  AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                                //  dialogBuilder.setTitle("猜您喜欢");
                                //  dialogBuilder.setMessage(commodity.getProductname());
                                dialogBuilder.setCancelable(false);
                                View view = LayoutInflater.from(context).inflate(R.layout.blutooth_dialog,null);
                                final AlertDialog alertDialog = dialogBuilder.create();
                                alertDialog.setView(view);
                                Button blueOk = (Button) view.findViewById(R.id.blue_ok);
                                Button blueNo = (Button) view.findViewById(R.id.blue_no);
                                ImageView blueGoodImg = (ImageView) view.findViewById(R.id.blue_good_img);
                                TextView blueGoodName = (TextView) view.findViewById(R.id.blue_good_name);
                                TextView blueGoodMoney = (TextView) view.findViewById(R.id.blue_good_money);
                                TextView blueGoodNum = (TextView) view.findViewById(R.id.blue_good_num);
                                Glide.with(context).load(commodity.getLogo()).into(blueGoodImg);
                                blueGoodName.setText(commodity.getProductname());
                                blueGoodMoney.setText(commodity.getPrice() + "元");
                                blueGoodNum.setText("销售量："+commodity.getSalesvolu() + "");
                                blueNo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                    }
                                });
                                blueOk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("commodity", commodity);
                                        Intent intent1 = new Intent(context, GoodActivity.class);
                                        intent1.putExtras(bundle);
                                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent1);
                                        alertDialog.dismiss();
                                    }
                                });
                                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                                WindowManager.LayoutParams layoutParams = alertDialog.getWindow().getAttributes();
                                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                                alertDialog.getWindow().setAttributes(layoutParams);

                                alertDialog.show();*/


                            } else {
                                //显示通知栏
                                Intent broadcastIntent = new Intent(context, NotificationReceiver.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("commodity", commodity);
                                broadcastIntent.putExtras(bundle);
                                PendingIntent pendingIntent = PendingIntent.
                                        getBroadcast(context, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                                builder.setContentTitle(commodity.getProductname())
                                        .setContentText("进入应用查看详情")
                                        .setTicker("进入应用查看详情")
                                        .setAutoCancel(true)
                                        .setDefaults(Notification.DEFAULT_ALL)
                                        .setContentIntent(pendingIntent)
                                        .setSmallIcon(android.R.drawable.ic_lock_idle_charging);
                                Log.i("定位推送", "显示推送消息");
                                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                manager.notify(2, builder.build());
                            }
                        }
                    }

                    @Override
                    public void onHandleError(Commodity commodity) {

                    }
                });
            } else {
                Log.v("蓝牙RSSI", "值达不到");
            }
        }


    }


}
