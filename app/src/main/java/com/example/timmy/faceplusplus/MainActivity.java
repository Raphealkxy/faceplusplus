package com.example.timmy.faceplusplus;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timmy.faceplusplus.util.FaceUtil;
import com.megvii.cloud.http.CommonOperate;
import com.megvii.cloud.http.FaceSetOperate;
import com.megvii.cloud.http.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener {


    private static final int PICK_CODE = 0x110;
    private ImageView imageView;//勇于显示选择或者拍摄的照片
    private Button btn_getpicture;//获取本地图片
    private Button btn_detect;//开始探测
    public TextView textView;//用于提示用户按探测键
    private final int REQUEST_CAMERA_IMAGE = 2;//拍照请求码
    private Bitmap mImage = null;//处理后的位图,也是处理图片的开始文件
    private final int REQUEST_PICTURE_CHOOSE = 1;//照片选择请求码
    private File mPictureFile;
    private Toast mToast;
    private byte[] mImageData = null;
    private Button btn_verfiy;
    //api涉及的参数
    //private TextView mTextView;
    private String attributes="emotion,gender,age,smiling,glass,headpose,facequality,blur";
    String key = "-09MfhydPuTqnptP0osrI7eOauP90aTu";//api_key
    String secret = "gPpSvUkJNEb9MNFGeeH0creMcfl7r-M8";//api_secret
    String imageUrl = "http://pic1.hebei.com.cn/003/005/869/00300586905_449eedbb.jpg";//来自网络上的一张图片
    StringBuffer sb = new StringBuffer();//字符串缓冲区

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setclick();
    }

    private void setclick() {
      btn_detect.setOnClickListener(this);
        btn_getpicture.setOnClickListener(this);
        btn_verfiy.setOnClickListener(this);

    }

    private void initView() {
        imageView= (ImageView) findViewById(R.id.image_pic);
        btn_getpicture= (Button) findViewById(R.id.btn_getImage);
        btn_detect= (Button) findViewById(R.id.btn_detect);
        btn_verfiy= (Button) findViewById(R.id.pipei);
        //  textView= (TextView) findViewById(R.id.Tip);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_detect:
                Log.e("TAG","detect");
                //detect函数
                detect();

                break;
            case R.id.btn_getImage:
                getImage();
                break;
            case R.id.pipei:
                Log.e("TAG","123456");
                face_pipei();
                break;
        }

    }

    private void face_pipei() {
        if(TextUtils.isEmpty(key) || TextUtils.isEmpty(secret)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);//创建对话框
            builder.setMessage("please enter key and secret");//添加对话框信息
            builder.setTitle("");//将对话框标题设为空
            builder.show();//让对话框显示
        }else{

            new Thread(new Runnable() {
                @Override
                public void run() {
                    CommonOperate commonOperate = new CommonOperate(key, secret, false);//创建连接
                    FaceSetOperate FaceSet = new FaceSetOperate(key, secret, false);//创建用于存储脸库的集合
                    ArrayList<String> faces = new ArrayList<>();

                    String faceToken1 = null;//提取这个人的faceToken;
                    String faceToken2=null;
                    String faceToken3=null;
                    String faceToken4=null;
                    String faceToken5=null;
                    String faceToken6=null;


                    try {
                        Response response1 = commonOperate.detectByte(getBitmap(R.mipmap.c04), 0, attributes);//以本地而形式探测
                        faceToken1 = getFaceToken(response1);
                     //   Response response2=commonOperate.detectByte(getBitmap(R.mipmap.c05), 0, attributes);
                      //  faceToken2=getFaceToken(response2);
                      //  faces.add(faceToken2);//将这个人脸假如列表用于后续搜索比对
           //             Response response3=commonOperate.detectByte(getBitmap(R.mipmap.c06), 0, attributes);
//                        faceToken3=getFaceToken(response3);
              //          faces.add(faceToken3);//将这个人脸假如列表用于后续搜索比对
                        Response response4=commonOperate.detectByte(getBitmap(R.mipmap.c032), 0, attributes);
                        faceToken4=getFaceToken(response4);
                        faces.add(faceToken4);//将这个人脸假如列表用于后续搜索比对


                        Response response5=commonOperate.detectByte(getBitmap(R.mipmap.c033), 0, attributes);
                        faceToken5=getFaceToken(response5);
                        faces.add(faceToken5);//将这个人脸假如列表用于后续搜索比对

                        Response response6=commonOperate.detectByte(getBitmap(R.mipmap.c11), 0, attributes);
                        faceToken6=getFaceToken(response6);
                       //faces.add(faceToken6);//将这个人脸假如列表用于后续搜索比对

                        //创建人脸库，并往里加人脸
                        //create faceSet and add face
                        String faceTokens = creatFaceTokens(faces);
                        Response faceset = FaceSet.createFaceSet(null,"test",null,faceTokens,null, 1);//test为脸集在系统中的标识
                        String faceSetResult = new String(faceset.getContent());//获取脸集创建的信息
                        Log.e("faceSetResult",faceSetResult);//打印信息
                   //     Response res = commonOperate.searchByOuterId(null, imageUrl, null, "test", 1);
                      Response res=commonOperate.searchByFaceSetToken(faceToken6,null,null,"c0f50247c7d194f0998d555de208f0e6",1);
                        String result = new String(res.getContent());//获取比对结果
                        Log.e("result", result);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).start();




        }

    }

    private String creatFaceTokens(ArrayList<String> faceTokens){//创建人脸库字符组；
        if(faceTokens == null || faceTokens.size() == 0){//判断是否为空，如果为空，则返回主程序
            return "";
        }
        StringBuffer face = new StringBuffer();//建立缓冲区
        for (int i = 0; i < faceTokens.size(); i++){
            if(i == 0){//建立以逗号为间隔的字符组。
                face.append(faceTokens.get(i));
            }else{
                face.append(",");
                face.append(faceTokens.get(i));
            }
        }
        return face.toString();//将缓冲区转成字符组返回给主程序
    }


    private void detect() {

        if(mImage==null) {
            showTip("请选择图片后在操作");
            return;
        }
        if(TextUtils.isEmpty(key) || TextUtils.isEmpty(secret)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);//创建对话框
            builder.setMessage("please enter key and secret");//添加对话框信息
            builder.setTitle("");//将对话框标题设为空
            builder.show();//让对话框显示
        }else{
            //为了避免因网络问题而靠成的阻塞，建议将 API 调用放进异步线程里执行。
            final CommonOperate commonOperate = new CommonOperate(key, secret, false);//创建连接
             new Thread(new Runnable() {
                 @Override
                 public void run() {
                     try {
                         Response response1 = commonOperate.detectByte(Bitmap2Bytes(mImage), 0, attributes);//以本地而形式探测
                         //Response response2 = commonOperate.detectUrl(, 0, null);
                         String faceToken1 = getFaceToken(response1);//提取这个人的faceToken;
                         //给图片中的所有人进行加框
                         drawRectangle(response1);

                         //画出年龄与性别






                         // Log.e("TAG",s);
                     } catch (Exception e) {
                         e.printStackTrace();
                     }

                 }
             }).start();


        }


    }

    private void draw_age_gender(Response response) throws JSONException {
        JSONArray faces=getfaces(response);//提取人的face；
        Bitmap mutableBitmap = mImage.copy(Bitmap.Config.ARGB_8888, true);
        for(int i=0;i<faces.length();i++)
        {
            JSONObject face=faces.getJSONObject(i);
            String gender=face.getJSONObject("gender").toString();
            int age=face.getInt("age");
            Bitmap ageBitmap= buildagebitmap(age,"Male".equals(gender));
            int agewidth=ageBitmap.getWidth();
            int ageheight=ageBitmap.getHeight();
            if(mImage.getWidth()<imageView.getWidth()&&mImage.getHeight()<imageView.getHeight())
            {
                float ratio=Math.max(mImage.getWidth()*1.0f/imageView.getWidth(),mImage.getHeight());
                ageBitmap=Bitmap.createScaledBitmap(ageBitmap,(int)(agewidth*ratio),(int)(ageheight*ratio),false);



            }





      }

    }

    private Bitmap buildagebitmap(int age, boolean isMale) {
        final TextView tv= (TextView) findViewById(R.id.id_age_and_gender);
        final String s= String.valueOf(age);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText(s);
            }
        });
        if(isMale)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.male),null,null,null);

                }
            });
        }else
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.female),null,null,null);
                }
            });

        }
        tv.setDrawingCacheEnabled(true);
        Bitmap bitmap=Bitmap.createBitmap(tv.getDrawingCache());
        tv.destroyDrawingCache();
        return bitmap;
    }

    private void drawRectangle(Response response) throws JSONException {
        JSONArray faces=getfaces(response);//提取人的face；

        Bitmap mutableBitmap = mImage.copy(Bitmap.Config.ARGB_8888, true);

        for(int i=0;i<faces.length();i++) {
            JSONObject face=faces.getJSONObject(i);
            JSONObject face_rectangle=face.getJSONObject("face_rectangle");
            int width = face_rectangle.getInt("width");
            int height = face_rectangle.getInt("height");
            int top = face_rectangle.getInt("top");
            int left = face_rectangle.getInt("left");
            mutableBitmap = mutableBitmap.copy(Bitmap.Config.ARGB_8888, true);

            Canvas canvas = new Canvas(mutableBitmap);
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);//不填充
            paint.setStrokeWidth(2);  //线的宽度
            canvas.drawRect(left, top, width + left, top + height, paint);

            String gender=face.getJSONObject("attributes").getJSONObject("gender").getString("value");
            int age=face.getJSONObject("attributes").getJSONObject("age").getInt("value");

            Bitmap ageBitmap= buildagebitmap(age,"Male".equals(gender));
            int agewidth=ageBitmap.getWidth();
            int ageheight=ageBitmap.getHeight();
          //  String S=agewidth+","+ageheight;

            if(mImage.getWidth()<imageView.getWidth()&&mImage.getHeight()<imageView.getHeight())
            {
                double ratio=Math.max(mImage.getWidth()*1.0f/imageView.getWidth(),mImage.getHeight()*1.0f/imageView.getHeight());
                ratio=ratio-0.12;
                String S=ratio+"";
               Log.e("TAG",S);
             ageBitmap=Bitmap.createScaledBitmap(ageBitmap,(int)(agewidth*ratio),(int)(ageheight*ratio),false);




            }
            canvas.drawBitmap(ageBitmap,left+width/2-ageBitmap.getWidth()/2,top-ageBitmap.getHeight(),null);
        }
        final Bitmap finalMutableBitmap = mutableBitmap;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(finalMutableBitmap);//更新UI1线程
            }
        });
    }

    private String getFaceToken(Response response) throws JSONException {
        if(response.getStatus() != 200){//连接不成功
            return new String(response.getContent());
        }
        String res = new String(response.getContent());//连接成功
        Log.e("response", res);//将返回的数据打印出来
        JSONObject json = new JSONObject(res);//将返回的数据转化为json数据格式
        String faceToken = json.optJSONArray("faces").optJSONObject(0).optString("face_token");//将face_token提取出来
        return faceToken;//返回字符串
    }

    private JSONArray getfaces(Response response)throws JSONException {
        if(response.getStatus() != 200){//连接不成功
            return null;
        }
        String res = new String(response.getContent());//连接成功
        Log.e("response", res);//将返回的数据打印出来
        JSONObject json = new JSONObject(res);//将返回的数据转化为json数据格式
        JSONArray faces = json.optJSONArray("faces");//将face_token提取出来
        return faces;//返回字符串
    }
    private void getImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, REQUEST_PICTURE_CHOOSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            return;
        }
        String fileSrc = null;
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICTURE_CHOOSE) {
            if ("file".equals(data.getData().getScheme())) {
                // 有些低版本机型返回的Uri模式为file
                fileSrc = data.getData().getPath();
            } else {
                // Uri模型为content
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(data.getData(), proj,
                        null, null, null);
                cursor.moveToFirst();
                int idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                fileSrc = cursor.getString(idx);
                cursor.close();
            }
            // 跳转到图片裁剪页面
            FaceUtil.cropPicture(this,Uri.fromFile(new File(fileSrc)));
        } else if (requestCode == REQUEST_CAMERA_IMAGE) {
            if (null == mPictureFile) {
                showTip("拍照失败，请重试");
                return;
            }

            fileSrc = mPictureFile.getAbsolutePath();
            updateGallery(fileSrc);
            // 跳转到图片裁剪页面
            FaceUtil.cropPicture(this,Uri.fromFile(new File(fileSrc)));
        } else if (requestCode == FaceUtil.REQUEST_CROP_IMAGE) {
            // 获取返回数据
            Bitmap bmp = data.getParcelableExtra("data");
            // 若返回数据不为null，保存至本地，防止裁剪时未能正常保存
            if(null != bmp){
                FaceUtil.saveBitmapToFile(MainActivity.this, bmp);
            }
            // 获取图片保存路径
            fileSrc = FaceUtil.getImagePath(MainActivity.this);
            // 获取图片的宽和高
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            mImage = BitmapFactory.decodeFile(fileSrc, options);

            // 压缩图片
            options.inSampleSize = Math.max(1, (int) Math.ceil(Math.max(
                    (double) options.outWidth / 1024f,
                    (double) options.outHeight / 1024f)));
            options.inJustDecodeBounds = false;
            mImage = BitmapFactory.decodeFile(fileSrc, options);


            // 若mImageBitmap为空则图片信息不能正常获取
            if(null == mImage) {
                showTip("图片信息无法正常获取！");
                return;
            }

            // 部分手机会对图片做旋转，这里检测旋转角度
            int degree = FaceUtil.readPictureDegree(fileSrc);
            if (degree != 0) {
                // 把图片旋转为正的方向
                mImage = FaceUtil.rotateImage(degree, mImage);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            //可根据流量及网络状况对图片进行压缩
            mImage.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            mImageData = baos.toByteArray();

            imageView.setImageBitmap(mImage);
        }






    }

    private void updateGallery(String filename) {
        MediaScannerConnection.scanFile(this, new String[] {filename}, null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    @Override
                    public void onScanCompleted(String path, Uri uri) {

                    }
                });
    }


    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }
    public  byte[] Bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    private byte[] getBitmap(int res){
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), res);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
}
