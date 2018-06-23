package com.example.wanzhuang.newble;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.sensoro.beacon.kit.Beacon;
import java.util.concurrent.CopyOnWriteArrayList;

public class BeaconsAdapter extends BaseAdapter {

    private CopyOnWriteArrayList<Beacon> beacons;
    private LayoutInflater layoutInflater;

    BeaconsAdapter(LayoutInflater inflater) {
        super();
        beacons = new CopyOnWriteArrayList<>();
        layoutInflater = inflater;
    }


    @Override
    public int getCount() {
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

