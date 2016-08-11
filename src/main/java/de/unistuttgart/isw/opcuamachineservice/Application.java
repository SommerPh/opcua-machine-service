package de.unistuttgart.isw.opcuamachineservice;

import de.unistuttgart.isw.opcuamachineservice.opcua.Subscriptions;
import de.unistuttgart.isw.opcuamachineservice.utils.Json2NodeId;

import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

import java.io.File;
import java.util.List;

/**
 * @author Timur Tasci
 * @version 1.0
 * @date 11.08.16
 * @copyright Timur Tasci, ISW Uni Stuttgart
 */
public class Application {
    public static void main(String[] args) throws Exception {
        Subscriptions subscriptions = new Subscriptions();
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        NodeId[] ids = Json2NodeId.json2NodeId(new File(classLoader.getResource("subscribeItems.json").getFile()));
        List<UaMonitoredItem> itemList = subscriptions.createMonitoredItemsFromNodes(ids).get();
        for (UaMonitoredItem item : itemList){
            item.setValueConsumer(v -> {
                System.out.println(v.getValue());
                System.out.println(v.getServerTime());
            });
        }

        while (true);
    }
}
