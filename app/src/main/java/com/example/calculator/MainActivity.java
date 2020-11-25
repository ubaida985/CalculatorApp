package com.example.calculator;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    Button b0, bdot, bplus, bequals, buttonac, b1, b2, b3, bminus, b4, b5, b6, bmultiply, b7, b8, b9, bdivide, bpie, be, bopenbracket, bclosebracket, blog, bsin, bcos, btan, bsqrt, bpower;
    TextView inputTextView, outputTextView;
    ImageButton bclear, discountButton;
    int sum = 0;
    double pi = Math.PI;
    boolean dotPresent = false;

    public static String negative(String receivedExpression) {

        String oldExpression = receivedExpression.replaceAll("√", "u");

        for (int i = 1; i < oldExpression.length() - 1; i++) {
            String st = "";
            int flag = 0;//but of no country
            char currentCharacter = oldExpression.charAt(i);
            if ("u(sct".contains(Character.toString(currentCharacter)) && "0123456789)".contains(Character.toString(oldExpression.charAt(i - 1)))) {
                st = oldExpression.substring(0, i) + "*" + oldExpression.substring(i);
                flag = 1;
            }
            if (flag == 1)
                oldExpression = st;

            if (currentCharacter == ')' && "u.0123456789".contains(Character.toString(oldExpression.charAt(i + 1)))) {
                st = oldExpression.substring(0, i + 1) + "*" + oldExpression.substring(i + 1);
                flag = 2;
            }
            if (flag == 2)
                oldExpression = st;

            if (currentCharacter == '.' && "(u)+-*/^".contains(Character.toString(oldExpression.charAt(i - 1)))) {
                st = oldExpression.substring(0, i) + "0" + oldExpression.substring(i);
                flag = 3;
            }
            if (flag == 3)
                oldExpression = st;
        }

        for (int i = 0; i < oldExpression.length() - 1; i++) {
            String st = "";
            int flag = 0;
            char ch = oldExpression.charAt(i);
            if (ch == '-' && (i == 0 || "u+-*/^(".contains(Character.toString(oldExpression.charAt(i - 1))))//
                    && (Character.isDigit(oldExpression.charAt(i + 1)) || "u(sctl".contains(Character.toString(oldExpression.charAt(i + 1))))) {
                st = oldExpression.substring(0, i) + "n" + oldExpression.substring(i + 1);
                flag = 1;
            }
            if (flag == 1)
                oldExpression = st;

            if ("sctl".contains(Character.toString(ch)) && (i == 0 || "+-u*/^(n".contains(Character.toString(oldExpression.charAt(i - 1))))//
                    && oldExpression.charAt(i + 3) == '(') {
                st = oldExpression.substring(0, i + 1) + oldExpression.substring(i + 3);
                flag = 2;
            }
            if (flag == 2)
                oldExpression = st;
        }
        return oldExpression;
    }

    public double evaluate(final String oldString) {
        String newString = negative(oldString);
        Stack<Double> operands = new Stack<>();
        Stack<Character> operator = new Stack<>();

        for (int i = 0; i < newString.length(); i++) {

            int count = 0;
            char chr = newString.charAt(i);
            String s = "";
            int j;

            // if the first number is negative, we need to push n in operator stack
            /*if (i == 0 && ("nsctl".contains(Character.toString(chr)))) {
                operator.push(chr);
                i++;
            }// has to be written before j loop

            chr = newString.charAt(i);//if upper if executes for trigo functions, chr must change wrt i

            //this loop will store the double value in variable s
                /*count is used for decreasing i by j-1 as after the end of j loop, i shouldn't point
                at the middle of the double value stored in s and should move to the upcoming operator
                or number after its increment*/
            for (j = i; j < newString.length() && !("(u)+-*/^nstcl".contains(Character.toString(newString.charAt(j)))); j++) {
                s = s + newString.charAt(j);
                count = 1;
            }
            if (count == 1)
                i = j - 1;

            if (("nsctl(".contains(Character.toString(chr)))) {
                operator.push(chr);
            } else if (chr == ')') {
                while (operator.peek() != '(') {
                    char c = operator.pop();
                    double a, b, ans;
                    // when special operation, only pop one value from operands and operate with it
                    if (c == 'n' || c == 's' || c == 'c' || c == 't' || c == 'l'|| c == 'u') {
                        a = operands.pop();
                        ans = special(a, c);
                    } else {
                        a = operands.pop();
                        b = operands.pop();
                        ans = operate(b, a, c);
                    }
                    operands.push(ans);
                }
                operator.pop();
            }
            //if the value in s is parsable to double value, it will push the parsed s in stack
            else if (s.matches("-?\\d+(\\.\\d+)?")) {
                operands.push(Double.parseDouble(s));
            } else if ("u+-*/^nsct".contains(Character.toString(chr))) {
                while (operator.size() > 0 && operator.peek() != '(' && precede(chr) <= precede(operator.peek())) {
                    char c = operator.pop();
                    double a, b, ans;
                    // when special operation, only pop one value from operands and operate with it
                    if (c == 'n' || c == 's' || c == 'c' || c == 't' || c == 'l'|| c == 'u') {
                        a = operands.pop();
                        ans = special(a, c);
                    } else {
                        a = operands.pop();
                        b = operands.pop();
                        ans = operate(b, a, c);
                    }
                    operands.push(ans);
                }//end of while
                operator.push(chr);
            }//end of else if
        }

        while (operator.size() != 0) {

            char c = operator.pop();
            double a, b, ans;
            // when special operation, only pop one value from operands and operate with it
            if (c == 'n' || c == 's' || c == 'c' || c == 't' || c == 'l'|| c == 'u') {
                a = operands.pop();
                ans = special(a, c);
            } else {
                a = operands.pop();
                b = operands.pop();
                ans = operate(b, a, c);
            }
            operands.push(ans);
        }
        return operands.peek();

    }

    public static int precede(char ch) {
        if (ch == '+' || ch == '-')
            return 1;
        else if (ch == '*' || ch == '/')
            return 2;
        else if (ch == '^')
            return 3;
        else if (ch == 'n')
            return 4;// for 'n'
        else if (ch == 'u')
            return 5;
        else
            return 6;
    }

    public static double operate(double a, double b, char ch) {
        if (ch == '+')
            return b + a;
        else if (ch == '-')
            return a - b;
        else if (ch == '*')
            return a * b;
        else if (ch == '/')
            return a / b;
        else
            return (int) (Math.pow(a, b));
    }

    //for special operations
    public double special(double a, char ch) {
        if (ch == 'n')
            return -a;
        if (ch == 'l')
            return ( Math.log(a)/2.302585092994046 );
        if (ch == 'u')
            return Math.sqrt(a);
        a = Math.toRadians(a);
        if (ch == 's')
            return Math.sin(a);
        else if (ch == 'c'){
            if((Math.toDegrees(a)) == 90)
                return 0;
            else
                return Math.cos(a);
        }
        else{
            if((Math.toDegrees(a)) == 90){
                inputTextView.setText("Undefined");
                return 0;
            }
            return Math.tan(a);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b0 = findViewById(R.id.b0);
        bdot = findViewById(R.id.bdot);
        bplus = findViewById(R.id.bplus);
        bequals = findViewById(R.id.bequals);
        b1 = findViewById(R.id.b1);
        b2 = findViewById(R.id.b2);
        b3 = findViewById(R.id.b3);
        bminus = findViewById(R.id.bminus);
        b4 = findViewById(R.id.b4);
        b5 = findViewById(R.id.b5);
        b6 = findViewById(R.id.b6);
        bmultiply = findViewById(R.id.bmultiply);
        b7 = findViewById(R.id.b7);
        b8 = findViewById(R.id.b8);
        b9 = findViewById(R.id.b9);
        bdivide = findViewById(R.id.bdivide);
        bclear = findViewById(R.id.bclear);
        bpie = findViewById(R.id.bpie);
        buttonac = findViewById(R.id.buttonac);
        be = findViewById(R.id.be);
        bopenbracket = findViewById(R.id.bopenbracket);
        bclosebracket = findViewById(R.id.bclosebracket);
        blog = findViewById(R.id.blog);
        bsin = findViewById(R.id.bsin);
        bcos = findViewById(R.id.bcos);
        btan = findViewById(R.id.btan);
        bsqrt = findViewById(R.id.bsqrt);
        bpower = findViewById(R.id.bpower);
        discountButton = findViewById(R.id.discountButton);

        inputTextView = findViewById(R.id.inputTextView);
        outputTextView = findViewById(R.id.outputTextView);

        discountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DiscountCalculator.class);
                        startActivity(intent);
            }
        });

        b0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = inputTextView.getText().toString();
                if (!value.equals("")) {
                    value += "0";
                    inputTextView.setText(value);
                }
            }
        });

        bdot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dotPresent == false) {
                    String value = inputTextView.getText().toString();
                    inputTextView.setText(value + bdot.getText().toString());
                    dotPresent = true;
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = inputTextView.getText().toString();
                inputTextView.setText((value + b1.getText().toString()));
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = inputTextView.getText().toString();
                inputTextView.setText((value + b2.getText().toString()));
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = inputTextView.getText().toString();
                inputTextView.setText(value + b3.getText().toString());
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = inputTextView.getText().toString();
                inputTextView.setText(value + b4.getText().toString());
            }
        });

        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = inputTextView.getText().toString();
                inputTextView.setText(value + b5.getText().toString());
            }
        });

        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = inputTextView.getText().toString();
                inputTextView.setText(value + b6.getText().toString());
            }
        });

        b7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = inputTextView.getText().toString();
                inputTextView.setText(value + b7.getText().toString());
            }
        });

        b8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = inputTextView.getText().toString();
                inputTextView.setText(value + b8.getText().toString());
            }
        });

        b9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = inputTextView.getText().toString();
                inputTextView.setText(value + b9.getText().toString());
            }
        });

        bplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = inputTextView.getText().toString();
                if (!value.equals("")) {
                    inputTextView.setText(value + bplus.getText().toString());
                }
            }
        });

        bdivide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = inputTextView.getText().toString();
                if (!value.equals("")) {
                    inputTextView.setText(value + bdivide.getText().toString());
                }
            }
        });

        bminus.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = inputTextView.getText().toString();
                if (!value.equals("")) {
                    inputTextView.setText(value + bminus.getText().toString());
                }
            }
        }));


        bmultiply.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = inputTextView.getText().toString();
                if (!value.equals("")) {
                    inputTextView.setText(value + bmultiply.getText().toString());
                }
            }
        }));

        bsqrt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = inputTextView.getText().toString();
                inputTextView.setText(value + bsqrt.getText().toString());
            }
        });


        bequals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = inputTextView.getText().toString();
                if (!value.equals("")) {
                    String replacedString = value.replace('÷', '/').replace('×', '*');
                    double result = evaluate(replacedString);
                    String r = String.valueOf(result);
                    if( !inputTextView.getText().toString().equals("Undefined") ){
                        inputTextView.setText(r);
                    }
                    outputTextView.setText(value);
                }
            }
        });

        bclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = inputTextView.getText().toString();
                if (!value.equals("")) {
                    value = value.substring(0, (value.length() - 1));
                    inputTextView.setText(value);
                }
            }
        });

        buttonac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputTextView.setText("");
                outputTextView.setText("");
                dotPresent = false;
            }
        });

        bopenbracket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputTextView.setText(inputTextView.getText().toString() + "(");
            }
        });

        bclosebracket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputTextView.setText(inputTextView.getText().toString() + ")");
            }
        });

        bpie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputTextView.setText(inputTextView.getText().toString() + "3.14");
            }
        });

        bsin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputTextView.setText(inputTextView.getText().toString() + "sin(");
            }
        });

        bcos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputTextView.setText(inputTextView.getText().toString() + "cos(");
            }
        });

        btan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputTextView.setText(inputTextView.getText().toString() + "tan(");
            }
        });

        be.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputTextView.setText(inputTextView.getText().toString() + "2.718281");
            }
        });

        bpower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = inputTextView.getText().toString();
                if ((!value.equals("")) && (value.charAt((value.length() - 1)) != '^')) {
                    inputTextView.setText(value + "^");
                }
            }
        });

        blog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = inputTextView.getText().toString();
                inputTextView.setText(value + "log(");
            }
        });

    }
}
