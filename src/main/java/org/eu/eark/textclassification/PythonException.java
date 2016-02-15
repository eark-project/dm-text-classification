/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eu.eark.textclassification;

/**
 *
 * @author janrn
 */

public class PythonException extends Exception {
    // called when the python script returns an error message, so that the java part stops as well
    public PythonException (String error) {
        super(error);
    }
}
