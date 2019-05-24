package com.prave.tools.exercisetimer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AllExerciseActivity extends AppCompatActivity {

    ListView lv_all_exercises;
    List<Pair<String,Integer>> data;
    AutoCompleteTextView search_box;
    private class ExerciseListAdapter extends BaseAdapter{
        private Context context;
        private List<Pair<String,Integer>> data;
        public ExerciseListAdapter(Context context, List<Pair<String, Integer>> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size()*2;
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return (long)position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //position==0 - title
            //position%2==1 - sport
            //position%2==0 - split
            if(position%2==0){
                if(position==0)
                    return View.inflate(this.context,R.layout.single_item_all_exercise_title,null);
                else
                    return View.inflate(this.context,R.layout.single_item_all_exercise_split,null);
            }
            else{
                View v=View.inflate(this.context,R.layout.single_item_all_exercise,null);
                TextView name=v.findViewById(R.id.all_exercise_item_name);
                TextView duration=v.findViewById(R.id.all_exercise_item_duration);
                ImageButton go=v.findViewById(R.id.all_exercise_item_func);
                name.setText(data.get(position/2).first);
                duration.setText(""+data.get(position/2).second);
                go.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent();
                        intent.setClass(AllExerciseActivity.this,ExercisingActivity.class);
                        intent.putExtra("NAME",data.get(position/2).first);
                        startActivity(intent);
                    }
                });
                return v;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_exercise);
        setTitle("所有训练项目");
        lv_all_exercises=findViewById(R.id.all_exercise_lv);
        search_box=findViewById(R.id.all_exercise_search);
        File folder=new File(SundryFunctions.saveFolder);
        data=new ArrayList<>();
        if(!folder.exists())
            folder.mkdirs();
        else{
            for(File f:folder.listFiles()){
                if(!f.getName().endsWith(".sport"))
                    continue;
                try{
                    data.add(new Pair<String, Integer>(f.getName().split(".sport")[0],SundryFunctions.getDuration(SundryFunctions.getSport(f.getAbsolutePath()))));
                }catch (Exception e){
                    continue;
                }
            }
        }
        if(data.size()==0){
            AlertDialog.Builder ab=new AlertDialog.Builder(this);
            ab.setTitle("提示").setMessage("现在还没有训练，去创建一个吧！").setCancelable(false);
            ab.setPositiveButton("好", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            ab.show();
        }
        lv_all_exercises.setAdapter(new ExerciseListAdapter(this,data));
        search_box.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lv_all_exercises.setAdapter(new ExerciseListAdapter(AllExerciseActivity.this,SundryFunctions.filterExerciseByName(data,search_box.getText().toString())));
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

}
