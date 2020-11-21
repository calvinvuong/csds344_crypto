/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csds344_gui;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

/**
 *
 * @author Bertram Su
 */
public class FXMLDocumentController implements Initializable {
    private boolean shouldEncrypt = true;
    
    @FXML
    private void radioEncryptPushed(ActionEvent event) {
        shouldEncrypt = true;
        choiceBoxLabel.setText("Type of encyption");
    }

    @FXML
    private void radioDecryptPushed(ActionEvent event) {
        shouldEncrypt = false;
        choiceBoxLabel.setText("Type of decryption");

    }
    
    private enum EncryptionType{
        VIGNERE,
        RSA,
        DES
    }
    
    @FXML
    private Label processLabel;
    @FXML
    private ToggleGroup ButtonGroup;
    @FXML
    private RadioButton ButtonGroupEncrypt;
    @FXML
    private RadioButton ButtonGroupDecrypt;
    @FXML
    private TextArea myTextArea; //Used for key
    @FXML
    private TextField textFieldFileName;
    @FXML
    private Button ButtonProcess;
    @FXML
    private ChoiceBox<EncryptionType> choiceBox;
    @FXML
    private Label choiceBoxLabel; //used for type of encryption
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        //These items are for configuring the choicebox
        List<EncryptionType> choiceBoxList = choiceBox.getItems();
        choiceBoxList.add(EncryptionType.VIGNERE);
        choiceBoxList.add(EncryptionType.RSA);
        choiceBoxList.add(EncryptionType.DES);
        choiceBox.setValue(EncryptionType.VIGNERE);
        
        
        
    }    
    
    @FXML
    private void ProcessButonPushed(ActionEvent event) {
        String endMessage = textFieldFileName.getText() + " has been processed using " + choiceBox.getValue() +". Your key was " + myTextArea.getText();
        
        processLabel.setText(endMessage);
        
        //Below is what we'll probably use
        String fileName = textFieldFileName.getText();
        EncryptionType chosen = choiceBox.getValue();
        
        if(shouldEncrypt){
           encryptFile(fileName, chosen);
        }else{
            decryptFile(fileName, chosen);
        }
    }
    
    private void decryptFile(String fileName, EncryptionType type){
        switch(type){
                case VIGNERE :
                    //INSERT VIGNERE HERE
                    break;
                case RSA :
                    //INSERT RSA HERE
                    break;
                case DES :
                    //INSERT DES HERE
                    break;
        }
    }
    
    private void encryptFile(String fileName, EncryptionType type){
        switch(type){
                case VIGNERE :
                    //INSERT VIGNERE HERE
                    break;
                case RSA :
                    //INSERT RSA HERE
                    break;
                case DES :
                    //INSERT DES HERE
                    break;
        }
    }
    
}
