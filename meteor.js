
const Tasks = new Mongo.Collection('tasks');

if (Meteor.isClient) {

Template.body.helpers({
 	 tasks: function() {

if (Session.get('hideFinished'))
{
     return Tasks.find({checked: {$ne: true}},{sort: {createdAt: 1}});
   }
     else {
       return Tasks.find();
     }
},
hideFinished: function(){return Session.get('hideFinished')}
});

Template.body.events({
  'submit .newtask'(event){

    //prevent default submit
    event.preventDefault();

    //fetch value
    const target=event.target;
    const text= target.text.value;

    //insertit to db
  Tasks.insert({
    text,
    createdAt: new Date(),

  });
  target.text.value=' ';
},

'change .hide-finished': function(event)
{
Session.set('hideFinished', event.target.checked);

}
})





Template.task.events({
'click .toggle-checked': function(){

Tasks.update(this._id, {$set: {checked: !this.checked}})

},

  'click .delete': function(){
    Tasks.remove(this._id);

}

});
Accounts.ui.config({

  passwordSignupFields: "USERNAME_ONLY"
});
}

//db.meteor_accounts_loginServiceConfiguration.remove({"service":"google"})
//resetting facebook login


if (Meteor.isServer) {
  Meteor.startup(function () {
    // code to run on server at startup
  });
}
