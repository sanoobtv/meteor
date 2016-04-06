const Tasks = new Mongo.Collection('tasks');

if (Meteor.isClient) {
    Meteor.subscribe("tasks");
    Template.body.helpers({
        tasks: function() {

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
        hideFinished: function() {
            return Session.get('hideFinished')
        }
    });
Template.task.helpers({
isOwner: function(){
  return this.owner===Meteor.userId();
}

});
    Template.body.events({
        'submit .newtask' (event) {

            //prevent default submit
            event.preventDefault();
            //fetch value
            const target = event.target;
            const text = target.text.value;
            //insertit to db
            Meteor.call("addTask", text);
            target.text.value = ' ';
        },

        'change .hide-finished': function(event) {
            Session.set('hideFinished', event.target.checked);
        }
    })

    Template.task.events({
        'click .toggle-checked': function() {
            Meteor.call("updateTask", this._id, !this.checked);
        },

        'click .delete': function() {
            Meteor.call("deleteTask", this._id);

        },
        'click .toggle-private': function() {
            Meteor.call("setPrivate", this._id, !this.private);
        }

    });
    Accounts.ui.config({

        passwordSignupFields: "USERNAME_ONLY"
    });
}

//db.meteor_accounts_loginServiceConfiguration.remove({"service":"google"})
//resetting facebook login


if (Meteor.isServer) {
    Meteor.startup(function() {
        // code to run on server at startup

    });
    Meteor.publish("tasks", function tasksPublication() {
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
Meteor.methods({
    addTask: function(text) {
        Tasks.insert({
            text: text,
            createdAt: new Date(),
            owner: Meteor.userId()
        });
    },

    updateTask: function(id, checked) {
      var tk = Tasks.findOne(id);

      if (tk.owner !== Meteor.userId())
      {
          throw new Meteor.Error('no');
      }
        Tasks.update(id, {$set: {checked: checked}
        });
    },

    deleteTask: function(id) {
      var tk = Tasks.findOne(id);

      if (tk.owner !== Meteor.userId())
      {
          throw new Meteor.Error('no');
      }

        Tasks.remove(id);
    },

    setPrivate: function(id, private)
    {
        var tk = Tasks.findOne(id);
        if (tk.owner !== Meteor.userId())
        {
            throw new Meteor.Error('no');
        }
        Tasks.update(id, {$set: {       private: private                }});


      }
});
