package com.company;

import java.util.HashMap;
import java.util.Map;

//trade record structure setup
class basicTrade {
    private Map<String,Integer> fields = new HashMap<>();

    basicTrade() {
        fields.put("time_stamp", 0);
        fields.put("broker", 1);
        fields.put("sequence_id", 2);
        fields.put("type", 3);
        fields.put("symbol", 4);
        fields.put("quantity", 5);
        fields.put("price", 6);
        fields.put("side", 7);
    }

    Integer indexOf(String s) {
        return fields.get(s);
    }
}
