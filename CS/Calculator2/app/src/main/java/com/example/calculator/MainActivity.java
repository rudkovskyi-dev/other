package com.example.calculator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Map<String, Integer> dict = new HashMap<String, Integer>();
    private Stack<String> pfin = new Stack<String>();
    public void RPN_split_step(){
        dict.put("^", 3);
        dict.put("×", 2);
        dict.put("÷", 2);
        dict.put("+", 1);
        dict.put("-", 1);
        dict.put("=", -1);
        while (!pfin.empty()){
            pfin.pop();
        }
        TextView t = (TextView) findViewById(R.id.textAnsw);
        String temp = t.getText().toString();

        int minus = temp.indexOf("-");
        int count_s = 0;
        int count_e = 0;
        Set<String> d = dict.keySet();//.split("[\\[\\], ]", 1);

        for (char c: temp.toCharArray()){
            count_e++;
            for (String s: d){
                if(c==s.charAt(0)){
                    if (count_s < count_e && minus!=0){
                        pfin.push(temp.substring(count_s, count_e-1));
                        Log.i("test1_o_l_r", temp.substring(count_s, count_e-1));
                    }
                    if(minus==0)
                        minus=-1;
                    pfin.push(s);
                    Log.i("test1_op", s);
                    count_s=count_e;
                }
            }
            if (count_e==temp.length() && count_s<count_e){
                pfin.push(temp.substring(count_s, count_e));
                Log.i("test1_o_last_l_r", temp.substring(count_s, count_e));
            }
        }
        boolean check = false;
        if (!pfin.empty())
        {
            String p = (String) pfin.pop();
            for (String s: d){
                if(p=="-" || p=="+" || p=="÷" || p=="×" || p=="^"){
                    check = true;
                }
            }
            if (!check){
                pfin.push(p);
            }
        }
        pfin.push("=");
        int counter = 0;
        boolean check2 = false;
        Stack<String> reverse = new Stack<String>();
        while (!pfin.empty()){
            String tmp = pfin.pop();
            if (tmp=="-"){
                check2 = true;
                counter=-1;
            }
            else {
                check2 = false;
                for (String sx : d){
                    if (sx == tmp) {
                        check2 = true;
                    }
                }
                if (!check2){
                    counter = -1;
                }
            }
            counter++;
            reverse.push(tmp);
        }
        if (check2) {
            if (!reverse.empty()){
                for (int i = 0; i<counter; i++){
                    pfin.push(reverse.pop());
                }
                if (!reverse.empty()){
                    String sign = reverse.pop();
                    String number = reverse.pop();
                    pfin.push(sign+number);
                }
            }
        }
        while (!pfin.empty()){
            reverse.push(pfin.pop());
        }
        pfin = reverse;
    }

    private Toast toast;
    public void showAToast (String st){
        try{ toast.getView().isShown();
            toast.setText(st);
        } catch (Exception e) {
            toast = Toast.makeText(getApplicationContext(), st, Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    public static String round(BigDecimal bd, int places) {
        if (places < 0) throw new IllegalArgumentException();
        bd = bd.setScale(places, RoundingMode.HALF_EVEN);
        return bd.toPlainString();
    }

    public String makeSomeMath(String ls, String op, String rs) {
        if (ls.equals(""))
            ls = "0";
        if (ls!="" && rs!="") {
            BigDecimal dec1 = new BigDecimal(ls);
            BigDecimal dec2 = new BigDecimal(rs);

            switch (op) {
                case "+":
                    return round(dec1.add(dec2), 20);
                case "-":
                    return round(dec1.subtract(dec2), 20);
                case "×":
                    return round(dec1.multiply(dec2), 20);
                case "÷":
                    try {
                        return round(dec1.divide(dec2, 20, RoundingMode.CEILING.HALF_EVEN), 20);
                    } catch (ArithmeticException ex) {
                        showAToast("DIVISION BY 0!");
                        return "0";
                    }
            }
        }
        return "0";
    }

    public String parseR(String ls, int m_p){
        if (!pfin.empty()){
            String la = pfin.peek();
            while (dict.containsKey(la) && dict.get(la)>=m_p) {
                String op = pfin.pop();
                String rs = pfin.pop();
                if (!pfin.empty()){
                    la = pfin.peek();
                    while (la!= null && dict.containsKey(la) && dict.get(la) > dict.get(op)) {
                        rs = parseR(rs, dict.get(la));
                        if (!pfin.empty()){
                            la = pfin.peek();
                        } else {
                            la = null;
                        }
                    }
                }
                ls = makeSomeMath(ls, op, rs);
            }
        }
        return ls;
    }

    private boolean isIntegerValue(BigDecimal bd) {
        return bd.stripTrailingZeros().scale() <= 0;
    }

    public void parse(boolean inter){
        TextView t = (TextView) findViewById(R.id.textAnsw);
        TextView t2 = (TextView) findViewById(R.id.textIntermediate);
        String answer;
        if (!pfin.empty()) {
            answer = parseR(pfin.pop(), 0);
            boolean checkx = false;
            for (String s : dict.keySet()){
                if (s==answer){
                    checkx = true;
                }
            }
            if (!checkx && !answer.equals("")){
                BigDecimal l = new BigDecimal(answer);
            }
            if(answer.equals("="))
                answer="0";
            BigDecimal b = new BigDecimal(answer);
            BigInteger r = new BigInteger(String.valueOf(round(b, 0)));
            if (inter){
                DecimalFormat df = new DecimalFormat("#.####");
                if (isIntegerValue(b)) {
                    t2.setText("≈ " + r);
                } else {
                    BigDecimal o = new BigDecimal(round(b, 5));
                    String answ = df.format(o);
                    t2.setText("≈ " + answ);
                }
                TextViewCompat.setAutoSizeTextTypeWithDefaults(t2, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            }
            else{
                if (isIntegerValue(b)) {
                    t.setText("" + r);
                } else {
                    int counter = 0;
                    boolean check = false;
                    int counter2 = 0;
                    for (char c : round(b, 20).toCharArray()){
                        if (c=='0'){
                            counter++;
                        }
                        else {
                            counter = 0;
                        }
                        if (check)
                            counter2++;
                        if (c=='.'){
                            check = true;
                        }
                    }
                    if (counter != round(b, 20).toCharArray().length){
                        if (counter != counter2)
                            answer = round(b, 20).substring(0, round(b, 20).length()-counter);
                        else
                            answer = round(b, 20).substring(0, round(b, 20).length()-counter-1);
                    }
                    t.setText("" + answer);
                }
                final HorizontalScrollView h = (HorizontalScrollView) findViewById(R.id.hv);
                h.postDelayed(new Runnable() {
                    public void run() {
                        h.fullScroll(HorizontalScrollView.FOCUS_LEFT);
                    }
                }, 15);
            }
        }
        else
            t.setText("Stack Error");
    }

    public void refresh(){
        final HorizontalScrollView h = (HorizontalScrollView) findViewById(R.id.hv);
        h.postDelayed(new Runnable() {
            public void run() {
                h.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }, 15);
    }

    public void c_del(){
        TextView t = (TextView) findViewById(R.id.textAnsw);
        String temp = t.getText().toString();
        if (temp.length()!=0){
            temp = temp.substring(0,temp.length()-1);
            t.setText(temp);
        }
        refresh();
    }

    public void conc(String key){
        TextView t = (TextView) findViewById(R.id.textAnsw);
        String temp = t.getText().toString();
        String[] tempsplitted = temp.split("[-+÷×]", -1);
        if(tempsplitted[tempsplitted.length-1].length()!=0){
            if(tempsplitted[tempsplitted.length-1].charAt(0)=='0' && tempsplitted[tempsplitted.length-1].length()==1)
                c_del();
            temp = t.getText().toString().concat(key);
            t.setText(getString(R.string.temp,temp));
        }
        else
            t.setText(getString(R.string.temp,temp+key));
        refresh();
    }

    public void sign(String key){
        TextView t = (TextView) findViewById(R.id.textAnsw);
        String temp = t.getText().toString();
        String[] tempsplitted = temp.split("[-+÷×]",-1);
        boolean l = temp.length()!=0;
        boolean len = tempsplitted[tempsplitted.length-1].length()!=0;
        if (l) {
            boolean test2 = temp.charAt(temp.length()-1)!='-';
            boolean test3 = temp.charAt(temp.length()-1)!='÷';
            boolean test4 = temp.charAt(temp.length()-1)!='×';
            boolean test5 = temp.charAt(temp.length()-1)!='+';
            if(test2 && test3 && test4 && test5){
                t.setText(getString(R.string.temp,temp.concat(key+"0")));
                c_del();
            }
            else {
                if(temp.length()!=1){
                c_del();
                temp = t.getText().toString();
                t.setText(getString(R.string.temp, temp.concat(key)));
                }
            }
        }
        else {
            if (key == "-"){
                t.setText(getString(R.string.temp,temp.concat(key+"0")));
                c_del();
            }
        }
        refresh();
    }
    public void dot(){
        TextView t = (TextView) findViewById(R.id.textAnsw);
        String[] tempsplitted = t.getText().toString().split("[-+÷×]", -1);
        String temp = t.getText().toString();
        temp = t.getText().toString();
        if(!(tempsplitted[tempsplitted.length-1].contains("."))){
            if (tempsplitted[tempsplitted.length-1].length()>0){
                t.setText(getString(R.string.temp,temp.concat("."+"0")));
                c_del();
            }
            else {
                t.setText(getString(R.string.temp,temp.concat("0."+"0")));
                c_del();
            }
        }
        refresh();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.but0:
                conc("0");
                RPN_split_step();
                parse(true);
                break;

            case R.id.but1:
                conc("1");
                RPN_split_step();
                parse(true);
                break;

            case R.id.but2:
                conc("2");
                RPN_split_step();
                parse(true);
                break;

            case R.id.but3:
                conc("3");
                RPN_split_step();
                parse(true);
                break;

            case R.id.but4:
                conc("4");
                RPN_split_step();
                parse(true);
                break;

            case R.id.but5:
                conc("5");
                RPN_split_step();
                parse(true);
                break;

            case R.id.but6:
                conc("6");
                RPN_split_step();
                parse(true);
                break;

            case R.id.but7:
                conc("7");
                RPN_split_step();
                parse(true);
                break;

            case R.id.but8:
                conc("8");
                RPN_split_step();
                parse(true);
                break;

            case R.id.but9:
                conc("9");
                RPN_split_step();
                parse(true);
                break;

            case R.id.butC:
                c_del();
                RPN_split_step();
                parse(true);
                break;

            case R.id.butD:
                dot();
                RPN_split_step();
                parse(true);
                break;

            case R.id.butDiv:
                sign("÷");
                RPN_split_step();
                parse(true);
                break;

            case R.id.butMin:
                sign("-");
                RPN_split_step();
                parse(true);
                break;

            case R.id.butMul:
                sign("×");
                RPN_split_step();
                parse(true);
                break;

            case R.id.butPlu:
                sign("+");
                RPN_split_step();
                parse(true);
                break;
            case R.id.butEqu:
                RPN_split_step();
                parse(false);
                RPN_split_step();
                parse(true);
                try{
                    if ("DIVISION BY 0!".equals(((TextView)((LinearLayout)toast.getView()).getChildAt(0)).getText().toString())){
                        ((TextView)findViewById(R.id.textAnsw)).setText("0");
                        ((TextView)findViewById(R.id.textIntermediate)).setText("≈ 0");
                    }
                } catch (Exception e){}
                break;
            default:
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("textAnsw", String.valueOf(((TextView) findViewById(R.id.textAnsw)).getText()));
        outState.putString("textInter", String.valueOf(((TextView) findViewById(R.id.textIntermediate)).getText()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView answTV = (TextView) findViewById(R.id.textAnsw);
        final TextView intTV = (TextView) findViewById(R.id.textIntermediate);
        answTV.setClickable(true);
        intTV.setClickable(true);
        answTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Main Answer Copied", answTV.getText().toString());
                clipboard.setPrimaryClip(clip);
                showAToast("Main Answer Copied");
            }
        });
        intTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intTV.getText().toString().length()>2){
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Main Answer Copied", intTV.getText().toString().substring(2));
                clipboard.setPrimaryClip(clip);
                    showAToast("Intermediate Answer Copied");
                }
            }
        });

        if (savedInstanceState!=null){
            ((TextView) findViewById(R.id.textAnsw)).setText(String.valueOf(savedInstanceState.getString("textAnsw")));
            ((TextView) findViewById(R.id.textIntermediate)).setText(String.valueOf(savedInstanceState.getString("textInter")));
        }

        Button removeall = (Button) findViewById(R.id.butC);
        removeall.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                TextView t = (TextView) findViewById(R.id.textAnsw);
                t.setText("");
                refresh();
                RPN_split_step();
                parse(true);
                return false;
            }
        });

        LinearLayout l = (LinearLayout) findViewById(R.id.l);
        l.setClickable(true);
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("tlsetOnClickListener", Integer.toString(v.getId()));
                Toast toast = Toast.makeText(getApplicationContext(), Integer.toString(v.getId()), Toast.LENGTH_LONG);
                toast.show();
            }
        });

        int c = l.getChildCount();
        for(int i = 0; i < c; i++){
            View v = l.getChildAt(i);
            if(v instanceof TableLayout){
                TableLayout t = (TableLayout) v;
                int c2 = t.getChildCount();
                for(int k = 0; k < c2; k++){
                    View z = t.getChildAt(k);
                    if(z instanceof TableRow){
                        TableRow r = (TableRow) z;
                        int rc = r.getChildCount();
                        for(int j = 0; j < rc; j++){
                            View y = r.getChildAt(j);
                            if (y instanceof Button){
                                Button b = (Button)y;
                                b.setOnClickListener(this);
                            }
                        }
                    }
                }
            }
        }
    }
}