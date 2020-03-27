package com.linktag.linkapp.ui.beacon;

import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.linkapp.R;
import com.linktag.linkapp.ui.scanner.ScanResult;

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

public class beaconMain extends BaseActivity implements BeaconConsumer {
    private BeaconManager beaconManager;

    private List<Beacon> beaconList = new ArrayList<>();
    TextView textView;
    ImageView searchload;
    String bcuuid ="";
    ImageButton btnExit;
    Button button;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);
        textView = (TextView) findViewById(R.id.Textview);
        searchload = (ImageView)  findViewById(R.id.searchload);
        button = (Button)  findViewById(R.id.button);
        btnExit = findViewById(R.id.btnExit);
        btnExit.setOnClickListener(v -> finish());

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

        beaconManager.bind(this);
        button.performClick();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    beaconList.clear();
                    for (Beacon beacon : beacons) {
                        beaconList.add(beacon);
                    }
                }
            }
        });
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
        }
    }

    public void OnButtonClicked(View view) {
        handler.sendEmptyMessage(0);
    }
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            searchload.startAnimation(  AnimationUtils.loadAnimation( beaconMain.this,  R.anim.load_change ) );
//            textView.setText("");
            for (Beacon beacon : beaconList) {
                if(Double.parseDouble(String.format("%.3f", beacon.getDistance())) <= 1.0){
                    textView.setText("장치 : " + beacon.getBluetoothName()  + "\n " + "거리 : " + Double.parseDouble(String.format("%.3f", beacon.getDistance())) + "m\n");
                    bcuuid = beacon.getId1().toString();
                }
            }
            if(bcuuid.equals("")|| bcuuid.equals(null)){
                handler.sendEmptyMessageDelayed(0, 1000);
            }else{
                handler.removeMessages(0);
                searchload.clearAnimation();
            }
        }
    };

    public void OnButtonClicked2(View view) {
      //  handler.removeMessages(0);
      //  searchload.clearAnimation();
        if(bcuuid.equals("")|| bcuuid.equals(null))
        {
            Toast.makeText(this, "검색된 비콘이 없습니다. \n 찾기를 실행해 주세요.", Toast.LENGTH_LONG).show();

        }else{
            scanResult("http://www.linktag.io/scan?t=BEACON&u="+mUser.Value.OCM_01+"&s="+ bcuuid);
        }
    }


    @Override
    protected void initLayout() {
    }

    @Override
    protected void initialize() {

    }

    @Override
    protected void scanResult(String str){
        ScanResult scanResult = new ScanResult(mContext, str, null);
        scanResult.run();
    }
}