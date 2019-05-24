package com.prave.tools.exercisetimer;

import android.content.Context;
import android.content.DialogInterface;
import android.opengl.ETC1;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class AddExerciseActivity extends AppCompatActivity{

    ImageButton submit;
    AutoCompleteTextView name;
    ListView lv;
    ImageButton add_e;
    EditText repeat_times;
    private class ExerciseItemAdapter extends BaseAdapter{
        private Context context;
        //用于保存输入的数据
        private int[][] inputData;

        public int[][] getInputData() {
            return inputData;
        }

        public ExerciseItemAdapter(Context context) {
            this.context = context;
            this.inputData=new int[1][2];
        }

        public ExerciseItemAdapter(Context context,int[][] inputData) {
            // 调用这个Constructor的目的是恢复之前的数据，nums应该要和inputData.length对的上，
            // 所以对inputData的更改不应放在这里
            this.context = context;
            this.inputData=inputData;
        }
        @Override
        public int getCount() {
            return this.inputData.length;
        }

        @Override
        public Object getItem(int position) {
            int[] res=new int[2];
            View v=this.getView(position,null,null);
            Spinner sp=(Spinner)(v.findViewById(R.id.add_exercise_item_exerciseName));
            res[0]=(int)sp.getSelectedItemId();
            EditText et=(EditText)(v.findViewById(R.id.add_exercise_item_timesOrseconds));
            String s=et.getText().toString();
            res[1]=s.equals("")?-1:Integer.valueOf(s);
            return res;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position_lv, View convertView, ViewGroup parent) {
            View view=View.inflate(context,R.layout.single_item_add_exercise,null);
            TextView no=view.findViewById(R.id.add_exercise_item_no);
            final TextView timeors=view.findViewById(R.id.add_exercise_item_unit);
            final Spinner spinner=view.findViewById(R.id.add_exercise_item_exerciseName);
            EditText et=view.findViewById(R.id.add_exercise_item_timesOrseconds);
            et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        inputData[position_lv][1] = s.toString().equals("") ? 0 :Integer.valueOf(s.toString());
                    }catch (Exception e){
                    }
                }
                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            no.setText(""+(position_lv+1));
            spinner.setAdapter(new ArrayAdapter<>(context,R.layout.support_simple_spinner_dropdown_item,SundryFunctions.exercises));
            if(SundryFunctions.exercises[spinner.getSelectedItemPosition()].unit==0)
                timeors.setText("秒");
            else
                timeors.setText("次");
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position_sp, long id) {
                    if(SundryFunctions.exercises[position_sp].unit==0)
                        timeors.setText("秒");
                    else
                        timeors.setText("次");
                    inputData[position_lv][0]=position_sp;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    spinner.setSelection(0);
                }
            });
            spinner.setSelection(inputData[position_lv][0]);
                // maybe need
            /*
            * if(exercises.get(spinner.getSelectedItemPosition()).unit==0)
                timeors.setText("秒");
            else
                timeors.setText("次");
            * */
            String s=inputData[position_lv][1]==0?"":String.valueOf(inputData[position_lv][1]);
            et.setText(s);
            return view;
        }
    }
    int exerciseNums=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);
        setTitle("添加一项训练");
        submit=findViewById(R.id.add_exercise_confirm);
        lv=findViewById(R.id.add_exercise_list);
        name=findViewById(R.id.add_exercise_name);
        add_e=findViewById(R.id.add_exercise_add_one);
        repeat_times=findViewById(R.id.add_exercise_repeat_times);
        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                InputMethodManager imManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(!hasFocus) {
                    if (imManager.isActive())
                        imManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
                else{
                    if(!imManager.isActive())
                        imManager.toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
        exerciseNums++;
        ExerciseItemAdapter ear=new ExerciseItemAdapter(this);
        lv.setAdapter(ear);
        add_e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //lv.setAdapter(new ExerciseAdapter(AddExerciseActivity.this,++exerciseNums));
                exerciseNums++;
                lv.setAdapter(new ExerciseItemAdapter(AddExerciseActivity.this,SundryFunctions.expandAtLastRow(((ExerciseItemAdapter)lv.getAdapter()).getInputData())));

            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder ab=new AlertDialog.Builder(AddExerciseActivity.this);
                ab.setTitle("询问").setMessage("是否删除这项内容？").setCancelable(false);
                ab.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int[][] dt=SundryFunctions.deleteExactRow(((ExerciseItemAdapter)lv.getAdapter()).getInputData(),position);
                        exerciseNums--;
                        lv.setAdapter(new ExerciseItemAdapter(AddExerciseActivity.this,dt));
                    }
                });
                ab.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                ab.show();
                return true;
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sportName=name.getText().toString();
                if(sportName.equals("")){
                    SundryFunctions.tipDialog(AddExerciseActivity.this,"请输入运动名称");
                    return;
                }
                int repeat=Integer.valueOf(repeat_times.getText().toString());
                if(repeat<=0){
                    SundryFunctions.tipDialog(AddExerciseActivity.this,"重复次数错误，请重新输入。");
                    return;
                }
                int[][] items=((ExerciseItemAdapter)lv.getAdapter()).getInputData();
                try{
                    SundryFunctions.judgeItems(items);
                    //该存储文件了
                    final JSONObject jsonObject=new JSONObject();
                    for (int j = 0; j < repeat; j++) {
                        for (int i = 0; i < items.length; i++)
                            jsonObject.put(new StringBuilder().append(j).append('-').append(i).toString(),SundryFunctions.intArrayToString(items[i]));
                        if(repeat!=1 && j!=repeat-1 && items[0][0]!=0 && items[items.length-1][0]!=0) {//如果重复一次以上，且最后一项不是休息，自动添加30秒休息
                            int[] t=new int[2];
                            t[0]=0;
                            t[1]=30;
                            jsonObject.put(new StringBuilder().append(j).append('-').append(items.length).toString(),SundryFunctions.intArrayToString(t));
                        }
                    }
                    File folder=new File(SundryFunctions.saveFolder);
                    if(!folder.exists())
                        folder.mkdirs();
                    final File file=new File(SundryFunctions.saveFolder+"/"+sportName+".sport");
                    if(!file.exists()) {
                        file.createNewFile();
                        try {
                            FileOutputStream fos = new FileOutputStream(file, false);
                            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
                            osw.write(new String(Base64.encode(jsonObject.toString().getBytes(), Base64.DEFAULT), "UTF-8"));
                            osw.close();
                            fos.close();
                        } catch (Exception e) {
                            SundryFunctions.tipDialog(AddExerciseActivity.this, "没有存储卡权限，或者JSON出错了");
                        }
                        AlertDialog.Builder ab = new AlertDialog.Builder(AddExerciseActivity.this);
                        ab.setTitle("提示").setMessage("添加成功！\n").setCancelable(false)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                        ab.show();
                    }
                    else{
                        AlertDialog.Builder ab=new AlertDialog.Builder(AddExerciseActivity.this);
                        ab.setMessage("已经有名为 "+sportName+" 的运动，是否覆盖它？");
                        ab.setTitle("提示").setCancelable(false);
                        ab.setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    FileOutputStream fos = new FileOutputStream(file, false);
                                    OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
                                    osw.write(new String(Base64.encode(jsonObject.toString().getBytes(), Base64.DEFAULT), "UTF-8"));
                                    osw.close();
                                    fos.close();
                                }catch (Exception e){
                                    SundryFunctions.tipDialog(AddExerciseActivity.this,"没有存储卡权限，或者JSON出错了");
                                }
                                AlertDialog.Builder ab=new AlertDialog.Builder(AddExerciseActivity.this);
                                ab.setTitle("提示").setMessage("添加成功！\n").setCancelable(false)
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        }).show();
                            }
                        });
                        ab.setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        ab.show();
                    }
                }catch (RuntimeException e){
                    SundryFunctions.tipDialog(AddExerciseActivity.this,e.getMessage());
                }catch (JSONException e){
                    SundryFunctions.tipDialog(AddExerciseActivity.this,"JSON ERROR");
                }catch (IOException e){
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            AlertDialog.Builder ab=new AlertDialog.Builder(AddExerciseActivity.this);
            ab.setCancelable(false);
            ab.setTitle("提示");
            ab.setMessage("退出后刚才添加的内容不会被保存，是否确定？");
            ab.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            ab.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            ab.show();
            return false;
        }
        else
            return super.onKeyDown(keyCode, event);
    }

}
