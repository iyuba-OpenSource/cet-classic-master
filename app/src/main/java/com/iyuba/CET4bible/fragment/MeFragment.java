package com.iyuba.CET4bible.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.iyuba.CET4bible.BuildConfig;
import com.iyuba.CET4bible.MyApplication;
import com.iyuba.CET4bible.activity.AboutActivity;
import com.iyuba.CET4bible.activity.MainApplication;
import com.iyuba.CET4bible.activity.SetActivity;
import com.iyuba.CET4bible.activity.login.QuickRegistrationActivity;
import com.iyuba.CET4bible.mvp.model.bean.UserBean;
import com.iyuba.CET4bible.mvp.presenter.MePresenter;
import com.iyuba.CET4bible.mvp.view.MeContract;
import com.iyuba.CET4bible.sqlite.op.NewTypeSectionAAnswerOp;
import com.iyuba.CET4bible.sqlite.op.NewTypeTextAOp;
import com.iyuba.abilitytest.sqlite.TestRecordHelper;
import com.iyuba.activity.sign.SignActivity;
import com.iyuba.biblelib.R;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.activity.LoginActivity;
import com.iyuba.core.activity.RegistByPhoneActivity;
import com.iyuba.core.activity.Web;
import com.iyuba.core.discover.activity.DiscoverForAtActivity;
import com.iyuba.core.eventbus.LoginEvent;
import com.iyuba.core.listener.ProtocolResponse;
import com.iyuba.core.manager.AccountManager;
import com.iyuba.core.me.activity.AttentionCenter;
import com.iyuba.core.me.activity.FansCenter;
import com.iyuba.core.me.activity.InfoFullFillActivity;
import com.iyuba.core.me.activity.NoticeCenter;
import com.iyuba.core.me.activity.StudyRankingActivity;
import com.iyuba.core.me.activity.VipCenterActivity;
import com.iyuba.core.me.pay.MD5;
import com.iyuba.core.mvp.model.bean.MoreInfoBean;
import com.iyuba.core.network.ClientSession;
import com.iyuba.core.network.IResponseReceiver;
import com.iyuba.core.protocol.BaseHttpRequest;
import com.iyuba.core.protocol.BaseHttpResponse;
import com.iyuba.core.protocol.base.GradeRequest;
import com.iyuba.core.protocol.base.GradeResponse;
import com.iyuba.core.protocol.base.LoginResponse;
import com.iyuba.core.protocol.message.RequestBasicUserInfo;
import com.iyuba.core.protocol.message.RequestNewInfo;
import com.iyuba.core.protocol.message.RequestUserDetailInfo;
import com.iyuba.core.protocol.message.ResponseBasicUserInfo;
import com.iyuba.core.protocol.message.ResponseNewInfo;
import com.iyuba.core.protocol.message.ResponseUserDetailInfo;
import com.iyuba.core.setting.SettingConfig;
import com.iyuba.core.sqlite.mode.UserInfo;
import com.iyuba.core.sqlite.mode.me.Emotion;
import com.iyuba.core.teacher.activity.CommunityActivity;
import com.iyuba.core.teacher.activity.QuesListActivity;
import com.iyuba.core.teacher.activity.QuestionNotice;
import com.iyuba.core.teacher.activity.TheQuesListActivity;
import com.iyuba.core.util.CheckGrade;
import com.iyuba.core.util.ExeProtocol;
import com.iyuba.core.util.Expression;
import com.iyuba.core.util.TouristUtil;
import com.iyuba.core.widget.dialog.CustomToast;
import com.iyuba.imooclib.ImoocManager;
import com.iyuba.module.dl.DLActivity;
import com.iyuba.module.favor.ui.BasicFavorActivity;
import com.iyuba.module.intelligence.ui.LearningGoalActivity;
import com.iyuba.module.intelligence.ui.WordResultActivity;
import com.iyuba.module.user.IyuUserManager;
import com.iyuba.module.user.User;
import com.iyuba.wordtest.db.CetDataBase;
import com.iyuba.wordtest.manager.WordConfigManager;
import com.mob.secverify.SecVerify;
import com.mob.secverify.VerifyCallback;
import com.mob.secverify.common.exception.VerifyException;
import com.mob.secverify.datatype.UiSettings;
import com.mob.secverify.datatype.VerifyResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import personal.iyuba.personalhomelibrary.ui.groupChat.GroupChatManageActivity;
import personal.iyuba.personalhomelibrary.ui.home.PersonalHomeActivity;
import personal.iyuba.personalhomelibrary.ui.message.MessageActivity;

