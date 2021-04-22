package com.marco.javacovidstatus.enums;

public enum Gender {
    MEN(1), WOMEN(2);
    
    private int gender;

    Gender(int gender) {
        this.gender = gender;
    }

    public int getGender() {
        return this.gender;
    }

    public static Gender fromInt(int gender) {
        for (Gender p : Gender.values()) {
            if (p.gender == gender) {
                return p;
            }
        }
        return null;
    }
}
