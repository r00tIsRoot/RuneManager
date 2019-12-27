package com.isroot.runemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    TessBaseAPI tess;
    String dataPath ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //데이터 경로
        dataPath = getFilesDir()+"/tesseract/";

        //한글 데이터 체크
        checkFile(new File(dataPath+"tessdata/"), "kor");

        //문자 인식을 수행할 tess 객체 생성
        String lang = "kor";
        tess = new TessBaseAPI();
        tess.init(dataPath, lang);

        //문자 인식 진행
        Button startBtn = findViewById(R.id.startBtn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processImage(BitmapFactory.decodeResource(getResources(), R.drawable.test9));
            }
        });
    }

    //문자 인식 및 결과 출력
    public void processImage(Bitmap bitmap){
        Toast.makeText(getApplicationContext(), "이미지가 복잡할 경우 해석 시 많은 시간이 소요될 수도 있습니다.\n안드로이드글씨체를 맑은고딕으로 해주셔야 높은 정확도를 받을 수 있습니다.", Toast.LENGTH_LONG).show();
        String ocrResult = null;
        tess.setImage(bitmap);
        ocrResult = tess.getUTF8Text();
        TextView ocrTV = findViewById(R.id.resultTV);
        ocrTV.setText(ocrResult);
        Log.d("isroot", "ocrResult:"+ocrResult);

        ImageView contentIV = findViewById(R.id.contentIV);
        contentIV.setImageBitmap(bitmap);
    }

    //파일 복제
    private void copyFiles(String lang){
        try{
            //location we want the file to be at
            String filepath = dataPath + "/tessdata/" + lang+".traineddata";

            //get access to AssetManager
            AssetManager assetManager = getAssets();

            //open byte streams for r/w
            InputStream inStream = assetManager.open("tessdata/"+lang+".traineddata");
            OutputStream outStream = new FileOutputStream(filepath);

            //copy the file to the location specified by filepath
            byte[] buffer = new byte[1024];
            int read;
            while((read = inStream.read(buffer))!=-1){
                outStream.write(buffer, 0, read);
            }
            outStream.flush();
            outStream.close();
            inStream.close();
        }catch (FileNotFoundException fe){
            fe.printStackTrace();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

    //파일 존재 확인
    private void checkFile(File dir, String lang){
        //directory does not exist, but we can successfully create it
        if(!dir.exists() && dir.mkdirs()){
            copyFiles(lang);
        }
        //The directory exists, but there is no data file in it
        if(dir.exists()){
            String datafilePath = dataPath+"/tessdata/"+lang+".traineddata";
            File datafile = new File(datafilePath);
            if(!datafile.exists()){
                copyFiles(lang);
            }
        }
    }
}
