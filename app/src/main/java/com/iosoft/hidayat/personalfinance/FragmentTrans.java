package com.iosoft.hidayat.personalfinance;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.iosoft.hidayat.personalfinance.sqlite.DBHelper;
import com.iosoft.hidayat.personalfinance.adapter.AdapterTransaction;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hidayat on 19/08/16.
 */
public class FragmentTrans extends Fragment {

    private DBHelper myDb;
    private List<String> mDate;
    private ArrayList<HashMap<String, String>> mTrans;
    private ExpandableListView mExpandableListView;
    private AdapterTransaction mAdapter;
    private List<String> mGroups;
    private HashMap<String, List<String>> mChilds;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup vroot = (ViewGroup) inflater.inflate(R.layout.fragment_trans, container, false);

        //call the database
        myDb = new DBHelper(getActivity());

        int[] sumTrans = myDb.getBalance();
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();

        formatter.applyPattern("#,###,###");
        String formattedString = formatter.format(sumTrans[0]-sumTrans[1]);

        getActivity().setTitle("Rp. "+formattedString);

        TextView txtIn = (TextView) vroot.findViewById(R.id.txtTotalIn);
        TextView txtOut = (TextView) vroot.findViewById(R.id.txtTotalOut);

        txtIn.setText("In : "+formatter.format(sumTrans[0]));
        txtOut.setText("Out : "+formatter.format(sumTrans[1]));

        prepareData();

        TextView txtNoData = (TextView) vroot.findViewById(R.id.txtNoData);

        if(mGroups.size()<1){

            txtNoData.setVisibility(View.VISIBLE);
        }
        else{

            txtNoData.setVisibility(View.GONE);
        }

        mAdapter = new AdapterTransaction(getActivity(), mGroups, mChilds);
        mExpandableListView = (ExpandableListView) vroot.findViewById(R.id.listTrans);

        mExpandableListView.setAdapter(mAdapter);

        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                String iData = mChilds.get(mGroups.get(groupPosition)).get(childPosition);
                Intent intent = new Intent(getActivity(), TransactionNew.class);
                intent.putExtra("iData", iData);
                startActivity(intent);
                return true;
            }
        });

        for(int i=0;i<mGroups.size();i++){

            mExpandableListView.expandGroup(i);
        }

        FloatingActionButton fab = (FloatingActionButton) vroot.findViewById(R.id.fabAddTrans);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), TransactionNew.class);
                startActivity(intent);
                //getActivity().finish();
            }
        });

        return vroot;
    }

    private void prepareData() {

        String iDate;
        mDate = myDb.getTransDate();


        mGroups = new ArrayList<>();
        mChilds = new HashMap<>();

        for(int i=0;i<mDate.size();i++){

            iDate = mDate.get(i);
            mGroups.add(iDate);

            //get detail transaction by date
            String param = " tgl='"+iDate+"' ";
            mTrans = myDb.getTransListByParam(param);

            List<String> transDetail = new ArrayList<>();

            for (int x=0;x<mTrans.size();x++){

                String strDetail = mTrans.get(x).get("id_trans")+"##";
                    strDetail+=mTrans.get(x).get("tgl")+"##";
                    strDetail+=mTrans.get(x).get("tipe")+"##";
                    strDetail+=mTrans.get(x).get("id_kat")+"##";
                    strDetail+=mTrans.get(x).get("desk")+"##";
                    strDetail+=mTrans.get(x).get("nominal")+"##";
                    strDetail+=mTrans.get(x).get("kat")+"##";
                    strDetail+=mTrans.get(x).get("ic_kat");

                transDetail.add(strDetail);
            }

            mChilds.put(iDate, transDetail);
        }
    }
}
