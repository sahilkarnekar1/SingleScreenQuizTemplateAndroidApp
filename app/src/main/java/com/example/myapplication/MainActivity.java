package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView questionTextView;
    private ImageView questionImageView;
    private RadioGroup optionsRadioGroup;
    private Button nextButton;
    private ProgressBar progressBar;
    private SparseArray<Integer> selectedOptions = new SparseArray<>();



    private int currentQuestionIndex = 0;
    private List<Question> questionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize views
        questionTextView = findViewById(R.id.questionTextView);
        questionImageView = findViewById(R.id.questionImageView);
        optionsRadioGroup = findViewById(R.id.optionsRadioGroup);
        nextButton = findViewById(R.id.nextButton);
        progressBar = findViewById(R.id.progressBar);

        // Load questions
        loadQuestions();

        // Display first question
        displayQuestion(currentQuestionIndex);
        // Set maximum value for the progress bar
        progressBar.setMax(questionList.size());

        // Set onClickListener for nextButton
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if an option is selected
                int selectedOptionIndex = getSelectedOptionIndex(optionsRadioGroup);
                if (selectedOptionIndex == -1) {
                    Toast.makeText(MainActivity.this, "Please select an option", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Store the selected option index
                selectedOptions.put(currentQuestionIndex, selectedOptionIndex);
// Check answer and update progress
                if (checkAnswer(selectedOptionIndex)) {
                    // Correct answer
                    Toast.makeText(MainActivity.this, "Correct!", Toast.LENGTH_SHORT).show();
                } else {
                    // Incorrect answer
                    Toast.makeText(MainActivity.this, "Incorrect!", Toast.LENGTH_SHORT).show();
                }

                // Move to next question
                currentQuestionIndex++;
                if (currentQuestionIndex < questionList.size()) {

                    displayQuestion(currentQuestionIndex);
                } else {
                    // End of quiz
                    Toast.makeText(MainActivity.this, "End of Quiz", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, ThankYouActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void loadQuestions() {
        try {
            InputStream inputStream = getAssets().open("questions.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String jsonString = new String(buffer, "UTF-8");

            // Convert JSON string to list of Question objects
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Question>>() {}.getType();
            questionList = gson.fromJson(jsonString, listType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayQuestion(int index) {

        // Load the animation
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        // Display question at given index
        Question question = questionList.get(index);
        questionTextView.setText(question.getQuestionText());
        if (question.getQuestionImage() != null) {
            String imageUrl = question.getQuestionImage(); // Assuming getQuestionImage() returns image URL
            Picasso.get().load(imageUrl).into(questionImageView);
            questionImageView.setVisibility(View.VISIBLE);
            questionImageView.startAnimation(fadeInAnimation);
        } else {
            questionImageView.setVisibility(View.GONE);
        }

        // Clear optionsRadioGroup and add options
        optionsRadioGroup.removeAllViews();
        for (int i = 0; i < question.getOptions().size(); i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(question.getOptions().get(i));
            optionsRadioGroup.addView(radioButton);

            // Check if this option was previously selected
            if (selectedOptions.indexOfKey(index) >= 0 && selectedOptions.get(index) == i) {
                radioButton.setChecked(true);
            }
            optionsRadioGroup.startAnimation(fadeInAnimation);
        }
// Apply fade-in animation to the question view
        questionTextView.startAnimation(fadeInAnimation);

        // Update progress
        progressBar.setProgress(index + 1);
    }


    private boolean checkAnswer(int selectedOptionIndex) {
        Question currentQuestion = questionList.get(currentQuestionIndex);
        return selectedOptionIndex == currentQuestion.getCorrectAnswerIndex();
    }
    // Method to get the index of the selected option in the RadioGroup
    private int getSelectedOptionIndex(RadioGroup radioGroup) {
        int selectedOptionId = radioGroup.getCheckedRadioButtonId();
        View radioButton = radioGroup.findViewById(selectedOptionId);
        if (radioButton != null) {
            return radioGroup.indexOfChild(radioButton);
        }
        return -1; // No option selected
    }
    @Override
    public void onBackPressed() {
        if (currentQuestionIndex > 0) {
            // If there are previous questions, move to the previous question
            currentQuestionIndex--;
            displayQuestion(currentQuestionIndex);
        } else {
            // If this is the first question, proceed with the default back behavior (finish activity)
            super.onBackPressed();
        }
    }

}