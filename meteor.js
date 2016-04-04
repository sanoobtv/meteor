
const Tasks = new Mongo.Collection('tasks');

if (Meteor.isClient) {

Template.body.helpers({
 	 tasks: function() { return Tasks.find({},{sort: {createdAt: 1}}); }

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

}

if (Meteor.isServer) {
  Meteor.startup(function () {
    // code to run on server at startup
  });
}
