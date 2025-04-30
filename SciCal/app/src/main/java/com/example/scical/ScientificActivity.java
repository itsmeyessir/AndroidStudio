package com.example.scical;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.mariuszgromada.math.mxparser.Expression;
import java.util.HashMap;
import java.util.Map;

public class ScientificActivity extends AppCompatActivity {

    private TextView display;
    private StringBuilder inputExpression = new StringBuilder();
    private boolean lastInputIsOperator = false;
    private boolean isRadiansMode = true;
    private boolean isSecondFunctionMode = false;
    private double memoryValue = 0;
    private boolean isMemoryStored = false;

    // Map for second functions
    private final Map<Integer, String> secondFunctionMap = new HashMap<Integer, String>() {{
        put(R.id.button_sin, "sin⁻¹");
        put(R.id.button_cos, "cos⁻¹");
        put(R.id.button_tan, "tan⁻¹");
        put(R.id.button_sinh, "sinh⁻¹");
        put(R.id.button_cosh, "cosh⁻¹");
        put(R.id.button_tanh, "tanh⁻¹");
        put(R.id.button_ln, "logᵧ");
        put(R.id.button_log, "log₂");
        put(R.id.button_ex, "yˣ");
        put(R.id.button_10x, "2ˣ");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scientific);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        display = findViewById(R.id.display);
        setupAllButtons();
        updateMemoryButtonStates();
    }

