package com.brm.uz.models;

import java.util.Comparator;

public class ProductPOJO {
     String type;
     String address;
     String name;
     String id;
     String visit;
     String time, timeStart, timeEnd, medications;
     String comment;

     public ProductPOJO() {
     }

     public ProductPOJO(String type, String address, String name, String id, String visit, String time, String timeStart, String timeEnd, String medications, String comment) {
          this.type = type;
          this.address = address;
          this.name = name;
          this.id = id;
          this.visit = visit;
          this.time = time;
          this.timeStart = timeStart;
          this.timeEnd = timeEnd;
          this.medications = medications;
          this.comment = comment;
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

     public String getComment() {
          return comment;
     }

     public void setComment(String comment) {
          this.comment = comment;
     }

     public static Comparator<ProductPOJO> getVisitType() {
          return visitType;
     }

     public static void setVisitType(Comparator<ProductPOJO> visitType) {
          ProductPOJO.visitType = visitType;
     }

     public static Comparator<ProductPOJO> getVisitTime() {
          return visitTime;
     }

     public static void setVisitTime(Comparator<ProductPOJO> visitTime) {
          ProductPOJO.visitTime = visitTime;
     }

     public void setMedications(String medications) {
          this.medications = medications;
     }
     public static Comparator<ProductPOJO> visitType = new Comparator<ProductPOJO>() {
          @Override
          public int compare(ProductPOJO o1, ProductPOJO o2) {
               return o1.getType().compareTo(o2.getType());
          }
     };
     public static Comparator<ProductPOJO> visitTime = new Comparator<ProductPOJO>() {
          @Override
          public int compare(ProductPOJO o1, ProductPOJO o2) {
               return o1.getTime().compareTo(o2.getTime());
          }
     };
}
