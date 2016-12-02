package com.anthony;
import java.util.Date;
import java.util.LinkedList;

public class Ticket {

    private int priority;
    private String reporter; //Stores person or department who reported issue
    private String description;
    private Date dateReported;
    private Date resolutionDate;
    private String resolutionDescription;


    //STATIC Counter - accessible to all Ticket objects.
    //If any Ticket object modifies this counter, all Ticket objects will have the modified value
    //Make it private - only Ticket objects should have access
    private static int staticTicketIDCounter = 1;

    public void setTicketID(int ticketID) {
        this.ticketID = ticketID;
    }

    //The ID for each ticket - instance variable. Each Ticket will have it's own ticketID variable
    private int ticketID;

    public Ticket(String desc, int p, String rep, Date date) {
        this.description = desc;
        this.priority = p;
        this.reporter = rep;
        this.dateReported = date;
        this.ticketID = staticTicketIDCounter;
        staticTicketIDCounter++;
    }

    public Ticket(String desc, int p, String rep, Date date, int tickID){
        this.description = desc;
        this.priority = p;
        this.reporter = rep;
        this.dateReported = date;
        this.ticketID = tickID;
    }

    protected int getPriority() {
        return priority;
    }

    public String toString(){
        return("ID= " + this.ticketID + " Issue: " + this.description + " Priority: " + this.priority + " Reported by: "
                + this.reporter + " Reported on: " + this.dateReported);
    }
    public int getTicketID(){
        return this.ticketID;
    }
    public String getDescription(){
        return this.description;
    }
    public String getReporter(){
        return this.reporter;
    }

    public Date getResolutionDate() {
        return resolutionDate;
    }

    public String getResolutionDescription() {
        return resolutionDescription;
    }

    public void setResolutionDate(Date resolutionDate) {
        this.resolutionDate = resolutionDate;
    }

    public void setResolutionDescription(String resolutionDescription) {
        this.resolutionDescription = resolutionDescription;
    }
    public int getStaticTicketIDCounter(){
        return staticTicketIDCounter;
    }
    public void setStaticTicketIDCounter(int start){
        this.staticTicketIDCounter = start;
    }

    public Date getDateReported() {
        return dateReported;
    }
}

