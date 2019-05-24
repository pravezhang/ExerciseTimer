package com.prave.tools.exercisetimer;

public class Exercise {
    public int unit;
    public String name;
    public int voiceID;
    public int eID;
/*如果unit是0，则是以时间计算的，如果不是0，就是以次数计算的，unit表示每次多少秒
*
* */

    public Exercise(int eID,String name,int unit, int voiceID) {
        this.eID=eID;
        this.unit = unit;
        this.name = name;
        this.voiceID=voiceID;
    }
    @Override
    public String toString() {
        return name;
    }


}
