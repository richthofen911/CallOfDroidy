package project.richthofen911.callofdroidy;

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

    private String TOBEFOUND_UUID = "";
    private int TOBEFOUND_MAJOR = 0;
    private int TOBEFOUND_MINOR = 0;

    private final boolean DISCONTINUOUS_SCAN = false;

    private boolean haveFoundBeacon = false;

    protected RECOBeaconManager mRecoManager = RECOBeaconManager.getInstance(this, false, false);
    protected ArrayList<RECOBeaconRegion> definedRegions;

    protected void assignRegionArgs(String uuid){
        definedRegions = generateBeaconRegion(uuid);
    }

    protected void assignRegionArgs(String uuid, int major){
        definedRegions = generateBeaconRegion(uuid, major);
    }

    protected void assignRegionArgs(String uuid, int major, int minor){
        definedRegions = generateBeaconRegion(uuid, major, minor);
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
                Log.e("setup a region: ", region.getProximityUuid());
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
        for(RECOBeaconRegion region : regions) {
            try {
                mRecoManager.stopRangingBeaconsInRegion(region);
                entered = false;
                Log.e("setup a region: ", region.getProximityUuid());
            } catch (RemoteException e) {
                Log.i("RECORangingActivity", "Remote Exception");
                e.printStackTrace();
            } catch (NullPointerException e) {
                Log.i("RECORangingActivity", "Null Pointer Exception");
                e.printStackTrace();
            }
        }
    }

    private ArrayList<RECOBeaconRegion> generateBeaconRegion(String uuid) {
        ArrayList<RECOBeaconRegion> regions = new ArrayList<>();
        RECOBeaconRegion recoRegion;
        recoRegion = new RECOBeaconRegion(uuid, "Defined Region");
        regions.add(recoRegion);
        return regions;
    }

    private ArrayList<RECOBeaconRegion> generateBeaconRegion(String uuid, int major) {
        ArrayList<RECOBeaconRegion> regions = new ArrayList<>();
        RECOBeaconRegion recoRegion;
        recoRegion = new RECOBeaconRegion(uuid, major, "Defined Region");
        regions.add(recoRegion);
        return regions;
    }

    private ArrayList<RECOBeaconRegion> generateBeaconRegion(String uuid, int major, int minor) {
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
    }

    @Override
    public void onServiceFail(RECOErrorCode recoErrorCode) {
        Log.e("RECO service error:", recoErrorCode.toString());
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<RECOBeacon> recoBeacons, RECOBeaconRegion recoBeaconRegion) {
        if(!recoBeacons.isEmpty()){
            synchronized (recoBeacons){
                for(RECOBeacon recoBeacon: recoBeacons){
                    if(recoBeacon.getProximity() == RECOProximity.RECOProximityImmediate || recoBeacon.getProximity() == RECOProximity.RECOProximityNear){
                        if(!haveFoundBeacon){
                            Toast.makeText(getApplicationContext(), "found the beacon", Toast.LENGTH_SHORT).show();
                            haveFoundBeacon = true;
                        }else{
                            //haveFoundBeacon flag is true, haven't exit the region
                            Log.e("have found already", "");
                        }
                    }else{
                        //range is farther than RECOProximityNear
                        Log.e("out of range", "");
                        haveFoundBeacon = false;
                    }
                }
            }
        }else{
            //the beacon isn't detected at all
            Log.e("out of range", "");
            haveFoundBeacon = false;
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
