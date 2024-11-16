package com.example.metalconstructionsestimates.models;

import java.io.Serializable;

public class Estimate implements Serializable {
    private Integer id;
    private String doneIn;
    private String issueDate;
    private String expirationDate;
    private Integer customer;
    private Float excludingTaxTotal;
    private Float discount;
    private Float excludingTaxTotalAfterDiscount;
    private Float vat;
    private Float allTaxIncludedTotal;
    private Float amountPaid;

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

    public String getIssueDate(){
        return issueDate;
    }
    public void setIssueDate(String issueDate){ this.issueDate = issueDate; }

    public String getExpirationDate(){
        return expirationDate;
    }
    public void setExpirationDate(String expirationDate){ this.expirationDate = expirationDate; }

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

    public Float getAmountPaid(){ return amountPaid; }
    public void setAmountPaid(Float amountPaid){ this.amountPaid = amountPaid; }
}