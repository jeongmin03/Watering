package com.example.watering;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class Plant implements Serializable {

    private String plantNum;
    private String plantName;
    private int plantCycle;
    private String plantLastWater;
    private String plantPhotoInfo;
    private String plantWaterCheck;

    public Plant(){}

    public Plant(String plantNum, String plantName, int plantCycle, String plantLastWater, String plantPhotoInfo){
        this.plantNum = plantNum;
        this.plantName = plantName;
        this.plantCycle = plantCycle;
        this.plantLastWater = plantLastWater;
        this.plantPhotoInfo = plantPhotoInfo;
        plantWaterCheck = "false";
    }
    public Plant(String plantNum, String plantName, int plantCycle, String plantLastWater, String plantPhotoInfo, String plantWaterCheck){
        this.plantNum =plantNum;
        this.plantName = plantName;
        this.plantCycle = plantCycle;
        this.plantLastWater = plantLastWater;
        this.plantPhotoInfo = plantPhotoInfo;
        this.plantWaterCheck = plantWaterCheck;
    }

    public String getPlantNum() { return plantNum; }
    public void setPlantNum(String plantNum) { this.plantNum = plantNum; }
    public String getPlantName() { return plantName; }
    public void setPlantName(String plantName) { this.plantName = plantName; }
    public int getPlantCycle() { return plantCycle; }
    public void setPlantCycle(int plantCycle) { this.plantCycle = plantCycle; }
    public String getPlantLastWater() { return plantLastWater; }
    public void setPlantLastWater(String plantLastWater) { this.plantLastWater = plantLastWater; }
    public String getPlantWaterCheck() { return plantWaterCheck; }
    public void setPlantWaterCheck(String plantWaterCheck) { this.plantWaterCheck = plantWaterCheck; }
    public String getPlantPhotoInfo() { return plantPhotoInfo; }
    public void setPlantPhotoInfo(String plantPhotoInfo) { this.plantPhotoInfo = plantPhotoInfo; }

}
