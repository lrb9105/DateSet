package com.teamnova.dateset.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Util {
    /**
     *  1. 메소드명: EncBySha256
     *  2. 메소드 역할: 개인정보를 암호화한다.
     *  3. 입력파라미터
     *      1) data: 암호화할 데이터
     *  4. 출력파라미터: 암호화된 문자열
     * */
    public static String EncBySha256(String data) throws Exception {
        String  encData = null;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(data.getBytes());

            byte byteData[] = md.digest();
            StringBuffer sb = new StringBuffer();

            for(int i=0; i<byteData.length; i++) {
                sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
            }

            StringBuffer hexString = new StringBuffer();
            for(int i=0; i<byteData.length;i++) {
                String hex = Integer.toHexString(0xff & byteData[i]);
                if(hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            encData = hexString.toString();
        } catch(NoSuchAlgorithmException e){
            System.out.println("EncBySHA256 Error:" + e.toString());
        }
        return encData;
    }


    /**
     *  1. 메소드명: getDateDay
     *  2. 메소드 역할: 입력한 날짜에 해당하는 요일을 반환한다.
     *  3. 입력파라미터
     *      1) date: 날짜
     *      2) dadeType: date변수의 값과 동일한 형태여야 한다.
     *  4. 출력파라미터: date에 해당하는 요일
     * */
    public static String getDateDay(String date, String dateType) throws Exception {

        String day = "";

        SimpleDateFormat dateFormat = new SimpleDateFormat(dateType);
        Date nDate = dateFormat.parse(date);

        Calendar cal = Calendar.getInstance();
        cal.setTime(nDate);

        int dayNum = cal.get(Calendar.DAY_OF_WEEK);

        switch (dayNum) {
            case 1:
                day = "일";
                break;
            case 2:
                day = "월";
                break;
            case 3:
                day = "화";
                break;
            case 4:
                day = "수";
                break;
            case 5:
                day = "목";
                break;
            case 6:
                day = "금";
                break;
            case 7:
                day = "토";
                break;

        }

        return day;
    }

    public static String getTime(int hour, int minute){
        String time = null;
        String minStr = null;
        if(minute < 10){
            minStr = "0"+minute;
        } else{
            minStr = ""+minute;
        }

        if(hour == 0){
            time = "오전 " + 12 + ":" + minStr;
        } else if(hour < 12){
            time = "오전 " + hour + ":" + minStr;
        } else if(hour == 12){
            time = "오후 " + hour + ":" + minStr;
        } else{
            time = "오후 " + (hour - 12) + ":" + minStr;
        }
        return time;
    }

    // 시작일, 종료일 차이 계산(반복 저장하기 위해서) - 동일한 날짜면 0반환
    public static int dateDiff(String srtDate, String endDate){
        if(srtDate.equals(endDate)) return 0;

        String strFormat = "yyyyMMdd";    //strStartDate 와 strEndDate 의 format
        long diffDay = 0;

        //SimpleDateFormat 을 이용하여 startDate와 endDate의 Date 객체를 생성한다.
        SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
        try{
            Date startDate = sdf.parse(srtDate);
            Date endDate2 = sdf.parse(endDate);

            //두날짜 사이의 시간 차이(ms)를 하루 동안의 ms(24시*60분*60초*1000밀리초) 로 나눈다.
            diffDay = ( endDate2.getTime() - startDate.getTime()) / (24*60*60*1000);

        }catch(ParseException e){
            e.printStackTrace();
        }

        return (int)diffDay;
    }
}
