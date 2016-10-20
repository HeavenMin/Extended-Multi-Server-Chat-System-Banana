package ChatGUI;

import java.awt.*;        
import java.awt.event.*;


public class ChatGUI extends Frame implements ActionListener {
 private TextArea outputText;
 private TextArea inputText; 
 private Button btnCount;
 private MessageSendThread sendThread;
 
 public ChatGUI(){
	  setTitle("GUI");
	  setVisible(true);
	  setBounds(500,200,700,400);
	  setFont(new Font("futura",Font.PLAIN,16));
	  setForeground(new Color(37, 156, 192));
	  
	  
	  outputText = new TextArea();
	  outputText.setEditable(false);
	  
	  
	  inputText = new TextArea();
	  inputText.setEditable(true);
	  
	  btnCount = new Button("SEND");
	  btnCount.addActionListener(this);  
	 
	  
	  add(outputText,BorderLayout.NORTH);
	  add(btnCount,BorderLayout.EAST);
	  add(inputText,BorderLayout.WEST);

	 }

 
 
 public static void main(String[] args){
  ChatGUI gui = new ChatGUI();
 }
 
 @Override
 public void actionPerformed(ActionEvent eve){
   String input = inputText.getText();
   inputText.setText("");
   sendThread.sendFromGUI(input);
 }
 
 public void updateOutputTextWithNewLine(String addString){
  String originalString = outputText.getText();
  if(!originalString.equals("")){
   String updatedString = originalString + "\n" + addString;
   outputText.setText(updatedString);
  }
  else{
   outputText.setText(addString);
  }
  
 }
 
 public void updateOutputTextWithoutNewLine(String addString){
  String originalString = outputText.getText();
  if(!originalString.equals("")){
   String updatedString = originalString + addString;
   outputText.setText(updatedString);
  }
  else{
   outputText.setText(addString);
  }
 }
 
 public void updateOutputTextJustNewLine(){
  String originalString = outputText.getText();
  outputText.setText(originalString + "\n");
 }
 
 public void addMessageSendThread (MessageSendThread sendThread){
  this.sendThread = sendThread;
 }
}