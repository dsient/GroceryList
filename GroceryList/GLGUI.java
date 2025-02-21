/* Walker Caskey
 * i decided to use a gui to challenge myself a lil and also because ive been in intro java courses since genuinely 8th grade so i gotta have SOME sort of fun here.
 * update: i thought converting this into a gui app would be cool and im definentally right but also i held an all nighter for something that did not need to be done at all; i just thought of it. why am i like this. */

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
     
 
     public GLGUI() {
         //main frame setup
         setTitle("BAGGY - a Grocery list GUI app.");
 
         ImageIcon icon = new ImageIcon(getClass().getResource("bag.png"));
         setIconImage(icon.getImage());
         setUndecorated(true);
         setBackground(new Color(0,0,0,0));
         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         setSize(600, 400);
         setLayout(new BorderLayout(10, 10));
         setLocationRelativeTo(null); // center main mon
 
         ImageIcon bgIc = new ImageIcon(getClass().getResource("bg.png"));

         JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) { // all this does is force the bg img to fit within window size.
                super.paintComponent(g);
                g.drawImage(bgIc.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new BorderLayout(10, 10));
        backgroundPanel.setOpaque(false);



        setContentPane(backgroundPanel);

         // left panel hold buttons n text field
         JPanel leftPanel = new JPanel();
         leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
         leftPanel.setBorder(new EmptyBorder(12, 20, 10, 10)); // border for the layout
         leftPanel.setOpaque(true);
         leftPanel.setBackground(new Color(255,255,255,0));
         
         //adding inputfields
         inputField = new JTextField();
         inputField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
         leftPanel.add(inputField);
         leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
         
         
         JPanel buttonPanel = new JPanel(); // create vert stacked panel
         buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS)); // set button panel to use BoxLayout, w/ 5 px spacers! Im not hardcoding these.
         buttonPanel.setOpaque(true);
         buttonPanel.setBackground(new Color(255,255,255,0));
         
         addButton = new JButton("- Add -");
         removeButton = new JButton("- Remove -");
         checkOffButton = new JButton("- Check Off -");
         printListButton = new JButton("+ UPD +"); //  "prints" the values of the list constantly since gui lol (any btton updates the screen)
         exitButton = new JButton("! Exit !");
 
 
         // button listeners
         addButton.addActionListener(e -> { // basically, the arrow just means "was acted upon", so for e, when the addButton is pressed, text gets taken from the field and shoved into string 'text' w trimming for ease of use
             String text = inputField.getText().trim();
             if (!text.isEmpty()) { //only adds text to array 1 if the field has characters
                 addItem(text);
                 inputField.setText(""); // reset text field
                 updateDisplayArea(); // update the user's window
             }
         });
         
         removeButton.addActionListener(e -> { // Same thing as addButton
             String text = inputField.getText().trim();
             if (!text.isEmpty()) {
                 removeItem(text);
                 inputField.setText("");
                 updateDisplayArea();
             }
         });
         
         checkOffButton.addActionListener(e -> { // Same thing as addButton
             String text = inputField.getText().trim();
             if (!text.isEmpty()) {
                 checkOffItem(text);
                 inputField.setText("");
                 updateDisplayArea();
             }
         });
         
         printListButton.addActionListener(e -> {
             updateDisplayArea();
             playUPD(); // so that the print button atleast lets you know something happened
         });
 
         exitButton.addActionListener(e -> {
             updateDisplayArea();
             Object[] opt = {"Yes!", "Not yet!"}; // obj for option dialog
             int choi = JOptionPane.showOptionDialog( // we'll use int 'choi' for holding the true/false basic
                 this,
                 "Are you SURE thats all you need?",
                 "Exit?",
                 JOptionPane.YES_NO_OPTION,
                 JOptionPane.QUESTION_MESSAGE,
                 icon, // baggy icon o7
                 opt, // giving the showOptionDialog the opt object that has the options
                 opt[1] // this basically, is just just choosing which button from opt will be highlighted automatically. We highlight NOT YET to kinda mimic how real apps try to use psycology to keep you on it a bit longer
             );
             if (choi == JOptionPane.YES_OPTION) {
                 dispose(); // just closes the popup itself (closes main jframe), THEN sends the system.exit(0) with a new thread, otherwise system.exit will only close the popup. 
                 new Thread(() -> System.exit(0)).start(); // disposes of window, closing it, then opening a new thread so the EDT doesnt block the command.
             } // exit option
         });
         
         // add buttons to panel with spacing
         buttonPanel.add(addButton);
         buttonPanel.add(Box.createRigidArea(new Dimension(0, 5))); // SPACER
         buttonPanel.add(removeButton);
         buttonPanel.add(Box.createRigidArea(new Dimension(0, 5))); // SPACER
         buttonPanel.add(checkOffButton);
         buttonPanel.add(Box.createRigidArea(new Dimension(0, 10))); // SPACER
         buttonPanel.add(printListButton);
         buttonPanel.add(Box.createRigidArea(new Dimension(0, 5))); // SPACER
         buttonPanel.add(exitButton); // doesnt need spacer, but if add another button we would
         
         // adding buttons to the left-side panel of window
         leftPanel.add(buttonPanel);
         add(leftPanel, BorderLayout.WEST); // BorderLayout is kinda weird and uses literal compass directions (???)
         
         


         // ""output"" area lol
         displayArea = new JTextArea();
         displayArea.setEditable(false);
         displayArea.setLineWrap(true);
         displayArea.setWrapStyleWord(true);
         displayArea.setMargin(new Insets(0, 0, 0, 0));
         
         JScrollPane scrollPane = new JScrollPane(displayArea); // viewport of a data file basically 
         scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, 100));
         
         JLabel instructionsLabel = new JLabel("Enter your grocery items on the left!");
         instructionsLabel.setHorizontalAlignment(SwingConstants.CENTER);
         instructionsLabel.setForeground(new Color(102,204,255,180));


         JLabel iconLabel = new JLabel(new ImageIcon(getClass().getResource("bag.png"))); // adding window icon into window itself since i made a cool one
         iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
         iconLabel.setVerticalAlignment(SwingConstants.TOP);
         iconLabel.setBorder(new EmptyBorder(0, 28, 0, 0)); // jlabel doesnt inherit setmargin for some reason i dont care enough to look into but this does the job

         JPanel centerContainer = new JPanel(new BorderLayout());
         centerContainer.setOpaque(true);
         centerContainer.setBackground(new Color(225,0,255,0)); // leave alpha at 0
         centerContainer.add(instructionsLabel, BorderLayout.NORTH); // adding components into center comp for "ease of use"
         centerContainer.add(scrollPane, BorderLayout.SOUTH);
         centerContainer.add(iconLabel, BorderLayout.EAST);
         centerContainer.setBorder(new EmptyBorder(50, 50, 50, 50));

        add(centerContainer, BorderLayout.CENTER);
     }
     
     
     private void addItem(String item) { // MUST only add if the item doesnt exist.
         for (int i = 0; i < itemCount; i++) {
             if (items[i].equalsIgnoreCase(item)) { // equalsIgnoreCase is extreeeeemely useful here. 'xyz' is treated the same as 'XyZ'! not worrying about extra letters yet. (if at all)
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
         try { // use a try so we can search by name, usually wont be a number though.
             index = Integer.parseInt(input) - 1; // Convert to 0-based index.
         } catch (NumberFormatException e) {
             // search via name if exception type is numberformat
             for (int i = 0; i < itemCount; i++) {if (items[i].equalsIgnoreCase(input)) {index = i; break;}}
         }
         if (index >= 0 && index < itemCount) {
             // shmoove em to kill random gap (took me way too long to figure out)
             for (int i = index; i < itemCount - 1; i++) {
                 items[i] = items[i + 1];
                 checkedOff[i] = checkedOff[i + 1];
             }
             itemCount--;
         } else {JOptionPane.showMessageDialog(this, "Item not found!!", "Nope, Error!!", JOptionPane.ERROR_MESSAGE);}
     }
     
     
     private void playUPD() { // since the print button is obsolete with a GUI app, i just decided it should play a sound when pressed, and refresh the window. (This counts, right?)
         try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(getClass().getResource("upd.wav"))) {
             Clip clip = AudioSystem.getClip();
             clip.open(audioStream);
             
             if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) { // checking that floatcontrol identifies gain, which allows us to control the volume!
                 FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                 float VOLL = 1.0f; // -80 dB (mute) - 6 dB (max)     i set it at 2.5 for 1/3~ vol orig, however it just seems like it doesnt want to work correctly and im too tired to fix it fr
                 volumeControl.setValue(VOLL);
             }
 
             clip.start();
         } catch (Exception e) { // catch errors as exception e, popup message.
             JOptionPane.showMessageDialog(this, "Error with javax " + e.getMessage(), "Audio Error", JOptionPane.ERROR_MESSAGE);
         }
     }
 
 
     private void checkOffItem(String input) { // btw i condense my programs after finishing them so thats why anything that CAN be one line, i will totally make it.
         int index = -1;
         try {index = Integer.parseInt(input) - 1;} 
         catch (NumberFormatException e) { for (int i = 0; i < itemCount; i++) { if (items[i].equalsIgnoreCase(input)) {index = i; break;}} }
         if (index >= 0 && index < itemCount) { checkedOff[index] = true;} 
         else { JOptionPane.showMessageDialog(this, "Item not found!!", "Nope, Error!!", JOptionPane.ERROR_MESSAGE); }
     }
     
 
     private void updateDisplayArea() { // every line in the display area shows the item num, name, and markers for checked off / not. [-] / [x]
         StringBuilder sb = new StringBuilder();
         // runs thru itemcount to append and build the return, checking against checkedOff[i].
         for (int i = 0; i < itemCount; i++) { sb.append((i + 1) + " . " + items[i] + " [ " + (checkedOff[i] ? "x" : "-") + " ]\n"); } // my masterpiece..... DONT TOUCH. 
         displayArea.setText(sb.toString());
     }
     
     public static void main(String[] args) {
         // EDT Job scheduling part, just think of it as "gui updater". it que's up the GLGUI obj gui, and sets it to visible.
         SwingUtilities.invokeLater(() -> {
             GLGUI gui = new GLGUI();
             gui.setVisible(true);
         });
     }
 }
 