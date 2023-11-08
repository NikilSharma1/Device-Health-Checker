package com.example.devicehealthchecker;

public class Checks {
    private String name;
    private String description;
    public Checks(String name,String description){
        this.name=name;
        this.description=description;
    }
    public void setName(String Name){
        name=Name;
    }
    public void setDescription(String Desc){
        description=Desc;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
