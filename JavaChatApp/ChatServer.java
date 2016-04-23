


/**
 * **************************************************************
 *                    $$ 03/05/2014 $$
 *
 * Description This is a Server Side application of Chat System. This
 * application is used for receiving the messages from any client and send to
 * each and every client and in this we can maintain the list of  online
 * users.
 * we are using extra classes to obtain the online user list.
 * we have classes for handling messages,handling online users and updating list of online users.
 *
* Remarks This application is also able  to provide the private chatting facility
*****************************************************************
 */
//package chatobject;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.ScrollPane;
import java.io.*;
import java.net.*;
import java.time.Clock;
import java.util.*;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class ChatServer {

    ServerSocket ss;
    Socket s;
//array list to maintain
    ArrayList message = new ArrayList();//maintain messages
    ArrayList update = new ArrayList();//maintain the list of username for update purposes if some one login or logout
    ArrayList maintain = new ArrayList();//to keep list of usernames
int count=0;
//the gui components and their initialisation
    static JFrame frame = new JFrame("Chat Server");
    static JTextArea ta = new JTextArea(30, 30);
    //millisec to calculate server up time.
    static long  time=System.currentTimeMillis();
    static int onlineusers=0;

    //sccrollpane ffor chat history
     JScrollPane sc = new JScrollPane(list);
     JLabel l = new JLabel("Chat Log");
     JLabel on=new JLabel("Online Users");
     static JLabel brm,prm;

    static JLabel ol=new JLabel();
    static JLabel l1=new JLabel();
    static JList list;
    JLabel totalusers=new JLabel();

    static DefaultListModel model;
    static JScrollPane scrollPane;

    static DefaultListModel model1;
    static JScrollPane scrollPane1;
    static JList list2;
    static int bmsg=0;
    static int pmsg=0;
    static int online=0;
    ArrayList messagename = new ArrayList();
    Socket s1, s2;
    //panel is added for storing server staticitics

static boolean ch;
    ChatServer() throws IOException

    {

  ch=false;
  frame.setLayout(null);
  frame.setBounds(200, 20, 550, 600);
brm=new JLabel();
prm=new JLabel();

//initialising gui components and setting size and bounds
            model = new DefaultListModel();
            list = new JList(model);
            scrollPane = new JScrollPane(list);
            scrollPane.setSize(400, 200);
            scrollPane.setBackground(Color.yellow);
            list.setBackground(Color.yellow);

            model1 = new DefaultListModel();
            list2 = new JList(model1);
            scrollPane1 = new JScrollPane(list2);
            scrollPane1.setSize(300, 200);
            scrollPane1.setBackground(Color.blue);
            list2.setBackground(Color.white);
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            //adding components to the frame
            frame.add(scrollPane);
            frame.add(scrollPane1);
            frame.add(totalusers);
            frame.add(l1);
            frame.add(ol);
            frame.add(l);
            frame.add(on);
            frame.add(brm);
            frame.add(prm);

            frame.setBackground(Color.BLUE);
            //possitioning the components on the frame using x and y axis.
             l.setBounds(10,10,100,20);
            scrollPane.setBounds(10, 30, 300, 400);
            scrollPane1.setBounds(350,115,150,300);
            totalusers.setBounds(310, 5, 200, 10);
            l1.setBounds(310, 40, 200, 10);
             ol.setBounds(310, 60, 200, 10);
             on.setBounds(370, 80, 100, 50);
             brm.setBounds(250, 440, 200, 10);
             prm.setBounds(250, 470, 300, 10);
           // frame.pack();


            //counting the number of registered users buy traversing the login file.Enc.doc
            try (BufferedReader br = new BufferedReader(new FileReader("Enc.doc"))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null)
            {
                count = count + 1;
            }}

        catch (IOException e) {
            JOptionPane.showMessageDialog(ChatServer.l1, "Cannot connect to server file");
        }//setting the tota no of reisterd users
             totalusers.setText("Total No of registered Users :"+String.valueOf(count));


        ss = new ServerSocket(1004);	// create server socket
        while (true) {


            s = ss.accept();	//accept the client socket
            s1 = ss.accept();
            s2 = ss.accept();
                                    frame.setVisible(true);
++online;
            message.add(s);	// add the client socket in arraylist
            update.add(s1);
            maintain.add(s2);
            ///  l.setBounds(10, 10, 100, 100);
            //setting layout for the frames and panels
            System.out.println("Client is Connected");


            maintaint m = new maintaint(s2, maintain, messagename); //new thread for maintaning the list of user name
            Thread t2 = new Thread(m);
            t2.start();

            messaget r = new messaget(s, message);//new thread for receive and sending the messages
            Thread t = new Thread(r);
            t.start();

            updatet my = new updatet(s1, update, s, s2); // new thread for update the list of user name
            Thread t1 = new Thread(my);
            t1.start();
        }
    }

    public static void main(String[] args) {
        try {
          new ChatServer();
        } catch (IOException e) {
        }
    }
}
//class is used to update the list of user name

