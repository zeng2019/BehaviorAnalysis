package com.example.wanzhuang.newble;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.sensoro.beacon.kit.Beacon;

import java.util.concurrent.CopyOnWriteArrayList;

public class BeaconsFragment extends Fragment{

    GridView beaconsGridView;
    BeaconAdapter adapter;
    CopyOnWriteArrayList<Beacon> beacons;

    MainActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        return inflater.inflate(R.layout.fragment_beacons,container,false);
    }
    private void initCtrl(){
        activity=(MainActivity) getActivity();
        beaconsGridView=(GridView)activity.findViewById(R.id.fragment_beacons_gv_beacons);
        adapter =new BeaconAdapter(activity);
        beaconsGridView.setAdapter(adapter);

    }
    @Override
    public void onResume(){
        super.onResume();
    }
    @Override
    public void onActivityCreated(Bundle savedIntanceState){
        initCtrl();
        super.onActivityCreated(savedIntanceState);
    }

    public class BeaconAdapter extends BaseAdapter {

        private CopyOnWriteArrayList<Beacon> beacons;
        private LayoutInflater layoutInflater;

        BeaconAdapter(MainActivity activity) {
            super();
            beacons = new CopyOnWriteArrayList<>();
            layoutInflater = LayoutInflater.from(activity);
        }


        @Override
        public int getCount() {
            //activity=(MainActivity) getActivity();
            beacons = activity.beacons;
            if (beacons == null) {
                return 0;
            }
            return beacons.size();
        }

        @Override
        public Object getItem(int position) {

            if (beacons == null) {
                return null;
            }
            return beacons.get(position);
        }

        @Override
        public long getItemId(int position) {

            return position;

        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {

            ViewHolder viewHolder = null;
            if (view == null) {
                view = layoutInflater.inflate(R.layout.listitems, null);
                viewHolder = new ViewHolder();
                viewHolder.snTextView = view.findViewById(R.id.beacon_item_tv_sn);
                viewHolder.idTextView = view.findViewById(R.id.beacon_item_tv_id);
                //  viewHolder.majorTextView = view.findViewById(R.id.ibeacon_item_tv_major);
                //  viewHolder.minorTextView =view.findViewById(R.id.ibeacon_item_tv_minor);
                // viewHolder.rssiTextView=view.findViewById(R.id.ibeacon_item_tv_rssi);
                //  viewHolder.uuidTextView=view.findViewById(R.id.beacon_item_tv_prouuid);
                //   viewHolder.distanceTextview=view.findViewById(R.id.beacon_item_tv_distance);
                //   viewHolder.tempTextView=view.findViewById(R.id.ibeacon_item_tv_temperature);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            Beacon beacon = beacons.get(position);
            if (beacon == null) {
                return null;
            }
            // set id
            String id= String.format("ID；%04x-%04x",beacon.getMajor(),beacon.getMinor());
            viewHolder.idTextView.setText(id);
            //set sn
            String sn = String.format("SN:%s", beacon.getSerialNumber());
            viewHolder.snTextView.setText(sn);
            return view;
        }

        class ViewHolder{
            TextView snTextView;
            TextView idTextView;
            //TextView majorTextView;
            // TextView minorTextView;
            // TextView uuidTextView;
            // TextView rssiTextView;
            //  TextView tempTextView;
            //   TextView distanceTextview;
        }


    }

}
