package project.richthofen911.callofdroidy.beacon;

import android.app.Activity;
import android.os.RemoteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.perples.recosdk.RECOBeacon;
import com.perples.recosdk.RECOBeaconManager;
import com.perples.recosdk.RECOBeaconRegion;
import com.perples.recosdk.RECOErrorCode;
import com.perples.recosdk.RECOProximity;
import com.perples.recosdk.RECORangingListener;
import com.perples.recosdk.RECOServiceConnectListener;

import java.util.ArrayList;
import java.util.Collection;


public class ActivityBeaconDetectionByRECO extends Activity implements RECOServiceConnectListener, RECORangingListener{

    protected final boolean DISCONTINUOUS_SCAN = false;

    protected boolean entered = false;
    protected int exitCount = 0;
    protected boolean exited = false;
    protected int rssiBorder = 0;
    protected boolean startImmediately = false;

    protected RECOBeaconManager mRecoManager = RECOBeaconManager.getInstance(this, false, false);
    protected ArrayList<RECOBeaconRegion> definedRegions;

    protected void assignRegionArgs(String uuid, int borderValue, boolean start){
        definedRegions = generateBeaconRegion(uuid);
        rssiBorder = borderValue;
        startImmediately = start;
    }

    protected void assignRegionArgs(String uuid, int major, int borderValue, boolean start){
        definedRegions = generateBeaconRegion(uuid, major);
        rssiBorder = borderValue;
        startImmediately = start;
    }

    protected void assignRegionArgs(String uuid, int major, int minor, int borderValue, boolean start){
        definedRegions = generateBeaconRegion(uuid, major, minor);
        rssiBorder = borderValue;
        startImmediately = start;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRecoManager.setRangingListener(this);
        mRecoManager.bind(this);
    }

    protected void start(ArrayList<RECOBeaconRegion> regions) {
        for(RECOBeaconRegion region : regions) {
            try {
                mRecoManager.startRangingBeaconsInRegion(region);
                Log.e("start detecting", region.describeContents() + "");
            } catch (RemoteException e) {
                Log.i("RECORangingActivity", "Remote Exception");
                e.printStackTrace();
            } catch (NullPointerException e) {
                Log.i("RECORangingActivity", "Null Pointer Exception");
                e.printStackTrace();
            }
        }
    }

    protected void stop(ArrayList<RECOBeaconRegion> regions) {
        Log.e("stop detecting", "...");
        for(RECOBeaconRegion region : regions) {
            try {
                mRecoManager.stopRangingBeaconsInRegion(region);
                entered = false;
            } catch (RemoteException e) {
                Log.i("RECORangingActivity", "Remote Exception");
                e.printStackTrace();
            } catch (NullPointerException e) {
                Log.i("RECORangingActivity", "Null Pointer Exception");
                e.printStackTrace();
            }
        }
    }

    protected ArrayList<RECOBeaconRegion> generateBeaconRegion(String uuid) {
        ArrayList<RECOBeaconRegion> regions = new ArrayList<>();
        RECOBeaconRegion recoRegion;
        recoRegion = new RECOBeaconRegion(uuid, "Defined Region");
        regions.add(recoRegion);
        return regions;
    }

    protected ArrayList<RECOBeaconRegion> generateBeaconRegion(String uuid, int major) {
        ArrayList<RECOBeaconRegion> regions = new ArrayList<>();
        RECOBeaconRegion recoRegion;
        recoRegion = new RECOBeaconRegion(uuid, major, "Defined Region");
        regions.add(recoRegion);
        return regions;
    }

    protected ArrayList<RECOBeaconRegion> generateBeaconRegion(String uuid, int major, int minor) {
        ArrayList<RECOBeaconRegion> regions = new ArrayList<>();
        RECOBeaconRegion recoRegion;
        recoRegion = new RECOBeaconRegion(uuid, major, minor, "Defined Region");
        regions.add(recoRegion);
        return regions;
    }

    @Override
    public void onServiceConnect() {
        Log.e("RangingActivity", "onServiceConnect()");
        mRecoManager.setDiscontinuousScan(DISCONTINUOUS_SCAN);
        if(startImmediately)
            start(definedRegions);
    }

    @Override
    public void onServiceFail(RECOErrorCode recoErrorCode) {
        Log.e("RECO service error:", recoErrorCode.toString());
    }

    protected void actionOnEnter(RECOBeacon recoBeacon){}

    protected void actionOnExit(RECOBeacon recoBeacon){}

    protected void inOut(int theRssi, RECOBeacon recoBeacon){
        if(theRssi > rssiBorder){
            if(!entered){
                exitCount = 0;
                entered = true;
                exited = false;
                actionOnEnter(recoBeacon);
            }else{
                Log.e("entered already", ")");
            }
        }else{
            if(exitCount < 3){
                exitCount++;
            }else {
                if(!exited){
                    entered = false;
                    exited = true;
                    actionOnExit(recoBeacon);
                }else {
                    Log.e("exited already", ")");
                }
            }
        }
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<RECOBeacon> recoBeacons, RECOBeaconRegion recoBeaconRegion) {
        synchronized (recoBeacons){
            for(RECOBeacon recoBeacon: recoBeacons){
                Log.e("beacon detected, rssi", String.valueOf(recoBeacon.getRssi()));
                inOut(recoBeacon.getRssi(), recoBeacon);
            }
        }
    }

    @Override
    public void rangingBeaconsDidFailForRegion(RECOBeaconRegion recoBeaconRegion, RECOErrorCode recoErrorCode) {
        Log.e("RECO ranging error:", recoErrorCode.toString());
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        try{
            mRecoManager.unbind();
        }catch (RemoteException e){
            Log.e("on destroy error", e.toString());
        }
    }
}
