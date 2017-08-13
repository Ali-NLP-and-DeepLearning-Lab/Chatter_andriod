package com.hanium.chat.chatter_andriod.css;

import android.content.Context;
import android.media.MediaPlayer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

/**
 * Created by kliv on 2017-08-06.
 */

public class TTSConfig {

    private String clientId;
    private String clientSecret;
    private Context context;

    public TTSConfig(String clientId, String clientSecret, Context context) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.context = context;
    }

    public void speak(String[] message) {
        int responseCode = 0;

        try {
            String encodedText = URLEncoder.encode(message[0], "UTF-8");
            String apiURL = "https://openapi.naver.com/v1/voice/tts.bin";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("X-Naver-Client-Id", clientId);
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
            // post request
            String postParams = "speaker=jinho&speed=0&text=" + encodedText;
            con.setDoOutput(true);
            con.setDoInput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
            responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode==200) { // 정상 호출
                InputStream is = con.getInputStream();
                int read = 0;
                byte[] bytes = new byte[1024];
                String tempname = "ttsTemp";
                File f = new File(context.getCacheDir() + File.separator + tempname + ".mp3");
                f.createNewFile();
                OutputStream outputStream = new FileOutputStream(f);
                while ((read =is.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
                is.close();

                String audioPath = context.getCacheDir() + "/" + tempname + ".mp3";
                MediaPlayer audioPlay = new MediaPlayer();
                audioPlay.setDataSource(audioPath);
                audioPlay.prepare();
                audioPlay.start();
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                System.out.println(response.toString());
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
