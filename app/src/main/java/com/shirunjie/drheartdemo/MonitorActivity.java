package com.shirunjie.drheartdemo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import io.smooch.core.Message;
import io.smooch.ui.ConversationActivity;

public class MonitorActivity extends Activity {
    enum State {
        HIGH, NORMAL, LOW
    };
    MessageHelper messageHelper = new MessageHelper();
    boolean monitorRunning = false;
    private int heartRate;
    private int lower;
    private int higher;
    private State state = State.NORMAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        if (savedInstanceState != null) {
            monitorRunning = savedInstanceState.getBoolean("monitorRunning");
            heartRate = savedInstanceState.getInt("monitorRunning");
            lower = savedInstanceState.getInt("lower");
            higher = savedInstanceState.getInt("higher");
        }
        runMonitor();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("monitorRunning", monitorRunning);
        outState.putInt("heartRate", heartRate);
        outState.putInt("lower", lower);
        outState.putInt("higher", higher);
    }

    private void runMonitor() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (monitorRunning) {
                    updateBounds();
                    updateMainView();
                }
                handler.postDelayed(this, 500);
            }
        });

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (monitorRunning) {
                    autoIncrement();
                }
                handler.postDelayed(this, 1000);
            }
        });

        handler.post(new Runnable() {
            @Override
            public void run() {
                Switch manualOn = (Switch)findViewById(R.id.demo_switch);
                if (manualOn.isChecked()) {
                    findViewById(R.id.manual_heart_rate).setVisibility(View.VISIBLE);
                    findViewById(R.id.submit_heart_rate).setVisibility(View.VISIBLE);
                    findViewById(R.id.comment).setVisibility(View.VISIBLE);
                    findViewById(R.id.increment_group).setVisibility(View.VISIBLE);
                } else {
                    monitorRunning = false;
                    findViewById(R.id.manual_heart_rate).setVisibility(View.INVISIBLE);
                    findViewById(R.id.submit_heart_rate).setVisibility(View.INVISIBLE);
                    findViewById(R.id.comment).setVisibility(View.INVISIBLE);
                    findViewById(R.id.increment_group).setVisibility(View.INVISIBLE);
                }
                handler.postDelayed(this, 500);
            }
        });
    }

    private void autoIncrement() {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.increment_group);
        int id = radioGroup.getCheckedRadioButtonId();
        switch (id) {
            case -1:
                radioGroup.check(R.id.constant);
                break;
            case R.id.constant:
                break;
            case R.id.increment:
                heartRate++;
                break;
            case R.id.decrement:
                heartRate--;
                break;
        }
    }

    private void updateBounds() {
        Integer temp = getBoundValue((EditText)findViewById(R.id.lower_bound_val));
        if (temp != null) {
            lower = temp;
        }
        temp = getBoundValue((EditText)findViewById(R.id.higher_bound_val));
        if (temp != null) {
            higher = temp;
        }

    }

    private Integer getBoundValue(EditText viewId) {
        try {
            return Integer.parseInt(viewId.getText().toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void updateMainView() {
        TextView mainView = (TextView)findViewById(R.id.main_view);
        TextView mainComment = (TextView)findViewById(R.id.main_comment);
        String mainText = String.format("%d", heartRate);
        mainView.setText(mainText);

        String commentText;
        State prevState = state;
        if (heartRate < lower) {
            state = State.LOW;
            mainView.setTextColor(Color.RED);
            mainComment.setTextColor(Color.RED);
            commentText = "Heart rate too low!";
        } else if (heartRate > higher) {
            state = State.HIGH;
            mainView.setTextColor(Color.RED);
            mainComment.setTextColor(Color.RED);
            commentText = "Heart rate too high!";
        } else {
            state = State.NORMAL;
            mainView.setTextColor(Color.GREEN);
            mainComment.setTextColor(Color.GREEN);
            commentText = "Normal";
        }

        mainComment.setText(commentText);
        if (prevState != state) {
            messageHelper.sendMessage(new Message(
                    String.format("Heart rate: %d\n%s", heartRate, commentText)));
        }

    }

    public void onClickStart(View view) {
        ((Switch) (findViewById(R.id.demo_switch))).setChecked(true);
        monitorRunning = true;
        onclickSubmit(findViewById(R.id.submit_heart_rate));
        messageHelper.sendOnMessage();
    }

    public void onClickStop(View view) {
        monitorRunning = false;
        messageHelper.sendOffMessage();

        ((TextView)findViewById(R.id.main_view)).setTextColor(Color.rgb(220, 220, 220));
        ((TextView)findViewById(R.id.main_comment)).setTextColor(Color.rgb(220, 220, 220));
    }

    public void onClickPair(View view) {
        return;
    }

    public void onClickChat(View view) {
        ConversationActivity.show(this);
    }

    public void onRadioModeClicked(View view) {
        return;
    }

    public void onclickSubmit(View view) {
        if (geCurrHeartRate() == null) {
            CharSequence text = "New heart rate string is invalid!";
            Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private Integer geCurrHeartRate() {
        EditText manualHeartRate = (EditText)findViewById(R.id.manual_heart_rate);
        String heartRateString = manualHeartRate.getText().toString();
        try {
            heartRate = Integer.parseInt(heartRateString);
            return heartRate;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }
}