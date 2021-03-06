/*
 * GeoSolutions map - Digital field mapping on Android based devices
 * Copyright (C) 2013  GeoSolutions (www.geo-solutions.it)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.geosolutions.android.map.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class to represent a query model to perform a search by a circle drawed on map.
 * @author Jacopo Pianigiani (jacopo.pianigiani85@gmail.com).
 */
public class FeatureCircleQuery implements Parcelable {
	//Coordinates of center
	private double x;
	private double y;
	private double radius;
	//private double stroke_width;
	private byte zoomLevel;
	private String srid;
	
	/**
	 * Method that return x coordinate of center
	 * @return double
	 */
	public double getX() {
		return x;
	}
	/**
	 * Method that set x coordinate of center
	 * @param double
	 */
	public void setX(double x) {
		this.x = x;
	}
	/**
	 * Method that return y coordinate of center
	 * @return double
	 */
	public double getY() {
		return y;
	}
	/**
	 * Method that set y coordinate of center
	 * @param double
	 */
	public void setY(double y) {
		this.y = y;
	}
	/**
	 * Method that return radius of circle
	 * @return double
	 */
	public double getRadius() {
		return radius;
	}
	/**
	 * Method that set radius of circle
	 * @param double
	 */
	public void setRadius(double radius) {
		this.radius = radius;
	}
	/**
	 * Method that return stroke width of circle
	 * @return double
	 
	public double getStrokeWidth() {
		return stroke_width;
	}
	/**
	 * Method that set stroke width of circle
	 * @param double
	 
	public void setStrokeWidth(double stroke_width) {
		this.stroke_width = stroke_width;
	}
	/**
	 * Method that return zoom level
	 * @return byte
	 */
	public byte getZoomLevel() {
		return zoomLevel;
	}
	/**
	 * Method that set requested zoom level
	 * @param byte
	 */
	public void setZoomLevel(byte zoomLevel) {
		this.zoomLevel = zoomLevel;
	}
	/**
	 * Method that return current reference system
	 * @return String
	 */
	public String getSrid() {
		return srid;
	}
	/**
	 * Method that set the reference system
	 * @param String
	 */
	public void setSrid(String srid) {
		this.srid = srid;
	}	
	/* (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	/* (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(x);
		dest.writeDouble(y);
		dest.writeDouble(radius);
		dest.writeByte(zoomLevel);
		dest.writeString(srid);	
	}
	
	public FeatureCircleQuery(Parcel source){
		x=source.readDouble();
		y=source.readDouble();
		radius=source.readDouble();
		zoomLevel=source.readByte();
		srid=source.readString();
	}
	public FeatureCircleQuery(){}
	
	 public static final Parcelable.Creator<FeatureCircleQuery> CREATOR
     = new Parcelable.Creator<FeatureCircleQuery>() {
	 public FeatureCircleQuery createFromParcel(Parcel in) {
	     return new FeatureCircleQuery(in);
	 }

	 public FeatureCircleQuery[] newArray(int size) {
	     return new FeatureCircleQuery[size];
	 }	
	};	
}