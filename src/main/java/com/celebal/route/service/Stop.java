package com.celebal.route.service;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Stop {
    private int stopNo;
    private int x;
    private int y;


    @Override
    public String toString() {
        return this.stopNo + " " + this.x + " " + this.y;
    }

}
