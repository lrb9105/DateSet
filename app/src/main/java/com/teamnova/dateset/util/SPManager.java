package com.teamnova.dateset.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.teamnova.dateset.dto.ScheduleDto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class SPManager {
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String separatorAboutData = "@#&";

    public SPManager(Context context, String name, int mode){
        this.sp = context.getSharedPreferences(name,mode);
        this.editor = sp.edit();
    }

    // 추가 - 사이즈 저장
    public void insertScheduleDto(String key, ScheduleDto dto, int position){
        //int position = getSize();

        // key값과 위치값을 받아와서 key값 생성.
        key = key+position;
        String value = dto.getWriter() + separatorAboutData + dto.getSchType() + separatorAboutData + dto.getTitle() + separatorAboutData + dto.getPlace() + separatorAboutData + dto.getStartDateWithDay() + separatorAboutData + dto.getStartDate()
                + separatorAboutData + dto.getStartTime() + separatorAboutData + dto.getEndDate() + separatorAboutData + dto.getEndDateWithDay() + separatorAboutData + dto.getEndTime() + separatorAboutData + dto.getRepeatType()
                + separatorAboutData + dto.getRepeatEndDateWithDay() + separatorAboutData + dto.getRepeatEndDate() + separatorAboutData + dto.getAlarmType()
                + separatorAboutData + dto.getMemo() + separatorAboutData + position + separatorAboutData + dto.getStartTimeWithoutAmPm() + separatorAboutData + dto.getEndTimeWithoutAmPm()+ separatorAboutData + dto.getFirstDate() + separatorAboutData + dto.getKey() + separatorAboutData + key;

        editor.putString(key,value);
        editor.putInt("size",position); //size저장
        editor.commit();
    }

    // 수정 - 사이즈값 저장 안함
    public void modifyScheduleDto(String key, ScheduleDto dto, int position){
        // key값과 위치값을 받아와서 key값 생성.
        String value = dto.getWriter() + separatorAboutData + dto.getSchType() + separatorAboutData + dto.getTitle() + separatorAboutData + dto.getPlace() + separatorAboutData + dto.getStartDateWithDay() + separatorAboutData + dto.getStartDate()
                + separatorAboutData + dto.getStartTime() + separatorAboutData + dto.getEndDate() + separatorAboutData + dto.getEndDateWithDay() + separatorAboutData + dto.getEndTime() + separatorAboutData + dto.getRepeatType()
                + separatorAboutData + dto.getRepeatEndDateWithDay() + separatorAboutData + dto.getRepeatEndDate() + separatorAboutData + dto.getAlarmType()
                + separatorAboutData + dto.getMemo() + separatorAboutData + position + separatorAboutData + dto.getStartTimeWithoutAmPm() + separatorAboutData + dto.getEndTimeWithoutAmPm() + separatorAboutData + dto.getFirstDate() + separatorAboutData + dto.getKey() + separatorAboutData + key;

        editor.putString(key,value);
        editor.commit();
    }

    public ScheduleDto getDto(String key, int position){
        key = key+position;
        String dtoStr = sp.getString(key,"");
        if(dtoStr.equals("")) return null;

        String[] dtoArr = dtoStr.split(separatorAboutData);

        Log.d("debug_Dto",key);

        String writer = dtoArr[0];        // 작성자
        String schType = dtoArr[1];       // 스케줄 종류 0:공동, 1:본인, 2:상대방
        String title = dtoArr[2];         // 제목
        String place = dtoArr[3];         // 위치
        String startDateWithDay = dtoArr[4];     // 시작날짜(일 포함)
        String startDate = dtoArr[5];     // 시작날짜(YYYYMMDD)
        String startTime = dtoArr[6];     // 시작시간
        String endDate = dtoArr[7];       // 종료날짜(YYYYMMDD)
        String endDateWithDay = dtoArr[8];       // 종료날짜(일 포함)
        String endTime = dtoArr[9];       // 종료시간
        String repeatType = dtoArr[10];    // 반복종류
        String repeatEndDateWithDay = dtoArr[11]; // 반복종료날짜(일 포함)
        String repeatEndDate = dtoArr[12]; // 반복종료날짜(YYYYMMDD)
        String alarmType = dtoArr[13];     // 알람종류
        String memo = dtoArr[14];          // 메모
        int id = Integer.parseInt(dtoArr[15]);               // SP에서 사용할 id
        String startTimeWithoutAmPm = dtoArr[16];          // 메모
        String endTimeWithoutAmPm = dtoArr[17];          // 메모
        String firstDate = dtoArr[18];          // 최초 시작일
        String fbKey = dtoArr[19];          // 파이어베이스 키
        String spKey = dtoArr[20];          // sp키

        ScheduleDto dto = new ScheduleDto(writer, schType, title, place, startDateWithDay, startDate, startTime, endDate, endDateWithDay, endTime, repeatType, repeatEndDateWithDay, repeatEndDate, alarmType, memo, id, startTimeWithoutAmPm, endTimeWithoutAmPm, firstDate);
        dto.setKey(fbKey);
        dto.setSpKey(spKey);
        return dto;
    }

    // 현재 사이즈를 반환
    public int getCurrentSize(){
        int size = sp.getInt("size",0);
        return size;
    }

    public Map<String,?> getAll(){
        return sp.getAll();
    }

    //데이터 삭제
    public void removeDto(String key){
        Log.d("debug_remove",key);

        editor.remove(key);
        editor.commit();
    }

    public String getProfileStr(String key){
        return sp.getString(key,"");
    }

    public void saveProfileImg(String key, String profileImgStr){
        editor.putString(key,profileImgStr);
        editor.commit();
    }

    public void removeProfileImg(String key){
        editor.remove(key);
        editor.commit();
    }

    public void setJsonData() throws JSONException {
        String str = "{\"key1\",:\"value1\"}";
        JSONObject jsonObj = new JSONObject(str);
        jsonObj.put("af",1);
        JSONArray jsonArr = new JSONArray();
        jsonArr.put(jsonObj);
        jsonArr.put(1);
        jsonArr.put("asd");
        jsonObj.put("af",jsonArr);
    }

    public void setCompleteStep(int step){
        editor.putInt("step",step);
        editor.commit();
    }

    public int getCompleteStep(){
        return sp.getInt("step",0);
    }
}
