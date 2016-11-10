package com.permis.test;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String TAG = getClass().getSimpleName();
    private PermissionUtils.PermissionGrant mGrant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //添加要使用的功能
        mGrant = new PermissionUtils.PermissionGrant() {
            @Override
            public void onPermissionGranted(int requestCode) {
                Toast.makeText(MainActivity.this, "相机功能已开启", Toast.LENGTH_SHORT).show();
            }
        };

    }


    public void startCamera(View view){
        Log.d(TAG,"开启相机功能");
        //使用功能是先请求权限。
        //不要忘记在Manifest文件中也要配置权限
        PermissionUtils.requestPermission(this, PermissionUtils.CODE_CAMERA,mGrant);
    }


    /**
     * 在activity中回调：
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Toast.makeText(this, "回调的是Activity:", Toast.LENGTH_SHORT).show();
        
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults,mGrant);
    }
}
