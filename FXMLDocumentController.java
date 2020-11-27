/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csds344_gui;

import static csds344_gui.RSAKeyGen.generateRSAKey;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
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
        String userFile = textFieldFileName.getText();
        String key = myTextArea.getText();
        EncryptionType chosen = choiceBox.getValue();
        
        //File fileName = new File(userFile);
        if(shouldEncrypt){
           encryptFile(userFile, key, chosen);
        }else{
           decryptFile(userFile, key, chosen);
        }
    }
    
    private void decryptFile(String fileName, String key, EncryptionType type){
        switch(type){
                case VIGNERE :
                    VigenereCipher vc = new VigenereCipher("decryptedVIG.png", fileName, key);
                    vc.execute();
                    break;
                case RSA :
                    
                    try{
                        RSAPrivateKey privateKey = (RSAPrivateKey) RSAKey.loadKey(key);
                        RSA.decryptFile(fileName, "decOutputRSA.txt", privateKey);
                    }catch(Exception e){
                        
                    }
                    
                    break;
                case DES :
                    try{
                        DESCipher.decryptFile(new File(fileName), key, "decOutput.txt");
                    }
                    catch(FileNotFoundException e) {
                        processLabel.setText("The file was not found.");
                    }catch(IOException e){
                        processLabel.setText("The file was not of the correct type");
                    }catch(IllegalArgumentException e){
                        processLabel.setText("The key provided was invalid");
                    }
                    break;
        }
    }
    
    private void encryptFile(String fileName, String key, EncryptionType type){
        switch(type){
                case VIGNERE :
                    VigenereCipher vc = new VigenereCipher(fileName, "encVIG", key);
                    vc.execute();
                    break;
                case RSA :
                    try{
                        //Key length must be 2049
                        int KEY_LEN = 2049; // in bits
                        RSAKeyPair keyPair = generateRSAKey(KEY_LEN);
                        RSAPublicKey pubKey = keyPair.getPublicKey();
                        RSAPrivateKey privKey = keyPair.getPrivateKey();
                        
                        // Save keys first.
                        RSAKey.saveKey("rsaKey.pub", pubKey);
                        RSAKey.saveKey("rsaKey", privKey);
                        
                        System.out.println("keys were saved in rsaKey");
                        RSA.encryptFile(fileName, "encOutputRSA", pubKey);
                    }catch(Exception e){
                        processLabel.setText("File was not found");
                    }
                    
                    break;
                case DES :
                    try{
                        String key1 = DESUtils.randomHexKey();
                        System.out.println(key1);
                        DESCipher.encryptFile(new File(fileName), key1, "encOutputDES");
                    }
                    catch(FileNotFoundException e) {
                        processLabel.setText("The file was not found.");
                    }catch(IOException e){
                        processLabel.setText("The file was not of the correct type");
                    }catch(IllegalArgumentException e){
                        processLabel.setText("The key provided was invalid");
                    }
                    break;
        }
    }
    
}