    private void performHapticFeedback(int type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    vibrator.vibrate(VibrationEffect.createPredefined(type));
                } else {
                    vibrator.vibrate(50);
                }
            }
        }
    }

    private void setupAllButtons() {
        // Standard number buttons
        int[] numberButtons = {R.id.button_0, R.id.button_1, R.id.button_2, R.id.button_3,
                R.id.button_4, R.id.button_5, R.id.button_6, R.id.button_7,
                R.id.button_8, R.id.button_9, R.id.button_dot};

        for (int id : numberButtons) {
            findViewById(id).setOnClickListener(this::onNumberClick);
        }

        // Basic operation buttons
        int[] operationButtons = {R.id.button_plus, R.id.button_minus, R.id.button_multiply,
                R.id.button_divide, R.id.button_equal, R.id.button_ac,
                R.id.button_plus_minus, R.id.button_percent};

        for (int id : operationButtons) {
            findViewById(id).setOnClickListener(this::onOperationClick);
        }

        // Scientific function buttons
        int[] sciButtons = {R.id.button_sin, R.id.button_cos, R.id.button_tan,
                R.id.button_sinh, R.id.button_cosh, R.id.button_tanh,
                R.id.button_ln, R.id.button_log, R.id.button_fact,
                R.id.button_x2, R.id.button_x3, R.id.button_xy,
                R.id.button_ex, R.id.button_10x, R.id.button_sqrt,
                R.id.button_cbrt, R.id.button_yroot, R.id.button_inv,
                R.id.button_pi, R.id.button_e, R.id.button_ee,
                R.id.button_rand, R.id.button_paren_left, R.id.button_paren_right};

        for (int id : sciButtons) {
            findViewById(id).setOnClickListener(this::onScientificFunctionClick);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            findViewById(R.id.button_sci_mode).setOnClickListener(v -> showModeSelectionDialog());
        }

        // Memory buttons
        findViewById(R.id.button_mc).setOnClickListener(v -> memoryClear());
        findViewById(R.id.button_mplus).setOnClickListener(v -> memoryAdd());
        findViewById(R.id.button_mminus).setOnClickListener(v -> memorySubtract());
        findViewById(R.id.button_mr).setOnClickListener(v -> memoryRecall());

        // Special buttons
        findViewById(R.id.button_rad).setOnClickListener(v -> toggleAngleMode());
        findViewById(R.id.button_2nd).setOnClickListener(v -> toggleSecondFunctionMode());
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
        if (sciMode != null) {
            sciMode.setBackgroundResource(R.drawable.mode_selected_background);
            sciMode.setTextColor(ContextCompat.getColor(this, R.color.white));
        }

        // Click listeners
        assert sciMode != null;
        sciMode.setOnClickListener(v -> {
            assert view != null;
            bottomSheetDialog.dismiss();
        });
        assert basicMode != null;
        basicMode.setOnClickListener(v -> {
            performHapticFeedback(VibrationEffect.EFFECT_CLICK); // subtle click
            startActivity(new Intent(ScientificActivity.this, MainActivity.class));
            finish();
//            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            bottomSheetDialog.dismiss();
        });
        assert graphMode != null;
        graphMode.setOnClickListener(v -> {
            startActivity(new Intent(ScientificActivity.this, GraphingActivity.class));
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

    private void onNumberClick(View view) {
        Button button = (Button) view;
        String buttonText = button.getText().toString();
        performHapticFeedback(VibrationEffect.EFFECT_TICK);

        if (buttonText.equals(".")) {
            if (!inputExpression.toString().contains(".")) {
                inputExpression.append(buttonText);
            }
        } else {
            inputExpression.append(buttonText);
        }

        lastInputIsOperator = false;
        updateDisplay();
    }

    private void onOperationClick(View view) {
        Button button = (Button) view;
        String buttonText = button.getText().toString();
        performHapticFeedback(VibrationEffect.EFFECT_TICK);

        switch (buttonText) {
            case "AC":
                clearAll();
                break;
            case "=":
                calculateResult();
                break;
            case "+/-":
                toggleSign();
                break;
            case "%":
                inputExpression.append("/100");
                calculateResult();
                break;
            default:
                if (!lastInputIsOperator && inputExpression.length() > 0) {
                    inputExpression.append(buttonText);
                    lastInputIsOperator = true;
                } else if (inputExpression.length() == 0 && buttonText.equals("-")) {
                    inputExpression.append(buttonText);
                    lastInputIsOperator = false;
                }
                updateDisplay();
                break;
        }
    }

    private void onScientificFunctionClick(View view) {
        Button button = (Button) view;
        String function = button.getText().toString();
        performHapticFeedback(VibrationEffect.EFFECT_TICK);

        if (isSecondFunctionMode && secondFunctionMap.containsKey(view.getId())) {
            function = secondFunctionMap.get(view.getId());
        }

        switch (function) {
            // Trigonometric functions
            case "sin":
                appendTrigFunction("sin");
                break;
            case "cos":
                appendTrigFunction("cos");
                break;
            case "tan":
                appendTrigFunction("tan");
                break;
            case "sin⁻¹":
                appendInverseTrigFunction("arcsin");
                break;
            case "cos⁻¹":
                appendInverseTrigFunction("arccos");
                break;
            case "tan⁻¹":
                appendInverseTrigFunction("arctan");
                break;

            // Hyperbolic functions
            case "sinh":
                inputExpression.append("sinh(");
                break;
            case "cosh":
                inputExpression.append("cosh(");
                break;
            case "tanh":
                inputExpression.append("tanh(");
                break;
            case "sinh⁻¹":
                inputExpression.append("arsinh(");
                break;
            case "cosh⁻¹":
                inputExpression.append("arcosh(");
                break;
            case "tanh⁻¹":
                inputExpression.append("artanh(");
                break;

            // Logarithmic and exponential functions
            case "ln":
                inputExpression.append("ln(");
                break;
            case "log":
                inputExpression.append("log10(");
                break;
            case "eˣ":
                inputExpression.append("exp(");
                break;
            case "10ˣ":
                inputExpression.append("10^(");
                break;
            case "2ˣ":
                inputExpression.append("2^(");
                break;
            case "log₂":
                inputExpression.append("log2(");
                break;
            case "yˣ":
                inputExpression.append("^(");
                break;
            case "logᵧ":
                inputExpression.append("log10(");
                break;

            // Power functions
            case "x²":
                if (inputExpression.length() > 0) {
                    inputExpression.insert(0, "(").append(")^2");
                }
                break;
            case "x³":
                if (inputExpression.length() > 0) {
                    inputExpression.insert(0, "(").append(")^3");
                }
                break;
            case "xʸ":
            case "x^y":
            case "xy":
                inputExpression.append("^(");
                break;
            case "√x":
                inputExpression.append("sqrt(");
                break;
            case "∛x":
            case "cbrt":
                inputExpression.append("cbrt(");
                break;

            // Factorial
            case "x!":
                if (inputExpression.length() > 0) {
                    inputExpression.insert(0, "(").append(")!");
                }
                break;

            // Constants
            case "π":
                inputExpression.append("pi");
                break;
            case "e":
                inputExpression.append("e");
                break;

            // Random number
            case "Rand":
                inputExpression.append(String.valueOf(Math.random()));
                break;

            // Parentheses
            case "(":
            case ")":
                inputExpression.append(function);
                break;

            // Exponent entry
            case "EE":
                inputExpression.append("E");
                break;
        }

        lastInputIsOperator = false;
        updateDisplay();
    }

    private void appendTrigFunction(String function) {
        if (isRadiansMode) {
            inputExpression.append(function).append("(");
        } else {
            inputExpression.append(function).append("(deg(");
        }
    }

    private void appendInverseTrigFunction(String function) {
        if (isRadiansMode) {
            inputExpression.append(function).append("(");
        } else {
            inputExpression.append(function).append("(");
            // For inverse functions, we'll convert the result to degrees
        }
    }

    // Memory Functions
    private void memoryClear() {
        performHapticFeedback(VibrationEffect.EFFECT_CLICK);
        memoryValue = 0;
        isMemoryStored = false;
        updateMemoryButtonStates();
    }

    private void memoryAdd() {
        performHapticFeedback(VibrationEffect.EFFECT_CLICK);
        try {
            double currentValue = Double.parseDouble(inputExpression.toString());
            memoryValue += currentValue;
            isMemoryStored = true;
            showMemoryToast("Added to memory");
        } catch (NumberFormatException e) {
            showMemoryToast("Invalid number");
        }
        updateMemoryButtonStates();
    }

    private void memorySubtract() {
        performHapticFeedback(VibrationEffect.EFFECT_CLICK);
        try {
            double currentValue = Double.parseDouble(inputExpression.toString());
            memoryValue -= currentValue;
            isMemoryStored = true;
            showMemoryToast("Subtracted from memory");
        } catch (NumberFormatException e) {
            showMemoryToast("Invalid number");
        }
        updateMemoryButtonStates();
    }

    private void memoryRecall() {
        performHapticFeedback(VibrationEffect.EFFECT_CLICK);
        if (isMemoryStored) {
            inputExpression.setLength(0);
            inputExpression.append(memoryValue);
            updateDisplay();
        } else {
            showMemoryToast("Memory empty");
        }
    }

    private void updateMemoryButtonStates() {
        Button mcButton = findViewById(R.id.button_mc);
        Button mrButton = findViewById(R.id.button_mr);

        if (isMemoryStored) {
            mcButton.setAlpha(1f);
            mrButton.setAlpha(1f);
            mcButton.setTextColor(Color.WHITE);
            mrButton.setTextColor(Color.WHITE);
        } else {
            mcButton.setAlpha(0.5f);
            mrButton.setAlpha(0.5f);
            mcButton.setTextColor(Color.GRAY);
            mrButton.setTextColor(Color.GRAY);
        }
    }

    private void showMemoryToast(String message) {
        // You can replace this with a Toast or Snackbar
        display.setHint(message);
        display.postDelayed(() -> display.setHint(null), 2000);
    }

    // Second Function Mode
    private void toggleSecondFunctionMode() {
        isSecondFunctionMode = !isSecondFunctionMode;
        performHapticFeedback(VibrationEffect.EFFECT_HEAVY_CLICK);
        updateSecondFunctionButtons();
        animateSecondFunctionMode();
    }

    private void updateSecondFunctionButtons() {
        for (Map.Entry<Integer, String> entry : secondFunctionMap.entrySet()) {
            Button button = findViewById(entry.getKey());
            if (button != null) {
                if (isSecondFunctionMode) {
                    button.setText(entry.getValue());
                    button.setBackgroundResource(R.drawable.circle_button_light);
                } else {
                    // Restore original text - you might want to store these in another map
                    button.setText(getOriginalButtonText(entry.getKey()));
                    button.setBackgroundResource(R.drawable.circle_button_dark);
                }
            }
        }
    }

    private String getOriginalButtonText(int id) {
        if (id == R.id.button_sin) return "sin";
        if (id == R.id.button_cos) return "cos";
        if (id == R.id.button_tan) return "tan";
        if (id == R.id.button_sinh) return "sinh";
        if (id == R.id.button_cosh) return "cosh";
        if (id == R.id.button_tanh) return "tanh";
        if (id == R.id.button_ln) return "ln";
        if (id == R.id.button_log) return "log";
        if (id == R.id.button_sqrt) return "√x";
        if (id == R.id.button_cbrt) return "∛x";
        if (id == R.id.button_fact) return "x!";
        if (id == R.id.button_ex) return "eˣ";
        if (id == R.id.button_10x) return "10ˣ";
        return "";
    }

    private void animateSecondFunctionMode() {
        Button secondButton = findViewById(R.id.button_2nd);
        if (isSecondFunctionMode) {
            secondButton.setBackgroundResource(R.drawable.circle_button_orange);
            secondButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        } else {
            secondButton.setBackgroundResource(R.drawable.circle_button_dark);
            secondButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
        }
    }

    private void toggleAngleMode() {
        isRadiansMode = !isRadiansMode;
        Button radButton = findViewById(R.id.button_rad);
        radButton.setText(isRadiansMode ? "RAD" : "DEG");
        radButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
        performHapticFeedback(VibrationEffect.EFFECT_TICK);
    }

    private void clearAll() {
        inputExpression.setLength(0);
        display.setText("0");
        lastInputIsOperator = false;
        display.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
    }

    private void toggleSign() {
        if (inputExpression.length() > 0) {
            if (inputExpression.charAt(0) == '-') {
                inputExpression.deleteCharAt(0);
            } else {
                inputExpression.insert(0, '-');
            }
            updateDisplay();
        }
    }

    private void calculateResult() {
        try {
            String expressionStr = inputExpression.toString()
                    .replace("×", "*")
                    .replace("÷", "/")
                    .replace("π", "pi")
                    .replace("−", "-");

            // Handle degree conversion for inverse trig functions
            if (!isRadiansMode) {
                if (expressionStr.contains("arcsin(") || expressionStr.contains("arccos(") || expressionStr.contains("arctan(")) {
                    expressionStr = expressionStr.replace("arcsin(", "arcsin(")
                            .replace("arccos(", "arccos(")
                            .replace("arctan(", "arctan(");
                }
            }

            Expression expression = new Expression(expressionStr);
            double result = expression.calculate();

            // Convert from radians to degrees if needed for inverse trig functions
            if (!isRadiansMode && (expressionStr.contains("arcsin(") || expressionStr.contains("arccos(") || expressionStr.contains("arctan("))) {
                result = Math.toDegrees(result);
            }

            if (Double.isNaN(result)) {
                display.setText("Error");
                display.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
            } else {
                String resultStr;
                if (result == (long) result) {
                    resultStr = String.format("%d", (long) result);
                } else {
                    // Format to show up to 10 decimal places, removing trailing zeros
                    resultStr = String.format("%.10f", result)
                            .replaceAll("0*$", "")
                            .replaceAll("\\.$", "");
                }

                inputExpression = new StringBuilder(resultStr);
                display.setText(resultStr);
                display.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            }
        } catch (Exception e) {
            display.setText("Error");
            display.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
        }
    }

    private void updateDisplay() {
        String displayText = inputExpression.toString()
                .replace("*", "×")
                .replace("/", "÷")
                .replace("pi", "π");

        display.setText(displayText.isEmpty() ? "0" : displayText);
    }
}