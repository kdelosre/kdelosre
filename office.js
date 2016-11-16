/**
 * http://usejsdoc.org/
 */

// MongoDB start-up code
var mongo = require('mongodb');
var Server = mongo.Server;
var Db = mongo.Db;

// connect to local 'officedb' database
// (if not present, this code will create 'officedb' database)
var server = new Server('localhost', 27017, {auto_reconnect: true});
var db = new Db('officedb', server);

// open mongoDB database
// Note 1: prerequisite, need mongoDB currently running from command prompt 'mongod'
// Note 2: to reset all DB entries, remove DB, 'officedb' by dropping all collections
// by commands, db.meetings.drop(), db.users.drop() and db.rooms.drop(). 
// This will kick off the creation of meetings, users and rooms hardcoded in this file.

db.open(function(err, db) {
	if(!err) {
		console.log("Connecting to 'meetings' collection...");
		db.collection('meetings', {strict:true}, function(err, collection) {
			if(err) {
				createMeetings();
			} else {
				if(collection.length == 0) {
					createMeetings();				
				}				
			}
		});
		console.log("Connecting to 'users' collection...");
		db.collection('users', {strict:true}, function(err, collection) {
			if(err) {
				createUsers();
			} else {
				if(collection.length == 0) {
					createUsers();				
				}				
			}
		});
		console.log("Connecting to 'rooms' collection...");
		db.collection('rooms', {strict:true}, function(err, collection) {
			if(err) {
				createRooms();
			} else {
				if(collection.length == 0) {
					createRooms();				
				}				
			}
		});
	}
});

// REST API methods - begin

exports.getMeetings = function(req, res) {
	// parameters passed in the url
	// ex. http://myserver.com/smartoffice/meetings?room=sunset&user=5...
	var room = req.param('room');
	var user = req.param('user');
	var status = req.param('status');
	var start = req.param('start');
	var end = req.param('end');
	//var documents = req.param('documents');  //added
	//var topics = req.param('topics');        //added
	
	db.collection('meetings', function(err, collection) {
		collection.find().toArray(function(err, meetings) {
			var smeetings = [];

			for(var i = 0; i < meetings.length; i++) {	
				var meeting = meetings[i];
				var smeeting = {
						"id": 0,
						"title": "",
						"description": "",
						"plnStart": "",
						"plnEnd": "",
						"actStart": "",
						"actEnd": "",
						"room": 0,
						"status": "",
						"invitedUsers": [], //added
						"documents": [],    //added
						"topics": []       //added
				};
				
				// find room match if specified
				if((room != null) && (room != meeting.room)) {
					continue;
				}
				
				// find specified user, if any, from array of invited users
				if((user != null) && !foundUser(user, meeting.invitedUsers)) {
					continue;
				}
				
				// find status match if specified
				if((status != null) && (status != meeting.status)) {
					continue;
				}
				
				// if start (from) time specified is later than meeting start time,
				// skip this meeting
				if((start != null) && (compareTime(start, meeting.plnStart) > 0)) {
					continue;
				}
				
				// if end (to) time specified is earlier than meeting start time,
				// skip this meeting
				if((end != null) && (compareTime(end, meeting.plnStart) < 0)) {
					continue;
				}
				
				smeeting.id = meeting.id;
				smeeting.title = meeting.title;
				smeeting.description = meeting.description;
				smeeting.plnStart = meeting.plnStart;
				smeeting.plnEnd = meeting.plnEnd;
				smeeting.actStart = meeting.actStart;
				smeeting.actEnd = meeting.actEnd;
				smeeting.room = meeting.room;
				smeeting.status = meeting.status;
				smeeting.invitedUsers = meeting.invitedUsers;   //added
				smeeting.documents = meeting.documents;         //added
				smeeting.topics = meeting.topics;              //added
				
				smeetings.push(smeeting);
			}
			
			res.send(smeetings);
		});
	});
};

// find user id within an array of users
var foundUser = function(user, users) {
	var found = false;
	for(var i = 0; i < users.length; i++) {
		if(user == users[i]) {
			found = true;
			break;
		}
	}	
	return found;
}

// comparing ISO standard times
// time1 < time2 if time1 is earlier than time2
var compareTime = function(time1, time2) {
	var timeObj1 = new Date(time1);
	var timeObj2 = new Date(time2);
	var timeVal1 = timeObj1.getTime();
	var timeVal2 = timeObj2.getTime();
	var compare = 0;
	
	if(timeVal1 < timeVal2) {
		compare = -1;
	} else if(timeVal1 > timeVal2) {
		compare = 1;
	}
	
	return compare;
}

// retrieve a meeting specified by its 'id'
exports.getMeeting = function(req, res) {
	var id = req.params.id;
	db.collection('meetings', function(err, collection) {
		var query = JSON.parse('{"id":' + id + '}');
		collection.findOne(query, function(err, meeting) {
			res.send(meeting);
		});
	});
};

