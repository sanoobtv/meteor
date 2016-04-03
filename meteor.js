
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


}

if (Meteor.isServer) {
  Meteor.startup(function () {
    // code to run on server at startup
  });
}
