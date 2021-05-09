package com.example.watering;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class Plant implements Serializable {

    //private int plantPhoto;
    //private String plantUrl;

    private String plantName;
    private int plantCycle;
    private String plantLastWater;
    private String plantPhotoInfo;
    private String plantWaterCheck;

    public Plant(){}
    /*public Plant(String plantName){
        this.plantName = plantName;
        plantPhoto = 0;
    }
    public Plant(int plantPhoto, String plantName){
        this.plantPhoto = plantPhoto;
        this.plantName = plantName;
    }*/
    public Plant(String plantName, int plantCycle, String plantLastWater, String plantPhotoInfo){
        this.plantName = plantName;
        this.plantCycle = plantCycle;
        this.plantLastWater = plantLastWater;
        this.plantPhotoInfo = plantPhotoInfo;
        plantWaterCheck = "false";
    }
    public Plant(String plantName, int plantCycle, String plantLastWater, String plantPhotoInfo, String plantWaterCheck){
        this.plantName = plantName;
        this.plantCycle = plantCycle;
        this.plantLastWater = plantLastWater;
        this.plantPhotoInfo = plantPhotoInfo;
        this.plantWaterCheck = plantWaterCheck;
    }

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


    /*  public int getPlantPhoto() {
         return 0;
     }
  /* public void setPlantPhoto(int plantPhoto) {
          this.plantPhoto = plantPhoto;
     /*
      public String getPlantUrl() {
          return plantUrl;
      }
      public void setPlantUrl(String plantUrl) {
          this.plantUrl = plantUrl;
      }
  */
}
