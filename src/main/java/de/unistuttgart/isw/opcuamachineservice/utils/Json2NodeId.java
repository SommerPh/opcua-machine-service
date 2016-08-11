package de.unistuttgart.isw.opcuamachineservice.utils;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import java.io.File;
import java.io.FileReader;

/**
 *
 * @author Timur Tasci
 * @version 1.0
 * @date 11.08.16
 * @copyright Timur Tasci, ISW Uni Stuttgart
 */
public class Json2NodeId {

    public static NodeId[] json2NodeId(File file) throws Exception {
        JsonReader jsonReader = new JsonReader(new FileReader(file));
        Gson gson = new Gson();
        return gson.fromJson(jsonReader, NodeId[].class);
    }
}
