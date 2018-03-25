package com.example.sheryl.deerdetection;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.VideoView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.util.Arrays;

/**
 * Created by sheryl on 3/24/18.
 */

public class start extends Activity implements CameraBridgeViewBase.CvCameraViewListener2{

    private static final int REQUEST_CODE = 1;
    private Bitmap bitmap;
    private ImageView imageView;
    static VideoView myView;
    public Mat result;
    private static final String MODEL_FILE = "file:///android_asset/my_model.pb";
    public static final String INPUT_NODE = "conv2d_19";
    public static final String OUTPUT_NODE = "dense_8";
    public static final long[] INPUT_SIZE = {3, 32, 32};
    public static int[] ans = new int[2];
    public Bitmap bmp;
    private TensorFlowInferenceInterface inferenceInterface;
    static{

        System.loadLibrary("tensorflow_inference");



    }
    static int current = 0;
    static ConstraintLayout llayout;
    @Override
    public void onCreate(Bundle savedInstanceState){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 0);
        }
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        //dispatchTakeVideoIntent();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.HelloOpenCvView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        llayout = (ConstraintLayout) findViewById(R.id.llayout);
    }
    private void dispatchTakeVideoIntent(){
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {

            startActivityForResult(takeVideoIntent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
//        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
//        if (ans[1] == 1){
//            llayout.setBackgroundColor(current);
//            current = current++ % 2;
//            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 2);
//
//        }
//        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK){
//            Uri videoUri = intent.getData();
//            myView.setVideoURI(videoUri);
//
//            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//            mmr.setDataSource(this, videoUri);
//            result = mmr.getFrameAtTime();
//            result = Bitmap.createScaledBitmap(result, 32, 32, false);
//            Log.d("ans: ", "yay it's a result");
//            inferenceInterface = new TensorFlowInferenceInterface(getAssets(), MODEL_FILE);
//            result = result.copy(Bitmap.Config.ARGB_8888, true);
//            int[] intArray = new int[32*32];
//            result.getPixels(intArray,0, 32, 0, 0, 32, 32);
//            long[] changeArray = new long[32*32*3];
//            for (int i = 0; i < 32; i++) {
//                for (int j = 0; j < 32; j++) {
//                    String hex = Integer.toHexString(intArray[32*i+j]);
//                    changeArray[3*(32*i+j)] = Long.parseLong(hex.substring(2, 4), 16);
//                    changeArray[3*(32*i+j)+1] = Long.parseLong(hex.substring(4, 6), 16);
//                    changeArray[3*(32*i+j)+2] = Long.parseLong(hex.substring(6, 8), 16);
//                }
//            }
//            inferenceInterface.feed(INPUT_NODE, changeArray, INPUT_SIZE);
//            inferenceInterface.run(new String[]{OUTPUT_NODE});
//            inferenceInterface.fetch(OUTPUT_NODE, ans);
//            Log.d("ans: ", Arrays.toString(ans));
        }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat rgba = inputFrame.rgba();
        Log.d("ans: ", "yay it's a result");
        inferenceInterface = new TensorFlowInferenceInterface(getAssets(), MODEL_FILE);
        rgba.reshape(3);
        Log.d("ans: ",rgba.size().toString());
        Utils.matToBitmap(rgba, bmp);
        bmp = Bitmap.createScaledBitmap(bmp, 32, 32, false);
        int[] intArray = new int[32*32];
        bmp.getPixels(intArray,0, 32, 0, 0, 32, 32);
        long[] changeArray = new long[32*32*3];
        for (int i = 0; i < 32; i++) {
            for (int j = 0; j < 32; j++) {
                String hex = Integer.toHexString(intArray[32*i+j]);
                changeArray[3*(32*i+j)] = Long.parseLong(hex.substring(2, 4), 16);
                changeArray[3*(32*i+j)+1] = Long.parseLong(hex.substring(4, 6), 16);
                changeArray[3*(32*i+j)+2] = Long.parseLong(hex.substring(6, 8), 16);
            }
        }
        inferenceInterface.feed(INPUT_NODE, changeArray, INPUT_SIZE);
        inferenceInterface.run(new String[]{OUTPUT_NODE});
        inferenceInterface.fetch(OUTPUT_NODE, ans);
        Log.d("ans: ", Arrays.toString(ans));

        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        if (ans[1] == 1){
            llayout.setBackgroundColor(current);
            current = current++ % 2;
            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 2);

        }
        return inputFrame.rgba();
    }
    private CameraBridgeViewBase mOpenCvCameraView;
    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();

    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
    }
}
