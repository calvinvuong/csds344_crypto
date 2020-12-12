package csds344_gui;

import static csds344_gui.RSAKeyGen.generateRSAKey;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;

/**
 *
 * @author Bertram Su
 */
public class FXMLDocumentController implements Initializable {
    private boolean shouldEncrypt = true;
    @FXML
    private TextField textFieldOutput;
    @FXML
    private Label keyLabel;
    @FXML
    private TextArea keyTextArea;
    @FXML
    private Label processLabel;
    @FXML
    private ToggleGroup ButtonGroup;
    @FXML
    private RadioButton ButtonGroupEncrypt;
    @FXML
    private RadioButton ButtonGroupDecrypt;
    @FXML
    private TextField textFieldinputFile;
    @FXML
    private Button ButtonProcess;
    @FXML
    private ChoiceBox<EncryptionType> choiceBox;
    @FXML
    private Label choiceBoxLabel; //used for type of encryption
    
    List<EncryptionType> choiceBoxList;
    @FXML
    private Hyperlink inputBrowser;
    @FXML
    private Hyperlink keyBrowser;
    
    @FXML
    private void radioEncryptPushed(ActionEvent event) {
        shouldEncrypt = true;
        choiceBoxLabel.setText("Type of encryption");
        checkTypeChange();
    }

    @FXML
    private void radioDecryptPushed(ActionEvent event) {
        shouldEncrypt = false;
        choiceBoxLabel.setText("Type of decryption");
        checkTypeChange();
    }

    long startTime = System.nanoTime();
    
    private void printDuration(){
        long duration = System.nanoTime() - startTime;
        System.out.println("The duration of this method was "+duration);
    }
    
    private void choiceBoxSelected(EncryptionType type) {   
        switch(type){
            case VIGNERE:
                keyLabel.setText("Key file");
                keyTextArea.setText("");
                keyTextArea.setPromptText("Enter the file name");
                keyBrowser.setManaged(true);
                break;
            case RSA:
                if(shouldEncrypt){
                    keyLabel.setText("Keys will found in");
                    keyTextArea.setText("Private: rsaKey \nPublic: rsaKey.pub");
                }else{
                    keyLabel.setText("Private Key File");
                    keyTextArea.setText("");
                    keyTextArea.setPromptText("Enter the file name");
                    keyBrowser.setManaged(true);
                }
                break;
            case DES:
                if(shouldEncrypt){
                    keyLabel.setText("Key will be generated below");
                    keyTextArea.setText("");
                    keyTextArea.setPromptText("After encrypting, your key will be here");
                }else{
                    keyLabel.setText("Key");
                    keyTextArea.setText("");
                    keyTextArea.setPromptText("Enter the hexadecimal key");
                    keyBrowser.setManaged(false);
                }
                break;
            
        }
    }

    @FXML
    private void inputBrowserPushed(ActionEvent event) {
        FileChooser fc = new FileChooser();
        File selectedFile = fc.showOpenDialog(null);
        
        if(selectedFile != null){
            textFieldinputFile.setText(selectedFile.getAbsolutePath());
        }else{
            processLabel.setText("Please choose an input file");
        }
    }

    @FXML
    private void keyBrowserPushed(ActionEvent event) {
        FileChooser fc = new FileChooser();
        File selectedFile = fc.showOpenDialog(null);
        
        if(selectedFile != null){
            keyTextArea.setText(selectedFile.getAbsolutePath());
        }else{
            processLabel.setText("Please choose a valid key file");
        }
    }
    
    private enum EncryptionType{
        VIGNERE,
        RSA,
        DES
    }
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        //These items are for configuring the choicebox
        choiceBoxList = choiceBox.getItems();
        choiceBoxList.add(EncryptionType.VIGNERE);
        choiceBoxList.add(EncryptionType.RSA);
        choiceBoxList.add(EncryptionType.DES);
        choiceBox.setValue(EncryptionType.VIGNERE);
        
        
        choiceBox.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            choiceBoxSelected(choiceBoxList.get(newValue.intValue()));
        });
    }
    
    private void checkTypeChange(){
        choiceBoxSelected(currentEncryptionType());
    }
    
    private EncryptionType currentEncryptionType(){
        Integer index = choiceBox.getSelectionModel().selectedIndexProperty().getValue();
        return choiceBoxList.get(index);
    }
    
    @FXML
    private void ProcessButonPushed(ActionEvent event) {
        if(textFieldinputFile == null || textFieldOutput == null ){
            processLabel.setText("Please input a valid input and output file name");
            return;
        }
        
        if(currentEncryptionType().equals(EncryptionType.VIGNERE)){
            if(keyTextArea == null){
                processLabel.setText("Please input the name of your key file");
                return;
            }
        }
        
        String endMessage = textFieldinputFile.getText() + " has been processed";
        
        processLabel.setText(endMessage);
        
        String userFile = textFieldinputFile.getText();
        String outputFile = textFieldOutput.getText();
        String key = keyTextArea.getText();
        EncryptionType chosen = choiceBox.getValue();
        
        startTime = System.nanoTime();
        
        if(shouldEncrypt){
           encryptFile(userFile, outputFile, key, chosen);
        }else{
           decryptFile(userFile, outputFile, key, chosen);
        }
        
        //printDuration();
    }
    
    private void decryptFile(String inputFile, String outputFile, String key, EncryptionType type){
        switch(type){
                case VIGNERE :
                    try{
                        VigenereCipher vc = new VigenereCipher(outputFile, inputFile, key);
                        vc.execute();
                    }catch(IOException e){
                        processLabel.setText("Please check the inputs");
                    }
                    break;
                case RSA :
                    
                    try{
                        RSAPrivateKey privateKey = (RSAPrivateKey) RSAKey.loadKey(key);
                        RSA.decryptFile(inputFile, outputFile, privateKey);
                    }catch(Exception e){
                        processLabel.setText("Please check the inputs");
                    }
                    
                    break;
                case DES :
                    try{
                        DESCipher.decryptFile(new File(inputFile), key, outputFile);
                    }
                    catch(FileNotFoundException e) {
                        processLabel.setText("The file was not found.");
                    }catch(IOException e){
                        processLabel.setText("The file was not of the correct type");
                    }catch(IllegalArgumentException e){
                        processLabel.setText("The key provided was invalid");
                    }
                    break;
                default:
                    break;
        }
    }
    
    private void encryptFile(String inputFile, String outputFile, String key, EncryptionType type){
        switch(type){
                case VIGNERE :
                   try{
                       VigenereCipher vc = new VigenereCipher(inputFile, outputFile, key);
                       vc.execute();
                    }catch(IOException e){
                        processLabel.setText("Please check the inputs");
                    }
                    
                   
                    
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
                        
                        RSA.encryptFile(inputFile, outputFile, pubKey);
                    }catch(Exception e){
                        processLabel.setText("Please check the inputs");
                    }
                    
                    break;
                case DES :
                    try{
                        String keyGenerated = DESUtils.randomHexKey();
                        DESCipher.encryptFile(new File(inputFile), keyGenerated, outputFile);
                        keyTextArea.setText(keyGenerated);
                    }
                    catch(FileNotFoundException e) {
                        processLabel.setText("The file was not found.");
                    }catch(IOException e){
                        processLabel.setText("The file was not of the correct type");
                    }catch(IllegalArgumentException e){
                        processLabel.setText("The key provided was invalid");
                    }
                    break;
                default:
                    break;
        }
    }
    
}
