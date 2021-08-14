package com.example.androidlabs;


public class Message {
    protected String type;
    protected String message;
    protected long id;

    public Message(String type, String m, long i) {
        this.type = type;
        this.message = m;
        id = i;
    }

    public void update(String type, String m) {
        this.type = type;
        this.message = m;
    }

    /**Chaining constructor: */
    public Message(String type, String m) { this(type, m, 0);}

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public long getId() {
        return id;
    }
}
