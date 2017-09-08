package com.example.yogeshtikam.wordcountcorrection;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.TextInfo;
import android.view.textservice.TextServicesManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SpellCheckerSession.SpellCheckerSessionListener{

    private EditText mEditText;

    private Button mCountBtn;
    private Button mCheckBtn;

    private TextView mCountTxt;
    private TextView mCorrectedTxt;

    private String mString;

    private int mWordCount;


    private SpellCheckerSession mSpellCheckerSession;

    private TextServicesManager mTextServicesManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditText = (EditText) findViewById(R.id.editText);
        mCountBtn = (Button) findViewById(R.id.check_no_of_words);
        mCheckBtn = (Button) findViewById(R.id.check_text);
        mCountTxt = (TextView) findViewById(R.id.no_of_words_value);
        mCorrectedTxt = (TextView) findViewById(R.id.corrected_text);

        mTextServicesManager = (TextServicesManager) getSystemService(Context.TEXT_SERVICES_MANAGER_SERVICE);
        mSpellCheckerSession = mTextServicesManager.newSpellCheckerSession(null,null,this,true);

        mCountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkWordCount();
            }
        });

        mCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkText();
            }
        });
    }

    private void checkWordCount() {

        mString = mEditText.getText().toString().trim();

        if (mString.isEmpty() || mString == null) {
            mCountTxt.setText(String.valueOf(mWordCount));
        } else {
            String[] words = mString.split("[\\s\\d(),/@&.?$+-]+"); // \\d for numbers
            mWordCount = words.length;
            for (int i = 0; i < mWordCount; i++) {
                Log.v("word no " + i + ":", words[i]);
            }
            mCountTxt.setText(String.valueOf(mWordCount));
        }
    }

    private void checkText() {

        mString = mEditText.getText().toString().trim();

//        mSpellCheckerSession.getSentenceSuggestions(new TextInfo[]{new TextInfo(mString)},1);
        mSpellCheckerSession.getSuggestions(new TextInfo(mString),1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mSpellCheckerSession != null){
            mSpellCheckerSession.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTextServicesManager = (TextServicesManager) getSystemService(Context.TEXT_SERVICES_MANAGER_SERVICE);
    }

    @Override
    public void onGetSuggestions(SuggestionsInfo[] suggestionsInfos) {
        mCorrectedTxt.setText("");
        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < suggestionsInfos.length; ++i) {
            // Returned suggestions are contained in SuggestionsInfo
            final int len = suggestionsInfos[i].getSuggestionsCount();
            sb.append('\n');

            for (int j = 0; j < len; ++j) {
                Log.v("suggestions", suggestionsInfos[i].getSuggestionAt(j));
                sb.append(suggestionsInfos[i].getSuggestionAt(j));
            }

//            sb.append(" (" + len + ")");
        }

        runOnUiThread(new Runnable() {
            public void run() {
                mCorrectedTxt.append(sb.toString());
            }
        });
    }

    @Override
    public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] sentenceSuggestionsInfos) {
        mCorrectedTxt.setText("");
        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < sentenceSuggestionsInfos.length; ++i) {

            Log.v("suggestions", sentenceSuggestionsInfos[i].toString());

            // Returned suggestions are contained in SuggestionsInfo
            final int len = sentenceSuggestionsInfos[i].getSuggestionsCount();
            sb.append('\n');

            for (int j = 0; j < len; ++j) {
                sb.append("," + sentenceSuggestionsInfos[i].getSuggestionsInfoAt(j).toString());
            }

//            sb.append(" (" + len + ")");
        }

        runOnUiThread(new Runnable() {
            public void run() {
                mCorrectedTxt.append(sb.toString());
            }
        });
    }
}
