package dynamicReduce.reducer;
import dynamicReduce.utils.Logger;

import java.util.ArrayList;
import java.util.List;

public class DeltaSelector<T> {
    private List<T> targetList;
    private int step;
    private int startPin=0;//mark start position of a round
    private int startPos=0;
    private boolean overRound=false;
    public boolean ended=false;
    public int stepLimit=1;
    public DeltaSelector(List<T> targetList){
        if(targetList.isEmpty()){ended=true;}
        this.targetList=targetList;
        step=targetList.size();
    }
    public DeltaSelector(List<T> targetList,int startStep){
        if(targetList.isEmpty()){ended=true;}
        this.targetList=targetList;
        step=startStep;
    }
    public List<T> get(){
        List<T> resList=new ArrayList<>();
        if(step<=0){ended=true;return resList;}
        Logger logger=new Logger();
        Logger.log("From 0-"+(targetList.size()-1));
        logger.logStart("Deleting: ");
        for (int i = 0; i < step; i++) {
            logger.logPart(startPos+",");
            resList.add(targetList.get(startPos));
            startPos++;
            if(startPos>=targetList.size()){startPos=0;}
            if(startPos==startPin){overRound=true;break;}
        }
        logger.endLog();
        return resList;
    }
    public void success(List<T> updateList){
        overRound=false;
        targetList=updateList;
        if(targetList.isEmpty()){
            ended=true;
            return;
        }
        if (targetList.size()<step){
            step=targetList.size();
        }
        startPin=startPos-step;
        while (startPin<0){
            startPin+=targetList.size();
        }
        while (startPin>=targetList.size()){
            startPin-=targetList.size();
        }
        startPos=startPin;
    }
    public void success(List<T> updateList,int startPos){
        success(updateList);
        this.startPos=startPos;
        this.startPin=startPos;
    }
    public void fail(){
        if(!overRound){return;}
        startPin=0;
        startPos=0;
        if(step<=stepLimit){ended=true;}
        step=step/2;
        overRound=false;
    }
    public void resetFail(){
        if(!overRound){return;}
        startPos=startPin;
        if(step<=stepLimit){ended=true;}
        step=step/2;
        overRound=false;
    }

    public int getStep(){
        return step;
    }
}

