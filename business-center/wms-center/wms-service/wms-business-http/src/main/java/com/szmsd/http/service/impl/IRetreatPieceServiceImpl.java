package com.szmsd.http.service.impl;

import com.szmsd.http.mapper.BasRetreatPieceMapper;
import com.szmsd.http.service.IRetreatPieceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class IRetreatPieceServiceImpl implements IRetreatPieceService {
    @Autowired
    private BasRetreatPieceMapper basRetreatPieceMapper;
    @Override
    public int insetRetreatPiece(Map map5) {
//        Map map =new HashMap();
//        map.put("partner_code","TST2");
//        List<Map> list=new ArrayList<>();
//        Map map1=new HashMap();
//        map1.put("reception_wms_id",84823);
//        map1.put("date_finished","2022-09-06T19:03:05.73Z");
//        map1.put("reception_service","RETURN");
//
//        Map map2=new HashMap();
//
//        Map map3=new HashMap();
//        map3.put("code","PARCEL_SN");
//        map3.put("um","Buc");
//        map3.put("qty_received",1);
//        map3.put("sn","SN1090767");
//        map3.put("ua","Buc");
//        map2.put("269697SN1090767",map3);
//
//        Map map4=new HashMap();
//        map4.put("code","PARCEL_SN");
//        map4.put("um","Buc");
//        map4.put("qty_received",1);
//        map4.put("sn","SN1090766");
//        map4.put("ua","Buc");
//        map2.put("269697SN1090766",map4);
//
//        map1.put("products",map2);
//        list.add(map1);
//        map.put("events",list);

        List<Map> list1= (List<Map>) map5.get("events");
        list1.forEach(x->{
            Map map6= (Map) x.get("products");
            List<Map> list2 = new ArrayList<>(map6.values());
            List<Map> list3 = new ArrayList<>(map6.keySet());
            for (int s=0;s<list2.size();s++){
                list2.get(s).put("code_key",list3.get(s));
                list2.get(s).put("partner_code",map5.get("partner_code"));
                list2.get(s).put("reception_wms_id",x.get("reception_wms_id"));

                list2.get(s).put("reception_service",x.get("reception_service"));
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date date = null;
                DateFormat df2 = null;
                Date date1 = null;
                try {
                    date = df.parse(String.valueOf(x.get("date_finished")));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
                try {
                    date1 = df1.parse(date.toString());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                list2.get(s).put("date_finished",df2.format(date1));
                basRetreatPieceMapper.insertSelective( list2.get(s));
            }

        });

        return 0;
    }

}
