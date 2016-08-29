package de.unistuttgart.isw.opcuamachineservice.utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;

/**
 * @author Timur Tasci
 * @version 1.0
 * @date 29.08.2016
 * @copyright Timur Tasci, ISW Uni Stuttgart
 */
public class XMLHelper {

    public static String createXML(DataValue value){
        XStream xStream = new XStream(new StaxDriver());
        return xStream.toXML(value);
    }
}
