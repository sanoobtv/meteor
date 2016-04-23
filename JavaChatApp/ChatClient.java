
/**
 * **************************************************************
 * Version	:	1.0 Date	:	03/05/2014
 *
 * Description This is a client side of chat application. This application is
 * used to sending and receiving the messages and in this we can maintain the
 * list of all online users
 *
 * Remarks Before running the client application make sure the server is
 * running.If server is running then only you can execute your application.
 *****************************************************************
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

//create the GUI of the client side
public class ChatClient extends WindowAdapter implements ActionListener {

JFrame frame;
JList list;
JList userList;
JTextField tf;
DefaultListModel model;
DefaultListModel model1;
JButton button, sel, send;
JButton lout;
JScrollPane scrollpane;
JScrollPane userListscroll;
JLabel label, label1, label2, label3, label4, label5;
JTextField saveto;
public static File file;
static JLabel ll;
JRadioButton broadcast, priva;
long login=System.currentTimeMillis();
Socket s, s1, s2;

DataInputStream dataInput;
DataOutputStream dataOut;
DataOutputStream dataOut1;
DataOutputStream dataOut2;
DataInputStream dataInput1;

public String name;

static ArrayList users = new ArrayList();

public ChatClient(String name) throws IOException
{

        frame = new JFrame("<Client Side> Logged in as :" + name);
        tf = new JTextField();
        saveto = new JTextField();
        model = new DefaultListModel();
        model1 = new DefaultListModel();
        label = new JLabel("Message:");
        label1 = new JLabel("Chat Log :");
        label2 = new JLabel("Online Users");
        label5 = new JLabel("File");
        ll = new JLabel();
        label3 = new JLabel("Chat Options:");
        broadcast = new JRadioButton("BraodCast");
        priva = new JRadioButton("Private");
        //to choose between private and broadcast messages.
        ButtonGroup bg = new ButtonGroup();
        bg.add(broadcast);
        bg.add(priva);

        list = new JList(model);
        userList = new JList(model1);

        sel = new JButton("...");
        send = new JButton("Send");
        button = new JButton("Send");
        lout = new JButton("Logout");
        scrollpane = new JScrollPane(list);
        userListscroll = new JScrollPane(userList);
        list.setBackground(Color.cyan);

        userList.setBackground(Color.cyan);
        tf.setBackground(Color.LIGHT_GRAY);
        //adding a panel
        //adding components to the panel
        //then adding panel to the frame

        JPanel panel = new JPanel();

        button.addActionListener(this);
        lout.addActionListener(this);

        panel.add(ll);
        panel.add(tf);
        panel.add(button);
        panel.add(scrollpane);
        panel.add(label);
        panel.add(lout);
        panel.add(userListscroll);
        panel.add(label1);
        panel.add(label2);
        panel.add(label3);
        panel.add(broadcast);
        panel.add(priva);


        label1.setBounds(10, 20, 100, 20);
        scrollpane.setBounds(10, 40, 400, 150);
        label2.setBounds(450, 20, 100, 20);

        userListscroll.setBounds(450, 40, 100, 150);

        label.setBounds(10, 250, 80, 30);
        tf.setBounds(65, 250, 300, 30);

        broadcast.setSelected(true);
        userList.setSelectedIndex(0);

        label3.setBounds(200, 200, 90, 30);
        broadcast.setBounds(280, 200, 90, 30);
        priva.setBounds(390, 200, 90, 30);
        button.setBounds(450, 250, 90, 30);
        lout.setBounds(450, 280, 90, 30);



        frame.add(panel);
        panel.setLayout(null);
        frame.setSize(600, 400);
        frame.setVisible(true);
        this.name = name;
        frame.addWindowListener(this);

        s = new Socket("localhost", 1004);  //creates a socket object
        s1 = new Socket("localhost", 1004);
        s2 = new Socket("localhost", 1004);
        //create inputstream for a particular socket
        dataInput = new DataInputStream(s.getInputStream());
        //create outputstream
        dataOut = new DataOutputStream(s.getOutputStream());


        //sending a message for login
        dataOut.writeUTF(name + " has Logged in");



        dataOut1 = new DataOutputStream(s1.getOutputStream());
        dataOut2 = new DataOutputStream(s2.getOutputStream());
        dataInput1 = new DataInputStream(s2.getInputStream());

        //adding a string named who to get the  username.whichs later is used for private messaging
        String who = name;
        System.out.println("<><>" + who + "<><>");
        //This will be passed to my2 class as a string variable.



        // creating a thread for maintaning the list of user name
        maintainUser m1 = new maintainUser(dataOut2, model1, name, dataInput1);
        Thread t1 = new Thread(m1);
        t1.start();
        //creating a thread for receiving a messages
        messageUser m = new messageUser(dataInput, model, who);
        Thread t = new Thread(m);
        t.start();
}

public void actionPerformed(ActionEvent e) {
        // sendataInputg the messages
        //check wheter the broad cast is selected or not.if selected add it to the chat string and later on use split to do needed in the servver
        if (e.getSource() == button) {
                String str = "";
                String Header = "";
                str = tf.getText();
                if (broadcast.isSelected()) //we are aadding a seperator as ---- so that we can split the message in server side.
                // w
                {
                        Header = "b-";
                        str ="("+ name + " :>all )" + str;
                } else
                {
                        Header = "p-" + userList.getSelectedValue() + "-" + name + "-";
                        str = "("+name + ":> "+userList.getSelectedValue() +")"+ str;
                }
                tf.setText("");

                str = Header + str;
                try {
                        dataOut.writeUTF(str);
                        System.out.println(str);
                        dataOut.flush();
                } catch (IOException ae) {
                        System.out.println(ae);
                }
        }

        // client logout
        if (e.getSource() == lout) {
                frame.dispose();
                try {
                        //printing the time period the user was active in seconds.
                        long logout=System.currentTimeMillis()-login;

                        //sending the message for logout with active session length in seconds
                        dataOut.writeUTF(name + " has Logged out after "+String.valueOf((logout / 1000) % 60)+" seconds");
                        //  users.remove(name);
                        dataOut1.writeUTF(name);
                        dataOut1.flush();
                        Thread.currentThread().sleep(1000);
                        System.exit(1);
                } catch (Exception oe) {
                }
        }
}

public void windowClosing(WindowEvent w) {
        try {
                dataOut1.writeUTF(name);
                dataOut1.flush();
                Thread.currentThread().sleep(1000);
                System.exit(1);
        } catch (Exception oe) {
        }
}
}

// class is used to maintaning the list of user name
class maintainUser implements Runnable {

DataOutputStream dataOut2;
DefaultListModel model1;
DataInputStream dataInput1;
String name, lname;
ArrayList alname = new ArrayList();     //stores the list of user names
ObjectInputStream obj;     // read the list of user names
int i = 0;

maintainUser(DataOutputStream dataOut2, DefaultListModel model1, String name, DataInputStream dataInput1) {
        this.dataOut2 = dataOut2;
        this.model1 = model1;
        this.name = name;
        this.dataInput1 = dataInput1;
}

public void run() {
        try {
                dataOut2.writeUTF(name); // write the user name in output stream
                while (true) {
                        obj = new ObjectInputStream(dataInput1);
                        //read the list of user names
                        alname = (ArrayList) obj.readObject();
                        if (i > 0)
                        {
                                model1.clear();
                        }
                        Iterator i1 = alname.iterator();
                        System.out.println(alname);
                        while (i1.hasNext())
                        {
                                lname = (String) i1.next();
                                i=i+1;
                                //add the user names in list box
                                model1.addElement(lname);
                        }
                        model1.removeElement(name);
                }
        } catch (Exception oe)
        {
                JOptionPane.showMessageDialog(ChatClient.ll, oe.getMessage());
        }
}
}

//class is used to received the messages

class messageUser implements Runnable {

DataInputStream dataInput;
DefaultListModel model;
String who;

messageUser(DataInputStream dataInput, DefaultListModel model, String who) {
        this.dataInput = dataInput;
        this.model = model;
        this.who = who;
}

public void run() {
        String str1 = "";
        while (true) {
                try {

                        str1 = dataInput.readUTF(); // receive the message

                        String temp = str1.toString();

                        if (temp.contains("-"))
                        {
//split the string to get components
                                String part[] = temp.split("-");

                                //check if the first part is 'b' //b for broadcast
                                if (part[0].equals("b"))
                                {
                                        temp = part[1];
                                        model.addElement(temp);
                                }
                                else

                                {
                                        temp = part[3];
                                        //temp = "<to>"+part[2] +"<from>"+ temp + "<>";

                                        if ((((who.equals(part[1])) || (who.equals(part[2])))))
                                        {
                                                model.addElement(temp);

                                        }
                                }
                        }
                        else
                        {
                                model.addElement(temp);
                        }

                        // add the message in list box
                } catch (Exception e) {
                        model.addElement("Select a user from online user List");
                }
        }
}
}
