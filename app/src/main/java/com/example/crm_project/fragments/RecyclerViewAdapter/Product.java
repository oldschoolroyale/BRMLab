package com.example.crm_project.fragments.RecyclerViewAdapter;

public class Product {
     String type;
     String address;
     String name;
     String id;
     String visit;
     String time, timeStart, timeEnd, medications;

     public Product() {
     }

     public Product(String type, String address, String name, String id, String visit, String time, String timeStart, String timeEnd, String medications) {
          this.type = type;
          this.address = address;
          this.name = name;
          this.id = id;
          this.visit = visit;
          this.time = time;
          this.timeStart = timeStart;
          this.timeEnd = timeEnd;
          this.medications = medications;
     }

     public String getType() {
          return type;
     }

     public void setType(String type) {
          this.type = type;
     }

     public String getAddress() {
          return address;
     }

     public void setAddress(String address) {
          this.address = address;
     }

     public String getName() {
          return name;
     }

     public void setName(String name) {
          this.name = name;
     }

     public String getId() {
          return id;
     }

     public void setId(String id) {
          this.id = id;
     }

     public String getVisit() {
          return visit;
     }

     public void setVisit(String visit) {
          this.visit = visit;
     }

     public String getTime() {
          return time;
     }

     public void setTime(String time) {
          this.time = time;
     }

     public String getTimeStart() {
          return timeStart;
     }

     public void setTimeStart(String timeStart) {
          this.timeStart = timeStart;
     }

     public String getTimeEnd() {
          return timeEnd;
     }

     public void setTimeEnd(String timeEnd) {
          this.timeEnd = timeEnd;
     }

     public String getMedications() {
          return medications;
     }

     public void setMedications(String medications) {
          this.medications = medications;
     }
}