// create new meeting with entire meeting info (in json) passed from client
exports.createMeeting = function(req, res) {
	var meeting = req.body;
	//console.log(req.body);  //test
	db.collection('meetings', function(err, collection) {
		console.log("in collection...");
		collection.insert(meeting, {safe:true}, function(err, result) {
			if(err) {
				res.send({'error': 'Error has occured'});
			} else {
				res.send(meeting);
			}
		});
	});
};

// remove a meeting specified by its 'id'
exports.deleteMeeting = function(req, res) {
	var id = req.params.id;
	db.collection('meetings', function(err, collection) {
		var query = JSON.parse('{"id":' + id + '}');
		collection.remove(query, {safe:true}, function(err, result) {
			if(err) {
				res.send({'error': 'Error has occured'});
			} else {
				res.send(result);
			}
		});
		sendMeetingUpdate("Meeting deleted");
	});		
};

// add text summary for a topic specified
// client must identify meeting id and topic id
// specify 'write' to overwrite previous text, if any or
// 'append' to append specified text to the previous text
exports.addTopicSummary = function(req, res) {
	var id = req.params.id;
	var tid = req.params.tid;
	var body = req.body;
	
	db.collection('meetings', function(err, collection) {
		var query = JSON.parse('{"id":' + id + '}');
		collection.findOne(query, function(err, pmeeting) {
			var meeting = pmeeting;
			var topics = meeting.topics;
			
			for(var i = 0; i < topics.length; i++) {
				var topic = topics[i];
				if(topic.id == tid) {
					if(body.operation == "write") {
						topic.summary = body.summary;
					} else {	// append, add space between for readability
						if(topic.summary == "") {
							topic.summary = body.summary;
						} else {
							topic.summary += ' ' + body.summary;
						}
					}
					break;
				}
			}
			collection.update(query, meeting);
			res.send(meeting);
		});
	});
};

// add timeline item (note, comment or question) to a specified
// timeline within meeting topic.  Client must specify meeting id
// and topic id
exports.addTimelineItem = function(req, res) {
	var id = req.params.id;
	var tid = req.params.tid;
	var body = req.body;
	
	db.collection('meetings', function(err, collection) {
		var query = JSON.parse('{"id":' + id + '}');
		collection.findOne(query, function(err, pmeeting) {
			var meeting = pmeeting;
			var topics = meeting.topics;
			
			for(var i = 0; i < topics.length; i++) {
				var topic = topics[i];
				if(topic.id == tid) {
					topic.timelineItems.push(body);
					break;
				}
			}
			collection.update(query, meeting);
			res.send(meeting);
		});
	});
};

// retrieve a list of users
exports.getUsers = function(req, res) {
	db.collection('users', function(err, collection) {
		collection.find().toArray(function(err, users) {
			res.send(users);
		});
	});
};

// retrieve a list of rooms
exports.getRooms = function(req, res) {
	db.collection('rooms', function(err, collection) {
		collection.find().toArray(function(err, rooms) {
			res.send(rooms);
		});
	});
};

//REST API methods - end

// Hardcoded creation of meetings, users and rooms - just for Proof-of-concept (PoC)

