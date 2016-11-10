package com.permis.test;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by tangjun on 2016/11/9.
 */

public class SuperFragment extends Fragment implements View.OnClickListener{

    private Button mButton;
    private String TAG = getClass().getSimpleName();
    private PermissionUtils.PermissionGrant mGrant;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View inflate = inflater.inflate(R.layout.fragment_super, container, false);

        mButton = (Button) inflate.findViewById(R.id.button);

        //在fragment中开启权限
        mButton.setOnClickListener(this);

        mGrant = new PermissionUtils.PermissionGrant() {
            @Override
            public void onPermissionGranted(int requestCode) {
                Toast.makeText(getActivity(), "联系人功能已打开", Toast.LENGTH_SHORT).show();
            }
        };

        return inflate;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Toast.makeText(getActivity(), "回调的是SuperFragment:", Toast.LENGTH_SHORT).show();
        PermissionUtils.requestPermissionsResult(this,requestCode,permissions,grantResults,mGrant);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button){
            Log.d(TAG,"开启联系人");
            PermissionUtils.requestPermission(this,PermissionUtils.CODE_GET_ACCOUNTS,mGrant);
        }
    }
}
