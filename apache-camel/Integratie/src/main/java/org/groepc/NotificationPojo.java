package org.groepc;

import java.util.Map;

public class NotificationPojo {
	private String id;
	private String email;
	private String notification_time;
	private String location_start;
	private String location_start_lat;
	private String location_start_lng;
	private String location_end;
	private String location_end_lat;
	private String location_end_lng;
	private String confirmed;
	private String hash;
	private Map<String, String> weather;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNotification_time() {
		return notification_time;
	}

	public void setNotification_time(String notification_time) {
		this.notification_time = notification_time;
	}

	public String getLocation_start() {
		return location_start;
	}

	public void setLocation_start(String location_start) {
		this.location_start = location_start;
	}

	public String getLocation_start_lat() {
		return location_start_lat;
	}

	public void setLocation_start_lat(String location_start_lat) {
		this.location_start_lat = location_start_lat;
	}

	public String getLocation_start_lng() {
		return location_start_lng;
	}

	public void setLocation_start_lng(String location_start_lng) {
		this.location_start_lng = location_start_lng;
	}

	public String getLocation_end() {
		return location_end;
	}

	public void setLocation_end(String location_end) {
		this.location_end = location_end;
	}

	public String getLocation_end_lat() {
		return location_end_lat;
	}

	public void setLocation_end_lat(String location_end_lat) {
		this.location_end_lat = location_end_lat;
	}

	public String getLocation_end_lng() {
		return location_end_lng;
	}

	public void setLocation_end_lng(String location_end_lng) {
		this.location_end_lng = location_end_lng;
	}

	public String getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(String confirmed) {
		this.confirmed = confirmed;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public Map<String, String> getWeather() {
		return weather;
	}

	public void setWeather(Map<String, String> weather) {
		this.weather = weather;
	}
}
