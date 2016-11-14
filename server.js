/**
 * http://usejsdoc.org/
 */
var express = require('express');
var http = require('http');
var path = require('path');
var bodyParser = require('body-parser');
var office = require('./routes/office');

var app = express();
app.set('port', process.env.PORT || 8088);
app.use(bodyParser.json());

// url end points for REST API
var basePath = '/smartoffice';
var meetingPath = basePath + '/meetings';
var userPath = basePath + '/users';
var roomPath = basePath + '/rooms';

// REST API calls for meeting CRUD operations
app.get(meetingPath, office.getMeetings);
app.get(meetingPath + '/:id', office.getMeeting);
app.post(meetingPath, office.createMeeting);
app.delete(meetingPath + '/:id', office.deleteMeeting);
app.put(meetingPath + '/:id/topics/:tid', office.addTopicSummary);
app.post(meetingPath + '/:id/topics/:tid/timelines', office.addTimelineItem);

//REST API calls for user CRUD operations
app.get(userPath, office.getUsers);

//REST API calls for room CRUD operations
app.get(roomPath, office.getRooms);

// server to start listening for client calls
app.listen(app.get('port'), function() {
  console.log('Smart Office Server listening on port ' + app.get('port'));
});
