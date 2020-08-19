package com.company;

import java.util.HashMap;
import java.util.Map;

//trade record structure setup
class basicTrade {
    boolean _header = true;
    String _delim = ",";

    private Map<String,Integer> _fields = new HashMap<>();

    basicTrade() {
        _fields.put("time_stamp", 0);
        _fields.put("broker", 1);
        _fields.put("sequence_id", 2);
        _fields.put("type", 3);
        _fields.put("symbol", 4);
        _fields.put("quantity", 5);
        _fields.put("price", 6);
        _fields.put("side", 7);
    }

    Integer indexOf(String s) {
        if(_fields.containsKey(s))
            return _fields.get(s);
        return -1;
    }

    Integer fieldsCount() {
        return _fields.size();
    }
}
