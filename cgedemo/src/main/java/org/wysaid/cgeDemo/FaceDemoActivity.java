package org.wysaid.cgeDemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import org.wysaid.common.Common;
import org.wysaid.demoViews.FaceDemoView;
import org.wysaid.myUtils.ImageUtil;
import org.wysaid.nativePort.CGEFaceFunctions;

public class FaceDemoActivity extends AppCompatActivity {

    public static final int CHOOSE_PHOTO = 1;

    private FaceDemoView mFaceDemoView;
    private ImageView mResultView;
    private CGEFaceFunctions.FaceFeature mFirstFaceFeature;
    private Bitmap mFirstFaceImage;
    private CGEFaceFunctions.FaceFeature mSecondFaceFeature;
    private Bitmap mSecondFaceImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_demo);

        mFaceDemoView = (FaceDemoView)findViewById(R.id.faceDemoView);
        mResultView = (ImageView)findViewById(R.id.resultView);
        mResultView.setVisibility(View.GONE);

        Bitmap eye = BitmapFactory.decodeResource(getResources(), R.drawable.adjust_face_eye);
        Bitmap mouth = BitmapFactory.decodeResource(getResources(), R.drawable.adjust_face_mouth);
        Bitmap chin = BitmapFactory.decodeResource(getResources(), R.drawable.adjust_face_chin);

        mFaceDemoView.loadResources(eye, mouth, chin);

        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mFaceDemoView.setFaceImageZoom(progress / 20.0f + 0.5f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void setAsFirstPhoto(View view) {
        Log.i(Common.LOG_TAG, "setAsFirstPhoto...");
        mFirstFaceFeature = mFaceDemoView.getFeature();
        mFirstFaceImage = mFaceDemoView.getFaceimage();
        Toast.makeText(this, "当前画面设置为第一张图", Toast.LENGTH_SHORT).show();
    }

    public void setAsSecondPhoto(View view) {
        Log.i(Common.LOG_TAG, "setAsSecondPhoto...");

        mSecondFaceFeature = mFaceDemoView.getFeature();
        mSecondFaceImage = mFaceDemoView.getFaceimage();
        Toast.makeText(this, "当前画面设置为第二张图", Toast.LENGTH_SHORT).show();
    }

    public void showResult(View view) {
        Log.i(Common.LOG_TAG, "showResult...");
        Bitmap bmp = CGEFaceFunctions.blendFaceWidthFeatures(mFirstFaceImage, mFirstFaceFeature, mSecondFaceImage, mSecondFaceFeature, CGEFaceFunctions.AutoLumAdjustMode.LumAdjust_OnlyBrightness, null);
        mResultView.setImageBitmap(bmp);
        mResultView.setVisibility(View.VISIBLE);
        mFaceDemoView.setVisibility(View.GONE);
    }

    public void choosePhoto(View view) {
        mResultView.setVisibility(View.GONE);
        mFaceDemoView.setVisibility(View.VISIBLE);
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, CHOOSE_PHOTO);
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

        if(resultCode != RESULT_OK)
            return;

        switch (requestCode)
        {
            case CHOOSE_PHOTO:
            {
                mResultView.setImageURI(data.getData());
                Bitmap result = ((BitmapDrawable)mResultView.getDrawable()).getBitmap();
                if(result.getWidth() > 2000 || result.getHeight() > 2000) {
                    float scaling = (float)Math.min(2000.0 / result.getWidth(), 2000.0 / result.getHeight());
                    result = Bitmap.createScaledBitmap(result, (int)(result.getWidth() * scaling), (int)(result.getHeight() * scaling), false);
                }
                mFaceDemoView.setFaceImage(result);
            }
                break;
            default:break;
        }
    }

    public void resetControllers(View view) {
        mFaceDemoView.resetControllers();
        mFaceDemoView.postInvalidate();
    }

    public void saveResult(View view) {
        Bitmap result = ((BitmapDrawable)mResultView.getDrawable()).getBitmap();
        ImageUtil.saveBitmap(result);
    }

    public void useResult(View view) {
        Bitmap result = ((BitmapDrawable)mResultView.getDrawable()).getBitmap();
        mFaceDemoView.setFaceImage(result);
    }
}
