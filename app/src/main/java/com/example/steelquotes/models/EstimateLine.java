package com.example.steelquotes.models;

public class EstimateLine {
    private Integer id;
    private Integer steel;
    private Integer estimate;
    private Float weight;
    private Float length;
    private Float width;
    private Float height;
    private Long quantity;
    private Float total;
    private Integer margin;
    private Float quantityPlusMargin;
    private Float unitPrice;
    private Float totalPrice;

    public EstimateLine(){

    }

    public Integer getId(){
        return id;
    }
    public void setId(Integer id){
        this.id = id;
    }

    public Integer getSteel(){
        return steel;
    }
    public void setSteel(Integer steel){
        this.steel = steel;
    }

    public Integer getEstimate(){
        return estimate;
    }
    public void setEstimate(Integer estimate){
        this.estimate = estimate;
    }

    public Float getWeight(){
        return weight;
    }
    public void setWeight(Float weight){
        this.weight = weight;
    }

    public Float getLength(){
        return length;
    }
    public void setLength(Float length){
        this.length = length;
    }

    public Float getWidth(){
        return width;
    }
    public void setWidth(Float width){
        this.width = width;
    }

    public Float getHeight(){ return height; }
    public void setHeight(Float height){ this.height = height;}

    public Long getQuantity(){ return quantity;}
    public void setQuantity(Long quantity){ this.quantity = quantity;}

    public Float getTotal(){
        return total;
    }
    public void setTotal(Float total){
        this.total = total;
    }

    public Integer getMargin(){
        return margin;
    }
    public void setMargin(Integer margin){
        this.margin = margin;
    }

    public Float getNetQuantityPlusMargin(){
        return quantityPlusMargin;
    }
    public void setNetQuantityPlusMargin(Float netQuantityPlusMargin){
        this.quantityPlusMargin = netQuantityPlusMargin;
    }

    public Float getUnitPrice(){
        return unitPrice;
    }
    public void setUnitPrice(Float unitPrice){
        this.unitPrice = unitPrice;
    }

    public Float getTotalPrice(){
        return totalPrice;
    }
    public void setTotalPrice(Float totalPrice){
        this.totalPrice = totalPrice;
    }
}