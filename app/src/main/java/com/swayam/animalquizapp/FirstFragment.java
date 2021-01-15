package com.swayam.animalquizapp;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.github.nikartm.button.FitButton;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FirstFragment extends Fragment {
    private static final String TAG = "FirstFragment";

    private List<String> allAnimalsNamesList;
    private List<String> animalsNameQuizList;
    private Set<String> animalTypesInQuiz;
    private String correctAnimalAnswer;
    private int numberOfAllGuesses;
    private int numberOfRightAnswers;
    private int currentQuestionNumber;
    private int maxNumberOfQuestion = 10;
    private int numberOfWrongAnswers;
    private int numberOfAnimalsGuessButtons = 2;
    private SecureRandom secureRandom;
    private Handler handler;
    private Animation wrongAnswerAnimation;
    private ArrayList<String> tameAnimalList;
    private ArrayList<String> wildAnimalList;
    private List<String> remainingAnimalList;

    private Typeface currentTypeface;

    @BindView(R.id.animal_quiz_linearLayout)
    LinearLayout animalQuizLinearLayout;
    @BindView(R.id.buttonGrid)
    GridLayout buttonGrid;
    @BindView(R.id.question_number)
    TextView textQuestionNumber;
    @BindView(R.id.animal_image)
    ImageView imgAnimal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,getView());

        allAnimalsNamesList = new ArrayList<>();
        animalsNameQuizList = new ArrayList<>();
        secureRandom = new SecureRandom();
        handler = new Handler();

        wrongAnswerAnimation = AnimationUtils.loadAnimation(getContext(),R.anim.wrong_answer_animation);
        wrongAnswerAnimation.setRepeatCount(1);

        numberOfAnimalsGuessButtons = Integer.parseInt(SharedPreferenceManager.getString(getContext(), MainActivity.GUESS,"2"));

        updateBackground();
        initAllAnimalList();
        updateFontForAll();

        PreferenceManager.getDefaultSharedPreferences(getContext())
                .registerOnSharedPreferenceChangeListener(settingChangeListener);
    }

    private void restartQuiz(){
        currentQuestionNumber = -1;
        remainingAnimalList = new ArrayList<>(allAnimalsNamesList);
        try {
            nextQuestion();
        }catch (Exception e){
            Log.i(TAG, "restartQuiz: "+e.getMessage());
        }
    }

    private void nextQuestion() throws Exception{
        currentQuestionNumber++;
        correctAnimalAnswer = remainingAnimalList.get(secureRandom.nextInt(remainingAnimalList.size()));
        Log.i(TAG, "nextQuestion: "+correctAnimalAnswer);
        remainingAnimalList.remove(correctAnimalAnswer);

        //updating buttons
        updateNumberOfButtons();

        //updating question number
        textQuestionNumber.setText(getString(R.string.question_text,currentQuestionNumber+1,maxNumberOfQuestion));

        //updating new image
        imgAnimal.setImageBitmap(null);
        String folder = (tameAnimalList.contains(correctAnimalAnswer)) ? "tame_animals":"wild_animals";

        Glide.with(getContext()).load(Uri.parse("file:///android_asset/"+folder+"/"+correctAnimalAnswer+".png")).into(imgAnimal);

    }

    //update question textView color on background updates
    private void updateQuestionTextViewColor(String bgColor){
        if (bgColor.equals("#ffffff")){
            textQuestionNumber.setTextColor(getContext().getResources().getColor(R.color.black));
        }else {
            textQuestionNumber.setTextColor(getContext().getResources().getColor(R.color.white));
        }
    }

    //complete dialog to show progress
    private void showCompleteDialogBox(){
        float result = (float) maxNumberOfQuestion / (float) (maxNumberOfQuestion+numberOfWrongAnswers) * 100;
        Log.i(TAG, "showCompleteDialogBox: "+result);
        String message = getString(R.string.progress_dialog_text,result,maxNumberOfQuestion);
        new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton("Restart Quiz", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        restartQuiz();
                    }
                })
                .create()
                .show();
    }

    //initialize tame Animals and Wild Animals
    private void initAllAnimalList(){
        Set<String> animalTypes = SharedPreferenceManager.getStrings(getContext(),MainActivity.ANIMAL_TYPE,new HashSet());
        Log.i(TAG, "initAllAnimalList: "+animalTypes.size());
        tameAnimalList = new ArrayList<>();
        wildAnimalList = new ArrayList<>();
        try {
            for (String animalType : animalTypes) {
                String[] names = getContext().getAssets().list(animalType);
                for (String name : names) {
                    name = name.substring(0, name.indexOf('.'));
                    if (animalType.equals("tame_animals")){
                        tameAnimalList.add(name);
                    }else {
                        wildAnimalList.add(name);
                    }
                    allAnimalsNamesList.add(name);
                }
            }

        } catch (IOException e) {
            Log.i(TAG, "initAllAnimalList: "+e.getMessage());
        }
        restartQuiz();
    }

    //updating fonts for all texts
    private void updateFontForAll(){
        String choseFont = SharedPreferenceManager.getString(getContext(),MainActivity.QUIZ_FONT,"allura.otf");
        Log.i(TAG, "updateFontForAll: "+choseFont);
        currentTypeface = Typeface.createFromAsset(getContext().getAssets(),"fonts/"+choseFont);

        textQuestionNumber.setTypeface(currentTypeface);

        for (int i=0;i<buttonGrid.getChildCount();i++){
            FitButton fitButton = (FitButton)buttonGrid.getChildAt(i);
            fitButton.setTextTypeface(currentTypeface);
        }

    }

    // updating number of buttons when guess button count settings changed
    private void updateNumberOfButtons(){
        int count = Integer.parseInt(SharedPreferenceManager.getString(getContext(), MainActivity.GUESS,"2"));
        numberOfAnimalsGuessButtons = count;
        buttonGrid.removeAllViews();

        List<String> allAnimals = new ArrayList<>(allAnimalsNamesList);
        allAnimals.remove(correctAnimalAnswer);
        String[] animalsOnButton = new String[count];
        for(int i=0;i<count;i++){
            animalsOnButton[i] = allAnimals.get(secureRandom.nextInt(allAnimals.size()));
            allAnimals.remove(animalsOnButton[i]);
        }
        animalsOnButton[secureRandom.nextInt(count)] = correctAnimalAnswer;

        

        for(int i=0;i<count;i++){
            FitButton fitButton = (FitButton) LayoutInflater.from(getContext()).inflate(R.layout.button_view,null);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(
                    GridLayout.spec(GridLayout.UNDEFINED, 1f),
                    GridLayout.spec(GridLayout.UNDEFINED, 1f)
            );
            params.setMargins(50,20,50,20);
            params.width = 0;
            fitButton.setLayoutParams(params);
            fitButton.setTag(animalsOnButton[i]);
            fitButton.setText(animalsOnButton[i]);
            fitButton.setTextTypeface(currentTypeface);
            fitButton.setOnClickListener(onFitButtonClickListener);
            buttonGrid.addView(fitButton);
        }

    }

    //updating background color
    private void updateBackground(){
        String color = SharedPreferenceManager.getString(getContext(),MainActivity.QUIZ_BACKGROUND_COLOR,"#ffffff");
        animalQuizLinearLayout.setBackgroundColor(Color.parseColor(color));
        updateQuestionTextViewColor(color);
    }

    //disable all button after right answer
    private void disableAllButtons(){
        for (int i=0;i<buttonGrid.getChildCount();i++){
            buttonGrid.getChildAt(i).setClickable(false);
        }
    }

    private View.OnClickListener onFitButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //On write Answer
            if (v.getTag().toString().equals(correctAnimalAnswer)){
                numberOfRightAnswers++;
                ((FitButton)v).setBackgroundColor(getContext().getResources().getColor(R.color.right_answer_color));
                disableAllButtons();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            animateOnNextQuestion(true);
                        }catch (Exception e){
                            Log.i(TAG, "run: "+e.toString());
                        }
                    }
                },1000);
            }
            //On Wring Answer
            else {
                ((FitButton)v).setBackgroundColor(getContext().getResources().getColor(R.color.wrong_answer_color));
                v.setClickable(false);
                numberOfWrongAnswers++;
                imgAnimal.startAnimation(wrongAnswerAnimation);
            }
        }
    };

    private SharedPreferences.OnSharedPreferenceChangeListener settingChangeListener
            = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(MainActivity.QUIZ_FONT)){
                updateFontForAll();
            }
            if (key.equals(MainActivity.QUIZ_BACKGROUND_COLOR)){
                updateBackground();
            }
            if (key.equals(MainActivity.ANIMAL_TYPE)){
                initAllAnimalList();
            }
            if (key.equals(MainActivity.GUESS)){
                updateNumberOfButtons();
            }
            Toast.makeText(getContext(), getString(R.string.setting_changed_text), Toast.LENGTH_SHORT).show();
        }
    };

    //animate layout on next question
    private void animateOnNextQuestion(boolean value){
        if (currentQuestionNumber == maxNumberOfQuestion-1) {
            showCompleteDialogBox();
            return;
        }

        int xTopLeft = 0;
        int yTopLeft = 0;

        int xBottomRight = animalQuizLinearLayout.getLeft() + animalQuizLinearLayout.getRight();
        int yBottomRight = animalQuizLinearLayout.getTop() + animalQuizLinearLayout.getBottom();

        int radius = Math.max(animalQuizLinearLayout.getWidth(),animalQuizLinearLayout.getHeight());

        Animator animator;

        if (value) {

            animator = ViewAnimationUtils.createCircularReveal(animalQuizLinearLayout, xBottomRight, yBottomRight, radius, 0);

            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    animateOnNextQuestion(false);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }else {
            animator = ViewAnimationUtils.createCircularReveal(animalQuizLinearLayout, xTopLeft, yTopLeft, 0, radius);
            try {
                nextQuestion();
            } catch (Exception exception) {
                Log.i(TAG, "onAnimationEnd: " + exception.toString());
            }
        }

        animator.setDuration(700);
        animator.start();
    }

}