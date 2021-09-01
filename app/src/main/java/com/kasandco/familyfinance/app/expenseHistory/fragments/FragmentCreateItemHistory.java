package com.kasandco.familyfinance.app.expenseHistory.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.expenseHistory.presenters.CreateHistoryItemContract;
import com.kasandco.familyfinance.app.expenseHistory.presenters.CreateHistoryItemPresenter;
import com.kasandco.familyfinance.utils.KeyboardUtil;
import com.kasandco.familyfinance.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class FragmentCreateItemHistory extends Fragment implements CreateHistoryItemContract {
    private int type;
    private Button btnDatePiker;
    private Button btnCreate;
    private ImageButton btnCalculator;
    private EditText amount;
    private EditText comment;
    private DatePicker datePicker;
    private GregorianCalendar selectedDate;
    private FrameLayout calculator;
    private long categoryId;
    private CreateHistoryItemPresenter presenter;
    private ClickListener callback;
    private View.OnClickListener createListener  = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            presenter.createNewItem(type, categoryId, amount.getText().toString(), comment.getText().toString(), selectedDate);
        }
    };
    private View.OnClickListener closeListener = view -> {
        close();
    };

    public FragmentCreateItemHistory(int type, CreateHistoryItemPresenter presenter) {
        this.type = type;
        this.presenter = presenter;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        presenter.viewReady(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_data_finance, container, false);
        view.setOnClickListener(null);
        btnDatePiker = view.findViewById(R.id.materialButton);
        btnCalculator = view.findViewById(R.id.imageButton);
        amount = view.findViewById(R.id.editText);

        view.getRootView().setOnClickListener(closeListener);

        amount.setFocusable(true);
        amount.requestFocus();
        KeyboardUtil.showKeyboard(getActivity());

        comment = view.findViewById(R.id.editText2);
        btnCreate = view.findViewById(R.id.btnCreate);
        datePicker = view.findViewById(R.id.date_picker_frame);
        calculator = view.findViewById(R.id.calculator);
        GregorianCalendar dateNow = new GregorianCalendar();
        selectedDate = new GregorianCalendar();
        selectedDate.setTime(new Date());
        setDateToBtnDatePicker(selectedDate.getTime());
        dateNow.setTime(new Date());

        btnDatePiker.setOnClickListener(btnListener);
        btnCalculator.setOnClickListener(btnListener);

        datePicker.init(dateNow.get(Calendar.YEAR), dateNow.get(Calendar.MONTH), dateNow.get(Calendar.DATE), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
                GregorianCalendar calendar = new GregorianCalendar(year, month, day);
                setDateToBtnDatePicker(calendar.getTime());
                selectedDate.setTime(calendar.getTime());
                datePicker.setVisibility(View.GONE);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnCreate.setOnClickListener(createListener);
    }

    @SuppressLint("SimpleDateFormat")
    private void setDateToBtnDatePicker(Date time) {
        btnDatePiker.setText(new SimpleDateFormat("dd.MM.yyyy").format(time));
    }

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.materialButton:
                    datePicker.setVisibility(View.VISIBLE);
                    break;
                case R.id.imageButton:
                    calculator.setVisibility(View.VISIBLE);
                    getView().setOnClickListener((View view1)->{
                        calculator.setVisibility(View.GONE);
                    });
                    new Calculator(calculator);
                    break;
            }
        }
    };

    public void setAmount(String amount){
        this.amount.setText(amount);
        calculator.setVisibility(View.GONE);
        requireView().setOnClickListener(closeListener);
    }

    public void setCategory(long categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public void close() {
        KeyboardUtil.hideKeyboard(getActivity());
        amount.setText("");
        comment.setText("");
        callback.closeCreateItemHistory();
    }

    public void setCallback(ClickListener callback) {
        this.callback = callback;
    }

    public interface ClickListener{
        void closeCreateItemHistory();
    }

    class Calculator {
        private Button btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btnDel, btnRes, btnOk, btnDiv, btnMulti, btnAdd, btnSub, btnPercent, btnPoint;
        private EditText calcLine;

        private double x, y;
        private String operation;
        private boolean isResult;

        public Calculator(View view) {
            btn0 = view.findViewById(R.id.button0);
            btn1 = view.findViewById(R.id.button1);
            btn2 = view.findViewById(R.id.button2);
            btn3 = view.findViewById(R.id.button3);
            btn4 = view.findViewById(R.id.button4);
            btn5 = view.findViewById(R.id.button5);
            btn6 = view.findViewById(R.id.button6);
            btn7 = view.findViewById(R.id.button7);
            btn8 = view.findViewById(R.id.button8);
            btn9 = view.findViewById(R.id.button9);
            btnDel = view.findViewById(R.id.buttonDel);
            btnRes = view.findViewById(R.id.buttoneql);
            btnOk = view.findViewById(R.id.buttonOk);
            btnMulti = view.findViewById(R.id.buttonmul);
            btnAdd = view.findViewById(R.id.buttonadd);
            btnDiv = view.findViewById(R.id.buttondiv);
            btnSub = view.findViewById(R.id.buttonsub);
            btnPercent = view.findViewById(R.id.Remainder);
            btnPoint = view.findViewById(R.id.buttonDot);
            calcLine = view.findViewById(R.id.display);

            btn0.setOnClickListener(listener);
            btn1.setOnClickListener(listener);
            btn2.setOnClickListener(listener);
            btn3.setOnClickListener(listener);
            btn4.setOnClickListener(listener);
            btn5.setOnClickListener(listener);
            btn6.setOnClickListener(listener);
            btn7.setOnClickListener(listener);
            btn8.setOnClickListener(listener);
            btn9.setOnClickListener(listener);
            btnDel.setOnClickListener(listener);
            btnRes.setOnClickListener(listener);
            btnOk.setOnClickListener(listener);
            btnMulti.setOnClickListener(listener);
            btnAdd.setOnClickListener(listener);
            btnDiv.setOnClickListener(listener);
            btnSub.setOnClickListener(listener);
            btnPercent.setOnClickListener(listener);
            btnPoint.setOnClickListener(listener);
        }

        private View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.button0:
                        result("0");
                        break;
                    case R.id.button1:
                        result("1");
                        break;
                    case R.id.button2:
                        result("2");
                        break;
                    case R.id.button3:
                        result("3");
                        break;
                    case R.id.button4:
                        result("4");
                        break;
                    case R.id.button5:
                        result("5");
                        break;
                    case R.id.button6:
                        result("6");
                        break;
                    case R.id.button7:
                        result("7");
                        break;
                    case R.id.button8:
                        result("8");
                        break;
                    case R.id.button9:
                        result("9");
                        break;
                    case R.id.buttondiv:
                        result("/");
                        break;
                    case R.id.buttonDel:
                        clear();
                        break;
                    case R.id.buttonDot:
                        result(".");
                        break;
                    case R.id.buttonadd:
                        result("+");
                        break;
                    case R.id.buttoneql:
                        result("=");
                        break;
                    case R.id.buttonsub:
                        result("-");
                        break;
                    case R.id.buttonOk:
                        calculate();
                        setAmount(calcLine.getText().toString().replace(",","."));
                        break;
                    case R.id.Remainder:
                        result("%");
                        break;
                    case R.id.buttonmul:
                        result("*");
                        break;
                }
            }
        };


        @SuppressLint("SetTextI18n")
        private void result(String sign) {
            if(sign.equals("=") && operation!=null){
                calculate();
            }
            switch (sign) {
                case "+":
                case "-":
                case "*":
                case "/":
                    if (x == 0) {
                        x = Double.parseDouble(calcLine.getText().toString().replace(",","."));
                    }
                    isResult=true;
                    if(operation!=null){
                        calculate();
                    }
                    operation=sign;
                    break;
                default:
                    if(!sign.equals("=")) {
                        if (calcLine.getText().toString().equals("0")) {
                            calcLine.setText(sign);
                        } else {
                            if (x != 0 && isResult) {
                                calcLine.setText(sign);
                                isResult = false;
                            } else {
                                calcLine.setText(calcLine.getText().toString() + sign);
                            }
                        }
                    }
            }
        }

        private void calculate() {
            y=Double.parseDouble(calcLine.getText().toString().replace(",","."));
            if(operation!=null)
            switch (operation) {
                case "+":
                    x = com.kasandco.familyfinance.service.Calculator.addPl(x, y);
                    break;
                case "-":
                    x = com.kasandco.familyfinance.service.Calculator.subtract(x, y);
                    break;
                case "*":
                    x = com.kasandco.familyfinance.service.Calculator.multiple(x, y);
                    break;
                case "/":
                    if(y!=0)
                    x = com.kasandco.familyfinance.service.Calculator.divide(x, y);
                    else
                        ToastUtils.showToast(getString(R.string.error_divide_0), getContext());
                    break;

            }
            if(y!=0) {
                calcLine.setText(String.format("%.2f", x));
                operation = null;
                y = 0;
            }
        }

        private void clear() {
            calcLine.setText("0");
            x=0;
            y=0;
        }
    }
}
