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
        return Tasks.find();
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
        Tasks.update(id, {
            $set: {
                checked: checked
            }
        });
    },

    deleteTask: function(id) {
        Tasks.remove(id);
    }
});