class updatet implements Runnable {

    Socket s1, s, s2;
    static ArrayList update;
    DataInputStream ddin;
    String sname;

    updatet(Socket s1, ArrayList update, Socket s, Socket s2) {
        this.s1 = s1;
        this.update = update;
        this.s = s;
        this.s2 = s2;
    }

    public void run() {
        try {
            ddin = new DataInputStream(s1.getInputStream());
            while (true) {
                sname = ddin.readUTF();
                System.out.println("Exit  :" + sname);
         --ChatServer.online;
                maintaint.messagename.remove(sname);//remove the logout user name from arraylist
                maintaint.every();
                update.remove(s1);
                messaget.message.remove(s);
                maintaint.maintain.remove(s2);

                if (update.isEmpty()) {
                    System.exit(0); // client has been logout
                }
            }
        } catch (Exception ie) {
        }
    }
}

// class is used to maintain the list of messagel online users
class maintaint implements Runnable {

    Socket s2;
    static ArrayList maintain;
    static ArrayList messagename;
    static DataInputStream din1;
    static DataOutputStream dout1;

    maintaint(Socket s2, ArrayList maintain, ArrayList messagename) {
        this.s2 = s2;
        this.maintain = maintain;
        this.messagename = messagename;
    }

    public void run() {

            //code added to see wheter we can get the broadcast message in the list of online users.....@@
        //messagename.add("Broadcast");
        try {
            din1 = new DataInputStream(s2.getInputStream());
            messagename.add(din1.readUTF());	// store the user name in arraylist
            //setting the number of online users by getting the length of array list that store active usernames
            every();
        } catch (Exception oe) {
            System.out.println("Main expression" + oe);
        }
    }

    // send the list of user name to messagel client
    static void every() throws Exception
    {
        //removing all elements in online users which will be added later.in case if a user logout from the session
        //we remove all the elements in the list and add the updated list again.
        ChatServer.model1.removeAllElements();
        Iterator i1 = maintain.iterator();
        Socket st1;

        while (i1.hasNext()) {
            st1 = (Socket) i1.next();
            dout1 = new DataOutputStream(st1.getOutputStream());
            ObjectOutputStream obj = new ObjectOutputStream(dout1);
            obj.writeObject(messagename); //write the list of users in stream of clients
            dout1.flush();
            obj.flush();
        }
        //using an iterator to go through the active user list and add it to the online user list on server side.
        Iterator i2=messagename.iterator();
        while(i2.hasNext())
        {
        ChatServer.model1.addElement(i2.next());
        }
                    ChatServer.ol.setText("The number of online users:"+String.valueOf(ChatServer.online));

    }

}
//class is used to receive the message and send  message to clients

class messaget implements Runnable {

    Socket s;
    static ArrayList message;
    DataInputStream din;
    DataOutputStream dout;

    messaget(Socket s, ArrayList message) {
        this.s = s;
        this.message = message;
    }

    public void run() {
        String str;
        int i = 1;
        try {
            din = new DataInputStream(s.getInputStream());
        } catch (Exception e) {
        }

        while (i == 1) {
            try {

                str = din.readUTF(); //read the message

                String temp = str.toString();
                //splitting the message to see if broad cast or private
                //if it contains the message ,it is extracted and added to the chatserver gui
                if (temp.contains("-")) {

                    String part[] = temp.split("-");
                    if (part[0].equals("b")) {
                        temp = part[1];
                       ++ ChatServer.bmsg;
                       ChatServer.brm.setText("Total number of broadcasts:"+String.valueOf(ChatServer.bmsg));
                    } else {
                        temp = part[3];
                        ++ChatServer.pmsg;
                    ChatServer.prm.setText("Total number of Private Messages:"+String.valueOf(ChatServer.pmsg));
                    }

                }

                //adding the message to chat log on the server side.
                ChatServer.model.addElement("<" + temp + ">");
                //to obtain seconds from milli seconds and update server uptime.its refreshed every time the server gets a message from client.
                long now = System.currentTimeMillis() - ChatServer.time;
                now = (now / 1000) % 60;
                String ak = String.valueOf(now);
                //setting server uptime
                ChatServer.l1.setText("Server Uptime: " + ak + "Seconds");
                //broadcasting
                distribute(str);

            } catch (IOException e) {
            }
        }

    }

    // send it to messagel clients
    //THIS FUNCTION IS USED TO SEND THE MESSAGE TO EVERY CLIENT.

    public void distribute(String str) throws IOException {
        Iterator i = message.iterator();
        Socket st;
        while (i.hasNext()) {
            st = (Socket) i.next();
            //checking the socket details
            dout = new DataOutputStream(st.getOutputStream());
            dout.writeUTF(str);
            dout.flush();
        }
    }
}
