if (Meteor.isClient) {
    //helper for the template, fetching the current id
    Template.task.helpers({
        isOwner: function() {
                return this.owner === Meteor.userId();
            }
            // the above code is uded to determine the value of the button in the template.
    });
    //tempelate events.
    Template.task.events({
        'click .toggle-checked': function() {
            Meteor.call("updateTask", this._id, !this.checked); //passinf the current id and the negate the checked value.
        },

        'click .delete': function() {
            Meteor.call("deleteTask", this._id);

        },
        'click .toggle-private': function() {
            Meteor.call("setPrivate", this._id, !this.private);
        }

    });
    //use usernames instead of email address
    Accounts.ui.config({
        passwordSignupFields: "USERNAME_ONLY"
    });
}
