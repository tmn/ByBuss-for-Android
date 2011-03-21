package net.trimn.bybuss;

public class Holdeplass {
	private String name;
	private double latitude;
	private double longitude;
	
	public Holdeplass(String name, double latitude, double longitude) {
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public Holdeplass() {
		// TODO Auto-generated constructor stub
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setLatitude(double lat) {
		this.latitude = lat;
	}
	
	public void setLongtitude(double lon) {
		this.longitude = lon;
	}

	public String getName() {
		return this.name;
	}
	
	public double getLat() {
		return this.latitude;
	}
	
	public double getLon() {
		return this.longitude;
	}
	
	@Override
	public String toString() {
		return "Name: " + name + "\nLatitude: " + latitude + "\nLongitude: " + longitude;
	}
}
