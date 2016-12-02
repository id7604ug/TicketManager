package com.anthony;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;

public class TicketManager {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        LinkedList<Ticket> ticketQueue = new LinkedList<>(); // List for open tickets
        LinkedList<Ticket> resolvedTickets = new LinkedList<>(); // List for resolved tickets
        try(BufferedReader readQueue = new BufferedReader(new FileReader("queueFile.txt"))) {
            readFile(ticketQueue, readQueue);
        } catch (IOException e){
            System.out.println("There was a problem reading the queueFile.txt file");
        }
        int task;
        while(true){
            // Display menu
            System.out.println("1. Enter Ticket\n2. Delete by ID\n3. Delete by Issue\n4. Search by Name\n5. Display All Tickets\n6. Quit");
            task = scan.nextInt();
            if (task == 1) {
                //Call addTickets, which will let us enter any number of new tickets
                addTickets(ticketQueue);
            } else if (task == 2) { // Delete by ID
                deleteByID(ticketQueue, resolvedTickets);
            } else if (task == 3){ // Delete by Issue
                deleteByIssue(ticketQueue, resolvedTickets);
            } else if (task == 4){ // Search by name
                searchByName(ticketQueue, resolvedTickets);
            }else if(task == 5){
                printAllTickets(ticketQueue); // display all tickets
            } else if( task == 6 ) {
                //Quit. Future prototype may want to save all tickets to a file
                System.out.println("Quitting program");
                writeFile(resolvedTickets, ticketQueue); // Call method to write data
                break;
            }
            else {
                //TODO Program crashes if you enter anything else - please fix
                //Default will be print all tickets
                printAllTickets(ticketQueue);
            }

        }
        scan.close();
    }


    // Method to read data from a file
    private static void readFile(LinkedList<Ticket> ticketQueue, BufferedReader readQueue) throws IOException {
        Ticket newTic = new Ticket("", 0, "", new Date()); // Create temp ticket to set the start ticket ID counter
        newTic.setStaticTicketIDCounter(Integer.parseInt(readQueue.readLine()));
        String readLine = readQueue.readLine();
        while(true){
            String description;
            int priority;
            String rep;
            Date date;
            if (readLine == null){
                break;
            } else {
                String[] readLineList = readLine.split(";");
                Date importDate = new Date();
                Ticket t = new Ticket(readLineList[1], Integer.parseInt(readLineList[2]), readLineList[3], new Date(), Integer.parseInt(readLineList[0]));
                ticketQueue.add(t); // Add ticket to ticket queue
                readLine = readQueue.readLine();
            }

        }
    }


    // Method to write out the programs data to files
    private static void writeFile(LinkedList<Ticket> resolvedTickets, LinkedList<Ticket> ticketQueue) {
        try {
            // http://docs.oracle.com/javase/7/docs/api/java/util/Date.html
            File writeFileName = new File("Resolved_tickets_as_of_"
                    + Calendar.MONTH + "_" + Calendar.DAY_OF_MONTH + "_" + Calendar.YEAR + ".txt");
            BufferedWriter writeResolvedTickets = new BufferedWriter((new FileWriter(writeFileName)));
            for (Ticket t : resolvedTickets) {
                writeResolvedTickets.write(t.toString() + "\n"); // Write data line by line
            }
            writeResolvedTickets.close(); // Close writer
        // ---------
            File writeFileName2 = new File("queueFile.txt"); // File name and location
            BufferedWriter writeQueueFile = new BufferedWriter(new FileWriter(writeFileName2));
            writeQueueFile.write(ticketQueue.get(ticketQueue.size() - 1).getStaticTicketIDCounter() + "\n"); // Writes last staticTicketIDCounter
            for (Ticket t2 : ticketQueue) {
                writeQueueFile.write(t2.getTicketID() + ";" + t2.getDescription() + ";" + t2.getPriority()
                        + ";" + t2.getReporter() + ";" + t2.getDateReported() + ";" + t2.getResolutionDate() + ";"
                        + t2.getResolutionDescription() + "\n"); // Writes each ticket to a new line in the file
            }
            writeQueueFile.close();
        } catch (IOException e) {
            System.out.println("There was a problem writing data to the file.");
        }
    }


    // Method to search by rep name
    private static void searchByName(LinkedList<Ticket> ticketQueue, LinkedList<Ticket> resolvedTickets) {
        Scanner scan = new Scanner(System.in);
        LinkedList<Ticket> ticketQuery = new LinkedList<>();
        System.out.println("Whose name would you like to search for?");
        String nameQuery = scan.nextLine();
        for (Ticket t: ticketQueue) {
            if (t.getReporter().equalsIgnoreCase(nameQuery)){
                ticketQuery.add(t);
            }
        }
        deleteTicket(ticketQueue, ticketQuery, resolvedTickets);
    }


    // Method to delete tickets by ID
    private  static void deleteByID(LinkedList<Ticket> ticketQueue, LinkedList<Ticket> resolvedTickets){
        Scanner scan = new Scanner(System.in);
        printAllTickets(ticketQueue);   //display list for user
        if (ticketQueue.size() == 0) {    //no tickets!
            System.out.println("No tickets to delete!\n");
            return;
        }
        deleteTicket(ticketQueue, ticketQueue, resolvedTickets);
        scan.close();
    }


    // Method to delete tickets based on the issue
    private static void deleteByIssue(LinkedList<Ticket> ticketQueue, LinkedList<Ticket> resolvedTickets) {
        Scanner scan = new Scanner(System.in);
        LinkedList<Ticket> ticketQuery = new LinkedList<>(); // Create query result list
        System.out.println("What issue would you like to search for?");
        String issue = scan.nextLine();
        for (Ticket t : ticketQueue) {
            if (t.getDescription().contains(issue)){
                ticketQuery.add(t);
            }
        }
        printAllTickets(ticketQuery);
        if (ticketQuery.size() == 0) { //no tickets!
            System.out.println("No tickets to delete!\n");
            return;
        }
        deleteTicket(ticketQueue, ticketQuery, resolvedTickets);
        scan.close();
    }


    // Method to take two lists of tickets and delete the queried ID from the ticket query
    private static void deleteTicket(LinkedList<Ticket> ticketQueue, LinkedList<Ticket> ticketQuery, LinkedList<Ticket> resolvedTickets) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter ID of ticket to delete");
        int deleteID;
        while (true) {
            while (true) {
                try {
                    deleteID = Integer.parseInt(scan.nextLine());
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid integer.");
                }
            }
            //Loop over all tickets. Delete the one with the ticket ID
            boolean found = false;
            for (Ticket ticket : ticketQuery) {
                if (ticket.getTicketID() == deleteID) {
                    found = true;
                    System.out.println("How was the ticket resolved?");
                    ticket.setResolutionDescription(scan.nextLine()); // Set resolution of removed ticket
                    ticket.setResolutionDate(new Date()); // Set resolution to today's date
                    resolvedTickets.add(ticket); // Add resolved ticket to list
                    ticketQueue.remove(ticket);
                    System.out.println(String.format("Ticket %d deleted", deleteID));
                    break; //don't need loop any more.
                }
            }
            if (found){
                break; // Break from main while
            }
            System.out.println("Ticket ID not found, no ticket deleted");
            System.out.println("Enter ID of ticket again");
        }
        scan.close();
    }
    // Method to add tickets
    private static void addTickets(LinkedList<Ticket> ticketQueue) {
        Scanner sc = new Scanner(System.in);

        boolean moreProblems = true;
        String description;
        String reporter;
        //let's assume all tickets are created today, for testing. We can change this later if needed
        Date dateReported = new Date(); //Default constructor creates date with current date/time
        int priority;

        while (moreProblems){
            System.out.println("Enter problem");
            description = sc.nextLine();
            System.out.println("Who reported this issue?");
            reporter = sc.nextLine();
            System.out.println("Enter priority of " + description);
            while (true) {
                try {
                    priority = Integer.parseInt(sc.nextLine());
                    break; // Break loop to get valid input
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid integer");
                }
            }
            Ticket t = new Ticket(description, priority, reporter, dateReported);
            addTicketInPriorityOrder(ticketQueue, t);

            //To test, let's print out all of the currently stored tickets
            printAllTickets(ticketQueue);

            System.out.println("More tickets to add?");
            String more = sc.nextLine();
            if (more.equalsIgnoreCase("N")) {
                moreProblems = false;
            }
        }

    }


    // Method to add tickets in priority order
    private static void addTicketInPriorityOrder(LinkedList<Ticket> tickets, Ticket newTicket){

        //Logic: assume the list is either empty or sorted

        if (tickets.size() == 0 ) {//Special case - if list is empty, add ticket and return
            tickets.add(newTicket);
            return;
        }

        //Tickets with the HIGHEST priority number go at the front of the list. (e.g. 5=server on fire)
        //Tickets with the LOWEST value of their priority number (so the lowest priority) go at the end

        int newTicketPriority = newTicket.getPriority();

        for (int x = 0; x < tickets.size() ; x++) {    //use a regular for loop so we know which element we are looking at

            //if newTicket is higher or equal priority than the this element, add it in front of this one, and return
            if (newTicketPriority >= tickets.get(x).getPriority()) {
                tickets.add(x, newTicket);
                return;
            }
        }

        //Will only get here if the ticket is not added in the loop
        //If that happens, it must be lower priority than all other tickets. So, add to the end.
        tickets.addLast(newTicket);
    }


    // Method to print each ticket in the sent list
    private static void printAllTickets(LinkedList<Ticket> tickets) {
        System.out.println(" ------- All open tickets ----------");
        for (Ticket t : tickets ) {
            System.out.println(t); //Write a toString method in Ticket class
            //println will try to call toString on its argument
        }
        System.out.println(" ------- End of ticket list ----------");

    }
}