package com.prave.tools.exercisetimer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SundryFunctions {
    static String saveFolder=Environment.getExternalStorageDirectory().getAbsolutePath()
            +"/ExerciseTimer"
            +"/exercises";
    static String dbFolder=Environment.getExternalStorageDirectory().getAbsolutePath()
            +"/ExerciseTimer"
            +"/db";
    public static Exercise[] exercises={
            new Exercise(0x0000,"休息",0,R.raw.take_rest),
            new Exercise(0x0001,"平板支撑",0,R.raw.ping_ban_zhi_cheng),
            new Exercise(0x0002,"Burpees",0,R.raw.burpees),
            new Exercise(0x0003,"原地慢跑",0,R.raw.yuan_di_man_pao),
            new Exercise(0x0004,"垫脚尖",0,R.raw.dian_jiao_jian),
            new Exercise(0x0005,"胸部拉伸",0,R.raw.xiong_bu_la_shen),
            new Exercise(0x0006,"俯卧登山",0,R.raw.dian_jiao_jian),
            new Exercise(0x1001,"深蹲",3,R.raw.shen_dun),
    };


    public static int[][] deleteExactRow(int[][] array,int rowPosition){
        if(array.length==0)
            return array;
        int[][] result=new int[array.length-1][array[0].length];
        int offset=0;
        for (int i = 0; i < result.length; i++) {
            if(i==rowPosition && offset==0)
                offset+=1;
            System.arraycopy(array[i + offset], 0, result[i], 0, result[0].length);
        }
        return result;
    }

    public static int[][] expandAtLastRow(int[][] array){
        if(array.length==0)
            return new int[1][2];
        int[][] result=new int[array.length+1][array[0].length];
        for (int i = 0; i < array.length; i++) {
            System.arraycopy(array[i], 0, result[i], 0, array[0].length);
        }
        return result;
    }

    public static void judgeItems(int[][] dt) throws RuntimeException{
        if(dt.length==0) throw new RuntimeException("尚未添加一项运动！");
        for (int i = 0; i < dt.length; i++) {
            if(i!=dt.length-1)  // 不是最后一项
                if(dt[i][0]==dt[i+1][0])
                    throw new RuntimeException("两项连续的运动项目相同，请重试。");
            if(dt[i][1]<=0)
                throw new RuntimeException("第"+(i+1)+"项运动的时间为0，请重试。");
        }
    }

    public static AlertDialog tipDialog(Context context,String message){
        AlertDialog.Builder ab=new AlertDialog.Builder(context);
        ab.setTitle("提示");
        ab.setMessage(message);
        ab.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        return ab.show();
    }

    public static String intArrayToString(int[] array){
        StringBuilder sb=new StringBuilder();
        for (int i:array)
            sb.append(i).append("＠");
        return sb.toString();
    }

    public static int[] stringToIntArray(String str){
        String[] ss=str.split("＠");
        int[] ress=new int[ss.length];
        for (int i = 0; i < ss.length; i++) {
            ress[i]=Integer.valueOf(ss[i]);
        }
        return ress;
    }




    public static int getDuration(List<Pair<Exercise,Integer>> sport){
        return getDuration(sport,0,sport.size());
    }

    public static int getDuration(List<Pair<Exercise,Integer>> sport,int from,int to){
        int dur=0;
        for (int i = from; i < to; i++) {
            Pair<Exercise,Integer> p=sport.get(i);
            dur=dur+(p.first.unit==0?p.second:(p.second*p.first.unit));
        }
        return dur;
    }
    public static List<Pair<Exercise,Integer>> getSport(String fileUrl) throws Exception{
        //要返回的是Exercise类，Exercise类包含名字（汉字）、语音ID、单次时间或持续时间
        //从json中读到的key是第几遍-第几个动作，content 是运动编号（位于exercises数组里的）@运动时间/次数
        List<Pair<Exercise,Integer>> result=new ArrayList<>();
        File file=new File(fileUrl);
        FileReader fr=new FileReader(file);
        BufferedReader br=new BufferedReader(fr);
        String t=br.readLine();
        StringBuilder sb=new StringBuilder();
        while(t!=null){
            sb.append(t).append("\n");
            t=br.readLine();
        }
        String origin=new String(Base64.decode(sb.toString(),0),"UTF-8");
        JSONObject j=new JSONObject(origin);
        Iterator<String> it=j.keys();
        while(it.hasNext()){
            int[] ret=stringToIntArray((String) j.get(it.next()));
            result.add(new Pair<>(exercises[ret[0]],ret[1]));
        }
        return result;
    }



    public static List<Pair<String,Integer>> filterExerciseByName(List<Pair<String,Integer>> exercises, String name){
        List<Pair<String,Integer>> result=new ArrayList<>();
        for (Pair<String,Integer> e:exercises)
            if((e.first).contains(name))
                result.add(e);
        return result;
    }




}
