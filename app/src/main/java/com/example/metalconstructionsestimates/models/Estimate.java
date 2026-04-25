package com.example.metalconstructionsestimates.models;

import java.io.Serializable;

public class Estimate implements Serializable {
    private Integer id;
    private String doneIn;
    private long issueDate;
    private long expirationDate;
    private long dueDate;
    private String dueTerms;
    private String status;
    private Integer customer;
    private Float excludingTaxTotal;
    private Float discount;
    private Float excludingTaxTotalAfterDiscount;
    private Float vat;
    private Float allTaxIncludedTotal;

    public Estimate(){

    }

    public Integer getId(){
        return id;
    }
    public void setId(Integer id){
        this.id = id;
    }

    public String getDoneIn(){
        return doneIn;
    }
    public void setDoneIn(String doneIn){ this.doneIn = doneIn; }

    public long getIssueDate(){
        return issueDate;
    }
    public void setIssueDate(long issueDate){ this.issueDate = issueDate; }

    public long getExpirationDate(){
        return expirationDate;
    }
    public void setExpirationDate(long expirationDate){ this.expirationDate = expirationDate; }
    public long getDueDate(){
        return dueDate;
    }
    public void setDueDate(long dueDate){
        this.dueDate = dueDate;
    }
    public String getDueTerms(){
        return dueTerms;
    }
    public void setDueTerms(String dueTerms){
        this.dueTerms = dueTerms;
    }
    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
        this.status = status;
    }
    public Integer getCustomer(){
        return customer;
    }
    public void setCustomer(Integer customer){ this.customer = customer; }

    public Float getExcludingTaxTotal(){
        return excludingTaxTotal;
    }
    public void setExcludingTaxTotal(Float excludingTaxTotal){ this.excludingTaxTotal = excludingTaxTotal; }

    public Float getDiscount(){ return discount; }
    public void setDiscount(Float discount){ this.discount = discount; }

    public Float getExcludingTaxTotalAfterDiscount(){ return excludingTaxTotalAfterDiscount; }
    public void setExcludingTaxTotalAfterDiscount(Float excludingTaxTotalAfterDiscount){ this.excludingTaxTotalAfterDiscount = excludingTaxTotalAfterDiscount; }

    public Float getVat(){ return vat; }
    public void setVat(Float vat){ this.vat = vat; }

    public Float getAllTaxIncludedTotal(){ return allTaxIncludedTotal; }
    public void setAllTaxIncludedTotal(Float allTaxIncludedTotal){ this.allTaxIncludedTotal = allTaxIncludedTotal; }

}