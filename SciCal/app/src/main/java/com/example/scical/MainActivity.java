package com.example.scical;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.mariuszgromada.math.mxparser.Expression;

public class MainActivity extends AppCompatActivity {

    private TextView display;
    private final StringBuilder inputExpression = new StringBuilder();
    private boolean lastInputIsOperator = false; // To prevent multiple operators

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        display = findViewById(R.id.display);

        // Initialize all number and operator buttons
        setupNumberAndOperatorButtons();

        // Special buttons
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            findViewById(R.id.button_ac).setOnClickListener(this::onClearClick);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            findViewById(R.id.button_equal).setOnClickListener(this::onEqualClick);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            findViewById(R.id.button_dot).setOnClickListener(this::onDotClick);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            findViewById(R.id.button_plus_minus).setOnClickListener(this::onPlusMinusClick);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            findViewById(R.id.button_percent).setOnClickListener(this::onPercentClick);
        }

        // Button to switch to scientific mode
        ImageButton sciModeButton = findViewById(R.id.button_sci_mode);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            sciModeButton.setOnClickListener(v -> showModeSelectionDialog());
        }
    }

    private void performHapticFeedback(int type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            VibrationEffect effect = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                effect = VibrationEffect.createPredefined(type);
            }
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(effect);
            }
        } else {
            // For older versions, fallback to basic vibration
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(50); // 50ms
            }
        }
    }

    private void switchToStandardMode() {
        performHapticFeedback(VibrationEffect.EFFECT_CLICK);
        startActivity(new Intent(this, MainActivity.class));
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void showModeSelectionDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.dialog_mode_selection); // Set the layout ID directly
        bottomSheetDialog.show();

        View view = bottomSheetDialog.findViewById(android.R.id.content);
        if (view != null) {
            view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up));
        }

        // Get buttons
        TextView basicMode = bottomSheetDialog.findViewById(R.id.mode_basic);
        TextView sciMode = bottomSheetDialog.findViewById(R.id.mode_sci);
        TextView graphMode = bottomSheetDialog.findViewById(R.id.mode_graph);

        // Highlight current mode
        if (basicMode != null) {
            basicMode.setBackgroundResource(R.drawable.mode_selected_background);
            basicMode.setTextColor(ContextCompat.getColor(this, R.color.white));
        }

        // Click listeners
        assert basicMode != null;
        basicMode.setOnClickListener(v -> bottomSheetDialog.dismiss());
        assert sciMode != null;
        sciMode.setOnClickListener(v -> {
            performHapticFeedback(VibrationEffect.EFFECT_CLICK); // subtle click
            startActivity(new Intent(MainActivity.this, ScientificActivity.class));
            finish();
//            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            bottomSheetDialog.dismiss();
        });
        assert graphMode != null;
        graphMode.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, GraphingActivity.class));
            finish();
//            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.setOnDismissListener(dialog -> {
            if(view != null){
                view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_down));
            }
        });
    }

private void setupNumberAndOperatorButtons() {
        int[] numberIds = {R.id.button_0, R.id.button_1, R.id.button_2, R.id.button_3,
                R.id.button_4, R.id.button_5, R.id.button_6, R.id.button_7,
                R.id.button_8, R.id.button_9};

        int[] operatorIds = {R.id.button_plus, R.id.button_minus,
                R.id.button_multiply, R.id.button_divide};

        for (int id : numberIds) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                findViewById(id).setOnClickListener(this::onNumberClick);
            }
        }

        for (int id : operatorIds) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                findViewById(id).setOnClickListener(this::onOperatorClick);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void onNumberClick(View view) {
        Button button = (Button) view;
        performHapticFeedback(VibrationEffect.EFFECT_TICK); // light tap
        inputExpression.append(button.getText().toString());
        display.setText(inputExpression.toString());
        lastInputIsOperator = false; // Reset operator flag
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void onOperatorClick(View view) {
        if (inputExpression.length() == 0) return; // Prevent operator at start

        if (lastInputIsOperator) {
            inputExpression.setLength(inputExpression.length() - 1); // Replace last operator
        }

        Button button = (Button) view;
        performHapticFeedback(VibrationEffect.EFFECT_TICK); // light tap
        inputExpression.append(button.getText().toString());
        display.setText(inputExpression.toString());
        lastInputIsOperator = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void onDotClick(View view) {
        if (inputExpression.length() == 0 || lastInputIsOperator) {
            inputExpression.append("0."); // Add leading zero if needed
        } else {
            inputExpression.append(".");
        }
        performHapticFeedback(VibrationEffect.EFFECT_TICK); // light tap
        display.setText(inputExpression.toString());
        lastInputIsOperator = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void onPlusMinusClick(View view) {
        if (inputExpression.length() == 0) return;

        char firstChar = inputExpression.charAt(0);
        if (firstChar == '-') {
            inputExpression.deleteCharAt(0);
        } else {
            inputExpression.insert(0, "-");
        }
        performHapticFeedback(VibrationEffect.EFFECT_TICK); // light tap
        display.setText(inputExpression.toString());
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void onPercentClick(View view) {
        if (inputExpression.length() == 0) return;

        try {
            double value = Double.parseDouble(inputExpression.toString());
            value /= 100;
            inputExpression.setLength(0);
            inputExpression.append(value);
            performHapticFeedback(VibrationEffect.EFFECT_TICK); // light tap
            display.setText(inputExpression.toString());
        } catch (Exception e) {
            display.setText(getString(R.string.error));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void onClearClick(View view) {
        performHapticFeedback(VibrationEffect.EFFECT_TICK); // light tap
        inputExpression.setLength(0);
        display.setText("0");
        lastInputIsOperator = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void onEqualClick(View view) {
        if (inputExpression.length() == 0) return;

        try {
            String expression = inputExpression.toString();
            Expression mathExpression = new Expression(expression);
            double result = mathExpression.calculate();

            if (Double.isNaN(result) || Double.isInfinite(result)) {
                display.setText(getString(R.string.error)); // Handle invalid calculations
            } else {
                performHapticFeedback(VibrationEffect.EFFECT_TICK); // light tap
                display.setText(String.valueOf(result));
                inputExpression.setLength(0);
                inputExpression.append(result);
            }
        } catch (Exception e) {
            display.setText(getString(R.string.error));
        }
    }
}
