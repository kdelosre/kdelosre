package com.example.izaya.smartoffice;

import java.util.ArrayList;

/**
 * Created by syamamura on 9/27/2016.
 * Edited by Team SHARP on 11/8/2016.
 *
 * Data object to contain Meeting content, currently just id and name.
 * (needed for Retrofit to work)
 */
public class Meeting {

    private long id;
    private String name;
    private String description;
    private long plnStart;
    private long plnEnd;
    private long actStart;
    private long actEnd;
    private ArrayList<User> meetingMembers = new ArrayList();
    private ArrayList<Topic> agendaTopics = new ArrayList();
//    private meetingDashboard = new Dashboard(); //uncomment when Dashboard class is made

//    public Meeting(long meetingId, String name, String description, long plnStart, long plnEnd, long actStart, long actEnd) {
//        this.id = meetingId;
//        this.name = name;
//        this.description = description;
//        this.plnStart = plnStart;
//        this.plnEnd = plnEnd;
//        this.actStart = actStart;
//        this.actEnd = actEnd;
//    }
    public Meeting(long meetingId, String name) {
        this.id = meetingId;
        this.name = name;
    }

    private void addTopic() {

    }

    private void inviteMembers() {
        // for every selected member in the list of all Members (access list from database),
//           add to meetingMembers list
//           return meeting member list
        ArrayList<User> selected = new ArrayList();
//        for

    }

    private void notifyMembers() {

    }

//    private File uploadFile() {
//
//    }
//
//    private Dashboard createDashboard{
//
//    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.id + " - " + this.name;
    }

}


