package com.neverwasradio.neverwasplayer.Model;

import java.util.ArrayList;

/**
 * Created by chiara on 22/11/15.
 */
public class NWProgramManager {

    public static final int MONDAY = 0;
    public static final int TUESDAY = 1;
    public static final int WEDNESDAY = 2;

    private static NWProgramManager instance;
    private static ArrayList<NWProgram> programList;

    public static void create(ArrayList<NWProgram> list){
        programList=list;
        instance=new NWProgramManager();
    }

    public static NWProgramManager getInstance() {
        return instance;
    }

    public ArrayList<NWProgram> getAllPrograms() { return programList; }

    public ArrayList<NWProgram> getProgramsByDay(int day) {

        ArrayList<NWProgram> result = new ArrayList<NWProgram>();
        for(int i =0; i<programList.size(); i++) {
            if(programList.get(i).getDay()==day) {result.add(programList.get(i));}
        }

        return result;
    }

    public NWProgram getProgramById(String id) {
        for(int i =0; i<programList.size(); i++) {
            if(programList.get(i).getId().compareTo(id)==0) {return programList.get(i);}
        }
        return null;
    }

}
