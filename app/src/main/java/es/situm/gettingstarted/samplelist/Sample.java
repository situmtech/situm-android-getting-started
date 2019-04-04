package es.situm.gettingstarted.samplelist;

import android.content.Intent;

class Sample {

    private final String title;
    private final Intent intent;

    Sample(String text, Intent intent) {
        this.title = text;
        this.intent = intent;
    }

    String getText() {
        return title;
    }

    Intent getIntent() {
        return intent;
    }
}
