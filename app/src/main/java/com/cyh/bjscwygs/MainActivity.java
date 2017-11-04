package com.cyh.bjscwygs;

import android.Manifest;
import android.accounts.NetworkErrorException;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import cn.jiguang.share.android.api.JShareInterface;
import cn.jiguang.share.android.api.PlatActionListener;
import cn.jiguang.share.android.api.Platform;
import cn.jiguang.share.android.api.ShareParams;
import cn.jiguang.share.qqmodel.QQ;
import cn.jiguang.share.qqmodel.QZone;
import cn.jiguang.share.wechat.Wechat;
import cn.jiguang.share.wechat.WechatMoments;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    MyDialog dialog;
    private TextView t1;
    private TextView t2;
    private TextView textView;
    private ImageView iv;
    PopupWindow pop;
    Button btnCancel;
    ImageView btnWxFriend, btnWxQuan, btnQQFriend,btnQzone;
    View view;
    TextView hideView;
    boolean isOut, isIn;// 是否弹窗显示
    private String UserName;
    private String PhoneNumber;
    private String url;
    private boolean IsEmploy;
    final Handler cwjHandler = new Handler();
    private ProgressDialog progressDialog;
    private final long SPLASH_LENGTH = 2000;
    Handler handler = new Handler();
    private Handler handler_share=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String toastMsg = (String) msg.obj;
            Toast.makeText(MainActivity.this,toastMsg,Toast.LENGTH_SHORT).show();
            if(progressDialog!=null&&progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
    };
    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            updateUI();
        }
    };

    private void updateUI() {

        t1.setText(UserName);
        t2.setText(PhoneNumber);

        Bitmap qrBitmap = generateBitmap(url, 400, 400);

        iv.setImageBitmap(qrBitmap);
        if (IsEmploy)
            textView.setVisibility(View.INVISIBLE);
        else
            textView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        hideView = (TextView) findViewById(R.id.hideView);
        LayoutInflater inflater = LayoutInflater.from(this);
        // 引入窗口配置文件 - 即弹窗的界面
        t1 = (TextView) findViewById(R.id.textView);
        t2 = (TextView) findViewById(R.id.textView2);
        iv = (ImageView) findViewById(R.id.iv);
        view = inflater.inflate(R.layout.menu_view, null);
        btnWxFriend = (ImageView) view.findViewById(R.id.btnWxFriend);
        btnWxQuan = (ImageView) view.findViewById(R.id.btnWxQuan);
        btnQQFriend = (ImageView) view.findViewById(R.id.btnQQFriend);
        btnQzone = (ImageView) view.findViewById(R.id.btnQzone);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        textView = (TextView) findViewById(R.id.textView7);
        textView.setText(getClickableSpan());
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        SharedPreferences setting = getSharedPreferences("com.cyh.bjscwygs", 0);
        Boolean user_first = setting.getBoolean("FIRST", true);
        if (user_first) {// 第一次则跳转到欢迎页面
            System.out.println("First");
            setting.edit().putBoolean("FIRST", false).commit();

        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        btnWxFriend.setOnClickListener(this);
        btnWxQuan.setOnClickListener(this);
        iv.setOnClickListener(this);
        btnQQFriend.setOnClickListener(this);
        btnQzone.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        view.setFocusableInTouchMode(true);
//        view.setOnKeyListener(this);
        // PopupWindow实例化
        pop = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        /**
         * PopupWindow 设置
         */
        // pop.setFocusable(true); //设置PopupWindow可获得焦点
        // pop.setTouchable(true); //设置PopupWindow可触摸
        // pop.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
        // 设置PopupWindow显示和隐藏时的动画
        pop.setAnimationStyle(R.style.MenuAnimationFade);
        /**
         * 改变背景可拉的弹出窗口。后台可以设置为null。 这句话必须有，否则按返回键popwindow不能消失 或者加入这句话
         * ColorDrawable dw = new
         * ColorDrawable(-00000);pop.setBackgroundDrawable(dw);
         */
        pop.setBackgroundDrawable(new BitmapDrawable());

    }

    protected void onStart() {
        super.onStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                dialog = MyDialog.showDialog(MainActivity.this);
                dialog.show();
                final String deviceId;
                final String phone;
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    TelephonyManager tm = (TelephonyManager) MainActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
                    deviceId = tm.getDeviceId();
                    phone = tm.getLine1Number();
                    System.out.println("deviceId:" + deviceId + ";phone:" + phone);
                    try {
                        final String state = NetUtil.ConfigOfPost(deviceId, phone);
                        System.out.println(state);
                        JSONTokener jsonParser = new JSONTokener(state);
                        JSONObject person = (JSONObject) jsonParser.nextValue();
                        int code = person.getInt("Code");
                        if (code == 0) {
                            UserName = person.getString("UserName");
                            PhoneNumber = person.getString("PhoneNumber");
                            IsEmploy = person.getBoolean("IsEmploy");
                            url = "http://192.168.0.5/share?uid=" + person.getLong("UserId");
                            cwjHandler.post(mUpdateResults);
                        } else {
                        }
                    } catch (final NetworkErrorException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "系统错误！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "请确保已允许APP读取手机信息的权利！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                dialog.dismiss();
            }
        }).start();
    }
    private PlatActionListener mPlatActionListener = new PlatActionListener() {
        @Override
        public void onComplete(Platform platform, int action, HashMap<String, Object> data) {
            if(handler_share != null) {
                Message message = handler_share.obtainMessage();
                message.obj = "分享成功";
                handler_share.sendMessage(message);
            }
        }

        @Override
        public void onError(Platform platform, int action, int errorCode, Throwable error) {
            if(handler_share != null) {
                Message message = handler_share.obtainMessage();
                message.obj = "分享失败:" + (error != null ? error.getMessage() : "");
                handler_share.sendMessage(message);
            }
        }

        @Override
        public void onCancel(Platform platform, int action) {
            if(handler_share != null) {
                Message message = handler_share.obtainMessage();
                message.obj = "分享取消";
                handler_share.sendMessage(message);
            }
        }
    };
    @Override
    public void onClick(View view) {
        ShareParams shareParams = new ShareParams();
        switch (view.getId()) {
            case R.id.btnWxFriend:
                changePopupWindowState();
                shareParams.setTitle("移动营业厅业务情况统计APP下载");
                shareParams.setText("移动营业厅业务情况统计APP下载");
                shareParams.setShareType(Platform.SHARE_WEBPAGE);
                shareParams.setUrl(url);//必须
                shareParams.setImageData(generateBitmap(url, 400, 400));
                JShareInterface.share(Wechat.Name, shareParams, mPlatActionListener);
//                Toast.makeText(MainActivity.this, "你点击了夜间模式", Toast.LENGTH_SHORT)
//                        .show();
                break;
            case R.id.btnWxQuan:
                changePopupWindowState();
                shareParams.setTitle("移动营业厅业务情况统计APP下载");
                shareParams.setText("移动营业厅业务情况统计APP下载");
                shareParams.setShareType(Platform.SHARE_WEBPAGE);
                shareParams.setUrl(url);//必须
                shareParams.setImageData(generateBitmap(url, 400, 400));
                JShareInterface.share(WechatMoments.Name, shareParams, mPlatActionListener);
                break;
            case R.id.btnQQFriend:
                changePopupWindowState();
                shareParams.setTitle("移动营业厅业务情况统计APP下载");
                shareParams.setText("移动营业厅业务情况统计APP下载");
                shareParams.setShareType(Platform.SHARE_WEBPAGE);
                shareParams.setUrl(url);//必须
                shareParams.setImageData(generateBitmap(url, 400, 400));
                JShareInterface.share(QQ.Name, shareParams, mPlatActionListener);
                break;
            case R.id.btnQzone:
                changePopupWindowState();
                shareParams.setTitle("移动营业厅业务情况统计APP下载");
                shareParams.setText("移动营业厅业务情况统计APP下载");
                shareParams.setShareType(Platform.SHARE_WEBPAGE);
                shareParams.setUrl(url);//必须
                shareParams.setImageData(generateBitmap(url, 400, 400));
                JShareInterface.share(QZone.Name, shareParams, mPlatActionListener);
                break;
//            case R.id.btnExit:
//                exitTheDemo();
//                break;
            case R.id.btnCancel:
                changePopupWindowState();
                break;
            case R.id.iv:
                isOut = true;
                changePopupWindowState();
                break;
        }
    }

    /**
     * 退出程序
     */
    private void exitTheDemo() {
        changePopupWindowState();
        new AlertDialog.Builder(MainActivity.this).setMessage("确定退出这个 Demo 吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("取消", null).show();
    }
    /**
     * 改变 PopupWindow 的显示和隐藏
     */
    private void changePopupWindowState() {
        if (pop.isShowing()) {
            // 隐藏窗口，如果设置了点击窗口外消失，则不需要此方式隐藏
            pop.dismiss();
        } else {
            // 弹出窗口显示内容视图,默认以锚定视图的左下角为起点，这里为点击按钮
            pop.showAtLocation(hideView, Gravity.BOTTOM, 0, 0);
        }
    }
    private Bitmap generateBitmap(String content, int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        try {
            BitMatrix encode = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (encode.get(j, i)) {
                        pixels[i * width + j] = 0x00000000;
                    } else {
                        pixels[i * width + j] = 0xffffffff;
                    }
                }
            }
            return Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    private SpannableString getClickableSpan() {

        //监听器
        View.OnClickListener listener = this;
        String content = getString(R.string.changeEmploy);
        SpannableString spanableInfo = new SpannableString(content);
        int start = 0;  //超链接起始位置
        int end = content.length();   //超链接结束位置

        //可以为多部分设置超链接
        spanableInfo.setSpan(new Clickable(listener), start, end, Spanned.SPAN_MARK_MARK);

        return spanableInfo;
    }
}

class Clickable extends ClickableSpan implements View.OnClickListener {
    private final View.OnClickListener mListener;

    public Clickable(View.OnClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View view) {
        mListener.onClick(view);
    }
}
