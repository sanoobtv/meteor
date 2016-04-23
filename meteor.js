


const Tasks = new Mongo.Collection('tasks');

if (Meteor.isClient) {
  //remove auto publish package from meteor app
  //subscribing to data from publications.
  //without the publish method in the server part, we wont be able to retrive data from the collection.

    Meteor.subscribe("tasks");
    Template.body.helpers({
        tasks: function() {
            //if a session variable is found. return have not  checked tasks sorted.
            if (Session.get('hideFinished')) {
                return Tasks.find({
                    checked: {
                        $ne: true
                    }
                }, {
                    sort: {
                        createdAt: 1
                    }
                });
            } else {
                return Tasks.find();
            }
        },
        //defining the hideFinished as a helper , to be used in the HTML file
        hideFinished: function() {
            return Session.get('hideFinished')
        }
    });

    //events happening in the body and not template.

    Template.body.events({
        'submit .newtask' (event) {

            //prevent default submit
            event.preventDefault();
            //fetch value
            const target = event.target; //form element
            const text = target.text.value; //extract text from form
            //insertit to db
            Meteor.call("addTask", text); //call meteor meathod
            target.text.value = ' ';
        },
        //look for a change event, hide-finished. A session variable is created(if not created) and set to the target value.
        //hide-finished is a session variable storing the checked value.
           'change .hide-finished': function(event) {
            Session.set('hideFinished', event.target.checked);
        }
    })
}
//db.meteor_accounts_loginServiceConfiguration.remove({"service":"facebook"})
//resetting facebook login, run above command in Mongo shell


if (Meteor.isServer) {
    Meteor.startup(function() {
        // code to run on server at startup

    });
    Meteor.publish("tasks", function tasksPublication() {
      //listing all the task on the main page, an or condition is used.
      //tasks is the name of the Collection

        return Tasks.find ({
          $or: [
            {
              private: {$ne: true}
            },
            {
              owner:this.userId
            }
          ]
        })
    });
}
//methods that can acess the db, to prevent console insert.
//only methods listed here can access database, the insecure package has been removed from project package.
Meteor.methods({
    addTask: function(text) {
      //simple insert function, insert text,date and owner.
      Tasks.insert({
          text: text,
          createdAt: new Date(), //current time
          owner: Meteor.userId()
      });
  },

    updateTask: function(id, checked) {
    var tempTask = Tasks.findOne(id);
    //the id of the task along with the checked status is passed on to the function.
    //the owners id is compared against the actual id
    if (tempTask.owner !== Meteor.userId()) {
        throw new Meteor.Error('no');
    }
    //for updating the task list pass id and update the checked field.
    Tasks.update(id, {
        $set: {
            checked: checked
        }
    });
},

    deleteTask: function(id) {
      var tempTask = Tasks.findOne(id);
      //confirming authority then call the remove function
      if (tempTask.owner !== Meteor.userId()) {
          throw new Meteor.Error('no');
      }

      Tasks.remove(id);
  },
    //function to set if the task is to be made private or not. id is passed along with the state of the text obtained from the button.
    setPrivate: function(id, private) {
      var tempTask = Tasks.findOne(id);
      if (tempTask.owner !== Meteor.userId()) {
          throw new Meteor.Error('no');
      }
      Tasks.update(id, {
          $set: {
              private: private
          }
      });
  }
});
