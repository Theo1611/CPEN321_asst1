package com.example.assignment1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SudokuActivity extends AppCompatActivity {
    private boolean solved = false;
    private int[][] board = new int[9][9];
    private boolean[][] orgKnown = new boolean[9][9];
    private int[][] sol = new int[9][9];
    private int[][] gridIds = new int[9][9];
    private static final String TAG = "SudokuActivity";
    RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);
        queue = Volley.newRequestQueue(this);
        getPuzzle();
        TableLayout layout = findViewById(R.id.sudoku_layout);
        TableLayout.LayoutParams tblParams = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT
        );
        for (int i = 0; i < 9; i++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(tblParams);
            tableRow.setGravity(Gravity.CENTER);
            for (int j = 0; j < 9; j++) {
                TableRow.LayoutParams params = new TableRow.LayoutParams(
                        dpToPx(34),
                        TableRow.LayoutParams.MATCH_PARENT
                );
                int margRight = 0;
                int margBot = 0;
                if ((i+1) % 3 == 0 && i != 8) {
                    margBot = 6;
                }
                if ((j+1) % 3 == 0 && j != 8) {
                    margRight = 6;
                }
                params.setMargins(0, 0, margRight, margBot);
                TextView textView = new EditText(this);
                textView.setLayoutParams(params);
                int id = View.generateViewId();
                textView.setId(id);
                gridIds[i][j] = id;
                textView.setBackground(AppCompatResources.getDrawable(this, R.drawable.border));
                textView.setGravity(Gravity.CENTER);
                textView.setInputType(InputType.TYPE_CLASS_NUMBER);
                textView.setFilters(new InputFilter[] {new InputFilter.LengthFilter(1)});
                int finalJ = j;
                int finalI = i;
                textView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void afterTextChanged(Editable editable) {
                        int val = editable.toString().equals("") ? 0 : Integer.parseInt(editable.toString());
                        board[finalI][finalJ] = val;
                        checkBoard();
                    }
                });
                tableRow.addView(textView);
            }
            layout.addView(tableRow);
        }
        findViewById(R.id.solve_button).setOnClickListener(view -> solve());
        findViewById(R.id.new_puzzle_button).setOnClickListener(view -> resetSudoku());
    }

    private int dpToPx(float dpVal) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dpVal*scale + 0.5f);
    }

    private void getPuzzle() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://sudoku-api.vercel.app/api/dosuku";
        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.GET, url, null, response -> {
                    try {
                        jsonArrayToArray(board, response.getJSONObject("newboard").getJSONArray("grids").getJSONObject(0).getJSONArray("value"));
                        jsonArrayToArray(sol, response.getJSONObject("newboard").getJSONArray("grids").getJSONObject(0).getJSONArray("solution"));
                        System.out.println(Arrays.deepToString(sol));
                        updateDisplay();
                        updateSet();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }, error -> Log.d(TAG, "Error" + error));
        queue.add(request);
    }

    private void updateDisplay() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] != 0) {
                    EditText view = findViewById(gridIds[i][j]);
                    view.setEnabled(false);
                    view.setBackground(AppCompatResources.getDrawable(this, R.drawable.filled_border));
                    view.setText(String.valueOf(board[i][j]));
                }
            }
        }
    }
    private void updateSet() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] != 0) {
                    orgKnown[i][j] = true;
                }
            }
        }
    }

    private void checkBoard() {
        if (solved) {
            return;
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] != sol[i][j]) {
                        return;
                }
            }
        }
        solved = true;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!orgKnown[i][j]) {
                    EditText view = findViewById(gridIds[i][j]);
                    view.setEnabled(false);
                    view.setTextColor(Color.parseColor("#90EE90"));
                }
            }
        }
        Toast.makeText(SudokuActivity.this, "You solved the Sudoku!", Toast.LENGTH_LONG).show();
    }

    private void solve() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                EditText view = findViewById(gridIds[i][j]);
                view.setEnabled(false);
                view.setText(String.valueOf(sol[i][j]));
                board[i][j] = sol[i][j];
            }
        }
    }

    private void resetSudoku() {
        startActivity(new Intent(this, SudokuActivity.class));
        finish();
    }

    private void jsonArrayToArray(int[][] arr, JSONArray jsonArr) throws JSONException {
        for (int i = 0; i < 9; i++) {
            JSONArray tmp = jsonArr.getJSONArray(i);
            for (int j = 0; j < 9; j++) {
                arr[i][j] = tmp.getInt(j);
            }
        }
    }
}