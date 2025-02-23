/* Walker Caskey
 * i thought converting this into a gui app would be cool and im definentally right but also i held an all nighter for something that did not need to be done at all; i just thought of it. why am i like this. */

 import javax.swing.*;
 import javax.swing.border.EmptyBorder;
 import java.awt.*;
 import javax.sound.sampled.*; // wav playing via javax so we can just import
 
 public class GLGUI extends JFrame { 
     // i hate jframe bro
     // max will be 64 for now
     private static final int MAX_ITEMS = 64; // reminds me of mc modding (I HATE GRADLE!!!!!!!!!!!!)
     
     // grocery stuff (lame)
     private String[] items = new String[MAX_ITEMS];
     private boolean[] checkedOff = new boolean[MAX_ITEMS];
     private int itemCount = 0;
     
     // UI stuff (epic)
     private JTextField inputField; // txt entry
     private JTextArea displayArea;
     private JButton addButton;
     private JButton removeButton;
     private JButton checkOffButton;
     private JButton printListButton; 
     private JButton exitButton;
     
     public GLGUI() { // main window setup stuff 
         setTitle("BAGGY - a Grocery list GUI app.");
   
         ImageIcon icon = new ImageIcon(getClass().getResource("bag.png"));
         setIconImage(icon.getImage());
         setUndecorated(true);
         setBackground(new Color(0,0,0,0));
         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         setSize(600, 600); 
         setLayout(new BorderLayout(10, 10));
         setLocationRelativeTo(null); // center the window on main mon
   
         ImageIcon bgIc = new ImageIcon(getClass().getResource("bg.png"));
   
         JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) { // force the bg img to fit within window size.
                super.paintComponent(g);
                g.drawImage(bgIc.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
         };
         backgroundPanel.setLayout(new BorderLayout(10, 10));
         backgroundPanel.setOpaque(false);
   
         setContentPane(backgroundPanel);
         
         // cntrol panel (BUTTONS AND TEXT FIELD)
         JPanel controlPanel = new JPanel(new GridLayout(2, 3, 40, 15));
         controlPanel.setOpaque(false);

         // button listeners (SETUP)

         inputField = new JTextField();
         inputField.setBackground(new Color(170, 180, 180, 255));
         inputField.setSize(75, 30);

         int buttonsze = 45; // i removed this and it broke (?) likely just gotta restart or sumn idk
         addButton = new JButton("- Add -");
         addButton.setBackground(new Color(60, 180, 255, 255));
         addButton.setSize(45, 45);
         addButton.addActionListener(e -> { // what actually happens when button event triggers
             String text = inputField.getText().trim();
             if (!text.isEmpty()) {
                 addItem(text);
                 inputField.setText("");
                 updateDisplayArea();
             }
         });
         
         removeButton = new JButton("- Remove -");
         removeButton.setBackground(new Color(60, 180, 255, 255));
         removeButton.setSize(45, 45);
         removeButton.addActionListener(e -> { 
             String text = inputField.getText().trim();
             if (!text.isEmpty()) {
                 removeItem(text);
                 inputField.setText("");
                 updateDisplayArea();
             }
         });
         
         checkOffButton = new JButton("- Check Off -");
         checkOffButton.setBackground(new Color(60, 180, 255, 255));
         checkOffButton.setSize(45, 45);
         checkOffButton.addActionListener(e -> { 
             String text = inputField.getText().trim();
             if (!text.isEmpty()) {
                 checkOffItem(text);
                 inputField.setText("");
                 updateDisplayArea();
             }
         });
   
         printListButton = new JButton("+ UPD +");
         printListButton.setBackground(new Color(60, 180, 255, 255));
         printListButton.setSize(45, 45);
         printListButton.addActionListener(e -> {
             updateDisplayArea();
             playUPD();
         });
   
         exitButton = new JButton("! Exit !");
         exitButton.setBackground(new Color(255, 15, 120, 255));
         exitButton.setSize(45, 45);
         exitButton.addActionListener(e -> {
             updateDisplayArea();
             Object[] opt = {"Yes!", "Not yet!"};
             int choi = JOptionPane.showOptionDialog(
                 this,
                 "Are you SURE thats all you need?",
                 "Exit?",
                 JOptionPane.YES_NO_OPTION,
                 JOptionPane.QUESTION_MESSAGE,
                 icon,
                 opt,
                 opt[1]
             );
             if (choi == JOptionPane.YES_OPTION) {
                 dispose(); // threading masterpiece right here ong
                 new Thread(() -> System.exit(0)).start(); 
             }
         });
   
         // arrange components 

         controlPanel.add(addButton);
         controlPanel.add(removeButton);
         controlPanel.add(checkOffButton);
         
         controlPanel.add(printListButton);
         controlPanel.add(exitButton);
         controlPanel.add(inputField);
         controlPanel.setBorder(new EmptyBorder(0,70,50,70));
   
         // display area of list
         displayArea = new JTextArea();
         displayArea.setEditable(false);
         displayArea.setLineWrap(true);
         displayArea.setWrapStyleWord(true);
         displayArea.setMargin(new Insets(0, 0, 30, 0));
         JScrollPane scrollPane = new JScrollPane(displayArea);
         scrollPane.setPreferredSize(new Dimension(120, 120));
   
         JPanel contentPanel = new JPanel();
         contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
         contentPanel.setOpaque(false);
         contentPanel.setBorder(new EmptyBorder(200, 10, 10, 10));
   
         // Add scrollpane and scrollwrapper the scroll wrapper, add wrapper to (centered horizontally) content panel
         JPanel scrollWrapper = new JPanel();
         scrollWrapper.setOpaque(false);
         scrollWrapper.add(scrollPane);
         contentPanel.add(scrollWrapper);
         
         // vert spacer
         contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
         contentPanel.add(controlPanel); // center

         // placing into background , center
         backgroundPanel.add(contentPanel, BorderLayout.CENTER);
     }
     
     private void addItem(String item) { // MUST only add if the item doesnt exist.
         for (int i = 0; i < itemCount; i++) {
             if (items[i].equalsIgnoreCase(item)) { 
                 JOptionPane.showMessageDialog(this, "This already exists though!", "Nope. Error!", JOptionPane.ERROR_MESSAGE);
                 return;
             }
         }
         if (itemCount < MAX_ITEMS) {
             items[itemCount] = item;
             checkedOff[itemCount] = false; // its NOT checked off
             itemCount++;
         } else {JOptionPane.showMessageDialog(this, "LIST IS FULL! Impressive, honestly. Can you even see them all?", "Nope. Error!", JOptionPane.ERROR_MESSAGE);}
     }
     
     private void removeItem(String input) { // remove item from list whether index or name based
         int index = -1;
         try { 
             index = Integer.parseInt(input) - 1; //  0based index
         } catch (NumberFormatException e) {
             for (int i = 0; i < itemCount; i++) {
                 if (items[i].equalsIgnoreCase(input)) { 
                     index = i; 
                     break; 
                 }
             }
         }
         if (index >= 0 && index < itemCount) {
             for (int i = index; i < itemCount - 1; i++) {
                 items[i] = items[i + 1];
                 checkedOff[i] = checkedOff[i + 1];
             }
             itemCount--; // arent i a smart cookie
         } else {JOptionPane.showMessageDialog(this, "Item not found!!", "Nope, Error!!", JOptionPane.ERROR_MESSAGE);}
     }
     
     private void playUPD() { // print button just plays a sound since uh. Yeah. needed something to happen.
         try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(getClass().getResource("upd.wav"))) {
             Clip clip = AudioSystem.getClip();
             clip.open(audioStream);
             if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                 FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                 float VOLL = 1.0f;
                 volumeControl.setValue(VOLL);
             }
             clip.start();
         } catch (Exception e) {JOptionPane.showMessageDialog(this, "![o7]! Error with javax ::: " + e.getMessage(), "Audio Error", JOptionPane.ERROR_MESSAGE);}
     }
     
     private void checkOffItem(String input) { 
         int index = -1;
         try {index = Integer.parseInt(input) - 1;}
         catch (NumberFormatException e) {
             for (int i = 0; i < itemCount; i++) {
                 if (items[i].equalsIgnoreCase(input)) {  // tried some other methods, hated them, running w this
                     index = i; 
                     break; 
                 }
            }
         }
         if (index >= 0 && index < itemCount) {checkedOff[index] = true;} 
         else {JOptionPane.showMessageDialog(this, "Item not found!!", "Nope, Error!!", JOptionPane.ERROR_MESSAGE);}
     }
     
     private void updateDisplayArea() { // every line shows the item num, name, and checked marker
         StringBuilder sb = new StringBuilder();
         for (int i = 0; i < itemCount; i++) {sb.append((i + 1) + " . " + items[i] + " [ " + (checkedOff[i] ? "x" : "-") + " ]\n");}
         displayArea.setText(sb.toString());
     }
     
     public static void main(String[] args) {
         SwingUtilities.invokeLater(() -> { // thread controls
             GLGUI gui = new GLGUI();
             gui.setVisible(true);
         });
     }
 }