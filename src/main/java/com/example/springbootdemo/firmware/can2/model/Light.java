package com.example.springbootdemo.firmware.can2.model;

import java.util.HashMap;
import java.util.Map;

public class Light {
    public boolean enable;
    public LigthMode frontLigthMode;
    public int frontLigthValue;
    public LigthMode backLigthMode;
    public int backLigthValue;

    public byte[] getBytes() {
        byte[] result = new byte[8];
        result[0]= (byte)(enable?0x01:0x00);
        result[1]= (byte)frontLigthMode.getKey();
        result[2] = (byte) frontLigthValue;
        result[3] = ((byte) backLigthMode.getKey());
        result[4] = ((byte) backLigthValue);
        return result;
    }


    public enum LigthMode{
        off,
        open,
        breath,
        custom;
        private static Map<Integer,LigthMode> map;
        static{
            map = new HashMap();
            map.put(0x00, off);
            map.put(0x01,open);
            map.put(0x02,breath);
            map.put(0x03,custom);
        }
        public static LigthMode parse(int key){
            return map.get(key);
        }
        public int getKey(){
           return  map.entrySet().stream().filter(entry -> entry.getValue().equals(this)).findFirst().get().getKey();
        }
    }
}
