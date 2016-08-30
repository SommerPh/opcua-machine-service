package de.unistuttgart.isw.opcuamachineservice;

import com.siemens.ct.exi.exceptions.EXIException;
import de.unistuttgart.isw.opcuamachineservice.opcua.Subscriptions;
import de.unistuttgart.isw.opcuamachineservice.utils.Json2NodeId;
import de.unistuttgart.isw.opcuamachineservice.utils.KafkaHelper;
import de.unistuttgart.isw.opcuamachineservice.utils.XMLHelper;
import de.unistuttgart.isw.serviceorchestration.servicecore.MessageBus;
import de.unistuttgart.isw.serviceorchestration.servicecore.MessageSender;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Timur Tasci
 * @version 1.0
 * @date 11.08.16
 * @copyright Timur Tasci, ISW Uni Stuttgart
 */
public class Application {
    public static void main(String[] args) throws Exception {
        System.setProperty("http.agent", "Mozilla/5.0");
        Subscriptions subscriptions = new Subscriptions();
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        NodeId[] ids = Json2NodeId.json2NodeId(new File(classLoader.getResource("subscribeItems.json").getFile()));
        List<UaMonitoredItem> itemList = subscriptions.createMonitoredItemsFromNodes(ids).get();
        MessageBus bus = new MessageBus();
        Map<String,MessageSender> messageSenders = KafkaHelper.creadeSendersFromIds(ids, bus);
        for (UaMonitoredItem item : itemList)
            item.setValueConsumer((uaMonitoredItemitem, value) -> {
                try {
                    messageSenders.get(uaMonitoredItemitem.getReadValueId().getNodeId().getIdentifier().toString()).send(XMLHelper.createXML(value));
                } catch (IOException | SAXException | EXIException e) {
                    e.printStackTrace();
                }
            });
        while (true);
    }
}
