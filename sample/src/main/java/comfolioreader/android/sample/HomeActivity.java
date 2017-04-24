/*
* Copyright (C) 2016 Pedro Paulo de Amorim
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package comfolioreader.android.sample;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.folioreader.activity.FolioActivity;

import java.io.File;

public class HomeActivity extends AppCompatActivity {
    private static final int GALLERY_REQUEST = 102;
    private static final int OPEN_FILE_SYSTEM = 200;
    private static final String PATH = "sdcard/Download/MyEpub3.1.epub";
    private static final String PATH_CHINESE = "/storage/emulated/0/Download/云图.epub";

    public static final String[] WRITE_EXTERNAL_STORAGE_PERMS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        findViewById(R.id.btn_assest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(HomeActivity.this, WRITE_EXTERNAL_STORAGE_PERMS, GALLERY_REQUEST);
                } else {
                    //openEpub(FolioActivity.EpubSourceType.ASSESTS,"PhysicsSyllabus.epub",0);
                    openEpub(FolioActivity.EpubSourceType.ASSESTS,"The Silver Chair.epub",0);
                }
            }
        });

        findViewById(R.id.btn_raw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(HomeActivity.this, WRITE_EXTERNAL_STORAGE_PERMS, GALLERY_REQUEST);
                } else {
                    openEpub(FolioActivity.EpubSourceType.RAW,null,R.raw.adventures);
                }
            }
        });

        findViewById(R.id.btn_sdcard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(HomeActivity.this, WRITE_EXTERNAL_STORAGE_PERMS, GALLERY_REQUEST);
                } else {
//                    openEpub(FolioActivity.EpubSourceType.RAW,null,R.raw.adventures);
                    openEpub(FolioActivity.EpubSourceType.SD_CARD,PATH,0);
                }
            }
        });

        findViewById(R.id.btn_chinese).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(HomeActivity.this, WRITE_EXTERNAL_STORAGE_PERMS, GALLERY_REQUEST);
                } else {
                    openEpub(FolioActivity.EpubSourceType.SD_CARD,PATH_CHINESE,0);
                }
            }
        });

        findViewById(R.id.btn_file_system).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(HomeActivity.this, WRITE_EXTERNAL_STORAGE_PERMS, GALLERY_REQUEST);
                } else {
//                    openEpub(FolioActivity.EpubSourceType.SD_CARD,PATH_TXT,0);
                    openSystemFile();
                }
            }
        });
    }

    private void openSystemFile() {
        //系统调用Action属性
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        //设置文件类型
        intent.setType("application/epub+zip");
        // 添加Category属性
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try{
            startActivityForResult(intent, OPEN_FILE_SYSTEM);
        }catch(Exception e){
            Toast.makeText(this, "没有正确打开文件管理器", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != OPEN_FILE_SYSTEM) {
            Toast.makeText(this, "获取文件失败", Toast.LENGTH_SHORT).show();
            return;
        } else if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            Log.e("xns", "uri:" + uri + " Authority:" + uri.getAuthority() + " Path:"
                    + uri.getPath() + " Scheme:" + uri.getScheme());
            readMeteData(HomeActivity.this, uri);
        }
    }

    /**
     * 给定Uri解析元数据
     *
     * @param uri
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void readMeteData(Context context, Uri uri) {
        if (uri == null || context == null) {
            return;
        }
        openEpub(FolioActivity.EpubSourceType.SD_CARD, uri.getPath(), 0);
    }

    private void openEpub(FolioActivity.EpubSourceType sourceType, String path, int rawID) {
        Intent intent = new Intent(HomeActivity.this, FolioActivity.class);
        if(rawID!=0) {
            intent.putExtra(FolioActivity.INTENT_EPUB_SOURCE_PATH, rawID);
        } else {
            intent.putExtra(FolioActivity.INTENT_EPUB_SOURCE_PATH, path);
        }
        intent.putExtra(FolioActivity.INTENT_EPUB_SOURCE_TYPE, sourceType);
        startActivity(intent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //openEpub();
                } else {
                    Toast.makeText(this, "Cannot open epub it needs storage access !", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /*private static class TestFragmentAdapter extends FragmentPagerAdapter {

        protected static final String[] CONTENT = new String[] { "This", "Is Is", "A A A", "Test", };

        public TestFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return TestFragment.newInstance(CONTENT[position]);
        }

        @Override
        public int getCount() {
            return CONTENT.length;
        }

    }*/
}