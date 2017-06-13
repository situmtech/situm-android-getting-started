package es.situm.gettingstarted;

import android.content.Intent;

/**
 * Created by alberto.penas on 13/06/17.
 */

class Sample {


    private String text;
    private Class clazz;


    Sample(String text,
           Class clazz) {
        this.text = text;
        this.clazz= clazz;
    }

    String getText() {
        return text;
    }

    Class getClazz() {
        return clazz;
    }
}
