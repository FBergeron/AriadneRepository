package org.ariadne_eu.content.retrieve;

import javax.activation.DataHandler;

/**
 * Created by ben
 * Date: 11-sep-2007
 * Time: 21:22:52
 * To change this template use File | Settings | File Templates.
 */
public abstract class RetrieveContentImpl {
//    private int number;

    public abstract DataHandler retrieveContent(String identifier);
    
//    public abstract String retrieveFileName(String identifier);
//    
//    public abstract String retrieveFileType(String identifier);


//    public int getNumber() {
//        return number;
//    }
//
//    void setNumber(int number) {
//        this.number = number;
//    }

    void initialize() {

    }
}
