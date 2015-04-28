package me.madhvani.dwells.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

//http://www.jsonschema2pojo.org/
public class Kot implements Parcelable {

    private String city;
    private String url;
    private Integer price;
    private Integer area;
    private Double latitude;
    private Double longitude;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The city
     */
    public String getCity() {
        return city;
    }

    /**
     *
     * @param city
     * The city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     *
     * @return
     * The url
     */
    public String getUrl() {
        return url;
    }

    /**
     *
     * @param url
     * The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     *
     * @return
     * The price
     */
    public Integer getPrice() {
        return price;
    }

    /**
     *
     * @param price
     * The price
     */
    public void setPrice(Integer price) {
        this.price = price;
    }

    /**
     *
     * @return
     * The area
     */
    public Integer getArea() {
        return area;
    }

    /**
     *
     * @param area
     * The area
     */
    public void setArea(Integer area) {
        this.area = area;
    }

    /**
     *
     * @return
     * The latitude
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     *
     * @param latitude
     * The latitude
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     *
     * @return
     * The longitude
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     *
     * @param longitude
     * The longitude
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    //Plugin: Android Parcelable Code Generator
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.city);
        dest.writeString(this.url);
        dest.writeValue(this.price);
        dest.writeValue(this.area);
        dest.writeValue(this.latitude);
        dest.writeValue(this.longitude);
    }

    public Kot() {
    }

    private Kot(Parcel in) {
        this.city = in.readString();
        this.url = in.readString();
        this.price = (Integer) in.readValue(Integer.class.getClassLoader());
        this.area = (Integer) in.readValue(Integer.class.getClassLoader());
        this.latitude = (Double) in.readValue(Double.class.getClassLoader());
        this.longitude = (Double) in.readValue(Double.class.getClassLoader());
    }

    public static final Creator<Kot> CREATOR = new Creator<Kot>() {
        public Kot createFromParcel(Parcel source) {
            return new Kot(source);
        }

        public Kot[] newArray(int size) {
            return new Kot[size];
        }
    };
}
