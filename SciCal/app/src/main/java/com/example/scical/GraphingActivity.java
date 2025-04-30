package com.example.scical;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import androidx.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.License;

import java.util.ArrayList;
import java.util.List;

public class GraphingActivity extends AppCompatActivity {

    private GraphView graphView;
    private EditText functionInput;
    private TextView coordinatesText;
    private List<LineGraphSeries<DataPoint>> functionSeries = new ArrayList<>();
    private PointsGraphSeries<DataPoint> pointSeries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphing);

        // Initialize views
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        graphView = findViewById(R.id.graph);
        functionInput = findViewById(R.id.function_input);
        coordinatesText = findViewById(R.id.coordinates_text);

        // Set up license for mxParser
        License.iConfirmNonCommercialUse("SciCal");

        // Configure graph appearance
        configureGraphView();

        // Set up function input listener
        functionInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                plotFunction(s.toString());
            }
        });

        // Set up touch listener for coordinates
        setupTouchListener();
    }

    private void configureGraphView() {
        // Styling the graph
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(-10);
        graphView.getViewport().setMaxX(10);
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(-10);
        graphView.getViewport().setMaxY(10);
        graphView.getViewport().setScalable(true);
        graphView.getViewport().setScalableY(true);

        // Grid and label styling
        graphView.getGridLabelRenderer().setVerticalAxisTitle("y");
        graphView.getGridLabelRenderer().setHorizontalAxisTitle("x");
        graphView.getGridLabelRenderer().setGridColor(Color.LTGRAY);
        graphView.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        graphView.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
        graphView.getGridLabelRenderer().setLabelsSpace(10);
        graphView.getGridLabelRenderer().setTextSize(24);
        graphView.getGridLabelRenderer().setPadding(32);
    }

    private void plotFunction(String functionStr) {
        graphView.removeAllSeries();
        functionSeries.clear();

        if (functionStr.isEmpty()) {
            coordinatesText.setText("Enter a function to graph");
            return;
        }

        try {
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
            double x, y;
            int pointsPlotted = 0;

            for (int i = 0; i <= 500; i++) {
                x = -10 + i * 0.04; // From -10 to +10 in 500 steps
                Expression e = new Expression(functionStr.replace("x", "(" + x + ")"));
                y = e.calculate();

                if (!Double.isNaN(y) && !Double.isInfinite(y) && Math.abs(y) <= 1000) {
                    series.appendData(new DataPoint(x, y), false, 500);
                    pointsPlotted++;
                }
            }

            if (pointsPlotted == 0) {
                coordinatesText.setText("No valid points to plot");
                return;
            }

            // Style the series
            series.setColor(getRandomColor());
            series.setThickness(4);
            series.setDrawBackground(false);
            series.setDrawDataPoints(pointsPlotted < 100);
            series.setDataPointsRadius(4);

            graphView.addSeries(series);
            functionSeries.add(series);
            coordinatesText.setText("Graphing: " + functionStr);

        } catch (Exception e) {
            coordinatesText.setText("Error: " + e.getMessage());
        }
    }

    private void setupTouchListener() {
        // Alternative touch listener implementation
        graphView.setOnLongClickListener(v -> {
            // Simple coordinates display without exact graph conversion
            coordinatesText.setText("Long press on graph for coordinates");
            performHapticFeedback(VibrationEffect.EFFECT_TICK);
            return true;
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void showModeSelectionDialog(View view) {
        performHapticFeedback(VibrationEffect.EFFECT_CLICK);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.dialog_mode_selection);
        bottomSheetDialog.show();

        View dialogView = bottomSheetDialog.findViewById(android.R.id.content);
        if (dialogView != null) {
            dialogView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up));
        }

        TextView basicMode = bottomSheetDialog.findViewById(R.id.mode_basic);
        TextView sciMode = bottomSheetDialog.findViewById(R.id.mode_sci);
        TextView graphMode = bottomSheetDialog.findViewById(R.id.mode_graph);

        // Highlight current mode
        if (graphMode != null) {
            graphMode.setBackgroundResource(R.drawable.mode_selected_background);
            graphMode.setTextColor(ContextCompat.getColor(this, R.color.white));
        }

        assert basicMode != null;
        basicMode.setOnClickListener(v -> {
            performHapticFeedback(VibrationEffect.EFFECT_CLICK);
            startActivity(new Intent(this, MainActivity.class));
            finish();
            bottomSheetDialog.dismiss();
        });

        assert sciMode != null;
        sciMode.setOnClickListener(v -> {
            performHapticFeedback(VibrationEffect.EFFECT_CLICK);
            startActivity(new Intent(this, ScientificActivity.class));
            finish();
            bottomSheetDialog.dismiss();
        });

        assert graphMode != null;
        graphMode.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.setOnDismissListener(dialog -> {
            if (dialogView != null) {
                dialogView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_down));
            }
        });
    }

    private void performHapticFeedback(int effect) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    vibrator.vibrate(VibrationEffect.createPredefined(effect));
                } else {
                    vibrator.vibrate(50);
                }
            }
        }
    }

    private int getRandomColor() {
        int[] colors = {
                Color.BLUE, Color.GREEN, Color.MAGENTA,
                Color.CYAN, Color.YELLOW, Color.rgb(255, 165, 0),
                Color.rgb(148, 0, 211), Color.rgb(255, 192, 203)
        };
        return colors[(int) (Math.random() * colors.length)];
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}