package com.mazinaltokhais.trees;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by mazoo_000 on 05/08/2017.
 */
@IgnoreExtraProperties
public class Tree {
    private LatLng mLatLng;
    private String mCity;
    private String mYear;
    private String mAdminArea ;
    private String mCountryName;
    private String mType = "";
    private String mYear_Area;
    private String mDate;
    private String mCampaignName;


    private Tree(LatLng LatLng,
             String City,
             String Year,
             String AdminArea ,
             String CountryName) {

            this.mCity = City;
            this.mLatLng = LatLng;
        this.mYear = Year;
        this.setmAdminArea(AdminArea);
        this.setmCountryName(CountryName);


    }
    private Tree( ) {



    }
    public static Tree createTree() {
        return new Tree();
    }

    public LatLng getmLatLng() {
        return mLatLng;
    }

    public void setmLatLng(LatLng mLatLng) {
        this.mLatLng = mLatLng;
    }

    public String getmCity() {
        return mCity;
    }

    public void setmCity(String mCity) {
        this.mCity = mCity;
    }

    public String getmYear() {
        return mYear;
    }

    public void setmYear(String mYear) {
        this.mYear = mYear;
    }

    public String getmAdminArea() {
        return mAdminArea;
    }

    public void setmAdminArea(String mAdminArea) {
        this.mAdminArea = mAdminArea;
    }

    public String getmCountryName() {
        return mCountryName;
    }

    public void setmCountryName(String mCountryName) {
        this.mCountryName = mCountryName;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public String getmYear_Area() {
        return mYear_Area;
    }

    public void setmYear_Area(String mYear_Area) {
        this.mYear_Area = mYear_Area;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String mDate) {
        this.mDate = mDate;
    }

    public String getmCampaignName() {
        return mCampaignName;
    }

    public void setmCampaignName(String mCampaignName) {
        this.mCampaignName = mCampaignName;
    }
}
