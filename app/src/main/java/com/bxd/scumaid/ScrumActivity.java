package com.bxd.scumaid;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ScrumActivity extends AppCompatActivity {

    private List<String> mAttendeesEnList;
    private List<String> mAttendeesCnList;
    private List<Integer> mScrumOrder;
    private int mAttendeeIndex = 0;
    private CountDownTimer mTimerTimeLeft = null;
    private CountDownTimer mTimerShineAlert = null;
    private TextView mTvName;
    private TextView mTvTime;
    private SpeechSynthesizer mTts;
    private SynthesizerListener mSynListener = new SynthesizerListener() {
        public void onCompleted(SpeechError error) {
        }

        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
        }

        public void onSpeakBegin() {
        }

        public void onSpeakPaused() {
        }

        public void onSpeakProgress(int percent, int beginPos, int endPos) {
        }

        public void onSpeakResumed() {
        }

        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
        }
    };

    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                Log.d(ScrumAidConstants.TAG, "InitListener failed code = " + code);
            } else {
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrum);

        initAttendees();
        if (mAttendeesEnList.size() == 0) {
            Toast.makeText(this, "No attendess available!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mTvName = (TextView) findViewById(R.id.attendeeName);
        mTvTime = (TextView) findViewById(R.id.timeLeft);

        // Make attendees random
        mScrumOrder = new ArrayList<>();
        for (int i = 0; i < mAttendeesCnList.size(); ++i) {
            mScrumOrder.add(i);
        }
        Collections.shuffle(mScrumOrder);

        // Prevent screen auto locking
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mTts = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener);
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
        mTts.setParameter(SpeechConstant.SPEED, "50");
        mTts.setParameter(SpeechConstant.VOLUME, "100");
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        mTts.startSpeaking("晨会开始", mSynListener);

        while (mTts.isSpeaking()) {
            SystemClock.sleep(1000);
        }
        showTime();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("Are you sure to stop scrum?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //super.onBackPressed();
                mTimerTimeLeft.cancel();
                mTimerShineAlert.cancel();
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void nextAttendee(View view) {
        mAttendeeIndex++;
        if (mAttendeeIndex == mScrumOrder.size()) {
            if (mTimerTimeLeft != null) {
                mTimerTimeLeft.cancel();
            }
            if (mTimerShineAlert != null) {
                mTimerShineAlert.cancel();
            }
            mTvName.setText("END");
            mTvTime.setText("0");
            mTts.startSpeaking("晨会结束", mSynListener);
            while (mTts.isSpeaking()) {
                SystemClock.sleep(1000);
            }
            finish();
        } else if(mAttendeeIndex < mScrumOrder.size()) {
            showTime();
        }
    }

    public void showTime() {
        if (mTimerTimeLeft != null) {
            mTimerTimeLeft.cancel();
        }
        if (mTimerShineAlert != null) {
            mTimerShineAlert.cancel();
        }

        mTvTime.setBackgroundColor(0x00000000);
        mTvName.setText(mAttendeesEnList.get(mScrumOrder.get(mAttendeeIndex)));
        speak();

        mTimerTimeLeft = new CountDownTimer(61000, 1000) {
            public void onTick(long millisUntilFinished) {
                long second = millisUntilFinished/1000;
                if (second == 10) {
                    mTts.startSpeaking("还剩十秒钟", mSynListener);
                }
                mTvTime.setText(Long.toString(second));
            }

            public void onFinish() {
                mTts.startSpeaking("时间到", mSynListener);
                mTvTime.setText("!0!");
                if (mTimerShineAlert != null) {
                    mTimerShineAlert.start();
                }
            }
        };
        mTimerShineAlert = new CountDownTimer(9000000, 100) {
            public void onTick(long millisUntilFinished) {
                long second = millisUntilFinished/100;
                if (second % 3 == 0) {
                    mTvTime.setBackgroundColor(0xffff0000);
                } else {
                    mTvTime.setBackgroundColor(0x00000000);
                }
            }

            public void onFinish() {
                mTvTime.setText("Timeout");
            }
        };
        mTimerTimeLeft.start();

    }

    public void speak() {
        mTts.startSpeaking("下一位，"+ mAttendeesCnList.get(mScrumOrder.get(mAttendeeIndex)), mSynListener);
    }

    public void initAttendees() {
        SQLiteDatabase db = ScrumDbHelper.getInstance(this).getReadableDatabase();
        mAttendeesEnList = new ArrayList<>();
        mAttendeesCnList = new ArrayList<>();

        String[] projection = {
                ScrumDbSchema.ScrumEntry.COLUMN_NAME_EN,
                ScrumDbSchema.ScrumEntry.COLUMN_NAME_CN
        };
        Cursor cursor = db.query(ScrumDbSchema.ScrumEntry.TABLE_NAME, projection, null, null, null, null, null);
        Log.d(ScrumAidConstants.TAG, "Start query from DB");
        while (cursor.moveToNext()) {
            String nameEn = cursor.getString(cursor.getColumnIndexOrThrow(ScrumDbSchema.ScrumEntry.COLUMN_NAME_EN));
            mAttendeesEnList.add(nameEn);
            String nameCn = cursor.getString(cursor.getColumnIndexOrThrow(ScrumDbSchema.ScrumEntry.COLUMN_NAME_CN));
            mAttendeesCnList.add(nameCn);
            Log.d(ScrumAidConstants.TAG, "Query from DB, EN name: "+nameEn+", CN name: "+nameCn);
        }
    }
}
