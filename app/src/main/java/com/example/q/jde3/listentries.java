package com.example.q.jde3;

import java.io.Serializable;

public class listentries implements Serializable {

    private String NameString;
    private String NumberString;

    private boolean active;

    public listentries(String name, String number){
        this.NameString = name;
        this.NumberString = number;
        this.active = true;
    }

    public listentries(String name, String number, boolean active){
        this.NameString = name;
        this.NumberString = number;
        this.active = active;
    }
    public String getNumber() {
        return NumberString;
    }

    public void setNumber(String newNumber) {
        this.NumberString = newNumber;
    }

    public String getName() {
        return NameString;
    }

    public void setName(String newNumber) {
        this.NameString = newNumber;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return this.NameString +":\n  "+ this.NumberString;
    }

}