/**
 * 类名
 *
 * @author 作者 <br/>
 * 实现的主要功能。 创建日期 修改者，修改日期，修改内容。
 */
public class MeFragment extends Fragment implements MeContract.MeView {
    private View noLogin, login; // 登录提示面板
    private Button loginBtn, logout;
    private Context mContext;
    private ImageView photo;
    private TextView name, state, fans, attention, notification, listem_time,
            integral, position, lv;
    private View person, local_panel;
    private View stateView, messageView, vipView;
    private View local, favor, read, back;
    private View attentionView, fansView, notificationView, integralView,
            discover_rqlist, discover_qnotice, discover_myq, discover_mysub, speak_circle;
    private UserInfo userInfo;
    private View root;
    private boolean showLocal;
    private View intel_userinfo;
    private View intel_learn_goal;
    private View intel_result;
    private View intel_test_result;
    private View intel_word_result;
    private View study_ranking;
    private TextView money;

    private View tv_sign;

    private LinearLayout agreement;

    private boolean bInfoFullFill = false;
    private boolean bLearnTarget = false;
    private ResponseUserDetailInfo userDetailInfo;
    private View me_discover;
    private View me_privilege;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private String date = sdf.format(new Date());

    private MePresenter mePresenter;


    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {


            switch (msg.what) {
                case 0:
                    CustomToast.showToast(mContext, R.string.check_network);
                    break;
                case 2:
                    root.findViewById(R.id.newletter).setVisibility(View.VISIBLE);
                    break;
                case 3:
                    setTextViewContent();
                    break;
                case 4:


                    new NewTypeTextAOp(mContext).clearRecord();
                    TestRecordHelper.getInstance(requireContext()).clearAbilityResult();
                    //清楚答题记录
                    new NewTypeSectionAAnswerOp(requireContext()).resetAnswer();
                    //清楚闯关记录
                    CetDataBase db = CetDataBase.getInstance(requireContext());
                    db.getCetRootWordDao().reset();
                    WordConfigManager.Instance(requireActivity()).putInt("stage", 1);

                    AccountManager.Instace(mContext).loginOut();
                    resetLogoutStatus();
                    IyuUserManager.getInstance().logout();
                    CustomToast.showToast(mContext, R.string.loginout_success);
                    SettingConfig.Instance().setHighSpeed(false);
                    onResume();
                    break;
                case 5:
                    root.findViewById(R.id.newletter).setVisibility(View.GONE);
                    break;
                case 6:
                    Dialog dialog = new AlertDialog.Builder(mContext)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("提示")
                            .setMessage(
                                    "亲,请先完善个人信息~")
                            .setPositiveButton("确定", null)
                            .create();
                    dialog.show();// 如果要显示对话框，一定要加上这句
                    break;
                case 7:
                    Dialog dialog2 = new AlertDialog.Builder(mContext)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("提示")
                            .setMessage(
                                    "亲,请先补充您的学习目标~")
                            .setPositiveButton("确定", null)
                            .create();
                    dialog2.show();// 如果要显示对话框，一定要加上这句
                    break;
            }
            return false;
        }
    });

    private View me_top;

    private void resetLogoutStatus() {
        ImoocManager.appId = Constant.APPID;
        User user = new User();
        user.vipStatus = "0";
        user.name = "";
        user.uid = 0;
    }


    /**
     * 登录
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void verifyLogin(LoginEvent loginEvent) {

        UiSettings.Builder builder = new UiSettings.Builder();
        builder.setSwitchAccText("其他方式登录");
        builder.setLoginBtnTextSize(14);
        builder.setImmersiveTheme(true);
        builder.setImmersiveStatusTextColorBlack(true);
        builder.setCusAgreementNameId1("《用户协议》");
        builder.setCusAgreementUrl1(Constant.USE_URL + Constant.APPName + "&company=1");
        builder.setCusAgreementNameId2("《隐私协议》");
        builder.setCusAgreementUrl2(Constant.PRI_URL + Constant.APPName + "&company=1");

        SecVerify.setUiSettings(builder.build());
        SecVerify.autoFinishOAuthPage(false);
        SecVerify.verify(new VerifyCallback() {
            @Override
            public void onOtherLogin() {

                startActivity(new Intent(requireActivity(), LoginActivity.class));
                SecVerify.finishOAuthPage();
            }

            @Override
            public void onUserCanceled() {
                // 用户点击“关闭按钮”或“物理返回键”取消登录，处理自己的逻辑
            }

            @Override
            public void onComplete(VerifyResult verifyResult) {

                // 获取授权码成功，将token信息传给应用服务端，再由应用服务端进行登录验证，此功能需由开发者自行实现
                // opToken
                String opToken = verifyResult.getOpToken();
                // token
                String token = verifyResult.getToken();
                // 运营商类型，[CMCC:中国移动，CUCC：中国联通，CTCC：中国电信]
                String operator = verifyResult.getOperator();

                String tokenEncoder;
                try {
                    tokenEncoder = URLEncoder.encode(token, "utf-8");
//                    tokenEncoder = URLEncoder.encode(tokenEncoder, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                mePresenter.mobSecVerifyLogin(10010, Integer.parseInt(Constant.APPID), Constant.SMSAPPID, opToken, operator, tokenEncoder);
            }

            @Override
            public void onFailure(VerifyException e) {

                SecVerify.finishOAuthPage();
                int code = e.getCode();
                if (6119098 == code) {

                    Toast.makeText(MainApplication.getContext(), "网络异常", Toast.LENGTH_SHORT).show();
                } else {

                    startActivity(new Intent(requireActivity(), LoginActivity.class));
                }
                //TODO处理失败的结果
                Log.d("VerifyException", e.getMessage());
            }
        });
    }

    private OnClickListener ocl = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            Intent intent;
            int id = arg0.getId();
            if (id == R.id.personalhome) {
                if (AccountManager.Instace(mContext).checkUserLogin()
                        && !TouristUtil.isTourist()) {
                    startActivity(PersonalHomeActivity.buildIntent(getContext(),
                            Integer.parseInt(AccountManager.Instace(mContext).userId),
                            AccountManager.Instace(mContext).userName, 0));
                }
            } else if (id == R.id.me_state_change) {
                if (AccountManager.Instace(mContext).checkUserLogin() && !TouristUtil.isTourist()) {
//                    GroupChatManageActivity.start(mContext,10016,"VOA慢速英语",true);
                    GroupChatManageActivity.start(mContext, 10106, "CET官方群", true);
                } else {
                    verifyLogin(null);
                }
            } else if (id == R.id.me_vip) {
                intent = new Intent(mContext, VipCenterActivity.class);
                startActivity(intent);

            } else if (id == R.id.me_privilege) {
                if (AccountManager.Instace(mContext).checkUserLogin()) {
                    if (TouristUtil.isTourist()) {
                        TouristUtil.showTouristHint(mContext);
                        return;
                    }
                    intent = new Intent();
                    intent.setClass(mContext, Web.class);
                    intent.putExtra("url", "http://vip.iyuba.cn/mycode.jsp?"
                            + "uid=" + AccountManager.Instace(mContext).userId
                            + "&appid=" + Constant.APPID
                            + "&sign=" + MD5.getMD5ofStr(AccountManager.Instace(mContext).userId + "iyuba" + Constant.APPID + date));
                    intent.putExtra("title",
                            "优惠券");
                    startActivity(intent);
                } else {
                    verifyLogin(null);
                }
            } else if (id == R.id.me_discover) {
                if (AccountManager.Instace(mContext).checkUserLogin()) {
                    intent = new Intent(mContext, DiscoverForAtActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtils.showShort("请登录正式账号");
                }

            } else if (id == R.id.me_message) {
                if (AccountManager.Instace(mContext).checkUserLogin()
                        && !TouristUtil.isTourist()) {
                    intent = new Intent(mContext, MessageActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtils.showShort("请登录正式账号");
                }

            } else if (id == R.id.attention_area) {
                if (AccountManager.Instace(mContext).checkUserLogin()) {
                    Intent intent1 = new Intent(getActivity(), AttentionCenter.class);
                    intent1.putExtra("userid", AccountManager.Instace(mContext).userId);
                    startActivity(intent1);
                } else {
                    ToastUtils.showShort("请登录正式账号");
                }
            } else if (id == R.id.fans_area) {
                if (AccountManager.Instace(mContext).checkUserLogin()) {
                    Intent intent2 = new Intent(getActivity(), FansCenter.class);
                    intent2.putExtra("userid", AccountManager.Instace(mContext).userId);
                    startActivity(intent2);
                } else {
                    ToastUtils.showShort("请登录正式账号");
                }
            } else if (id == R.id.notification_area) {
                intent = new Intent(mContext, NoticeCenter.class);
                intent.putExtra("userid", AccountManager.Instace(mContext).userId);
                startActivity(intent);
            } else if (id == R.id.Integral) {
                if (AccountManager.Instace(mContext).checkUserLogin()) {

                    intent = new Intent(mContext, Web.class);
                    intent.putExtra("title", "积分兑换");
                    intent.putExtra("url", "http://m.iyuba.cn/mall/index.jsp?"
                            + "&uid=" + AccountManager.Instace(mContext).getId()
                            + "&sign=" + com.iyuba.core.util.MD5.getMD5ofStr("iyuba" + AccountManager.Instace(mContext).getId() + "camstory")
                            + "&username=" + AccountManager.Instace(mContext).getUserName()
                            + "&platform=android&appid="
                            + Constant.APPID);
//                    intent.putExtra("url",
//                            "http://api.iyuba.cn/credits/useractionrecordmobileList1.jsp?uid="
//                                    + AccountManager.Instace(mContext).userId);
                    startActivity(intent);
                } else {
                    ToastUtils.showShort("请登录正式账号");
                }
            } else if (id == R.id.discover_rqlist) {
                if (AccountManager.Instace(mContext).checkUserLogin()) {
                    intent = new Intent();
                    intent.setClass(mContext, QuesListActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtils.showShort("请登录正式账号");
                }

            } else if (id == R.id.discover_qnotice) {
                intent = new Intent();
                intent.setClass(mContext, QuestionNotice.class);
                startActivity(intent);
            } else if (id == R.id.discover_myq) {
                if (AccountManager.Instace(mContext).checkUserLogin()) {
                    intent = new Intent();
                    intent.setClass(mContext, TheQuesListActivity.class);
                    intent.putExtra("utype", "4");
                    startActivity(intent);
                } else {
                    ToastUtils.showShort("请登录正式账号");
                }

            } else if (id == R.id.discover_mysub) {
                if (AccountManager.Instace(mContext).checkUserLogin()) {

                    intent = new Intent();
                    intent.setClass(mContext, TheQuesListActivity.class);
                    intent.putExtra("utype", "2");
                    startActivity(intent);
                } else {
                    ToastUtils.showShort("请登录正式账号");
                }
            } else if (id == R.id.speak_circle) {
                if (AccountManager.Instace(mContext).checkUserLogin()) {
                    // TODO: 2022/7/12 口语圈跳转个人中心界面
//                    SpeakCircleActivity.instance(mContext, Constant.APP_CONSTANT.mListen());
                    Log.d("bible", Constant.APP_CONSTANT.mListen());
                } else {
                    ToastUtils.showShort("请登录正式账号");
                }
            } else if (id == R.id.intel_userinfo) {

                if (AccountManager.Instace(mContext).checkUserLogin()) {

                    intent = new Intent();
                    intent.setClass(mContext, InfoFullFillActivity.class);
                    startActivity(intent);
                } else {

                    verifyLogin(null);
                }
            } else if (id == R.id.intel_goal) {
                if (bInfoFullFill) {
                    try {
                        startActivity(LearningGoalActivity.buildIntent(mContext));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                } else {
                    handler.sendEmptyMessage(6);
                }
            } else if (id == R.id.intel_result) {
                if (bLearnTarget) {
                    try {
                        startActivity(com.iyuba.module.intelligence.ui.LearnResultActivity.buildIntent(mContext));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                } else {
                    handler.sendEmptyMessage(7);
                }
            } else if (id == R.id.intel_test_result) {
                if (bLearnTarget) {
                    try {
                        startActivity(com.iyuba.module.intelligence.ui.TestResultActivity.buildIntent(mContext));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                } else {
                    handler.sendEmptyMessage(7);
                }
            } else if (id == R.id.intel_word_result) {
                if (bLearnTarget) {
                    try {
                        startActivity(WordResultActivity.buildIntent(mContext));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                } else {
                    handler.sendEmptyMessage(7);
                }

            } else if (id == R.id.study_ranking) {
                if (AccountManager.Instace(mContext).checkUserLogin()) {
                    intent = new Intent();
                    intent.setClass(mContext, StudyRankingActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtils.showShort("请登录正式账号");
                }
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        EventBus.getDefault().register(this);

        mePresenter = new MePresenter();
        mePresenter.attchView(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mePresenter != null) {

            mePresenter.detachView();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.layout_me, container, false);
        initView(root);
        return root;
    }

    private void initView(View root) {
        person = root.findViewById(R.id.personalhome);
        me_top = root.findViewById(R.id.me_top);
        photo = root.findViewById(R.id.me_pic);
        name = root.findViewById(R.id.me_name);
        state = root.findViewById(R.id.me_state);
        attention = root.findViewById(R.id.me_attention);
        money = root.findViewById(R.id.money);
        listem_time = root.findViewById(R.id.me_listem_time);
        position = root.findViewById(R.id.me_position);
        lv = root.findViewById(R.id.lv);
        fans = root.findViewById(R.id.me_fans);
        notification = root.findViewById(R.id.me_notification);

        integral = root.findViewById(R.id.Integral_notification);
        stateView = root.findViewById(R.id.me_state_change);
        vipView = root.findViewById(R.id.me_vip);
        me_privilege = root.findViewById(R.id.me_privilege);
        me_discover = root.findViewById(R.id.me_discover);
        messageView = root.findViewById(R.id.me_message);
        attentionView = root.findViewById(R.id.attention_area);
        fansView = root.findViewById(R.id.fans_area);

        discover_rqlist = root.findViewById(R.id.discover_rqlist);
        discover_qnotice = root.findViewById(R.id.discover_qnotice);
        discover_myq = root.findViewById(R.id.discover_myq);
        discover_mysub = root.findViewById(R.id.discover_mysub);
        speak_circle = root.findViewById(R.id.speak_circle);

        //智能化学习
        intel_userinfo = root.findViewById(R.id.intel_userinfo);
        intel_learn_goal = root.findViewById(R.id.intel_goal);
        intel_result = root.findViewById(R.id.intel_result);
        intel_word_result = root.findViewById(R.id.intel_word_result);
        intel_test_result = root.findViewById(R.id.intel_test_result);
        study_ranking = root.findViewById(R.id.study_ranking);

        notificationView = root.findViewById(R.id.notification_area);
        integralView = root.findViewById(R.id.Integral);

        noLogin = root.findViewById(R.id.noLogin);
        login = root.findViewById(R.id.login);
        logout = root.findViewById(R.id.logout);
        back = root.findViewById(R.id.button_back);
        back.setVisibility(View.GONE);

        agreement = root.findViewById(R.id.agreement);

        agreement.setOnClickListener(v -> {
            showPermissionDialog();
        });

        tv_sign = root.findViewById(R.id.tv_sign);
        root.findViewById(R.id.ll_about).setOnClickListener(v -> startActivity(new Intent(mContext, AboutActivity.class)));
        root.findViewById(R.id.discover_iyubaset).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, SetActivity.class));
            }
        });
        root.findViewById(R.id.rl_favorite).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccountManager.Instace(mContext).checkUserLogin()) {
                    startActivity(BasicFavorActivity.buildIntent(mContext));
                } else {
                    ToastUtils.showShort("请登录正式账号");
                }
            }
        });
        root.findViewById(R.id.rl_download).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, DLActivity.class));
            }
        });
        root.findViewById(R.id.rl_community).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfigManager.Instance().putInt("quesAbilityType", 0);
                int type = 0;
                if (BuildConfig.isEnglish) {
                    if (BuildConfig.isCET4) {
                        type = 4;
                    } else {
                        type = 5;
                    }
                } else {
                    String tttt = Constant.APP_CONSTANT.TYPE();

                    if (tttt.equals("1")) {
                        type = 7;
                    } else if (tttt.equals("2")) {
                        type = 8;
                    } else if (tttt.equals("3")) {
                        type = 9;
                    }
                }
                ConfigManager.Instance().putInt("quesAppType", type + 100);
                startActivity(new Intent(mContext, CommunityActivity.class));
            }
        });

    }

    private void showPermissionDialog() {
//        String usageUrl = Constant.USE_URL + Constant.APPName + "&company="+Constant.COMPANY_NAME;
//        String priUrl = Constant.PRI_URL + Constant.APPName + "&company="+Constant.COMPANY_NAME;
        String usageUrl = Constant.USE_URL + Constant.APPName + "&company=1";
        String priUrl = Constant.PRI_URL + Constant.APPName + "&company=1";
        final String[] items = {"使用协议", "隐私政策"};
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
//                    Web.start(mContext, "https://ai."+WebConstant.WEB_SUFFIX+"api/protocoluse666.jsp?apptype=" + Constant.AppName + "&company=爱语吧", "用户使用协议");
                    Web.start(mContext, usageUrl, "用户使用协议");
                } else if (which == 1) {
//                    Web.start(mContext, "https://ai."+WebConstant.WEB_SUFFIX+"api/protocolpri.jsp?apptype=" + Constant.AppName + "&company=1", "用户隐私政策");
                    Web.start(mContext, priUrl, "用户隐私政策");
                }
            }
        });
        builder.show();
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        showLocal = args.getBoolean("showLocal");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (getUserVisibleHint() && isVisibleToUser) {
            checkLogin();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onResume() {
        super.onResume();
        viewChange();
        init();
        verifyUsrInfoAndTarget();
    }

    private void verifyUsrInfoAndTarget() {
        ExeProtocol.exe(new RequestUserDetailInfo(AccountManager.Instace(mContext).userId),
                new ProtocolResponse() {

                    @Override
                    public void finish(BaseHttpResponse bhr) {
                        ResponseUserDetailInfo responseUserDetailInfo = (ResponseUserDetailInfo) bhr;
                        if (responseUserDetailInfo.result.equals("211")) {
                            userDetailInfo = responseUserDetailInfo;
                            bInfoFullFill = !userDetailInfo.gender.equals("")
                                    && !userDetailInfo.birthday.equals("")
                                    && !userDetailInfo.resideLocation.equals("")
                                    && !userDetailInfo.occupation.equals("")
                                    && !userDetailInfo.education.equals("")
                                    && !userDetailInfo.graduateschool.equals("");

                            if (!userDetailInfo.editUserInfo.getPlevel().equals("") &&
                                    !userDetailInfo.editUserInfo.getPtalklevel().equals("") &&
                                    !userDetailInfo.editUserInfo.getPreadlevel().equals("") &&
                                    !userDetailInfo.editUserInfo.getGlevel().equals("") &&
                                    !userDetailInfo.editUserInfo.getGtalklevel().equals("") &&
                                    !userDetailInfo.editUserInfo.getGreadlevel().equals("") &&
                                    Integer.valueOf(userDetailInfo.editUserInfo.getPlevel()) > 0
                                    && Integer.valueOf(userDetailInfo.editUserInfo.getPtalklevel()) > 0
                                    && Integer.valueOf(userDetailInfo.editUserInfo.getPreadlevel()) > 0
                                    && Integer.valueOf(userDetailInfo.editUserInfo.getGlevel()) > 0
                                    && Integer.valueOf(userDetailInfo.editUserInfo.getGtalklevel()) > 0
                                    && Integer.valueOf(userDetailInfo.editUserInfo.getGreadlevel()) > 0)
                                bLearnTarget = true;
                        } else {
                            bLearnTarget = false;
                        }
                    }

                    @Override
                    public void error() {
                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    private void viewChange() {
        if (checkLogin()) {
            userInfo = AccountManager.Instace(mContext).userInfo;
            ClientSession.Instance()
                    .asynGetResponse(
                            new RequestNewInfo(
                                    AccountManager.Instace(mContext).userId),
                            new IResponseReceiver() {
                                @Override
                                public void onResponse(
                                        BaseHttpResponse response,
                                        BaseHttpRequest request, int rspCookie) {
                                    ResponseNewInfo rs = (ResponseNewInfo) response;
                                    if (rs.letter > 0) {
                                        handler.sendEmptyMessage(2);
                                    } else {
                                        handler.sendEmptyMessage(5);
                                    }
                                }


                            });
            loadData();
        }
    }

    public boolean checkLogin() {
        if (!AccountManager.Instace(mContext).checkUserLogin()) {
            noLogin.setVisibility(View.VISIBLE);
            me_top.setVisibility(View.GONE);
            login.setVisibility(View.VISIBLE);
            loginBtn = root.findViewById(R.id.button_to_login);
            loginBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    verifyLogin(null);
                }
            });
            logout.setVisibility(View.GONE);
            tv_sign.setVisibility(View.GONE);
            return false;
        } else {
            me_top.setVisibility(View.VISIBLE);
            noLogin.setVisibility(View.GONE);
            login.setVisibility(View.VISIBLE);
            logout.setVisibility(View.VISIBLE);
            tv_sign.setVisibility(View.VISIBLE);
            if (TouristUtil.isTourist()) {
                logout.setText("注册/登录");
                logout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        verifyLogin(null);
                    }
                });
                tv_sign.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext, "请注册正式用户后再进行打卡", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                logout.setText(getString(R.string.logout));
                tv_sign.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, SignActivity.class);
                        startActivity(intent);
                    }
                });
                logout.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        AlertDialog dialog = new AlertDialog.Builder(mContext)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle(getResources().getString(R.string.alert_title))
                                .setMessage(getResources().getString(R.string.logout_alert))
                                .setPositiveButton(getResources().getString(R.string.alert_btn_ok),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int whichButton) {
                                                handler.sendEmptyMessage(4);
                                            }
                                        })
                                .setNeutralButton(getResources().getString(R.string.alert_btn_cancel),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                            }
                                        }).create();
                        dialog.show();
                    }
                });
            }
            return true;
        }
    }

    /**
     *
     */
    private void loadData() {
        final String id = AccountManager.Instace(mContext).getId();
        init();
        ExeProtocol.exe(new RequestBasicUserInfo(id, id),
                new ProtocolResponse() {

                    @Override
                    public void finish(BaseHttpResponse bhr) {
                        ResponseBasicUserInfo response = (ResponseBasicUserInfo) bhr;
                        userInfo = response.userInfo;
                        AccountManager.Instace(mContext).setUserInfo(userInfo);
                        handler.sendEmptyMessage(3);
                        Looper.prepare();
                        ExeProtocol.exe(new GradeRequest(id),
                                new ProtocolResponse() {

                                    @Override
                                    public void finish(BaseHttpResponse bhr) {
                                        GradeResponse response = (GradeResponse) bhr;
                                        userInfo.studytime = Integer
                                                .parseInt(response.totalTime);
                                        userInfo.position = response.positionByTime;
                                        handler.sendEmptyMessage(3);
                                    }

                                    @Override
                                    public void error() {
                                        handler.sendEmptyMessage(0);
                                    }
                                });
                        Looper.loop();
                    }

                    @Override
                    public void error() {
                    }
                });
    }

    private void refreshIntegral() {
        final String id = AccountManager.Instace(mContext).getId();
        ExeProtocol.exe(new RequestBasicUserInfo(id, id),
                new ProtocolResponse() {

                    @Override
                    public void finish(BaseHttpResponse bhr) {
                        ResponseBasicUserInfo response = (ResponseBasicUserInfo) bhr;
                        userInfo = response.userInfo;
                        AccountManager.Instace(mContext).setUserInfo(userInfo);
                        handler.sendEmptyMessage(3);
                    }

                    @Override
                    public void error() {
                    }
                });
    }

    /**
     *
     */
    private void init() {
        if (AccountManager.Instace(mContext).isteacher.equals("1")) {
            discover_mysub.setVisibility(View.GONE);
            discover_myq.setVisibility(View.VISIBLE);
        } else {
            discover_myq.setVisibility(View.GONE);
            discover_mysub.setVisibility(View.VISIBLE);
        }

        setViewClick();
        if (userInfo != null) {
            setTextViewContent();
        }
    }

    /**
     *
     */
    private void setViewClick() {
        person.setOnClickListener(ocl);
        stateView.setOnClickListener(ocl);
        vipView.setOnClickListener(ocl);
        me_privilege.setOnClickListener(ocl);
        me_discover.setOnClickListener(ocl);
        messageView.setOnClickListener(ocl);
        attentionView.setOnClickListener(ocl);
        fansView.setOnClickListener(ocl);
        stateView.setOnClickListener(ocl);
        notificationView.setOnClickListener(ocl);
        integralView.setOnClickListener(ocl);
        discover_rqlist.setOnClickListener(ocl);
        discover_qnotice.setOnClickListener(ocl);
        discover_myq.setOnClickListener(ocl);
        discover_mysub.setOnClickListener(ocl);
        speak_circle.setOnClickListener(ocl);

        intel_userinfo.setOnClickListener(ocl);
        intel_learn_goal.setOnClickListener(ocl);
        intel_result.setOnClickListener(ocl);
        intel_word_result.setOnClickListener(ocl);
        intel_test_result.setOnClickListener(ocl);
        study_ranking.setOnClickListener(ocl);

    }


    public void setTextViewContent() {

        Glide.with(MyApplication.getInstance())
                .load("http://api.iyuba.com.cn/v2/api.iyuba?protocol=10005&uid=" +
                        AccountManager.Instace(mContext).userId + "&size=middle")
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(photo);

        if (TouristUtil.isTourist()) { // 临时账户显示为uId
            name.setText(AccountManager.Instace(mContext).userId);
        } else {
            name.setText(userInfo.username);
        }

        if (ConfigManager.Instance().loadInt("isvip") > 0) {
            Drawable img = mContext.getResources().getDrawable(R.drawable.vip);
            img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
            name.setCompoundDrawables(null, null, img, null);
        } else {
            Drawable img = mContext.getResources().getDrawable(
                    R.drawable.no_vip);
            img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
            name.setCompoundDrawables(null, null, img, null);
        }
        if (userInfo.text == null) {
            state.setText(R.string.social_default_state);
        } else {

            String zhengze = "image[0-9]{2}|image[0-9]";
            Emotion emotion = new Emotion();
            userInfo.text = emotion.replace(userInfo.text);
            SpannableString spannableString = Expression.getExpressionString(
                    mContext, userInfo.text, zhengze);
            state.setText(spannableString);
        }
        if (TextUtils.isEmpty(userInfo.money)) {
            money.setText(String.format("钱包余额:%s", "0"));
        } else {
            final double f = Integer.parseInt(userInfo.money) * 0.01;
            money.setText(String.format("钱包余额:%s", String.format("%.2f", f)));
            money.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMoneyDialog(String.format("%.2f", f));
                }
            });
        }


        attention.setText(userInfo.following);
        fans.setText(userInfo.follower);
        listem_time.setText(exeStudyTime());
        position.setText(exePosition());
        lv.setText(exeIyuLevel());
        notification.setText(userInfo.notification);
        integral.setText(userInfo.icoins);
    }

    private void showMoneyDialog(String money) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("当前钱包余额" + money + "元,满10元可在[爱语吧]微信公众号提现(关注绑定爱语吧账号),每天坚持打卡分享,获得更多红包吧!")
                .setTitle("提示")
                .setPositiveButton("确定", null);
        builder.show();
    }

    private String exeStudyTime() {
        StringBuffer sb = new StringBuffer(
                mContext.getString(R.string.me_study_time));
        int time = userInfo.studytime;
        int minus = time % 60;
        int minute = time / 60 % 60;
        int hour = time / 60 / 60;
        if (hour > 0) {
            sb.append(hour).append(mContext.getString(R.string.me_hour));
        } else if (minute > 0) {
            sb.append(minute).append(mContext.getString(R.string.me_minute));
        } else {
            sb.append(minus).append(mContext.getString(R.string.me_minus));
        }
        return sb.toString();
    }

    private String exePosition() {
        StringBuffer sb = new StringBuffer(
                mContext.getString(R.string.me_study_position));
        int position = Integer.parseInt(userInfo.position);

        if (position < 10000) {
            sb.append(position);
        } else {
            sb.append(position / 10000 * 10000).append("+");
        }
        return sb.toString();
    }

    private String exeIyuLevel() {
        StringBuffer sb = new StringBuffer("");
        sb.append(mContext.getString(R.string.me_lv));
        sb.append(CheckGrade.Check(userInfo.icoins));
        sb.append(" ");
        sb.append(CheckGrade.CheckLevelName(userInfo.icoins));
        return sb.toString();
    }

    @Override
    public void showLoading(String msg) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void toast(String msg) {

        Toast.makeText(MyApplication.getInstance(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void moreInfoComplete(MoreInfoBean moreInfoBean) {

    }

    @Override
    public void mobSecVerifyLogin(UserBean userBean) {

        if (userBean.getIsLogin().equalsIgnoreCase("1")) {//登录成功

            LoginResponse tmpResp = new LoginResponse();
            tmpResp.amount = userBean.getUserinfo().getAmount();
            tmpResp.imgsrc = userBean.getUserinfo().getImgSrc();
//            tmpResp.money = userBean.getUserinfo().getMoney()
            tmpResp.result = userBean.getUserinfo().getResult();
            tmpResp.uid = String.valueOf(userBean.getUserinfo().getUid());
            tmpResp.username = userBean.getUserinfo().getUsername();
            tmpResp.vip = userBean.getUserinfo().getVipStatus();
            tmpResp.validity = String.valueOf(userBean.getUserinfo().getExpireTime());
            AccountManager.Instace(requireContext()).Refresh(tmpResp);
        } else {//没有注册

            if (userBean.getRes().getPhone() != null || !userBean.getRes().getPhone().equals("")) {//跳快捷注册页面

                QuickRegistrationActivity.startActivity(requireActivity(), userBean.getRes().getPhone());
            } else {

                startActivity(new Intent(requireActivity(), RegistByPhoneActivity.class));
            }
        }
        SecVerify.finishOAuthPage();
    }
}
