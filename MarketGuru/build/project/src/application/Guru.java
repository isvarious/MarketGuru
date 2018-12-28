package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Guru
{	
    @FXML
    private MenuItem openFile;

    @FXML
    private ListView<String> commentList, questionList;    
    
    @FXML
    private TextArea includeText;

    @FXML
    private Button exportQuestion,filterResult,removeQuestion,addComment, removeDuplicate;
        
    @FXML
    private Text commentCount, questionCount;
    
    String fileLocation;    
    ArrayList<String> fileContents = new ArrayList<String>();    
          
    String  commentToMove, questionToRemove;
    
    boolean commentMouseEntered = false,
    		questionMouseEntered = false;
    
    int questionCounter = 0;
        
    @FXML
    void addComment(ActionEvent event) 
    {
    	int index = commentList.getSelectionModel().getSelectedIndex();				
		if(index > -1)
		{			
			//store selected item
			commentToMove = commentList.getItems().get(index);
			
			//copy to list
			questionList.getItems().add(commentToMove);
			questionList.refresh();
			
			//remove from original list
			commentList.getItems().remove(index);
			commentToMove = "";
			
			
			//update count display
			updateCommentCount(commentList.getItems().size());
			updateQuestionCount(questionList.getItems().size());
			
			questionCounter++;
		}
    }
    @FXML
    void removeQuestions(ActionEvent event)
    {
    	int index = questionList.getSelectionModel().getSelectedIndex();				
		if(index > -1)
		{			
			//store selected item
			questionToRemove = questionList.getItems().get(index);
			
			//copy to list
			commentList.getItems().add(questionToRemove);
			commentList.refresh();
			
			//remove from original list
			questionList.getItems().remove(index);
			questionToRemove = "";
			
			//update count display
			updateCommentCount(commentList.getItems().size());
			updateQuestionCount(questionList.getItems().size());
			
			if(questionCounter > 0) 
			{
				questionCounter--;
			}
		}
    }
        
    
    @FXML
    void openFile(ActionEvent event) throws FileNotFoundException, IOException, ParseException 
    {
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Open Json File");
    	FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
    	fileChooser.getExtensionFilters().add(extFilter);
    	
    	File file  = fileChooser.showOpenDialog(new Stage());    	
    	if(file !=null) 
    	{
    		fileLocation = file.getAbsolutePath();    		
    		fileContents = removeDuplicates(getJson(fileLocation));
    		populateCommentList(fileContents);
    	}    	
    }
    void populateCommentList(ArrayList<String> list) 
    {
    	commentList.getItems().clear();    	
    	
    	for(String s: list) 
    	{
    		commentList.getItems().add(s);
    	} 
    	
    	updateCommentCount(list.size());
    	commentList.refresh();
    }     
    void populateQuestionList(ArrayList<String> list) 
    {
    	questionList.getItems().clear();    	
    	
    	for(String s: list) 
    	{
    		questionList.getItems().add(s);
    	} 
    	
    	updateQuestionCount(list.size());
    	questionList.refresh();
    }    
    
    
    void updateCommentCount(int count) 
	{	
		commentCount.setText(count+" comments");
	}
	void updateQuestionCount(int count) 
	{	
		questionCount.setText(count+" questions");
	}
    String filter(String item)
	{
		String pattern = "[^A-Za-z0-9'?]", fItem="";		
		
		//clear garbage
		fItem = item.replaceAll(pattern, " ");	
	
		return fItem;
	}
	ArrayList<String> getJson(String path) throws FileNotFoundException, IOException, ParseException 
	{
		JSONParser parser = new JSONParser();   
		Object object = parser.parse(new FileReader(path));		
		JSONArray jArray = (JSONArray)object;		
		ArrayList<String> tList = new ArrayList<String>();
		
		for(Object o : jArray)
        {
			JSONObject j = (JSONObject)o;			
			String item = (String) j.get("commentText");
			if(item != null) 
			{
				String fItem = filter(item);
				
				tList.add(fItem);
			}	
		}
		
		return tList;
	}
	
	public ArrayList<String> removeDuplicates(ArrayList<String> list) 
	{
		//create container for non duplicates
		Set<String> hs = new HashSet<>();
		
		//add list to container
		hs.addAll(list);
		
		//clear original list
		list.clear();
		
		//add container back to list
		list.addAll(hs);
		
		return list;
	}	
	
	@FXML
    void filterResults(ActionEvent event) 
	{
		applyFilteredWords();
		removeDupes();
    }
	void applyFilteredWords() 
	{		
		//include words
		ArrayList<String> includList = getListOfIncludedWords();		
		if(includList.size() > 0) 
		{
			populateCommentList( includList );
		}		
	}
	ArrayList<String> getListOfIncludedWords()
	{
		String[] list = includeText.getText().split(",");
		ArrayList<String> tList = new ArrayList<String>();
		
		for(String comment: fileContents) 
		{
			for(String includedWord: list) 
			{
				String c = comment.toLowerCase(),
						iw = includedWord.toLowerCase();
				if(c.contains(iw)) 
				{
					tList.add(comment);
				}
			}
		}
				
		return tList;
	}	
		
    @FXML
    void exportQuestions(ActionEvent event)
    {    	
    	if(questionCounter > 0)
		{
    		exportAndSaveFile();
		}
    }
    void exportAndSaveFile() 
    {
    	FileChooser fileChooser = new FileChooser();
    	 
        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            saveTextToFile(buildFileFromString(), file);
        }
    }
    void saveTextToFile(String content, File file) 
    {
        try {
            PrintWriter writer;
            writer = new PrintWriter(file);
            writer.println(content);
            writer.close();
        }
        catch (IOException ex) 
        {
        	System.out.println("Failed to save file - "+ex.getMessage());
        }
    }
    String buildFileFromString() 
    {
    	StringBuilder sb = new StringBuilder();
    	
    	for(String s: questionList.getItems()) 
    	{
    		sb.append(s+"\n");
    	}
    	    	
    	return sb.toString();
    }    

    @FXML
    void removeDuplicates(ActionEvent event)
    {
    	removeDupes();
    }    
    void removeDupes() 
    {
    	if(commentList.getItems().size() > 0) 
    	{
    		populateCommentList(removeDuplicates(convertItemsToList(commentList.getItems())));
    		updateCommentCount(commentList.getItems().size());
    	}
    	
    	if(questionList.getItems().size() > 0) 
    	{
    		populateQuestionList(removeDuplicates(convertItemsToList(questionList.getItems())));
    		updateQuestionCount(questionList.getItems().size());
    	}
    }
    ArrayList<String> convertItemsToList(ObservableList oList)
    {
    	ArrayList<String> list = new ArrayList<String>();    	
    	for(Object o: oList) 
    	{
    		list.add(o.toString());
    	}    	
    	
    	return list;
    }    
}