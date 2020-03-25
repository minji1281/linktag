package com.linktag.linkapp.ui.arclayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentResult;
import com.linktag.base.base_footer.BaseFooter;
import com.linktag.base.util.ScanCode;
import com.linktag.linkapp.ui.beacon.beaconMain;
import com.linktag.linkapp.ui.jdm.JdmRecycleAdapter;
import com.linktag.linkapp.ui.nfc.nfcMain;

import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.andreilisun.circular_layout.CircularLayout;
import com.google.zxing.integration.android.IntentIntegrator;
import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.linkapp.R;
import com.linktag.linkapp.ui.scanner.ScanBarcode;
import com.linktag.linkapp.ui.scanner.ScanResult;
import com.linktag.linkapp.value_object.CtdVO;
import com.ogaclejapan.arclayout.ArcLayout;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class arclayoutMain extends BaseActivity  implements View.OnClickListener {
    private BaseFooter footer;
    private static final String KEY_DEMO = "demo";
    Toast toast = null;
    View fab;
    View menuLayout;
    ArcLayout arcLayout;
    ImageButton btnExit;


    private CtdVO intentVO;

    public static void startActivity(Context context, Demo demo) {
        Intent intent = new Intent(context, arclayoutMain.class);
        intent.putExtra(KEY_DEMO, "BOTTOM");
        context.startActivity(intent);
    }

//    private static Demo getDemo(Intent intent) {
//        return Demo.valueOf(intent.getStringExtra(KEY_DEMO));
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_arclayout);


        initLayout();
        initialize();
       // Demo demo = getDemo(getIntent());

    //    ActionBar bar = getSupportActionBar();
//        bar.setTitle(demo.titleResId);
     //   bar.setDisplayHomeAsUpEnabled(true);
     //   bar.setHomeButtonEnabled(true);

        fab = findViewById(R.id.fab);
        menuLayout = findViewById(R.id.menu_layout);
        arcLayout = (ArcLayout) findViewById(R.id.arc_layout);

        btnExit = findViewById(R.id.btnExit);
        btnExit.setOnClickListener(v -> finish());

        for (int i = 0, size = arcLayout.getChildCount(); i < size; i++) {
            arcLayout.getChildAt(i).setOnClickListener(this);
        }

        fab.setOnClickListener(this);
        fab.performClick();


    }


    protected void initLayout() {
        intentVO = (CtdVO) getIntent().getSerializableExtra("intentVO");
        initLayoutByContractType();


    }

    protected void initialize() {

    }

    private void initLayoutByContractType(){
        footer = findViewById(R.id.footer);
        footer.btnFooterScan.setOnClickListener(v -> goScan());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            onFabClick(v);
            return;
        }

        if (v instanceof Button) {
            showToast((Button) v);
        }

    }

    private void showToast(Button btn) {
        if (toast != null) {
            toast.cancel();
        }

//        String text = "Clicked: " + btn.getText();
//        toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
//        toast.show();

        String text = btn.getText().toString();

        if(text.equals("QR")){
            goScan();
        }else if(text.equals("BEACON")){
            Intent intent = new Intent(mContext, beaconMain.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//        intent.putExtra("DSH_GB", mList.get(position).DSH_GB);
//        intent.putExtra(BoardMain.WORK_STATE, mList.get(position));
            mContext.startActivity(intent);
        }else if(text.equals("NFC")){
                    Intent intent = new Intent(mContext, nfcMain.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//        intent.putExtra("DSH_GB", mList.get(position).DSH_GB);
//        intent.putExtra(BoardMain.WORK_STATE, mList.get(position));
        mContext.startActivity(intent);
        }
    }

//        /**
//     * 스캔화면으로 이동한다.
//     */
//    private void goScan() {
//        IntentIntegrator integrator = new IntentIntegrator(this);
//        integrator.setCaptureActivity(ScanBarcode.class);
//        integrator.setOrientationLocked(false);
//        integrator.initiateScan();
//    }

    public void goScan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(ScanCode.class);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case IntentIntegrator.REQUEST_CODE:
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

                if (result.getFormatName() != null){
                    String str = result.getContents();
                    scanResultArc(str);
                }

                break;
        }
    }

    private void onFabClick(View v) {
        if (v.isSelected()) {
            hideMenu();
        } else {
            showMenu();
        }
        v.setSelected(!v.isSelected());
    }

    @SuppressWarnings("NewApi")
    private void showMenu() {
        menuLayout.setVisibility(View.VISIBLE);

        List<Animator> animList = new ArrayList<>();

        for (int i = 0, len = arcLayout.getChildCount(); i < len; i++) {
            animList.add(createShowItemAnimator(arcLayout.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(400);
        animSet.setInterpolator(new OvershootInterpolator());
        animSet.playTogether(animList);
        animSet.start();
    }

    @SuppressWarnings("NewApi")
    private void hideMenu() {

        List<Animator> animList = new ArrayList<>();

        for (int i = arcLayout.getChildCount() - 1; i >= 0; i--) {
            animList.add(createHideItemAnimator(arcLayout.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(400);
        animSet.setInterpolator(new AnticipateInterpolator());
        animSet.playTogether(animList);
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                menuLayout.setVisibility(View.INVISIBLE);
            }
        });
        animSet.start();
        finish();

    }

    private Animator createShowItemAnimator(View item) {

        float dx = fab.getX() - item.getX();
        float dy = fab.getY() - item.getY();

        item.setRotation(0f);
        item.setTranslationX(dx);
        item.setTranslationY(dy);

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(0f, 720f),
                AnimatorUtils.translationX(dx, 0f),
                AnimatorUtils.translationY(dy, 0f)
        );

        return anim;
    }

    private Animator createHideItemAnimator(final View item) {
        float dx = fab.getX() - item.getX();
        float dy = fab.getY() - item.getY();

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(720f, 0f),
                AnimatorUtils.translationX(0f, dx),
                AnimatorUtils.translationY(0f, dy)
        );

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                item.setTranslationX(0f);
                item.setTranslationY(0f);
            }
        });

        return anim;
    }


    protected void scanResultArc(String str){
        ScanResult scanResult = new ScanResult(mContext, str, null);
        scanResult.run();
    }

}