var createMeetings = function() {
	
	var meetings = [
	    {
	    	id: 342,
	    	title: "Smart Office Project Kick-off",
	    	description: "Initial meeting to introduce our project into UCI students",
	    	plnStart: "2016-11-03T09:00:00",
	    	plnEnd: "2016-11-03T10:00:00",
	    	actStart: "2016-11-03T09:01:23",
	    	actEnd: "2016-11-03T10:02:34",
	    	status: "completed",
	    	room: 1,
	    	invitedUsers: [ 1, 2, 3 ],
	    	
	    	documents: [
	    	   {
	    		   title: "Project Overview",
	    		   url: "http://mysharpserver/mydocs/smart_meeting_project/overview.pptx"
	    	   },
	    	   {
	    		   title: "Technical Architecture",
	    		   url: "http://mysharpserver/mydocs/smart_meeting_project/architecture_diagram.pdf"
	    	   }	    	   
	    	],
	    	topics: [
	    	   {
	    		   id: 1,
	    		   title: "Self Introduction",
	    		   plnLength: "10.00",
	    		   start: "“2016-11-03T10:01:18",
	    		   end: "2016-11-03T10:12:55",
	    		   summary: "All members introduced themselves.",
	    		   status: "completed",
	    		   timelineItems: [
	    		      {
	    		    	  id: 1,
	    		    	  category: "note",
	    		    	  title: "Note 1",
	    		    	  description: "This topic is important.  Need to review later",
	    		    	  time: "“2016-11-03T10:14:52"
	    		      },
	    		      {
	    		    	  id: 2,
	    		    	  category: "question",
	    		    	  title: "Question 1",
	    		    	  description: "Is there any related documents for this topic?",
	    		    	  time: "“2016-11-03T10:53:07"
	    		      }	    		      
	    		   ]
	    	   },
	    	   {
	    		   id: 2,
	    		   title: "Project Logistics",
	    		   plnLength: "15.00",
	    		   start: "2016-11-03T10:12:55",
	    		   end: "2016-11-03T10:31:02",
	    		   summary: "Hardware requirements identified.",
	    		   status: "completed",
	    		   timelineItems: [
	    		      {
	    		    	  id: 1,
	    		    	  category: "question",
	    		    	  title: "Note 1",
	    		    	  description: "What software depository we should use?",
	    		    	  time: "“2016-11-03T10:15:24"
	    		      }		      
	    		   ]
	    	   }
	    	]
	    },
	    {
	    	id: 134,
	    	title: "Project X Emergency Meeting",
	    	description: "Emargency meeting required for Project X due to suddon scope changes requested by our sales organization",
	    	plnStart: "2016-11-03T11:00:00",
	    	plnEnd: "2016-11-03T11:30:00",
	    	actStart: "2016-11-03T11:05:16",
	    	actEnd: "",
	    	status: "inprogress",
	    	room: "3",
	    	invitedUsers: [ 3, 4, 5, 6, 7 ],
	    	documents: [
	    	],
	    	topics: [
	    	   {
	    		   id: 1,
	    		   title: "Status Overview",
	    		   plnLength: "10.00",
	    		   start: "2016-11-03T11:01:18",
	    		   end: "",
	    		   summary: "Explanation of nature of mergency by manager",
	    		   status: "inprogress",
	    		   timelineItems: [
	    		      {
	    		    	  id: 1,
	    		    	  category: "note",
	    		    	  title: "Note 1",
	    		    	  description: "I probably have to work on this weekend",
	    		    	  time: "“2016-11-03T11:05:52"
	    		      } 		      
	    		   ]
	    	   },
	    	   {
	    		   id: 2,
	    		   title: "Action Plan",
	    		   plnLength: "20.00",
	    		   start: "",
	    		   end: "",
	    		   summary: "Explanation of nature of mergency by manager",
	    		   status: "scheduled",
	    		   timelineItems: []
	    	   },	    	   
	    	   {
	    		   id: 3,
	    		   title: "Confirmation of task assignments",
	    		   plnLength: "5.00",
	    		   start: "",
	    		   end: "",
	    		   summary: "confirm who does what",
	    		   status: "scheduled",
	    		   timelineItems: []
	    	   }	    	   
	    	]
	    }
	];
	
	console.log("'meetings' collection not found.  recreating 'meetings' collection...");
	
	db.collection('meetings', function(err, collection) {
		collection.insert(meetings, {safe:true}, function(err, result) {});
	});
};

var createUsers = function() {
	
	var users = [
	    {
	    	id: 1,
	    	firstName: "Linda",
	    	lastName: "Lu",
	    	email: "lindal6@uci.edu"
	    },
	    {
	    	id: 2,
	    	firstName: "Tessa",
	    	lastName: "Pew",
	    	email: "TPEW@uci.edu"
	    },
	    {
	    	id: 3,
	    	firstName: "Gaku",
	    	lastName: "Sasaki",
	    	email: "gsasaki@uci.edu"
	    },
	    {
	    	id: 4,
	    	firstName: "Kirstie",
	    	lastName: "Rayes",
	    	email: "kdelosre@uci.edu"
	    },
	    {
	    	id: 5,
	    	firstName: "Kathy",
	    	lastName: "Pang",
	    	email: "kpang1@uci.edu"
	    },
	    {
	    	id: 6,
	    	firstName: "Scott",
	    	lastName: "Lee",
	    	email: "lees@sharplabs.com"
	    },
	    {
	    	id: 7,
	    	firstName: "Shinichi",
	    	lastName: "Yamamura",
	    	email: "syamamura@sharplabs.com"
	    }
	];
	
	console.log("'users' collection not found.  recreating 'users' collection...");
	
	db.collection('users', function(err, collection) {
		collection.insert(users, {safe:true}, function(err, result) {});
	});
};

var createRooms = function() {
	
	var rooms = [
	    {
	    	id: 1,
	    	name: "Sunset"
	    },
	    {
	    	id: 2,
	    	name: "Catalina",
	    },
	    {
	    	id: 3,
	    	name: "Laguna",
	    }
	];
	
	console.log("'rooms' collection not found.  recreating 'rooms' collection...");
	
	db.collection('rooms', function(err, collection) {
		collection.insert(rooms, {safe:true}, function(err, result) {});
	});
};
