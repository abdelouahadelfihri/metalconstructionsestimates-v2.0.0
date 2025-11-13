package com.example.metalconstructionsestimates.models;

public class Steel {
    private Integer id;
    private String type;
    private String geometricShape;
    private Float weight;
    private String unit;

    public Steel(){
    }

    public Integer getId(){
        return id;
    }
    public void setId(Integer id){
        this.id = id;
    }

    public String getType(){
        return type;
    }
    public void setType(String type){
        this.type= type;
    }

    public String getGeometricShape(){
        return geometricShape;
    }
    public void setGeometricShape(String geometricShape){
        this.geometricShape = geometricShape;
    }

    public Float getWeight(){ return weight; }
    public void setWeight(Float weight){
        this.weight = weight;
    }
    public String getUnit(){ return unit; }
    public void setUnit(String unit){
        this.unit = unit;
    }

}