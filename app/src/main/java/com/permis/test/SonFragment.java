package com.permis.test;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by tangjun on 2016/11/9.
 */

public class SonFragment extends Fragment implements View.OnClickListener{

    private Button mButton;
    private PermissionUtils.PermissionGrant mGrant;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View inflate = inflater.inflate(R.layout.fragment_son, container, false);

        mButton = (Button) inflate.findViewById(R.id.son_button);
        mButton.setOnClickListener(this);

        mGrant = new PermissionUtils.PermissionGrant() {
            @Override
            public void onPermissionGranted(int requestCode) {
                Toast.makeText(getActivity(), "打电话", Toast.LENGTH_SHORT).show();
            }
        };

        return inflate;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.son_button){
            PermissionUtils.requestPermission(this,PermissionUtils.CODE_CALL_PHONE,mGrant);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Toast.makeText(getActivity(), "调用的是sonFragment", Toast.LENGTH_SHORT).show();
        PermissionUtils.requestPermissionsResult(this,requestCode,permissions,grantResults,mGrant);
    }
}
