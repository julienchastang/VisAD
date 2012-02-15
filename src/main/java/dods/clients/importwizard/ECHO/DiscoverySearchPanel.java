package dods.clients.importwizard.ECHO;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import gnu.regexp.*;
import org.jdom.*;
import org.jdom.output.XMLOutputter;
import org.jdom.input.DOMBuilder;
import java.io.*;

/** 
 * This class displays a panel for initiating a discovery search query
 *
 * @author Zhifang(Sheila Jiang)
 */
public class DiscoverySearchPanel extends JSplitPane
    implements MouseListener //ActionListener, ListSelectionListener
{
    private final int SIZE = 8;
    private JCheckBox[] category;
    private JLabel[] categoryLabel;
    //private JList valids;
    private Vector queryValids;
    private Vector inputValids;
    private JScrollPane validsPane;
    private JTextField inputText;
    //private JSplitPane rightPane;
    private JPanel rightPanel;
    private JButton selectAll;
    private JButton reset;
    private JList currentList;
    private JPanel inputPanel;
    private JPanel validsPanel;

    /**
      * Constructor  
      * @param	xmlFile  xml file name/path.
      *
      * Create a new <code>DiscoverySearchPanel/code>
     */

    public DiscoverySearchPanel(String xmlFile) {
	//Create a split pane 
	super(JSplitPane.HORIZONTAL_SPLIT);
        //init
	category = new JCheckBox[SIZE];
	categoryLabel = new JLabel[SIZE];
       	validsPane = new JScrollPane();
	queryValids = new Vector();
        inputValids = new Vector();
	inputText = new JTextField(20);
	rightPanel = new JPanel();
	currentList = new JList();
	inputPanel = new JPanel();
	validsPanel = new JPanel();

	//category names for discovery search
	String[] categoryName = {"campaign", "dataSetId", "instrumentName","parameter", "processingLevel", "sensorName", "shortName", "sourceName"};
	//extract info from xml file
	File xml;
	Document outXMLDoc;
	
	try {//create a file object
	     xml = new File(xmlFile);
	     // convert a file to a JDOM Document
	     DOMBuilder domBuilder = new DOMBuilder(false);
	     outXMLDoc = domBuilder.build(xml);

	     //get the desired valids
	     Element root = outXMLDoc.getRootElement();
	     java.util.List rootChildren = root.getChildren(); //<Category>
	     for (int i=0; i<categoryName.length; i++) {
	     		for (int j=0; j<rootChildren.size(); j++) {
				Element category = (Element)rootChildren.get(j);
				//Vector valids = new Vector();
				String[] valids; //stores the values if this category is one of discoveries'
				if (category.getChildText("CategoryName").equals(categoryName[i])) {
					Element criteriaValues = category.getChild("CriteriaList").getChild("Criteria").getChild("CriteriaValues");
					java.util.List values = criteriaValues.getChildren(); //<CriteriaValue>
					valids = new String[values.size()];
					for (int k=0; k<values.size(); k++) {
						valids[k] = ((Element)values.get(k)).getText();
					}
					CollectionValids theCategory = new CollectionValids(categoryName[i], valids);
					theCategory.addMouseListener(this);
					queryValids.addElement(theCategory);
				}
			}
		}  

      } catch(NullPointerException ex){
	     System.err.println("\n File doesn't exist.");
	     System.err.println(ex.getMessage());
      } catch(JDOMException ex){
	     System.err.println("\nXML file convertion to Document failed.");
	     System.err.println(ex.getMessage());
      } catch(Exception ex){
	  System.err.println(ex.getMessage());
      }
	
	//inputValids is a vector of JTextField
	for(int i=0; i<SIZE; i++){
	    JTextField t = new JTextField(20);
	    t.setName(categoryName[i]);
	    inputValids.addElement(t);
	}
	
	//Debug
	//System.out.println("Debug: " + queryValids.size());
	for(int i=0;i<SIZE;i++){// init checkboxes and labels with mouse listeners
	    category[i] = new JCheckBox();
	    category[i].addMouseListener(this);
	    categoryLabel[i] = new JLabel(categoryName[i]);
	    categoryLabel[i].addMouseListener(this);
	}

	selectAll = new JButton("Select all");
	reset = new JButton("Reset");
	selectAll.addMouseListener(this);
	reset.addMouseListener(this);

	initGUI();
 
    }

    /**
     * Initialize the GUI components.
     */
    public void initGUI() {
	// the panel holding category names
	JPanel categoryPanel = new JPanel();
	GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        categoryPanel.setLayout(gridbag);
        c.fill = GridBagConstraints.BOTH; 
        
	//
	//left side
	//
	categoryPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Categories of Valids"), BorderFactory.createEmptyBorder(0,10,0,0)));
	/*
	//add a label on the left
	//JLabel label1 = new JLabel("<html><b><font size=\"2\", face=courier, color=navy>Select one or more from following to limit your query: </font></b></html>");
	//label1.setPreferredSize(new Dimension(220, 60));
	
	//c.weighty = 0.5;
	c.gridx = 0;
        c.gridy = 0;
	c.gridwidth = 10;
	gridbag.setConstraints(label1, c);
	categoryPanel.add(label1);
	*/
	
	for(int i=0;i<SIZE;i++){
	    // add checkboxes
	    c.weightx = 0.5;
            c.weighty = 1.0;
	    c.gridx = 0;
            c.gridy = i;
	    c.gridwidth = 1;
	    gridbag.setConstraints(category[i], c);
	    categoryPanel.add(category[i]);

	    // add labels
	    c.weightx = 7.0;
            c.gridx = 1;
            c.gridy = i;
	    gridbag.setConstraints(categoryLabel[i], c);
	    categoryPanel.add(categoryLabel[i]);
	}
	// the scroll panel holding categoryPanel
	JScrollPane categoryPane = new JScrollPane(categoryPanel);
	
	//
	//right side
	//
	//JPanel rightPanel = new JPanel();
	rightPanel.setLayout(gridbag);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(10,10,10,10)));
	/*
	//JLabel label2 = new JLabel("<html><b><font size=\"2\", face=courier, color=navy>Type in a string pattern in the text field OR select from the list below:</font></b></html>");	
	c.gridx = 0;
        c.gridy = 0;
	//c.gridwidth = 4;
	c.weightx = 1.0;
	c.weighty = 0.5;
	//gridbag.setConstraints(label2, c);
	//rightPanel.add(label2);
	*/
		
	//the scroll panel holding the valids, init as empty
	validsPane = new JScrollPane(currentList);
			
	//add text field then add inputPanel
	inputPanel.setLayout(new BorderLayout());
	inputPanel.add(inputText, BorderLayout.CENTER);
	c.gridx = 0;
        c.gridy = 0;
	c.weightx = 1.0;
        c.weighty = 0;
	gridbag.setConstraints(inputPanel, c);
	rightPanel.add(inputPanel);
	//add title
	inputPanel.setBorder(
           BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
					    "Input Field for Valid")
	   ); 
	
	//add valid list
	validsPanel.setLayout(new BorderLayout());
	validsPanel.add(validsPane, BorderLayout.CENTER);
	//add title
	validsPanel.setBorder(
           BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
					    "Valid List")
	   ); 
	
	
	//add select/deselect buttons
	JPanel buttonPanel = new JPanel();
	buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
	buttonPanel.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
	buttonPanel.add(Box.createHorizontalGlue());
	buttonPanel.add(selectAll);
	//buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
	buttonPanel.add(reset);
	buttonPanel.add(Box.createHorizontalGlue());
	validsPanel.add(buttonPanel, BorderLayout.SOUTH);

	//add validsPanel
	c.gridx = 0;
        c.gridy = 1;
	c.weightx = 1.0;
        c.weighty = 0.5;
	gridbag.setConstraints(validsPanel, c);
	rightPanel.add(validsPanel);
	
	/*	
	c.gridx = 0;
        c.gridy = 2;
	//c.gridwidth = 1;
	//c.weightx = 0.5;
	c.weighty = 0.5;
	gridbag.setConstraints(buttonPanel, c);
	rightPanel.add(buttonPanel);
	*/
	
	//Provide minimum sizes for the two components in the split pane
	Dimension minimumSize = new Dimension(100, 250);
	Dimension preferredSize = new Dimension(300, 400);
	categoryPane.setMinimumSize(minimumSize);
	validsPanel.setMinimumSize(minimumSize);
	validsPanel.setPreferredSize(preferredSize);
	setLeftComponent(categoryPane);
	setRightComponent(rightPanel);
	setOneTouchExpandable(true);
	setDividerLocation(220);
	//Provide a preferred size for the split pane
	setPreferredSize(new Dimension(500, 250));
    }

    /*
    public void actionPerformed(ActionEvent e){
	Object o = e.getSource();
       	for (int i=0;i<SIZE;i++){
	    if(o == category[i] && ((JCheckBox)o).isSelected()) {
		((CollectionValids)queryValids.elementAt(i)).setSelected();
		//queryValids.addElement(queryValids.elementAt(i));
		//valids.setListData(((CollectionValids)queryValids.elementAt(i)).getValids());
		//valids = (CollectionValids)queryValids.elementAt(i);
		remove(validsPane);
		validsPane = new JScrollPane((CollectionValids)queryValids.elementAt(i));
	        add(validsPane);
		break;
	    }
	}
	// Debug
	for (int i=0;i<queryValids.size();i++){
	    CollectionValids theList = (CollectionValids)queryValids.elementAt(i);
	    System.out.println("\n" + theList.getName() + "\t" + theList.isSelected());    
	    for (int j=0;j<theList.getValids().length;j++){
		System.out.println(theList.getValids()[j] + "   " + theList.getSelection(j));
	    }
	 } 
    }*/

    public void mouseEntered(MouseEvent e) {//do nothing       
	/*
	Object o = e.getSource();
       	for (int i=0;i<SIZE;i++){
	    if(o == category[i]) {
		remove(validsPane);
		validsPane = new JScrollPane((CollectionValids)queryValids.elementAt(i));
	        add(validsPane);
		break;
	    }
	}*/
    }

    public void mousePressed(MouseEvent e) {//do nothing
    }

    public void mouseReleased(MouseEvent e) {//do nothing
    }

    public void mouseExited(MouseEvent e) {//do nothing
    }

    public void mouseClicked(MouseEvent e) {
	Object o = e.getSource();          
	for (int i=0;i<SIZE;i++){  
	    //when a checkbox is clicked, the category is set "selected", the valid list is shown for user to select
	    if(o == category[i] && ((JCheckBox)o).isSelected()) {
		currentList = (CollectionValids)queryValids.elementAt(i);
		((CollectionValids)currentList).setSelected();
				
		//update GUI
		validsPanel.remove(validsPane);
		inputPanel.remove(inputText);

		validsPane = new JScrollPane(currentList);
		inputText = (JTextField)inputValids.elementAt(i);

		validsPane.setVisible(true);
		inputText.setVisible(true);
		inputPanel.add(inputText);
		validsPanel.add(validsPane);
		getRootPane().getContentPane().repaint();
		break;
	    }
	    //deselect a category
	    else if (o == category[i]&& !((JCheckBox)o).isSelected()) {
		((CollectionValids)queryValids.elementAt(i)).deSelect();
		break;
	    }
	    //when the category name (JLabel object) is clicked, the valid list is shown
	    else if (o == categoryLabel[i]) {
		currentList = (CollectionValids)queryValids.elementAt(i);
		
		//update GUI
		validsPanel.remove(validsPane);
		inputPanel.remove(inputText);
		
		validsPane = new JScrollPane(currentList);
		inputText = (JTextField)inputValids.elementAt(i);
		
		validsPane.setVisible(true);
		inputText.setVisible(true);
		inputPanel.add(inputText);
		validsPanel.add(validsPane);
		getRootPane().getContentPane().repaint();
		break;
	    }
	    //when valids is clicked, it's check box is checked
	    else if (o == queryValids.elementAt(i)) {
		category[i].setSelected(true);
	    }
	    //when select all button is clicked, select all
	    else if (o == selectAll) {
		int length = currentList.getModel().getSize();
		if (length>0) {
		    int[] indices = new int[length];
		    for (int j=0; j< length; j++) {
			indices[j] = j;
		    }
		    currentList.setSelectedIndices(indices);
		}
		int index = queryValids.indexOf(currentList);
		if (index != -1)
		    category[index].setSelected(true);
	    }
	    //when reset is clicked, clear all
	    else if (o == reset) {
		currentList.clearSelection();
		int index = queryValids.indexOf(currentList);
		if (index != -1)
		    category[index].setSelected(false);
	    }
	}
    }

    public Vector getQueryValids(){//return queryValids & inputValids
	Vector allValids = (Vector)queryValids.clone();
	for (int i=0; i<inputValids.size(); i++){
	    // input is taken only if nothing's selected in the list
	    if (((JCheckBox)category[i]).isSelected() && ((CollectionValids)queryValids.elementAt(i)).getSelectedIndex() == -1) {
		allValids.addElement(inputValids.elementAt(i));
	    }
	}
	return allValids;
    }
}


